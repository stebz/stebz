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
package org.stebz.softassertedsteps;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.stebz.softassertedsteps.SoftAssertedStepsExtension.SoftAssertedSteps;
import static org.stebz.softassertedsteps.SoftAssertedStepsExtension.SoftAssertedStepsResult;
import static org.stebz.softassertedsteps.SoftAssertedStepsExtension.softAssertedSteps;
import static org.stebz.softassertedsteps.SoftAssertedStepsExtension.softAssertedStepsResult;

/**
 * Tests for {@link SoftAssertedStepsExtension}.
 */
final class SoftAssertedStepsExtensionTest {

  private static SoftAssertedStepsExtension newExtension(final Map<String, String> properties) {
    return new SoftAssertedStepsExtension(new PropertiesReader.Of(properties));
  }

  private static SoftAssertedStepsExtension newExtensionWithProperty(final String name,
                                                                     final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static boolean thrownStepException(final SoftAssertedStepsExtension extension,
                                             final StepObj<?> step,
                                             final Throwable exception,
                                             final boolean currentState) {
    return extension.thrownStepException(step, NullableOptional.empty(), exception, currentState);
  }

  private static void assertThatThreadLocalsAreCleared() throws Exception {
    final Field depthField = SoftAssertedStepsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    depthField.setAccessible(true);
    assertThat(((ThreadLocal<Integer>) depthField.get(null)).get())
      .isNull();
    final Field exceptionsField =
      SoftAssertedStepsExtension.class.getDeclaredField("THREAD_LOCAL_EXCEPTIONS");
    exceptionsField.setAccessible(true);
    assertThat(((ThreadLocal<Map<Integer, ?>>) exceptionsField.get(null)).get())
      .isNull();
    final Field lastAssertionErrorField =
      SoftAssertedStepsExtension.class.getDeclaredField("LAST_ASSERTION_ERROR");
    lastAssertionErrorField.setAccessible(true);
    assertThat(((ThreadLocal<?>) lastAssertionErrorField.get(null)).get())
      .isNull();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.LATE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.softAssertedSteps.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void interceptStepExceptionShouldReturnGivenException() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception))
      .isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void thrownStepExceptionShouldReturnCurrentStateOutsideBlockWithoutCollecting() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThat(thrownStepException(extension, step, exception, true))
      .isTrue();
    assertThat(thrownStepException(extension, step, exception, false))
      .isFalse();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void thrownStepExceptionShouldReturnFalseAndCollectExceptionInsideBlock() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedSteps(() -> {
        assertThat(thrownStepException(extension, step, exception, false))
          .isFalse();
        assertThat(thrownStepException(extension, step, exception, true))
          .isFalse();
      })
    ).isInstanceOf(MultipleFailuresError.class);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void thrownStepExceptionShouldReturnCurrentStateInsideBlockWhenExtensionDisabled() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension =
      newExtensionWithProperty("stebz.softAssertedSteps.enabled", "false");

    softAssertedSteps(() -> {
      assertThat(thrownStepException(extension, step, exception, true))
        .isTrue();
      assertThat(thrownStepException(extension, step, exception, false))
        .isFalse();
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepExceptionShouldReturnCurrentState() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, true))
      .isTrue();
    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, false))
      .isFalse();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldExecuteBlockWithoutExceptions() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    assertThatCode(() -> softAssertedSteps(() -> executed.set(true)))
      .doesNotThrowAnyException();
    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    softAssertedSteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsResultShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(softAssertedStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsResultShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(softAssertedStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldReportCollectedStepExceptionsAsMultipleFailuresError() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new IllegalStateException("1");
    final Throwable exception2 = new IllegalStateException("2");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(thrownStepException(extension, step, exception1, false))
          .isFalse();
        assertThat(thrownStepException(extension, step, exception2, false))
          .isFalse();
      })
    ).isInstanceOfSatisfying(MultipleFailuresError.class, resultException -> {
      assertThat(resultException.getSuppressed())
        .containsExactly(exception1, exception2);
      assertThat(resultException.getFailures())
        .containsExactly(exception1, exception2);
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsResultShouldReportCollectedStepExceptionsAsMultipleFailuresError() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedStepsResult((ThrowingSupplier<Object, ?>) () -> {
        assertThat(thrownStepException(extension, step, exception, false))
          .isFalse();
        return new Object();
      })
    ).isInstanceOf(MultipleFailuresError.class);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldReportNestedBlockExceptionsInsideOuterError() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new IllegalStateException("1");
    final Throwable exception2 = new IllegalStateException("2");
    final Throwable exception3 = new IllegalStateException("3");
    final Throwable exception4 = new IllegalStateException("4");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(thrownStepException(extension, step, exception1, false))
          .isFalse();
        assertThat(thrownStepException(extension, step, exception2, false))
          .isFalse();
        softAssertedSteps(() -> {
          assertThat(thrownStepException(extension, step, exception3, false))
            .isFalse();
          assertThat(thrownStepException(extension, step, exception4, false))
            .isFalse();
        });
      })
    ).isInstanceOfSatisfying(MultipleFailuresError.class, resultException -> {
      final Throwable[] suppressed = resultException.getSuppressed();
      assertThat(suppressed)
        .hasSize(3)
        .contains(exception1, exception2);
      assertThat(suppressed[2])
        .isInstanceOf(MultipleFailuresError.class);
      assertThat(suppressed[2].getSuppressed())
        .containsExactly(exception3, exception4);
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldRethrowNonStepExceptionWithoutCollectedExceptionsAsIs() throws Exception {
    final Throwable exception = new IllegalStateException("non-step exception");

    assertThatThrownBy(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        throw exception;
      })
    ).isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldRethrowNonStepExceptionWithCollectedExceptionsAsSuppressed() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable stepException = new IllegalStateException("step exception");
    final Throwable blockException = new IllegalStateException("non-step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(thrownStepException(extension, step, stepException, false))
          .isFalse();
        throw blockException;
      })
    ).isSameAs(blockException)
      .hasSuppressedException(stepException);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsShouldRethrowNestedNonStepExceptionWithAllCollectedExceptionsAsSuppressed()
    throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new IllegalStateException("1");
    final Throwable exception2 = new IllegalStateException("2");
    final Throwable exception3 = new IllegalStateException("3");
    final Throwable nestedBlockException = new IllegalStateException("nested non-step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatThrownBy(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(thrownStepException(extension, step, exception1, false))
          .isFalse();
        assertThat(thrownStepException(extension, step, exception2, false))
          .isFalse();
        softAssertedSteps((ThrowingRunnable<?>) () -> {
          assertThat(thrownStepException(extension, step, exception3, false))
            .isFalse();
          throw nestedBlockException;
        });
      })
    ).isSameAs(nestedBlockException)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2)
      .hasSuppressedException(exception3);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void outerBlockShouldNotReportCollectedExceptionsIfNestedBlockErrorIsSwallowedByUser() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new IllegalStateException("step exception");
    final SoftAssertedStepsExtension extension = newExtension(new HashMap<>());

    assertThatCode(() ->
      softAssertedSteps(() -> {
        try {
          softAssertedSteps(() -> {
            assertThat(thrownStepException(extension, step, exception, false))
              .isFalse();
          });
        } catch (final MultipleFailuresError ignored) {
          /* nested error is handled by user code */
        }
      })
    ).doesNotThrowAnyException();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsAliasShouldExecuteBlockWithoutExceptions() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    assertThatCode(() -> SoftAssertedSteps(() -> executed.set(true)))
      .doesNotThrowAnyException();
    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsAliasShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    SoftAssertedSteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsResultAliasShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(SoftAssertedStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsResultAliasShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(SoftAssertedStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> softAssertedSteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> softAssertedSteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> softAssertedStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> softAssertedStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void softAssertedStepsAliasMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> SoftAssertedSteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SoftAssertedSteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SoftAssertedStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SoftAssertedStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }
}
