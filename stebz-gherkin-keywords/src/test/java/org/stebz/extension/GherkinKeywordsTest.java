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
package org.stebz.extension;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GherkinKeywords}.
 */
final class GherkinKeywordsTest {

  @Test
  void featureKeywordDefaultValue() {
    assertThat(GherkinKeywords.feature().value())
      .isEqualTo("Feature");
  }

  @Test
  void ruleKeywordDefaultValue() {
    assertThat(GherkinKeywords.rule().value())
      .isEqualTo("Rule");
  }

  @Test
  void scenarioKeywordDefaultValue() {
    assertThat(GherkinKeywords.scenario().value())
      .isEqualTo("Scenario");
  }

  @Test
  void scenarioOutlineKeywordDefaultValue() {
    assertThat(GherkinKeywords.scenarioOutline().value())
      .isEqualTo("Scenario outline");
  }

  @Test
  void examplesKeywordDefaultValue() {
    assertThat(GherkinKeywords.examples().value())
      .isEqualTo("Examples");
  }

  @Test
  void backgroundKeywordDefaultValue() {
    assertThat(GherkinKeywords.background().value())
      .isEqualTo("Background");
  }

  @Test
  void givenKeywordDefaultValue() {
    assertThat(GherkinKeywords.given().value())
      .isEqualTo("Given");
  }

  @Test
  void whenKeywordDefaultValue() {
    assertThat(GherkinKeywords.when().value())
      .isEqualTo("When");
  }

  @Test
  void thenKeywordDefaultValue() {
    assertThat(GherkinKeywords.then().value())
      .isEqualTo("Then");
  }

  @Test
  void andKeywordDefaultValue() {
    assertThat(GherkinKeywords.and().value())
      .isEqualTo("And");
  }

  @Test
  void butKeywordDefaultValue() {
    assertThat(GherkinKeywords.but().value())
      .isEqualTo("But");
  }

  @Test
  void asteriskKeywordDefaultValue() {
    assertThat(GherkinKeywords.asterisk().value())
      .isEqualTo("*");
  }
}
