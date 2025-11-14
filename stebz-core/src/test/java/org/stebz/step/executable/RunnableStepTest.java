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
package org.stebz.step.executable;

import dev.jlet.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.stebz.attribute.StepAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RunnableStep}.
 */
final class RunnableStepTest {

  @Test
  void runnableStepContainsGivenAttributesAndBody() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final ThrowingRunnable<Error> body = mockRunnable();
    final RunnableStep step = new RunnableStep.Of(attributes, body);

    assertThat(step.attributes())
      .isSameAs(attributes);
    assertThat(step.body())
      .isSameAs(body);
  }

  @Test
  void runnableStepCanUpdateAttributes() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final StepAttributes newAttributes = mock(StepAttributes.class);
    final ThrowingRunnable<Error> body = mockRunnable();
    final RunnableStep step = new RunnableStep.Of(attributes, body);

    final RunnableStep newStep = step.withAttributes(newAttributes);
    assertThat(newStep.attributes())
      .isSameAs(newAttributes);
  }

  @Test
  void runnableStepCanUpdateBody() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final ThrowingRunnable<Error> body = mockRunnable();
    final ThrowingRunnable<Error> newBody = mockRunnable();
    final RunnableStep step = new RunnableStep.Of(attributes, body);

    final RunnableStep newStep = step.withBody(newBody);
    assertThat(newStep.body())
      .isSameAs(newBody);
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnable() {
    return mock(ThrowingRunnable.class);
  }
}
