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
 * The {@link java.util.function.Consumer} specialization with {@code [Object->void]} signature that might throw an
 * exception.
 *
 * @param <T> the type of the input argument
 * @param <E> the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   * @throws E if consumer threw exception
   */
  void accept(T t) throws E;

  /**
   * Returns given consumer.
   *
   * @param consumer the consumer
   * @param <T>      the type of the input argument
   * @param <E>      the type of the throwing exception
   * @return unchecked consumer
   * @throws NullPointerException if {@code consumer} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T, E extends Throwable> ThrowingConsumer<T, E> of(final ThrowingConsumer<? super T, ? extends E> consumer) {
    if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
    return (ThrowingConsumer<T, E>) consumer;
  }

  /**
   * Returns given consumer as an unchecked consumer.
   *
   * @param origin the origin consumer
   * @param <T>    the type of the input argument
   * @return unchecked consumer
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T> ThrowingConsumer<T, Error> unchecked(final ThrowingConsumer<? super T, ?> origin) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingConsumer<T, Error>) origin;
  }
}
