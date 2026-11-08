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
package org.stebz.qase;

import dev.jlet.function.ThrowingFunction;
import io.qase.commons.StepStorage;
import io.qase.commons.models.domain.Attachment;
import io.qase.commons.models.domain.StepResult;
import io.qase.commons.models.domain.StepResultStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.stebz.annotation.attribute.ReflectiveStepAttributes;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.stebz.core.attribute.StepAttribute.COMMENT;
import static org.stebz.core.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.core.attribute.StepAttribute.HIDING;
import static org.stebz.core.attribute.StepAttribute.KEYWORD;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.core.attribute.StepAttribute.PARAMS;

/**
 * Tests for {@link QaseStepListener}.
 */
final class QaseStepListenerTest {

  private static QaseStepListener newListener(final Map<String, String> properties) {
    return new QaseStepListener(new PropertiesReader.Of(properties));
  }

  private static QaseStepListener newListenerWithProperty(final String name,
                                                          final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newListener(properties);
  }

  private static RunnableStep step(final String keyword,
                                   final String name,
                                   final boolean hidden,
                                   final Map<String, Object> params,
                                   final String expectedResult,
                                   final String comment) {
    final StepAttributes.BuilderOf builder = new StepAttributes.BuilderOf();
    if (keyword != null) {
      builder.add(KEYWORD, new Keyword.Of(keyword));
    }
    if (name != null) {
      builder.add(NAME, name);
    }
    builder.add(HIDING, hidden);
    if (params != null) {
      builder.add(PARAMS, params);
    }
    if (expectedResult != null) {
      builder.add(EXPECTED_RESULT, expectedResult);
    }
    if (comment != null) {
      builder.add(COMMENT, comment);
    }
    return RunnableStep.of(builder.build(), RunnableStep.emptyBody());
  }

  private static RunnableStep stepWithJoinPoint(final String keyword,
                                                final String name,
                                                final Object thisObj) {
    final StepAttributes.BuilderOf builder = new StepAttributes.BuilderOf();
    if (keyword != null) {
      builder.add(KEYWORD, new Keyword.Of(keyword));
    }
    if (name != null) {
      builder.add(NAME, name);
    }
    builder.add(HIDING, false);
    builder.add(ReflectiveStepAttributes.JOIN_POINT, joinPoint(thisObj));
    return RunnableStep.of(builder.build(), RunnableStep.emptyBody());
  }

  private static JoinPoint joinPoint(final Object thisObj) {
    return new JoinPoint() {
      @Override
      public Object getThis() {
        return thisObj;
      }

      @Override
      public Object getTarget() {
        return thisObj;
      }

      @Override
      public Signature getSignature() {
        return new MethodSignature() {
          @Override
          public Class<?> getDeclaringType() {
            return String.class;
          }

          @Override
          public String getDeclaringTypeName() {
            return String.class.getName();
          }

          @Override
          public int getModifiers() {
            return java.lang.reflect.Modifier.PUBLIC;
          }

          @Override
          public String getName() {
            return "toString";
          }

          @Override
          public Method getMethod() {
            return toStringMethod();
          }

          @Override
          public Class<?> getReturnType() {
            return String.class;
          }

          @Override
          public Class<?>[] getParameterTypes() {
            return new Class<?>[0];
          }

          @Override
          public String[] getParameterNames() {
            return new String[0];
          }

          @Override
          public Class<?>[] getExceptionTypes() {
            return new Class<?>[0];
          }

          @Override
          public String toShortString() {
            return "java.lang.String.toString()";
          }

          @Override
          public String toLongString() {
            return "public java.lang.String java.lang.Object.toString()";
          }
        };
      }

      @Override
      public Object[] getArgs() {
        return new Object[0];
      }

      @Override
      public org.aspectj.lang.reflect.SourceLocation getSourceLocation() {
        return null;
      }

      @Override
      public String getKind() {
        return "method-execution";
      }

      @Override
      public JoinPoint.StaticPart getStaticPart() {
        return null;
      }

      @Override
      public String toShortString() {
        return "Object.toString()";
      }

      @Override
      public String toLongString() {
        return "public java.lang.String java.lang.Object.toString()";
      }
    };
  }

