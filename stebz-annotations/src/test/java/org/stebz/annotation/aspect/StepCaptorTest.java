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
package org.stebz.annotation.aspect;

import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.entry;
import static org.stebz.annotation.aspect.StepCaptor.captured;

/**
 * Tests for {@link StepCaptor}.
 */
final class StepCaptorTest {

  @BeforeEach
  void clearState() {
    StaticStepListener.clear();
    MethodSteps.quickStaticMethodBodyCount.set(0);
    QuickConstructorStep.ctorBodyCount.set(0);
  }

  /**
   * Tests for {@code StepCaptor.captured} methods returning {@code RunnableStep}.
   */
  @Nested
  class CapturedRunnableStepTests {

    @Test
    void capturedMethodRunnableStep() {
      final RunnableStep step = captured(MethodSteps::quickStaticMethodStep);

      assertThat(MethodSteps.quickStaticMethodBodyCount)
        .hasValue(0);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
      assertThat(step.getName())
        .isEqualTo("quickStaticMethodStep");
      assertThat(step.getParams())
        .containsExactly(
          entry("param name", "param value")
        );
    }

    @Test
    void capturedMethodRunnableStepExecutesBodyOnStepExecution() {
      final RunnableStep step = captured(MethodSteps::quickStaticMethodStep);

      StepExecutor.get().execute(step);

      assertThat(MethodSteps.quickStaticMethodBodyCount)
        .hasValue(1);
      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      assertThat(StaticStepListener.succeededSteps)
        .hasSize(1);
      assertThat(StaticStepListener.startedSteps.get(0).getName())
        .isEqualTo("quickStaticMethodStep");
    }

    @Test
    void nestedCaptureRestoresOuterCapture() {
      final RunnableStep[] innerStep = new RunnableStep[1];
      final RunnableStep outerStep = captured(() -> {
        MethodSteps.quickStaticMethodStep();
        innerStep[0] = captured(MethodSteps::quickStaticMethodStep);
      });

      assertThat(MethodSteps.quickStaticMethodBodyCount)
        .hasValue(0);
      assertThat(outerStep.getName())
        .isEqualTo("quickStaticMethodStep");
      assertThat(innerStep[0].getName())
        .isEqualTo("quickStaticMethodStep");

      StepExecutor.get().execute(outerStep);
      StepExecutor.get().execute(innerStep[0]);

      assertThat(MethodSteps.quickStaticMethodBodyCount)
        .hasValue(2);
    }
  }

  /**
   * Tests for {@code StepCaptor.captured} methods returning {@code SupplierStep}.
   */
  @Nested
  class CapturedSupplierStepTests {

    @Test
    void capturedMethodSupplierStep() throws Throwable {
      final SupplierStep<String> step = captured(new MethodSteps()::quickInstanceMethodStep, "value");

      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
      assertThat(step.getName())
        .isEqualTo("quickInstanceMethodStep");
      assertThat(step.getParams())
        .containsExactly(
          entry("param", "value")
        );
      assertThat(step.isHidden())
        .isTrue();
      assertThat(step.getBody().get())
        .isEqualTo("value");
    }

    @Test
    void capturedMethodSupplierStepWithCustomResult() throws Throwable {
      final SupplierStep<Integer> step = captured(() -> {
        new MethodSteps().quickInstanceMethodStep("value");
        return 100;
      });

      assertThat(step.getName())
        .isEqualTo("quickInstanceMethodStep");
      assertThat(step.isHidden())
        .isTrue();
      assertThat(step.getBody().get())
        .isEqualTo(100);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void capturedMethodSupplierStepExecutesBodyOnStepExecution() {
      final SupplierStep<String> step = captured(new MethodSteps()::quickInstanceMethodStep, "value");

      final String result = StepExecutor.get().execute(step);

      assertThat(result)
        .isEqualTo("value");
      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      assertThat(StaticStepListener.succeededSteps)
        .hasSize(1);
    }
  }

  /**
   * Tests for constructor steps capturing.
   */
  @Nested
  class CapturedConstructorStepTests {

    @Test
    void capturedConstructorSupplierStep() throws Throwable {
      final SupplierStep<QuickConstructorStep> step = captured(QuickConstructorStep::new, "value1", "value2");

      assertThat(QuickConstructorStep.ctorBodyCount)
        .hasValue(0);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
      assertThat(step.getName())
        .isEqualTo("name value");
      assertThat(step.getParams())
        .containsExactly(
          entry("param1", "******"),
          entry("param name", "param value")
        );
      assertThat(step.getBody().get())
        .isInstanceOf(QuickConstructorStep.class);
      assertThat(QuickConstructorStep.ctorBodyCount)
        .hasValue(1);
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
      assertThat(step.getBody().get())
        .isEqualTo(100);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void capturedConstructorSupplierStepReturnsNewInstances() throws Throwable {
      final SupplierStep<QuickConstructorStep> step = captured(QuickConstructorStep::new, "value1", "value2");
      final ThrowingSupplier<QuickConstructorStep, ?> body = step.getBody();

      final QuickConstructorStep firstResult = body.get();

      assertThat(body.get())
        .isInstanceOf(QuickConstructorStep.class)
        .isNotSameAs(firstResult);
      assertThat(body.get())
        .isInstanceOf(QuickConstructorStep.class)
        .isNotSameAs(firstResult);
      assertThat(QuickConstructorStep.ctorBodyCount)
        .hasValue(3);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }
  }

  /**
   * Tests for {@code StepCaptor.captured} methods error cases.
   */
  @Nested
  class CapturedStepErrorTests {

    @Test
    void nullReference() {
      assertThatNullPointerException()
        .isThrownBy(() -> captured((ThrowingRunnable<?>) null));
    }

    @Test
    void noStepCaptured() {
      assertThatCode(() -> captured(() -> { }))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("does not contain any steps");
    }

    @Test
    void moreThanOneStepCaptured() {
      assertThatCode(() -> captured(() -> {
        new QuickConstructorStep("value1", "value2");
        MethodSteps.quickStaticMethodStep();
      })).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Only one step can be captured");
    }
  }
}
