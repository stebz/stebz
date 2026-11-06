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

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StepAttributes}.
 */
final class StepAttributesTest {
  private static final SimpleStepAttribute<String> ATTR1 = SimpleStepAttribute.nullable("test:key1");
  private static final SimpleStepAttribute<String> ATTR2 = SimpleStepAttribute.nullable("test:key2");
  private static final SimpleStepAttribute<String> ATTR3 = SimpleStepAttribute.nullable("test:key3");
  private static final SimpleStepAttribute<String> ATTR4 = SimpleStepAttribute.nullable("test:key4");

  @Test
  void emptyAttributesShouldReturnAttributeDefaults() {
    final SimpleStepAttribute<String> nonNullAttr = SimpleStepAttribute.nonNull("test:key", "default");
    final SimpleStepAttribute<String> nullableAttr = SimpleStepAttribute.nullable("test:key");
    final StepAttributes attributes = new StepAttributes.Of();

    final String nonNullDefault = attributes.get(nonNullAttr);
    final String nullableDefault = attributes.get(nullableAttr);
    assertThat(nonNullDefault).isEqualTo("default");
    assertThat(nullableDefault).isNull();
  }

  @Test
  void getShouldReturnStoredValue() {
    final Object value = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull("test:key", new Object());

    assertThat(new StepAttributes.Of(attribute, value).get(attribute)).isSameAs(value);
    assertThat(new StepAttributes.Of(ATTR1, "value1", ATTR2, "value2").get(ATTR2)).isEqualTo("value2");
  }

