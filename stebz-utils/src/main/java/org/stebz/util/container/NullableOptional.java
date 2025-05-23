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
import org.stebz.util.function.ThrowingPredicate;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.util.NoSuchElementException;
import java.util.Objects;

import static org.stebz.util.Throw.unchecked;

/**
 * An immutable value container. Value may be {@code null}.
 *
 * @param <V> the type of the value
 */
public interface NullableOptional<V> extends NullableValueContainer<V> {

  /**
   * Return an {@code NullableOptional} describing the value if value is present and the value is not null, otherwise
   * return an empty {@code NullableOptional}.
   *
   * @return an {@code NullableOptional} describing the value if value is present and the value is not null, otherwise
   * an empty {@code NullableOptional}
   */
  NullableOptional<V> nonNull();

  /**
   * Return an {@code NullableOptional} describing the value if value is present and the value matches the given
   * predicate, otherwise return an empty {@code NullableOptional}.
   *
   * @param predicate the predicate
   * @return an {@code NullableOptional} describing the value if value is present and the value matches the given
   * predicate, otherwise an empty {@code NullableOptional}
   * @throws NullPointerException if {@code predicate} arg is null
   */
  NullableOptional<V> filter(final ThrowingPredicate<? super V, ?> predicate);

  /**
   * If a value is present, apply the provided mapping function to it and return an {@code NullableOptional} describing
   * the result, otherwise return an empty {@code NullableOptional}.
   *
   * @param mapper the mapper
   * @param <U>    the type of the result of {@code mapper}
   * @return an {@code NullableOptional} describing the result of mapping function if value is present, otherwise an
   * empty {@code NullableOptional}
   * @throws NullPointerException if {@code mapper} arg is null
   */
  <U> NullableOptional<U> map(ThrowingFunction<? super V, ? extends U, ?> mapper);

  /**
   * If a value is present, apply the provided {@code NullableOptional}-bearing mapping function to it, return that
   * result, otherwise return an empty {@code NullableOptional}.
   *
   * @param mapper the mapper
   * @param <U>    the type of the {@code NullableOptional} value of mapping function result
   * @return result of mapping function if value is present, otherwise an empty {@code NullableOptional}
   * @throws NullPointerException if {@code mapper} arg is null
   */
  <U> NullableOptional<U> flatMap(ThrowingFunction<? super V, ? extends NullableOptional<U>, ?> mapper);

  /**
   * {@inheritDoc}
   */
  @Override
  NullableOptional<V> ifPresent(ThrowingConsumer<? super V, ?> consumer);

  /**
   * {@inheritDoc}
   */
  @Override
  NullableOptional<V> ifEmpty(ThrowingRunnable<?> runnable);

  /**
   * Indicates whether some other object is "equal to" this one. Implementations of the {@link NullableOptional} should
   * have the same contract.
   *
   * @return true if this object is the same as the {@code obj} argument, false otherwise
   * @see Empty#equals(Object)
   * @see Of#equals(Object)
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns a hash code value for the object. Implementations of the {@link NullableOptional} should have the same
   * contract.
   *
   * @return a hash code value for this object
   * @see Empty#hashCode()
   * @see Of#hashCode()
   */
  @Override
  int hashCode();

  /**
   * Returns empty OptionalValue.
   *
   * @param <V> the type of the value
   * @return empty OptionalValue
   */
  @SuppressWarnings("unchecked")
  static <V> NullableOptional<V> empty() {
    return (NullableOptional<V>) Empty.INSTANCE;
  }

  /**
   * Returns OptionalValue of given value.
   *
   * @param value the value
   * @param <V>   the type of the value
   * @return OptionalValue of given value
   */
  static <V> NullableOptional<V> of(final V value) {
    return new Of<>(value);
  }

  /**
   * Default empty {@code OptionalValue} implementation.
   *
   * @param <V> the type of the value
   */
  class Empty<V> implements NullableOptional<V> {
    private static final NullableOptional<?> INSTANCE = new Empty<>();

