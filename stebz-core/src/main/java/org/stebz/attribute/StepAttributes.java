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
package org.stebz.attribute;

import dev.jlet.function.ThrowingConsumer;
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
   * @param <V>       the type of the attribute
   * @return value of given attribute or default value if attribute is not present
   * @throws NullPointerException if {@code attribute} arg is null
   */
  <V> V get(StepAttribute<V> attribute);

  /**
   * Returns {@code true} if given attribute value is present, otherwise returns {@code false}.
   *
   * @param attribute the attribute
   * @return {@code true} if given attribute value is present, otherwise {@code false}
   * @throws NullPointerException if {@code attribute} arg is null
   */
  boolean contains(StepAttribute<?> attribute);

  /**
   * Returns {@code StepAttributes} without given attribute.
   *
   * @param attribute the attribute
   * @return {@code StepAttributes} without given attribute
   * @throws NullPointerException if {@code attribute} arg is null
   */
  StepAttributes without(StepAttribute<?> attribute);

  /**
   * Returns {@code StepAttributes} with given attribute value updated by {@code updater}.
   *
   * @param attribute the attribute
   * @param updater   the updater
   * @param <V>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg or {@code updater} arg is null
   */
  <V> StepAttributes withUpd(StepAttribute<V> attribute,
                             ThrowingConsumer<? super V, ?> updater);

  /**
   * Returns {@code StepAttributes} with given attribute value created by {@code generator}.
   *
   * @param attribute the attribute
   * @param generator the generator
   * @param <V>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg or {@code generator} arg is null or if {@code attribute} is
   *                              not nullable and {@code generator} result is null
   */
  <V> StepAttributes withNew(StepAttribute<V> attribute,
                             ThrowingFunction<? super V, ? extends V, ?> generator);

  /**
   * Returns {@code StepAttributes} with given attribute value.
   *
   * @param attribute the attribute
   * @param value     the attribute value
   * @param <V>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
   *                              {@code value} is null
   */
  <V> StepAttributes with(StepAttribute<V> attribute,
                          V value);

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param <V1>       the type of the first attribute
   * @param <V2>       the type of the second attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null or if {@code attribute1}
   *                              is not nullable and {@code value1} is null or if {@code attribute2} is not nullable
   *                              and {@code value2} is null
   */
  <V1, V2> StepAttributes with(StepAttribute<V1> attribute1,
                               V1 value1,
                               StepAttribute<V2> attribute2,
                               V2 value2);

  /**
   * Returns {@code StepAttributes} with given attributes values.
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
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is null
   *                              or if {@code attribute1} is not nullable and {@code value1} is null or if
   *                              {@code attribute2} is not nullable and {@code value2} is null or if {@code attribute3}
   *                              is not nullable and {@code value3} is null
   */
  <V1, V2, V3> StepAttributes with(StepAttribute<V1> attribute1,
                                   V1 value1,
                                   StepAttribute<V2> attribute2,
                                   V2 value2,
                                   StepAttribute<V3> attribute3,
                                   V3 value3);

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
   * @param <V>       the type of the attribute
   * @return {@code StepAttributes} with given attribute value
   * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
   *                              {@code value} is null
   */
  static <V> StepAttributes of(final StepAttribute<V> attribute,
                               final V value) {
    return new StepAttributes.Of(attribute, value);
  }

  /**
   * Returns {@code StepAttributes} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param <V1>       the type of the first attribute
   * @param <V2>       the type of the second attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null or if {@code attribute1}
   *                              is not nullable and {@code value1} is null or if {@code attribute2} is not nullable
   *                              and {@code value2} is null
   */
  static <V1, V2> StepAttributes of(final StepAttribute<V1> attribute1,
                                    final V1 value1,
                                    final StepAttribute<V2> attribute2,
                                    final V2 value2) {
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
   * @param <V1>       the type of the first attribute
   * @param <V2>       the type of the second attribute
   * @param <V3>       the type of the third attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is null
   *                              or if {@code attribute1} is not nullable and {@code value1} is null or if
   *                              {@code attribute2} is not nullable and {@code value2} is null or if {@code attribute3}
   *                              is not nullable and {@code value3} is null
   */
  static <V1, V2, V3> StepAttributes of(final StepAttribute<V1> attribute1,
                                        final V1 value1,
                                        final StepAttribute<V2> attribute2,
                                        final V2 value2,
                                        final StepAttribute<V3> attribute3,
                                        final V3 value3) {
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
   * @param <V1>       the type of the first attribute
   * @param <V2>       the type of the second attribute
   * @param <V3>       the type of the third attribute
   * @param <V4>       the type of the fourth attribute
   * @return {@code StepAttributes} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg or
   *                              {@code attribute4} arg is null or if {@code attribute1} is not nullable and
   *                              {@code value1} is null or if {@code attribute2} is not nullable and {@code value2} is
   *                              null or if {@code attribute3} is not nullable and {@code value3} is null or if
   *                              {@code attribute4} is not nullable and {@code value4} is null
   */
  static <V1, V2, V3, V4> StepAttributes of(final StepAttribute<V1> attribute1,
                                            final V1 value1,
                                            final StepAttribute<V2> attribute2,
                                            final V2 value2,
                                            final StepAttribute<V3> attribute3,
                                            final V3 value3,
                                            final StepAttribute<V4> attribute4,
                                            final V4 value4) {
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
     * Appends given attribute value.
     *
     * @param attribute the attribute
     * @param value     the attribute value
     * @param <V>       the type of the attribute
     * @return {@code StepAttributes.Builder} with given attribute value
     * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
     *                              {@code value} is null
     */
    <V> Builder add(StepAttribute<V> attribute,
                    V value);

    /**
     * Appends given attribute.
     *
     * @param attribute the attribute
     * @return {@code StepAttributes.Builder} without given attribute
     * @throws NullPointerException if {@code attribute} arg is null
     */
    Builder remove(StepAttribute<?> attribute);

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
    private final Map<StepAttribute<?>, Object> map;

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
     * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
     *                              {@code value} is null
     */
    public <V> Of(final StepAttribute<V> attribute,
                  final V value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      if (!attribute.nullable() && value == null) {
        throw new IllegalArgumentException("value arg is null but attribute is not nullable");
      }
      final Map<StepAttribute<?>, Object> map = new HashMap<>();
      map.put(attribute, value);
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
     * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null or if {@code attribute1}
     *                              is not nullable and {@code value1} is null or if {@code attribute2} is not nullable
     *                              and {@code value2} is null
     */
    public <V1, V2> Of(final StepAttribute<V1> attribute1,
                       final V1 value1,
                       final StepAttribute<V2> attribute2,
                       final V2 value2) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (!attribute1.nullable() && value1 == null) {
        throw new IllegalArgumentException("value1 arg is null but attribute1 is not nullable");
      }
      if (!attribute2.nullable() && value2 == null) {
        throw new IllegalArgumentException("value2 arg is null but attribute2 is not nullable");
      }
      final Map<StepAttribute<?>, Object> map = new HashMap<>();
      map.put(attribute1, value1);
      map.put(attribute2, value2);
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
     *                              null or if {@code attribute1} is not nullable and {@code value1} is null or if
     *                              {@code attribute2} is not nullable and {@code value2} is null or if
     *                              {@code attribute3} is not nullable and {@code value3} is null
     */
    public <V1, V2, V3> Of(final StepAttribute<V1> attribute1,
                           final V1 value1,
                           final StepAttribute<V2> attribute2,
                           final V2 value2,
                           final StepAttribute<V3> attribute3,
                           final V3 value3) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      if (!attribute1.nullable() && value1 == null) {
        throw new IllegalArgumentException("value1 arg is null but attribute1 is not nullable");
      }
      if (!attribute2.nullable() && value2 == null) {
        throw new IllegalArgumentException("value2 arg is null but attribute2 is not nullable");
      }
      if (!attribute3.nullable() && value3 == null) {
        throw new IllegalArgumentException("value3 arg is null but attribute3 is not nullable");
      }
      final Map<StepAttribute<?>, Object> map = new HashMap<>();
      map.put(attribute1, value1);
      map.put(attribute2, value2);
      map.put(attribute3, value3);
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
     *                              {@code attribute4} arg is null or if {@code attribute1} is not nullable and
     *                              {@code value1} is null or if {@code attribute2} is not nullable and {@code value2}
     *                              is null or if {@code attribute3} is not nullable and {@code value3} is null or if
     *                              {@code attribute4} is not nullable and {@code value4} is null
     */
    public <V1, V2, V3, V4> Of(final StepAttribute<V1> attribute1,
                               final V1 value1,
                               final StepAttribute<V2> attribute2,
                               final V2 value2,
                               final StepAttribute<V3> attribute3,
                               final V3 value3,
                               final StepAttribute<V4> attribute4,
                               final V4 value4) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      if (attribute4 == null) { throw new NullPointerException("attribute4 arg is null"); }
      if (!attribute1.nullable() && value1 == null) {
        throw new IllegalArgumentException("value1 arg is null but attribute1 is not nullable");
      }
      if (!attribute2.nullable() && value2 == null) {
        throw new IllegalArgumentException("value2 arg is null but attribute2 is not nullable");
      }
      if (!attribute3.nullable() && value3 == null) {
        throw new IllegalArgumentException("value3 arg is null but attribute3 is not nullable");
      }
      if (!attribute4.nullable() && value4 == null) {
        throw new IllegalArgumentException("value4 arg is null but attribute4 is not nullable");
      }
      final Map<StepAttribute<?>, Object> map = new HashMap<>();
      map.put(attribute1, value1);
      map.put(attribute2, value2);
      map.put(attribute3, value3);
      map.put(attribute4, value4);
      this.map = map;
    }

    private Of(final Map<StepAttribute<?>, Object> map) {
      this.map = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V get(final StepAttribute<V> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Object value = this.map.getOrDefault(attribute, NO_VALUE);
      return attribute.safeValue(
        value == NO_VALUE || (value == null && !attribute.nullable())
          ? attribute.defaultValue()
          : (V) value
      );
    }

    @Override
    public boolean contains(final StepAttribute<?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Object value = this.map.getOrDefault(attribute, NO_VALUE);
      return !(value == NO_VALUE || (value == null && !attribute.nullable()));
    }

    @Override
    public StepAttributes without(final StepAttribute<?> attribute) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      final Map<StepAttribute<?>, Object> attributesCopy = new HashMap<>(this.map);
      attributesCopy.remove(attribute);
      return new Of(attributesCopy);
    }

    @Override
    public <V> StepAttributes withUpd(final StepAttribute<V> attribute,
                                      final ThrowingConsumer<? super V, ?> updater) {
      final V value = this.get(attribute);
      ThrowingConsumer.unchecked(updater).accept(value);
      return this.with(attribute, value);
    }

    @Override
    public <V> StepAttributes withNew(final StepAttribute<V> attribute,
                                      final ThrowingFunction<? super V, ? extends V, ?> generator) {
      return this.with(
        attribute,
        ThrowingFunction.unchecked(generator).apply(this.get(attribute))
      );
    }

    @Override
    public <V> StepAttributes with(final StepAttribute<V> attribute,
                                   final V value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      if (!attribute.nullable() && value == null) {
        throw new IllegalArgumentException("value arg is null but attribute is not nullable");
      }
      final Map<StepAttribute<?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute, value);
      return new Of(mapCopy);
    }

    @Override
    public <V1, V2> StepAttributes with(final StepAttribute<V1> attribute1,
                                        final V1 value1,
                                        final StepAttribute<V2> attribute2,
                                        final V2 value2) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (!attribute1.nullable() && value1 == null) {
        throw new IllegalArgumentException("value1 arg is null but attribute1 is not nullable");
      }
      if (!attribute2.nullable() && value2 == null) {
        throw new IllegalArgumentException("value2 arg is null but attribute2 is not nullable");
      }
      final Map<StepAttribute<?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute1, value1);
      mapCopy.put(attribute2, value2);
      return new Of(mapCopy);
    }

    @Override
    public <V1, V2, V3> StepAttributes with(final StepAttribute<V1> attribute1,
                                            final V1 value1,
                                            final StepAttribute<V2> attribute2,
                                            final V2 value2,
                                            final StepAttribute<V3> attribute3,
                                            final V3 value3) {
      if (attribute1 == null) { throw new NullPointerException("attribute1 arg is null"); }
      if (attribute2 == null) { throw new NullPointerException("attribute2 arg is null"); }
      if (attribute3 == null) { throw new NullPointerException("attribute3 arg is null"); }
      if (!attribute1.nullable() && value1 == null) {
        throw new IllegalArgumentException("value1 arg is null but attribute1 is not nullable");
      }
      if (!attribute2.nullable() && value2 == null) {
        throw new IllegalArgumentException("value2 arg is null but attribute2 is not nullable");
      }
      if (!attribute3.nullable() && value3 == null) {
        throw new IllegalArgumentException("value3 arg is null but attribute3 is not nullable");
      }
      final Map<StepAttribute<?>, Object> mapCopy = new HashMap<>(this.map);
      mapCopy.put(attribute1, value1);
      mapCopy.put(attribute2, value2);
      mapCopy.put(attribute3, value3);
      return new Of(mapCopy);
    }

    @Override
    public Builder asBuilder() {
      return new BuilderOf(new HashMap<>(this.map));
    }

    @Override
    public String toString() {
      return "StepAttributes";
    }
  }

  /**
   * Default {@code StepAttributes.Builder} implementation.
   */
  class BuilderOf implements Builder {
    private final Map<StepAttribute<?>, Object> map;

    /**
     * Ctor.
     */
    public BuilderOf() {
      this(new HashMap<>());
    }

    private BuilderOf(final Map<StepAttribute<?>, Object> map) {
      this.map = map;
    }

    @Override
    public <V> Builder add(final StepAttribute<V> attribute,
                           final V value) {
      if (attribute == null) { throw new NullPointerException("attribute arg is null"); }
      if (!attribute.nullable() && value == null) {
        throw new IllegalArgumentException("value arg is null but attribute is not nullable");
      }
      this.map.put(attribute, value);
      return this;
    }

    @Override
    public Builder remove(final StepAttribute<?> attribute) {
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
