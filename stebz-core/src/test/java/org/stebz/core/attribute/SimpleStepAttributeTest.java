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
 * Tests for {@link SimpleStepAttribute}.
 */
final class SimpleStepAttributeTest {

  @Test
  void nullableMethodShouldCreateAttributeWithNullDefaultValue() {
    final String key = "test:key";
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable(key);

    assertThat(attribute.key())
      .isEqualTo(key);
    assertThat(attribute.defaultOutputValue())
      .isNull();
  }

  @Test
  void nullableMethodShouldThrowExceptionForNullKey() {
    assertThatCode(() -> SimpleStepAttribute.nullable(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableMethodWithDefaultValueShouldCreateAttributeWithGivenDefaultValue() {
    final String key = "test:key";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable(key, defaultValue);

    assertThat(attribute.key())
      .isEqualTo(key);
    assertThat(attribute.defaultOutputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void nullableMethodWithDefaultValueShouldThrowExceptionForNullKey() {
    final Object defaultValue = new Object();

    assertThatCode(() -> SimpleStepAttribute.nullable(null, defaultValue))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableMethodWithExtractOutputFunctionShouldApplyFunction() {
    final String key = "test:key";
    final Object defaultValue = "default";
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable(
      key,
      defaultValue.toString(),
      v -> "[" + v + "]"
    );

    final String result = attribute.extractOutputValue("test");

    assertThat(result)
      .isEqualTo("[test]");
  }

  @Test
  void nullableMethodWithExtractOutputFunctionShouldThrowExceptionForNullKey() {
    assertThatCode(() -> SimpleStepAttribute.nullable(null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableMethodWithExtractOutputFunctionShouldThrowExceptionForNullFunction() {
    assertThatCode(() -> SimpleStepAttribute.nullable("test:key", "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullMethodShouldCreateAttributeWithGivenDefaultValue() {
    final String key = "test:key";
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull(key, defaultValue);

    assertThat(attribute.key())
      .isEqualTo(key);
    assertThat(attribute.defaultOutputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void nonNullMethodShouldThrowExceptionForNullKey() {
    final Object defaultValue = new Object();

    assertThatCode(() -> SimpleStepAttribute.nonNull(null, defaultValue))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullMethodShouldThrowExceptionForNullDefaultValue() {
    assertThatCode(() -> SimpleStepAttribute.nonNull("test:key", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullMethodWithExtractOutputFunctionShouldApplyFunction() {
    final String key = "test:key";
    final String defaultValue = "default";
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nonNull(
      key,
      defaultValue,
      v -> "[" + v + "]"
    );

    final String result = attribute.extractOutputValue("test");

    assertThat(result)
      .isEqualTo("[test]");
  }

  @Test
  void nonNullMethodWithExtractOutputFunctionShouldThrowExceptionForNullKey() {
    assertThatCode(() -> SimpleStepAttribute.nonNull(null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullMethodWithExtractOutputFunctionShouldThrowExceptionForNullDefaultValue() {
    assertThatCode(() -> SimpleStepAttribute.nonNull("test:key", null, v -> v))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullMethodWithExtractOutputFunctionShouldThrowExceptionForNullFunction() {
    assertThatCode(() -> SimpleStepAttribute.nonNull("test:key", "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableAttributeExtractInputValueShouldReturnValue() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable("test:key");
    final String value = "testValue";

    final String result = attribute.extractInputValue(value);

    assertThat(result)
      .isSameAs(value);
  }

  @Test
  void nullableAttributeExtractInputValueShouldAcceptNull() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    final Object result = attribute.extractInputValue(null);

    assertThat(result)
      .isNull();
  }

  @Test
  void nonNullAttributeExtractInputValueShouldReturnValue() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nonNull("test:key", "default");
    final String value = "testValue";

    final String result = attribute.extractInputValue(value);

    assertThat(result)
      .isSameAs(value);
  }

  @Test
  void nonNullAttributeExtractInputValueShouldThrowExceptionForNull() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nonNull("test:key", "default");

    assertThatCode(() -> attribute.extractInputValue(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("attribute value is null");
  }

  @Test
  void nullableAttributeExtractOutputValueShouldReturnValue() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable("test:key");

    final String result = attribute.extractOutputValue("testValue");

    assertThat(result)
      .isEqualTo("testValue");
  }

  @Test
  void nullableAttributeExtractOutputValueShouldReturnNull() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    final Object result = attribute.extractOutputValue(null);

    assertThat(result)
      .isNull();
  }

  @Test
  void nonNullAttributeExtractOutputValueShouldReturnValue() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nonNull("test:key", "default");

    final String result = attribute.extractOutputValue("testValue");

    assertThat(result)
      .isEqualTo("testValue");
  }

  @Test
  void defaultInputValueForNullableAttributeShouldReturnDefaultValue() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key", defaultValue);

    assertThat(attribute.defaultInputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void defaultOutputValueForNullableAttributeShouldReturnDefaultValue() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key", defaultValue);

    assertThat(attribute.defaultOutputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void defaultInputValueForNonNullAttributeShouldReturnDefaultValue() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull("test:key", defaultValue);

    assertThat(attribute.defaultInputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void defaultOutputValueForNonNullAttributeShouldReturnDefaultValue() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull("test:key", defaultValue);

    assertThat(attribute.defaultOutputValue())
      .isSameAs(defaultValue);
  }

  @Test
  void equalsMethodShouldReturnTrueForSameInstance() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    assertThat(attribute)
      .isEqualTo(attribute);
  }

  @Test
  void equalsMethodShouldReturnTrueForAttributesWithSameKey() {
    final String key = "test:key";
    final SimpleStepAttribute<Object> attribute1 = SimpleStepAttribute.nullable(key);
    final SimpleStepAttribute<Object> attribute2 = SimpleStepAttribute.nonNull(key, new Object());

    assertThat(attribute1)
      .isEqualTo(attribute2);
  }

  @Test
  void equalsMethodShouldReturnFalseForAttributesWithDifferentKeys() {
    final SimpleStepAttribute<Object> attribute1 = SimpleStepAttribute.nullable("test:key1");
    final SimpleStepAttribute<Object> attribute2 = SimpleStepAttribute.nullable("test:key2");

    assertThat(attribute1)
      .isNotEqualTo(attribute2);
  }

  @Test
  void equalsMethodShouldReturnFalseForNonStepAttributeObject() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    assertThat(attribute)
      .isNotEqualTo("not an attribute");
  }

  @Test
  void hashCodeMethodShouldReturnSameValueForAttributesWithSameKey() {
    final String key = "test:key";
    final SimpleStepAttribute<Object> attribute1 = SimpleStepAttribute.nullable(key);
    final SimpleStepAttribute<Object> attribute2 = SimpleStepAttribute.nullable(key);

    assertThat(attribute1.hashCode())
      .isEqualTo(attribute2.hashCode());
  }

  @Test
  void hashCodeMethodShouldReturnDifferentValueForAttributesWithDifferentKeys() {
    final SimpleStepAttribute<Object> attribute1 = SimpleStepAttribute.nullable("test:key1");
    final SimpleStepAttribute<Object> attribute2 = SimpleStepAttribute.nullable("test:key2");

    assertThat(attribute1.hashCode())
      .isNotEqualTo(attribute2.hashCode());
  }

  @Test
  void toStringMethodShouldIncludeKey() {
    final String key = "test:key";
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable(key);

    assertThat(attribute.toString())
      .contains(key);
  }

  @Test
  void nullableAttributeWithCustomExtractOutputFunctionShouldApplyTransformation() {
    final SimpleStepAttribute<Integer> attribute = SimpleStepAttribute.nullable(
      "test:key",
      42,
      v -> v * 2
    );

    final Integer result = attribute.extractOutputValue(21);

    assertThat(result)
      .isEqualTo(42);
  }

  @Test
  void nonNullAttributeWithCustomExtractOutputFunctionShouldApplyTransformation() {
    final SimpleStepAttribute<Integer> attribute = SimpleStepAttribute.nonNull(
      "test:key",
      0,
      v -> v * 2
    );

    final Integer result = attribute.extractOutputValue(21);

    assertThat(result)
      .isEqualTo(42);
  }

  @Test
  void nullableAttributeWithNullDefaultValueShouldHaveNullDefault() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    assertThat(attribute.defaultInputValue())
      .isNull();
    assertThat(attribute.defaultOutputValue())
      .isNull();
  }

  @Test
  void multipleAttributesWithSameKeyButDifferentDefaultsShouldBeEqual() {
    final SimpleStepAttribute<String> attribute1 = SimpleStepAttribute.nullable("test:key", "default1");
    final SimpleStepAttribute<String> attribute2 = SimpleStepAttribute.nullable("test:key", "default2");

    assertThat(attribute1)
      .isEqualTo(attribute2);
    assertThat(attribute1.hashCode())
      .isEqualTo(attribute2.hashCode());
  }

  @Test
  void extractOutputValueWithFunctionShouldHandleException() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable(
      "test:key",
      "default",
      v -> {
        if (v.equals("error")) {
          throw new RuntimeException("Expected error");
        }
        return v.toUpperCase();
      }
    );

    assertThat(attribute.extractOutputValue("test"))
      .isEqualTo("TEST");

    assertThatCode(() -> attribute.extractOutputValue("error"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Expected error");
  }
}

