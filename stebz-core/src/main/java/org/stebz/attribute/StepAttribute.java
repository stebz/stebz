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
package org.stebz.attribute;

import org.stebz.util.function.ThrowingFunction;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyMap;

/**
 * Step attribute.
 *
 * @param <V> the type of attribute value
 */
public interface StepAttribute<V> {
  /**
   * Keyword step attribute (default).
   */
  StepAttribute<Keyword> KEYWORD = nonNull("default:keyword", Keyword.empty());
  /**
   * Name step attribute (default).
   */
  StepAttribute<String> NAME = nonNull("default:name", "");
  /**
   * Params step attribute (default).
   */
  StepAttribute<Map<String, Object>> PARAMS = nonNull("default:params", emptyMap(), LinkedHashMap::new);
  /**
   * Expected result step attribute (default).
   */
  StepAttribute<String> EXPECTED_RESULT = nonNull("default:expected_result", "");
  /**
   * Comment step attribute (default).
   */
  StepAttribute<String> COMMENT = nonNull("default:comment", "");
  /**
   * Hidden step attribute (default).
   */
  StepAttribute<Boolean> HIDDEN = nonNull("default:hidden", false);

  /**
   * Returns attribute key.
   *
   * @return attribute key
   */
  String key();

  /**
   * Returns {@code true} if attribute is nullable, otherwise returns {@code false}.
   *
   * @return {@code true} if attribute is nullable, otherwise {@code false}
   */
  boolean nullable();

  /**
   * Returns attribute default value.
   *
   * @return attribute default value
   */
  V defaultValue();

  /**
   * Returns the processed given value.
   *
   * @param value the value
   * @return processed given value
   */
  V safeValue(V value);

  /**
   * Indicates whether some other object is "equal to" this one. Implementations of the {@link StepAttribute} should
   * have the same contract.
   *
   * @return true if this object is the same as the {@code obj} argument, false otherwise
   * @see StepAttribute.Of#equals(Object)
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns a hash code value for the object. Implementations of the {@link StepAttribute} should have the same
   * contract.
   *
   * @return a hash code value for this object
   * @see StepAttribute.Of#hashCode()
   */
  @Override
  int hashCode();

  /**
   * Returns non-null attribute with given key and default value.
   *
   * @param key          the attribute key
   * @param defaultValue the attribute default value
   * @param <V>          the type of the attribute value
   * @return non-null attribute
   * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg is null
   */
  static <V> StepAttribute<V> nonNull(final String key,
                                      final V defaultValue) {
    return new Of<>(key, false, defaultValue);
  }

  /**
   * Returns non-null attribute with given key, default value and safe value function.
   *
   * @param key               the attribute key
   * @param defaultValue      the attribute default value
   * @param safeValueFunction the safe value function
   * @param <V>               the type of the attribute value
   * @return non-null attribute
   * @throws NullPointerException if {@code key} arg or {@code defaultValue} arg or {@code safeValueFunction} arg is
   *                              null
   */
  static <V> StepAttribute<V> nonNull(final String key,
                                      final V defaultValue,
                                      final ThrowingFunction<V, V, Error> safeValueFunction) {
    return new Of<>(key, false, defaultValue, safeValueFunction);
  }

  /**
   * Returns nullable attribute with given key and {@code null} default value.
   *
   * @param key the attribute key
   * @param <V> the type of the attribute value
   * @return nullable attribute
   * @throws NullPointerException if {@code key} arg is null
   */
  static <V> StepAttribute<V> nullable(final String key) {
    return new Of<>(key, true, null);
  }

  /**
   * Returns nullable attribute with given key and default value.
   *
   * @param key          the attribute key
   * @param defaultValue the attribute default value
   * @param <V>          the type of the attribute value
   * @return nullable attribute
   * @throws NullPointerException if {@code key} arg is null
   */
  static <V> StepAttribute<V> nullable(final String key,
                                       final V defaultValue) {
    return new Of<>(key, true, defaultValue);
  }

  /**
   * Returns nullable attribute with given key, default value and safe value function.
   *
   * @param key               the attribute key
   * @param defaultValue      the attribute default value
   * @param safeValueFunction the safe value function
   * @param <V>               the type of the attribute value
   * @return nullable attribute
   * @throws NullPointerException if {@code key} arg or {@code safeValueFunction} arg is null
   */
  static <V> StepAttribute<V> nullable(final String key,
                                       final V defaultValue,
                                       final ThrowingFunction<V, V, Error> safeValueFunction) {
    return new Of<>(key, true, defaultValue, safeValueFunction);
  }

  /**
   * Returns keyword of given value.
   *
   * @param value the keyword value
   * @return keyword of given value
   * @throws NullPointerException if {@code value} arg is null
   */
  static Keyword keyword(final String value) {
    return new Keyword.Of(value);
  }

  /**
   * Returns empty params.
   *
   * @return empty params
   */
  static Map<String, Object> params() {
    return Collections.emptyMap();
  }

