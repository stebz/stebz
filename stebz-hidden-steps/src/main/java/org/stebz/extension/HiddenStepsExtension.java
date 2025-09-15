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
package org.stebz.extension;

import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;
import org.stebz.util.property.PropertiesReader;

/**
 * Hidden steps {@link StebzExtension}.
 */
public class HiddenStepsExtension implements InterceptStep {
  private static final ThreadLocal<Integer> THREAD_LOCAL_DEPTH = new ThreadLocal<>();
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public HiddenStepsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public HiddenStepsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.hiddenSteps.enabled", true);
    this.order = properties.getInteger("stebz.extensions.hiddenSteps.order", DEFAULT_ORDER);
  }

  /**
   * Executes given block of steps with hiding.
   *
   * @param steps the block of steps for hiding
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static void hiddenSteps(final ThrowingRunnable<?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startHiddenSteps();
    try {
      ThrowingRunnable.unchecked(steps).run();
    } finally {
      finishHiddenSteps();
    }
  }

  /**
   * Executes given block of steps with hiding and returns result.
   *
   * @param steps the block of steps for hiding
   * @param <R>   the type of the result
   * @return result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <R> R hiddenSteps(final ThrowingSupplier<? extends R, ?> steps) {
    if (steps == null) { throw new NullPointerException("steps arg is null"); }
    startHiddenSteps();
    try {
      return ThrowingSupplier.unchecked(steps).get();
    } finally {
      finishHiddenSteps();
    }
  }

  /**
   * Executes given block of steps with hiding. Alias for {@link #hiddenSteps(ThrowingRunnable)} method.
   *
   * @param steps the block of steps for hiding
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static void hiddenArea(final ThrowingRunnable<?> steps) {
    hiddenSteps(steps);
  }

  /**
   * Executes given block of steps with hiding and returns result. Alias for {@link #hiddenSteps(ThrowingSupplier)}
   * method.
   *
   * @param steps the block of steps for hiding
   * @param <R>   the type of the result
   * @return result
   * @throws NullPointerException if {@code steps} arg is null
   */
  public static <R> R hiddenArea(final ThrowingSupplier<? extends R, ?> steps) {
    return hiddenSteps(steps);
  }

  private static void startHiddenSteps() {
    final Integer currentDepth = THREAD_LOCAL_DEPTH.get();
    if (currentDepth == null) {
      THREAD_LOCAL_DEPTH.set(0);
    } else {
      THREAD_LOCAL_DEPTH.set(currentDepth + 1);
    }
  }

  private static void finishHiddenSteps() {
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
    return this.enabled && THREAD_LOCAL_DEPTH.get() != null && !step.getHidden()
      ? step.withHidden(true)
      : step;
  }
}
