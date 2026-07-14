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

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyMap;

/**
 * Step attribute.
 *
 * @param <I> the type of input attribute value
 * @param <S> the type of stored attribute value
 * @param <O> the type of output attribute value
 */
public interface StepAttribute<I, S, O> {

  /**
   * Keyword step attribute (default).
   *
   * @see #keyword
   */
  SimpleStepAttribute<Keyword> KEYWORD =
    SimpleStepAttribute.nonNull("default:keyword", Keyword.empty());

  /**
   * Keyword step attribute (default). Alias for {@link #KEYWORD}.
   */
  SimpleStepAttribute<Keyword> keyword = KEYWORD;

  /**
   * Name step attribute (default).
   */
  SimpleStepAttribute<String> NAME =
    SimpleStepAttribute.nonNull("default:name", "");

  /**
   * Name step attribute (default). Alias for {@link #NAME}.
   */
  SimpleStepAttribute<String> name = NAME;

  /**
   * Params step attribute (default).
   */
  SimpleStepAttribute<Map<String, Object>> PARAMS =
    SimpleStepAttribute.nonNull("default:params", emptyMap(), LinkedHashMap::new);

  /**
   * Params step attribute (default). Alias for {@link #PARAMS}.
   */
  SimpleStepAttribute<Map<String, Object>> params = PARAMS;

  /**
   * Expected result step attribute (default).
   */
  SimpleStepAttribute<String> EXPECTED_RESULT =
    SimpleStepAttribute.nonNull("default:expected_result", "");

  /**
   * Expected result step attribute (default). Alias for {@link #EXPECTED_RESULT}.
   */
  SimpleStepAttribute<String> expectedResult = EXPECTED_RESULT;

  /**
   * Comment step attribute (default).
   */
  SimpleStepAttribute<String> COMMENT =
    SimpleStepAttribute.nonNull("default:comment", "");

  /**
   * Comment step attribute (default). Alias for {@link #COMMENT}.
   */
  SimpleStepAttribute<String> comment = COMMENT;

  /**
   * Hidden step attribute (default).
   */
  SimpleStepAttribute<Boolean> HIDDEN =
    SimpleStepAttribute.nonNull("default:hidden", false);

  /**
   * Hidden step attribute (default). Alias for {@link #HIDDEN}.
   */
  SimpleStepAttribute<Boolean> hidden = HIDDEN;

  /**
   * Returns attribute key.
   *
   * @return attribute key
   */
  String key();

  /**
   * Returns attribute default input value.
   *
   * @return attribute default input value
   */
  I defaultInputValue();

  /**
   * Returns attribute default output value.
   *
   * @return attribute default output value
   */
  O defaultOutputValue();

  /**
   * Returns the processed given input value.
   *
   * @param value the value
   * @return processed given input value
   */
  S extractInputValue(I value);

  /**
   * Returns the processed given output value.
   *
   * @param value the value
   * @return processed given output value
   */
  O extractOutputValue(S value);

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
   * @param <I> the type of input attribute value
   * @param <S> the type of stored attribute value
   * @param <O> the type of output attribute value
   */
  class Of<I, S, O> implements StepAttribute<I, S, O> {
    private final String key;
    private final I defaultInputValue;
    private final O defaultOutputValue;
    private final ThrowingFunction<I, S, Error> extractInputValue;
    private final ThrowingFunction<S, O, Error> extractOutputValue;

    /**
     * Ctor.
     *
     * @param key                the attribute key
     * @param defaultInputValue  the default input value
     * @param extractInputValue  the extract input value function
     * @param defaultOutputValue the default output value
     * @param extractOutputValue the extract output value function
     * @throws NullPointerException if {@code key} arg or {@code extractInputValue} arg or {@code extractOutputValue}
     *                              arg is null
     */
    public Of(final String key,
              final I defaultInputValue,
              final ThrowingFunction<I, S, ?> extractInputValue,
              final O defaultOutputValue,
              final ThrowingFunction<S, O, ?> extractOutputValue) {
      if (key == null) { throw new NullPointerException("key arg is null"); }
      if (extractInputValue == null) { throw new NullPointerException("extractInputValue arg is null"); }
      if (extractOutputValue == null) { throw new NullPointerException("extractOutputValue arg is null"); }
      this.key = key;
      this.defaultInputValue = defaultInputValue;
      this.defaultOutputValue = defaultOutputValue;
      this.extractInputValue = ThrowingFunction.unchecked(extractInputValue);
      this.extractOutputValue = ThrowingFunction.unchecked(extractOutputValue);
    }

    @Override
    public String key() {
      return this.key;
    }

    @Override
    public I defaultInputValue() {
      return this.defaultInputValue;
    }

    @Override
    public O defaultOutputValue() {
      return this.defaultOutputValue;
    }

    @Override
    public S extractInputValue(final I value) {
      return this.extractInputValue.apply(value);
    }

    @Override
    public O extractOutputValue(final S value) {
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
