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
package org.stebz.core.step;

import java.util.NoSuchElementException;

/**
 * Context container step.
 *
 * @param <C> the type of the context
 * @param <B> the type of the contextual body
 */
public interface ContextContainerStep<C, B> {

  /**
   * Return {@code true} if there is a context present, otherwise {@code false}.
   *
   * @return {@code true} if there is a context present, otherwise {@code false}
   */
  boolean hasContext();

  /**
   * Return context.
   *
   * @return context
   * @throws NoSuchElementException if no context present
   */
  C getContext();

  /**
   * Returns contextual step body.
   *
   * @return contextual step body
   * @throws NoSuchElementException if no context present
   */
  B getContextualBody();
}
