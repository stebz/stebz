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
package org.stebz.util.container;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;

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
   * @return {@code true} if there is no value present, otherwise {@code false}
   */
  boolean isEmpty();

  /**
   * Returns the value.
   *
   * @return value
   * @throws NoSuchElementException if no value present
   */
  V get();

  /**
   * If a value is present, invokes the specified consumer with the value, otherwise does nothing.
   *
   * @param consumer the consumer
   * @return this object
   * @throws NullPointerException if {@code consumer} arg is null
   */
  NullableValueContainer<V> ifPresent(ThrowingConsumer<? super V, ?> consumer);

  /**
   * If a value is not present, invokes the specified runnable, otherwise does nothing.
   *
   * @param runnable the runnable
   * @return this object
   * @throws NullPointerException if {@code runnable} arg is null
   */
  NullableValueContainer<V> ifEmpty(ThrowingRunnable<?> runnable);

  /**
   * If a value is present, returns result of the {@code ifPresent} function, otherwise returns result of
   * {@code ifEmpty} supplier.
   *
   * @param ifPresent the function
   * @param ifEmpty   the supplier
   * @param <R>       the type of the result
   * @return result of the {@code ifPresent} function, otherwise result of {@code ifEmpty} supplier
   * @throws NullPointerException if {@code ifPresent} arg or {@code ifEmpty} arg is null
   */
  <R> R fold(ThrowingFunction<? super V, ? extends R, ?> ifPresent,
             ThrowingSupplier<? extends R, ?> ifEmpty);

  /**
   * Returns the value if present, otherwise returns {@code other} value.
   *
   * @param other the other value
   * @return value if present, otherwise {@code other} value
   */
  V orElse(V other);

  /**
   * Returns the value if present, otherwise invokes {@code other} and returns the result of that invocation.
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
   * @throws NoSuchElementException if no value is present
   */
  V orElseThrow();

  /**
   * If a value is present, returns the value, otherwise throws the exception produced by the provided supplier (the
   * exception is rethrown as is, no wrapping is performed).
   *
   * @param exceptionSupplier the exception supplier
   * @return value
   * @throws NullPointerException if {@code exceptionSupplier} arg is null
   */
  V orElseThrow(ThrowingSupplier<? extends Throwable, ?> exceptionSupplier);
}
