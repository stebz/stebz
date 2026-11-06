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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link StepAttribute}.
 */
final class StepAttributeTest {

  @Test
  void keywordMethodShouldCreateKeywordWithGivenValue() {
    assertThat(StepAttribute.keyword("abc").value()).isEqualTo("abc");
  }

  @Test
  void keywordMethodShouldThrowExceptionForNullValue() {
    assertThatThrownBy(() -> StepAttribute.keyword(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void paramsMethodWithoutArgsShouldReturnEmptyMap() {
    assertThat(StepAttribute.params()).isEmpty();
  }

  @Test
  void paramsMethodShouldReturnMapWithGivenEntries() {
    assertThat(StepAttribute.params("param1", "value1"))
      .containsExactly(entry("param1", "value1"));
    assertThat(StepAttribute.params(
      "param1", "value1",
      "param2", "value2"))
      .containsExactly(
        entry("param1", "value1"),
        entry("param2", "value2"));
    assertThat(StepAttribute.params(
      "param1", "value1",
      "param2", "value2",
      "param3", "value3"))
      .containsExactly(
        entry("param1", "value1"),
        entry("param2", "value2"),
        entry("param3", "value3"));
    assertThat(StepAttribute.params(
      "param1", "value1",
      "param2", "value2",
      "param3", "value3",
      "param4", "value4"))
      .containsExactly(
        entry("param1", "value1"),
        entry("param2", "value2"),
        entry("param3", "value3"),
        entry("param4", "value4"));
    assertThat(StepAttribute.params(
      "param1", "value1",
      "param2", "value2",
      "param3", "value3",
      "param4", "value4",
      "param5", "value5"))
      .containsExactly(
        entry("param1", "value1"),
        entry("param2", "value2"),
        entry("param3", "value3"),
        entry("param4", "value4"),
        entry("param5", "value5"));
  }

  @Test
  void paramsMethodWithVarargEntriesShouldReturnMapWithAllEntries() {
    final Map<String, Object> params = StepAttribute.params(
      StepAttribute.param("param1", "value1"),
      StepAttribute.param("param2", "value2"),
      StepAttribute.param("param3", "value3"));

    assertThat(params).containsExactly(
      entry("param1", "value1"),
      entry("param2", "value2"),
      entry("param3", "value3"));
  }

  @Test
  void paramMethodShouldCreateMapEntry() {
    final Map.Entry<String, Object> entry = StepAttribute.param("param1", "value1");

    assertThat(entry.getKey()).isEqualTo("param1");
    assertThat(entry.getValue()).isEqualTo("value1");
  }

  @Test
  void attributeOfShouldStoreKeyAndDefaultValues() {
    final Object defaultInput = "defaultInput";
    final Object defaultOutput = "defaultOutput";
    final StepAttribute<Object, Object, Object> attribute =
      new StepAttribute.Of<>("test:key", defaultInput, v -> v, defaultOutput, v -> v);

    assertThat(attribute.key()).isEqualTo("test:key");
    assertThat(attribute.defaultInputValue()).isSameAs(defaultInput);
    assertThat(attribute.defaultOutputValue()).isSameAs(defaultOutput);
  }

  @Test
  void attributeOfShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new StepAttribute.Of<>(null, "default", v -> v, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttribute.Of<>("test:key", "default", null, "default", v -> v))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepAttribute.Of<>("test:key", "default", v -> v, "default", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void extractInputValueShouldApplyFunction() {
    final StepAttribute<Integer, String, String> attribute =
      new StepAttribute.Of<>("test:key", 0, String::valueOf, "", v -> v);

    assertThat(attribute.extractInputValue(42)).isEqualTo("42");
  }

  @Test
  void extractOutputValueShouldApplyFunction() {
    final StepAttribute<String, String, Integer> attribute =
      new StepAttribute.Of<>("test:key", "", v -> v, 0, String::length);

    assertThat(attribute.extractOutputValue("hello")).isEqualTo(5);
  }

  @Test
  void equalsShouldBeBasedOnKey() {
    assertThat(attribute("test:key"))
      .isEqualTo(attribute("test:key"))
      .isNotEqualTo(attribute("test:key2"))
      .isNotEqualTo("not an attribute");
  }

  @Test
  void hashCodeShouldBeBasedOnKey() {
    assertThat(attribute("test:key"))
      .hasSameHashCodeAs(attribute("test:key"))
      .doesNotHaveSameHashCodeAs(attribute("test:key2"));
  }

  @Test
  void toStringShouldContainKey() {
    assertThat(attribute("test:key")).hasToString("Attribute[test:key]");
  }

  private static StepAttribute<Object, Object, Object> attribute(final String key) {
    return new StepAttribute.Of<>(key, null, v -> v, null, v -> v);
  }
}
