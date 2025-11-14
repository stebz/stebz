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
  void ctorOf3ArgsShouldThrowExceptionForNullKey() {
    final String key = null;
    final boolean nullable = true;
    final Object defaultValue = new Object();

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf3ArgsShouldThrowExceptionForNullKeyAndFalseNullable() {
    final String key = "abc";
    final boolean nullable = false;
    final Object defaultValue = null;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf3ArgsShouldNotThrowExceptionForNullKeyAndTrueNullable() {
    final String key = "abc";
    final boolean nullable = true;
    final Object defaultValue = null;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue))
      .doesNotThrowAnyException();
  }

  @Test
  void ctorOf4ArgsShouldThrowExceptionForNullKey() {
    final String key = null;
    final boolean nullable = true;
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue, getFunction))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf4ArgsShouldThrowExceptionForNullKeyAndFalseNullable() {
    final String key = "abc";
    final boolean nullable = false;
    final Object defaultValue = null;
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue, getFunction))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorOf4ArgsShouldNotThrowExceptionForNullKeyAndTrueNullable() {
    final String key = "abc";
    final boolean nullable = true;
    final Object defaultValue = null;
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue, getFunction))
      .doesNotThrowAnyException();
  }

  @Test
  void ctorOf4ArgsShouldThrowExceptionForNullGetFunction() {
    final String key = null;
    final boolean nullable = true;
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = null;

    assertThatCode(() -> new StepAttribute.Of<>(key, nullable, defaultValue, getFunction))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void getterMethodsShouldReturnValueFromCtorOf3Args() {
    final String key = "abc";
    final boolean nullable = true;
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;
    final StepAttribute<Object> stepAttribute = new StepAttribute.Of<>(key, nullable, defaultValue, getFunction);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isTrue();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void getterMethodsShouldReturnValueFromCtorOf4Args() {
    final String key = "abc";
    final boolean nullable = true;
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;
    final StepAttribute<Object> stepAttribute = new StepAttribute.Of<>(key, nullable, defaultValue, getFunction);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isTrue();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }


  @Test
  void equalsMethodShouldBeCorrect() {
    final Object defaultValue = new Object();
    final StepAttribute<Object> stepAttribute1 = new StepAttribute.Of<>("abc", false, defaultValue, v -> v);
    final StepAttribute<Object> stepAttribute2 = customAttributeType("abc", false, defaultValue, v -> new Object());
    final StepAttribute<Object> stepAttribute3 = customAttributeType("def", false, null, v -> new Object());

    assertThat(stepAttribute1)
      .isEqualTo(stepAttribute1)
      .isEqualTo(stepAttribute2)
      .isNotEqualTo(stepAttribute3);
  }

  @Test
  void hashCodeMethodShouldBeCorrect() {
    final StepAttribute<Object> stepAttribute1 = new StepAttribute.Of<>("abc", false, new Object(), v -> v);
    final StepAttribute<Object> stepAttribute2 = new StepAttribute.Of<>("abc", true, null, v -> new Object());
    final StepAttribute<Object> stepAttribute3 = new StepAttribute.Of<>("def", true, null, v -> new Object());

    assertThat(stepAttribute1)
      .hasSameHashCodeAs(stepAttribute1)
      .hasSameHashCodeAs(stepAttribute2)
      .doesNotHaveSameHashCodeAs(stepAttribute3);
  }

  @Test
  void toStringMethodReturnCorrectValueForNonEmptyKey() {
    final String key = "abc";
    final StepAttribute<Object> stepAttribute = new StepAttribute.Of<>(key, false, new Object(), v -> v);

    assertThat(stepAttribute)
      .hasToString("Attribute[" + key + "]");
  }

  @Test
  void toStringMethodReturnCorrectValueForEmptyKey() {
    final StepAttribute<Object> stepAttribute = new StepAttribute.Of<>("", false, new Object(), v -> v);

    assertThat(stepAttribute)
      .hasToString("Attribute[]");
  }

  @Test
  void nonNullStaticMethodOf2ArgsReturnCorrectAttributeType() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> stepAttribute = StepAttribute.nonNull(key, defaultValue);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isFalse();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void nonNullStaticMethodOf3ArgsReturnCorrectAttributeType() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;
    final StepAttribute<Object> stepAttribute = StepAttribute.nonNull(key, defaultValue, getFunction);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isFalse();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void nullableStaticMethodOf1ArgReturnCorrectAttributeType() {
    final String key = "abc";
    final StepAttribute<Object> stepAttribute = StepAttribute.nullable(key);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isTrue();
    assertThat(stepAttribute.defaultValue())
      .isNull();
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void nullableStaticMethodOf2ArgsReturnCorrectAttributeType() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final StepAttribute<Object> stepAttribute = StepAttribute.nullable(key, defaultValue);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isTrue();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void nullableStaticMethodOf3ArgsReturnCorrectAttributeType() {
    final String key = "abc";
    final Object defaultValue = new Object();
    final ThrowingFunction<Object, Object, Error> getFunction = v -> v;
    final StepAttribute<Object> stepAttribute = StepAttribute.nullable(key, defaultValue, getFunction);

    assertThat(stepAttribute.key())
      .isEqualTo(key);
    assertThat(stepAttribute.nullable())
      .isTrue();
    assertThat(stepAttribute.defaultValue())
      .isEqualTo(defaultValue);
    final Object tempValue = new Object();
    assertThat(stepAttribute.safeValue(tempValue))
      .isSameAs(tempValue);
  }

  @Test
  void keywordStaticMethodReturnCorrectKeyword() {
    final String value = "abc";
    final Keyword keyword = StepAttribute.keyword(value);

    assertThat(keyword.value())
      .isEqualTo(value);
  }

  @Test
  void paramsStaticMethodOf0ArgsReturnCorrectMap() {
    final Map<String, Object> params = StepAttribute.params();

    assertThat(params)
      .isEmpty();
  }

  @Test
  void paramsStaticMethodOf2ArgsReturnCorrectMap() {
    final String paramName = "param1";
    final Object paramValue = new Object();
    final Map<String, Object> params = StepAttribute.params(
      paramName, paramValue
    );

    assertThat(params).containsExactly(
      entry(paramName, paramValue)
    );
  }

  @Test
  void paramsStaticMethodOf4ArgsReturnCorrectMap() {
    final String paramName1 = "param1";
    final Object paramValue1 = new Object();
    final String paramName2 = "param2";
    final Object paramValue2 = new Object();
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
  void paramsStaticMethodOf6ArgsReturnCorrectMap() {
    final String paramName1 = "param1";
    final Object paramValue1 = new Object();
    final String paramName2 = "param2";
    final Object paramValue2 = new Object();
    final String paramName3 = "param3";
    final Object paramValue3 = new Object();
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
  void paramsStaticMethodOf8ArgsReturnCorrectMap() {
    final String paramName1 = "param1";
    final Object paramValue1 = new Object();
    final String paramName2 = "param2";
    final Object paramValue2 = new Object();
    final String paramName3 = "param3";
    final Object paramValue3 = new Object();
    final String paramName4 = "param4";
    final Object paramValue4 = new Object();
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
  void paramsStaticMethodOf10ArgsReturnCorrectMap() {
    final String paramName1 = "param1";
    final Object paramValue1 = new Object();
    final String paramName2 = "param2";
    final Object paramValue2 = new Object();
    final String paramName3 = "param3";
    final Object paramValue3 = new Object();
    final String paramName4 = "param4";
    final Object paramValue4 = new Object();
    final String paramName5 = "param5";
    final Object paramValue5 = new Object();
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
  void paramsStaticMethodOfVargargsReturnCorrectMap() {
    final String paramName1 = "param1";
    final Object paramValue1 = new Object();
    final String paramName2 = "param2";
    final Object paramValue2 = new Object();
    final String paramName3 = "param3";
    final Object paramValue3 = new Object();
    final Map<String, Object> params = StepAttribute.params(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3)
    );

    assertThat(params).containsExactly(
      entry(paramName1, paramValue1),
      entry(paramName2, paramValue2),
      entry(paramName3, paramValue3)
    );
  }

  @Test
  void paramStaticMethodReturnCorrectMapEntry() {
    final String paramName = "param1";
    final Object paramValue = new Object();
    final Map.Entry<String, Object> entry = StepAttribute.param(paramName, paramValue);

    assertThat(entry.getKey())
      .isEqualTo(paramName);
    assertThat(entry.getValue())
      .isEqualTo(paramValue);
  }

  private static <V> StepAttribute<V> customAttributeType(final String key,
                                                          final boolean nullable,
                                                          final V defaultValue,
                                                          final ThrowingFunction<V, V, Error> getFunction) {
    return new StepAttribute<V>() {
      @Override
      public String key() {
        return key;
      }

      @Override
      public boolean nullable() {
        return nullable;
      }

      @Override
      public V defaultValue() {
        return defaultValue;
      }

      @Override
      public V safeValue(final V value) {
        return getFunction.apply(value);
      }
    };
  }
}
