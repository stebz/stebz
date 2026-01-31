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
package org.stebz.context;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingConsumer2;
import dev.jlet.function.ThrowingConsumer3;
import dev.jlet.function.ThrowingConsumer4;
import dev.jlet.function.ThrowingConsumer5;
import dev.jlet.function.ThrowingConsumer6;
import dev.jlet.function.ThrowingConsumer7;
import dev.jlet.function.ThrowingConsumer8;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingFunction2;
import dev.jlet.function.ThrowingFunction3;
import dev.jlet.function.ThrowingFunction4;
import dev.jlet.function.ThrowingFunction5;
import dev.jlet.function.ThrowingFunction6;
import dev.jlet.function.ThrowingFunction7;
import dev.jlet.function.ThrowingFunction8;

/**
 * Step context utils.
 */
public final class Context {

  /**
   * Utility class ctor.
   */
  private Context() {
  }

  /**
   * Returns step context of 2 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @return step context of 2 values
   */
  public static <T1, T2> Of2<T1, T2> context(final T1 v1,
                                             final T2 v2) {
    return new Of2<>(v1, v2);
  }

  /**
   * Returns step context of 3 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @return step context of 3 values
   */
  public static <T1, T2, T3> Of3<T1, T2, T3> context(final T1 v1,
                                                     final T2 v2,
                                                     final T3 v3) {
    return new Of3<>(v1, v2, v3);
  }

  /**
   * Returns step context of 4 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param v4   the fourth value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @param <T4> the type of the fourth value
   * @return step context of 4 values
   */
  public static <T1, T2, T3, T4> Of4<T1, T2, T3, T4> context(final T1 v1,
                                                             final T2 v2,
                                                             final T3 v3,
                                                             final T4 v4) {
    return new Of4<>(v1, v2, v3, v4);
  }

  /**
   * Returns step context of 5 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param v4   the fourth value
   * @param v5   the fifth value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @param <T4> the type of the fourth value
   * @param <T5> the type of the fifth value
   * @return step context of 5 values
   */
  public static <T1, T2, T3, T4, T5> Of5<T1, T2, T3, T4, T5> context(final T1 v1,
                                                                     final T2 v2,
                                                                     final T3 v3,
                                                                     final T4 v4,
                                                                     final T5 v5) {
    return new Of5<>(v1, v2, v3, v4, v5);
  }

  /**
   * Returns step context of 6 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param v4   the fourth value
   * @param v5   the fifth value
   * @param v6   the sixth value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @param <T4> the type of the fourth value
   * @param <T5> the type of the fifth value
   * @param <T6> the type of the sixth value
   * @return step context of 6 values
   */
  public static <T1, T2, T3, T4, T5, T6> Of6<T1, T2, T3, T4, T5, T6> context(final T1 v1,
                                                                             final T2 v2,
                                                                             final T3 v3,
                                                                             final T4 v4,
                                                                             final T5 v5,
                                                                             final T6 v6) {
    return new Of6<>(v1, v2, v3, v4, v5, v6);
  }

  /**
   * Returns step context of 7 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param v4   the fourth value
   * @param v5   the fifth value
   * @param v6   the sixth value
   * @param v7   the seventh value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @param <T4> the type of the fourth value
   * @param <T5> the type of the fifth value
   * @param <T6> the type of the sixth value
   * @param <T7> the type of the seventh value
   * @return step context of 7 values
   */
  public static <T1, T2, T3, T4, T5, T6, T7> Of7<T1, T2, T3, T4, T5, T6, T7> context(final T1 v1,
                                                                                     final T2 v2,
                                                                                     final T3 v3,
                                                                                     final T4 v4,
                                                                                     final T5 v5,
                                                                                     final T6 v6,
                                                                                     final T7 v7) {
    return new Of7<>(v1, v2, v3, v4, v5, v6, v7);
  }

