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
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link StepAttribute}.
 */
final class StepAttributeTest {

  @Test
  void keywordMethodShouldCreateKeywordWithGivenValue() {
    final String value = "TestKeyword";
    final Keyword keyword = StepAttribute.keyword(value);

    assertThat(keyword.value())
      .isEqualTo(value);
  }

  @Test
  void keywordMethodShouldThrowExceptionForNullValue() {
    assertThatCode(() -> StepAttribute.keyword(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void paramsMethodWithoutArgsShouldReturnEmptyMap() {
    assertThat(StepAttribute.params())
      .isEmpty();
  }

  @Test
  void paramsMethodWith1ParamShouldReturnMapWith1Entry() {
    final String paramName = "param1";
    final Object paramValue = "value1";

    final Map<String, Object> params = StepAttribute.params(paramName, paramValue);

    assertThat(params).containsExactly(
      entry(paramName, paramValue)
    );
  }

  @Test
  void paramsMethodWith2ParamsShouldReturnMapWith2Entries() {
    final String paramName1 = "param1";
    final Object paramValue1 = "value1";
    final String paramName2 = "param2";
    final Object paramValue2 = "value2";

    final Map<String, Object> params = StepAttribute.params(
      paramName1, paramValue1,
      paramName2, paramValue2
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2)
    );
  }

  @Test
  void paramsMethodWith3ParamsShouldReturnMapWith3Entries() {
    final String paramName1 = "param1";
    final Object paramValue1 = "value1";
    final String paramName2 = "param2";
    final Object paramValue2 = "value2";
    final String paramName3 = "param3";
    final Object paramValue3 = "value3";

    final Map<String, Object> params = StepAttribute.params(
      paramName1, paramValue1,
      paramName2, paramValue2,
      paramName3, paramValue3
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3)
    );
  }

  @Test
  void paramsMethodWith4ParamsShouldReturnMapWith4rEntries() {
    final String paramName1 = "param1";
    final Object paramValue1 = "value1";
    final String paramName2 = "param2";
    final Object paramValue2 = "value2";
    final String paramName3 = "param3";
    final Object paramValue3 = "value3";
    final String paramName4 = "param4";
    final Object paramValue4 = "value4";

    final Map<String, Object> params = StepAttribute.params(
      paramName1, paramValue1,
      paramName2, paramValue2,
      paramName3, paramValue3,
      paramName4, paramValue4
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3),
      entry(paramName4, paramValue4)
    );
  }

