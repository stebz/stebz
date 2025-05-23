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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link Keyword}.
 */
final class KeywordTest {

  @Test
  void ctorShouldThrowExceptionForNullValue() {
    assertThatCode(() -> new Keyword.Of(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void valueMethodShouldReturnValueFromCtor() {
    final String value = "abc";
    final Keyword keyword = new Keyword.Of(value);

    assertThat(keyword.value())
      .isEqualTo(value);
  }

  @Test
  void equalsMethodShouldBeCorrect() {
    final Keyword keyword1 = new Keyword.Of("abc");
    final Keyword keyword2 = customKeyword("abc");
    final Keyword keyword3 = customKeyword("def");

    assertThat(keyword1)
      .isEqualTo(keyword1)
      .isEqualTo(keyword2)
      .isNotEqualTo(keyword3);
  }

  @Test
  void hashCodeMethodShouldBeCorrect() {
    final Keyword keyword1 = new Keyword.Of("abc");
    final Keyword keyword2 = new Keyword.Of("abc");
    final Keyword keyword3 = new Keyword.Of("def");

    assertThat(keyword1)
      .hasSameHashCodeAs(keyword1)
      .hasSameHashCodeAs(keyword2)
      .doesNotHaveSameHashCodeAs(keyword3);
  }

  @Test
  void emptyStaticMethodValueIsEmptyString() {
    assertThat(Keyword.empty().value())
      .isEmpty();
  }

  @Test
  void toStringMethodReturnCorrectValueForNonEmptyValue() {
    final String value = "abc";
    final Keyword keyword = new Keyword.Of(value);

    assertThat(keyword)
      .hasToString("Keyword[" + value + "]");
  }

  @Test
  void toStringMethodReturnCorrectValueForEmptyValue() {
    final Keyword keyword = new Keyword.Of("");

    assertThat(keyword)
      .hasToString("Keyword[]");
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
