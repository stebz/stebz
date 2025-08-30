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

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.NamingUtils;
import io.qameta.allure.util.ObjectUtils;
import io.qameta.allure.util.ResultsUtils;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.ReflectiveStepAttributes;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Allure {@code StepListener} implementation.
 */
public class AllureStepListener implements StepListener {
  private static final String CONTEXT_PARAM_NAME = "context";
  private static final String COMMENT_ATTACHMENT_NAME = "Comment";
  private static final String EXPECTED_RESULT_ATTACHMENT_NAME = "Expected result";
  private final boolean enabled;
  private final int order;
  private final KeywordPosition keywordPosition;
  private final boolean processName;
  private final boolean contextParam;
  private final boolean expectedResultAttachment;
  private final boolean commentAttachment;
  private final boolean isStebzAnnotationsUsed;

  /**
   * Ctor.
   */
  public AllureStepListener() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public AllureStepListener(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.listeners.allure.enabled", true);
    this.order = properties.getInteger("stebz.listeners.allure.order", DEFAULT_ORDER);
    this.keywordPosition = properties.getEnum("stebz.listeners.allure.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.processName = properties.getBoolean("stebz.listeners.allure.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.allure.contextParam", true);
    this.expectedResultAttachment = properties.getBoolean("stebz.listeners.allure.expectedResultAttachment", true);
    this.commentAttachment = properties.getBoolean("stebz.listeners.allure.commentAttachment", true);
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
    final StepResult stepResult = new StepResult();
    final Map<String, Object> params = step.getParams();
    if (this.contextParam && context.isPresent()) {
      params.putIfAbsent(CONTEXT_PARAM_NAME, context.get());
    }
    if (!params.isEmpty()) {
      final List<Parameter> allureParams = stepResult.getParameters();
      params.forEach((paramName, paramValue) -> allureParams.add(
        new Parameter().setName(paramName).setValue(ObjectUtils.toString(paramValue))
      ));
    }
    stepResult.setName(
      this.keywordPosition.concat(step.getKeyword(), this.processStepName(step, step.getName(), params))
    );

    Allure.getLifecycle().startStep(UUID.randomUUID().toString(), stepResult);
    if (this.expectedResultAttachment) {
      final String expectedResult = step.getExpectedResult();
      if (!expectedResult.isEmpty()) {
        Allure.addAttachment(EXPECTED_RESULT_ATTACHMENT_NAME, expectedResult);
      }
    }
    if (this.commentAttachment) {
      final String comment = step.getComment();
      if (!comment.isEmpty()) {
        Allure.addAttachment(COMMENT_ATTACHMENT_NAME, comment);
      }
    }
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final AllureLifecycle allureLifecycle = Allure.getLifecycle();
    allureLifecycle.updateStep(stepResult -> {
      if (stepResult.getStatus() == null) {
        stepResult.setStatus(Status.PASSED);
      }
    });
    allureLifecycle.stopStep();
  }

  @Override
  public void onStepFailure(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final Throwable exception) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final AllureLifecycle allureLifecycle = Allure.getLifecycle();
    allureLifecycle.updateStep(stepResult ->
      stepResult.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
        .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null))
    );
    allureLifecycle.stopStep();
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
    return paramsToProcess.isEmpty()
      ? name
      : NamingUtils.processNameTemplate(name, paramsToProcess);
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
