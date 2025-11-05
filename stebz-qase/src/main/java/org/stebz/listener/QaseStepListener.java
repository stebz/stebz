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

import io.qase.commons.StepStorage;
import io.qase.commons.models.domain.Attachment;
import io.qase.commons.models.domain.StepResult;
import io.qase.commons.models.domain.StepResultStatus;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Qase {@code StepListener} implementation.
 */
public class QaseStepListener implements StepListener {
  private final boolean enabled;
  private final int order;
  private final boolean onlyKeywordSteps;
  private final KeywordPosition keywordPosition;
  private final boolean keywordToUppercase;
  private final boolean processName;
  private final boolean contextParam;
  private final String contextParamName;
  private final boolean commentAttachment;
  private final String commentAttachmentName;
  private final boolean isStebzAnnotationsUsed;

  /**
   * Ctor.
   */
  public QaseStepListener() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public QaseStepListener(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.listeners.qase.enabled", true);
    this.order = properties.getInteger("stebz.listeners.qase.order", DEFAULT_ORDER);
    this.onlyKeywordSteps = properties.getBoolean("stebz.listeners.qase.onlyKeywordSteps", false);
    this.keywordPosition =
      properties.getEnum("stebz.listeners.qase.keywordPosition", KeywordPosition.class, KeywordPosition.AT_START);
    this.keywordToUppercase = properties.getBoolean("stebz.listeners.qase.keywordToUppercase", false);
    this.processName = properties.getBoolean("stebz.listeners.qase.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.qase.contextParam", true);
    this.contextParamName = properties.getString("stebz.listeners.qase.contextParamName", "Context");
    this.commentAttachment = properties.getBoolean("stebz.listeners.qase.commentAttachment", true);
    this.commentAttachmentName = properties.getString("stebz.listeners.qase.commentAttachmentName", "Comment");
    this.isStebzAnnotationsUsed = isStebzAnnotationsUsed();
  }

  /**
   * Sets current step name if any. Takes no effect if no step run at the moment.
   *
   * @param name the step name
   */
  public static void stepName(final String name) {
    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult != null) {
      stepResult.data.action = name;
    }
  }

  /**
   * Sets current step name if any. Takes no effect if no step run at the moment.
   *
   * @param update the step name update function
   * @throws NullPointerException if {@code update} arg is null
   */
  public static void stepName(final ThrowingFunction<? super String, String, ?> update) {
    if (update == null) { throw new NullPointerException("update arg is null"); }
    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult != null) {
      stepResult.data.action = ThrowingFunction.unchecked(update).apply(stepResult.data.action);
    }
  }

  /**
   * Sets current step status if any. Takes no effect if no step run at the moment.
   *
   * @param status the step status
   */
  public static void stepStatus(final StepResultStatus status) {
    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult != null) {
      stepResult.execution.status = status;
    }
  }

  /**
   * Sets current step status if any. Takes no effect if no step run at the moment.
   *
   * @param exception the step exception
   * @throws NullPointerException if {@code exception} arg is null
   */
  public static void stepStatus(final Throwable exception) {
    if (exception == null) { throw new NullPointerException("exception arg is null"); }
    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult != null) {
      stepResult.execution.status = StepResultStatus.FAILED;
      stepResult.throwable = exception;
    }
  }

  /**
   * Updates current step if any. Takes no effect if no step run at the moment.
   *
   * @param update the step update function
   * @throws NullPointerException if {@code update} arg is null
   */
  public static void updateStep(final ThrowingConsumer<? super StepResult, ?> update) {
    if (update == null) { throw new NullPointerException("update arg is null"); }
    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult != null) {
      ThrowingConsumer.unchecked(update).accept(stepResult);
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
    final Keyword keyword = step.getKeyword();
    if (this.onlyKeywordSteps && keyword.value().isEmpty()) {
      return;
    }

    StepStorage.startStep();
    final StepResult stepResult = StepStorage.getCurrentStep();
    final Map<String, Object> params = step.getParams();
    if (this.contextParam && context.isPresent()) {
      params.putIfAbsent(this.contextParamName, context.get());
    }
    stepResult.data.action = this.keywordPosition.concat(
      this.keywordValue(keyword),
      this.processStepName(step, step.getName(), params)
    );
    final String expectedResult = step.getExpectedResult();
    if (!expectedResult.isEmpty()) {
      stepResult.data.expectedResult = expectedResult;
    }
    if (this.commentAttachment) {
      final String comment = step.getComment();
      if (!comment.isEmpty()) {
        final Attachment attachment = new Attachment();
        attachment.fileName = this.commentAttachmentName;
        attachment.content = comment;
        attachment.mimeType = "text/plain";
        stepResult.attachments.add(attachment);
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
    if (this.onlyKeywordSteps && step.getKeyword().value().isEmpty()) {
      return;
    }

    final StepResult stepResult = StepStorage.getCurrentStep();
    if (stepResult.execution.status == StepResultStatus.UNTESTED) {
      stepResult.execution.status = StepResultStatus.PASSED;
    }
    StepStorage.stopStep();
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

    final StepResult stepResult = StepStorage.getCurrentStep();
    stepResult.execution.status = StepResultStatus.FAILED;
    stepResult.throwable = exception;
    StepStorage.stopStep();
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
                                 String name,
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
    if (paramsToProcess.isEmpty()) {
      return name;
    }
    for (final Map.Entry<String, Object> entry : paramsToProcess.entrySet()) {
      final String paramName = '{' + entry.getKey() + '}';
      if (name.contains(paramName)) {
        name = name.replaceAll("\\" + paramName, asString(entry.getValue()));
      }
    }
    return name;
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

  private static String asString(final Object obj) {
    if (obj == null) {
      return "null";
    }
    if (obj.getClass().isArray()) {
      if (obj instanceof int[]) {
        return Arrays.toString((int[]) obj);
      } else if (obj instanceof long[]) {
        return Arrays.toString((long[]) obj);
      } else if (obj instanceof double[]) {
        return Arrays.toString((double[]) obj);
      } else if (obj instanceof float[]) {
        return Arrays.toString((float[]) obj);
      } else if (obj instanceof boolean[]) {
        return Arrays.toString((boolean[]) obj);
      } else if (obj instanceof short[]) {
        return Arrays.toString((short[]) obj);
      } else if (obj instanceof char[]) {
        return Arrays.toString((char[]) obj);
      } else if (obj instanceof byte[]) {
        return Arrays.toString((byte[]) obj);
      } else {
        return Arrays.stream((Object[]) obj).map(String::valueOf).collect(Collectors.joining(", "));
      }
    }
    return obj.toString();
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
