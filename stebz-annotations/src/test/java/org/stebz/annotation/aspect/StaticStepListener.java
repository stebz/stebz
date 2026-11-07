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
package org.stebz.annotation.aspect;

import org.stebz.core.listener.StepListener;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Test step listener. Collects steps started, succeeded, and failed via the main {@code StepExecutor}.
 */
public final class StaticStepListener implements StepListener {
  /**
   * Steps started by the main {@code StepExecutor}.
   */
  public static final List<StepObj<?>> startedSteps = new CopyOnWriteArrayList<>();
  /**
   * Steps succeeded by the main {@code StepExecutor}.
   */
  public static final List<StepObj<?>> succeededSteps = new CopyOnWriteArrayList<>();
  /**
   * Steps failed by the main {@code StepExecutor}.
   */
  public static final List<StepObj<?>> failedSteps = new CopyOnWriteArrayList<>();

  /**
   * Ctor.
   */
  public StaticStepListener() {
  }

  /**
   * Clears collected steps.
   */
  public static void clear() {
    startedSteps.clear();
    succeededSteps.clear();
    failedSteps.clear();
  }

  @Override
  public void onStepStart(final StepObj<?> step,
                          final NullableOptional<Object> context) {
    startedSteps.add(step);
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    succeededSteps.add(step);
  }

  @Override
  public void onStepFailure(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final Throwable exception) {
    failedSteps.add(step);
  }
}