  /**
   * Returns step context of 8 values.
   *
   * @param v1   the first value
   * @param v2   the second value
   * @param v3   the third value
   * @param v4   the fourth value
   * @param v5   the fifth value
   * @param v6   the sixth value
   * @param v7   the seventh value
   * @param v8   the eighth value
   * @param <T1> the type of the first value
   * @param <T2> the type of the second value
   * @param <T3> the type of the third value
   * @param <T4> the type of the fourth value
   * @param <T5> the type of the fifth value
   * @param <T6> the type of the sixth value
   * @param <T7> the type of the seventh value
   * @param <T8> the type of the eighth value
   * @return step context of 8 values
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> Of8<T1, T2, T3, T4, T5, T6, T7, T8> context(final T1 v1,
                                                                                             final T2 v2,
                                                                                             final T3 v3,
                                                                                             final T4 v4,
                                                                                             final T5 v5,
                                                                                             final T6 v6,
                                                                                             final T7 v7,
                                                                                             final T8 v8) {
    return new Of8<>(v1, v2, v3, v4, v5, v6, v7, v8);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2> ThrowingConsumer<Of2<T1, T2>, Error> context(
    final ThrowingConsumer2<? super T1, ? super T2, ?> block
  ) {
    final ThrowingConsumer2<T1, T2, Error> unchecked = ThrowingConsumer2.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3> ThrowingConsumer<Of3<T1, T2, T3>, Error> context(
    final ThrowingConsumer3<? super T1, ? super T2, ? super T3, ?> block
  ) {
    final ThrowingConsumer3<T1, T2, T3, Error> unchecked = ThrowingConsumer3.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4> ThrowingConsumer<Of4<T1, T2, T3, T4>, Error> context(
    final ThrowingConsumer4<? super T1, ? super T2, ? super T3, ? super T4, ?> block
  ) {
    final ThrowingConsumer4<T1, T2, T3, T4, Error> unchecked = ThrowingConsumer4.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3, c.v4);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5> ThrowingConsumer<Of5<T1, T2, T3, T4, T5>, Error> context(
    final ThrowingConsumer5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ?> block
  ) {
    final ThrowingConsumer5<T1, T2, T3, T4, T5, Error> unchecked = ThrowingConsumer5.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3, c.v4, c.v5);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6> ThrowingConsumer<Of6<T1, T2, T3, T4, T5, T6>, Error> context(
    final ThrowingConsumer6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ?> block
  ) {
    final ThrowingConsumer6<T1, T2, T3, T4, T5, T6, Error> unchecked = ThrowingConsumer6.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @param <T7>  the type of the seventh block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6, T7> ThrowingConsumer<Of7<T1, T2, T3, T4, T5, T6, T7>, Error> context(
    final ThrowingConsumer7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ?> block
  ) {
    final ThrowingConsumer7<T1, T2, T3, T4, T5, T6, T7, Error> unchecked = ThrowingConsumer7.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6, c.v7);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @param <T7>  the type of the seventh block value
   * @param <T8>  the type of the eighth block value
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> ThrowingConsumer<Of8<T1, T2, T3, T4, T5, T6, T7, T8>, Error> context(
    final ThrowingConsumer8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ?> block
  ) {
    final ThrowingConsumer8<T1, T2, T3, T4, T5, T6, T7, T8, Error> unchecked = ThrowingConsumer8.unchecked(block);
    return c -> unchecked.accept(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6, c.v7, c.v8);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, R> ThrowingFunction<Of2<T1, T2>, R, Error> context(
    final ThrowingFunction2<? super T1, ? super T2, ? extends R, ?> block
  ) {
    final ThrowingFunction2<T1, T2, R, Error> unchecked = ThrowingFunction2.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, R> ThrowingFunction<Of3<T1, T2, T3>, R, Error> context(
    final ThrowingFunction3<? super T1, ? super T2, ? super T3, ? extends R, ?> block
  ) {
    final ThrowingFunction3<T1, T2, T3, R, Error> unchecked = ThrowingFunction3.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, R> ThrowingFunction<Of4<T1, T2, T3, T4>, R, Error> context(
    final ThrowingFunction4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R, ?> block
  ) {
    final ThrowingFunction4<T1, T2, T3, T4, R, Error> unchecked = ThrowingFunction4.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3, c.v4);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, R> ThrowingFunction<Of5<T1, T2, T3, T4, T5>, R, Error> context(
    final ThrowingFunction5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R, ?> block
  ) {
    final ThrowingFunction5<T1, T2, T3, T4, T5, R, Error> unchecked = ThrowingFunction5.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3, c.v4, c.v5);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6, R> ThrowingFunction<Of6<T1, T2, T3, T4, T5, T6>, R, Error> context(
    final ThrowingFunction6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R, ?> block
  ) {
    final ThrowingFunction6<T1, T2, T3, T4, T5, T6, R, Error> unchecked = ThrowingFunction6.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @param <T7>  the type of the seventh block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6, T7, R> ThrowingFunction<Of7<T1, T2, T3, T4, T5, T6, T7>, R, Error> context(
    final ThrowingFunction7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R, ?> block
  ) {
    final ThrowingFunction7<T1, T2, T3, T4, T5, T6, T7, R, Error> unchecked = ThrowingFunction7.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6, c.v7);
  }

  /**
   * Returns a tupled version of given block.
   *
   * @param block the block
   * @param <T1>  the type of the first block value
   * @param <T2>  the type of the second block value
   * @param <T3>  the type of the third block value
   * @param <T4>  the type of the fourth block value
   * @param <T5>  the type of the fifth block value
   * @param <T6>  the type of the sixth block value
   * @param <T7>  the type of the seventh block value
   * @param <T8>  the type of the eighth block value
   * @param <R>   the type of the block result
   * @return tupled version of given block
   * @throws NullPointerException if {@code block} arg is null
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> ThrowingFunction<Of8<T1, T2, T3, T4, T5, T6, T7, T8>, R, Error> context(
    final ThrowingFunction8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R, ?> block
  ) {
    final ThrowingFunction8<T1, T2, T3, T4, T5, T6, T7, T8, R, Error> unchecked = ThrowingFunction8.unchecked(block);
    return c -> unchecked.apply(c.v1, c.v2, c.v3, c.v4, c.v5, c.v6, c.v7, c.v8);
  }
}
