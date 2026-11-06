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
 * Tests for {@link Keyword}.
 */
final class KeywordTest {

  @Test
  void ofShouldCreateKeywordWithGivenValue() {
    assertThat(Keyword.of("abc").value()).isEqualTo("abc");
  }

  @Test
  void ofShouldThrowExceptionForNullValue() {
    assertThatThrownBy(() -> Keyword.of(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorShouldCreateKeywordWithGivenValue() {
    assertThat(new Keyword.Of("abc").value()).isEqualTo("abc");
  }

  @Test
  void ctorShouldThrowExceptionForNullValue() {
    assertThatThrownBy(() -> new Keyword.Of(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyShouldReturnKeywordWithEmptyValue() {
    assertThat(Keyword.empty().value()).isEmpty();
  }

  @Test
  void equalsShouldBeBasedOnValueForAnyKeywordImplementation() {
    final Keyword keyword = new Keyword.Of("abc");
    final Keyword sameValueKeyword = customKeyword("abc");
    final Keyword otherValueKeyword = customKeyword("def");

    assertThat(keyword)
      .isEqualTo(sameValueKeyword)
      .isNotEqualTo(otherValueKeyword)
      .isNotEqualTo("not a keyword");
  }

  @Test
  void hashCodeShouldBeBasedOnValue() {
    assertThat(new Keyword.Of("abc"))
      .hasSameHashCodeAs(new Keyword.Of("abc"))
      .doesNotHaveSameHashCodeAs(new Keyword.Of("def"));
  }

  @Test
  void toStringShouldContainValue() {
    assertThat(new Keyword.Of("abc")).hasToString("Keyword[abc]");
    assertThat(new Keyword.Of("")).hasToString("Keyword[]");
  }

  private static Keyword customKeyword(final String value) {
    return new Keyword() {
      @Override
      public String value() {
        return value;
      }
    };
  }
}
