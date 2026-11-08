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
package org.stebz.allure;

import dev.jlet.function.ThrowingFunction;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
 * Tests for {@link AllureStepListener}.
 */
final class AllureStepListenerTest {

  private static AllureStepListener newListener(final Map<String, String> properties) {
    return new AllureStepListener(new PropertiesReader.Of(properties));
  }

  private static AllureStepListener newListenerWithProperty(final String name,
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

  private static StepResult startMarkerStep() {
    final StepResult marker = new StepResult().setName("marker");
    Allure.getLifecycle().startStep(UUID.randomUUID().toString(), marker);
    return marker;
  }

  private static AtomicReference<StepResult> currentStepRef() {
    final AtomicReference<StepResult> ref = new AtomicReference<>();
    Allure.getLifecycle().updateStep(ref::set);
    return ref;
  }

  private static void stopAllureStep() {
    Allure.getLifecycle().stopStep();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newListener(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newListenerWithProperty("stebz.allure.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldReportStepWithNameParamsExpectedResultAndComment() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    params.put("param2", "value2");
    final RunnableStep step = step("keyword", "name", false, params, "expected result", "comment");
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final StepResult stepResult = currentStepRef().get();

      assertThat(stepResult).isNotNull();
      assertThat(stepResult.getStage()).isEqualTo(Stage.RUNNING);
      assertThat(stepResult.getStatus()).isNull();
      assertThat(stepResult.getName()).isEqualTo("keyword name");
      assertThat(stepResult.getParameters()).containsExactly(
        new Parameter().setName("param1").setValue("value1"),
        new Parameter().setName("param2").setValue("value2"),
        new Parameter().setName("Expected result").setValue("expected result"),
        new Parameter().setName("Comment").setValue("comment")
      );
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldNotStartStepWhenListenerDisabled() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final AllureStepListener listener = newListenerWithProperty("stebz.allure.enabled", "false");
    final StepResult marker = startMarkerStep();

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get())
        .isSameAs(marker);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldNotStartStepWhenStepIsHidden() {
    final RunnableStep step = step(null, "name", true, null, null, null);
    final AllureStepListener listener = newListener(new HashMap<>());
    final StepResult marker = startMarkerStep();

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get())
        .isSameAs(marker);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldNotStartNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final AllureStepListener listener =
      newListenerWithProperty("stebz.allure.onlyKeywordSteps", "true");
    final StepResult marker = startMarkerStep();

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get())
        .isSameAs(marker);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldProcessNameTemplateWithIndexedParams() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get().getName())
        .isEqualTo("name with value1");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldNotProcessNameWhenProcessNameDisabled() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name with {0}", false, params, null, null);
    final AllureStepListener listener = newListenerWithProperty("stebz.allure.processName", "false");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get().getName())
        .isEqualTo("name with {0}");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldAddContextParamWhenEnabled() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step(null, "name", false, params, null, null);
    final AllureStepListener listener = newListenerWithProperty("stebz.allure.contextParam", "true");

