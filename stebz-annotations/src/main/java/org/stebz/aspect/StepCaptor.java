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
package org.stebz.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.stebz.attribute.StepAttributes;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingConsumer2;
import org.stebz.util.function.ThrowingConsumer3;
import org.stebz.util.function.ThrowingConsumer4;
import org.stebz.util.function.ThrowingConsumer5;
import org.stebz.util.function.ThrowingConsumer6;
import org.stebz.util.function.ThrowingConsumer7;
import org.stebz.util.function.ThrowingConsumer8;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingFunction2;
import org.stebz.util.function.ThrowingFunction3;
import org.stebz.util.function.ThrowingFunction4;
import org.stebz.util.function.ThrowingFunction5;
import org.stebz.util.function.ThrowingFunction6;
import org.stebz.util.function.ThrowingFunction7;
import org.stebz.util.function.ThrowingFunction8;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.lang.reflect.Constructor;

import static org.stebz.aspect.StepAspects.capturedAttributes;
import static org.stebz.aspect.StepAspects.capturedJointPoint;
import static org.stebz.aspect.StepAspects.disableCaptureMode;
import static org.stebz.aspect.StepAspects.disableIgnoreMode;
import static org.stebz.aspect.StepAspects.enableCaptureMode;
import static org.stebz.aspect.StepAspects.enableIgnoreMode;

/**
 * Step captor. Contains methods for step capturing.
 */
public final class StepCaptor {

