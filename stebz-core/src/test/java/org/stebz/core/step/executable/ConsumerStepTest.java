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
package org.stebz.core.step.executable;

import dev.jlet.function.ThrowingConsumer;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ConsumerStep}.
 */
final class ConsumerStepTest {
  private static final ThrowingConsumer<Object, Error> EMPTY_CONSUMER = value -> { };

  private static ThrowingConsumer<Object, Error> counting(final AtomicInteger counter) {
    return value -> counter.incrementAndGet();
  }

  private static ThrowingConsumer<Object, Error> failing(final String message) {
    return value -> { throw new IllegalStateException(message); };
  }

  @Test
  void emptyShouldReturnStepWithEmptyBody() {
    assertThatCode(() -> ConsumerStep.empty().getBody().accept("v"))
      .doesNotThrowAnyException();
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> ConsumerStep.notImplemented().getBody().accept("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyShouldReturnConsumerThatDoesNothing() {
    assertThatCode(() -> ConsumerStep.emptyBody().accept("v"))
      .doesNotThrowAnyException();
  }

  @Test
  void notImplementedBodyShouldReturnConsumerThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> ConsumerStep.notImplementedBody().accept("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void ofShouldCreateStepWithGivenBody() throws Throwable {
    final AtomicInteger runs = new AtomicInteger();
    final ConsumerStep<Object> step = ConsumerStep.of(counting(runs));
    assertThat(step.getName()).isEmpty();
    assertThat(step.getBody()).isNotNull();
    step.getBody().accept("v");
    assertThat(runs.get()).isEqualTo(1);
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> ConsumerStep.of((ThrowingConsumer<Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofVariantsShouldCreateStepWithGivenAttributes() {
    assertThat(ConsumerStep.of("name", EMPTY_CONSUMER).getName()).isEqualTo("name");
    assertThat(ConsumerStep.of("name", "expected", EMPTY_CONSUMER).getExpectedResult())
      .isEqualTo("expected");
    assertThat(ConsumerStep.of("name", java.util.Collections.emptyMap(), EMPTY_CONSUMER).getParams())
      .isEmpty();
    assertThat(ConsumerStep.of("name", java.util.Collections.emptyMap(), "expected", EMPTY_CONSUMER)
      .getExpectedResult()).isEqualTo("expected");
  }

  @Test
  void ofWithNameShouldThrowExceptionForNullName() {
    assertThatThrownBy(() -> ConsumerStep.of((String) null, EMPTY_CONSUMER))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofOriginShouldCopyAttributesAndBody() {
    final ConsumerStep<Object> origin = ConsumerStep.of("name", EMPTY_CONSUMER);
    final ConsumerStep<Object> copy = new ConsumerStep.Of<>(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
  }

  @Test
  void ofAttributesAndBodyShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final ConsumerStep<Object> step = new ConsumerStep.Of<>(attributes, EMPTY_CONSUMER);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void ofAttributesAndBodyShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new ConsumerStep.Of<>((StepAttributes) null, EMPTY_CONSUMER))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new ConsumerStep.Of<>(StepAttributes.empty(), null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withAttributesShouldReplaceAttributes() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    final StepAttributes newAttributes = StepAttributes.of(StepAttribute.NAME, "new name");
    assertThat(step.withAttributes(newAttributes).getName()).isEqualTo("new name");
  }

  @Test
  void withAttributesShouldThrowExceptionForNullArg() {
    assertThatThrownBy(() -> ConsumerStep.of("name", EMPTY_CONSUMER).withAttributes(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBodyShouldReplaceBody() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    assertThat(step.withBody(EMPTY_CONSUMER).getBody()).isSameAs(EMPTY_CONSUMER);
  }

  @Test
  void withBodyShouldThrowExceptionForNullArg() {
    assertThatThrownBy(() -> ConsumerStep.of("name", EMPTY_CONSUMER).withBody(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBodyOfShouldApplyGeneratorToCurrentBody() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    assertThat(step.withBodyOf(body -> EMPTY_CONSUMER).getBody()).isSameAs(EMPTY_CONSUMER);
  }

  @Test
  void withEmptyBodyShouldReplaceBodyWithEmpty() {
    assertThatCode(() -> ConsumerStep.of("name", failing("fail")).withEmptyBody().getBody().accept("v"))
      .doesNotThrowAnyException();
  }

  @Test
  void withNotImplementedBodyShouldReplaceBodyWithNotImplemented() {
    assertThatThrownBy(() -> ConsumerStep.of("name", EMPTY_CONSUMER).withNotImplementedBody().getBody().accept("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void withIgnoredShouldIgnoreMatchingException() {
    assertThatCode(() -> ConsumerStep.of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class).getBody().accept("v")).doesNotThrowAnyException();
  }

  @Test
  void withIgnoredShouldRethrowNotMatchingException() {
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class).getBody().accept("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfTwoTypesShouldIgnoreMatchingException() {
    assertThatCode(() -> ConsumerStep.of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class, IllegalArgumentException.class).getBody().accept("v"))
      .doesNotThrowAnyException();
  }

  @Test
  void withIgnoredOfThreeTypesShouldRethrowExceptionOfFourthType() {
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class, NumberFormatException.class, UnsupportedOperationException.class)
      .getBody().accept("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredShouldThrowExceptionForNullArgs() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    assertThatThrownBy(() -> step.withIgnored((Class<? extends Throwable>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(null, IllegalStateException.class))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withRethrownShouldReplaceExceptionUsingGenerator() {
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("origin"))
      .withRethrown(ex -> new RuntimeException("generated", ex)).getBody().accept("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated")
      .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void withRethrownOfTypeShouldReplaceMatchingExceptionOnly() {
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("origin"))
      .withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated")).getBody().accept("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> ConsumerStep.of("name", value -> {
      throw new NumberFormatException("origin");
    }).withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated")).getBody().accept("v"))
      .isInstanceOf(NumberFormatException.class)
      .hasMessage("origin");
  }

  @Test
  void withRethrownShouldThrowExceptionForNullArgs() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    assertThatThrownBy(() -> step.withRethrown(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withRethrown(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBeforeShouldRunBlockBeforeBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    ConsumerStep.of("name", value -> order.append("body"))
      .withBefore(value -> order.append("before"))
      .getBody()
      .accept("v");
    assertThat(order).hasToString("beforebody");
  }

  @Test
  void withAfterShouldRunBlockAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    ConsumerStep.of("name", value -> order.append("body"))
      .withAfter(value -> order.append("after"))
      .getBody()
      .accept("v");
    assertThat(order).hasToString("bodyafter");
  }

  @Test
  void withOnSuccessShouldRunBlockAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    ConsumerStep.of("name", value -> order.append("body"))
      .withOnSuccess(value -> order.append("after"))
      .getBody()
      .accept("v");
    assertThat(order).hasToString("bodyafter");
  }

  @Test
  void withOnFailureShouldRunBlockOnlyOnFailure() throws Throwable {
    final AtomicInteger failures = new AtomicInteger();
    ConsumerStep.of("name", EMPTY_CONSUMER).withOnFailure(counting(failures)).getBody().accept("v");
    assertThat(failures.get()).isZero();
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("fail"))
      .withOnFailure(counting(failures)).getBody().accept("v"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("fail");
    assertThat(failures.get()).isEqualTo(1);
  }

  @Test
  void withOnFailureShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> ConsumerStep.of("name", value -> {
      throw stepEx;
    }).withOnFailure(value -> {
      throw new IllegalArgumentException("block");
    }).getBody().accept("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRunBlockOnceAfterBodyOnSuccess() throws Throwable {
    final AtomicInteger bodyRuns = new AtomicInteger();
    final AtomicInteger finallyRuns = new AtomicInteger();
    ConsumerStep.of("name", counting(bodyRuns))
      .withFinally(counting(finallyRuns))
      .getBody()
      .accept("v");
    assertThat(bodyRuns.get()).isEqualTo(1);
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldRunBlockOnceAndRethrowStepException() {
    final AtomicInteger finallyRuns = new AtomicInteger();
    assertThatThrownBy(() -> ConsumerStep.of("name", failing("step"))
      .withFinally(counting(finallyRuns)).getBody().accept("v"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("step");
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> ConsumerStep.of("name", value -> {
      throw stepEx;
    }).withFinally(value -> {
      throw new IllegalArgumentException("block");
    }).getBody().accept("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRethrowBlockExceptionOnSuccess() {
    assertThatThrownBy(() -> ConsumerStep.of("name", EMPTY_CONSUMER)
      .withFinally(value -> {
        throw new IllegalArgumentException("block");
      }).getBody().accept("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block");
  }

  @Test
  void withBeforeAndSimilarShouldThrowExceptionForNullArgs() {
    final ConsumerStep<Object> step = ConsumerStep.of("name", EMPTY_CONSUMER);
    assertThatThrownBy(() -> step.withBefore(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnSuccess(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnFailure(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withFinally(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void withContextShouldReturnRunnableStepWithGivenContext() throws Throwable {
    final AtomicInteger runs = new AtomicInteger();
    final ConsumerStep<Object> step = ConsumerStep.of("name", counting(runs));
    final RunnableStep contextual = step.withContext("input");
    assertThat(contextual.hasContext()).isTrue();
    assertThat(contextual.getContext()).isEqualTo("input");
    assertThat(contextual.getName()).isEqualTo("name");
    contextual.getBody().run();
    assertThat(runs.get()).isEqualTo(1);
  }

  @Test
  void toStringShouldContainStepName() {
    assertThat(ConsumerStep.of("name", EMPTY_CONSUMER))
      .hasToString("ConsumerStep[name]");
  }
}
