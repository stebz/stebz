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
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.junit.jupiter.api.Test;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttributes;
import org.stebz.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.attribute.StepAttribute.HIDDEN;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Tests for {@link AllureStepListener}.
 */
final class AllureStepListenerTest {

  @Test
  void onStepStartMethodMethod() {
    final Keyword keyword = new Keyword.Of("keyword");
    final String name = "name";
    final boolean hidden = false;
    final String paramName1 = "param1";
    final String paramValue1 = "value1";
    final String paramName2 = "param2";
    final String paramValue2 = "value2";
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put(paramName1, paramValue1);
    params.put(paramName2, paramValue2);
    final String context = "context";
    final RunnableStep step = RunnableStep.of(
      new StepAttributes.BuilderOf()
        .add(KEYWORD, keyword)
        .add(NAME, name)
        .add(HIDDEN, hidden)
        .add(PARAMS, params)
        .build(),
      RunnableStep.emptyBody()
    );
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();

    listener.onStepStart(step, NullableOptional.of(context));
    Allure.getLifecycle().updateStep(stepResultRef::set);
    try {
      assertThat(stepResultRef.get())
        .isNotNull();
      assertThat(stepResultRef.get().getStage())
        .isEqualTo(Stage.RUNNING);
      assertThat(stepResultRef.get().getStatus())
        .isNull();
      assertThat(stepResultRef.get().getName())
        .isEqualTo(keyword.value() + " " + name);
      assertThat(stepResultRef.get().getParameters()).containsExactly(
        new Parameter().setName(paramName1).setValue(paramValue1),
        new Parameter().setName(paramName2).setValue(paramValue2),
        new Parameter().setName("context").setValue(context)
      );
    } finally {
      Allure.getLifecycle().stopStep();
    }
  }

  @Test
  void onStepSuccessMethod() {
    final RunnableStep step = RunnableStep.of(RunnableStep.emptyBody());
    final NullableOptional<Object> optionalContext = NullableOptional.of("context");
    final NullableOptional<Object> optionalResult = NullableOptional.of("result");
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();

    listener.onStepStart(step, optionalContext);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    listener.onStepSuccess(step, optionalContext, optionalResult);
    assertThat(stepResultRef.get().getStage())
      .isEqualTo(Stage.FINISHED);
    assertThat(stepResultRef.get().getStatus())
      .isSameAs(Status.PASSED);

    stepResultRef.set(null);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }

  @Test
  void onStepFailureMethod() {
    final RunnableStep step = RunnableStep.of(RunnableStep.emptyBody());
    final NullableOptional<Object> optionalContext = NullableOptional.of("context");
    final Throwable exception = new Throwable();
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();

    listener.onStepStart(step, optionalContext);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    listener.onStepFailure(step, optionalContext, exception);
    assertThat(stepResultRef.get().getStage())
      .isEqualTo(Stage.FINISHED);
    assertThat(stepResultRef.get().getStatus())
      .isSameAs(Status.BROKEN);

    stepResultRef.set(null);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }
}
