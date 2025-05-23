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
 * The {@link java.util.function.Supplier} specialization that might throw an exception.
 *
 * @param <R> the type of the result
 * @param <E> the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingSupplier<R, E extends Throwable> {

  /**
   * Gets the result.
   *
   * @return result
   * @throws E if supplier threw exception
   */
  R get() throws E;

  /**
   * Returns given supplier.
   *
   * @param supplier the supplier
   * @param <R>      the type of the result
   * @param <E>      the type of the throwing exception
   * @return supplier
   * @throws NullPointerException if {@code supplier} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <R, E extends Throwable> ThrowingSupplier<R, E> of(final ThrowingSupplier<? extends R, ? extends E> supplier) {
    if (supplier == null) { throw new NullPointerException("supplier arg is null"); }
    return (ThrowingSupplier<R, E>) supplier;
  }

  /**
   * Returns given supplier as an unchecked supplier.
   *
   * @param origin the origin supplier
   * @param <R>    the type of the result
   * @return unchecked supplier
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <R> ThrowingSupplier<R, Error> unchecked(final ThrowingSupplier<? extends R, ?> origin) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingSupplier<R, Error>) origin;
  }

  /**
   * Returns supplier that stores the first result value.
   *
   * @param origin the origin supplier
   * @param <R>    the type of the result
   * @return supplier that stores the first result value
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  static <R> Caching<R> caching(final ThrowingSupplier<? extends R, ?> origin) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return new Caching<>(origin);
  }

  /**
   * Returns supplier that stores the first result value.
   *
   * @param lock   the lock
   * @param origin the origin supplier
   * @param <R>    the type of the result
   * @return supplier that stores the first result value
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  static <R> Caching<R> caching(final Object lock,
                                final ThrowingSupplier<? extends R, ?> origin) {
    if (lock == null) { throw new NullPointerException("lock arg is null"); }
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return new Caching<>(lock, origin);
  }

  /**
   * ThrowingSupplier that stores the first result value.
   *
   * @param <R> the type of the result
   */
  class Caching<R> implements ThrowingSupplier<R, Error> {
    private static final Object UNINITIALIZED_VALUE = new Object();
    private final Object lock;
    private volatile ThrowingSupplier<? extends R, ?> initializer;
    private volatile Object value;

    /**
     * Ctor.
     *
     * @param origin the origin supplier
     * @throws NullPointerException if {@code origin} arg is {@code null}
     */
    public Caching(final ThrowingSupplier<? extends R, ?> origin) {
      if (origin == null) { throw new NullPointerException("origin arg is null"); }
      this.lock = this;
      this.initializer = origin;
      this.value = UNINITIALIZED_VALUE;
    }

    /**
     * Ctor.
     *
     * @param lock   the lock
     * @param origin the origin supplier
     * @throws NullPointerException if {@code lock} or {@code origin} arg is {@code null}
     */
    public Caching(final Object lock,
                   final ThrowingSupplier<? extends R, ?> origin) {
      if (lock == null) { throw new NullPointerException("lock arg is null"); }
      if (origin == null) { throw new NullPointerException("origin arg is null"); }
      this.lock = lock;
      this.initializer = origin;
      this.value = UNINITIALIZED_VALUE;
    }

    /**
     * Returns {@code true} if a value has been already initialized, and {@code false} otherwise.
     *
     * @return {@code true} if a value has been already initialized, and {@code false} otherwise
     */
    public boolean isInitialized() {
      return this.value != UNINITIALIZED_VALUE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R get() {
      final Object v1 = this.value;
      if (v1 != UNINITIALIZED_VALUE) {
        return (R) v1;
      }
      synchronized (this.lock) {
        final Object v2 = this.value;
        if (v2 != UNINITIALIZED_VALUE) {
          return (R) v2;
        }
        final R newValue = ThrowingSupplier.unchecked(this.initializer).get();
        this.value = newValue;
        this.initializer = null;
        return newValue;
      }
    }
  }
}
