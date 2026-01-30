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

import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;

/**
 * Intercept step exception extension.
 *
 * @see StebzExtension
 */
public interface InterceptStepException extends StebzExtension {

  /**
   * Intercepts step exception.
   *
   * @param step      the step
   * @param context   the step context
   * @param exception the step exception
   * @return the new step exception value
   */
  default Throwable interceptStepException(final StepObj<?> step,
                                           final NullableOptional<Object> context,
                                           final Throwable exception) {
    return exception;
  }

  /**
   * Returns a {@code boolean} value indicating whether this exception is hidden for listeners.
   *
   * @param step         the step
   * @param context      the step context
   * @param exception    the step exception
   * @param currentState the current state
   * @return {@code boolean} value indicating whether this exception is hidden for listeners
   */
  default boolean hiddenStepException(final StepObj<?> step,
                                      final NullableOptional<Object> context,
                                      final Throwable exception,
                                      final boolean currentState) {
    return currentState;
  }

  /**
   * Returns a {@code boolean} value indicating whether this exception should be thrown.
   *
   * @param step         the step
   * @param context      the step context
   * @param exception    the step exception
   * @param currentState the current state
   * @return {@code boolean} value indicating whether this exception should be thrown
   */
  default boolean thrownStepException(final StepObj<?> step,
                                      final NullableOptional<Object> context,
                                      final Throwable exception,
                                      final boolean currentState) {
    return currentState;
  }
}