  @Test
  void getShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.Of().get(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void containsShouldReturnAttributePresence() {
    assertThat(new StepAttributes.Of(ATTR1, "value1").contains(ATTR1)).isTrue();
    assertThat(new StepAttributes.Of(ATTR1, null).contains(ATTR1)).isTrue();
    assertThat(new StepAttributes.Of().contains(ATTR1)).isFalse();
  }

  @Test
  void containsShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.Of().contains(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofCtorShouldStoreAllAttributes() {
    final StepAttributes attributes = new StepAttributes.Of(
      ATTR1, "value1", ATTR2, "value2", ATTR3, "value3", ATTR4, "value4");

    assertThat(attributes.get(ATTR1)).isEqualTo("value1");
    assertThat(attributes.get(ATTR2)).isEqualTo("value2");
    assertThat(attributes.get(ATTR3)).isEqualTo("value3");
    assertThat(attributes.get(ATTR4)).isEqualTo("value4");
  }

  @Test
  void ofCtorShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.Of(null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttributes.Of(ATTR1, "value", null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttributes.Of(ATTR1, "value", ATTR2, "value", null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttributes.Of(
      ATTR1, "value", ATTR2, "value", ATTR3, "value", null, "value"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withShouldAddAndReplaceAttributes() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nullable("test:key", defaultValue);

    final StepAttributes added = new StepAttributes.Of().with(attribute, "value1");
    assertThat(added.get(attribute)).isEqualTo("value1");

    final StepAttributes replaced = new StepAttributes.Of(attribute, "value1").with(attribute, "value2");
    assertThat(replaced.get(attribute)).isEqualTo("value2");

    final StepAttributes withDefault = new StepAttributes.Of(attribute, "value1").with(attribute);
    assertThat(withDefault.get(attribute)).isSameAs(defaultValue);
  }

  @Test
  void withShouldSetMultipleAttributes() {
    final StepAttributes attributes = new StepAttributes.Of()
      .with(ATTR1, "value1", ATTR2, "value2")
      .with(ATTR1, "value1", ATTR2, "value2", ATTR3, "value3");

    assertThat(attributes.get(ATTR1)).isEqualTo("value1");
    assertThat(attributes.get(ATTR2)).isEqualTo("value2");
    assertThat(attributes.get(ATTR3)).isEqualTo("value3");
  }

  @Test
  void withShouldNotModifyOriginal() {
    final StepAttributes attributes = new StepAttributes.Of(ATTR1, "value1");

    final StepAttributes updated = attributes.with(ATTR2, "value2");

    assertThat(attributes.contains(ATTR2)).isFalse();
    assertThat(updated.get(ATTR1)).isEqualTo("value1");
    assertThat(updated.get(ATTR2)).isEqualTo("value2");
  }

  @Test
  void withShouldThrowExceptionForNullAttribute() {
    final StepAttributes attributes = new StepAttributes.Of();

    assertThatThrownBy(() -> attributes.with(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> attributes.with(null, "value")).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> attributes.with(ATTR1, "value", null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> attributes.with(ATTR1, "value", ATTR2, "value", null, "value"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withOfShouldGenerateValueFromCurrent() {
    final StepAttributes attributes = new StepAttributes.Of().withOf(ATTR1, value -> value + "!");

    assertThat(attributes.get(ATTR1)).isEqualTo("null!");
  }

  @Test
  void withOfShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new StepAttributes.Of().withOf(null, value -> value))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttributes.Of().withOf(ATTR1, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withoutShouldRemoveAttribute() {
    final Object defaultValue = new Object();
    final SimpleStepAttribute<Object> attribute = SimpleStepAttribute.nonNull("test:key", defaultValue);
    final StepAttributes attributes = new StepAttributes.Of(attribute, "value1");

    final StepAttributes without = attributes.without(attribute);

    assertThat(without.contains(attribute)).isFalse();
    assertThat(without.get(attribute)).isSameAs(defaultValue);
    assertThat(attributes.contains(attribute)).isTrue();
  }

  @Test
  void withoutShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.Of().without(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void staticOfShouldCreateAttributes() {
    assertThat(StepAttributes.of(ATTR1, "value1").get(ATTR1)).isEqualTo("value1");

    final StepAttributes attributes = StepAttributes.of(ATTR1, "value1", ATTR2, "value2");
    assertThat(attributes.get(ATTR1)).isEqualTo("value1");
    assertThat(attributes.get(ATTR2)).isEqualTo("value2");
  }

  @Test
  void emptyShouldReturnSharedEmptyAttributes() {
    assertThat(StepAttributes.empty()).isSameAs(StepAttributes.empty());
  }

  @Test
  void builderShouldCollectAttributes() {
    final StepAttributes attributes = new StepAttributes.BuilderOf()
      .add(ATTR1, "value1")
      .add(ATTR2, "value2")
      .add(ATTR3)
      .build();

    assertThat(attributes.get(ATTR1)).isEqualTo("value1");
    assertThat(attributes.get(ATTR2)).isEqualTo("value2");
    assertThat(attributes.get(ATTR3)).isNull();
  }

  @Test
  void builderAddShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.BuilderOf().add(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttributes.BuilderOf().add(null, "value"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void builderRemoveShouldRemoveAttribute() {
    final StepAttributes attributes = new StepAttributes.BuilderOf()
      .add(ATTR1, "value1")
      .remove(ATTR1)
      .build();

    assertThat(attributes.contains(ATTR1)).isFalse();
  }

  @Test
  void builderRemoveShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> new StepAttributes.BuilderOf().remove(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void asBuilderShouldCopyAttributes() {
    final StepAttributes attributes = new StepAttributes.Of(ATTR1, "value1", ATTR2, "value2");

    final StepAttributes rebuilt = attributes.asBuilder().build();
    assertThat(rebuilt.get(ATTR1)).isEqualTo("value1");
    assertThat(rebuilt.get(ATTR2)).isEqualTo("value2");

    final StepAttributes modified = attributes.asBuilder().add(ATTR3, "value3").build();
    assertThat(attributes.contains(ATTR3)).isFalse();
    assertThat(modified.get(ATTR3)).isEqualTo("value3");
  }

  @Test
  void attributesShouldPreserveValueTypes() {
    final SimpleStepAttribute<Integer> intAttr = SimpleStepAttribute.nullable("test:int");
    final SimpleStepAttribute<String> stringAttr = SimpleStepAttribute.nullable("test:string");

    final StepAttributes attributes = new StepAttributes.Of(intAttr, 42, stringAttr, "value");

    assertThat(attributes.get(intAttr)).isEqualTo(42);
    assertThat(attributes.get(stringAttr)).isEqualTo("value");
  }

  @Test
  void attributesWithMapValueShouldPreserveMapContent() {
    final SimpleStepAttribute<Map<String, Object>> mapAttr = SimpleStepAttribute.nullable("test:map");
    final Map<String, Object> mapValue = new HashMap<>();
    mapValue.put("key1", "value1");
    mapValue.put("key2", 42);

    final Map<String, Object> result = new StepAttributes.Of(mapAttr, mapValue).get(mapAttr);

    assertThat(result)
      .containsEntry("key1", "value1")
      .containsEntry("key2", 42);
  }
}
