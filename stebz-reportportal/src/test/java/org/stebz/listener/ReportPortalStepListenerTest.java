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
package org.stebz.listener;

import org.junit.jupiter.api.Test;
import org.stebz.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link ReportPortalStepListener}.
 */
final class ReportPortalStepListenerTest {

  @Test
  void onStepStartMethodMethod() {
    final RunnableStep step = RunnableStep.of(RunnableStep.emptyBody());
    final NullableOptional<Object> optionalContext = NullableOptional.of("context");
    final ReportPortalStepListener listener = new ReportPortalStepListener(new PropertiesReader.Of(new Properties()));

    assertThatCode(() -> listener.onStepStart(step, optionalContext))
      .doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessMethod() {
    final RunnableStep step = RunnableStep.of(RunnableStep.emptyBody());
    final NullableOptional<Object> optionalContext = NullableOptional.of("context");
    final NullableOptional<Object> optionalResult = NullableOptional.of("result");
    final ReportPortalStepListener listener = new ReportPortalStepListener(new PropertiesReader.Of(new Properties()));

    assertThatCode(() -> listener.onStepSuccess(step, optionalContext, optionalResult))
      .doesNotThrowAnyException();
  }

  @Test
  void onStepFailureMethod() {
    final RunnableStep step = RunnableStep.of(RunnableStep.emptyBody());
    final NullableOptional<Object> optionalContext = NullableOptional.of("context");
    final Throwable exception = new Throwable();
    final ReportPortalStepListener listener = new ReportPortalStepListener(new PropertiesReader.Of(new Properties()));

    assertThatCode(() -> listener.onStepFailure(step, optionalContext, exception))
      .doesNotThrowAnyException();
  }
}
