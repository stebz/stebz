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
package org.stebz.core.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StebzStartupException}.
 */
final class StebzStartupExceptionTest {

  @Test
  void ctorWithoutArgsShouldCreateExceptionWithoutMessageAndCause() {
    final StebzStartupException exception = new StebzStartupException();

    assertThat(exception).isInstanceOf(RuntimeException.class);
    assertThat(exception.getMessage()).isNull();
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void messageCtorShouldCreateExceptionWithGivenMessage() {
    final StebzStartupException exception = new StebzStartupException("message");

    assertThat(exception.getMessage()).isEqualTo("message");
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void causeCtorShouldCreateExceptionWithGivenCause() {
    final IllegalStateException cause = new IllegalStateException("cause");
    final StebzStartupException exception = new StebzStartupException(cause);

    assertThat(exception.getMessage()).isEqualTo(cause.toString());
    assertThat(exception.getCause()).isSameAs(cause);
  }

  @Test
  void messageAndCauseCtorShouldCreateExceptionWithGivenMessageAndCause() {
    final IllegalStateException cause = new IllegalStateException("cause");
    final StebzStartupException exception = new StebzStartupException("message", cause);

    assertThat(exception.getMessage()).isEqualTo("message");
    assertThat(exception.getCause()).isSameAs(cause);
  }
}
