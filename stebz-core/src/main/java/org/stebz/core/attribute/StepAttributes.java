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

import java.util.HashMap;
import java.util.Map;

/**
 * Step attributes.
 */
public interface StepAttributes {

  /**
   * Returns value of given attribute or default value if attribute is not present.
   *
   * @param attribute the attribute
   * @param <O>       the type of the attribute
   * @return value of given attribute or default value if attribute is not present
   * @throws NullPointerException if {@code attribute} arg is null
   */
  <O> O get(StepAttribute<?, ?, O> attribute);

  /**
   * Returns {@code true} if given attribute value is present, otherwise returns {@code false}.
   *
   * @param attribute the attribute
   * @return {@code true} if given attribute value is present, otherwise {@code false}
   * @throws NullPointerException if {@code attribute} arg is null
   */
  boolean contains(StepAttribute<?, ?, ?> attribute);

  /**
   * Returns {@code StepAttributes} without given attribute.
   *
   * @param attribute the attribute
   * @return {@code StepAttributes} without given attribute
   * @throws NullPointerException if {@code attribute} arg is null
   */
  StepAttributes without(StepAttribute<?, ?, ?> attribute);

  /**
   * Returns {@code StepAttributes} with default attribute value.
   *
   * @param attribute the attribute
   * @return {@code StepAttributes} with default attribute value
   * @throws NullPointerException if {@code attribute} arg is null
   */
  StepAttributes with(StepAttribute<?, ?, ?> attribute);

  /**
   * Returns {@code StepAttributes} with given attribute value created by {@code generator}.
   *
   * @param attribute the attribute
   * @param generator the generator
   * @param <I>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg or {@code generator} arg is null
   */
  <I, O> StepAttributes withOf(StepAttribute<I, ?, O> attribute,
                               ThrowingFunction<? super O, ? extends I, ?> generator);

  /**
   * Returns {@code StepAttributes} with given attribute value.
   *
   * @param attribute the attribute
   * @param value     the attribute value
   * @param <I>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg is null
   */
  <I> StepAttributes with(StepAttribute<I, ?, ?> attribute,
                          I value);

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param <I1>       the type of the first attribute
   * @param <I2>       the type of the second attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null
   */
  <I1, I2> StepAttributes with(StepAttribute<I1, ?, ?> attribute1,
                               I1 value1,
                               StepAttribute<I2, ?, ?> attribute2,
                               I2 value2);

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param attribute3 the third attribute
   * @param value3     the third attribute value
   * @param <I1>       the type of the first attribute
   * @param <I2>       the type of the second attribute
   * @param <I3>       the type of the third attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is null
   */
  <I1, I2, I3> StepAttributes with(StepAttribute<I1, ?, ?> attribute1,
                                   I1 value1,
                                   StepAttribute<I2, ?, ?> attribute2,
                                   I2 value2,
                                   StepAttribute<I3, ?, ?> attribute3,
                                   I3 value3);

  /**
   * Returns {@code StepAttributes.Builder} of this {@code StepAttributes} attributes.
   *
   * @return {@code StepAttributes.Builder} of this {@code StepAttributes} attributes
   */
  Builder asBuilder();

  /**
   * Returns empty {@code StepAttributes}.
   *
   * @return empty {@code StepAttributes}
   */
  static StepAttributes empty() {
    return Of.EMPTY;
  }

  /**
   * Returns {@code StepAttributes} with given attribute value.
   *
   * @param attribute the attribute
   * @param value     the attribute value
   * @param <I>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
   *                              {@code value} is null
   */
  static <I> StepAttributes of(final StepAttribute<I, ?, ?> attribute,
                               final I value) {
    return new StepAttributes.Of(attribute, value);
  }

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param <I1>       the type of the first attribute
   * @param <I2>       the type of the second attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is nul
   */
  static <I1, I2> StepAttributes of(final StepAttribute<I1, ?, ?> attribute1,
                                    final I1 value1,
                                    final StepAttribute<I2, ?, ?> attribute2,
                                    final I2 value2) {
    return new StepAttributes.Of(attribute1, value1, attribute2, value2);
  }

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param attribute3 the third attribute
   * @param value3     the third attribute value
   * @param <I1>       the type of the first attribute
   * @param <I2>       the type of the second attribute
   * @param <I3>       the type of the third attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is null
   */
  static <I1, I2, I3> StepAttributes of(final StepAttribute<I1, ?, ?> attribute1,
                                        final I1 value1,
                                        final StepAttribute<I2, ?, ?> attribute2,
                                        final I2 value2,
                                        final StepAttribute<I3, ?, ?> attribute3,
                                        final I3 value3) {
    return new StepAttributes.Of(attribute1, value1, attribute2, value2, attribute3, value3);
  }

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param attribute3 the third attribute
   * @param value3     the third attribute value
   * @param attribute4 the fourth attribute
   * @param value4     the fourth attribute value
   * @param <I1>       the type of the first attribute
   * @param <I2>       the type of the second attribute
   * @param <I3>       the type of the third attribute
   * @param <I4>       the type of the fourth attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg or
   *                              {@code attribute4} arg is null
   */
  static <I1, I2, I3, I4> StepAttributes of(final StepAttribute<I1, ?, ?> attribute1,
                                            final I1 value1,
                                            final StepAttribute<I2, ?, ?> attribute2,
                                            final I2 value2,
                                            final StepAttribute<I3, ?, ?> attribute3,
                                            final I3 value3,
                                            final StepAttribute<I4, ?, ?> attribute4,
                                            final I4 value4) {
    return new StepAttributes.Of(attribute1, value1, attribute2, value2, attribute3, value3, attribute4, value4);
  }

