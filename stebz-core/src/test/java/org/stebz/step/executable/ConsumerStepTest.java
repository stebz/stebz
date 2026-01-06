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
package org.stebz.step.executable;

import dev.jlet.function.ThrowingConsumer;
import org.junit.jupiter.api.Test;
import org.stebz.attribute.StepAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link ConsumerStep}.
 */
final class ConsumerStepTest {

  @Test
  void consumerStepContainsGivenAttributesAndBody() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final ThrowingConsumer<Object, Error> body = mockConsumer();
    final ConsumerStep<Object> step = new ConsumerStep.Of<>(attributes, body);

    assertThat(step.attributes())
      .isSameAs(attributes);
    assertThat(step.body())
      .isSameAs(body);
  }

  @Test
  void consumerStepCanUpdateAttributes() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final StepAttributes newAttributes = mock(StepAttributes.class);
    final ThrowingConsumer<Object, Error> body = mockConsumer();
    final ConsumerStep<Object> step = new ConsumerStep.Of<>(attributes, body);

    final ConsumerStep<Object> newStep = step.withAttributes(newAttributes);
    assertThat(newStep.attributes())
      .isSameAs(newAttributes);
  }

  @Test
  void consumerStepCanUpdateBody() {
    final StepAttributes attributes = mock(StepAttributes.class);
    final ThrowingConsumer<Object, Error> body = mockConsumer();
    final ThrowingConsumer<Object, Error> newBody = mockConsumer();
    final ConsumerStep<Object> step = new ConsumerStep.Of<>(attributes, body);

    final ConsumerStep<Object> newStep = step.withBody(newBody);
    assertThat(newStep.body())
      .isSameAs(newBody);
  }

  @SuppressWarnings("unchecked")
  private static ThrowingConsumer<Object, Error> mockConsumer() {
    return mock(ThrowingConsumer.class);
  }
}
