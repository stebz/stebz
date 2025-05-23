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
package org.stebz.util.function;

/**
 * The {@link java.util.function.Function} specialization with {@code [Object->Object]} signature that might throw an
 * exception.
 *
 * @param <T> the type of the input argument
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

  /**
   * Applies this function to the given argument.
   *
   * @param t the input argument
   * @return result
   * @throws E if function threw exception
   */
  R apply(T t) throws E;

  /**
   * Returns given function.
   *
   * @param function the function
   * @param <T>      the type of the input argument
   * @param <R>      the type of the result
   * @param <E>      the type of the throwing exception
   * @return function
   * @throws NullPointerException if {@code function} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T, R, E extends Throwable> ThrowingFunction<T, R, RuntimeException> of(
    final ThrowingFunction<? super T, ? extends R, ? extends E> function
  ) {
    if (function == null) { throw new NullPointerException("function arg is null"); }
    return (ThrowingFunction<T, R, RuntimeException>) function;
  }

  /**
   * Returns given function as an unchecked function.
   *
   * @param origin the origin function
   * @param <T>    the type of the input argument
   * @param <R>    the type of the result
   * @return unchecked function
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T, R> ThrowingFunction<T, R, Error> unchecked(final ThrowingFunction<? super T, ? extends R, ?> origin) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingFunction<T, R, Error>) origin;
  }
}
