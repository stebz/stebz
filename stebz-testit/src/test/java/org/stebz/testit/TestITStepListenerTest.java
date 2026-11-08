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
package org.stebz.testit;

import dev.jlet.function.ThrowingFunction;
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
import ru.testit.clients.ClientConfiguration;
import ru.testit.listener.ListenerManager;
import ru.testit.models.ClassContainer;
import ru.testit.models.ItemStatus;
import ru.testit.models.MainContainer;
import ru.testit.models.StepResult;
import ru.testit.models.TestResult;
import ru.testit.properties.AdapterConfig;
import ru.testit.services.Adapter;
import ru.testit.services.AdapterManager;
import ru.testit.services.ResultStorage;
import ru.testit.services.ThreadContext;
import ru.testit.writers.Writer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

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
 * Tests for {@link TestITStepListener}.
 */
final class TestITStepListenerTest {

  private static TestITStepListener newListener(final Map<String, String> properties) {
    return new TestITStepListener(new PropertiesReader.Of(properties));
  }

  private static TestITStepListener newListenerWithProperty(final String name,
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

  private static void setAdapterManager(final AdapterManager manager) throws Exception {
    final Field field = Adapter.class.getDeclaredField("adapterManager");
    field.setAccessible(true);
    field.set(null, manager);
  }

  private static AdapterManager newAdapterManager(final ThreadContext threadContext) {
    final Writer writer = new Writer() {
      @Override
      public boolean writeTestRealtime(final TestResult testResult) {
        return true;
      }

      @Override
      public void writeTest(final TestResult testResult) {
        // no-op
      }

      @Override
      public void writeClass(final ClassContainer container) {
        // no-op
      }

      @Override
      public void writeTests(final MainContainer container) {
        // no-op
      }

      @Override
      public String writeAttachment(final String path) {
        return path;
      }
    };
    return new AdapterManager(
      new ClientConfiguration(new Properties()),
      new AdapterConfig(new Properties()),
      threadContext,
      new ResultStorage(),
      writer,
      null,
      new ListenerManager(new ArrayList<>())
    );
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
    assertThat(newListenerWithProperty("stebz.testit.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenStepIsHidden() {
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWithoutRunningTestCase() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step("keyword", "name with {0}", false, params,
        "expected result", "comment"), NullableOptional.of("context value"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWithoutRunningStep() {
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.of("result"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWithoutRunningStep() {
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void staticMethodsShouldTakeNoEffectWithoutRunningStep() {
    assertThatCode(() -> {
      TestITStepListener.stepParameter("param name", "param value");
      TestITStepListener.stepName("new name");
      TestITStepListener.stepName(name -> name + " updated");
      TestITStepListener.stepStatus(ItemStatus.PASSED);
      TestITStepListener.stepStatus(new IllegalStateException("message"));
      TestITStepListener.updateStep(stepResult -> { });
    }).doesNotThrowAnyException();
  }

  @Test
  void staticMethodsShouldThrowNpeOnNullArgs() {
    assertThatThrownBy(() -> TestITStepListener.stepParameter(null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> TestITStepListener.stepName((String) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> TestITStepListener.stepName((ThrowingFunction<String, String, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> TestITStepListener.stepStatus((ItemStatus) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> TestITStepListener.stepStatus((Throwable) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> TestITStepListener.updateStep(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void constructorShouldReadStartupProperties() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.testit.order", "54321");
    properties.put("stebz.testit.keywordToUppercase", "true");
    setStartupPropertiesReader(new PropertiesReader.Of(properties));
    try {
      final TestITStepListener listener = new TestITStepListener();

      assertThat(listener.order())
        .isEqualTo(54321);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void onStepStartShouldReportStepAndOnStepSuccessShouldFinishItWithPassedStatus() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.testit.contextParam", "true");
    properties.put("stebz.testit.contextDesc", "true");
    final TestITStepListener listener = newListener(properties);
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    params.put("nullParam", null);
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, params, "expected result", "comment"),
        NullableOptional.of("context value"));
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);
      final StepResult stepResult = stepResultRef.get();

      assertThat(stepResult).isNotNull();
      assertThat(stepResult.getTitle())
        .isEqualTo("keyword name");
      assertThat(stepResult.getParameters())
        .containsEntry("param1", "value1")
        .containsEntry("nullParam", "")
        .containsEntry("Context", "context value");
      assertThat(stepResult.getDescription())
        .isEqualTo("Context: context value. Expected result: expected result. Comment: comment");

      listener.onStepSuccess(step("keyword", "name", false, params, "expected result", "comment"),
        NullableOptional.empty(), NullableOptional.of("result"));

      assertThat(stepResult.getItemStatus())
        .isSameAs(ItemStatus.PASSED);
      assertThat(threadContext.getCurrent())
        .contains("test-case-uuid");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepFailureShouldSetFailedStatusAndThrowable() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final Throwable exception = new IllegalStateException("failure message");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), exception);
      final StepResult stepResult = stepResultRef.get();

      assertThat(stepResult.getItemStatus())
        .isSameAs(ItemStatus.FAILED);
      assertThat(stepResult.getThrowable())
        .isSameAs(exception);
      assertThat(threadContext.getCurrent())
        .contains("test-case-uuid");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepSuccessShouldKeepCustomStepStatus() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);
      TestITStepListener.stepStatus(ItemStatus.SKIPPED);
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty());

      assertThat(stepResultRef.get().getItemStatus())
        .isSameAs(ItemStatus.SKIPPED);
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenListenerDisabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenStepIsHidden() {
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", true, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenListenerDisabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenStepIsHidden() {
    final TestITStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", true, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void staticMethodsShouldUpdateRunningStep() throws Exception {
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      Adapter.getAdapterManager().startStep("step-uuid", new StepResult());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      TestITStepListener.stepParameter("param1", "value1");
      TestITStepListener.stepParameter("param2", null);
      TestITStepListener.stepName("new name");
      TestITStepListener.stepName(name -> name + " updated");
      TestITStepListener.stepStatus(ItemStatus.PASSED);
      TestITStepListener.stepStatus(new IllegalStateException("message"));
      TestITStepListener.updateStep(stepResult -> stepResult.setDescription("description"));

      final StepResult stepResult = stepResultRef.get();
      assertThat(stepResult.getParameters())
        .containsEntry("param1", "value1")
        .containsEntry("param2", "");
      assertThat(stepResult.getTitle())
        .isEqualTo("new name updated");
      assertThat(stepResult.getItemStatus())
        .isSameAs(ItemStatus.FAILED);
      assertThat(stepResult.getThrowable())
        .hasMessage("message");
      assertThat(stepResult.getDescription())
        .isEqualTo("description");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldNotProcessNameWhenProcessNameDisabled() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.processName", "false");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name with {0}", false, params, null, null),
        NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name with {0}");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldBuildDescriptionFromCommentOnly() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, "comment"), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getDescription())
        .isEqualTo("Comment: comment");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldNotBuildDescriptionWithoutExpectedResultAndComment() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getDescription())
        .isNull();
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUppercaseKeyword() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.keywordToUppercase", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("KEYWORD name");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUseKeywordOnlyWhenNameIsEmpty() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", null, false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("keyword");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUseNameOnlyWhenKeywordIsEmpty() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUseKeywordAtEndOnlyWhenNameIsEmpty() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.keywordPosition", "AT_END");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", null, false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("keyword");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUseNameAtEndOnlyWhenKeywordIsEmpty() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.testit.keywordPosition", "AT_END");
    properties.put("stebz.testit.keywordToUppercase", "true");
    final TestITStepListener listener = newListener(properties);
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsFromJoinPoint() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(
        stepWithJoinPoint(null, "name {method} {class} {classRef}", new Object()), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name toString String java.lang.String");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldAddReflectionParamsWithoutThis() throws Exception {
    final TestITStepListener listener = newListener(new HashMap<>());
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(stepWithJoinPoint(null, "name {method}", null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name toString");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldUseKeywordAndNameAtEnd() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.keywordPosition", "AT_END");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("name keyword");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabledAndStepIsHidden() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWhenListenerDisabledAndStepIsHidden() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", true, null, null, null),
        NullableOptional.empty(), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWhenListenerDisabledAndStepIsHidden() {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.enabled", "false");

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", true, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldReportKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getTitle())
        .isEqualTo("keyword name");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepSuccessShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());
      listener.onStepSuccess(step("keyword", "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.empty());

      assertThat(threadContext.getCurrent())
        .contains("test-case-uuid");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepFailureShouldFinishKeywordStepWhenOnlyKeywordStepsEnabled() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.onlyKeywordSteps", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());
      listener.onStepFailure(step("keyword", "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"));

      assertThat(threadContext.getCurrent())
        .contains("test-case-uuid");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldNotAddContextParamWhenContextIsEmpty() throws Exception {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.contextParam", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, params, null, null), NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getParameters())
        .containsEntry("param1", "value1")
        .doesNotContainKey("Context");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldNotBuildDescriptionWithAllDescriptionPartsDisabled() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.testit.contextDesc", "true");
    properties.put("stebz.testit.expectedResultDesc", "false");
    properties.put("stebz.testit.commentDesc", "false");
    final TestITStepListener listener = newListener(properties);
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, "expected result", "comment"),
        NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getDescription())
        .isNull();
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }

  @Test
  void onStepStartShouldAddEmptyContextParamWhenContextValueIsNull() throws Exception {
    final TestITStepListener listener = newListenerWithProperty("stebz.testit.contextParam", "true");
    final ThreadContext threadContext = new ThreadContext();
    setAdapterManager(newAdapterManager(threadContext));
    try {
      threadContext.start("test-case-uuid");
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.of(null));
      final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
      Adapter.getAdapterManager().updateStep(stepResultRef::set);

      assertThat(stepResultRef.get().getParameters())
        .containsEntry("Context", "");
    } finally {
      threadContext.clear();
      setAdapterManager(null);
    }
  }
}
