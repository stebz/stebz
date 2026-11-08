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
package org.stebz.reportportal;

import com.epam.reportportal.service.CapturingLaunch;
import com.epam.reportportal.service.Launch;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.stebz.core.attribute.StepAttribute.COMMENT;
import static org.stebz.core.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.core.attribute.StepAttribute.HIDING;
import static org.stebz.core.attribute.StepAttribute.KEYWORD;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.core.attribute.StepAttribute.PARAMS;

/**
 * Tests for {@link ReportPortalStepListener}.
 */
final class ReportPortalStepListenerTest {

  private static ReportPortalStepListener newListener(final Map<String, String> properties) {
    return new ReportPortalStepListener(new PropertiesReader.Of(properties));
  }

  private static ReportPortalStepListener newListenerWithProperty(final String name,
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

  private static void setCurrentLaunchToNoop() throws Exception {
    final Field field = Launch.class.getDeclaredField("CURRENT_LAUNCH");
    field.setAccessible(true);
    ((ThreadLocal<Launch>) field.get(null)).set(Launch.NOOP_LAUNCH);
  }

  private static void clearCurrentLaunch() throws Exception {
    final Field field = Launch.class.getDeclaredField("CURRENT_LAUNCH");
    field.setAccessible(true);
    ((ThreadLocal<Launch>) field.get(null)).remove();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newListener(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newListenerWithProperty("stebz.reportportal.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.of("result"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabled() {
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.enabled", "false");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenStepIsHidden() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void listenerShouldProcessStepLifecycleWithNoopLaunch() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step("keyword", "name", false, params, "expected result", "comment");
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() -> {
      setCurrentLaunchToNoop();
      try {
        listener.onStepStart(step, NullableOptional.of("context value"));
        listener.onStepSuccess(step, NullableOptional.of("context value"), NullableOptional.empty());
      } finally {
        clearCurrentLaunch();
      }
    }).doesNotThrowAnyException();
  }

  @Test
  void listenerShouldProcessFailedStepLifecycleWithNoopLaunch() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() -> {
      setCurrentLaunchToNoop();
      try {
        listener.onStepStart(step, NullableOptional.empty());
        listener.onStepFailure(step, NullableOptional.empty(), new IllegalStateException("message"));
      } finally {
        clearCurrentLaunch();
      }
    }).doesNotThrowAnyException();
  }

  @Test
  void constructorShouldReadStartupProperties() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.reportportal.order", "54321");
    properties.put("stebz.reportportal.keywordToUppercase", "true");
    setStartupPropertiesReader(new PropertiesReader.Of(properties));
    try {
      final ReportPortalStepListener listener = new ReportPortalStepListener();

      assertThat(listener.order())
        .isEqualTo(54321);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void onStepStartShouldReportStepWithKeywordNameAndDescription() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step("keyword", "name", false, params, "expected result", "comment");
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      final StartTestItemRQ request = launch.lastStartedStep();

      assertThat(request).isNotNull();
      assertThat(request.getName())
        .isEqualTo("keyword name");
      assertThat(request.getDescription())
        .isEqualTo("Expected result: expected result. Comment: comment");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepSuccessShouldFinishNestedStep() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.empty());

      assertThat(launch.lastFinishedStep())
        .isEqualTo("finished");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepFailureShouldFinishNestedStepWithThrowable() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final Throwable exception = new IllegalStateException("failure message");
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      listener.onStepFailure(step, NullableOptional.empty(), exception);

      assertThat(launch.lastFinishedStep())
        .isSameAs(exception);
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldProcessNameTemplateWithIndexedParams() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name with value1");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldNotProcessNameWhenProcessNameDisabled() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.processName", "false");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name with {0}");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldAddContextParamToNameTemplateWhenEnabled() throws Exception {
    final RunnableStep step = step(null, "name with {Context}", false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.contextParam", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.of("context value"));
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name with context value");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldNotAddContextParamWhenContextIsEmpty() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.contextParam", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUppercaseKeyword() throws Exception {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.keywordToUppercase", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("KEYWORD name");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUseKeywordOnlyWhenNameIsEmpty() throws Exception {
    final RunnableStep step = step("keyword", null, false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("keyword");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUseNameOnlyWhenKeywordIsEmpty() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUseKeywordAtEndOnlyWhenNameIsEmpty() throws Exception {
    final RunnableStep step = step("keyword", null, false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.keywordPosition", "AT_END");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("keyword");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUseNameAtEndOnlyWhenKeywordIsEmpty() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.keywordPosition", "AT_END");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldUseKeywordAndNameAtEnd() throws Exception {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.keywordPosition", "AT_END");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name keyword");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldBuildDescriptionWithContextExpectedResultAndComment() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.reportportal.contextDesc", "true");
    final RunnableStep step = step(null, "name", false, null, "expected result", "comment");
    final ReportPortalStepListener listener = newListener(properties);

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.of("context value"));
      assertThat(launch.lastStartedStep().getDescription())
        .isEqualTo("Context: context value. Expected result: expected result. Comment: comment");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldBuildDescriptionFromCommentOnly() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, "comment");
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getDescription())
        .isEqualTo("Comment: comment");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldNotBuildDescriptionWithoutExpectedResultAndComment() throws Exception {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getDescription())
        .isNull();
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldNotBuildDescriptionWithAllDescriptionPartsDisabled() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.reportportal.contextDesc", "true");
    properties.put("stebz.reportportal.expectedResultDesc", "false");
    properties.put("stebz.reportportal.commentDesc", "false");
    final RunnableStep step = step(null, "name", false, null, "expected result", "comment");
    final ReportPortalStepListener listener = newListener(properties);

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getDescription())
        .isNull();
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsFromJoinPoint() throws Exception {
    final RunnableStep step = stepWithJoinPoint(null, "name {method} {class} {classRef}", new Object());
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name toString String java.lang.String");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsWithoutThis() throws Exception {
    final RunnableStep step = stepWithJoinPoint(null, "name {method}", null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("name toString");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenListenerDisabled() {
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.enabled", "false");

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenStepIsHidden() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", true, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.empty());

      assertThat(launch.lastFinishedStep())
        .isEqualTo("finished");
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenListenerDisabled() {
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.enabled", "false");

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenStepIsHidden() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", true, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final Throwable exception = new IllegalStateException("failure message");
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      listener.onStepFailure(step, NullableOptional.empty(), exception);

      assertThat(launch.lastFinishedStep())
        .isSameAs(exception);
    } finally {
      clearCurrentLaunch();
    }
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabledAndStepIsHidden() {
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.enabled", "false");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldReportKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    final CapturingLaunch launch = new CapturingLaunch();
    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(launch.lastStartedStep().getName())
        .isEqualTo("keyword name");
    } finally {
      clearCurrentLaunch();
    }
  }
}