    /**
     * Ctor.
     */
    public Empty() {
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isPresent() {
      return false;
    }

    @Override
    public V get() {
      throw new NoSuchElementException("No value present");
    }

    @Override
    public V orElse(final V other) {
      return other;
    }

    @Override
    public V orElseGet(final ThrowingSupplier<? extends V, ?> other) {
      if (other == null) { throw new NullPointerException("other arg is null"); }
      return ThrowingSupplier.unchecked(other).get();
    }

    @Override
    public V orElseThrow() {
      return this.get();
    }

    @Override
    public V orElseThrow(final ThrowingSupplier<? extends Throwable, ?> exceptionSupplier) {
      if (exceptionSupplier == null) { throw new NullPointerException("exceptionSupplier arg is null"); }
      throw unchecked(ThrowingSupplier.unchecked(exceptionSupplier).get());
    }

    @Override
    public NullableOptional<V> nonNull() {
      return this;
    }

    @Override
    public NullableOptional<V> filter(final ThrowingPredicate<? super V, ?> predicate) {
      if (predicate == null) { throw new NullPointerException("predicate arg is null"); }
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> NullableOptional<U> map(final ThrowingFunction<? super V, ? extends U, ?> mapper) {
      if (mapper == null) { throw new NullPointerException("mapper arg is null"); }
      return (NullableOptional<U>) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U> NullableOptional<U> flatMap(final ThrowingFunction<? super V, ? extends NullableOptional<U>, ?> mapper) {
      if (mapper == null) { throw new NullPointerException("mapper arg is null"); }
      return (NullableOptional<U>) this;
    }

    @Override
    public NullableOptional<V> ifPresent(final ThrowingConsumer<? super V, ?> consumer) {
      if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
      return this;
    }

    @Override
    public NullableOptional<V> ifEmpty(final ThrowingRunnable<?> runnable) {
      if (runnable == null) { throw new NullPointerException("runnable arg is null"); }
      ThrowingRunnable.unchecked(runnable).run();
      return this;
    }

    @Override
    public <R> R fold(final ThrowingFunction<? super V, ? extends R, ?> ifPresent,
                      final ThrowingSupplier<? extends R, ?> ifEmpty) {
      if (ifPresent == null) { throw new NullPointerException("ifPresent arg is null"); }
      if (ifEmpty == null) { throw new NullPointerException("ifEmpty arg is null"); }
      return ThrowingSupplier.unchecked(ifEmpty).get();
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof NullableOptional) {
        return ((NullableOptional<?>) obj).isEmpty();
      }
      return false;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public String toString() {
      return "OptionalValue[<no value>]";
    }
  }

  /**
   * Default empty {@code OptionalValue} implementation.
   *
   * @param <V> the type of the value
   */
  class Of<V> implements NullableOptional<V> {
    private final V value;

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
      return false;
    }

    @Override
    public boolean isPresent() {
      return true;
    }

    @Override
    public V get() {
      return this.value;
    }

    @Override
    public V orElse(final V other) {
      return this.value;
    }

    @Override
    public V orElseGet(final ThrowingSupplier<? extends V, ?> other) {
      if (other == null) { throw new NullPointerException("other arg is null"); }
      return this.value;
    }

    @Override
    public V orElseThrow() {
      return this.value;
    }

    @Override
    public V orElseThrow(final ThrowingSupplier<? extends Throwable, ?> exceptionSupplier) {
      if (exceptionSupplier == null) { throw new NullPointerException("exceptionSupplier arg is null"); }
      return this.value;
    }

    @Override
    public NullableOptional<V> nonNull() {
      return this.value == null
        ? NullableOptional.empty()
        : this;
    }

    @Override
    public NullableOptional<V> filter(final ThrowingPredicate<? super V, ?> predicate) {
      if (predicate == null) { throw new NullPointerException("predicate arg is null"); }
      return ThrowingPredicate.unchecked(predicate).test(this.value)
        ? this
        : NullableOptional.empty();
    }

    @Override
    public <U> NullableOptional<U> map(final ThrowingFunction<? super V, ? extends U, ?> mapper) {
      if (mapper == null) { throw new NullPointerException("mapper arg is null"); }
      return new Of<>(ThrowingFunction.unchecked(mapper).apply(this.value));
    }

    @Override
    public <U> NullableOptional<U> flatMap(final ThrowingFunction<? super V, ? extends NullableOptional<U>, ?> mapper) {
      if (mapper == null) { throw new NullPointerException("mapper arg is null"); }
      return ThrowingFunction.unchecked(mapper).apply(this.value);
    }

    @Override
    public NullableOptional<V> ifPresent(final ThrowingConsumer<? super V, ?> consumer) {
      if (consumer == null) { throw new NullPointerException("consumer arg is null"); }
      ThrowingConsumer.unchecked(consumer).accept(this.value);
      return this;
    }

    @Override
    public NullableOptional<V> ifEmpty(final ThrowingRunnable<?> runnable) {
      if (runnable == null) { throw new NullPointerException("runnable arg is null"); }
      return this;
    }

    @Override
    public <R> R fold(final ThrowingFunction<? super V, ? extends R, ?> ifPresent,
                      final ThrowingSupplier<? extends R, ?> ifEmpty) {
      if (ifPresent == null) { throw new NullPointerException("ifPresent arg is null"); }
      if (ifEmpty == null) { throw new NullPointerException("ifEmpty arg is null"); }
      return ThrowingFunction.unchecked(ifPresent).apply(this.value);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof NullableOptional) {
        final NullableOptional<?> other = (NullableOptional<?>) obj;
        return other.isPresent() && Objects.equals(other.get(), this.value);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
      return "OptionalValue[" + this.value + "]";
    }
  }
}