  /**
   * Returns empty {@code StepAttributes.Builder}.
   *
   * @return empty {@code StepAttributes.Builder}
   */
  static Builder builder() {
    return new BuilderOf();
  }

  /**
   * Step attributes builder.
   */
  interface Builder {

    /**
     * Appends given attribute with default attribute value.
     *
     * @param attribute the attribute
     * @return {@code StepAttributes.Builder} with given attribute value
     * @throws NullPointerException if {@code attribute} arg is null
     */
    Builder add(StepAttribute<?, ?, ?> attribute);

    /**
     * Appends given attribute value.
     *
     * @param attribute the attribute
     * @param value     the attribute value
     * @param <I>       the type of the attribute
     * @return {@code StepAttributes.Builder} with given attribute value
     * @throws NullPointerException if {@code attribute} arg is null
     */
    <I> Builder add(StepAttribute<I, ?, ?> attribute,
                    I value);

    /**
     * Appends given attribute.
     *
     * @param attribute the attribute
     * @return {@code StepAttributes.Builder} without given attribute
     * @throws NullPointerException if {@code attribute} arg is null
     */
    Builder remove(StepAttribute<?, ?, ?> attribute);

    /**
     * Returns {@code StepAttributes} of current attributes.
     *
     * @return {@code StepAttributes} of current attributes
     */
    StepAttributes build();
  }

  /**
   * Default {@code StepAttributes} implementation.
   */
  class Of implements StepAttributes {
    private static final StepAttributes EMPTY = new Of();
    private static final Object NO_VALUE = new Object();
    private final Map<StepAttribute<?, ?, ?>, Object> map;

    /**
     * Ctor.
     */
    public Of() {
      this.map = new HashMap<>();
    }

    /**
     * Ctor.
     *
     * @param attribute the attribute
     * @param value     the attribute value
     * @param <V>       the type of the attribute
     * @throws NullPointerException if {@code attribute} arg is null
     */
    public <V> Of(final StepAttribute<V, ?, ?> attribute,
                  final V value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> map = new HashMap<>();
      map.put(attribute, attribute.extractInputValue(value));
      this.map = map;
    }

    /**
     * Ctor.
     *
     * @param attribute1 the first attribute
     * @param value1     the first attribute value
     * @param attribute2 the second attribute
     * @param value2     the second attribute value
     * @param <V1>       the type of the first attribute
     * @param <V2>       the type of the second attribute
     * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null
     */
    public <V1, V2> Of(final StepAttribute<V1, ?, ?> attribute1,
                       final V1 value1,
                       final StepAttribute<V2, ?, ?> attribute2,
                       final V2 value2) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> map = new HashMap<>();
      map.put(attribute1, attribute1.extractInputValue(value1));
      map.put(attribute2, attribute2.extractInputValue(value2));
      this.map = map;
    }

