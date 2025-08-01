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
 * The {@link java.util.function.Consumer} specialization with {@code [Object,Object,Object,Object,Object->void]}
 * signature that might throw an exception.
 *
 * @param <T1> the type of the first input argument
 * @param <T2> the type of the second input argument
 * @param <T3> the type of the third input argument
 * @param <T4> the type of the fourth input argument
 * @param <T5> the type of the fifth input argument
 * @param <E>  the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingConsumer5<T1, T2, T3, T4, T5, E extends Throwable> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the first input argument
   * @param t2 the second input argument
   * @param t3 the third input argument
   * @param t4 the fourth input argument
   * @param t5 the fifth input argument
   * @throws E if consumer threw exception
   */
  void accept(T1 t1,
              T2 t2,
              T3 t3,
              T4 t4,
              T5 t5) throws E;

  /**
   * Returns given consumer.
   *
   * @param consumer the consumer
   * @param <T1>     the type of the first input argument
   * @param <T2>     the type of the second input argument
   * @param <T3>     the type of the third input argument
   * @param <T4>     the type of the fourth input argument
   * @param <T5>     the type of the fifth input argument
   * @param <E>      the type of the throwing exception
   * @return unchecked consumer
   * @throws NullPointerException if {@code consumer} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T1, T2, T3, T4, T5, E extends Throwable> ThrowingConsumer5<T1, T2, T3, T4, T5, E> of(
    final ThrowingConsumer5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends E> consumer
  ) {
    if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
    return (ThrowingConsumer5<T1, T2, T3, T4, T5, E>) consumer;
  }

  /**
   * Returns given consumer as an unchecked consumer.
   *
   * @param origin the origin consumer
   * @param <T1>   the type of the first input argument
   * @param <T2>   the type of the second input argument
   * @param <T3>   the type of the third input argument
   * @param <T4>   the type of the fourth input argument
   * @param <T5>   the type of the fifth input argument
   * @return unchecked consumer
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T1, T2, T3, T4, T5> ThrowingConsumer5<T1, T2, T3, T4, T5, Error> unchecked(
    final ThrowingConsumer5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ?> origin
  ) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingConsumer5<T1, T2, T3, T4, T5, Error>) origin;
  }
}
