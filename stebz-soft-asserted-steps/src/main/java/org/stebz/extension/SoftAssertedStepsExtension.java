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

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.opentest4j.MultipleFailuresError;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Soft assertions {@link StebzExtension}.
 */
public class SoftAssertedStepsExtension implements InterceptStepException {
  private static final ThreadLocal<Integer> THREAD_LOCAL_DEPTH = new ThreadLocal<>();
  private static final ThreadLocal<Map<Integer, List<Throwable>>> THREAD_LOCAL_EXCEPTIONS = new ThreadLocal<>();
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public SoftAssertedStepsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor
   *
   * @param properties the properties reader
   */
  public SoftAssertedStepsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.softAssertedSteps.enabled", true);
    this.order = properties.getInteger("stebz.extensions.softAssertedSteps.order", LATE_ORDER);
  }

  /**
   * Executes given block of steps with soft assertions.
   *
   * @param steps the block of steps for soft assertions
   * @throws NullPointerException if {@code steps} arg is null
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
   * Executes given block of steps with soft assertions.
   *
   * @param value the additional value
   * @param steps the block of steps for soft assertions
   * @param <V>   the type of the additional value
   * @throws NullPointerException if {@code steps} arg is null
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
   * Executes given block of steps with soft assertions and returns execution result.
   *
   * @param steps the block of steps for soft assertions
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
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
   * Executes given block of steps with soft assertions and returns execution result.
   *
   * @param value the additional value
   * @param steps the block of steps for soft assertions
   * @param <V>   the type of the additional value
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
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
      if (currentExceptions != null) {
        if (blockException != null) {
          currentExceptions.add(blockException);
        }
        if (!currentExceptions.isEmpty()) {
          final MultipleFailuresError error = new MultipleFailuresError(null, currentExceptions);
          currentExceptions.forEach(error::addSuppressed);
          throw error;
        }
      }
    }
    if (blockException != null) {
      final List<Throwable> currentExceptions = new ArrayList<>(1);
      currentExceptions.add(blockException);
      final MultipleFailuresError error = new MultipleFailuresError(null, currentExceptions);
      error.addSuppressed(blockException);
      throw error;
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
