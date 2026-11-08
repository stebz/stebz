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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link RepeatExtension}.
 */
final class RepeatExtensionTest {

  private static final SimpleStepAttribute<WithRepeat> WITH_REPEAT =
    SimpleStepAttribute.nullable(WithRepeat.class);

  private static RepeatExtension newExtension(final Map<String, String> properties) {
    return new RepeatExtension(new PropertiesReader.Of(properties));
  }

  private static RepeatExtension newExtensionWithProperty(final String name,
                                                          final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static StepObj<?> intercept(final RepeatExtension extension,
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

  private static WithRepeat withRepeatAnnotation(final String methodName) throws Exception {
    return AnnotationHolder.class.getDeclaredMethod(methodName).getAnnotation(WithRepeat.class);
  }

  static class AnnotationHolder {
    @WithRepeat(count = 3)
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
    assertThat(newExtensionWithProperty("stebz.repeat.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void disabledExtensionShouldReturnStepUnchanged() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions()),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep =
      intercept(newExtensionWithProperty("stebz.repeat.enabled", "false"), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void stepWithoutRepeatOptionsShouldBeReturnedUnchanged() {
    final RunnableStep step = RunnableStep.of(
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void countLessThanTwoShouldBeIgnored() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(1)),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void repeatRunnableStepShouldExecuteBodyGivenCountTimes() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3)),
      (ThrowingRunnable<?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(RunnableStep.class)
      .isNotSameAs(step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void repeatRunnableStepShouldIgnoreSkippedExceptionsOnIntermediateAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void repeatRunnableStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).but(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void repeatRunnableStepShouldStopOnNonSkippedExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(3, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void repeatRunnableStepShouldPropagateExceptionOfLastAttempt() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(2).skip(IllegalStateException.class)
      ),
      bodyFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThatThrownBy(() -> ((RunnableStep) resultStep).getBody().run())
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void repeatSupplierStepShouldReturnResultOfLastAttempt() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3)),
      (ThrowingSupplier<Object, ?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(3);
  }

  @Test
  void repeatConsumerStepShouldPassContextAndExecuteBodyGivenCountTimes() throws Throwable {
    final List<Object> contexts = new ArrayList<>();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3)),
      (ThrowingConsumer<Object, ?>) contexts::add
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(ConsumerStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    consumerStep.getBody().accept("context value");

    assertThat(contexts)
      .hasSize(3)
      .containsOnly("context value");
  }

  @Test
  void repeatFunctionStepShouldReturnResultOfLastAttempt() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3)),
      (ThrowingFunction<Object, Object, ?>) context -> counter.incrementAndGet()
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
  void withRepeatAnnotationShouldBeUsed() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(WITH_REPEAT, withRepeatAnnotation("threeTimes")),
      (ThrowingRunnable<?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep).isNotSameAs(step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void repeatDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).delay(Duration.ofMillis(50))
      ),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    final long startTime = System.nanoTime();
    ((RunnableStep) resultStep).getBody().run();
    final long elapsedMillis = (System.nanoTime() - startTime) / 1_000_000;

    assertThat(elapsedMillis)
      .as("two 50ms delays are expected between three attempts")
      .isGreaterThanOrEqualTo(90L);
  }

  @Test
  void repeatDelayPreservesThreadInterruptStatus() throws Throwable {
    final CountDownLatch firstAttemptFinished = new CountDownLatch(1);
    final AtomicInteger attempts = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3).delay(Duration.ofSeconds(5))),
      () -> {
        attempts.incrementAndGet();
        firstAttemptFinished.countDown();
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
    properties.put("stebz.repeat.order", "12345");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      assertThat(new RepeatExtension().order())
        .isEqualTo(12345);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void repeatStaticMethodShouldReturnDefaultRepeatOptions() {
    assertThat(RepeatExtension.repeat())
      .isSameAs(RepeatExtension.repeatOptions());
  }

  @Test
  void repeatOptionsFromRepeatStaticMethodShouldBeAppliedToStep() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeat()),
      (ThrowingRunnable<?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep).isNotSameAs(step);
    ((RunnableStep) resultStep).getBody().run();

    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void stepOfCustomTypeShouldBeReturnedUnchanged() {
    final StepObj<?> step = new CustomStepObj(
      StepAttributes.of(RepeatExtension.REPEAT, RepeatExtension.repeatOptions().count(3))
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
  }

  @Test
  void repeatConsumerStepShouldIgnoreSkippedExceptionsOnIntermediateAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
      ),
      consumerFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    consumerStep.getBody().accept("context value");

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void repeatConsumerStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).but(IllegalStateException.class)
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
  void repeatConsumerStepShouldStopOnNonSkippedExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
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
  void repeatConsumerStepShouldPropagateExceptionOfLastAttempt() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(2).skip(IllegalStateException.class)
      ),
      consumerFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    assertThatThrownBy(() -> consumerStep.getBody().accept("context value"))
      .isSameAs(exception);

    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void repeatConsumerStepDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final ConsumerStep<Object> step = ConsumerStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).delay(Duration.ofMillis(50))
      ),
      (ThrowingConsumer<Object, ?>) context -> counter.incrementAndGet()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) resultStep;
    consumerStep.getBody().accept("context value");

    assertThat(counter.get()).isEqualTo(3);
  }

  @Test
  void repeatSupplierStepShouldIgnoreSkippedExceptionsOnIntermediateAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
      ),
      supplierFailingFirstAttempts(2, exception, counter)
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    assertThat(resultStep)
      .isInstanceOf(SupplierStep.class)
      .isNotSameAs(step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(3);
  }

  @Test
  void repeatSupplierStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).but(IllegalStateException.class)
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
  void repeatSupplierStepShouldStopOnNonSkippedExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
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
  void repeatSupplierStepDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final SupplierStep<Object> step = SupplierStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).delay(Duration.ofMillis(50))
      ),
      (ThrowingSupplier<Object, ?>) counter::incrementAndGet
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final Object result = ((SupplierStep<Object>) resultStep).getBody().get();

    assertThat(result).isEqualTo(3);
  }

  @Test
  void repeatFunctionStepShouldIgnoreSkippedExceptionsOnIntermediateAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
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
  void repeatFunctionStepShouldStopOnButExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalStateException exception = new IllegalStateException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).but(IllegalStateException.class)
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
  void repeatFunctionStepShouldStopOnNonSkippedExceptions() {
    final AtomicInteger counter = new AtomicInteger();
    final IllegalArgumentException exception = new IllegalArgumentException();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).skip(IllegalStateException.class)
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
  void repeatFunctionStepDelayShouldBeAppliedBetweenAttempts() throws Throwable {
    final AtomicInteger counter = new AtomicInteger();
    final FunctionStep<Object, Object> step = FunctionStep.of(
      StepAttributes.of(
        RepeatExtension.REPEAT,
        RepeatExtension.repeatOptions().count(3).delay(Duration.ofMillis(50))
      ),
      (ThrowingFunction<Object, Object, ?>) context -> counter.incrementAndGet()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);
    @SuppressWarnings("unchecked")
    final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) resultStep;
    final Object result = functionStep.getBody().apply("context value");

    assertThat(result).isEqualTo(3);
  }

  @Test
  void repeatOptionsDelayShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RepeatExtension.repeatOptions().delay(null));
  }

  @Test
  void repeatOptionsSkipShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RepeatExtension.repeatOptions().skip(null));
  }

  @Test
  void repeatOptionsButShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> RepeatExtension.repeatOptions().but(null));
  }
}
