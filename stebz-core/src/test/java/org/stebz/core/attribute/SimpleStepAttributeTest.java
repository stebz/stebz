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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SimpleStepAttribute}.
 */
final class SimpleStepAttributeTest {

  @Test
  void nullableWithoutDefaultValueShouldCreateAttributeWithNullDefault() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key");

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.defaultInputValue()).isNull();
    assertThat(attribute.defaultOutputValue()).isNull();
  }

  @Test
  void nullableShouldThrowExceptionForNullKey() {
    assertThatThrownBy(() -> SimpleStepAttribute.nullable((String) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableWithDefaultValueShouldCreateAttributeWithGivenDefault() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key", defaultValue);

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.defaultInputValue()).isSameAs(defaultValue);
    assertThat(attribute.defaultOutputValue()).isSameAs(defaultValue);
  }

  @Test
  void nullableWithDefaultValueShouldThrowExceptionForNullKey() {
    assertThatThrownBy(() -> SimpleStepAttribute.nullable((String) null, "default"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableWithExtractOutputFunctionShouldApplyFunction() {
    final SimpleStepAttribute<String> attribute =
      SimpleStepAttribute.nullable("test:key", "default", v -> "[" + v + "]");

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.extractOutputValue("test")).isEqualTo("[test]");
  }

  @Test
  void nullableWithExtractOutputFunctionShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nullable((String) null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nullable("test:key", "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullShouldCreateAttributeWithGivenDefault() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull("test:key", defaultValue);

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.defaultInputValue()).isSameAs(defaultValue);
    assertThat(attribute.defaultOutputValue()).isSameAs(defaultValue);
  }

  @Test
  void nonNullShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull((String) null, "default"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull("test:key", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullWithExtractOutputFunctionShouldApplyFunction() {
    final SimpleStepAttribute<String> attribute =
      SimpleStepAttribute.nonNull("test:key", "default", v -> "[" + v + "]");

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.extractOutputValue("test")).isEqualTo("[test]");
  }

  @Test
  void nonNullWithExtractOutputFunctionShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull((String) null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull("test:key", null, v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull("test:key", "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableExtractInputValueShouldReturnValueAsIs() {
    assertThat(SimpleStepAttribute.nullable("test:key").extractInputValue("value")).isEqualTo("value");
  }

  @Test
  void nullableExtractInputValueShouldAcceptNull() {
    assertThat(SimpleStepAttribute.nullable("test:key").extractInputValue(null)).isNull();
  }

  @Test
  void nonNullExtractInputValueShouldReturnValue() {
    assertThat(SimpleStepAttribute.nonNull("test:key", "default").extractInputValue("value"))
      .isEqualTo("value");
  }

  @Test
  void nonNullExtractInputValueShouldThrowExceptionForNull() {
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull("test:key", "default").extractInputValue(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("attribute value is null");
  }

  @Test
  void nullableExtractOutputValueShouldReturnValueAsIs() {
    assertThat(SimpleStepAttribute.nullable("test:key").extractOutputValue("value")).isEqualTo("value");
    assertThat(SimpleStepAttribute.nullable("test:key").extractOutputValue(null)).isNull();
  }

  @Test
  void nonNullExtractOutputValueShouldReturnValue() {
    assertThat(SimpleStepAttribute.nonNull("test:key", "default").extractOutputValue("value"))
      .isEqualTo("value");
  }

  @Test
  void extractOutputValueFunctionMayThrowException() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable(
      "test:key", "default", v -> {
        if (v.equals("error")) {
          throw new IllegalStateException("expected error");
        }
        return v.toUpperCase();
      });

    assertThat(attribute.extractOutputValue("test")).isEqualTo("TEST");
    assertThatThrownBy(() -> attribute.extractOutputValue("error"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("expected error");
  }

  @Test
  void equalsShouldBeBasedOnKey() {
    assertThat(SimpleStepAttribute.nullable("test:key"))
      .isEqualTo(SimpleStepAttribute.nonNull("test:key", "default"))
      .isEqualTo(SimpleStepAttribute.nullable("test:key", "other default"))
      .isNotEqualTo(SimpleStepAttribute.nullable("test:key2"))
      .isNotEqualTo("not an attribute");
  }

  @Test
  void nullableWithClassKeyShouldUseClassNameAsKey() {
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable(SimpleStepAttribute.class);

    assertThat(attribute.key()).isEqualTo("org.stebz.core.attribute.SimpleStepAttribute");
    assertThat(attribute.defaultInputValue()).isNull();
    assertThat(attribute.defaultOutputValue()).isNull();
  }

  @Test
  void nullableWithClassKeyShouldThrowExceptionForNullClass() {
    assertThatThrownBy(() -> SimpleStepAttribute.nullable((Class<?>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nullableWithClassKeyAndDefaultValueShouldCreateAttributeWithGivenDefault() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nullable(String.class, "default");

    assertThat(attribute.key()).isEqualTo("java.lang.String");
    assertThat(attribute.defaultInputValue()).isEqualTo("default");
    assertThat(attribute.defaultOutputValue()).isEqualTo("default");
  }

  @Test
  void nullableWithClassKeyAndExtractOutputFunctionShouldApplyFunction() {
    final SimpleStepAttribute<String> attribute =
      SimpleStepAttribute.nullable(String.class, "default", v -> "[" + v + "]");

    assertThat(attribute.key()).isEqualTo("java.lang.String");
    assertThat(attribute.extractOutputValue("test")).isEqualTo("[test]");
  }

  @Test
  void nullableWithClassKeyAndExtractOutputFunctionShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nullable((Class<?>) null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nullable(String.class, "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullWithClassKeyShouldCreateAttributeWithGivenDefault() {
    final SimpleStepAttribute<String> attribute = SimpleStepAttribute.nonNull(String.class, "default");

    assertThat(attribute.key()).isEqualTo("java.lang.String");
    assertThat(attribute.defaultInputValue()).isEqualTo("default");
    assertThat(attribute.defaultOutputValue()).isEqualTo("default");
  }

  @Test
  void nonNullWithClassKeyShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull((Class<?>) null, "default"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull(String.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void nonNullWithClassKeyAndExtractOutputFunctionShouldApplyFunction() {
    final SimpleStepAttribute<Integer> attribute =
      SimpleStepAttribute.nonNull(Integer.class, 0, v -> v * 2);

    assertThat(attribute.key()).isEqualTo("java.lang.Integer");
    assertThat(attribute.extractOutputValue(21)).isEqualTo(42);
  }

  @Test
  void nonNullWithClassKeyAndExtractOutputFunctionShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull((Class<?>) null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull(String.class, null, v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> SimpleStepAttribute.nonNull(String.class, "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void attributeCreatedWithClassKeyShouldBeEqualToAttributeCreatedWithSameClassName() {
    assertThat(SimpleStepAttribute.nullable(String.class))
      .isEqualTo(SimpleStepAttribute.nullable("java.lang.String"))
      .hasSameHashCodeAs(SimpleStepAttribute.nullable("java.lang.String"))
      .hasToString("Attribute[java.lang.String]");
  }

  @Test
  void classKeyCtorsShouldUseClassNameAsKey() {
    assertThat(new SimpleStepAttribute.Nullable<>(String.class).key()).isEqualTo("java.lang.String");
    assertThat(new SimpleStepAttribute.Nullable<>(String.class, "default").key()).isEqualTo("java.lang.String");
    assertThat(new SimpleStepAttribute.NonNull<>(String.class, "default").key()).isEqualTo("java.lang.String");
  }

  @Test
  void classKeyCtorsShouldThrowExceptionForNullClass() {
    assertThatThrownBy(() -> new SimpleStepAttribute.Nullable<Object>((Class<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new SimpleStepAttribute.Nullable<>((Class<?>) null, "default"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new SimpleStepAttribute.NonNull<>((Class<?>) null, "default"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void hashCodeShouldBeBasedOnKey() {
    assertThat(SimpleStepAttribute.nullable("test:key"))
      .hasSameHashCodeAs(SimpleStepAttribute.nullable("test:key", "default"))
      .doesNotHaveSameHashCodeAs(SimpleStepAttribute.nullable("test:key2"));
  }

  @Test
  void toStringShouldContainKey() {
    assertThat(SimpleStepAttribute.nullable("test:key")).hasToString("Attribute[test:key]");
    assertThat(SimpleStepAttribute.nonNull("test:key", "default")).hasToString("Attribute[test:key]");
  }

  @Test
  void equalsShouldBeTrueForSameInstance() {
    final SimpleStepAttribute<String> nullableAttribute = SimpleStepAttribute.nullable("test:key");
    final SimpleStepAttribute<String> nonNullAttribute = SimpleStepAttribute.nonNull("test:key", "default");

    assertThat(nullableAttribute).isEqualTo(nullableAttribute);
    assertThat(nonNullAttribute).isEqualTo(nonNullAttribute);
  }

  @Test
  void equalsShouldBeFalseForNullOrDifferentTypeObject() {
    assertThat(SimpleStepAttribute.nullable("test:key")).isNotEqualTo(null);
    assertThat(SimpleStepAttribute.nonNull("test:key", "default")).isNotEqualTo(null);
    assertThat(SimpleStepAttribute.nonNull("test:key", "default")).isNotEqualTo(42);
  }

  @Test
  void nonNullEqualsShouldBeBasedOnKey() {
    assertThat(SimpleStepAttribute.nonNull("test:key", "default"))
      .isEqualTo(SimpleStepAttribute.nonNull("test:key", "other default"))
      .isEqualTo(SimpleStepAttribute.nullable("test:key"))
      .isNotEqualTo(SimpleStepAttribute.nonNull("test:key2", "default"))
      .hasSameHashCodeAs(SimpleStepAttribute.nonNull("test:key", "other default"));
  }

  @Test
  void nonNullWithDefaultValueExtractOutputValueShouldReturnValueAsIs() {
    assertThat(SimpleStepAttribute.nonNull("test:key", "default").extractOutputValue("value"))
      .isEqualTo("value");
  }

  @Test
  void nullableWithDefaultValueExtractOutputValueShouldReturnValueAsIs() {
    assertThat(SimpleStepAttribute.nullable("test:key", "default").extractOutputValue("value"))
      .isEqualTo("value");
    assertThat(SimpleStepAttribute.nullable("test:key", "default").extractOutputValue(null))
      .isNull();
  }
}