  /**
   * Utility class ctor.
   */
  private StepCaptor() {
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static RunnableStep captured(final ThrowingRunnable<?> reference) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingRunnable.unchecked(reference).run();
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for {@link #captured(ThrowingRunnable)} method.
   *
   * @param reference the step reference
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static RunnableStep ref(final ThrowingRunnable<?> reference) {
    return captured(reference);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value     the reference value
   * @param <T>       the type of the reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T> RunnableStep captured(final ThrowingConsumer<? super T, ?> reference,
                                          final T value) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer.unchecked(reference).accept(value);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for {@link #captured(ThrowingConsumer, Object)}
   * method.
   *
   * @param reference the step reference
   * @param value     the reference value
   * @param <T>       the type of the reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T> RunnableStep ref(final ThrowingConsumer<? super T, ?> reference,
                                     final T value) {
    return captured(reference, value);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2> RunnableStep captured(final ThrowingConsumer2<? super T1, ? super T2, ?> reference,
                                               final T1 value1,
                                               final T2 value2) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer2.unchecked(reference).accept(value1, value2);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer2, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2> RunnableStep ref(final ThrowingConsumer2<? super T1, ? super T2, ?> reference,
                                          final T1 value1,
                                          final T2 value2) {
    return captured(reference, value1, value2);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3> RunnableStep captured(
    final ThrowingConsumer3<? super T1, ? super T2, ? super T3, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer3.unchecked(reference).accept(value1, value2, value3);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer3, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3> RunnableStep ref(
    final ThrowingConsumer3<? super T1, ? super T2, ? super T3, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3
  ) {
    return captured(reference, value1, value2, value3);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4> RunnableStep captured(
    final ThrowingConsumer4<? super T1, ? super T2, ? super T3, ? super T4, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer4.unchecked(reference).accept(value1, value2, value3, value4);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer4, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4> RunnableStep ref(
    final ThrowingConsumer4<? super T1, ? super T2, ? super T3, ? super T4, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4
  ) {
    return captured(reference, value1, value2, value3, value4);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5> RunnableStep captured(
    final ThrowingConsumer5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer5.unchecked(reference).accept(value1, value2, value3, value4, value5);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer5, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5> RunnableStep ref(
    final ThrowingConsumer5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5
  ) {
    return captured(reference, value1, value2, value3, value4, value5);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6> RunnableStep captured(
    final ThrowingConsumer6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer6.unchecked(reference).accept(value1, value2, value3, value4, value5, value6);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer6, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6> RunnableStep ref(
    final ThrowingConsumer6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7> RunnableStep captured(
    final ThrowingConsumer7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer7.unchecked(reference).accept(value1, value2, value3, value4, value5, value6, value7);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer7, Object, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7> RunnableStep ref(
    final ThrowingConsumer7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6, value7);
  }

  /**
   * Returns {@code RunnableStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param value8    the eighth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <T8>      the type of the eighth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> RunnableStep captured(
    final ThrowingConsumer8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7,
    final T8 value8
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      ThrowingConsumer8.unchecked(reference).accept(value1, value2, value3, value4, value5, value6, value7, value8);
      return runnableStep();
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code RunnableStep} specified in the reference. Alias for
   * {@link #captured(ThrowingConsumer8, Object, Object, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param value8    the eighth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <T8>      the type of the eighth reference value
   * @return {@code RunnableStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> RunnableStep ref(
    final ThrowingConsumer8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7,
    final T8 value8
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6, value7, value8);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <R> SupplierStep<R> captured(final ThrowingSupplier<? extends R, ?> reference) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingSupplier.unchecked(reference).get());
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for {@link #captured(ThrowingSupplier)} method.
   *
   * @param reference the step reference
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <R> SupplierStep<R> ref(final ThrowingSupplier<? extends R, ?> reference) {
    return captured(reference);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value     the reference value
   * @param <T>       the type of the reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T, R> SupplierStep<R> captured(final ThrowingFunction<? super T, ? extends R, ?> reference,
                                                final T value) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction.unchecked(reference).apply(value));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for {@link #captured(ThrowingFunction, Object)}
   * method.
   *
   * @param reference the step reference
   * @param value     the reference value
   * @param <T>       the type of the reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T, R> SupplierStep<R> ref(final ThrowingFunction<? super T, ? extends R, ?> reference,
                                           final T value) {
    return captured(reference, value);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, R> SupplierStep<R> captured(
    final ThrowingFunction2<? super T1, ? super T2, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction2.unchecked(reference).apply(value1, value2));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction2, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, R> SupplierStep<R> ref(
    final ThrowingFunction2<? super T1, ? super T2, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2
  ) {
    return captured(reference, value1, value2);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, R> SupplierStep<R> captured(
    final ThrowingFunction3<? super T1, ? super T2, ? super T3, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction3.unchecked(reference).apply(value1, value2, value3));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction3, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, R> SupplierStep<R> ref(
    final ThrowingFunction3<? super T1, ? super T2, ? super T3, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3
  ) {
    return captured(reference, value1, value2, value3);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, R> SupplierStep<R> captured(
    final ThrowingFunction4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction4.unchecked(reference).apply(value1, value2, value3, value4));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction4, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, R> SupplierStep<R> ref(
    final ThrowingFunction4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4
  ) {
    return captured(reference, value1, value2, value3, value4);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, R> SupplierStep<R> captured(
    final ThrowingFunction5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction5.unchecked(reference).apply(value1, value2, value3, value4, value5));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction5, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, R> SupplierStep<R> ref(
    final ThrowingFunction5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5
  ) {
    return captured(reference, value1, value2, value3, value4, value5);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, R> SupplierStep<R> captured(
    final ThrowingFunction6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction6.unchecked(reference).apply(value1, value2, value3, value4, value5, value6));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction6, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, R> SupplierStep<R> ref(
    final ThrowingFunction6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, R> SupplierStep<R> captured(
    final ThrowingFunction7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction7.unchecked(reference)
        .apply(value1, value2, value3, value4, value5, value6, value7));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction7, Object, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, R> SupplierStep<R> ref(
    final ThrowingFunction7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6, value7);
  }

  /**
   * Returns {@code SupplierStep} specified in the reference.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param value8    the eighth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <T8>      the type of the eighth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> SupplierStep<R> captured(
    final ThrowingFunction8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7,
    final T8 value8
  ) {
    if (reference == null) { throw new NullPointerException("reference arg is null"); }
    enableCaptureMode();
    try {
      return supplierStep(ThrowingFunction8.unchecked(reference)
        .apply(value1, value2, value3, value4, value5, value6, value7, value8));
    } finally {
      disableCaptureMode();
    }
  }

  /**
   * Returns {@code SupplierStep} specified in the reference. Alias for
   * {@link #captured(ThrowingFunction8, Object, Object, Object, Object, Object, Object, Object, Object)} method.
   *
   * @param reference the step reference
   * @param value1    the first reference value
   * @param value2    the second reference value
   * @param value3    the third reference value
   * @param value4    the fourth reference value
   * @param value5    the fifth reference value
   * @param value6    the sixth reference value
   * @param value7    the seventh reference value
   * @param value8    the eighth reference value
   * @param <T1>      the type of the first reference value
   * @param <T2>      the type of the second reference value
   * @param <T3>      the type of the third reference value
   * @param <T4>      the type of the fourth reference value
   * @param <T5>      the type of the fifth reference value
   * @param <T6>      the type of the sixth reference value
   * @param <T7>      the type of the seventh reference value
   * @param <T8>      the type of the eighth reference value
   * @param <R>       the type of the step result
   * @return {@code SupplierStep} specified in the reference
   * @throws NullPointerException     if {@code reference} arg is null
   * @throws IllegalArgumentException if {@code reference} does not contain any steps or if {@code reference} contains
   *                                  more than one step
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> SupplierStep<R> ref(
    final ThrowingFunction8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R, ?> reference,
    final T1 value1,
    final T2 value2,
    final T3 value3,
    final T4 value4,
    final T5 value5,
    final T6 value6,
    final T7 value7,
    final T8 value8
  ) {
    return captured(reference, value1, value2, value3, value4, value5, value6, value7, value8);
  }

  private static RunnableStep runnableStep() {
    final StepAttributes attributes = capturedAttributes();
    final ProceedingJoinPoint joinPoint = capturedJointPoint();
    if (attributes == null || joinPoint == null) {
      throw new IllegalArgumentException("reference does not contain any steps");
    }
    return new RunnableStep.Of(
      attributes,
      joinPoint.getSignature() instanceof ConstructorSignature
        ? new CtorStepBody<>(joinPoint)::get
        : joinPoint::proceed
    );
  }

  @SuppressWarnings("unchecked")
  private static <R> SupplierStep<R> supplierStep(final R result) {
    final StepAttributes attributes = capturedAttributes();
    final ProceedingJoinPoint joinPoint = capturedJointPoint();
    if (attributes == null || joinPoint == null) {
      throw new IllegalArgumentException("reference does not contain any steps");
    }
    if (joinPoint.getSignature() instanceof ConstructorSignature) {
      final ThrowingSupplier<R, ?> body = new CtorStepBody<>(joinPoint);
      return new SupplierStep.Of<>(
        attributes,
        result == null || result == joinPoint.getThis()
          ? body
          : () -> { body.get(); return result; }
      );
    } else {
      return (SupplierStep<R>) new SupplierStep.Of<>(
        attributes,
        result == null
          ? joinPoint::proceed
          : () -> { joinPoint.proceed(); return result; }
      );
    }
  }

  private static final class CtorStepBody<R> implements ThrowingSupplier<R, Throwable> {
    /**
     * Possible values:
     * <p>0 -> return instance via join point
     * <p>1 -> init ctor and args values and return new instance via reflection
     * <p>2 -> return new instance via reflection
     */
    private volatile int state = 0;
    private volatile Constructor<?> ctor = null;
    private volatile Object[] args = null;
    private volatile ProceedingJoinPoint joinPoint;

    private CtorStepBody(final ProceedingJoinPoint joinPoint) {
      this.joinPoint = joinPoint;
    }

    @Override
    public R get() throws Throwable {
      if (this.state == 2) {
        return newInstance(this.ctor, this.args);
      }
      ProceedingJoinPoint storedJoinPoint = null;
      int currentState = 0;
      synchronized (this) {
        switch (this.state) {
          case 0:
            storedJoinPoint = this.joinPoint;
            this.state = 1;
            break;
          case 1:
            this.ctor = ((ConstructorSignature) this.joinPoint.getSignature()).getConstructor();
            this.ctor.setAccessible(true);
            this.args = this.joinPoint.getArgs();
            this.joinPoint = null;
            this.state = 2;
            currentState = 1;
            break;
          case 2:
            currentState = 2;
        }
      }
      return currentState == 0
        ? joinPointInstance(storedJoinPoint)
        : newInstance(this.ctor, this.args);
    }

    @SuppressWarnings("unchecked")
    private static <R> R joinPointInstance(final ProceedingJoinPoint joinPoint) throws Throwable {
      joinPoint.proceed();
      return (R) joinPoint.getThis();
    }

    @SuppressWarnings("unchecked")
    private static <R> R newInstance(final Constructor<?> ctor,
                                     final Object[] args) throws ReflectiveOperationException {
      enableIgnoreMode();
      try {
        return (R) ctor.newInstance(args);
      } finally {
        disableIgnoreMode();
      }
    }
  }
}
