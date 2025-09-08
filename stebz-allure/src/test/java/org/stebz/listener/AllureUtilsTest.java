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
import io.qameta.allure.model.StepResult;
import org.junit.jupiter.api.Test;
import org.stebz.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.listener.AllureUtils.hidden;
import static org.stebz.listener.AllureUtils.hiddenSteps;

/**
 * Tests for {@link AllureUtils}.
 */
final class AllureUtilsTest {

  @Test
  void hiddenRunnableMethodShouldHideSteps() {
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();

    hidden(() -> listener.onStepStart(RunnableStep.empty(), NullableOptional.empty()));
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }

  @Test
  void hiddenStepsRunnableMethodShouldHideSteps() {
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();

    hiddenSteps(() -> listener.onStepStart(RunnableStep.empty(), NullableOptional.empty()));
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }

  @Test
  void hiddenSupplierMethodShouldHideStepsAndReturnsResult() {
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
    final Object result = new Object();

    assertThat(
      hidden(() -> {
        listener.onStepStart(RunnableStep.empty(), NullableOptional.empty());
        return result;
      })
    ).isSameAs(result);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }

  @Test
  void hiddenStepsSupplierMethodShouldHideStepsAndReturnsResult() {
    final AllureStepListener listener = new AllureStepListener(new PropertiesReader.Of(new Properties()));
    final AtomicReference<StepResult> stepResultRef = new AtomicReference<>();
    final Object result = new Object();

    assertThat(
      hiddenSteps(() -> {
        listener.onStepStart(RunnableStep.empty(), NullableOptional.empty());
        return result;
      })
    ).isSameAs(result);
    Allure.getLifecycle().updateStep(stepResultRef::set);
    assertThat(stepResultRef.get())
      .isNull();
  }
}
