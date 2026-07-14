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
    final SimpleStepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(nullType, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf4ArgsShouldThrowExceptionForNullAttributeType() {
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable("abc");
    final SimpleStepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(nullType, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(type, value, nullType, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf6ArgsShouldThrowExceptionForNullAttributeType() {
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable("abc");
    final SimpleStepAttribute<Object> nullType = null;
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
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nonNull(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    assertThat(attributes.get(type))
      .isSameAs(value);
  }

  @Test
  void getMethodShouldReturnDefaultValueIfValueNotExist() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nonNull(key, defaultValue);
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.get(type))
      .isSameAs(defaultValue);
  }

  @Test
  void getMethodShouldReturnDefaultValueForNullableTypeIfValueNotExist() {
    final String key = "abc";
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key);
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.get(type))
      .isNull();
  }

  @Test
  void getMethodShouldReturnValueForNullableTypeIfValueExist() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    assertThat(attributes.get(type))
      .isSameAs(value);
  }

  @Test
  void withMethodOf2ArgsShouldAddAttribute() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key, defaultValue);
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
    final SimpleStepAttribute<Object> type1 = SimpleStepAttribute.nullable(key1, defaultValue1);
    final Object value1 = new Object();
    final String key2 = "abc2";
    final Object defaultValue2 = new Object();
    final SimpleStepAttribute<Object> type2 = SimpleStepAttribute.nullable(key2, defaultValue2);
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
    final SimpleStepAttribute<Object> type1 = SimpleStepAttribute.nullable(key1, defaultValue1);
    final Object value1 = new Object();
    final String key2 = "abc2";
    final Object defaultValue2 = new Object();
    final SimpleStepAttribute<Object> type2 = SimpleStepAttribute.nullable(key2, defaultValue2);
    final Object value2 = new Object();
    final String key3 = "abc3";
    final Object defaultValue3 = new Object();
    final SimpleStepAttribute<Object> type3 = SimpleStepAttribute.nullable(key3, defaultValue3);
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
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key, defaultValue);
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
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key, defaultValue);
    final Object value = new Object();
    final StepAttributes.Builder builder = new StepAttributes.BuilderOf();

    builder.add(type, value);
    final StepAttributes result = builder.build();
    assertThat(result.get(type))
      .isSameAs(value);
  }

  @Test
  void containsMethodShouldReturnTrueIfAttributePresent() {
    final String key = "abc";
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    assertThat(attributes.contains(type))
      .isTrue();
  }

  @Test
  void containsMethodShouldReturnFalseIfAttributeNotPresent() {
    final String key = "abc";
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key);
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.contains(type))
      .isFalse();
  }

  @Test
  void containsMethodShouldThrowExceptionForNullAttribute() {
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.contains(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withoutMethodShouldRemoveAttribute() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nonNull(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    final StepAttributes resultAttributes = attributes.without(type);

    assertThat(resultAttributes.contains(type))
      .isFalse();
    assertThat(resultAttributes.get(type))
      .isSameAs(defaultValue);
  }

  @Test
  void withoutMethodShouldThrowExceptionForNullAttribute() {
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.without(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withMethodOf2ArgsShouldThrowExceptionForNullAttribute() {
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.with(null, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withMethodOf4ArgsShouldThrowExceptionForNullAttribute() {
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable("abc");
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.with(null, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> attributes.with(type, value, null, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withMethodOf6ArgsShouldThrowExceptionForNullAttribute() {
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable("abc");
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.with(null, value, type, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> attributes.with(type, value, null, value, type, value))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> attributes.with(type, value, type, value, null, value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void getMethodShouldThrowExceptionForNullAttribute() {
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.get(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withMethodShouldReplaceExistingAttribute() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable(key, defaultValue);
    final Object value1 = new Object();
    final Object value2 = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value1);

    final StepAttributes resultAttributes = attributes.with(type, value2);

    assertThat(resultAttributes.get(type))
      .isSameAs(value2);
  }

  @Test
  void multipleWithCallsShouldChain() {
    final SimpleStepAttribute<String> type1 = SimpleStepAttribute.nullable("key1");
    final SimpleStepAttribute<String> type2 = SimpleStepAttribute.nullable("key2");
    final SimpleStepAttribute<String> type3 = SimpleStepAttribute.nullable("key3");

    final StepAttributes attributes = new StepAttributes.Of()
      .with(type1, "value1")
      .with(type2, "value2")
      .with(type3, "value3");

    assertThat(attributes.get(type1))
      .isEqualTo("value1");
    assertThat(attributes.get(type2))
      .isEqualTo("value2");
    assertThat(attributes.get(type3))
      .isEqualTo("value3");
  }

  @Test
  void builderShouldCollectMultipleAttributes() {
    final SimpleStepAttribute<String> type1 = SimpleStepAttribute.nullable("key1");
    final SimpleStepAttribute<String> type2 = SimpleStepAttribute.nullable("key2");
    final SimpleStepAttribute<String> type3 = SimpleStepAttribute.nullable("key3");
    final StepAttributes.Builder builder = new StepAttributes.BuilderOf();

    builder.add(type1, "value1");
    builder.add(type2, "value2");
    builder.add(type3, "value3");
    final StepAttributes result = builder.build();

    assertThat(result.get(type1))
      .isEqualTo("value1");
    assertThat(result.get(type2))
      .isEqualTo("value2");
    assertThat(result.get(type3))
      .isEqualTo("value3");
  }

  @Test
  void asBuilderMethodShouldCreateBuilderWithExistingAttributes() {
    final SimpleStepAttribute<String> type1 = SimpleStepAttribute.nullable("key1");
    final SimpleStepAttribute<String> type2 = SimpleStepAttribute.nullable("key2");
    final StepAttributes attributes = new StepAttributes.Of(type1, "value1", type2, "value2");

    final StepAttributes result = attributes.asBuilder()
      .build();

    assertThat(result.get(type1))
      .isEqualTo("value1");
    assertThat(result.get(type2))
      .isEqualTo("value2");
  }

  @Test
  void asBuilderMethodShouldAllowModifyingAttributesWithoutModifyingOriginal() {
    final SimpleStepAttribute<String> type1 = SimpleStepAttribute.nullable("key1");
    final SimpleStepAttribute<String> type2 = SimpleStepAttribute.nullable("key2");
    final StepAttributes attributes = new StepAttributes.Of(type1, "value1");

    final StepAttributes result = attributes.asBuilder()
      .add(type2, "value2")
      .build();

    assertThat(attributes.contains(type2))
      .isFalse();
    assertThat(result.contains(type2))
      .isTrue();
  }

  @Test
  void staticOfMethodOf2ArgsShouldCreateAttributesWithSingleAttribute() {
    final SimpleStepAttribute<String> type = SimpleStepAttribute.nullable("key");
    final String value = "value";

    final StepAttributes attributes = StepAttributes.of(type, value);

    assertThat(attributes.get(type))
      .isEqualTo(value);
  }

  @Test
  void staticOfMethodOf4ArgsShouldCreateAttributesWithTwoAttributes() {
    final SimpleStepAttribute<String> type1 = SimpleStepAttribute.nullable("key1");
    final SimpleStepAttribute<String> type2 = SimpleStepAttribute.nullable("key2");

    final StepAttributes attributes = StepAttributes.of(type1, "value1", type2, "value2");

    assertThat(attributes.get(type1))
      .isEqualTo("value1");
    assertThat(attributes.get(type2))
      .isEqualTo("value2");
  }

  @Test
  void withDefaultAttributeValueMethodShouldReturnAttributesWithDefaultValue() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nonNull(key, defaultValue);
    final Object value = new Object();
    final StepAttributes attributes = new StepAttributes.Of(type, value);

    final StepAttributes resultAttributes = attributes.with(type);

    assertThat(resultAttributes.get(type))
      .isSameAs(defaultValue);
  }

  @Test
  void withDefaultAttributeValueMethodShouldThrowExceptionForNullAttribute() {
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatCode(() -> attributes.with(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyAttributesShouldReturnDefaults() {
    final SimpleStepAttribute<String> type = SimpleStepAttribute.nonNull("key", "default");
    final StepAttributes attributes = new StepAttributes.Of();

    assertThat(attributes.get(type))
      .isEqualTo("default");
  }

  @Test
  void attributesShouldPreserveValueTypes() {
    final SimpleStepAttribute<Integer> intType = SimpleStepAttribute.nullable("int:key");
    final SimpleStepAttribute<String> stringType = SimpleStepAttribute.nullable("string:key");
    final Integer intValue = 42;
    final String stringValue = "test";
    final StepAttributes attributes = new StepAttributes.Of(intType, intValue, stringType, stringValue);

    assertThat(attributes.get(intType))
      .isEqualTo(intValue);
    assertThat(attributes.get(stringType))
      .isEqualTo(stringValue);
  }

  @Test
  void attributesWithMapValueShouldPreserveMapContent() {
    final SimpleStepAttribute<java.util.Map<String, Object>> mapType =
      SimpleStepAttribute.nullable("map:key");
    final java.util.Map<String, Object> mapValue = new java.util.HashMap<>();
    mapValue.put("key1", "value1");
    mapValue.put("key2", 42);

    final StepAttributes attributes = new StepAttributes.Of(mapType, mapValue);

    final java.util.Map<String, Object> result = attributes.get(mapType);
    assertThat(result)
      .containsEntry("key1", "value1")
      .containsEntry("key2", 42);
  }

  @Test
  void ctorOf8ArgsShouldCreateAttributesWithFourAttributes() {
    final SimpleStepAttribute<Object> type1 = SimpleStepAttribute.nullable("abc1");
    final SimpleStepAttribute<Object> type2 = SimpleStepAttribute.nullable("abc2");
    final SimpleStepAttribute<Object> type3 = SimpleStepAttribute.nullable("abc3");
    final SimpleStepAttribute<Object> type4 = SimpleStepAttribute.nullable("abc4");
    final Object value1 = new Object();
    final Object value2 = new Object();
    final Object value3 = new Object();
    final Object value4 = new Object();

    final StepAttributes attributes = new StepAttributes.Of(
      type1, value1, type2, value2, type3, value3, type4, value4
    );

    assertThat(attributes.get(type1))
      .isSameAs(value1);
    assertThat(attributes.get(type2))
      .isSameAs(value2);
    assertThat(attributes.get(type3))
      .isSameAs(value3);
    assertThat(attributes.get(type4))
      .isSameAs(value4);
  }

  @Test
  void ctorOf8ArgsShouldThrowExceptionForNullAttributeType() {
    final SimpleStepAttribute<Object> type = SimpleStepAttribute.nullable("abc");
    final SimpleStepAttribute<Object> nullType = null;
    final Object value = new Object();

    assertThatCode(() -> new StepAttributes.Of(
      nullType, value, type, value, type, value, type, value
    ))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(
      type, value, nullType, value, type, value, type, value
    ))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(
      type, value, type, value, nullType, value, type, value
    ))
      .isInstanceOf(NullPointerException.class);
    assertThatCode(() -> new StepAttributes.Of(
      type, value, type, value, type, value, nullType, value
    ))
      .isInstanceOf(NullPointerException.class);
  }
}
