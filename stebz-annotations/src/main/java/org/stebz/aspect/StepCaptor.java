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
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingFunction2;
import org.stebz.util.function.ThrowingFunction3;
import org.stebz.util.function.ThrowingFunction4;
import org.stebz.util.function.ThrowingFunction5;
import org.stebz.util.function.ThrowingFunction6;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import static org.stebz.aspect.StepAspects.capturedAttributes;
import static org.stebz.aspect.StepAspects.capturedJointPoint;
import static org.stebz.aspect.StepAspects.disableCaptureMode;
import static org.stebz.aspect.StepAspects.enableCaptureMode;

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
      ThrowingSupplier.unchecked(reference).get();
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction.unchecked(reference).apply(value);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction2.unchecked(reference).apply(value1, value2);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction3.unchecked(reference).apply(value1, value2, value3);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction4.unchecked(reference).apply(value1, value2, value3, value4);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction5.unchecked(reference).apply(value1, value2, value3, value4, value5);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
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
      ThrowingFunction6.unchecked(reference).apply(value1, value2, value3, value4, value5, value6);
      return supplierStep();
    } finally {
      disableCaptureMode();
    }
  }

  private static RunnableStep runnableStep() {
    final StepAttributes attributes = capturedAttributes();
    final ProceedingJoinPoint joinPoint = capturedJointPoint();
    if (attributes == null || joinPoint == null) {
      throw new IllegalArgumentException("reference does not contain any steps");
    }
    return new RunnableStep.Of(attributes, joinPoint::proceed);
  }

  @SuppressWarnings("unchecked")
  private static <R> SupplierStep<R> supplierStep() {
    final StepAttributes attributes = capturedAttributes();
    final ProceedingJoinPoint joinPoint = capturedJointPoint();
    if (attributes == null || joinPoint == null) {
      throw new IllegalArgumentException("reference does not contain any steps");
    }
    return (SupplierStep<R>) new SupplierStep.Of<>(
      attributes,
      joinPoint.getSignature() instanceof ConstructorSignature
        ? () -> { joinPoint.proceed(); return joinPoint.getThis(); }
        : joinPoint::proceed
    );
  }
}
