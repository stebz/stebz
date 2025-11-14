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

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.stebz.util.Cast;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static org.stebz.util.Throw.unchecked;

/**
 * A value container object whose value can be set once. Value may be {@code null}.
 *
 * @param <V> the type of the value
 */
public interface Val<V> extends NullableValueContainer<V> {

  /**
   * Sets given value.
   *
   * @param value the value to set
   * @return given value
   * @throws IllegalStateException if Val already has value
   */
  V set(final V value);

  /**
   * {@inheritDoc}
   */
  @Override
  Val<V> ifPresent(ThrowingConsumer<? super V, ?> consumer);

  /**
   * {@inheritDoc}
   */
  @Override
  Val<V> ifEmpty(ThrowingRunnable<?> runnable);

  /**
   * Indicates whether some other object is "equal to" this one. Implementations of the {@link Val} should have the same
   * contract.
   *
   * @return true if this object is the same as the {@code obj} argument, false otherwise
   * @see Of#equals(Object)
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns a hash code value for the object. Implementations of the {@link Val} should have the same contract.
   *
   * @return a hash code value for this object
   * @see Of#hashCode()
   */
  @Override
  int hashCode();

  /**
   * Returns empty {@code Val}.
   *
   * @param <V> the type of the value
   * @return empty {@code Val}
   */
  static <V> Val<V> empty() {
    return new Val.Of<>();
  }

  /**
   * Returns {@code Val} of given value.
   *
   * @param value the value
   * @param <V>   the type of the value
   * @return {@code Val} of given value
   */
  static <V> Val<V> of(final V value) {
    return new Val.Of<>(value);
  }

  /**
   * Default {@code Val} implementation.
   *
   * @param <V> the type of the value
   */
  class Of<V> implements Val<V> {
    private static final Object UNINITIALIZED_VALUE = new Object() {
      @Override
      public boolean equals(final Object obj) {
        return obj == this;
      }

      @Override
      public int hashCode() {
        return 0;
      }

      @Override
      public String toString() {
        return "<no value>";
      }
    };
    private static final AtomicReferenceFieldUpdater<Of<?>, Object> VALUE_FIELD_UPDATER =
      Cast.unsafe(AtomicReferenceFieldUpdater.newUpdater(Of.class, Object.class, "value"));
    private volatile Object value;

    /**
     * Ctor.
     */
    public Of() {
      this.value = UNINITIALIZED_VALUE;
    }

    /**
     * Ctor.
     *
     * @param value the value
     */
    public Of(final V value) {
      this.value = value;
    }

    @Override
    public boolean isEmpty() {
      return this.value == UNINITIALIZED_VALUE;
    }

    @Override
    public boolean isPresent() {
      return this.value != UNINITIALIZED_VALUE;
    }

    @Override
    public V set(final V value) {
      if (this.value != UNINITIALIZED_VALUE || !VALUE_FIELD_UPDATER.compareAndSet(this, UNINITIALIZED_VALUE, value)) {
        throw new IllegalStateException("Val already has value");
      }
      return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Val<V> ifPresent(final ThrowingConsumer<? super V, ?> consumer) {
      if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
      final Object v = this.value;
      if (v != UNINITIALIZED_VALUE) {
        ThrowingConsumer.unchecked(consumer).accept((V) v);
      }
      return this;
    }

    @Override
    public Val<V> ifEmpty(final ThrowingRunnable<?> runnable) {
      if (runnable == null) { throw new NullPointerException("runnable arg is null"); }
      if (this.value != UNINITIALIZED_VALUE) {
        ThrowingRunnable.unchecked(runnable).run();
      }
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R fold(final ThrowingFunction<? super V, ? extends R, ?> ifPresent,
                      final ThrowingSupplier<? extends R, ?> ifEmpty) {
      if (ifPresent == null) { throw new NullPointerException("ifPresent arg is null"); }
      if (ifEmpty == null) { throw new NullPointerException("ifEmpty arg is null"); }
      final Object v = this.value;
      return v == UNINITIALIZED_VALUE
        ? ThrowingSupplier.unchecked(ifEmpty).get()
        : ThrowingFunction.unchecked(ifPresent).apply((V) v);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get() {
      final Object v = this.value;
      if (v == UNINITIALIZED_VALUE) {
        throw new NoSuchElementException("Var has no value");
      }
      return (V) v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V orElse(final V other) {
      final Object v = this.value;
      return v == UNINITIALIZED_VALUE
        ? other
        : (V) v;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V orElseGet(final ThrowingSupplier<? extends V, ?> other) {
      if (other == null) { throw new NullPointerException("other arg is null"); }
      final Object v = this.value;
      return v == UNINITIALIZED_VALUE
        ? ThrowingSupplier.unchecked(other).get()
        : (V) v;
    }

    @Override
    public V orElseThrow() {
      return this.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V orElseThrow(final ThrowingSupplier<? extends Throwable, ?> exceptionSupplier) {
      if (exceptionSupplier == null) { throw new NullPointerException("exceptionSupplier arg is null"); }
      final Object v = this.value;
      if (v == UNINITIALIZED_VALUE) {
        throw unchecked(ThrowingSupplier.unchecked(exceptionSupplier).get());
      }
      return (V) v;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof Val) {
        final Val<?> other = (Val<?>) obj;
        return this.isPresent()
          ? other.isPresent() && Objects.equals(other.get(), this.value)
          : !other.isPresent();
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
      return "Val[" + this.value + "]";
    }
  }
}
