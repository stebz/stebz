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
package org.stebz.util.property;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link PropertiesReader}.
 */
final class PropertiesReaderTest {

  @Test
  void getStringMethodShouldReturnValueIfPropertyExists() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThat(reader.getString("property"))
      .isEqualTo("value");
  }

  @Test
  void getStringMethodShouldIgnoreCase() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "NaMe", "value"
    ));

    assertThat(reader.getString("nAmE"))
      .isEqualTo("value");
  }

  @Test
  void getStringMethodShouldTrimValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", " value "
    ));

    assertThat(reader.getString("property"))
      .isEqualTo("value");
  }

  @Test
  void getStringMethodShouldThrowExceptionIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatThrownBy(() -> reader.getString("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getStringMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getString("property", "default"))
      .isEqualTo("default");
  }

  @Test
  void getStringMethodShouldReturnDefaultValueIfPropertyIsEmpty() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", ""
    ));

    assertThat(reader.getString("property", "default"))
      .isEqualTo("default");
  }

  @Test
  void getStringMethodShouldReturnDefaultValueIfPropertyIsBlank() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "   "
    ));

    assertThat(reader.getString("property", "default"))
      .isEqualTo("default");
  }

  @Test
  void getStringMethodShouldThrowNullPointerExceptionForNullNameArg() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatNullPointerException()
      .isThrownBy(() -> reader.getString(null));
  }

  @Test
  void getStringListMethodShouldReturnListOfTrimmedValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value1, value2 , value3"
    ));

    assertThat(reader.getStringList("property", ","))
      .containsExactly("value1", "value2", "value3");
  }

  @Test
  void getStringListMethodShouldSupportCustomDelimiter() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value1;value2"
    ));

    assertThat(reader.getStringList("property", ";"))
      .containsExactly("value1", "value2");
  }

  @Test
  void getStringListMethodShouldReturnEmptyListIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getStringList("property", ","))
      .isEmpty();
  }

  @Test
  void getStringListMethodShouldReturnEmptyListIfPropertyIsEmpty() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", ""
    ));

    assertThat(reader.getStringList("property", ","))
      .isEmpty();
  }

  @Test
  void getStringListMethodShouldThrowNullPointerExceptionForNullDelimiterArg() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThatNullPointerException()
      .isThrownBy(() -> reader.getStringList("property", null));
  }

  @Test
  void getStringListMethodShouldSkipEmptyElements() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value1,, value2 , "
    ));

    assertThat(reader.getStringList("property", ","))
      .containsExactly("value1", "value2");
  }

  @Test
  void getStringListMethodShouldTreatDelimiterAsRegularExpression() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value1|value2"
    ));

    assertThat(reader.getStringList("property", "\\|"))
      .containsExactly("value1", "value2");
  }

  @Test
  void getCharacterMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "a"
    ));

    assertThat(reader.getCharacter("property"))
      .isEqualTo('a');
  }

  @Test
  void getCharacterMethodShouldThrowExceptionIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatThrownBy(() -> reader.getCharacter("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getCharacterMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "ab"
    ));

    assertThatThrownBy(() -> reader.getCharacter("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getCharacterMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getCharacter("property", 'a'))
      .isEqualTo('a');
  }

  @Test
  void getCharacterListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "a, b"
    ));

    assertThat(reader.getCharacterList("property", ","))
      .containsExactly('a', 'b');
  }

  @Test
  void getBooleanMethodShouldReturnTrueValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "true"
    ));

    assertThat(reader.getBoolean("property"))
      .isTrue();
  }

  @Test
  void getBooleanMethodShouldReturnFalseValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "false"
    ));

    assertThat(reader.getBoolean("property"))
      .isFalse();
  }

  @Test
  void getBooleanMethodShouldIgnoreCase() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "TRUE"
    ));

    assertThat(reader.getBoolean("property"))
      .isTrue();
  }

  @Test
  void getBooleanMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "yes"
    ));

    assertThatThrownBy(() -> reader.getBoolean("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getBooleanMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getBoolean("property", Boolean.TRUE))
      .isTrue();
  }

  @Test
  void getBooleanListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "true, false"
    ));

    assertThat(reader.getBooleanList("property", ","))
      .containsExactly(true, false);
  }

  @Test
  void getIntegerMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "42"
    ));

    assertThat(reader.getInteger("property"))
      .isEqualTo(42);
  }

  @Test
  void getIntegerMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThatThrownBy(() -> reader.getInteger("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getIntegerMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getInteger("property", 42))
      .isEqualTo(42);
  }

  @Test
  void getIntegerListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "1, 2, 3"
    ));

    assertThat(reader.getIntegerList("property", ","))
      .containsExactly(1, 2, 3);
  }

  @Test
  void getIntegerListMethodShouldSkipEmptyElements() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "1,, 2"
    ));

    assertThat(reader.getIntegerList("property", ","))
      .containsExactly(1, 2);
  }

  @Test
  void getLongMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "42"
    ));

    assertThat(reader.getLong("property"))
      .isEqualTo(42L);
  }

  @Test
  void getLongMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThatThrownBy(() -> reader.getLong("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getLongMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getLong("property", 42L))
      .isEqualTo(42L);
  }

  @Test
  void getLongListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "1, 2"
    ));

    assertThat(reader.getLongList("property", ","))
      .containsExactly(1L, 2L);
  }

  @Test
  void getDoubleMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "42.5"
    ));

    assertThat(reader.getDouble("property"))
      .isEqualTo(42.5);
  }

  @Test
  void getDoubleMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThatThrownBy(() -> reader.getDouble("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getDoubleMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getDouble("property", 42.5))
      .isEqualTo(42.5);
  }

  @Test
  void getDoubleListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "1.5, 2.5"
    ));

    assertThat(reader.getDoubleList("property", ","))
      .containsExactly(1.5, 2.5);
  }

  @Test
  void getEnumMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "FIRST"
    ));

    assertThat(reader.getEnum("property", TestEnum.class))
      .isEqualTo(TestEnum.FIRST);
  }

  @Test
  void getEnumMethodShouldMatchValueByNameIgnoringCase() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "first"
    ));

    assertThat(reader.getEnum("property", TestEnum.class))
      .isEqualTo(TestEnum.FIRST);
  }

  @Test
  void getEnumMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "value"
    ));

    assertThatThrownBy(() -> reader.getEnum("property", TestEnum.class))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getEnumMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getEnum("property", TestEnum.class, TestEnum.SECOND))
      .isEqualTo(TestEnum.SECOND);
  }

  @Test
  void getEnumMethodShouldThrowNullPointerExceptionForNullEnumTypeIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatNullPointerException()
      .isThrownBy(() -> reader.getEnum("property", null, TestEnum.SECOND));
  }

  @Test
  void getEnumListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "first, SECOND"
    ));

    assertThat(reader.getEnumList("property", TestEnum.class, ","))
      .containsExactly(TestEnum.FIRST, TestEnum.SECOND);
  }

  @Test
  void getEnumListMethodShouldThrowNullPointerExceptionForNullEnumTypeIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatNullPointerException()
      .isThrownBy(() -> reader.getEnumList("property", null, ","));
  }

  @Test
  void getClassMethodShouldReturnValue() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "java.lang.String"
    ));

    assertThat(reader.getClass("property"))
      .isSameAs(String.class);
  }

  @Test
  void getClassMethodShouldThrowExceptionIfValueHasIncorrectFormat() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "com.example.NoSuchClass"
    ));

    assertThatThrownBy(() -> reader.getClass("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getClassMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getClass("property", String.class))
      .isSameAs(String.class);
  }

  @Test
  void getClassListMethodShouldReturnListOfValues() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "property", "java.lang.String, java.lang.Integer"
    ));

    assertThat(reader.getClassList("property", ","))
      .containsExactly(String.class, Integer.class);
  }

  @Test
  void propertiesCtorShouldIgnoreCase() {
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      "NaMe", "value"
    ));

    assertThat(reader.getString("name"))
      .isEqualTo("value");
  }

  @Test
  void propertiesCtorShouldIncludeDefaultProperties() {
    final Properties defaults = propertiesOf("inheritedProperty", "inheritedValue");
    final Properties properties = new Properties(defaults);
    final PropertiesReader reader = new PropertiesReader.Of(properties);

    assertThat(reader.getString("inheritedproperty"))
      .isEqualTo("inheritedValue");
  }

  @Test
  void mapCtorShouldIgnoreCase() {
    final PropertiesReader reader = new PropertiesReader.Of(mapOf(
      "KEY", "value"
    ));

    assertThat(reader.getString("key"))
      .isEqualTo("value");
  }

  @Test
  void filesCtorShouldLoadPropertiesFromResourceFiles() {
    final PropertiesReader reader = new PropertiesReader.Of(
      "stebz-utils-test.properties",
      "missing-file.properties"
    );

    assertThat(reader.getString("fileProperty"))
      .isEqualTo("fileValue");
  }

  @Test
  void filesCtorShouldLoadPropertiesWhenContextClassLoaderIsNull() {
    final Thread thread = Thread.currentThread();
    final ClassLoader originalContextClassLoader = thread.getContextClassLoader();
    try {
      thread.setContextClassLoader(null);

      final PropertiesReader reader = new PropertiesReader.Of("stebz-utils-test.properties");

      assertThat(reader.getString("fileProperty"))
        .isEqualTo("fileValue");
    } finally {
      thread.setContextClassLoader(originalContextClassLoader);
    }
  }

  @Test
  void filesCtorShouldIgnoreMissingFiles() {
    assertThatCode(() -> new PropertiesReader.Of("missing-file.properties"))
      .doesNotThrowAnyException();
    assertThatThrownBy(() -> new PropertiesReader.Of("missing-file.properties").getString("property"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void filesCtorShouldContainSystemProperties() {
    assertThat(new PropertiesReader.Of().getString("java.version"))
      .isNotBlank();
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullPropertiesArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> new PropertiesReader.Of((Properties) null));
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullMapArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> new PropertiesReader.Of((Map<String, String>) null));
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullFilesArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> new PropertiesReader.Of((String[]) null));
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullFileElement() {
    assertThatNullPointerException()
      .isThrownBy(() -> new PropertiesReader.Of("stebz-utils-test.properties", null));
  }

  private enum TestEnum {
    FIRST,
    SECOND
  }

  private static Properties propertiesOf(final String... nameAndValueList) {
    final Properties properties = new Properties();
    for (int idx = 0; idx < nameAndValueList.length; idx = idx + 2) {
      properties.put(nameAndValueList[idx], nameAndValueList[idx + 1]);
    }
    return properties;
  }

  private static Map<String, String> mapOf(final String... nameAndValueList) {
    final Map<String, String> map = new HashMap<>();
    for (int idx = 0; idx < nameAndValueList.length; idx = idx + 2) {
      map.put(nameAndValueList[idx], nameAndValueList[idx + 1]);
    }
    return map;
  }
}
