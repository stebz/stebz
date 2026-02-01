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
package org.stebz.extension;

import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;
import org.stebz.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.stebz.extension.SoftAssertedStepsExtension.softAssertedSteps;

/**
 * Tests for {@link SoftAssertedStepsExtension}.
 */
final class SoftAssertedStepsExtensionTest {

  @Test
  void interceptStepExceptionMethodShouldReturnGivenException() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception))
      .isSameAs(exception);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void hiddenStepExceptionMethodShouldReturnGivenState() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, true))
      .isTrue();
    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, false))
      .isFalse();
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void thrownStepExceptionMethodShouldReturnGivenState() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception, true))
      .isTrue();
    assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception, false))
      .isFalse();
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodShouldWorkCorrectlyWithoutExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");

    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception3, true))
          .isTrue();
      })
    ).doesNotThrowAnyException();
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodShouldCatchceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Throwable exception5 = new Throwable("5");

    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception3, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception4, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception5, false))
          .isTrue();
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception2)
      .hasSuppressedException(exception3);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodShouldCatchNestedExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");

    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    Throwable mainException = null;
    try {
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        softAssertedSteps(() -> {
          assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception3, false))
            .isFalse();
          assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception4, false))
            .isFalse();
        });
      });
    } catch (final Throwable ex) {
      mainException = ex;
    }
    assertThat(mainException).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    final Throwable[] suppressed = mainException.getSuppressed();
    assertThat(suppressed)
      .hasSize(3);
    assertThat(suppressed[2])
      .isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception3)
      .hasSuppressedException(exception4);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softStepAssertionsMethodShouldCatchNonStepExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");

    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        throw exception2;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softStepAssertionsMethodShouldCatchOnlyNonStepExceptions() throws Exception {
    final Throwable exception = new Throwable();

    assertThatCode(() ->
      softAssertedSteps((ThrowingRunnable<?>) () -> {
        throw exception;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodWithResultShouldWorkCorrectlyWithoutExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Object result = new Object();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(
      softAssertedSteps((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception3, true))
          .isTrue();
        return result;
      })
    ).isSameAs(result);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodWithResultShouldCatchExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Throwable exception5 = new Throwable("5");
    final Object result = new Object();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    assertThatCode(() ->
      softAssertedSteps((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception3, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception4, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception5, false))
          .isTrue();
        return result;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception2)
      .hasSuppressedException(exception3);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softAssertedStepsMethodWithResultShouldCatchNestedExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Object result = new Object();
    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));

    Throwable mainException = null;
    try {
      softAssertedSteps((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        return softAssertedSteps(() -> {
          assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception3, false))
            .isFalse();
          assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception4, false))
            .isFalse();
          return result;
        });
      });
    } catch (final Throwable ex) {
      mainException = ex;
    }
    assertThat(mainException).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    final Throwable[] suppressed = mainException.getSuppressed();
    assertThat(suppressed)
      .hasSize(3);
    assertThat(suppressed[2])
      .isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception3)
      .hasSuppressedException(exception4);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchNonStepExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");

    final SoftAssertedStepsExtension extension =
      new SoftAssertedStepsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softAssertedSteps((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        throw exception2;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    assertThatTreadLocalsAreCleared();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchOnlyNonStepExceptions() throws Exception {
    final Throwable exception = new Throwable();

    assertThatCode(() ->
      softAssertedSteps((ThrowingSupplier<Object, ?>) () -> {
        throw exception;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception);
    assertThatTreadLocalsAreCleared();
  }

  @SuppressWarnings("unchecked")
  private static void assertThatTreadLocalsAreCleared() throws Exception {
    final Field depthField = SoftAssertedStepsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    depthField.setAccessible(true);
    assertThat(((ThreadLocal<Integer>) depthField.get(null)).get())
      .isNull();
    final Field exceptionsField = SoftAssertedStepsExtension.class.getDeclaredField("THREAD_LOCAL_EXCEPTIONS");
    exceptionsField.setAccessible(true);
    assertThat(((ThreadLocal<Map<Integer, List<Throwable>>>) depthField.get(null)).get())
      .isNull();
  }
}
