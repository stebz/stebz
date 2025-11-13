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
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;
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
  private final boolean enabled;
  private final int order;
  private final boolean onlyKeywordSteps;
  private final KeywordPosition keywordPosition;
  private final boolean keywordToUppercase;
  private final boolean processName;
  private final boolean contextParam;
  private final String contextParamName;
  private final boolean contextDesc;
  private final String contextDescName;
  private final boolean expectedResultDesc;
  private final String expectedResultDescName;
  private final boolean commentDesc;
  private final String commentDescName;
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
    this.order = properties.getInteger("stebz.listeners.testit.order", MIDDLE_ORDER);
    this.onlyKeywordSteps = properties.getBoolean("stebz.listeners.testit.onlyKeywordSteps", false);
    this.keywordPosition = properties.getEnum("stebz.listeners.testit.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.keywordToUppercase = properties.getBoolean("stebz.listeners.testit.keywordToUppercase", false);
    this.processName = properties.getBoolean("stebz.listeners.testit.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.testit.contextParam", true);
    this.contextParamName = properties.getString("stebz.listeners.testit.contextParamName", "Context");
    this.contextDesc = properties.getBoolean("stebz.listeners.testit.contextDesc", false);
    this.contextDescName = properties.getString("stebz.listeners.testit.contextDescName", "Context");
    this.expectedResultDesc = properties.getBoolean("stebz.listeners.testit.expectedResultDesc", true);
    this.expectedResultDescName =
      properties.getString("stebz.listeners.testit.expectedResultDescName", "Expected result");
    this.commentDesc = properties.getBoolean("stebz.listeners.testit.commentDesc", true);
    this.commentDescName = properties.getString("stebz.listeners.testit.commentDescName", "Comment");
    this.isStebzAnnotationsUsed = isStebzAnnotationsUsed();
  }

  /**
   * Adds parameter to current step if any. Takes no effect if no step run at the moment.
   *
   * @param name  the step parameter name
   * @param value the step parameter value
   * @param <T>   the type of the step parameter value
   * @return step parameter value
   */
  public static <T> T stepParameter(final String name,
                                    final T value) {
    Adapter.getAdapterManager().updateStep(stepResult -> {
      Map<String, String> parameters = stepResult.getParameters();
      if (parameters == null) {
        parameters = new HashMap<>();
        stepResult.setParameters(parameters);
      }
      parameters.put(name, value == null ? "" : value.toString());
    });
    return value;
  }

  /**
   * Sets current step name if any. Takes no effect if no step run at the moment.
   *
   * @param name the step name
   */
  public static void stepName(final String name) {
    Adapter.getAdapterManager().updateStep(stepResult ->
      stepResult.setName(name)
    );
  }

  /**
   * Sets current step name if any. Takes no effect if no step run at the moment.
   *
   * @param update the step name update function
   * @throws NullPointerException if {@code update} arg is null
   */
  public static void stepName(final ThrowingFunction<? super String, String, ?> update) {
    if (update == null) { throw new NullPointerException("update arg is null"); }
    Adapter.getAdapterManager().updateStep(stepResult ->
      stepResult.setName(ThrowingFunction.unchecked(update).apply(stepResult.getName()))
    );
  }

  /**
   * Sets current step status if any. Takes no effect if no step run at the moment.
   *
   * @param status the step status
   */
  public static void stepStatus(final ItemStatus status) {
    Adapter.getAdapterManager().updateStep(stepResult ->
      stepResult.setItemStatus(status)
    );
  }

  /**
   * Sets current step status if any. Takes no effect if no step run at the moment.
   *
   * @param exception the step exception
   * @throws NullPointerException if {@code exception} arg is null
   */
  public static void stepStatus(final Throwable exception) {
    if (exception == null) { throw new NullPointerException("exception arg is null"); }
    Adapter.getAdapterManager().updateStep(stepResult ->
      stepResult.setItemStatus(ItemStatus.FAILED).setThrowable(exception)
    );
  }

  /**
   * Updates current step if any. Takes no effect if no step run at the moment.
   *
   * @param update the step update function
   * @throws NullPointerException if {@code update} arg is null
   */
  public static void updateStep(final ThrowingConsumer<? super StepResult, ?> update) {
    if (update == null) { throw new NullPointerException("update arg is null"); }
    Adapter.getAdapterManager().updateStep(ThrowingConsumer.unchecked(update)::accept);
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
    final Keyword keyword = step.getKeyword();
    if (this.onlyKeywordSteps && keyword.value().isEmpty()) {
      return;
    }

    final StepResult stepResult = new StepResult();
    final Map<String, Object> params = step.getParams();
    final Map<String, String> stringParams = new HashMap<>();
    params.forEach((paramName, paramValue) -> stringParams.put(
      paramName,
      paramValue == null ? "" : paramValue.toString()
    ));
    if (this.contextParam && context.isPresent()) {
      final Object contextValue = context.get();
      stringParams.putIfAbsent(this.contextParamName, contextValue == null ? "" : contextValue.toString());
    }
    stepResult.setParameters(stringParams);
    stepResult.setName(this.keywordPosition.concat(
      this.keywordValue(keyword),
      this.processStepName(step, step.getName(), stringParams)
    ));
    stepResult.setDescription(this.processStepDescription(context, step.getExpectedResult(), step.getComment()));

    Adapter.getAdapterManager().startStep(UUID.randomUUID().toString(), stepResult);
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    if (this.onlyKeywordSteps && step.getKeyword().value().isEmpty()) {
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
    if (this.onlyKeywordSteps && step.getKeyword().value().isEmpty()) {
      return;
    }

    final AdapterManager adapterManager = Adapter.getAdapterManager();
    adapterManager.updateStep(stepResult ->
      stepResult.setItemStatus(ItemStatus.FAILED)
        .setThrowable(exception)
    );
    adapterManager.stopStep();
  }

  private static boolean isStebzAnnotationsUsed() {
    try {
      Class.forName("org.stebz.aspect.StepAspects");
      return true;
    } catch (final ClassNotFoundException ex) {
      return false;
    }
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
    return paramsToProcess.isEmpty()
      ? name
      : Utils.setParameters(name, paramsToProcess);
  }

  private String processStepDescription(final NullableOptional<Object> context,
                                        final String expectedResult,
                                        final String comment) {
    final StringBuilder sb = new StringBuilder();
    if (this.contextDesc && context.isPresent()) {
      sb.append(this.contextDescName).append(": ").append(context.get());
    }
    if (this.expectedResultDesc && !expectedResult.isEmpty()) {
      if (sb.length() != 0) {
        sb.append(". ");
      }
      sb.append(this.expectedResultDescName).append(": ").append(expectedResult);
    }
    if (this.commentDesc && !comment.isEmpty()) {
      if (sb.length() != 0) {
        sb.append(". ");
      }
      sb.append(this.commentDescName).append(": ").append(comment);
    }
    return sb.length() == 0
      ? null
      : sb.toString();
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

  private String keywordValue(final Keyword keyword) {
    return this.keywordToUppercase
      ? keyword.value().toUpperCase()
      : keyword.value();
  }

  private enum KeywordPosition {
    AT_START {
      @Override
      String concat(final String keywordValue,
                    final String name) {
        return name.isEmpty()
          ? keywordValue
          : keywordValue.isEmpty()
          ? name
          : keywordValue + ' ' + name;
      }
    },
    AT_END {
      @Override
      String concat(final String keywordValue,
                    final String name) {
        return name.isEmpty()
          ? keywordValue
          : keywordValue.isEmpty()
          ? name
          : name + ' ' + keywordValue;
      }
    };

    abstract String concat(String keywordValue,
                           String name);
  }
}
