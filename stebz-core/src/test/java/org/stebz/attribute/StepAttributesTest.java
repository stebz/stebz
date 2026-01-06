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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link StepAttributes}.
 */
final class StepAttributesTest {

  @Test
  void ctorOf0ArgsShouldNotThrowAnyException() {
    assertThatCode(() -> new StepAttributes.Of())
      .doesNotThrowAnyException();
  }

  @Test
  void ctorOf2ArgsShouldThrowExceptionForNullAttributeType() {
    final StepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(nullType, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf4ArgsShouldThrowExceptionForNullAttributeType() {
    final StepAttribute<Object> type = StepAttribute.nullable("abc");
    final StepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(nullType, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(type, value, nullType, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf6ArgsShouldThrowExceptionForNullAttributeType() {
    final StepAttribute<Object> type = StepAttribute.nullable("abc");
    final StepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(nullType, value, type, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(type, value, nullType, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(type, value, type, value, nullType, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void getMethodShouldReturnValueIfExist() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nonNull(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    assertThat(attributes.get(type))
      .isSameAs(value);
  }

  @Test
  void getMethodShouldReturnDefaultValueIfValueNotExist() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nonNull(key, defaultValue);
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.get(type))
      .isSameAs(defaultValue);
  }

  @Test
  void getMethodShouldReturnDefaultValueForNullableTypeIfValueNotExist() {
    final String key = "abc";
    final StepAttribute<Object> type = StepAttribute.nullable(key);
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.get(type))
      .isNull();
  }

  @Test
  void getMethodShouldReturnValueForNullableTypeIfValueExist() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    assertThat(attributes.get(type))
      .isSameAs(value);
  }

  @Test
  void withMethodOf2ArgsShouldAddAttribute() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    final StepAttributes resultAttributes = attributes.with(type, value);
    assertThat(resultAttributes.get(type))
      .isSameAs(value);
  }

  @Test
  void withMethodOf4ArgsShouldAddAttributes() {
    final String key1 = "abc1";
    final Object defaultValue1 = new Object();
    final StepAttribute<Object> type1 = StepAttribute.nullable(key1, defaultValue1);
    final Object value1 = new Object();
    final String key2 = "abc2";
    final Object defaultValue2 = new Object();
    final StepAttribute<Object> type2 = StepAttribute.nullable(key2, defaultValue2);
    final Object value2 = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    final StepAttributes resultAttributes = attributes.with(type1, value1, type2, value2);
    assertThat(resultAttributes.get(type1))
      .isSameAs(value1);
    assertThat(resultAttributes.get(type2))
      .isSameAs(value2);
  }

  @Test
  void withMethodOf6ArgsShouldAddAttributes() {
    final String key1 = "abc1";
    final Object defaultValue1 = new Object();
    final StepAttribute<Object> type1 = StepAttribute.nullable(key1, defaultValue1);
    final Object value1 = new Object();
    final String key2 = "abc2";
    final Object defaultValue2 = new Object();
    final StepAttribute<Object> type2 = StepAttribute.nullable(key2, defaultValue2);
    final Object value2 = new Object();
    final String key3 = "abc3";
    final Object defaultValue3 = new Object();
    final StepAttribute<Object> type3 = StepAttribute.nullable(key3, defaultValue3);
    final Object value3 = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    final StepAttributes resultAttributes = attributes.with(type1, value1, type2, value2, type3, value3);
    assertThat(resultAttributes.get(type1))
      .isSameAs(value1);
    assertThat(resultAttributes.get(type2))
      .isSameAs(value2);
    assertThat(resultAttributes.get(type3))
      .isSameAs(value3);
  }

  @Test
  void builderMethodShouldReturnBuilderOfCurrentAttributes() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    final StepAttributes result = attributes.asBuilder().build();
    assertThat(result.get(type))
      .isSameAs(value);
  }

  @Test
  void builderShouldCollectAttributes() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> type = StepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes.Builder builder = new StepAttributes.BuilderOf();

    builder.add(type, value);
    final StepAttributes result = builder.build();
    assertThat(result.get(type))
      .isSameAs(value);
  }
}
