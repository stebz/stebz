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
import static org.stebz.extension.SoftAssertionsExtension.softStepAssertions;

/**
 * Tests for {@link SoftAssertionsExtension}.
 */
final class SoftAssertionsExtensionTest {

  @Test
  void interceptStepExceptionMethodShouldReturnGivenException() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception))
      .isSameAs(exception);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void hiddenStepExceptionMethodShouldReturnGivenState() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, true))
      .isTrue();
    assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception, false))
      .isFalse();
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void thrownStepExceptionMethodShouldReturnGivenState() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception = new Throwable();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception, true))
      .isTrue();
    assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception, false))
      .isFalse();
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodShouldWorkCorrectlyWithoutExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");

    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softStepAssertions((ThrowingRunnable<?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception3, true))
          .isTrue();
      })
    ).doesNotThrowAnyException();
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodShouldCatchceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Throwable exception5 = new Throwable("5");

    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softStepAssertions((ThrowingRunnable<?>) () -> {
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
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodShouldCatchNestedExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");

    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    Throwable mainException = null;
    try {
      softStepAssertions((ThrowingRunnable<?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        softStepAssertions(() -> {
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
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodShouldCatchNonStepExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");

    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softStepAssertions((ThrowingRunnable<?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        throw exception2;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodShouldCatchOnlyNonStepExceptions() throws Exception {
    final Throwable exception = new Throwable();

    assertThatCode(() ->
      softStepAssertions((ThrowingRunnable<?>) () -> {
        throw exception;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldWorkCorrectlyWithoutExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Object result = new Object();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    assertThat(
      softStepAssertions((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.interceptStepException(step, NullableOptional.empty(), exception1))
          .isSameAs(exception1);
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        assertThat(extension.hiddenStepException(step, NullableOptional.empty(), exception3, true))
          .isTrue();
        return result;
      })
    ).isSameAs(result);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Throwable exception5 = new Throwable("5");
    final Object result = new Object();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    assertThatCode(() ->
      softStepAssertions((ThrowingSupplier<Object, ?>) () -> {
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
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchNestedExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");
    final Throwable exception3 = new Throwable("3");
    final Throwable exception4 = new Throwable("4");
    final Object result = new Object();
    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));

    Throwable mainException = null;
    try {
      softStepAssertions((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception2, false))
          .isFalse();
        return softStepAssertions(() -> {
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
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchNonStepExceptions() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final Throwable exception1 = new Throwable("1");
    final Throwable exception2 = new Throwable("2");

    final SoftAssertionsExtension extension = new SoftAssertionsExtension(new PropertiesReader.Of(new Properties()));
    assertThatCode(() ->
      softStepAssertions((ThrowingSupplier<Object, ?>) () -> {
        assertThat(extension.thrownStepException(step, NullableOptional.empty(), exception1, false))
          .isFalse();
        throw exception2;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception1)
      .hasSuppressedException(exception2);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @Test
  void softStepAssertionsMethodWithResultShouldCatchOnlyNonStepExceptions() throws Exception {
    final Throwable exception = new Throwable();

    assertThatCode(() ->
      softStepAssertions((ThrowingSupplier<Object, ?>) () -> {
        throw exception;
      })
    ).isInstanceOf(MultipleFailuresError.class)
      .hasSuppressedException(exception);
    assertThat(threadLocalDepth())
      .isNull();
    assertThat(threadLocalExceptions())
      .isNull();
  }

  @SuppressWarnings("unchecked")
  private static Integer threadLocalDepth() throws Exception {
    final Field field = SoftAssertionsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    field.setAccessible(true);
    return ((ThreadLocal<Integer>) field.get(null)).get();
  }

  @SuppressWarnings("unchecked")
  private static Map<Integer, List<Throwable>> threadLocalExceptions() throws Exception {
    final Field field = SoftAssertionsExtension.class.getDeclaredField("THREAD_LOCAL_EXCEPTIONS");
    field.setAccessible(true);
    return ((ThreadLocal<Map<Integer, List<Throwable>>>) field.get(null)).get();
  }
}
