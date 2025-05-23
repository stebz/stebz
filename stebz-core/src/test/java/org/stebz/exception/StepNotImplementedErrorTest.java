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
package org.stebz.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StepNotImplementedError}.
 */
final class StepNotImplementedErrorTest {

  @Test
  void ctorOf0ArgsShouldReturnCorrectError() {
    final Error error = new StepNotImplementedError();

    assertThat(error)
      .hasNoCause()
      .hasMessage(null);
  }

  @Test
  void ctorOfMessageArgShouldReturnCorrectError() {
    final String message = "abc";
    final Error error = new StepNotImplementedError(message);

    assertThat(error)
      .hasNoCause()
      .hasMessage(message);
  }

  @Test
  void ctorOfCauseArgShouldReturnCorrectError() {
    final Exception cause = new Exception();
    final Error error = new StepNotImplementedError(cause);

    assertThat(error)
      .hasCause(cause)
      .hasMessage(cause.toString());
  }

  @Test
  void ctorOfMessageAndCauseArgsShouldReturnCorrectError() {
    final String message = "abc";
    final Exception cause = new Exception();
    final Error error = new StepNotImplementedError(message, cause);

    assertThat(error)
      .hasCause(cause)
      .hasMessage(message);
  }
}