  @Test
  void paramsMethodWith5ParamsShouldReturnMapWith5Entries() {
    final String paramName1 = "param1";
    final Object paramValue1 = "value1";
    final String paramName2 = "param2";
    final Object paramValue2 = "value2";
    final String paramName3 = "param3";
    final Object paramValue3 = "value3";
    final String paramName4 = "param4";
    final Object paramValue4 = "value4";
    final String paramName5 = "param5";
    final Object paramValue5 = "value5";

    final Map<String, Object> params = StepAttribute.params(
      paramName1, paramValue1,
      paramName2, paramValue2,
      paramName3, paramValue3,
      paramName4, paramValue4,
      paramName5, paramValue5
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3),
      entry(paramName4, paramValue4),
      entry(paramName5, paramValue5)
    );
  }

  @Test
  void paramsMethodWithVarargEntriesShouldReturnMapWithAllEntries() {
    final String paramName1 = "param1";
    final Object paramValue1 = "value1";
    final String paramName2 = "param2";
    final Object paramValue2 = "value2";
    final String paramName3 = "param3";
    final Object paramValue3 = "value3";

    final Map<String, Object> params = StepAttribute.params(
      StepAttribute.param(paramName1, paramValue1),
      StepAttribute.param(paramName2, paramValue2),
      StepAttribute.param(paramName3, paramValue3)
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3)
    );
  }

  @Test
  void paramMethodShouldCreateMapEntry() {
    final Map.Entry<String, Object> entry = StepAttribute.param("testKey", "testValue");

    assertThat(entry.getKey())
      .isEqualTo("testKey");
    assertThat(entry.getValue())
      .isEqualTo("testValue");
  }

  @Test
  void ctorShouldCreateStepAttributeWithGivenParameters() {
    final String key = "test:key";
    final Object defaultInput = "defaultInput";
    final Object defaultOutput = "defaultOutput";
    final ThrowingFunction<Object, Object, ?> extractInput = v -> v;
    final ThrowingFunction<Object, Object, ?> extractOutput = v -> v;

    final StepAttribute<Object, Object, Object> attribute = new StepAttribute.Of<>(
      key, defaultInput, extractInput, defaultOutput, extractOutput
    );

    assertThat(attribute.key())
      .isEqualTo(key);
    assertThat(attribute.defaultInputValue())
      .isSameAs(defaultInput);
    assertThat(attribute.defaultOutputValue())
      .isSameAs(defaultOutput);
  }

  @Test
  void ctorShouldThrowExceptionForNullKey() {
    final String nullKey = null;
    final Object defaultInput = "defaultInput";
    final Object defaultOutput = "defaultOutput";
    final ThrowingFunction<Object, Object, ?> extractInput = v -> v;
    final ThrowingFunction<Object, Object, ?> extractOutput = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(
      nullKey, defaultInput, extractInput, defaultOutput, extractOutput
    )).isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorShouldThrowExceptionForNullExtractInputFunction() {
    final String key = "test:key";
    final Object defaultInput = "defaultInput";
    final Object defaultOutput = "defaultOutput";
    final ThrowingFunction<Object, Object, ?> extractOutput = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(
      key, defaultInput, null, defaultOutput, extractOutput
    ))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorShouldThrowExceptionForNullExtractOutputFunction() {
    final String key = "test:key";
    final Object defaultInput = "defaultInput";
    final Object defaultOutput = "defaultOutput";
    final ThrowingFunction<Object, Object, ?> extractInput = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(
      key, defaultInput, extractInput, defaultOutput, null
    ))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void extractInputValueMethodShouldApplyFunction() {
    final StepAttribute<Integer, String, String> attribute = new StepAttribute.Of<>(
      "test:key", 0, String::valueOf, "", v -> v
    );

    final String result = attribute.extractInputValue(42);

    assertThat(result)
      .isEqualTo("42");
  }

  @Test
  void extractOutputValueMethodShouldApplyFunction() {
    final StepAttribute<String, String, Integer> attribute = new StepAttribute.Of<>(
      "test:key", "", v -> v, 0, String::length
    );

    final Integer result = attribute.extractOutputValue("hello");

    assertThat(result)
      .isEqualTo(5);
  }

  @Test
  void equalsMethodShouldReturnTrueForSameInstance() {
    final StepAttribute<Object, Object, Object> attribute = new StepAttribute.Of<>(
      "test:key", null, v -> v, null, v -> v
    );

    assertThat(attribute)
      .isEqualTo(attribute);
  }

  @Test
  void equalsMethodShouldReturnTrueForAttributesWithSameKey() {
    final String key = "test:key";
    final StepAttribute<Object, Object, Object> attribute1 = new StepAttribute.Of<>(
      key, "value1", v -> v, "value1", v -> v
    );
    final StepAttribute<Object, Object, Object> attribute2 = new StepAttribute.Of<>(
      key, "value2", v -> v, "value2", v -> v
    );

    assertThat(attribute1)
      .isEqualTo(attribute2);
  }

  @Test
  void equalsMethodShouldReturnFalseForAttributesWithDifferentKeys() {
    final StepAttribute<Object, Object, Object> attribute1 = new StepAttribute.Of<>(
      "test:key1", null, v -> v, null, v -> v
    );
    final StepAttribute<Object, Object, Object> attribute2 = new StepAttribute.Of<>(
      "test:key2", null, v -> v, null, v -> v
    );

    assertThat(attribute1)
      .isNotEqualTo(attribute2);
  }

  @Test
  void hashCodeMethodShouldReturnSameValueForAttributesWithSameKey() {
    final String key = "test:key";
    final StepAttribute<Object, Object, Object> attribute1 = new StepAttribute.Of<>(
      key, null, v -> v, null, v -> v
    );
    final StepAttribute<Object, Object, Object> attribute2 = new StepAttribute.Of<>(
      key, null, v -> v, null, v -> v
    );

    assertThat(attribute1)
      .hasSameHashCodeAs(attribute2);
  }

  @Test
  void hashCodeMethodShouldReturnDifferentValueForAttributesWithDifferentKeys() {
    final StepAttribute<Object, Object, Object> attribute1 = new StepAttribute.Of<>(
      "test:key1", null, v -> v, null, v -> v
    );
    final StepAttribute<Object, Object, Object> attribute2 = new StepAttribute.Of<>(
      "test:key2", null, v -> v, null, v -> v
    );

    assertThat(attribute1.hashCode())
      .isNotEqualTo(attribute2.hashCode());
  }
}

