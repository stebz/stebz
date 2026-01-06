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
package org.stebz.step;

import dev.jlet.function.ThrowingFunction;

/**
 * Executable step.
 *
 * @param <B> the type of the body
 * @param <S> the type of the step implementing {@code Step}
 */
public interface ExecutableStep<B, S extends ExecutableStep<B, S>> extends StepObj<S> {

  /**
   * Returns body.
   *
   * @return body
   */
  B body();

  /**
   * Returns {@code ExecutableStep} with given body.
   *
   * @param body the body
   * @return {@code ExecutableStep} with given attribute value
   * @throws NullPointerException if {@code body} arg is null
   */
  S withBody(B body);

  /**
   * Returns {@code ExecutableStep} with given body created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code ExecutableStep} with given body
   * @throws NullPointerException if {@code generator} arg is null
   */
  default S withBodyOf(final ThrowingFunction<? super B, ? extends B, ?> generator) {
    return this.withBody(ThrowingFunction.unchecked(generator).apply(this.body()));
  }
}
