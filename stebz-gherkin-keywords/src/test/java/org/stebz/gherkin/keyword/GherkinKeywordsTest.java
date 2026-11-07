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
package org.stebz.gherkin.keyword;

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
 * Tests for {@link GherkinKeywords}.
 */
final class GherkinKeywordsTest {

  @BeforeEach
  @AfterEach
  void resetKeywords() throws Exception {
    final Field keywordsField = GherkinKeywords.class.getDeclaredField("keywords");
    keywordsField.setAccessible(true);
    keywordsField.set(null, null);
  }

  private static GherkinKeywords newExtension(final PropertiesReader reader) {
    try {
      final Constructor<GherkinKeywords> ctor = GherkinKeywords.class.getDeclaredConstructor(PropertiesReader.class);
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
    assertThat(GherkinKeywords.background().value()).isEqualTo("Background:");
    assertThat(GherkinKeywords.rule().value()).isEqualTo("Rule:");
    assertThat(GherkinKeywords.given().value()).isEqualTo("Given");
    assertThat(GherkinKeywords.when().value()).isEqualTo("When");
    assertThat(GherkinKeywords.then().value()).isEqualTo("Then");
    assertThat(GherkinKeywords.and().value()).isEqualTo("And");
    assertThat(GherkinKeywords.but().value()).isEqualTo("But");
    assertThat(GherkinKeywords.asterisk().value()).isEqualTo("*");
    assertThat(GherkinKeywords.technical().value()).isEqualTo("Technical:");
  }

  @Test
  void customKeywordValues() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.keyword.background", "Backstory:");
    properties.put("stebz.gherkin.keyword.rule", "Business rule:");
    properties.put("stebz.gherkin.keyword.given", "Let:");
    properties.put("stebz.gherkin.keyword.when", "If:");
    properties.put("stebz.gherkin.keyword.then", "Expect:");
    properties.put("stebz.gherkin.keyword.and", "Also:");
    properties.put("stebz.gherkin.keyword.but", "Except:");
    properties.put("stebz.gherkin.keyword.asterisk", "Step:");
    properties.put("stebz.gherkin.keyword.technical", "Helper:");
    configureKeywords(properties);
    assertThat(GherkinKeywords.background().value()).isEqualTo("Backstory:");
    assertThat(GherkinKeywords.rule().value()).isEqualTo("Business rule:");
    assertThat(GherkinKeywords.given().value()).isEqualTo("Let:");
    assertThat(GherkinKeywords.when().value()).isEqualTo("If:");
    assertThat(GherkinKeywords.then().value()).isEqualTo("Expect:");
    assertThat(GherkinKeywords.and().value()).isEqualTo("Also:");
    assertThat(GherkinKeywords.but().value()).isEqualTo("Except:");
    assertThat(GherkinKeywords.asterisk().value()).isEqualTo("Step:");
    assertThat(GherkinKeywords.technical().value()).isEqualTo("Helper:");
  }

  @Test
  void blankPropertyValueIsTreatedAsMissing() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.keyword.given", "   ");
    properties.put("stebz.gherkin.keyword.asterisk", "");
    configureKeywords(properties);
    assertThat(GherkinKeywords.given().value()).isEqualTo("Given");
    assertThat(GherkinKeywords.asterisk().value()).isEqualTo("*");
  }

  @Test
  void keywordValueIsTrimmed() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.keyword.then", "  Then  ");
    configureKeywords(properties);
    assertThat(GherkinKeywords.then().value()).isEqualTo("Then");
  }

  @Test
  void keywordsAreInitializedOnlyOnce() {
    final Map<String, String> firstProperties = new HashMap<>();
    firstProperties.put("stebz.gherkin.keyword.when", "If:");
    final Map<String, String> secondProperties = new HashMap<>();
    secondProperties.put("stebz.gherkin.keyword.when", "Whenever:");
    configureKeywords(firstProperties);
    configureKeywords(secondProperties);
    assertThat(GherkinKeywords.when().value()).isEqualTo("If:");
  }

  @Test
  void ctorReadsStartupProperties() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.keyword.given", "Let:");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      new GherkinKeywords();
      assertThat(GherkinKeywords.given().value()).isEqualTo("Let:");
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void keywordsAreReadFromStartupPropertiesDuringStartup() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.keyword.when", "If:");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      assertThat(GherkinKeywords.when().value()).isEqualTo("If:");
      assertThat(GherkinKeywords.given().value()).isEqualTo("Given");
    } finally {
      clearStartupPropertiesReader();
    }
  }
}
