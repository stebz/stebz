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
package org.stebz.core.step.executable.alias;

import dev.jlet.function.ThrowingFunction;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;
import org.stebz.core.step.executable.FunctionStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link FStep}.
 */
final class FStepTest {
  private static final ThrowingFunction<Object, String, Error> IDENTITY = String::valueOf;

  @Test
  void shouldBeAFunctionStep() {
    assertThat(FStep.of(IDENTITY)).isInstanceOf(FunctionStep.class);
  }

  @Test
  void ofShouldCreateStepWithGivenAttributes() throws Throwable {
    assertThat(FStep.of("name", IDENTITY).getName()).isEqualTo("name");
    assertThat(FStep.of("name", "expected", IDENTITY).getExpectedResult()).isEqualTo("expected");
    assertThat(FStep.of("name", java.util.Collections.emptyMap(), IDENTITY).getParams()).isEmpty();
    assertThat(FStep.of("name", java.util.Collections.emptyMap(), "expected", IDENTITY)
      .getExpectedResult()).isEqualTo("expected");
    assertThat(FStep.of(FunctionStep.of("name", IDENTITY)).getName()).isEqualTo("name");
    assertThat(FStep.of(StepAttributes.of(StepAttribute.NAME, "name"), IDENTITY).getName())
      .isEqualTo("name");
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> FStep.of((ThrowingFunction<Object, String, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyShouldReturnStepWithEmptyBody() throws Throwable {
    assertThat(FStep.empty().getBody().apply("v")).isNull();
  }

  @Test
  void emptyReturningShouldReturnStepReturningGivenResult() throws Throwable {
    assertThat(FStep.emptyReturning("result").getBody().apply("v")).isEqualTo("result");
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> FStep.notImplemented().getBody().apply("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyAndNotImplementedBodyShouldDelegate() throws Throwable {
    assertThat(FStep.emptyBody().apply("v")).isNull();
    assertThatThrownBy(() -> FStep.notImplementedBody().apply("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyReturningShouldDelegate() throws Throwable {
    assertThat(FStep.emptyBodyReturning("result").apply("v")).isEqualTo("result");
  }
}
