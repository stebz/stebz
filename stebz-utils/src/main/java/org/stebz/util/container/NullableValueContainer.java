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
package org.stebz.util.container;

import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.util.NoSuchElementException;

/**
 * Value container. Value may be {@code null}.
 *
 * @param <V> the type of the value
 */
public interface NullableValueContainer<V> {

  /**
   * Return {@code true} if there is a value present, otherwise {@code false}.
   *
   * @return {@code true} if there is a value present, otherwise {@code false}
   */
  boolean isPresent();

  /**
   * Return {@code true} if there is no value present, otherwise {@code false}.
   *
   * @return Returns {@code true} if there is no value present, otherwise {@code false}
   */
  boolean isEmpty();

  /**
   * Return value.
   *
   * @return value
   * @throws NoSuchElementException if no value present
   */
  V get();

  /**
   * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer the consumer
   * @return this object
   * @throws NullPointerException if {@code consumer} arg is null
   */
  NullableValueContainer<V> ifPresent(ThrowingConsumer<? super V, ?> consumer);

  /**
   * If a value is not present, invoke the specified runnable, otherwise do nothing.
   *
   * @param runnable the runnable
   * @return this object
   * @throws NullPointerException if {@code runnable} arg is null
   */
  NullableValueContainer<V> ifEmpty(ThrowingRunnable<?> runnable);

  /**
   * If a value is present, return result of the {@code ifPresent} function, otherwise return result of {@code ifEmpty}
   * supplier.
   *
   * @param ifPresent the function
   * @param ifEmpty   the supplier
   * @param <R>       the type of the result
   * @return result of the {@code ifPresent} function, otherwise result of {@code ifEmpty} supplier
   */
  <R> R fold(ThrowingFunction<? super V, ? extends R, ?> ifPresent,
             ThrowingSupplier<? extends R, ?> ifEmpty);

  /**
   * Return the value if present, otherwise returns {@code other} value.
   *
   * @param other the other value
   * @return value if present, otherwise {@code other} value
   */
  V orElse(V other);

  /**
   * Return the value if present, otherwise invoke {@code other} and return the result of that invocation.
   *
   * @param other the other value supplier
   * @return value if present otherwise the result of supplier
   * @throws NullPointerException if {@code other} arg is null
   */
  V orElseGet(ThrowingSupplier<? extends V, ?> other);

  /**
   * If a value is present, returns the value, otherwise throws {@code NoSuchElementException}.
   *
   * @return value
   */
  V orElseThrow();

  /**
   * If a value is present, returns the value, otherwise throws an exception to be created by the provided supplier.
   *
   * @param exceptionSupplier the exception supplier
   * @return value
   * @throws NullPointerException if {@code exceptionSupplier} arg is null
   */
  V orElseThrow(ThrowingSupplier<? extends Throwable, ?> exceptionSupplier);
}
