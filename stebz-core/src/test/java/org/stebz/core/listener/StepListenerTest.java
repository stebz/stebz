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
package org.stebz.core.listener;

import org.junit.jupiter.api.Test;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link StepListener} default methods.
 */
final class StepListenerTest {

  private StepListener emptyListener() {
    return new StepListener() {
      @Override
      public void onStepStart(final StepObj<?> step,
                              final NullableOptional<Object> context) {
      }

      @Override
      public void onStepSuccess(final StepObj<?> step,
                                final NullableOptional<Object> context,
                                final NullableOptional<Object> result) {
      }

      @Override
      public void onStepFailure(final StepObj<?> step,
                                final NullableOptional<Object> context,
                                final Throwable exception) {
      }
    };
  }

  @Test
  void orderShouldReturnMiddleOrderByDefault() {
    assertThat(this.emptyListener().order()).isEqualTo(StepListener.MIDDLE_ORDER);
  }

  @Test
  void configureShouldDoNothingByDefault() {
    assertThatCode(() -> this.emptyListener().configure(new PropertiesReader.Of(new Properties())))
      .doesNotThrowAnyException();
  }
}
