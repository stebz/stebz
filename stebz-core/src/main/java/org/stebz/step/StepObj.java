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
package org.stebz.step;

import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttribute;
import org.stebz.attribute.StepAttributes;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.COMMENT;
import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.HIDDEN;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Step object.
 *
 * @param <S> the type of the step implementing {@code StepObj}
 */
public interface StepObj<S extends StepObj<S>> {

  /**
   * Returns step attributes.
   *
   * @return step attributes
   */
  StepAttributes attributes();

  /**
   * Returns {@code StepObj} with given attributes.
   *
   * @param attributes the step attributes
   * @return {@code StepObj} with given attributes
   * @throws NullPointerException if {@code attributes} arg is null
   */
  S withAttributes(StepAttributes attributes);

  /**
   * Returns {@code true} if given attribute value is present, otherwise returns {@code false}.
   *
   * @param attribute the attribute
   * @return {@code true} if given attribute value is present, otherwise {@code false}
   * @throws NullPointerException if {@code attribute} arg is null
   */
  default boolean contains(final StepAttribute<?> attribute) {
    return this.attributes().contains(attribute);
  }

  /**
   * Returns value of given attribute or default value if attribute is not present.
   *
   * @param attribute the attribute
   * @param <V>       the type of the attribute
   * @return value of given attribute or default value if attribute is not present
   * @throws NullPointerException if {@code attribute} arg is null
   */
  default <V> V get(final StepAttribute<? extends V> attribute) {
    return this.attributes().get(attribute);
  }

  /**
   * Returns {@code StepObj} with given attribute value updated by {@code updater}.
   *
   * @param attribute the attribute
   * @param updater   the updater
   * @param <V>       the type of the attribute
   * @return {@code StepObj} with given attribute value
   * @throws NullPointerException if {@code attribute} arg or {@code updater} arg is null
   */
  default <V> S withUpd(final StepAttribute<V> attribute,
                        final ThrowingConsumer<? super V, ?> updater) {
    return this.withAttributes(this.attributes().withUpd(attribute, updater));
  }

  /**
   * Returns {@code StepObj} with given attribute value created by {@code generator}.
   *
   * @param attribute the attribute
   * @param generator the generator
   * @param <V>       the type of the attribute
   * @return {@code StepObj} with given attribute value
   * @throws NullPointerException if {@code attribute} arg or {@code generator} arg is null or if {@code attribute} is
   *                              not nullable and {@code generator} result is null
   */
  default <V> S withNew(final StepAttribute<V> attribute,
                        final ThrowingFunction<? super V, ? extends V, ?> generator) {
    return this.withAttributes(this.attributes().withNew(attribute, generator));
  }

  /**
   * Returns {@code StepObj} with given attribute value.
   *
   * @param attribute the attribute
   * @param value     the attribute value
   * @param <V>       the type of the attribute
   * @return {@code StepObj} with given attribute value
   * @throws NullPointerException if {@code attribute} arg is null or if {@code attribute} is not nullable and
   *                              {@code value} is null
   */
  default <V> S with(final StepAttribute<V> attribute,
                     final V value) {
    return this.withAttributes(this.attributes().with(
      attribute, value
    ));
  }

  /**
   * Returns {@code StepObj} with given attributes values.
   *
   * @param attribute1 the first attribute
   * @param value1     the first attribute value
   * @param attribute2 the second attribute
   * @param value2     the second attribute value
   * @param <V1>       the type of the first attribute
   * @param <V2>       the type of the second attribute
   * @return {@code StepObj} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg is null or if {@code attribute1}
   *                              is not nullable and {@code value1} is null or if {@code attribute2} is not nullable
   *                              and {@code value2} is null
   */
  default <V1, V2> S with(final StepAttribute<V1> attribute1,
                          final V1 value1,
                          final StepAttribute<V2> attribute2,
                          final V2 value2) {
    return this.withAttributes(this.attributes().with(
      attribute1, value1,
      attribute2, value2
    ));
  }

  /**
   * Returns {@code StepObj} with given attributes values.
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
   * @return {@code StepObj} with given attributes values
   * @throws NullPointerException if {@code attribute1} arg or {@code attribute2} arg or {@code attribute3} arg is null
   *                              or if {@code attribute1} is not nullable and {@code value1} is null or if
   *                              {@code attribute2} is not nullable and {@code value2} is null or if {@code attribute3}
   *                              is not nullable and {@code value3} is null
   */
  default <V1, V2, V3> S with(final StepAttribute<V1> attribute1,
                              final V1 value1,
                              final StepAttribute<V2> attribute2,
                              final V2 value2,
                              final StepAttribute<V3> attribute3,
                              final V3 value3) {
    return this.withAttributes(this.attributes().with(
      attribute1, value1,
      attribute2, value2,
      attribute3, value3
    ));
  }

