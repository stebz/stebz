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

import dev.jlet.function.ThrowingConsumer;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;
import org.stebz.core.step.executable.ConsumerStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link CStep}.
 */
final class CStepTest {
  private static final ThrowingConsumer<Object, Error> EMPTY_CONSUMER = value -> { };

  @Test
  void shouldBeAConsumerStep() {
    assertThat(CStep.of(EMPTY_CONSUMER)).isInstanceOf(ConsumerStep.class);
  }

  @Test
  void ofShouldCreateStepWithGivenAttributes() {
    assertThat(CStep.of("name", EMPTY_CONSUMER).getName()).isEqualTo("name");
    assertThat(CStep.of("name", "expected", EMPTY_CONSUMER).getExpectedResult()).isEqualTo("expected");
    assertThat(CStep.of("name", java.util.Collections.emptyMap(), EMPTY_CONSUMER).getParams()).isEmpty();
    assertThat(CStep.of("name", java.util.Collections.emptyMap(), "expected", EMPTY_CONSUMER)
      .getExpectedResult()).isEqualTo("expected");
    assertThat(CStep.of(ConsumerStep.of("name", EMPTY_CONSUMER)).getName()).isEqualTo("name");
    assertThat(CStep.of(StepAttributes.of(StepAttribute.NAME, "name"), EMPTY_CONSUMER).getName())
      .isEqualTo("name");
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> CStep.of((ThrowingConsumer<Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyShouldReturnStepWithEmptyBody() {
    assertThatCode(() -> CStep.empty().getBody().accept("v")).doesNotThrowAnyException();
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> CStep.notImplemented().getBody().accept("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyAndNotImplementedBodyShouldDelegate() {
    assertThatCode(() -> CStep.emptyBody().accept("v")).doesNotThrowAnyException();
    assertThatThrownBy(() -> CStep.notImplementedBody().accept("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }
}
