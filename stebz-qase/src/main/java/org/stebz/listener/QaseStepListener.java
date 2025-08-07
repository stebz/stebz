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
  private static final String CONTEXT_PARAM_NAME = "context";
  private static final String COMMENT_ATTACHMENT_NAME = "Comment";
  private final boolean enabled;
  private final int order;
  private final KeywordPosition keywordPosition;
  private final boolean processName;
  private final boolean contextParam;
  private final boolean commentAttachment;
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
    this.keywordPosition =
      properties.getEnum("stebz.listeners.qase.keywordPosition", KeywordPosition.class, KeywordPosition.AT_START);
    this.processName = properties.getBoolean("stebz.listeners.qase.processName", true);
    this.contextParam = properties.getBoolean("stebz.listeners.qase.contextParam", true);
    this.commentAttachment = properties.getBoolean("stebz.listeners.qase.commentAttachment", true);
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
    final Map<String, Object> params = step.getParams();
    if (this.contextParam && context.isPresent()) {
      params.putIfAbsent(CONTEXT_PARAM_NAME, context.get());
    }
    StepStorage.startStep();
    final StepResult stepResult = StepStorage.getCurrentStep();
    stepResult.data.action =
      this.keywordPosition.concat(step.getKeyword(), this.processStepName(step, step.getName(), params));

    final String expectedResult = step.getExpectedResult();
    if (!expectedResult.isEmpty()) {
      stepResult.data.expectedResult = expectedResult;
    }
    if (this.commentAttachment) {
      final String comment = step.getComment();
      if (!comment.isEmpty()) {
        final Attachment attachment = new Attachment();
        attachment.fileName = COMMENT_ATTACHMENT_NAME;
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
    final StepResult stepResult = StepStorage.getCurrentStep();
    stepResult.execution.status = StepResultStatus.FAILED;
    stepResult.throwable = exception;
    StepStorage.stopStep();
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
    for (final Map.Entry<String, Object> entry : params.entrySet()) {
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
