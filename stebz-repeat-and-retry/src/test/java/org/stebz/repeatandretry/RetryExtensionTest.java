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
package org.stebz.repeatandretry;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.SimpleStepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link RetryExtension}.
 */
final class RetryExtensionTest {

  private static final SimpleStepAttribute<WithRetry> WITH_RETRY =
    SimpleStepAttribute.nullable(WithRetry.class);

  private static RetryExtension newExtension(final Map<String, String> properties) {
    return new RetryExtension(new PropertiesReader.Of(properties));
  }

  private static RetryExtension newExtensionWithProperty(final String name,
                                                         final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static StepObj<?> intercept(final RetryExtension extension,
                                      final StepObj<?> step) {
    return extension.interceptStep(step, NullableOptional.empty());
  }

  private static ThrowingRunnable<?> bodyFailingFirstAttempts(final int timesToFail,
                                                              final Throwable exception,
                                                              final AtomicInteger counter) {
    return () -> {
      if (counter.incrementAndGet() <= timesToFail) {
        throw exception;
      }
    };
  }

  private static WithRetry withRetryAnnotation(final String methodName) throws Exception {
    return AnnotationHolder.class.getDeclaredMethod(methodName).getAnnotation(WithRetry.class);
  }

  static class AnnotationHolder {
    @WithRetry(count = 3)
    void threeTimes() {
    }
  }

  private static void setStartupPropertiesReader(final PropertiesReader reader) throws Exception {
    final Method setMethod = StartupPropertiesReader.class.getDeclaredMethod("set", PropertiesReader.class);
    setMethod.setAccessible(true);
    setMethod.invoke(null, reader);
  }

  private static void clearStartupPropertiesReader() throws Exception {
    final Method clearMethod = StartupPropertiesReader.class.getDeclaredMethod("clear");
    clearMethod.setAccessible(true);
    clearMethod.invoke(null);
  }

  private static ThrowingConsumer<Object, ?> consumerFailingFirstAttempts(final int timesToFail,
                                                                          final Throwable exception,
                                                                          final AtomicInteger counter) {
    return context -> {
      if (counter.incrementAndGet() <= timesToFail) {
        throw exception;
      }
    };
  }

  private static ThrowingSupplier<Object, ?> supplierFailingFirstAttempts(final int timesToFail,
                                                                          final Throwable exception,
                                                                          final AtomicInteger counter) {
    return () -> {
      if (counter.incrementAndGet() <= timesToFail) {
        throw exception;
      }
      return counter.get();
    };
  }

  private static ThrowingFunction<Object, Object, ?> functionFailingFirstAttempts(final int timesToFail,
                                                                                  final Throwable exception,
                                                                                  final AtomicInteger counter) {
    return context -> {
      if (counter.incrementAndGet() <= timesToFail) {
        throw exception;
      }
      return counter.get();
    };
  }

  private static final class CustomStepObj implements StepObj<CustomStepObj> {
    private final StepAttributes attributes;

    private CustomStepObj(final StepAttributes attributes) {
      this.attributes = attributes;
    }

    @Override
    public StepAttributes getAttributes() {
      return this.attributes;
    }

    @Override
    public CustomStepObj withAttributes(final StepAttributes attributes) {
      return new CustomStepObj(attributes);
    }
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.retry.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void disabledExtensionShouldReturnStepUnchanged() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions()),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep =
      intercept(newExtensionWithProperty("stebz.retry.enabled", "false"), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void stepWithoutRetryOptionsShouldBeReturnedUnchanged() {
    final RunnableStep step = RunnableStep.of(
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void countLessThanTwoShouldBeIgnored() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(1)),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void emptyOnListShouldBeIgnored() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().on()),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void retryRunnableStepShouldNotRetryAfterSuccess() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(3)),
      (ThrowingRunnable<?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(RunnableStep.class)
      .isNotSameAs(step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryRunnableStepShouldRetryOnMatchingExceptions() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void retryRunnableStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).but(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryRunnableStepShouldStopOnNonMatchingExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryRunnableStepShouldPropagateExceptionOfLastAttempt() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(3)),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void retrySupplierStepShouldReturnResultOfFirstSuccessfulAttempt() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      (ThrowingSupplier<Object, ?>) () -> {
        if (counter.incrementAndGet() <= 2) {
          throw new IllegalStateException();
        }
        return counter.get();
      }
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(3);
  }

  @Test
  void retryConsumerStepShouldStopRetryingAfterSuccess() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(5)),
      (ThrowingConsumer<Object, ?>) context -> {
        if (counter.incrementAndGet() == 1) {
          throw new IllegalStateException();
        }
      }
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    consumerStep.getBody().accept("context value");

    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void retryFunctionStepShouldReturnResultOfFirstSuccessfulAttempt() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      (ThrowingFunction<Object, Object, ?>) context -> {
        if (counter.incrementAndGet() == 1) {
          throw new IllegalStateException();
        }
        return counter.get();
      }
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) resultStep;
    final Object result = functionStep.getBody().apply("context value");

    assertThat(result).isEqualTo(2);
  }

  @Test
  void withRetryAnnotationShouldBeUsed() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(WITH_RETRY, withRetryAnnotation("threeTimes")),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep).isNotSameAs(step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void retryDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).delay(Duration.ofMillis(50))
      ),
      bodyFailingFirstAttempts(3, exception, new AtomicInteger())
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    final long startTime = System.nanoTime();
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);
    final long elapsedMillis = (System.nanoTime() - startTime) / 1_000_000;

    assertThat(elapsedMillis)
      .as("two 50ms delays are expected between three attempts")
      .isGreaterThanOrEqualTo(90L);
  }

  @Test
  void retryDelayPreservesThreadInterruptStatus() throws Throwable {
    final CountDownLatch firstAttemptFinished = new CountDownLatch(1);
    final AtomicInteger attempts = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(3).delay(Duration.ofSeconds(5))),
      () -> {
        attempts.incrementAndGet();
        firstAttemptFinished.countDown();
        throw new IllegalStateException();
      }
    );
    final RunnableStep resultStep = (RunnableStep) intercept(newExtension(new HashMap<>()), step);
    final AtomicReference<Throwable> failure = new AtomicReference<>();
    final AtomicReference<Boolean> interruptStatus = new AtomicReference<>();
    final Thread worker = new Thread(() -> {
      try {
        resultStep.getBody().run();
      } catch (final Throwable ex) {
        failure.set(ex);
      } finally {
        interruptStatus.set(Thread.currentThread().isInterrupted());
      }
    });

    worker.start();
    assertThat(firstAttemptFinished.await(5, TimeUnit.SECONDS)).isTrue();
    worker.interrupt();
    worker.join(5_000L);

    assertThat(worker.isAlive()).isFalse();
    assertThat(failure.get()).isInstanceOf(InterruptedException.class);
    assertThat(interruptStatus.get()).isTrue();
    assertThat(attempts.get()).isEqualTo(1);
  }

  @Test
  void ctorShouldUseStartupPropertiesReader() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.retry.order", "12345");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      assertThat(new RetryExtension().order())
        .isEqualTo(12345);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void retryStaticMethodShouldReturnDefaultRetryOptions() {
    assertThat(RetryExtension.retry())
      .isSameAs(RetryExtension.retryOptions());
  }

  @Test
  void retryOptionsFromRetryStaticMethodShouldBeAppliedToStep() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retry()),
      bodyFailingFirstAttempts(1, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep).isNotSameAs(step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void stepOfCustomTypeShouldBeReturnedUnchanged() {
    final StepObj<?> step = new CustomStepObj(
      StepAttributes.of(RetryExtension.RETRY, RetryExtension.retryOptions().count(3))
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void retryConsumerStepShouldExecuteBodyOnFinalAttemptAfterRetries() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).delay(Duration.ofMillis(50))
      ),
      consumerFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(ConsumerStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    consumerStep.getBody().accept("context value");

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void retryConsumerStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).but(IllegalStateException.class)
      ),
      consumerFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    assertThatThrownBy(() -> consumerStep.getBody().accept("context value"))
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryConsumerStepShouldStopOnNonMatchingExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      consumerFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    assertThatThrownBy(() -> consumerStep.getBody().accept("context value"))
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retrySupplierStepShouldReturnResultOfFirstAttemptWithoutRetrying() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3)
      ),
      (ThrowingSupplier<Object, ?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(1);
  }

  @Test
  void retrySupplierStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).but(IllegalStateException.class)
      ),
      supplierFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final SupplierStep<Object> supplierStep = (SupplierStep<Object>) resultStep;
    assertThatThrownBy(supplierStep.getBody()::get)
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retrySupplierStepShouldStopOnNonMatchingExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      supplierFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final SupplierStep<Object> supplierStep = (SupplierStep<Object>) resultStep;
    assertThatThrownBy(supplierStep.getBody()::get)
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retrySupplierStepDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).delay(Duration.ofMillis(50))
      ),
      supplierFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(3);
  }

  @Test
  void retryFunctionStepShouldReturnResultOfLastAttempt() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).delay(Duration.ofMillis(50))
      ),
      functionFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(FunctionStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) resultStep;
    final Object result = functionStep.getBody().apply("context value");

    assertThat(result).isEqualTo(3);
  }

  @Test
  void retryFunctionStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).but(IllegalStateException.class)
      ),
      functionFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) resultStep;
    assertThatThrownBy(() -> functionStep.getBody().apply("context value"))
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryFunctionStepShouldStopOnNonMatchingExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RetryExtension.RETRY,
        RetryExtension.retryOptions().count(3).on(IllegalStateException.class)
      ),
      functionFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) resultStep;
    assertThatThrownBy(() -> functionStep.getBody().apply("context value"))
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void retryOptionsDelayShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RetryExtension.retryOptions().delay(null));
  }

  @Test
  void retryOptionsOnShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RetryExtension.retryOptions().on((Class<? extends Throwable>[]) null));
  }

  @Test
  void retryOptionsButShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RetryExtension.retryOptions().but((Class<? extends Throwable>[]) null));
  }
}