    /**
     * Ctor.
     *
     * @param attribute1 the first attribute
     * @param value1     the first attribute value
     * @param attribute2 the second attribute
     * @param value2     the second attribute value
     * @param attribute3 the third attribute
     * @param value3     the third attribute value
     * @param <V1>       the type of the first attribute
     * @param <V2>       the type of the second attribute
     * @param <V3>       the type of the third attribute
     * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is
     *                              null
     */
    public <V1, V2, V3> Of(final StepAttribute<V1, ?, ?> attribute1,
                           final V1 value1,
                           final StepAttribute<V2, ?, ?> attribute2,
                           final V2 value2,
                           final StepAttribute<V3, ?, ?> attribute3,
                           final V3 value3) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> map = new HashMap<>();
      map.put(attribute1, attribute1.extractInputValue(value1));
      map.put(attribute2, attribute2.extractInputValue(value2));
      map.put(attribute3, attribute3.extractInputValue(value3));
      this.map = map;
    }

    /**
     * Ctor.
     *
     * @param attribute1 the first attribute
     * @param value1     the first attribute value
     * @param attribute2 the second attribute
     * @param value2     the second attribute value
     * @param attribute3 the third attribute
     * @param value3     the third attribute value
     * @param attribute4 the fourth attribute
     * @param value4     the fourth attribute value
     * @param <V1>       the type of the first attribute
     * @param <V2>       the type of the second attribute
     * @param <V3>       the type of the third attribute
     * @param <V4>       the type of the fourth attribute
     * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg or
     *                              {@code attribute4} arg is null
     */
    public <V1, V2, V3, V4> Of(final StepAttribute<V1, ?, ?> attribute1,
                               final V1 value1,
                               final StepAttribute<V2, ?, ?> attribute2,
                               final V2 value2,
                               final StepAttribute<V3, ?, ?> attribute3,
                               final V3 value3,
                               final StepAttribute<V4, ?, ?> attribute4,
                               final V4 value4) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      if (attribute4 == null) { throw new NullPointerException("attribute4 arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> map = new HashMap<>();
      map.put(attribute1, attribute1.extractInputValue(value1));
      map.put(attribute2, attribute2.extractInputValue(value2));
      map.put(attribute3, attribute3.extractInputValue(value3));
      map.put(attribute4, attribute4.extractInputValue(value4));
      this.map = map;
    }

    private Of(final Map<StepAttribute<?, ?, ?>, Object> map) {
      this.map = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O get(final StepAttribute<?, ?, O> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Object value = this.map.getOrDefault(attribute, NO_VALUE);
      return ((StepAttribute<Object, Object, O>) attribute).extractOutputValue(
        value == NO_VALUE
          ? attribute.defaultOutputValue()
          : value
      );
    }

    @Override
    public boolean contains(final StepAttribute<?, ?, ?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      return this.map.getOrDefault(attribute, NO_VALUE) != NO_VALUE;
    }

    @Override
    public StepAttributes without(final StepAttribute<?, ?, ?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> attributesCopy = new HashMap<>(this.map);
      attributesCopy.remove(attribute);
      return new Of(attributesCopy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StepAttributes with(final StepAttribute<?, ?, ?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      return this.with((StepAttribute<Object, Object, Object>) attribute, attribute.defaultInputValue());
    }

    @Override
    public <I, O> StepAttributes withOf(final StepAttribute<I, ?, O> attribute,
                                        final ThrowingFunction<? super O, ? extends I, ?> generator) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      if (generator == null) { throw new NullPointerException("generator arg is null"); }
      return this.with(
        attribute,
        ThrowingFunction.unchecked(generator).apply(this.get(attribute))
      );
    }

    @Override
    public <I> StepAttributes with(final StepAttribute<I, ?, ?> attribute,
                                   final I value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute, attribute.extractInputValue(value));
      return new Of(mapCopy);
    }

    @Override
    public <V1, V2> StepAttributes with(final StepAttribute<V1, ?, ?> attribute1,
                                        final V1 value1,
                                        final StepAttribute<V2, ?, ?> attribute2,
                                        final V2 value2) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute1, attribute1.extractInputValue(value1));
      mapCopy.put(attribute2, attribute2.extractInputValue(value2));
      return new Of(mapCopy);
    }

    @Override
    public <V1, V2, V3> StepAttributes with(final StepAttribute<V1, ?, ?> attribute1,
                                            final V1 value1,
                                            final StepAttribute<V2, ?, ?> attribute2,
                                            final V2 value2,
                                            final StepAttribute<V3, ?, ?> attribute3,
                                            final V3 value3) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      final Map<StepAttribute<?, ?, ?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute1, attribute1.extractInputValue(value1));
      mapCopy.put(attribute2, attribute2.extractInputValue(value2));
      mapCopy.put(attribute3, attribute3.extractInputValue(value3));
      return new Of(mapCopy);
    }

    @Override
    public Builder asBuilder() {
      return new BuilderOf(new HashMap<>(this.map));
    }
  }

  /**
   * Default {@code StepAttributes.Builder} implementation.
   */
  class BuilderOf implements Builder {
    private final Map<StepAttribute<?, ?, ?>, Object> map;

    /**
     * Ctor.
     */
    public BuilderOf() {
      this(new HashMap<>());
    }

    private BuilderOf(final Map<StepAttribute<?, ?, ?>, Object> map) {
      this.map = map;
    }

    @Override
    public Builder add(final StepAttribute<?, ?, ?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      this.map.put(attribute, attribute.defaultInputValue());
      return this;
    }

    @Override
    public <I> Builder add(final StepAttribute<I, ?, ?> attribute,
                           final I value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      this.map.put(attribute, attribute.extractInputValue(value));
      return this;
    }

    @Override
    public Builder remove(final StepAttribute<?, ?, ?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      this.map.remove(attribute);
      return this;
    }

    @Override
    public StepAttributes build() {
      return new StepAttributes.Of(new HashMap<>(this.map));
    }
  }
}
