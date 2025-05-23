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
package org.stebz.util.container;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link NullableOptional}.
 */
final class NullableOptionalTest {

  @Test
  void isEmptyMethodShouldReturnFalseForNonNullValue() {
    final Object value = new Object();
    final NullableOptional<Object> nullableOptional = new NullableOptional.Of<>(value);

    assertThat(nullableOptional.isEmpty())
      .isFalse();
  }

  @Test
  void isEmptyMethodShouldReturnFalseForNullValue() {
    final NullableOptional<Object> nullableOptional = new NullableOptional.Of<>(null);

    assertThat(nullableOptional.isEmpty())
      .isFalse();
  }

  @Test
  void valueMethodShouldReturnValueForNonNullValue() {
    final Object value = new Object();
    final NullableOptional<Object> nullableOptional = new NullableOptional.Of<>(value);

    assertThat(nullableOptional.get())
      .isSameAs(value);
  }

  @Test
  void valueMethodShouldReturnValueForNullValue() {
    final NullableOptional<Object> nullableOptional = new NullableOptional.Of<>(null);

    assertThat(nullableOptional.get())
      .isNull();
  }

  @Test
  void emptyStaticMethodShouldReturnOptionalValueWhichIsEmptyMethodShouldReturnTrue() {
    final NullableOptional<Object> nullableOptional = NullableOptional.empty();

    assertThat(nullableOptional.isEmpty())
      .isTrue();
  }

  @Test
  void emptyStaticMethodShouldReturnOptionalValueWhichValueMethodShouldThrowException() {
    final NullableOptional<Object> nullableOptional = NullableOptional.empty();

    assertThatCode(() -> nullableOptional.get())
      .isInstanceOf(NoSuchElementException.class);
  }
}
