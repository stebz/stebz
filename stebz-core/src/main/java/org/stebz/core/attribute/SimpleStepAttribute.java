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
package org.stebz.core.attribute;

import dev.jlet.function.ThrowingFunction;

import java.util.Objects;

/**
 * Simple step attribute.
 *
 * @param <V> the type of attribute value
 */
public interface SimpleStepAttribute<V> extends StepAttribute<V, V, V> {

  /**
   * Returns simple nullable attribute with given key and {@code null} default value.
   *
   * @param key the attribute key
   * @param <V> the type of the attribute value
   * @return simple nullable attribute
   * @throws NullPointerException if {@code key} arg is null
   */
  static <V> SimpleStepAttribute<V> nullable(final String key) {
    return new Nullable<>(key);
  }

  /**
   * Returns simple nullable attribute with given key and default value.
   *
   * @param key          the attribute key
   * @param defaultValue the attribute default value
   * @param <V>          the type of the attribute value
   * @return simple nullable attribute
   * @throws NullPointerException if {@code key} arg is null
   */
  static <V> SimpleStepAttribute<V> nullable(final String key,
                                             final V defaultValue) {
    return new Nullable<>(key, defaultValue);
  }

  /**
   * Returns simple nullable attribute with given key, default value and extract output value function.
   *
   * @param key                the attribute key
   * @param defaultValue       the attribute default value
   * @param extractOutputValue extract output value function
   * @param <V>                the type of the attribute value
   * @return simple nullable attribute
   * @throws NullPointerException if {@code key} arg or {@code extractOutputValue} arg is null
   */
  static <V> SimpleStepAttribute<V> nullable(final String key,
                                             final V defaultValue,
                                             final ThrowingFunction<? super V, ? extends V, ?> extractOutputValue) {
    return new Nullable<>(key, defaultValue, extractOutputValue);
  }

  /**
   * Returns simple non-null attribute with given key and default value.
   *
   * @param key          the attribute key
   * @param defaultValue the attribute default value
   * @param <V>          the type of the attribute value
   * @return simple non-null attribute
   * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg is null
   */
  static <V> SimpleStepAttribute<V> nonNull(final String key,
                                            final V defaultValue) {
    return new NonNull<>(key, defaultValue);
  }

  /**
   * Returns simple non-null attribute with given key, default value and extract output value function.
   *
   * @param key                the attribute key
   * @param defaultValue       the attribute default value
   * @param extractOutputValue extract output value function
   * @param <V>                the type of the attribute value
   * @return simple non-null attribute
   * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg or {@code extractOutputValue} arg is
   *                              null
   */
  static <V> SimpleStepAttribute<V> nonNull(final String key,
                                            final V defaultValue,
                                            final ThrowingFunction<? super V, ? extends V, ?> extractOutputValue) {
    return new NonNull<>(key, defaultValue, extractOutputValue);
  }

  /**
   * Default nullable {@code SimpleStepAttribute} implementation.
   *
   * @param <V> the type of attribute value
   */
  class Nullable<V> implements SimpleStepAttribute<V> {
    private final String key;
    private final V defaultValue;
    private final ThrowingFunction<? super V, ? extends V, Error> extractOutputValue;

    /**
     * Ctor.
     *
     * @param key the attribute key
     * @throws NullPointerException if {@code key} arg is null
     */
    public Nullable(final String key) {
      this(key, null, v -> v);
    }

    /**
     * Ctor.
     *
     * @param key          the attribute key
     * @param defaultValue the default value
     * @throws NullPointerException if {@code key} arg is null
     */
    public Nullable(final String key,
                    final V defaultValue) {
      this(key, defaultValue, v -> v);
    }

    /**
     * Ctor.
     *
     * @param key                the attribute key
     * @param defaultValue       the default value
     * @param extractOutputValue the extract output value function
     * @throws NullPointerException if {@code key} arg or {@code extractOutputValue} arg is null
     */
    public Nullable(final String key,
                    final V defaultValue,
                    final ThrowingFunction<? super V, ? extends V, ?> extractOutputValue) {
      if (key == null) { throw new NullPointerException("key arg is null"); }
      if (extractOutputValue == null) { throw new NullPointerException("extractOutputValue arg is null"); }
      this.key = key;
      this.defaultValue = defaultValue;
      this.extractOutputValue = ThrowingFunction.unchecked(extractOutputValue);
    }

    @Override
    public String key() {
      return this.key;
    }

    @Override
    public V defaultInputValue() {
      return this.defaultValue;
    }

    @Override
    public V defaultOutputValue() {
      return this.defaultValue;
    }

    @Override
    public V extractInputValue(final V value) {
      return value;
    }

    @Override
    public V extractOutputValue(final V value) {
      return this.extractOutputValue.apply(value);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof StepAttribute) {
        return Objects.equals(this.key, ((StepAttribute<?, ?, ?>) obj).key());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.key.hashCode();
    }

    @Override
    public String toString() {
      return "Attribute[" + this.key + "]";
    }
  }

  /**
   * Default non-null {@code SimpleStepAttribute} implementation.
   *
   * @param <V> the type of attribute value
   */
  class NonNull<V> implements SimpleStepAttribute<V> {
    private final String key;
    private final V defaultValue;
    private final ThrowingFunction<? super V, ? extends V, Error> extractOutputValue;

    /**
     * Ctor.
     *
     * @param key          the attribute key
     * @param defaultValue the default value
     * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg is null
     */
    public NonNull(final String key,
                   final V defaultValue) {
      this(key, defaultValue, v -> v);
    }

    /**
     * Ctor.
     *
     * @param key                the attribute key
     * @param defaultValue       the default value
     * @param extractOutputValue the extract output value function
     * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg or {@code extractOutputValue} arg is
     *                              null
     */
    public NonNull(final String key,
                   final V defaultValue,
                   final ThrowingFunction<? super V, ? extends V, ?> extractOutputValue) {
      if (key == null) { throw new NullPointerException("key arg is null"); }
      if (defaultValue == null) { throw new NullPointerException("defaultValue arg is null"); }
      if (extractOutputValue == null) { throw new NullPointerException("extractOutputValue arg is null"); }
      this.key = key;
      this.defaultValue = defaultValue;
      this.extractOutputValue = ThrowingFunction.unchecked(extractOutputValue);
    }

    @Override
    public String key() {
      return this.key;
    }

    @Override
    public V defaultInputValue() {
      return this.defaultValue;
    }

    @Override
    public V defaultOutputValue() {
      return this.defaultValue;
    }

    @Override
    public V extractInputValue(final V value) {
      if (value == null) { throw new IllegalArgumentException("attribute value is null"); }
      return value;
    }

    @Override
    public V extractOutputValue(final V value) {
      return this.extractOutputValue.apply(value);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof StepAttribute) {
        return Objects.equals(this.key, ((StepAttribute<?, ?, ?>) obj).key());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.key.hashCode();
    }

    @Override
    public String toString() {
      return "Attribute[" + this.key + "]";
    }
  }
}
