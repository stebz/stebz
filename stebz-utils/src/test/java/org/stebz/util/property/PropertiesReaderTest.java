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
package org.stebz.util.property;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link PropertiesReader}.
 */
final class PropertiesReaderTest {

  @Test
  void getStringMethodShouldThrowExceptionIfPropertyDoesNotExist() {
    final String name = "property";
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThatCode(() -> reader.getString(name))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void getStringMethodShouldReturnDefaultValueIfPropertyDoesNotExist() {
    final String name = "property";
    final String defaultValue = "default";
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf());

    assertThat(reader.getString(name, defaultValue))
      .isSameAs(defaultValue);
  }

  @Test
  void getStringMethodShouldReturnValueIfPropertyExists() {
    final String name = "property";
    final String value = "value";
    final PropertiesReader reader = new PropertiesReader.Of(propertiesOf(
      name, value
    ));

    assertThat(reader.getString(name))
      .isSameAs(value);
  }

  private static Properties propertiesOf(final String... nameAndValueList) {
    final Properties properties = new Properties();
    for (int idx = 0; idx < nameAndValueList.length; idx = idx + 2) {
      properties.put(nameAndValueList[idx], nameAndValueList[idx + 1]);
    }
    return properties;
  }
}