  /**
   * Returns {@code StepObj} without given attribute.
   *
   * @param attribute the attribute
   * @return {@code StepObj} without given attribute
   * @throws NullPointerException if {@code attribute} arg is null
   */
  default S without(final StepAttribute<?> attribute) {
    return this.withAttributes(this.attributes().without(attribute));
  }

  /**
   * Returns keyword attribute value.
   *
   * @return keyword attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#KEYWORD
   */
  default Keyword getKeyword() {
    return this.get(KEYWORD);
  }

  /**
   * Returns name attribute value.
   *
   * @return name attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#NAME
   */
  default String getName() {
    return this.get(NAME);
  }

  /**
   * Returns comment attribute value.
   *
   * @return comment attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#COMMENT
   */
  default String getComment() {
    return this.get(COMMENT);
  }

  /**
   * Returns params attribute value.
   *
   * @return params attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#PARAMS
   */
  default Map<String, Object> getParams() {
    return this.get(PARAMS);
  }

  /**
   * Returns hidden attribute value.
   *
   * @return hidden attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#HIDDEN
   */
  default boolean getHidden() {
    return this.get(HIDDEN);
  }

  /**
   * Returns expected result attribute value.
   *
   * @return expected result attribute value
   * @see #get(StepAttribute)
   * @see StepAttribute#EXPECTED_RESULT
   */
  default String getExpectedResult() {
    return this.get(EXPECTED_RESULT);
  }

  /**
   * Returns {@code StepObj} with given keyword attribute value.
   *
   * @param keyword the keyword attribute value
   * @return {@code StepObj} with given keyword attribute value
   * @throws NullPointerException if {@code keyword} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#KEYWORD
   */
  default S withKeyword(final Keyword keyword) {
    return this.with(KEYWORD, keyword);
  }

  /**
   * Returns {@code StepObj} with given keyword attribute value created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code StepObj} with given keyword attribute value
   * @throws NullPointerException {@code generator} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#KEYWORD
   */
  default S withNewKeyword(final ThrowingFunction<? super Keyword, Keyword, ?> generator) {
    return this.withNew(KEYWORD, generator);
  }

  /**
   * Returns {@code StepObj} with given name attribute value.
   *
   * @param name the name attribute value
   * @return {@code StepObj} with given name attribute value
   * @throws NullPointerException if {@code name} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#NAME
   */
  default S withName(final String name) {
    return this.with(NAME, name);
  }

  /**
   * Returns {@code StepObj} with given name attribute value created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code StepObj} with given name attribute value
   * @throws NullPointerException {@code generator} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#NAME
   */
  default S withNewName(final ThrowingFunction<? super String, String, ?> generator) {
    return this.withNew(NAME, generator);
  }

  /**
   * Returns {@code StepObj} with given params attribute value.
   *
   * @param params the params attribute value
   * @return {@code StepObj} with given params attribute value
   * @throws NullPointerException if {@code params} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#PARAMS
   */
  default S withParams(final Map<String, Object> params) {
    return this.with(PARAMS, params);
  }

  /**
   * Returns {@code StepObj} with given params attribute value updated by {@code updater}.
   *
   * @param updater the updater
   * @return {@code StepObj} with given params attribute value
   * @throws NullPointerException if {@code updater} arg is null
   * @see #withUpd(StepAttribute, ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withUpdParams(final ThrowingConsumer<? super Map<String, Object>, ?> updater) {
    return this.withUpd(PARAMS, updater);
  }

  /**
   * Returns {@code StepObj} with given params attribute value created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code StepObj} with given params attribute value
   * @throws NullPointerException {@code generator} arg is null
   * @see #withNew(StepAttribute, ThrowingFunction)
   * @see StepAttribute#PARAMS
   */
  @SuppressWarnings("unchecked")
  default S withNewParams(final ThrowingFunction<? super Map<String, Object>, ? extends Map<String, ?>, ?> generator) {
    return this.withNew(
      PARAMS,
      (ThrowingFunction<Map<String, Object>, Map<String, Object>, ?>) generator
    );
  }

