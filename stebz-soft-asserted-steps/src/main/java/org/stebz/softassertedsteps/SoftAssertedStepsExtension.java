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
import org.opentest4j.MultipleFailuresError;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.InterceptStepException;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.stebz.util.Throw.unchecked;

/**
 * Soft asserted steps {@link StebzExtension}: exceptions thrown by steps executed within a {@code softAssertedSteps}
 * block are collected instead of failing the block immediately, and at the end of the block all collected exceptions
 * are reported together as a single {@link MultipleFailuresError}.
 * <p>The {@code stebz.softAssertedSteps.enabled} property enables or disables this extension (default: {@code true}).
 * The {@code stebz.softAssertedSteps.order} property sets its execution order (default: {@code LATE_ORDER}).
 */
public class SoftAssertedStepsExtension implements InterceptStepException {
  private static final ThreadLocal<Integer> THREAD_LOCAL_DEPTH = new ThreadLocal<>();
  private static final ThreadLocal<Map<Integer, List<Throwable>>> THREAD_LOCAL_EXCEPTIONS = new ThreadLocal<>();
  private static final ThreadLocal<MultipleFailuresError> LAST_ASSERTION_ERROR = new ThreadLocal<>();
  private final boolean enabled;
  private final int order;

  /**
   * Creates the extension using the startup properties.
   */
  public SoftAssertedStepsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Creates the extension using the specified properties.
   *
   * @param properties the properties reader
   */
  public SoftAssertedStepsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.softAssertedSteps.enabled", true);
    this.order = properties.getInteger("stebz.softAssertedSteps.order", LATE_ORDER);
  }

  /**
   * Executes the given block of steps with soft assertions: exceptions thrown by steps executed within the block are
   * collected and reported together as a single {@link MultipleFailuresError} at the end of the block.
   *
   * @param steps the block of steps
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static void softAssertedSteps(final ThrowingRunnable<?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startSoftAssertions();
    Throwable blockException = null;
    try {
      steps.run();
    } catch (final Throwable ex) {
      blockException = ex;
    } finally {
      finishSoftAssertions(blockException);
    }
  }

  /**
   * Executes the given block of steps with soft assertions: exceptions thrown by steps executed within the block are
   * collected and reported together as a single {@link MultipleFailuresError} at the end of the block.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <V> void softAssertedSteps(final V value,
                                           final ThrowingConsumer<? super V, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startSoftAssertions();
    Throwable blockException = null;
    try {
      steps.accept(value);
    } catch (final Throwable ex) {
      blockException = ex;
    } finally {
      finishSoftAssertions(blockException);
    }
  }

  /**
   * Executes the given block of steps with soft assertions and returns the execution result: exceptions thrown by steps
   * executed within the block are collected and reported together as a single {@link MultipleFailuresError} at the end
   * of the block.
   *
   * @param steps the block of steps
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <R> R softAssertedStepsResult(final ThrowingSupplier<? extends R, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startSoftAssertions();
    Throwable blockException = null;
    try {
      return steps.get();
    } catch (final Throwable ex) {
      blockException = ex;
    } finally {
      finishSoftAssertions(blockException);
    }
    return null; /* unreachable */
  }

  /**
   * Executes the given block of steps with soft assertions and returns the execution result: exceptions thrown by steps
   * executed within the block are collected and reported together as a single {@link MultipleFailuresError} at the end
   * of the block.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <V, R> R softAssertedStepsResult(final V value,
                                                 final ThrowingFunction<? super V, ? extends R, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startSoftAssertions();
    Throwable blockException = null;
    try {
      return steps.apply(value);
    } catch (final Throwable ex) {
      blockException = ex;
    } finally {
      finishSoftAssertions(blockException);
    }
    return null; /* unreachable */
  }

  /**
   * Executes the given block of steps with soft assertions: exceptions thrown by steps executed within the block are
   * collected and reported together as a single {@link MultipleFailuresError} at the end of the block. Alias for
   * {@link #softAssertedSteps(ThrowingRunnable)}.
   *
   * @param steps the block of steps
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static void SoftAssertedSteps(final ThrowingRunnable<?> steps) {
    softAssertedSteps(steps);
  }

  /**
   * Executes the given block of steps with soft assertions: exceptions thrown by steps executed within the block are
   * collected and reported together as a single {@link MultipleFailuresError} at the end of the block. Alias for
   * {@link #softAssertedSteps(Object, ThrowingConsumer)}.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <V> void SoftAssertedSteps(final V value,
                                           final ThrowingConsumer<? super V, ?> steps) {
    softAssertedSteps(value, steps);
  }

  /**
   * Executes the given block of steps with soft assertions and returns the execution result: exceptions thrown by steps
   * executed within the block are collected and reported together as a single {@link MultipleFailuresError} at the end
   * of the block. Alias for {@link #softAssertedStepsResult(ThrowingSupplier)}.
   *
   * @param steps the block of steps
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <R> R SoftAssertedStepsResult(final ThrowingSupplier<? extends R, ?> steps) {
    return softAssertedStepsResult(steps);
  }

  /**
   * Executes the given block of steps with soft assertions and returns the execution result: exceptions thrown by steps
   * executed within the block are collected and reported together as a single {@link MultipleFailuresError} at the end
   * of the block. Alias for {@link #softAssertedStepsResult(Object, ThrowingFunction)}.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException  if {@code steps} arg is null
   * @throws MultipleFailuresError if one or more steps executed in the block threw an exception
   */
  public static <V, R> R SoftAssertedStepsResult(final V value,
                                                 final ThrowingFunction<? super V, ? extends R, ?> steps) {
    return softAssertedStepsResult(value, steps);
  }

  private static void startSoftAssertions() {
    final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
    if (currentDepth == null) {
      THREAD_LOCAL_DEPTH.set(0);
    } else {
      THREAD_LOCAL_DEPTH.set(currentDepth + 1);
    }
  }

  private static void finishSoftAssertions(final Throwable blockException) {
    final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
    if (currentDepth == 0) {
      THREAD_LOCAL_DEPTH.remove();
    } else {
      THREAD_LOCAL_DEPTH.set(currentDepth - 1);
    }

    final Map<Integer, List<Throwable>> exceptions = THREAD_LOCAL_EXCEPTIONS.get();
    if (exceptions != null) {
      final List<Throwable> currentExceptions = exceptions.remove(currentDepth);
      if (exceptions.isEmpty()) {
        THREAD_LOCAL_EXCEPTIONS.remove();
      }
      if (currentExceptions != null && !currentExceptions.isEmpty()) {
        if (blockException != null) {
          if (blockException != LAST_ASSERTION_ERROR.get()) {
            LAST_ASSERTION_ERROR.remove();
            currentExceptions.forEach(blockException::addSuppressed);
            throw unchecked(blockException);
          }
          currentExceptions.add(blockException);
        }
        final MultipleFailuresError error = new MultipleFailuresError(null, currentExceptions);
        currentExceptions.forEach(error::addSuppressed);
        if (currentDepth == 0) {
          LAST_ASSERTION_ERROR.remove();
        } else {
          LAST_ASSERTION_ERROR.set(error);
        }
        throw error;
      }
    }
    if (blockException != null) {
      LAST_ASSERTION_ERROR.remove();
      throw unchecked(blockException);
    }
    if (currentDepth == 0) {
      LAST_ASSERTION_ERROR.remove();
    }
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public boolean thrownStepException(final StepObj<?> step,
                                     final NullableOptional<Object> context,
                                     final Throwable exception,
                                     final boolean currentState) {
    if (this.enabled) {
      final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
      if (currentDepth != null) {
        Map<Integer, List<Throwable>> allExceptions = THREAD_LOCAL_EXCEPTIONS.get();
        List<Throwable> currentExceptions;
        if (allExceptions == null) {
          currentExceptions = new ArrayList<>();
          allExceptions = new HashMap<>();
          allExceptions.put(currentDepth, currentExceptions);
          THREAD_LOCAL_EXCEPTIONS.set(allExceptions);
        } else {
          currentExceptions = allExceptions.get(currentDepth);
          if (currentExceptions == null) {
            currentExceptions = new ArrayList<>();
            allExceptions.put(currentDepth, currentExceptions);
          }
        }
        currentExceptions.add(exception);
        return false;
      }
    }
    return currentState;
  }
}