  /**
   * Returns params of given values.
   *
   * @param paramName  the param name
   * @param paramValue the param value
   * @return params of given values
   */
  static Map<String, Object> params(final String paramName, final Object paramValue) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put(paramName, paramValue);
    return map;
  }

  /**
   * Returns params of given values.
   *
   * @param paramName1  the first param name
   * @param paramValue1 the first param value
   * @param paramName2  the second param name
   * @param paramValue2 the second param value
   * @return params of given values
   */
  static Map<String, Object> params(final String paramName1, final Object paramValue1,
                                    final String paramName2, final Object paramValue2) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put(paramName1, paramValue1);
    map.put(paramName2, paramValue2);
    return map;
  }

  /**
   * Returns params of given values.
   *
   * @param paramName1  the first param name
   * @param paramValue1 the first param value
   * @param paramName2  the second param name
   * @param paramValue2 the second param value
   * @param paramName3  the third param name
   * @param paramValue3 the third param value
   * @return params of given values
   */
  static Map<String, Object> params(final String paramName1, final Object paramValue1,
                                    final String paramName2, final Object paramValue2,
                                    final String paramName3, final Object paramValue3) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put(paramName1, paramValue1);
    map.put(paramName2, paramValue2);
    map.put(paramName3, paramValue3);
    return map;
  }

  /**
   * Returns params of given values.
   *
   * @param paramName1  the first param name
   * @param paramValue1 the first param value
   * @param paramName2  the second param name
   * @param paramValue2 the second param value
   * @param paramName3  the third param name
   * @param paramValue3 the third param value
   * @param paramName4  the fourth param name
   * @param paramValue4 the fourth param value
   * @return params of given values
   */
  static Map<String, Object> params(final String paramName1, final Object paramValue1,
                                    final String paramName2, final Object paramValue2,
                                    final String paramName3, final Object paramValue3,
                                    final String paramName4, final Object paramValue4) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put(paramName1, paramValue1);
    map.put(paramName2, paramValue2);
    map.put(paramName3, paramValue3);
    map.put(paramName4, paramValue4);
    return map;
  }

  /**
   * Returns params of given values.
   *
   * @param paramName1  the first param name
   * @param paramValue1 the first param value
   * @param paramName2  the second param name
   * @param paramValue2 the second param value
   * @param paramName3  the third param name
   * @param paramValue3 the third param value
   * @param paramName4  the fourth param name
   * @param paramValue4 the fourth param value
   * @param paramName5  the fifth param name
   * @param paramValue5 the fifth param value
   * @return params of given values
   */
  static Map<String, Object> params(final String paramName1, final Object paramValue1,
                                    final String paramName2, final Object paramValue2,
                                    final String paramName3, final Object paramValue3,
                                    final String paramName4, final Object paramValue4,
                                    final String paramName5, final Object paramValue5) {
    final Map<String, Object> map = new LinkedHashMap<>();
    map.put(paramName1, paramValue1);
    map.put(paramName2, paramValue2);
    map.put(paramName3, paramValue3);
    map.put(paramName4, paramValue4);
    map.put(paramName5, paramValue5);
    return map;
  }

  /**
   * Returns params of given values.
   *
   * @param entries params values
   * @return params of given values
   * @throws NullPointerException if {@code entries} arg is null
   * @see #param(String, Object)
   */
  @SafeVarargs
  static Map<String, Object> params(final Map.Entry<String, ?>... entries) {
    final Map<String, Object> map = new LinkedHashMap<>();
    for (final Map.Entry<String, ?> entry : entries) {
      map.put(entry.getKey(), entry.getValue());
    }
    return map;
  }

  /**
   * Returns param.
   *
   * @param paramName  the param name
   * @param paramValue the param value
   * @return param
   * @see #params(Map.Entry[])
   */
  static Map.Entry<String, Object> param(final String paramName,
                                         final Object paramValue) {
    return new AbstractMap.SimpleEntry<>(paramName, paramValue);
  }

  /**
   * Default {@code StepAttribute} implementation.
   *
   * @param <V> the type of attribute value
   */
  class Of<V> implements StepAttribute<V> {
    private static final ThrowingFunction<?, ?, ?> IDENTITY_FUNCTION = v -> v;
    private final String key;
    private final boolean nullable;
    private final V defaultValue;
    private final ThrowingFunction<V, V, Error> safeValueFunction;

    /**
     * Ctor.
     *
     * @param key          the attribute key
     * @param nullable     the nullable sign
     * @param defaultValue the attribute default value
     * @throws NullPointerException if {@code key} arg or {@code safeValueFunction} arg is null or if {@code nullable}
     *                              is {@code false} and {@code defaultValue} arg is null
     */
    @SuppressWarnings("unchecked")
    public Of(final String key,
              final boolean nullable,
              final V defaultValue) {
      this(key, nullable, defaultValue, (ThrowingFunction<V, V, Error>) IDENTITY_FUNCTION);
    }

    /**
     * Ctor.
     *
     * @param key               the attribute key
     * @param nullable          the nullable sign
     * @param defaultValue      the attribute default value
     * @param safeValueFunction the safe value function
     * @throws NullPointerException if {@code key} arg or {@code safeValueFunction} arg is null or if {@code nullable}
     *                              is {@code false} and {@code defaultValue} arg is null
     */
    public Of(final String key,
              final boolean nullable,
              final V defaultValue,
              final ThrowingFunction<V, V, ?> safeValueFunction) {
      if (key == null) { throw new NullPointerException("key arg is null"); }
      if (!nullable && defaultValue == null) { throw new NullPointerException("defaultValue arg is null"); }
      if (safeValueFunction == null) { throw new NullPointerException("safeValueFunction arg is null"); }
      this.key = key;
      this.nullable = nullable;
      this.defaultValue = defaultValue;
      this.safeValueFunction = ThrowingFunction.unchecked(safeValueFunction);
    }

    @Override
    public String key() {
      return this.key;
    }

    @Override
    public boolean nullable() {
      return this.nullable;
    }

    @Override
    public V defaultValue() {
      return this.defaultValue;
    }

    @Override
    public V safeValue(final V value) {
      return this.safeValueFunction.apply(value);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof StepAttribute) {
        final StepAttribute<?> other = (StepAttribute<?>) obj;
        return Objects.equals(this.key, other.key())
          && this.nullable == other.nullable()
          && Objects.equals(this.defaultValue, other.defaultValue());
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
