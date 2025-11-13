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
package org.stebz.extension;

import org.stebz.listener.StepListener;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

/**
 * Stebz extension.
 * <p>Method calling order.
 * <ul>
 *   <li>{@link InterceptStepContext#interceptStepContext(StepObj, Object)} if step has context</li>
 *   <li>{@link InterceptStep#interceptStep(StepObj, NullableOptional)}</li>
 *   <li>{@link BeforeStepStart#beforeStepStart(StepObj, NullableOptional)}</li>
 *   <li>{@link StepListener#onStepStart(StepObj, NullableOptional)}</li>
 *   <li>{@link AfterStepStart#afterStepStart(StepObj, NullableOptional)}</li>
 * </ul>
 * If step is successful:
 * <ul>
 *   <li>{@link InterceptStepResult#interceptStepResult(StepObj, NullableOptional, Object)} if step has result</li>
 *   <li>{@link BeforeStepSuccess#beforeStepSuccess(StepObj, NullableOptional, NullableOptional)}</li>
 *   <li>{@link StepListener#onStepSuccess(StepObj, NullableOptional, NullableOptional)}</li>
 *   <li>{@link AfterStepSuccess#afterStepSuccess(StepObj, NullableOptional, NullableOptional)}</li>
 * </ul>
 * If step is failed:
 * <ul>
 *   <li>{@link InterceptStepException#interceptStepException(StepObj, NullableOptional, Throwable)}</li>
 *   <li>{@link BeforeStepFailure#beforeStepFailure(StepObj, NullableOptional, Throwable)}</li>
 *   <li>{@link StepListener#onStepFailure(StepObj, NullableOptional, Throwable)}</li>
 *   <li>{@link AfterStepFailure#afterStepFailure(StepObj, NullableOptional, Throwable)}</li>
 * </ul>
 */
public interface StebzExtension {

  /**
   * Early extension order.
   */
  int EARLY_ORDER = 0;

  /**
   * Mid-early extension order.
   */
  int MID_EARLY_ORDER = 5000;

  /**
   * Middle extension order.
   */
  int MIDDLE_ORDER = 10000;

  /**
   * Mid-late extension order.
   */
  int MID_LATE_ORDER = 15000;

  /**
   * Late extension order.
   */
  int LATE_ORDER = 20000;

  /**
   * Configures this extension using the given {@code PropertiesReader}. Called once during the Stebz startup.
   *
   * @param properties the startup properties reader
   */
  default void configure(final PropertiesReader properties) {
  }

  /**
   * Returns this extension order.
   *
   * @return this extension order
   */
  default int order() {
    return MIDDLE_ORDER;
  }
}
