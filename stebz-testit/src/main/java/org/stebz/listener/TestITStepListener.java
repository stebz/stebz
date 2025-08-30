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

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.ReflectiveStepAttributes;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.Cast;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;
import ru.testit.models.ItemStatus;
import ru.testit.models.StepResult;
import ru.testit.services.Adapter;
import ru.testit.services.AdapterManager;
import ru.testit.services.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test IT {@code StepListener} implementation.
 */
public class TestITStepListener implements StepListener {
  private static final String CONTEXT_PARAM_NAME = "context";
  private static final String EXPECTED_RESULT_DESC_LINE_PREFIX = "Expected result: ";
  private static final String COMMENT_DESC_LINE_PREFIX = "Comment: ";
  private final boolean enabled;
  private final int order;
  private final KeywordPosition keywordPosition;
  private final boolean processName;
  private final boolean contextParam;
  private final boolean isStebzAnnotationsUsed;

  /**
   * Ctor.
   */
  public TestITStepListener() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public TestITStepListener(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.listeners.testit.enabled", true);
    this.order = properties.getInteger("stebz.listeners.testit.order", DEFAULT_ORDER);
    this.keywordPosition = properties.getEnum("stebz.listeners.testit.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.processName = properties.getBoolean("stebz.listeners.testit.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.testit.contextParam", true);
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
    final Map<String, String> stringParams;
    if (params.isEmpty()) {
      stringParams = Cast.unsafe(params);
    } else {
      stringParams = new HashMap<>();
      params.forEach((paramName, paramValue) -> stringParams.put(
        paramName,
        paramValue == null ? "" : paramValue.toString()
      ));
    }
    stepResult.setName(
      this.keywordPosition.concat(step.getKeyword(), this.processStepName(step, step.getName(), stringParams))
    );
    stepResult.setDescription(this.processStepDescription(step.getExpectedResult(), step.getComment()));
    stepResult.setParameters(stringParams);

    Adapter.getAdapterManager().startStep(UUID.randomUUID().toString(), stepResult);
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final AdapterManager adapterManager = Adapter.getAdapterManager();
    adapterManager.updateStep(stepResult -> {
      if (stepResult.getItemStatus() == null) {
        stepResult.setItemStatus(ItemStatus.PASSED);
      }
    });
    adapterManager.stopStep();
  }

  @Override
  public void onStepFailure(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final Throwable exception) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final AdapterManager adapterManager = Adapter.getAdapterManager();
    adapterManager.updateStep(stepResult ->
      stepResult.setItemStatus(ItemStatus.FAILED)
        .setThrowable(exception)
    );
    adapterManager.stopStep();
  }

  private String processStepName(final StepObj<?> step,
                                 final String name,
                                 final Map<String, String> params) {
    if (!this.processName) {
      return name;
    }
    final Map<String, String> paramsToProcess = new HashMap<>(params);
    final AtomicInteger idx = new AtomicInteger();
    params.forEach((key, value) -> paramsToProcess.putIfAbsent(String.valueOf(idx.getAndIncrement()), value));
    if (this.isStebzAnnotationsUsed) {
      addReflectionParams(step, paramsToProcess);
    }
    return Utils.setParameters(name, params);
  }

  private String processStepDescription(final String expectedResult,
                                        final String comment) {
    if (expectedResult.isEmpty()) {
      return comment.isEmpty()
        ? ""
        : comment;
    } else {
      return comment.isEmpty()
        ? expectedResult
        : expectedResultDescLine(expectedResult) + System.lineSeparator() + commentDescLine(comment);
    }
  }

  private static String expectedResultDescLine(final String expectedResult) {
    return EXPECTED_RESULT_DESC_LINE_PREFIX + expectedResult;
  }

  private static String commentDescLine(final String comment) {
    return COMMENT_DESC_LINE_PREFIX + comment;
  }

  private static void addReflectionParams(final StepObj<?> step,
                                          final Map<String, String> paramsToProcess) {
    final org.aspectj.lang.JoinPoint joinPoint = step.get(ReflectiveStepAttributes.JOIN_POINT);
    if (joinPoint != null) {
      final Object thisObj = joinPoint.getThis();
      if (thisObj != null) {
        paramsToProcess.putIfAbsent("this", thisObj.toString());
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
