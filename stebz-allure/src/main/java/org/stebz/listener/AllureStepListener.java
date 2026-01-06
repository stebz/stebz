/*
 * MIT License
 *
 * Copyright (c) 2025-2026 Evgenii Plugatar
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

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.NamingUtils;
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
  private final boolean enabled;
  private final int order;
  private final boolean onlyKeywordSteps;
  private final KeywordPosition keywordPosition;
  private final boolean keywordToUppercase;
  private final boolean processName;
  private final boolean contextParam;
  private final String contextParamName;
  private final boolean expectedResultParam;
  private final String expectedResultParamName;
  private final boolean commentParam;
  private final String commentParamName;
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
    this.order = properties.getInteger("stebz.listeners.allure.order", MIDDLE_ORDER);
    this.onlyKeywordSteps = properties.getBoolean("stebz.listeners.allure.onlyKeywordSteps", false);
    this.keywordPosition = properties.getEnum("stebz.listeners.allure.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.keywordToUppercase = properties.getBoolean("stebz.listeners.allure.keywordToUppercase", false);
    this.processName = properties.getBoolean("stebz.listeners.allure.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.allure.contextParam", true);
    this.contextParamName = properties.getString("stebz.listeners.allure.contextParamName", "Context");
    this.expectedResultParam = properties.getBoolean("stebz.listeners.allure.expectedResultParam", true);
    this.expectedResultParamName =
      properties.getString("stebz.listeners.allure.expectedResultParamName", "Expected result");
    this.commentParam = properties.getBoolean("stebz.listeners.allure.commentParam", true);
    this.commentParamName = properties.getString("stebz.listeners.allure.commentParamName", "Comment");
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
    return stepParameter(name, value, null, null);
  }

  /**
   * Adds parameter to current step if any. Takes no effect if no step run at the moment.
   *
   * @param name     the step parameter name
   * @param value    the step parameter value
   * @param excluded the step parameter excluded flag
   * @param <T>      the type of the step parameter value
   * @return step parameter value
   */
  public static <T> T stepParameter(final String name,
                                    final T value,
                                    final Boolean excluded) {
    return stepParameter(name, value, excluded, null);
  }

  /**
   * Adds parameter to current step if any. Takes no effect if no step run at the moment.
   *
   * @param name  the step parameter name
   * @param value the step parameter value
   * @param mode  the step parameter mode
   * @param <T>   the type of the step parameter value
   * @return step parameter value
   */
  public static <T> T stepParameter(final String name,
                                    final T value,
                                    final Parameter.Mode mode) {
    return stepParameter(name, value, null, mode);
  }

  /**
   * Adds parameter to current step if any. Takes no effect if no step run at the moment.
   *
   * @param name     the step parameter name
   * @param value    the step parameter value
   * @param excluded the step parameter excluded flag
   * @param mode     the step parameter mode
   * @param <T>      the type of the step parameter value
   * @return step parameter value
   */
  public static <T> T stepParameter(final String name,
                                    final T value,
                                    final Boolean excluded,
                                    final Parameter.Mode mode) {
    Allure.getLifecycle().updateStep(stepResult ->
      stepResult.getParameters().add(ResultsUtils.createParameter(name, value, excluded, mode))
    );
    return value;
  }

  /**
   * Adds parameter to current step if any. Takes no effect if no step run at the moment.
   *
   * @param update the step parameter update function
   * @throws NullPointerException if {@code update} arg is null
   */
  public static void stepParameter(final ThrowingConsumer<? super Parameter, ?> update) {
    if (update == null) { throw new NullPointerException("update arg is null"); }
    Allure.getLifecycle().updateStep(stepResult -> {
      final Parameter parameter = new Parameter();
      ThrowingConsumer.unchecked(update).accept(parameter);
      stepResult.getParameters().add(parameter);
    });
  }

  /**
   * Sets current step name if any. Takes no effect if no step run at the moment.
   *
   * @param name the step name
   */
  public static void stepName(final String name) {
    Allure.getLifecycle().updateStep(stepResult ->
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
    Allure.getLifecycle().updateStep(stepResult ->
      stepResult.setName(ThrowingFunction.unchecked(update).apply(stepResult.getName()))
    );
  }

  /**
   * Sets current step status if any. Takes no effect if no step run at the moment.
   *
   * @param status the step status
   */
  public static void stepStatus(final Status status) {
    Allure.getLifecycle().updateStep(stepResult ->
      stepResult.setStatus(status)
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
    Allure.getLifecycle().updateStep(stepResult ->
      stepResult.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
        .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null))
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
    Allure.getLifecycle().updateStep(ThrowingConsumer.unchecked(update)::accept);
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
    if (this.contextParam && context.isPresent()) {
      params.putIfAbsent(this.contextParamName, context.get());
    }
    if (this.expectedResultParam) {
      final String expectedResult = step.getExpectedResult();
      if (!expectedResult.isEmpty()) {
        params.putIfAbsent(this.expectedResultParamName, expectedResult);
      }
    }
    if (this.commentParam) {
      final String comment = step.getComment();
      if (!comment.isEmpty()) {
        params.putIfAbsent(this.commentParamName, comment);
      }
    }
    if (!params.isEmpty()) {
      final List<Parameter> allureParams = stepResult.getParameters();
      params.forEach((paramName, paramValue) -> allureParams.add(ResultsUtils.createParameter(paramName, paramValue)));
    }
    stepResult.setName(this.keywordPosition.concat(
      this.keywordValue(keyword),
      this.processStepName(step, step.getName(), params)
    ));

    Allure.getLifecycle().startStep(UUID.randomUUID().toString(), stepResult);
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
    if (this.onlyKeywordSteps && step.getKeyword().value().isEmpty()) {
      return;
    }

    final AllureLifecycle allureLifecycle = Allure.getLifecycle();
    allureLifecycle.updateStep(stepResult ->
      stepResult.setStatus(ResultsUtils.getStatus(exception).orElse(Status.BROKEN))
        .setStatusDetails(ResultsUtils.getStatusDetails(exception).orElse(null))
    );
    allureLifecycle.stopStep();
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
