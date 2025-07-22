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

import org.junit.jupiter.api.Test;
import org.stebz.attribute.StepAttributes;
import org.stebz.step.StepObj;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;
import org.stebz.util.property.PropertiesReader;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.stebz.extension.RetryExtension.retryOptions;

/**
 * Tests for {@link RetryExtension}.
 */
final class RetryExtensionTest {

  @Test
  void retryRunnableStepWithoutException() throws Throwable {
    final ThrowingRunnable<?> originBody = mockRunnable();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(RunnableStep.class);
    final ThrowingRunnable<?> body = ((RunnableStep) resultStep).body();
    assertThat(body)
      .isNotSameAs(originBody);
    body.run();
    verify(originBody, times(1)).run();
  }

  @Test
  void retryRunnableStepWithException() throws Throwable {
    final Throwable originException = new Throwable();
    final ThrowingRunnable<?> originBody = mockRunnableWithException(originException);
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(RunnableStep.class);
    final ThrowingRunnable<?> body = ((RunnableStep) resultStep).body();
    assertThat(body)
      .isNotSameAs(originBody);
    assertThatCode(() -> body.run())
      .isSameAs(originException);
    verify(originBody, times(2)).run();
  }

  @Test
  void retryRunnableStepWithFirstTimeException() throws Throwable {
    final Throwable originException = new Throwable();
    final ThrowingRunnable<?> originBody = mockRunnableWithFirstTimeException(originException);
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(RunnableStep.class);
    final ThrowingRunnable<?> body = ((RunnableStep) resultStep).body();
    assertThat(body)
      .isNotSameAs(originBody);
    body.run();
    verify(originBody, times(2)).run();
  }

  @Test
  void retrySupplierStepWithoutException() throws Throwable {
    final Object originResult = new Object();
    final ThrowingSupplier<Object, ?> originBody = mockSupplier(originResult);
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class);
    @SuppressWarnings("unchecked")
    final ThrowingSupplier<Object, ?> body = ((SupplierStep<Object>) resultStep).body();
    assertThat(body)
      .isNotSameAs(originBody);
    assertThat(body.get())
      .isSameAs(originResult);
    verify(originBody, times(1)).get();
  }

  @Test
  void retrySupplierStepWithException() throws Throwable {
    final Throwable originException = new Throwable();
    final ThrowingSupplier<Object, ?> originBody = mockSupplierWithException(originException);
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class);
    @SuppressWarnings("unchecked")
    final ThrowingSupplier<Object, ?> body = ((SupplierStep<Object>) resultStep).body();
    assertThat(body)
      .isNotSameAs(originBody);
    assertThatCode(() -> body.get())
      .isSameAs(originException);
    verify(originBody, times(2)).get();
  }

  @Test
  void retrySupplierStepWithFirstTimeException() throws Throwable {
    final Throwable originException = new Throwable();
    final Object originResult = new Object();
    final ThrowingSupplier<Object, ?> originBody = mockSupplierWithFirstTimeException(originException, originResult);
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY, retryOptions()
      ),
      originBody
    );
    final RetryExtension extension = new RetryExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class);
    @SuppressWarnings("unchecked")
    final ThrowingSupplier<Object, ?> body = ((SupplierStep<Object>) resultStep).body();

    assertThat(body)
      .isNotSameAs(originBody);
    assertThat(body.get())
      .isSameAs(originResult);
    verify(originBody, times(2)).get();
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnable() {
    return mock(ThrowingRunnable.class);
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnableWithException(final Throwable exception) {
    final ThrowingRunnable<Error> mock = mock(ThrowingRunnable.class);
    doThrow(exception).when(mock).run();
    return mock;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingRunnable<Error> mockRunnableWithFirstTimeException(final Throwable exception) {
    final ThrowingRunnable<Error> mock = mock(ThrowingRunnable.class);
    doThrow(exception).doNothing().when(mock).run();
    return mock;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingSupplier<Object, Error> mockSupplier(final Object result) {
    final ThrowingSupplier<Object, Error> mock = mock(ThrowingSupplier.class);
    doReturn(result).when(mock).get();
    return mock;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingSupplier<Object, Error> mockSupplierWithException(final Throwable exception) {
    final ThrowingSupplier<Object, Error> mock = mock(ThrowingSupplier.class);
    doThrow(exception).when(mock).get();
    return mock;
  }

  @SuppressWarnings("unchecked")
  private static ThrowingSupplier<Object, Error> mockSupplierWithFirstTimeException(final Throwable exception,
                                                                                    final Object result) {
    final ThrowingSupplier<Object, Error> mock = mock(ThrowingSupplier.class);
    doThrow(exception).doReturn(result).when(mock).get();

//    when(mock.get())
//      .thenThrow(exception)
//      .thenReturn(result);
    return mock;
  }
}
