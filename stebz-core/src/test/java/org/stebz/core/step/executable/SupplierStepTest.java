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
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SupplierStep}.
 */
final class SupplierStepTest {
  private static final ThrowingSupplier<Object, Error> NULL_SUPPLIER = () -> null;

  private static <R> ThrowingSupplier<R, Error> failing(final String message) {
    return () -> { throw new IllegalStateException(message); };
  }

  @Test
  void emptyShouldReturnStepWithEmptyBodyReturningNull() throws Throwable {
    assertThat(SupplierStep.empty().getBody().get()).isNull();
  }

  @Test
  void emptyReturningShouldReturnStepWithEmptyBodyReturningGivenResult() throws Throwable {
    assertThat(SupplierStep.emptyReturning("result").getBody().get()).isEqualTo("result");
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(SupplierStep.notImplemented().getBody()::get)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyShouldReturnSupplierReturningNull() {
    assertThat(SupplierStep.emptyBody().get()).isNull();
  }

  @Test
  void emptyBodyReturningShouldReturnSupplierReturningGivenResult() {
    assertThat(SupplierStep.emptyBodyReturning("result").get()).isEqualTo("result");
  }

  @Test
  void notImplementedBodyShouldReturnSupplierThrowingStepNotImplementedError() {
    assertThatThrownBy(SupplierStep.notImplementedBody()::get)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void ofShouldCreateStepWithGivenBody() throws Throwable {
    final SupplierStep<String> step = SupplierStep.of(() -> "result");
    assertThat(step.getBody().get()).isEqualTo("result");
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> SupplierStep.of((ThrowingSupplier<Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofVariantsShouldCreateStepWithGivenAttributes() {
    assertThat(SupplierStep.of("name", NULL_SUPPLIER).getName()).isEqualTo("name");
    assertThat(SupplierStep.of("name", "expected", NULL_SUPPLIER).getExpectedResult())
      .isEqualTo("expected");
    assertThat(SupplierStep.of("name", Collections.emptyMap(), NULL_SUPPLIER).getParams())
      .isEmpty();
    assertThat(SupplierStep.of("name", Collections.emptyMap(), "expected", NULL_SUPPLIER)
      .getExpectedResult()).isEqualTo("expected");
  }

  @Test
  void ofOriginShouldCopyAttributesAndBody() throws Throwable {
    final SupplierStep<String> origin = SupplierStep.of("name", () -> "result");
    final SupplierStep<String> copy = new SupplierStep.Of<>(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
    assertThat(copy.getBody().get()).isEqualTo("result");
  }

  @Test
  void ofAttributesAndBodyShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final SupplierStep<Object> step = new SupplierStep.Of<>(attributes, NULL_SUPPLIER);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void ofAttributesAndBodyShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new SupplierStep.Of<>((StepAttributes) null, NULL_SUPPLIER))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new SupplierStep.Of<>(StepAttributes.empty(), null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void contextualOfShouldCreateStepWithContext() {
    final SupplierStep<String> step = new SupplierStep.Of<>(
      StepAttributes.empty(), "ctx", (ThrowingFunction<Object, String, Error>) value -> "result");
    assertThat(step.hasContext()).isTrue();
    assertThat(step.getContext()).isEqualTo("ctx");
    assertThat(step.getContextualBody()).isNotNull();
  }

  @Test
  void contextualOfShouldThrowExceptionForNullAttributesOrContextualBody() {
    final ThrowingFunction<Object, String, Error> body = value -> "result";
    assertThatThrownBy(() -> new SupplierStep.Of<>(null, "ctx", body))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new SupplierStep.Of<>(StepAttributes.empty(), "ctx", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void getBodyShouldBindContextToContextualBody() throws Throwable {
    final SupplierStep<String> step = new SupplierStep.Of<>(
      StepAttributes.empty(), "ctx",
      (ThrowingFunction<Object, String, Error>) value -> "value:" + value);
    assertThat(step.getBody().get()).isEqualTo("value:ctx");
  }

  @Test
  void withAttributesShouldReplaceAttributes() {
    final SupplierStep<Object> step = SupplierStep.of("name", NULL_SUPPLIER);
    final StepAttributes newAttributes = StepAttributes.of(StepAttribute.NAME, "new name");
    final SupplierStep<Object> updated = step.withAttributes(newAttributes);
    assertThat(updated.getAttributes()).isSameAs(newAttributes);
    assertThat(updated.getName()).isEqualTo("new name");
  }

  @Test
  void withAttributesShouldKeepContextOfContextualStep() {
    final SupplierStep<String> step = new SupplierStep.Of<>(
      StepAttributes.empty(), "ctx", (ThrowingFunction<Object, String, Error>) value -> "result");
    final SupplierStep<String> updated = step.withAttributes(StepAttributes.of(StepAttribute.NAME, "new name"));
    assertThat(updated.hasContext()).isTrue();
    assertThat(updated.getContext()).isEqualTo("ctx");
  }

  @Test
  void withBodyShouldReplaceBody() throws Throwable {
    final SupplierStep<String> step = SupplierStep.of("name", () -> "result");
    final ThrowingSupplier<String, Error> newBody = () -> "new";
    assertThat(step.withBody(newBody).getBody()).isSameAs(newBody);
    assertThat(step.withBody(newBody).getBody().get()).isEqualTo("new");
  }

  @Test
  void withBodyShouldDropContextOfContextualStep() {
    final SupplierStep<String> step = new SupplierStep.Of<>(
      StepAttributes.empty(), "ctx", (ThrowingFunction<Object, String, Error>) value -> "result");
    final SupplierStep<String> updated = step.withBody(() -> "new");
    assertThat(updated.hasContext()).isFalse();
    assertThatThrownBy(updated::getContext).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void withBodyOfShouldApplyGeneratorToCurrentBody() throws Throwable {
    final SupplierStep<String> step = SupplierStep.of("name", () -> "result");
    assertThat(step.withBodyOf(body -> () -> "generated").getBody().get()).isEqualTo("generated");
  }

  @Test
  void withEmptyBodyShouldReplaceBodyWithEmptyReturningNull() throws Throwable {
    assertThat(SupplierStep.of("name", () -> "result").withEmptyBody().getBody().get()).isNull();
  }

  @Test
  void withEmptyBodyReturningShouldReplaceBodyWithEmptyReturningGivenResult() throws Throwable {
    assertThat(SupplierStep.of("name", () -> "result").withEmptyBodyReturning("new").getBody().get())
      .isEqualTo("new");
  }

  @Test
  void withNotImplementedBodyShouldReplaceBodyWithNotImplemented() {
    assertThatThrownBy(SupplierStep.of("name", NULL_SUPPLIER).withNotImplementedBody().getBody()::get)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void withIgnoredShouldIgnoreMatchingExceptionAndReturnNullResult() throws Throwable {
    final SupplierStep<String> step = SupplierStep.<String>of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class);
    assertThat(step.getBody().get()).isNull();
  }

  @Test
  void withIgnoredShouldRethrowNotMatchingException() {
    assertThatThrownBy(SupplierStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class).getBody()::get)
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfTwoTypesShouldIgnoreMatchingException() throws Throwable {
    assertThat(SupplierStep.of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class, IllegalArgumentException.class).getBody().get()).isNull();
  }

  @Test
  void withIgnoredOfThreeTypesShouldRethrowExceptionOfFourthType() {
    assertThatThrownBy(SupplierStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class, NumberFormatException.class, UnsupportedOperationException.class)
      .getBody()::get)
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredShouldThrowExceptionForNullArgs() {
    final SupplierStep<Object> step = SupplierStep.of("name", NULL_SUPPLIER);
    assertThatThrownBy(() -> step.withIgnored(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withRethrownShouldReplaceExceptionUsingGenerator() {
    assertThatThrownBy(SupplierStep.of("name", failing("origin"))
      .withRethrown(ex -> new RuntimeException("generated", ex)).getBody()::get)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated")
      .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void withRethrownOfTypeShouldReplaceMatchingExceptionOnly() {
    assertThatThrownBy(SupplierStep.of("name", failing("origin"))
      .withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated")).getBody()::get)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(SupplierStep.<String>of("name", () -> {
      throw new NumberFormatException("origin");
    }).withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated")).getBody()::get)
      .isInstanceOf(NumberFormatException.class)
      .hasMessage("origin");
  }

  @Test
  void withRethrownShouldThrowExceptionForNullArgs() {
    final SupplierStep<Object> step = SupplierStep.of("name", NULL_SUPPLIER);
    assertThatThrownBy(() -> step.withRethrown(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withRethrown(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBeforeShouldRunBlockBeforeBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    SupplierStep.of("name", () -> {
        order.append("body");
        return "result";
      })
      .withBefore(() -> order.append("before"))
      .getBody()
      .get();
    assertThat(order).hasToString("beforebody");
  }

  @Test
  void withAfterShouldRunBlockWithResultAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    SupplierStep.of("name", () -> {
        order.append("body");
        return "result";
      })
      .withAfter((ThrowingConsumer<String, Error>) order::append)
      .getBody()
      .get();
    assertThat(order).hasToString("bodyresult");
  }

  @Test
  void withAfterShouldTransformResultWithFunction() throws Throwable {
    final SupplierStep<String> step = SupplierStep.of("name", () -> "result")
      .withAfter(result -> result + "!");
    assertThat(step.getBody().get()).isEqualTo("result!");
  }

  @Test
  void withOnSuccessShouldRunBlockWithResultAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    SupplierStep.of("name", () -> {
        order.append("body");
        return "result";
      })
      .withOnSuccess((ThrowingConsumer<String, Error>) order::append)
      .getBody()
      .get();
    assertThat(order).hasToString("bodyresult");
  }

  @Test
  void withOnSuccessShouldTransformResultWithFunction() throws Throwable {
    final SupplierStep<String> step = SupplierStep.of("name", () -> "result")
      .withOnSuccess((ThrowingFunction<String, String, Error>) result -> result + "!");
    assertThat(step.getBody().get()).isEqualTo("result!");
  }

  @Test
  void withOnFailureShouldRunBlockOnlyOnFailure() throws Throwable {
    final AtomicInteger failures = new AtomicInteger();
    SupplierStep.of("name", () -> "result").withOnFailure(failures::incrementAndGet).getBody().get();
    assertThat(failures.get()).isZero();
    assertThatThrownBy(SupplierStep.of("name", failing("fail"))
      .withOnFailure(failures::incrementAndGet).getBody()::get)
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("fail");
    assertThat(failures.get()).isEqualTo(1);
  }

  @Test
  void withOnFailureShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(SupplierStep.of("name", () -> {
      throw stepEx;
    }).withOnFailure(() -> {
      throw new IllegalArgumentException("block");
    }).getBody()::get)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRunBlockOnSuccess() throws Throwable {
    final AtomicInteger finallyRuns = new AtomicInteger();
    SupplierStep.of("name", () -> "result")
      .withFinally(finallyRuns::incrementAndGet)
      .getBody()
      .get();
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldRunBlockAndRethrowStepException() {
    final AtomicInteger finallyRuns = new AtomicInteger();
    assertThatThrownBy(SupplierStep.of("name", failing("step"))
      .withFinally(finallyRuns::incrementAndGet).getBody()::get)
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("step");
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(SupplierStep.of("name", () -> {
      throw stepEx;
    }).withFinally(() -> {
      throw new IllegalArgumentException("block");
    }).getBody()::get)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRethrowBlockExceptionOnSuccess() {
    assertThatThrownBy(SupplierStep.of("name", () -> "result")
      .withFinally(() -> {
        throw new IllegalArgumentException("block");
      }).getBody()::get)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block");
  }

  @Test
  void withBeforeAndSimilarShouldThrowExceptionForNullArgs() {
    final SupplierStep<Object> step = SupplierStep.of("name", NULL_SUPPLIER);
    assertThatThrownBy(() -> step.withBefore(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter((ThrowingConsumer<Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter((ThrowingFunction<Object, Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnSuccess((ThrowingConsumer<Object, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnFailure(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withFinally(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void withoutResultShouldReturnRunnableStep() throws Throwable {
    final RunnableStep runnable = SupplierStep.of("name", () -> "result").withoutResult();
    assertThat(runnable.getName()).isEqualTo("name");
    runnable.getBody().run();
  }

  @Test
  void toStringShouldContainStepName() {
    assertThat(SupplierStep.of("name", NULL_SUPPLIER).toString()).isEqualTo("SupplierStep[name]");
  }
}
