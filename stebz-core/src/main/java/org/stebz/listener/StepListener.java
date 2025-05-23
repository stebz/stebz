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

import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

/**
 * Step listener.
 *
 * @see org.stebz.extension.StebzExtension
 */
public interface StepListener {
  /**
   * Default listener order.
   */
  int DEFAULT_ORDER = 10000;

  /**
   * Configures this listener using the given {@code PropertiesReader}. Called once during the Stebz startup.
   *
   * @param properties the startup properties reader
   */
  default void configure(final PropertiesReader properties) {
  }

  /**
   * Returns this listener order.
   *
   * @return this listener order
   */
  default int order() {
    return DEFAULT_ORDER;
  }

  /**
   * Calling on step start.
   *
   * @param step    the step
   * @param context the step context
   */
  void onStepStart(StepObj<?> step,
                   NullableOptional<Object> context);

  /**
   * Calling on step success.
   *
   * @param step    the step
   * @param context the step context
   * @param result  the step result
   */
  void onStepSuccess(StepObj<?> step,
                     NullableOptional<Object> context,
                     NullableOptional<Object> result);

  /**
   * Calling on step failure.
   *
   * @param step      the step
   * @param context   the step context
   * @param exception the step exception
   */
  void onStepFailure(StepObj<?> step,
                     NullableOptional<Object> context,
                     Throwable exception);
}
