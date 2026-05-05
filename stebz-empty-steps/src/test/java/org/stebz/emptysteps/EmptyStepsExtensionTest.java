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
package org.stebz.emptysteps;

import dev.jlet.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.emptysteps.EmptyStepsExtension.emptySteps;
import static org.stebz.emptysteps.EmptyStepsExtension.emptyStepsResult;

/**
 * Tests for {@link EmptyStepsExtension}.
 */
final class EmptyStepsExtensionTest {

  @Test
  void interceptStepMethodShouldNotOverrideBodyWithoutMethod() throws Exception {
    final ThrowingRunnable<?> body = () -> { };
    final RunnableStep step = RunnableStep.of(body);
    final EmptyStepsExtension extension = new EmptyStepsExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(((RunnableStep) resultStep).getBody())
      .isSameAs(body);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void interceptStepMethodStepsMethodShouldOverrideBodyInsideMethod() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = new EmptyStepsExtension(new PropertiesReader.Of(new Properties()));

    emptySteps(() -> {
      final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
      assertThat(((RunnableStep) resultStep).getBody())
        .isSameAs(RunnableStep.emptyBody());
    });
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void emptyStepsMethodWithResultShouldOverrideBodyInsideMethodAndReturnsResult() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = new EmptyStepsExtension(new PropertiesReader.Of(new Properties()));
    final Object result = new Object();

    assertThat(
      emptyStepsResult(() -> {
        final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
        assertThat(((RunnableStep) resultStep).getBody())
          .isSameAs(RunnableStep.emptyBody());
        return result;
      })
    ).isSameAs(result);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void emptyStepsMethodWithNestedMethodShouldOverrideBody() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = new EmptyStepsExtension(new PropertiesReader.Of(new Properties()));

    emptySteps(() -> {
      emptySteps(() -> {
        final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
        assertThat(((RunnableStep) resultStep).getBody())
          .isSameAs(RunnableStep.emptyBody());
      });
      final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
      assertThat(((RunnableStep) resultStep).getBody())
        .isSameAs(RunnableStep.emptyBody());
    });
    assertThatTreadLocalsAreCleared();
  }

  @SuppressWarnings("unchecked")
  private static void assertThatTreadLocalsAreCleared() throws Exception {
    final Field depthField = EmptyStepsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    depthField.setAccessible(true);
    assertThat(((ThreadLocal<Integer>) depthField.get(null)).get())
      .isNull();
  }
}
