/*
 * MIT License
 *
 * Copyright (c) 2025 Evgenii Plugatar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.stebz.listener;

import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.step.StepRequestUtils;
import com.epam.reportportal.utils.ParameterUtils;
import com.epam.reportportal.utils.formatting.templating.TemplateConfiguration;
import com.epam.reportportal.utils.formatting.templating.TemplateProcessing;
import com.epam.ta.reportportal.ws.model.ParameterResource;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.ReflectiveStepAttributes;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ReportPortal {@code StepListener} implementation.
 */
public class ReportPortalStepListener implements StepListener {
  private static final TemplateConfiguration TEMPLATE_CONFIG = new TemplateConfiguration();
  private final boolean enabled;
  private final int order;
  private final KeywordPosition keywordPosition;
  private final boolean processName;
  private final boolean contextParam;
  private final boolean commentDescription;
  private final boolean isStebzAnnotationsUsed;

  /**
   * Ctor.
   */
  public ReportPortalStepListener() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public ReportPortalStepListener(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.listeners.reportportal.enabled", true);
    this.order = properties.getInteger("stebz.listeners.reportportal.order", DEFAULT_ORDER);
    this.keywordPosition = properties.getEnum("stebz.listeners.reportportal.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.processName = properties.getBoolean("stebz.listeners.reportportal.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.reportportal.contextParam", true);
    this.commentDescription = properties.getBoolean("stebz.listeners.reportportal.commentDescription", true);
    this.isStebzAnnotationsUsed = isStebzAnnotationsUsed();
  }

  private static boolean isStebzAnnotationsUsed() {
    try {
      Class.forName("org.stebz.aspect.StepAspects");
      return true;
    } catch (final ClassNotFoundException ex) {
      return false;
    }
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public void onStepStart(final StepObj<?> step,
                          final NullableOptional<Object> context) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final Launch launch = Launch.currentLaunch();
    if (launch == null) {
      return;
    }
    final Map<String, Object> params = step.getParams();
    if (this.contextParam && context.isPresent()) {
      params.putIfAbsent("context", context.get());
    }
    final String comment;
    final StartTestItemRQ startTestItemRQ = StepRequestUtils.buildStartStepRequest(
      this.keywordPosition.concat(step.getKeyword(), this.processStepName(step, step.getName(), params)),
      this.commentDescription && !(comment = step.getComment()).isEmpty() ? comment : null
    );
    if (!params.isEmpty()) {
      final List<ParameterResource> rpParams = new ArrayList<>();
      params.forEach((paramName, paramValue) -> {
        final ParameterResource param = new ParameterResource();
        param.setKey(paramName);
        param.setValue(paramValue == null ? ParameterUtils.NULL_VALUE : String.valueOf(paramValue));
        rpParams.add(param);
      });
      startTestItemRQ.setParameters(rpParams);
    }
    launch.getStepReporter().startNestedStep(startTestItemRQ);
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final Launch launch = Launch.currentLaunch();
    if (launch == null) {
      return;
    }
    launch.getStepReporter().finishNestedStep();
  }

  @Override
  public void onStepFailure(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final Throwable exception) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final Launch launch = Launch.currentLaunch();
    if (launch == null) {
      return;
    }
    launch.getStepReporter().finishNestedStep(exception);
  }

  private String processStepName(final StepObj<?> step,
                                 final String name,
                                 final Map<String, Object> params) {
    if (!this.processName) {
      return name;
    }
    final Map<String, Object> paramsToProcess = new HashMap<>(params);
    final AtomicInteger idx = new AtomicInteger();
    params.forEach((key, value) -> paramsToProcess.putIfAbsent(String.valueOf(idx.getAndIncrement()), value));
    if (this.isStebzAnnotationsUsed) {
      addReflectionParams(step, paramsToProcess);
    }
    return TemplateProcessing.processTemplate(name, paramsToProcess, TEMPLATE_CONFIG);
  }

  private static void addReflectionParams(final StepObj<?> step,
                                          final Map<String, Object> paramsToProcess) {
    final org.aspectj.lang.JoinPoint joinPoint = step.get(ReflectiveStepAttributes.JOIN_POINT);
    if (joinPoint != null) {
      final Object thisObj = joinPoint.getThis();
      if (thisObj != null) {
        paramsToProcess.putIfAbsent("this", thisObj);
      }
      final Signature signature = joinPoint.getSignature();
      paramsToProcess.putIfAbsent("class", signature.getDeclaringType().getSimpleName());
      paramsToProcess.putIfAbsent("classRef", signature.getDeclaringType().getName());
      if (signature instanceof MethodSignature) {
        paramsToProcess.putIfAbsent("method", ((MethodSignature) signature).getMethod().getName());
      }
    }
  }

  private enum KeywordPosition {
    AT_START {
      @Override
      String concat(final Keyword keyword,
                    final String name) {
        if (name.isEmpty()) {
          return keyword.value();
        }
        final String keywordValue = keyword.value();
        return keywordValue.isEmpty()
          ? name
          : keywordValue + ' ' + name;
      }
    },
    AT_END {
      @Override
      String concat(final Keyword keyword,
                    final String name) {
        if (name.isEmpty()) {
          return keyword.value();
        }
        final String keywordValue = keyword.value();
        return keywordValue.isEmpty()
          ? name
          : name + ' ' + keywordValue;
      }
    };

    abstract String concat(Keyword keyword,
                           String name);
  }
}
