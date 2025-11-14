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
package org.stebz.aspect;

import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.entry;
import static org.stebz.aspect.StepCaptor.captured;

final class StepCaptorTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  @Test
  void capturedMethodRunnableStep() {
    final RunnableStep step = captured(MethodSteps::quickStaticMethodStep);

    assertThat(step.getName())
      .isEqualTo("quickStaticMethodStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param name", "param value")
      );
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void capturedMethodSupplierStep() throws Throwable {
    final SupplierStep<String> step = captured(new MethodSteps()::quickInstanceMethodStep, "value");

    assertThat(step.getName())
      .isEqualTo("quickInstanceMethodStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param", "value")
      );
    assertThat(step.getHidden())
      .isTrue();
    assertThat(step.body().get())
      .isEqualTo("value");
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void capturedMethodSupplierStepWithCustomResult() throws Throwable {
    final SupplierStep<Integer> step = captured(() -> {
      new MethodSteps().quickInstanceMethodStep("value");
      return 100;
    });

    assertThat(step.getName())
      .isEqualTo("quickInstanceMethodStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param", "value")
      );
    assertThat(step.getHidden())
      .isTrue();
    assertThat(step.body().get())
      .isEqualTo(100);
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void capturedConstructorSupplierStep() throws Throwable {
    final SupplierStep<QuickConstructorStep> step = captured(QuickConstructorStep::new, "value1", "value2");

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getParams())
      .containsExactly(
        entry("param1", "******"),
        entry("param name", "param value")
      );
    assertThat(step.body().get())
      .isInstanceOf(QuickConstructorStep.class);
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void capturedConstructorSupplierStepWithCustomResult() throws Throwable {
    final SupplierStep<Integer> step = captured(() -> {
      new QuickConstructorStep("value1", "value2");
      return 100;
    });

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getParams())
      .containsExactly(
        entry("param1", "******"),
        entry("param name", "param value")
      );
    assertThat(step.body().get())
      .isEqualTo(100);
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void capturedConstructorSupplierStepReturnsNewInstances() throws Throwable {
    final SupplierStep<QuickConstructorStep> step = captured(QuickConstructorStep::new, "value1", "value2");
    final ThrowingSupplier<QuickConstructorStep, ?> body = step.body();
    final QuickConstructorStep firstResult = body.get();

    assertThat(body.get())
      .isInstanceOf(QuickConstructorStep.class)
      .isNotSameAs(firstResult);
    assertThat(body.get())
      .isInstanceOf(QuickConstructorStep.class)
      .isNotSameAs(firstResult);
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void noStepCaptured() {
    assertThatCode(() -> captured(() -> { }))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void moreThanOneStepCaptured() {
    assertThatCode(() -> captured(() -> {
      new QuickConstructorStep("value1", "value2");
      MethodSteps.quickStaticMethodStep();
    })).isInstanceOf(IllegalArgumentException.class);
  }
}
