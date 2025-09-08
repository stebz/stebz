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
package org.stebz.listener;

import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import static org.stebz.listener.SystemOutStepListener.threadLocalDisable;
import static org.stebz.listener.SystemOutStepListener.threadLocalEnable;

/**
 * Allure utils.
 */
public final class SystemOutUtils {

  /**
   * Utility class ctor.
   */
  private SystemOutUtils() {
  }

  /**
   * Executes given block of steps with hiding.
   *
   * @param steps the block of steps for hiding
   */
  public static void hidden(final ThrowingRunnable<?> steps) {
    threadLocalDisable();
    try {
      ThrowingRunnable.unchecked(steps).run();
    } finally {
      threadLocalEnable();
    }
  }

  /**
   * Executes given block of steps with hiding. Alias for {@link #hidden(ThrowingRunnable)} method.
   *
   * @param steps the block of steps for hiding
   */
  public static void hiddenSteps(final ThrowingRunnable<?> steps) {
    hidden(steps);
  }

  /**
   * Executes given block of steps with hiding and returns result.
   *
   * @param steps the block of steps for hiding
   * @param <R>   the type of the result
   * @return result
   */
  public static <R> R hidden(final ThrowingSupplier<? extends R, ?> steps) {
    threadLocalDisable();
    try {
      return ThrowingSupplier.unchecked(steps).get();
    } finally {
      threadLocalEnable();
    }
  }

  /**
   * Executes given block of steps with hiding and returns result. Alias for {@link #hidden(ThrowingSupplier)} method.
   *
   * @param steps the block of steps for hiding
   * @param <R>   the type of the result
   * @return result
   */
  public static <R> R hiddenSteps(final ThrowingSupplier<? extends R, ?> steps) {
    return hidden(steps);
  }
}