  /**
   * Returns {@code StepObj} with added to params attribute value param. Alias for
   * {@link #withAddedParam(String, Object)} method.
   *
   * @param name  the param name
   * @param value the param value
   * @return {@code StepObj} with added param
   * @see #withUpdParams(ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withParam(final String name,
                      final Object value) {
    return this.withAddedParam(name, value);
  }

  /**
   * Returns {@code StepObj} with added to params attribute value param.
   *
   * @param name  the param name
   * @param value the param value
   * @return {@code StepObj} with added param
   * @see #withUpdParams(ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withAddedParam(final String name,
                           final Object value) {
    return this.withUpdParams(p -> p.put(name, value));
  }

  /**
   * Returns {@code StepObj} with added to params attribute value params.
   *
   * @param params the params
   * @return {@code StepObj} with added params
   * @throws NullPointerException if {@code params} arg is null
   * @see #withUpdParams(ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withAddedParams(final Map<String, Object> params) {
    return this.withUpdParams(p -> p.putAll(params));
  }

  /**
   * Returns {@code StepObj} with given expected result attribute value.
   *
   * @param expectedResult the expected result attribute value
   * @return {@code StepObj} with given expected result attribute value
   * @throws NullPointerException if {@code expectedResult} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#EXPECTED_RESULT
   */
  default S withExpectedResult(final String expectedResult) {
    return this.with(EXPECTED_RESULT, expectedResult);
  }

  /**
   * Returns {@code StepObj} with given expected result attribute value created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code StepObj} with given expected result attribute value
   * @throws NullPointerException {@code generator} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#EXPECTED_RESULT
   */
  default S withNewExpectedResult(final ThrowingFunction<? super String, String, ?> generator) {
    return this.withNew(EXPECTED_RESULT, generator);
  }

  /**
   * Returns {@code StepObj} with given comment attribute value.
   *
   * @param comment the comment attribute value
   * @return {@code StepObj} with given comment attribute value
   * @throws NullPointerException if {@code comment} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#COMMENT
   */
  default S withComment(final String comment) {
    return this.with(COMMENT, comment);
  }

  /**
   * Returns {@code StepObj} with given comment attribute value created by {@code generator}.
   *
   * @param generator the generator
   * @return {@code StepObj} with given comment attribute value
   * @throws NullPointerException {@code generator} arg is null
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#COMMENT
   */
  default S withNewComment(final ThrowingFunction<? super String, String, ?> generator) {
    return this.withNew(COMMENT, generator);
  }

  /**
   * Returns {@code StepObj} with hidden attribute value.
   *
   * @param hidden the hidden attribute value
   * @return {@code StepObj} with given hidden attribute value
   * @see #with(StepAttribute, Object)
   * @see StepAttribute#HIDDEN
   */
  default S withHidden(final boolean hidden) {
    return this.with(HIDDEN, hidden);
  }

  /**
   * Returns {@code StepObj} without keyword attribute.
   *
   * @return {@code StepObj} without keyword attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#KEYWORD
   */
  default S withoutKeyword() {
    return this.without(KEYWORD);
  }

  /**
   * Returns {@code StepObj} without name attribute.
   *
   * @return {@code StepObj} without name attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#NAME
   */
  default S withoutName() {
    return this.without(NAME);
  }

  /**
   * Returns {@code StepObj} without params attribute.
   *
   * @return {@code StepObj} without params attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#PARAMS
   */
  default S withoutParams() {
    return this.without(PARAMS);
  }

  /**
   * Returns {@code StepObj} without given params.
   *
   * @param paramNames the param names
   * @return {@code StepObj} without given params
   * @throws NullPointerException if {@code paramNames} arg is null
   * @see #withUpdParams(ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withoutParams(final String... paramNames) {
    return this.withUpdParams(params -> {
      for (final String paramName : paramNames) {
        params.remove(paramName);
      }
    });
  }

  /**
   * Returns {@code StepObj} without given param.
   *
   * @param paramName the param name
   * @return {@code StepObj} without given param
   * @see #withUpdParams(ThrowingConsumer)
   * @see StepAttribute#PARAMS
   */
  default S withoutParam(final String paramName) {
    return this.withUpdParams(params -> params.remove(paramName));
  }

  /**
   * Returns {@code StepObj} without expected result attribute.
   *
   * @return {@code StepObj} without expected result attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#COMMENT
   */
  default S withoutExpectedResult() {
    return this.without(EXPECTED_RESULT);
  }

  /**
   * Returns {@code StepObj} without comment attribute.
   *
   * @return {@code StepObj} without comment attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#COMMENT
   */
  default S withoutComment() {
    return this.without(COMMENT);
  }

  /**
   * Returns {@code StepObj} without hidden attribute.
   *
   * @return {@code StepObj} without hidden attribute
   * @see #without(StepAttribute)
   * @see StepAttribute#HIDDEN
   */
  default S withoutHidden() {
    return this.without(HIDDEN);
  }
}