  private static Method toStringMethod() {
    try {
      return Object.class.getMethod("toString");
    } catch (final NoSuchMethodException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static void setStartupPropertiesReader(final PropertiesReader reader) throws Exception {
    final Method setMethod = StartupPropertiesReader.class.getDeclaredMethod("set", PropertiesReader.class);
    setMethod.setAccessible(true);
    setMethod.invoke(null, reader);
  }

  private static void clearStartupPropertiesReader() throws Exception {
    final Method clearMethod = StartupPropertiesReader.class.getDeclaredMethod("clear");
    clearMethod.setAccessible(true);
    clearMethod.invoke(null);
  }

  @Test
  void orderDefaultValue() {
    assertThat(newListener(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newListenerWithProperty("stebz.qase.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldReportStepWithKeywordNameExpectedResultAndCommentAttachment() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step("keyword", "name", false, params, "expected result", "comment");
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final StepResult stepResult = StepStorage.getCurrentStep();

      assertThat(stepResult).isNotNull();
      assertThat(stepResult.execution.status).isSameAs(StepResultStatus.UNTESTED);
      assertThat(stepResult.data.action).isEqualTo("keyword name");
      assertThat(stepResult.data.expectedResult).isEqualTo("expected result");
      assertThat(stepResult.attachments).hasSize(1);
      final Attachment attachment = stepResult.attachments.get(0);
      assertThat(attachment.fileName).isEqualTo("Comment");
      assertThat(attachment.content).isEqualTo("comment");
      assertThat(attachment.mimeType).isEqualTo("text/plain");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldNotStartStepWhenListenerDisabled() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.enabled", "false");

    listener.onStepStart(step, NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldNotStartStepWhenStepIsHidden() {
    final RunnableStep step = step(null, "name", true, null, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepStart(step, NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldNotStartNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    listener.onStepStart(step, NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldProcessNameTemplateWithIndexedParams() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name with value1");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldProcessNameTemplateWithRegexSpecialCharsInParamValue() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value $1 \\d (x)");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name with value $1 \\d (x)");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldAddContextParamToNameTemplateWhenEnabled() {
    final RunnableStep step = step(null, "name with {Context}", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.contextParam", "true");

    try {
      listener.onStepStart(step, NullableOptional.of("context value"));
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name with context value");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldNotAddCommentAttachmentWhenDisabled() {
    final RunnableStep step = step(null, "name", false, null, null, "comment");
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.commentAttachment", "false");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().attachments)
        .isEmpty();
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseKeywordPositionAndUppercaseProperties() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.qase.keywordPosition", "AT_END");
    properties.put("stebz.qase.keywordToUppercase", "true");
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final QaseStepListener listener = newListener(properties);

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name KEYWORD");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepSuccessShouldFinishStepWithPassedStatus() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepStart(step, NullableOptional.empty());
    listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.of("result"));
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepSuccessShouldKeepCustomStepStatus() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepStart(step, NullableOptional.empty());
    final StepResult stepResult = StepStorage.getCurrentStep();
    QaseStepListener.stepStatus(StepResultStatus.SKIPPED);
    listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.empty());
    assertThat(stepResult.execution.status)
      .isSameAs(StepResultStatus.SKIPPED);
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldFinishStepWithFailedStatusAndThrowable() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final Throwable exception = new IllegalStateException("failure message");
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepStart(step, NullableOptional.empty());
    final StepResult stepResult = StepStorage.getCurrentStep();
    listener.onStepFailure(step, NullableOptional.empty(), exception);
    assertThat(stepResult.execution.status).isSameAs(StepResultStatus.FAILED);
    assertThat(stepResult.throwable).isSameAs(exception);
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWithoutRunningStep() {
    final QaseStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWithoutRunningStep() {
    final QaseStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException())
    ).doesNotThrowAnyException();
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void stepNameShouldSetStepName() {
    try {
      StepStorage.startStep();
      final StepResult stepResult = StepStorage.getCurrentStep();
      QaseStepListener.stepName("new name");
      assertThat(stepResult.data.action).isEqualTo("new name");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void stepNameFunctionShouldUpdateStepName() {
    try {
      StepStorage.startStep();
      final StepResult stepResult = StepStorage.getCurrentStep();
      stepResult.data.action = "origin name";
      QaseStepListener.stepName(name -> name + " updated");
      assertThat(stepResult.data.action).isEqualTo("origin name updated");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void stepNameMethodsShouldTakeNoEffectWithoutRunningStep() {
    assertThatCode(() -> {
      QaseStepListener.stepName("new name");
      QaseStepListener.stepName(name -> name + " updated");
    }).doesNotThrowAnyException();
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void stepStatusShouldSetStepStatus() {
    try {
      StepStorage.startStep();
      final StepResult stepResult = StepStorage.getCurrentStep();
      QaseStepListener.stepStatus(StepResultStatus.SKIPPED);
      assertThat(stepResult.execution.status).isSameAs(StepResultStatus.SKIPPED);
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void stepStatusThrowableShouldSetFailedStatusAndThrowable() {
    try {
      StepStorage.startStep();
      final StepResult stepResult = StepStorage.getCurrentStep();
      final Throwable exception = new IllegalStateException("message");
      QaseStepListener.stepStatus(exception);
      assertThat(stepResult.execution.status).isSameAs(StepResultStatus.FAILED);
      assertThat(stepResult.throwable).isSameAs(exception);
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void updateStepShouldUpdateCurrentStep() {
    try {
      StepStorage.startStep();
      final StepResult stepResult = StepStorage.getCurrentStep();
      QaseStepListener.updateStep(result -> result.data.action = "updated");
      assertThat(stepResult.data.action).isEqualTo("updated");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void staticMethodsShouldThrowNpeOnNullArgs() {
    assertThatThrownBy(() -> QaseStepListener.stepName((String) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> QaseStepListener.stepName((ThrowingFunction<String, String, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> QaseStepListener.stepStatus((StepResultStatus) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> QaseStepListener.stepStatus((Throwable) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> QaseStepListener.updateStep(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorShouldReadStartupProperties() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.qase.order", "54321");
    properties.put("stebz.qase.keywordToUppercase", "true");
    setStartupPropertiesReader(new PropertiesReader.Of(properties));
    try {
      final QaseStepListener listener = new QaseStepListener();

      assertThat(listener.order())
        .isEqualTo(54321);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void onStepStartShouldNotProcessNameWhenProcessNameDisabled() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.processName", "false");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name with {0}");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseKeywordOnlyWhenNameIsEmpty() {
    final RunnableStep step = step("keyword", null, false, null, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("keyword");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseNameOnlyWhenKeywordIsEmpty() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseKeywordAtEndOnlyWhenNameIsEmpty() {
    final RunnableStep step = step("keyword", null, false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.keywordPosition", "AT_END");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("keyword");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseNameAtEndOnlyWhenKeywordIsEmpty() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.keywordPosition", "AT_END");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldUseKeywordAndNameAtEnd() {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.keywordPosition", "AT_END");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name keyword");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsFromJoinPoint() {
    final RunnableStep step = stepWithJoinPoint(null, "name {method} {class} {classRef}", new Object());
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name toString String java.lang.String");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsWithoutThis() {
    final RunnableStep step = stepWithJoinPoint(null, "name {method}", null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name toString");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepStartShouldConvertDifferentParamValueTypesToString() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("p1", new int[]{1, 2});
    params.put("p2", new long[]{1, 2});
    params.put("p3", new double[]{1.0, 2.0});
    params.put("p4", new float[]{1.0f, 2.0f});
    params.put("p5", new boolean[]{true, false});
    params.put("p6", new short[]{1, 2});
    params.put("p7", new char[]{'a', 'b'});
    params.put("p8", new byte[]{1, 2});
    params.put("p9", new Object[]{"a", "b"});
    params.put("p10", null);
    params.put("p11", "string value");
    params.put("p12", 42);
    params.put("p13", Thread.State.NEW);
    params.put("p14", new Object() {
      @Override
      public String toString() {
        return "custom object";
      }
    });
    final RunnableStep step = step(null,
      "n {p1}|{p2}|{p3}|{p4}|{p5}|{p6}|{p7}|{p8}|{p9}|{p10}|{p11}|{p12}|{p13}|{p14}",
      false, params, null, null);
    final QaseStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("n [1, 2]|[1, 2]|[1.0, 2.0]|[1.0, 2.0]|[true, false]|[1, 2]|[a, b]|[1, 2]"
          + "|a, b|null|string value|42|NEW|custom object");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenListenerDisabled() {
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.enabled", "false");

    listener.onStepSuccess(step(null, "name", false, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenStepIsHidden() {
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepSuccess(step(null, "name", true, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    listener.onStepSuccess(step(null, "name", false, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepSuccessShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    listener.onStepStart(step, NullableOptional.empty());
    listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenListenerDisabled() {
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.enabled", "false");

    listener.onStepFailure(step(null, "name", false, null, null, null),
      NullableOptional.empty(), new IllegalStateException("failure message"));
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenStepIsHidden() {
    final QaseStepListener listener = newListener(new HashMap<>());

    listener.onStepFailure(step(null, "name", true, null, null, null),
      NullableOptional.empty(), new IllegalStateException("failure message"));
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    listener.onStepFailure(step(null, "name", false, null, null, null),
      NullableOptional.empty(), new IllegalStateException("failure message"));
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepFailureShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    listener.onStepStart(step, NullableOptional.empty());
    final StepResult stepResult = StepStorage.getCurrentStep();
    listener.onStepFailure(step, NullableOptional.empty(), new IllegalStateException("failure message"));
    assertThat(stepResult.execution.status).isSameAs(StepResultStatus.FAILED);
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabledAndStepIsHidden() {
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.enabled", "false");

    listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty());
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldReportKeywordStepWhenOnlyKeywordStepsEnabled() {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.onlyKeywordSteps", "true");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("keyword name");
    } finally {
      StepStorage.stopStep();
    }
  }

  @Test
  void stepStatusAndUpdateStepMethodsShouldTakeNoEffectWithoutRunningStep() {
    assertThatCode(() -> {
      QaseStepListener.stepStatus(StepResultStatus.PASSED);
      QaseStepListener.stepStatus(new IllegalStateException("message"));
      QaseStepListener.updateStep(stepResult -> { });
    }).doesNotThrowAnyException();
    assertThat(StepStorage.isStepInProgress()).isFalse();
  }

  @Test
  void onStepStartShouldNotAddContextParamWhenContextIsEmpty() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name", false, params, null, null);
    final QaseStepListener listener = newListenerWithProperty("stebz.qase.contextParam", "true");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(StepStorage.getCurrentStep().data.action)
        .isEqualTo("name");
    } finally {
      StepStorage.stopStep();
    }
  }
}
