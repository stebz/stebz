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

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.InterceptStep;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

/**
 * Empty steps {@link StebzExtension}: step bodies of steps executed within an {@code emptySteps} block are replaced
 * with empty bodies, so steps are reported to listeners, but not executed. When the {@code stebz.emptySteps.forEach}
 * property is enabled, every step is treated as an empty step regardless of the block.
 * <p>The {@code stebz.emptySteps.enabled} property enables or disables this extension (default: {@code true}). The
 * {@code stebz.emptySteps.order} property sets its execution order (default: {@code LATE_ORDER}). The
 * {@code stebz.emptySteps.forEach} property enables emptying of all steps (default: {@code false}).
 */
public class EmptyStepsExtension implements InterceptStep {
  private static final ThreadLocal<Integer> THREAD_LOCAL_DEPTH = new ThreadLocal<>();
  private final boolean enabled;
  private final int order;
  private final boolean forEach;

  /**
   * Creates the extension using the startup properties.
   */
  public EmptyStepsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Creates the extension using the specified properties.
   *
   * @param properties the properties reader
   */
  public EmptyStepsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.emptySteps.enabled", true);
    this.order = properties.getInteger("stebz.emptySteps.order", LATE_ORDER);
    this.forEach = properties.getBoolean("stebz.emptySteps.forEach", false);
  }

  /**
   * Executes the given block of steps as empty steps: each step executed within the block is reported to listeners, but
   * its body is not executed.
   *
   * @param steps the block of steps
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static void emptySteps(final ThrowingRunnable<?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startEmptySteps();
    try {
      ThrowingRunnable.unchecked(steps).run();
    } finally {
      finishEmptySteps();
    }
  }

  /**
   * Executes the given block of steps as empty steps: each step executed within the block is reported to listeners, but
   * its body is not executed.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <V> void emptySteps(final V value,
                                    final ThrowingConsumer<? super V, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startEmptySteps();
    try {
      ThrowingConsumer.unchecked(steps).accept(value);
    } finally {
      finishEmptySteps();
    }
  }

  /**
   * Executes the given block of steps as empty steps and returns the execution result: each step executed within the
   * block is reported to listeners, but its body is not executed.
   *
   * @param steps the block of steps
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <R> R emptyStepsResult(final ThrowingSupplier<? extends R, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startEmptySteps();
    try {
      return ThrowingSupplier.unchecked(steps).get();
    } finally {
      finishEmptySteps();
    }
  }

  /**
   * Executes the given block of steps as empty steps and returns the execution result: each step executed within the
   * block is reported to listeners, but its body is not executed.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <V, R> R emptyStepsResult(final V value,
                                          final ThrowingFunction<? super V, ? extends R, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startEmptySteps();
    try {
      return ThrowingFunction.unchecked(steps).apply(value);
    } finally {
      finishEmptySteps();
    }
  }

  /**
   * Executes the given block of steps as empty steps: each step executed within the block is reported to listeners, but
   * its body is not executed. Alias for {@link #emptySteps(ThrowingRunnable)}.
   *
   * @param steps the block of steps
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static void EmptySteps(final ThrowingRunnable<?> steps) {
    emptySteps(steps);
  }

  /**
   * Executes the given block of steps as empty steps: each step executed within the block is reported to listeners, but
   * its body is not executed. Alias for {@link #emptySteps(Object, ThrowingConsumer)}.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <V> void EmptySteps(final V value,
                                    final ThrowingConsumer<? super V, ?> steps) {
    emptySteps(value, steps);
  }

  /**
   * Executes the given block of steps as empty steps and returns the execution result: each step executed within the
   * block is reported to listeners, but its body is not executed. Alias for
   * {@link #emptyStepsResult(ThrowingSupplier)}.
   *
   * @param steps the block of steps
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <R> R EmptyStepsResult(final ThrowingSupplier<? extends R, ?> steps) {
    return emptyStepsResult(steps);
  }

  /**
   * Executes the given block of steps as empty steps and returns the execution result: each step executed within the
   * block is reported to listeners, but its body is not executed. Alias for
   * {@link #emptyStepsResult(Object, ThrowingFunction)}.
   *
   * @param value the additional value
   * @param steps the block of steps
   * @param <V>   the type of the additional value
   * @param <R>   the type of the result
   * @return execution result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <V, R> R EmptyStepsResult(final V value,
                                          final ThrowingFunction<? super V, ? extends R, ?> steps) {
    return emptyStepsResult(value, steps);
  }

  private static void startEmptySteps() {
    final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
    if (currentDepth == null) {
      THREAD_LOCAL_DEPTH.set(0);
    } else {
      THREAD_LOCAL_DEPTH.set(currentDepth + 1);
    }
  }

  private static void finishEmptySteps() {
    final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
    if (currentDepth == 0) {
      THREAD_LOCAL_DEPTH.remove();
    } else {
      THREAD_LOCAL_DEPTH.set(currentDepth - 1);
    }
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    if (this.enabled && (this.forEach || THREAD_LOCAL_DEPTH.get() != null)) {
      if (step instanceof RunnableStep) {
        return ((RunnableStep) step).withBody(RunnableStep.emptyBody());
      } else if (step instanceof ConsumerStep) {
        return ((ConsumerStep<?>) step).withBody(ConsumerStep.emptyBody());
      } else if (step instanceof SupplierStep) {
        return ((SupplierStep<?>) step).withBody(SupplierStep.emptyBody());
      } else if (step instanceof FunctionStep) {
        return ((FunctionStep<?, ?>) step).withBody(FunctionStep.emptyBody());
      }
    }
    return step;
  }
}
