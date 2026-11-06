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

import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;
import org.stebz.core.step.executable.SupplierStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SStep}.
 */
final class SStepTest {

  @Test
  void shouldBeASupplierStep() {
    assertThat(SStep.of(() -> "result")).isInstanceOf(SupplierStep.class);
  }

  @Test
  void ofShouldCreateStepWithGivenAttributes() {
    assertThat(SStep.of("name", () -> "result").getName()).isEqualTo("name");
    assertThat(SStep.of("name", "expected", () -> "result").getExpectedResult()).isEqualTo("expected");
    assertThat(SStep.of("name", java.util.Collections.emptyMap(), () -> "result").getParams()).isEmpty();
    assertThat(SStep.of("name", java.util.Collections.emptyMap(), "expected", () -> "result")
      .getExpectedResult()).isEqualTo("expected");
    assertThat(SStep.of(SupplierStep.of("name", () -> "result")).getName()).isEqualTo("name");
    assertThat(SStep.of(StepAttributes.of(StepAttribute.NAME, "name"), () -> "result").getName())
      .isEqualTo("name");
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> SStep.of((dev.jlet.function.ThrowingSupplier<String, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyShouldReturnStepWithEmptyBody() throws Throwable {
    assertThat(SStep.empty().getBody().get()).isNull();
  }

  @Test
  void emptyReturningShouldReturnStepReturningGivenResult() throws Throwable {
    assertThat(SStep.emptyReturning("result").getBody().get()).isEqualTo("result");
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(SStep.notImplemented().getBody()::get)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyAndNotImplementedBodyShouldDelegate() {
    assertThat(SStep.emptyBody().get()).isNull();
    assertThatThrownBy(SStep.notImplementedBody()::get)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyReturningShouldDelegate() {
    assertThat(SStep.emptyBodyReturning("result").get()).isEqualTo("result");
  }
}