    try {
      listener.onStepStart(step, NullableOptional.of("context value"));
      final List<Parameter> parameters = currentStepRef().get().getParameters();
      assertThat(parameters).hasSize(2);
      assertThat(parameters.get(1).getName()).isEqualTo("Context");
      assertThat(parameters.get(1).getValue()).isEqualTo("context value");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldNotAddExpectedResultParamWhenDisabled() {
    final RunnableStep step = step(null, "name", false, null, "expected result", null);
    final AllureStepListener listener =
      newListenerWithProperty("stebz.allure.expectedResultParam", "false");

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get().getParameters())
        .isEmpty();
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepStartShouldUseKeywordPositionAndUppercaseProperties() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.allure.keywordPosition", "AT_END");
    properties.put("stebz.allure.keywordToUppercase", "true");
    final RunnableStep step = step("keyword", "name", false, null, null, null);
    final AllureStepListener listener = newListener(properties);

    try {
      listener.onStepStart(step, NullableOptional.empty());
      assertThat(currentStepRef().get().getName())
        .isEqualTo("name KEYWORD");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepSuccessShouldFinishStepWithPassedStatus() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = currentStepRef();
      listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.of("result"));
      final StepResult stepResult = stepResultRef.get();

      assertThat(stepResult.getStage()).isEqualTo(Stage.FINISHED);
      assertThat(stepResult.getStatus()).isSameAs(Status.PASSED);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepSuccessShouldKeepCustomStepStatus() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = currentStepRef();
      AllureStepListener.stepStatus(Status.SKIPPED);
      listener.onStepSuccess(step, NullableOptional.empty(), NullableOptional.empty());
      assertThat(stepResultRef.get().getStatus())
        .isSameAs(Status.SKIPPED);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepFailureShouldFinishStepWithBrokenStatusAndDetails() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final Throwable exception = new IllegalStateException("failure message");
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = currentStepRef();
      listener.onStepFailure(step, NullableOptional.empty(), exception);
      final StepResult stepResult = stepResultRef.get();

      assertThat(stepResult.getStage()).isEqualTo(Stage.FINISHED);
      assertThat(stepResult.getStatus()).isSameAs(Status.BROKEN);
      assertThat(stepResult.getStatusDetails().getMessage()).isEqualTo("failure message");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void onStepFailureShouldUseFailedStatusForAssertionError() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final AssertionError exception = new AssertionError("assertion message");
    final AllureStepListener listener = newListener(new HashMap<>());

    try {
      listener.onStepStart(step, NullableOptional.empty());
      final AtomicReference<StepResult> stepResultRef = currentStepRef();
      listener.onStepFailure(step, NullableOptional.empty(), exception);
      final StepResult stepResult = stepResultRef.get();

      assertThat(stepResult.getStatus()).isSameAs(Status.FAILED);
      assertThat(stepResult.getStatusDetails().getMessage()).isEqualTo("assertion message");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepParameterShouldAddParameterAndReturnValue() {
    try {
      startMarkerStep();
      final String returnedValue = AllureStepListener.stepParameter("param name", "param value");

      assertThat(returnedValue).isEqualTo("param value");
      assertThat(currentStepRef().get().getParameters())
        .containsExactly(new Parameter().setName("param name").setValue("param value"));
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepParameterShouldAddParameterWithExcludedAndMode() {
    try {
      startMarkerStep();
      AllureStepListener.stepParameter("param name", "param value", true, Parameter.Mode.MASKED);

      final Parameter parameter = currentStepRef().get().getParameters().get(0);
      assertThat(parameter.getName()).isEqualTo("param name");
      assertThat(parameter.getExcluded()).isTrue();
      assertThat(parameter.getMode()).isSameAs(Parameter.Mode.MASKED);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepParameterConsumerShouldAddCustomParameter() {
    try {
      startMarkerStep();
      AllureStepListener.stepParameter(parameter -> parameter.setName("custom name"));

      final Parameter parameter = currentStepRef().get().getParameters().get(0);
      assertThat(parameter.getName()).isEqualTo("custom name");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepNameShouldSetStepName() {
    try {
      startMarkerStep();
      AllureStepListener.stepName("new name");
      assertThat(currentStepRef().get().getName())
        .isEqualTo("new name");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepNameFunctionShouldUpdateStepName() {
    try {
      startMarkerStep().setName("origin name");
      AllureStepListener.stepName(name -> name + " updated");
      assertThat(currentStepRef().get().getName())
        .isEqualTo("origin name updated");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepStatusShouldSetStepStatus() {
    try {
      startMarkerStep();
      AllureStepListener.stepStatus(Status.SKIPPED);
      assertThat(currentStepRef().get().getStatus())
        .isSameAs(Status.SKIPPED);
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void stepStatusThrowableShouldSetBrokenStatusAndDetails() {
    try {
      startMarkerStep();
      AllureStepListener.stepStatus(new IllegalStateException("message"));
      final StepResult stepResult = currentStepRef().get();
      assertThat(stepResult.getStatus()).isSameAs(Status.BROKEN);
      assertThat(stepResult.getStatusDetails().getMessage()).isEqualTo("message");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void updateStepShouldUpdateCurrentStep() {
    try {
      final StepResult marker = startMarkerStep();
      AllureStepListener.updateStep(stepResult -> stepResult.setName("updated"));
      assertThat(marker.getName()).isEqualTo("updated");
    } finally {
      stopAllureStep();
    }
  }

  @Test
  void staticMethodsShouldTakeNoEffectWithoutRunningStep() {
    assertThatCode(() -> {
      AllureStepListener.stepParameter("param name", "param value");
      AllureStepListener.stepName("new name");
      AllureStepListener.stepStatus(Status.PASSED);
      AllureStepListener.updateStep(stepResult -> { });
    }).doesNotThrowAnyException();
    assertThat(currentStepRef().get())
      .isNull();
  }

  @Test
  void staticMethodsShouldThrowNpeOnNullArgs() {
    assertThatThrownBy(() -> AllureStepListener.stepParameter(null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.stepParameter(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.stepName((String) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.stepName((ThrowingFunction<String, String, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.stepStatus((Status) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.stepStatus((Throwable) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> AllureStepListener.updateStep(null))
      .isInstanceOf(NullPointerException.class);
  }
}
