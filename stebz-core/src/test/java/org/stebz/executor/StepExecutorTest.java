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
package org.stebz.executor;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.stebz.extension.AfterStepFailure;
import org.stebz.extension.AfterStepStart;
import org.stebz.extension.AfterStepSuccess;
import org.stebz.extension.BeforeStepFailure;
import org.stebz.extension.BeforeStepStart;
import org.stebz.extension.BeforeStepSuccess;
import org.stebz.extension.InterceptStep;
import org.stebz.extension.InterceptStepContext;
import org.stebz.extension.InterceptStepException;
import org.stebz.extension.InterceptStepResult;
import org.stebz.extension.StebzExtension;
import org.stebz.listener.StepListener;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalAnswers.returnsLastArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link StepExecutor}.
 */
final class StepExecutorTest {

  @Test
  void ctorShouldThrowExceptionForNullExtensionsArg() {
    final StebzExtension[] extensions = null;
    final StepListener[] listeners = new StepListener[0];

    assertThatCode(() -> new StepExecutor.Of(listeners, extensions))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ctorShouldThrowExceptionForNullListenersArg() {
    final StebzExtension[] extensions = new StebzExtension[0];
    final StepListener[] listeners = null;

    assertThatCode(() -> new StepExecutor.Of(listeners, extensions))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void executorShouldWorkWithRunnableStep() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final ThrowingRunnable<?> runnable = mockRunnable();
    final RunnableStep step = mockRunnableStep(runnable);

    executor.execute(step);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepContext(same(step), same(NullableOptional.empty()));
    verify(extension, never()).interceptStepException(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).interceptStepResult(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).beforeStepFailure(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).afterStepFailure(same(step), same(NullableOptional.empty()), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepFailure(same(step), same(NullableOptional.empty()), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStep(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(listener, times(1)).onStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).beforeStepSuccess(same(step), same(NullableOptional.empty()), any());
    inOrder.verify(listener, times(1)).onStepSuccess(same(step), same(NullableOptional.empty()), any());
    inOrder.verify(extension, times(1)).afterStepSuccess(same(step), same(NullableOptional.empty()), any());
  }

  @Test
  void executorShouldWorkWithRunnableStepWithException() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Throwable exception = new Throwable();
    final ThrowingRunnable<Error> runnable = mockRunnableWithException(exception);
    final RunnableStep step = mockRunnableStep(runnable);

    assertThatCode(() -> executor.execute(step))
      .isSameAs(exception);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepContext(same(step), same(NullableOptional.empty()));
    verify(extension, never()).interceptStepResult(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).beforeStepSuccess(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).afterStepSuccess(same(step), same(NullableOptional.empty()), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepSuccess(same(step), same(NullableOptional.empty()), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStep(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(listener, times(1)).onStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).interceptStepException(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(extension, times(1)).beforeStepFailure(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(listener, times(1)).onStepFailure(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(extension, times(1)).afterStepFailure(same(step), same(NullableOptional.empty()), same(exception));
  }

  @Test
  void executorShouldWorkWithConsumerStep() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final ThrowingConsumer<Object, Error> consumer = mockConsumer();
    final ConsumerStep<Object> step = mockConsumerStep(consumer);
    final Object contextValue = new Object();
    final NullableOptional<Object> context = NullableOptional.of(contextValue);

    executor.execute(step, contextValue);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepException(same(step), eq(context), any());
    verify(extension, never()).interceptStepResult(same(step), eq(context), any());
    verify(extension, never()).beforeStepFailure(same(step), eq(context), any());
    verify(extension, never()).afterStepFailure(same(step), eq(context), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepFailure(same(step), eq(context), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStepContext(same(step), same(contextValue));
    inOrder.verify(extension, times(1)).interceptStep(same(step), eq(context));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), eq(context));
    inOrder.verify(listener, times(1)).onStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).beforeStepSuccess(same(step), eq(context), any());
    inOrder.verify(listener, times(1)).onStepSuccess(same(step), eq(context), any());
    inOrder.verify(extension, times(1)).afterStepSuccess(same(step), eq(context), any());
  }

  @Test
  void executorShouldWorkWithConsumerStepWithException() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Throwable exception = new Throwable();
    final ThrowingConsumer<Object, Error> consumer = mockConsumerWithException(exception);
    final ConsumerStep<Object> step = mockConsumerStep(consumer);
    final Object contextValue = new Object();
    final NullableOptional<Object> context = NullableOptional.of(contextValue);

    assertThatCode(() -> executor.execute(step, contextValue))
      .isSameAs(exception);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepResult(same(step), eq(context), any());
    verify(extension, never()).beforeStepSuccess(same(step), eq(context), any());
    verify(extension, never()).afterStepSuccess(same(step), eq(context), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepSuccess(same(step), eq(context), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStepContext(same(step), same(contextValue));
    inOrder.verify(extension, times(1)).interceptStep(same(step), eq(context));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), eq(context));
    inOrder.verify(listener, times(1)).onStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), eq(context));

    inOrder.verify(extension, times(1)).interceptStepException(same(step), eq(context), same(exception));
    inOrder.verify(extension, times(1)).beforeStepFailure(same(step), eq(context), same(exception));
    inOrder.verify(listener, times(1)).onStepFailure(same(step), eq(context), same(exception));
    inOrder.verify(extension, times(1)).afterStepFailure(same(step), eq(context), same(exception));
  }

  @Test
  void executorShouldWorkWithSupplierStep() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Object resultValue = new Object();
    final NullableOptional<Object> result = NullableOptional.of(resultValue);
    final ThrowingSupplier<Object, Error> supplier = mockSupplier(resultValue);
    final SupplierStep<Object> step = mockSupplierStep(supplier);

    assertThat(executor.execute(step))
      .isSameAs(resultValue);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepContext(same(step), same(NullableOptional.empty()));
    verify(extension, never()).interceptStepException(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).beforeStepFailure(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).afterStepFailure(same(step), same(NullableOptional.empty()), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepFailure(same(step), same(NullableOptional.empty()), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStep(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(listener, times(1)).onStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).interceptStepResult(same(step), same(NullableOptional.empty()), same(resultValue));
    inOrder.verify(extension, times(1)).beforeStepSuccess(same(step), same(NullableOptional.empty()), eq(result));
    inOrder.verify(listener, times(1)).onStepSuccess(same(step), same(NullableOptional.empty()), eq(result));
    inOrder.verify(extension, times(1)).afterStepSuccess(same(step), same(NullableOptional.empty()), eq(result));
  }

  @Test
  void executorShouldWorkWithSupplierStepWithException() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Throwable exception = new Throwable();
    final ThrowingSupplier<Object, Error> supplier = mockSupplierWithException(exception);
    final SupplierStep<Object> step = mockSupplierStep(supplier);

    assertThatCode(() -> executor.execute(step))
      .isSameAs(exception);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepContext(same(step), same(NullableOptional.empty()));
    verify(extension, never()).interceptStepResult(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).beforeStepSuccess(same(step), same(NullableOptional.empty()), any());
    verify(extension, never()).afterStepSuccess(same(step), same(NullableOptional.empty()), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepSuccess(same(step), same(NullableOptional.empty()), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStep(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(listener, times(1)).onStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), same(NullableOptional.empty()));
    inOrder.verify(extension, times(1)).interceptStepException(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(extension, times(1)).beforeStepFailure(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(listener, times(1)).onStepFailure(same(step), same(NullableOptional.empty()), same(exception));
    inOrder.verify(extension, times(1)).afterStepFailure(same(step), same(NullableOptional.empty()), same(exception));
  }

  @Test
  void executorShouldWorkWithFunctionStep() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Object resultValue = new Object();
    final NullableOptional<Object> result = NullableOptional.of(resultValue);
    final ThrowingFunction<Object, Object, Error> function = mockFunction(resultValue);
    final FunctionStep<Object, Object> step = mockFunctionStep(function);
    final Object contextValue = new Object();
    final NullableOptional<Object> context = NullableOptional.of(contextValue);

    assertThat(executor.execute(step, contextValue))
      .isSameAs(resultValue);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepException(same(step), eq(context), any());
    verify(extension, never()).beforeStepFailure(same(step), eq(context), any());
    verify(extension, never()).afterStepFailure(same(step), eq(context), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepFailure(same(step), eq(context), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStepContext(same(step), same(contextValue));
    inOrder.verify(extension, times(1)).interceptStep(same(step), eq(context));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), eq(context));
    inOrder.verify(listener, times(1)).onStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).interceptStepResult(same(step), eq(context), same(resultValue));
    inOrder.verify(extension, times(1)).beforeStepSuccess(same(step), eq(context), eq(result));
    inOrder.verify(listener, times(1)).onStepSuccess(same(step), eq(context), eq(result));
    inOrder.verify(extension, times(1)).afterStepSuccess(same(step), eq(context), eq(result));
  }

  @Test
  void executorShouldWorkWithFunctionStepWithException() {
    final ComplexExtension extension = mockStebzExtension();
    final StepListener listener = mockStepListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );
    final Throwable exception = new Throwable();
    final ThrowingFunction<Object, Object, Error> function = mockFunctionWithException(exception);
    final FunctionStep<Object, Object> step = mockFunctionStep(function);
    final Object contextValue = new Object();
    final NullableOptional<Object> context = NullableOptional.of(contextValue);

    assertThatCode(() -> executor.execute(step, contextValue))
      .isSameAs(exception);

    verify(extension, never()).configure(any());
    verify(extension, never()).order();
    verify(extension, never()).interceptStepResult(same(step), eq(context), any());
    verify(extension, never()).beforeStepSuccess(same(step), eq(context), any());
    verify(extension, never()).afterStepSuccess(same(step), eq(context), any());
    verify(listener, never()).configure(any());
    verify(listener, never()).order();
    verify(listener, never()).onStepSuccess(same(step), eq(context), any());

    final InOrder inOrder = inOrder(extension, listener);
    inOrder.verify(extension, times(1)).interceptStepContext(same(step), same(contextValue));
    inOrder.verify(extension, times(1)).interceptStep(same(step), eq(context));
    inOrder.verify(extension, times(1)).beforeStepStart(same(step), eq(context));
    inOrder.verify(listener, times(1)).onStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).afterStepStart(same(step), eq(context));
    inOrder.verify(extension, times(1)).interceptStepException(same(step), eq(context), same(exception));
    inOrder.verify(extension, times(1)).beforeStepFailure(same(step), eq(context), same(exception));
    inOrder.verify(listener, times(1)).onStepFailure(same(step), eq(context), same(exception));
    inOrder.verify(extension, times(1)).afterStepFailure(same(step), eq(context), same(exception));
  }

  private static ComplexExtension mockStebzExtension() {
    final ComplexExtension extension = mock(ComplexExtension.class);
    doCallRealMethod().when(extension).order();
    doAnswer(returnsFirstArg()).when(extension).interceptStep(any(), any());
    doAnswer(returnsLastArg()).when(extension).interceptStepContext(any(), any());
    doAnswer(returnsLastArg()).when(extension).interceptStepResult(any(), any(), any());
    doAnswer(returnsLastArg()).when(extension).interceptStepException(any(), any(), any());
    return extension;
  }

  private static StepListener mockStepListener() {
    return mock(StepListener.class);
  }

  @SuppressWarnings("unchecked")
  private static NullableOptional<Object> mockEmptyOptionalValue() {
    final NullableOptional<Object> nullableOptional = mock(NullableOptional.class);
    doThrow(NoSuchElementException.class).when(nullableOptional).get();
    doReturn(false).when(nullableOptional).isPresent();
    doReturn(true).when(nullableOptional).isEmpty();
    return nullableOptional;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnable() {
    return mock(ThrowingRunnable.class);
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnableWithException(final Throwable exception) {
    final ThrowingRunnable<Error> runnable = mock(ThrowingRunnable.class);
    doThrow(exception).when(runnable).run();
    return runnable;
  }

  private static RunnableStep mockRunnableStep(final ThrowingRunnable<?> body) {
    final RunnableStep step = mock(RunnableStep.class);
    doReturn(body).when(step).body();
    return step;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingConsumer<Object, Error> mockConsumer() {
    return mock(ThrowingConsumer.class);
  }

  @SuppressWarnings("unchecked")
  private static ThrowingConsumer<Object, Error> mockConsumerWithException(final Throwable exception) {
    final ThrowingConsumer<Object, Error> consumer = mock(ThrowingConsumer.class);
    doThrow(exception).when(consumer).accept(any());
    return consumer;
  }

  @SuppressWarnings("unchecked")
  private static ConsumerStep<Object> mockConsumerStep(final ThrowingConsumer<Object, Error> body) {
    final ConsumerStep<Object> step = mock(ConsumerStep.class);
    doReturn(body).when(step).body();
    return step;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingSupplier<Object, Error> mockSupplier(final Object result) {
    final ThrowingSupplier<Object, Error> supplier = mock(ThrowingSupplier.class);
    doReturn(result).when(supplier).get();
    return supplier;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingSupplier<Object, Error> mockSupplierWithException(final Throwable exception) {
    final ThrowingSupplier<Object, Error> consumer = mock(ThrowingSupplier.class);
    doThrow(exception).when(consumer).get();
    return consumer;
  }

  @SuppressWarnings("unchecked")
  private static SupplierStep<Object> mockSupplierStep(final ThrowingSupplier<Object, Error> body) {
    final SupplierStep<Object> step = mock(SupplierStep.class);
    doReturn(body).when(step).body();
    return step;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingFunction<Object, Object, Error> mockFunction(final Object result) {
    final ThrowingFunction<Object, Object, Error> function = mock(ThrowingFunction.class);
    doReturn(result).when(function).apply(any());
    return function;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingFunction<Object, Object, Error> mockFunctionWithException(final Throwable exception) {
    final ThrowingFunction<Object, Object, Error> function = mock(ThrowingFunction.class);
    doThrow(exception).when(function).apply(any());
    return function;
  }

  @SuppressWarnings("unchecked")
  private static FunctionStep<Object, Object> mockFunctionStep(final ThrowingFunction<Object, Object, Error> body) {
    final FunctionStep<Object, Object> step = mock(FunctionStep.class);
    doReturn(body).when(step).body();
    return step;
  }

  private interface ComplexExtension extends AfterStepFailure, AfterStepStart, AfterStepSuccess, BeforeStepFailure,
    BeforeStepStart, BeforeStepSuccess, InterceptStep, InterceptStepContext, InterceptStepException,
    InterceptStepResult {
  }
}
