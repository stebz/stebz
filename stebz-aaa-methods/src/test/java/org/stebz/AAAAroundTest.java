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
package org.stebz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.executor.StepExecutor;
import org.stebz.extension.AAAKeywords;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.util.container.NullableOptional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AAAAround}.
 */
final class AAAAroundTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  @Test
  void stepMethodWithConsumerStep() {
    final String context = "context value";
    final AAAAround<String> around = new AAAAround.Of<>(StepExecutor.get(), context);
    final ConsumerStep<String> originStep = ConsumerStep.empty();

    around.Arrange(originStep);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener.getKeyword())
      .isSameAs(AAAKeywords.arrange());
    final NullableOptional<Object> contextFromListener = StaticStepListener.lastContext;
    assertThat(contextFromListener)
      .isEqualTo(NullableOptional.of(context));
  }
}
