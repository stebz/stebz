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
 * The {@link java.util.function.Consumer} specialization with
 * {@code [Object,Object,Object,Object,Object,Object,Object,Object->void]} signature that might throw an exception.
 *
 * @param <T1> the type of the first input argument
 * @param <T2> the type of the second input argument
 * @param <T3> the type of the third input argument
 * @param <T4> the type of the fourth input argument
 * @param <T5> the type of the fifth input argument
 * @param <T6> the type of the sixth input argument
 * @param <T7> the type of the seventh input argument
 * @param <T8> the type of the eighth input argument
 * @param <E>  the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, E extends Throwable> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the first input argument
   * @param t2 the second input argument
   * @param t3 the third input argument
   * @param t4 the fourth input argument
   * @param t5 the fifth input argument
   * @param t6 the sixth input argument
   * @param t7 the seventh input argument
   * @param t8 the eighth input argument
   * @throws E if consumer threw exception
   */
  void accept(T1 t1,
              T2 t2,
              T3 t3,
              T4 t4,
              T5 t5,
              T6 t6,
              T7 t7,
              T8 t8) throws E;

  /**
   * Returns given consumer.
   *
   * @param consumer the consumer
   * @param <T1>     the type of the first input argument
   * @param <T2>     the type of the second input argument
   * @param <T3>     the type of the third input argument
   * @param <T4>     the type of the fourth input argument
   * @param <T5>     the type of the fifth input argument
   * @param <T6>     the type of the sixth input argument
   * @param <T7>     the type of the seventh input argument
   * @param <T8>     the type of the eighth input argument
   * @param <E>      the type of the throwing exception
   * @return unchecked consumer
   * @throws NullPointerException if {@code consumer} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T1, T2, T3, T4, T5, T6, T7, T8, E extends Throwable> ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, E> of(
    final ThrowingConsumer8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends E> consumer
  ) {
    if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
    return (ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, E>) consumer;
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
   * @param <T6>   the type of the sixth input argument
   * @param <T7>   the type of the seventh input argument
   * @param <T8>   the type of the eighth input argument
   * @return unchecked consumer
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <T1, T2, T3, T4, T5, T6, T7, T8> ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, Error> unchecked(
    final ThrowingConsumer8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ?> origin
  ) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, Error>) origin;
  }
}
