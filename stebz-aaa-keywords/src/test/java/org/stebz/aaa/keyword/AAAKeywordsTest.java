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
package org.stebz.aaa.keyword;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.StebzExtension;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AAAKeywords}.
 */
final class AAAKeywordsTest {

  @BeforeEach
  @AfterEach
  void resetKeywords() throws Exception {
    final Field keywordsField = AAAKeywords.class.getDeclaredField("keywords");
    keywordsField.setAccessible(true);
    keywordsField.set(null, null);
  }

  private static AAAKeywords newExtension(final PropertiesReader reader) {
    try {
      final Constructor<AAAKeywords> ctor = AAAKeywords.class.getDeclaredConstructor(PropertiesReader.class);
      ctor.setAccessible(true);
      return ctor.newInstance(reader);
    } catch (final ReflectiveOperationException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static void configureKeywords(final Map<String, String> properties) {
    newExtension(new PropertiesReader.Of(properties));
  }

  private static void setStartupPropertiesReader(final PropertiesReader reader) throws Exception {
    final Method setMethod = StartupPropertiesReader.class.getDeclaredMethod("set", PropertiesReader.class);
    setMethod.setAccessible(true);
    setMethod.invoke(null, reader);
  }

  private static void clearStartupPropertiesReader() throws Exception {
    final Method clearMethod = StartupPropertiesReader.class.getDeclaredMethod("clear");
    clearMethod.setAccessible(true);
    clearMethod.invoke(null);
  }

  @Test
  void orderValue() {
    assertThat(newExtension(new PropertiesReader.Of(new HashMap<>())).order())
      .isEqualTo(StebzExtension.EARLY_ORDER);
  }

  @Test
  void defaultKeywordValues() {
    configureKeywords(new HashMap<>());
    assertThat(AAAKeywords.setUp().value()).isEqualTo("Set Up:");
    assertThat(AAAKeywords.tearDown().value()).isEqualTo("Tear Down:");
    assertThat(AAAKeywords.arrange().value()).isEqualTo("Arrange:");
    assertThat(AAAKeywords.act().value()).isEqualTo("Act:");
    assertThat(AAAKeywords._assert().value()).isEqualTo("Assert:");
    assertThat(AAAKeywords.asterisk().value()).isEqualTo("*");
  }

  @Test
  void customKeywordValues() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.aaa.keyword.setUp", "Given:");
    properties.put("stebz.aaa.keyword.tearDown", "Finally:");
    properties.put("stebz.aaa.keyword.arrange", "Given:");
    properties.put("stebz.aaa.keyword.act", "When:");
    properties.put("stebz.aaa.keyword.assert", "Then:");
    properties.put("stebz.aaa.keyword.asterisk", "And:");
    configureKeywords(properties);
    assertThat(AAAKeywords.setUp().value()).isEqualTo("Given:");
    assertThat(AAAKeywords.tearDown().value()).isEqualTo("Finally:");
    assertThat(AAAKeywords.arrange().value()).isEqualTo("Given:");
    assertThat(AAAKeywords.act().value()).isEqualTo("When:");
    assertThat(AAAKeywords._assert().value()).isEqualTo("Then:");
    assertThat(AAAKeywords.asterisk().value()).isEqualTo("And:");
  }

  @Test
  void blankPropertyValueIsTreatedAsMissing() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.aaa.keyword.setUp", "   ");
    properties.put("stebz.aaa.keyword.asterisk", "");
    configureKeywords(properties);
    assertThat(AAAKeywords.setUp().value()).isEqualTo("Set Up:");
    assertThat(AAAKeywords.asterisk().value()).isEqualTo("*");
  }

  @Test
  void keywordValueIsTrimmed() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.aaa.keyword.arrange", "  Arrange:  ");
    configureKeywords(properties);
    assertThat(AAAKeywords.arrange().value()).isEqualTo("Arrange:");
  }

  @Test
  void keywordsAreInitializedOnlyOnce() {
    final Map<String, String> firstProperties = new HashMap<>();
    firstProperties.put("stebz.aaa.keyword.act", "When:");
    final Map<String, String> secondProperties = new HashMap<>();
    secondProperties.put("stebz.aaa.keyword.act", "Whenever:");
    configureKeywords(firstProperties);
    configureKeywords(secondProperties);
    assertThat(AAAKeywords.act().value()).isEqualTo("When:");
  }

  @Test
  void ctorReadsStartupProperties() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.aaa.keyword.setUp", "Given:");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      new AAAKeywords();
      assertThat(AAAKeywords.setUp().value()).isEqualTo("Given:");
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void keywordsAreReadFromStartupPropertiesDuringStartup() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.aaa.keyword.act", "When:");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      assertThat(AAAKeywords.act().value()).isEqualTo("When:");
      assertThat(AAAKeywords.setUp().value()).isEqualTo("Set Up:");
    } finally {
      clearStartupPropertiesReader();
    }
  }
}
