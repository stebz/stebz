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
import dev.jlet.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link RunnableStep}.
 */
final class RunnableStepTest {
  private static final ThrowingRunnable<Error> EMPTY_RUNNABLE = () -> { };

  private static RunnableStep failingStep(final String message) {
    return RunnableStep.of("name", () -> { throw new IllegalStateException(message); });
  }

  @Test
  void emptyShouldReturnStepWithEmptyBody() {
    assertThatCode(RunnableStep.empty().getBody()::run)
      .doesNotThrowAnyException();
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(RunnableStep.notImplemented().getBody()::run)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyShouldReturnRunnableThatDoesNothing() {
    assertThatCode(RunnableStep.emptyBody()::run)
      .doesNotThrowAnyException();
  }

  @Test
  void notImplementedBodyShouldReturnRunnableThrowingStepNotImplementedError() {
    assertThatThrownBy(RunnableStep.notImplementedBody()::run)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void ofShouldCreateStepWithGivenBody() throws Throwable {
    final AtomicInteger runs = new AtomicInteger();
    final RunnableStep step = RunnableStep.of(runs::incrementAndGet);
    assertThat(step.getBody()).isNotNull();
    step.getBody().run();
    assertThat(runs.get()).isEqualTo(1);
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> RunnableStep.of((ThrowingRunnable<Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofOriginStaticShouldCopyAttributesAndBody() {
    final RunnableStep origin = RunnableStep.of("name", EMPTY_RUNNABLE);
    final RunnableStep copy = RunnableStep.of(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
  }

  @Test
  void ofAttributesAndBodyStaticShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final RunnableStep step = RunnableStep.of(attributes, EMPTY_RUNNABLE);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void withIgnoredAndWithRethrownShouldKeepSucceedingBodyBehavior() throws Throwable {
    final AtomicInteger runs = new AtomicInteger();
    final RunnableStep step = RunnableStep.of("name", runs::incrementAndGet);
    step.withIgnored(IllegalStateException.class).getBody().run();
    step.withIgnored(IllegalStateException.class, IllegalArgumentException.class).getBody().run();
    step.withIgnored(IllegalStateException.class, IllegalArgumentException.class,
      UnsupportedOperationException.class).getBody().run();
    step.withRethrown(ex -> new RuntimeException("generated")).getBody().run();
    step.withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated"))
      .getBody().run();
    step.withRethrown(IllegalStateException.class, IllegalArgumentException.class,
      ex -> new RuntimeException("generated")).getBody().run();
    step.withRethrown(IllegalStateException.class, IllegalArgumentException.class,
        UnsupportedOperationException.class, ex -> new RuntimeException("generated"))
      .getBody().run();
    assertThat(runs.get()).isEqualTo(7);
  }

  @Test
  void ofWithNameShouldCreateStepWithNameAttribute() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    assertThat(step.getName()).isEqualTo("name");
  }

  @Test
  void ofWithNameShouldThrowExceptionForNullName() {
    assertThatThrownBy(() -> RunnableStep.of((String) null, EMPTY_RUNNABLE))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofWithNameAndExpectedResultShouldCreateStepWithBothAttributes() {
    final RunnableStep step = RunnableStep.of("name", "expected", EMPTY_RUNNABLE);
    assertThat(step.getName()).isEqualTo("name");
    assertThat(step.getExpectedResult()).isEqualTo("expected");
  }

  @Test
  void ofWithNameParamsAndBodyShouldCreateStepWithNameAndParamsAttributes() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = RunnableStep.of("name", params, EMPTY_RUNNABLE);
    assertThat(step.getName()).isEqualTo("name");
    assertThat(step.getParams()).containsEntry("param1", "value1");
  }

  @Test
  void ofWithNameParamsExpectedResultAndBodyShouldCreateStepWithAllAttributes() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = RunnableStep.of("name", params, "expected", EMPTY_RUNNABLE);
    assertThat(step.getName()).isEqualTo("name");
    assertThat(step.getParams()).containsEntry("param1", "value1");
    assertThat(step.getExpectedResult()).isEqualTo("expected");
  }

  @Test
  void ofOriginShouldCopyAttributesAndBody() {
    final RunnableStep origin = RunnableStep.of("name", EMPTY_RUNNABLE);
    final RunnableStep copy = new RunnableStep.Of(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
  }

  @Test
  void ofAttributesAndBodyShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final RunnableStep step = new RunnableStep.Of(attributes, EMPTY_RUNNABLE);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void ofAttributesAndBodyShouldThrowExceptionForNullAttributes() {
    assertThatThrownBy(() -> new RunnableStep.Of((StepAttributes) null, EMPTY_RUNNABLE))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofAttributesAndBodyShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> new RunnableStep.Of(StepAttributes.empty(), null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void contextualOfShouldCreateStepWithContext() {
    final RunnableStep step = new RunnableStep.Of(
      StepAttributes.empty(), "ctx", (ThrowingConsumer<Object, Error>) value -> { });
    assertThat(step.hasContext()).isTrue();
    assertThat(step.getContext()).isEqualTo("ctx");
    assertThat(step.getContextualBody()).isNotNull();
  }

  @Test
  void contextualOfShouldThrowExceptionForNullAttributesOrContextualBody() {
    final ThrowingConsumer<Object, Error> body = value -> { };
    assertThatThrownBy(() -> new RunnableStep.Of(null, "ctx", body))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new RunnableStep.Of(StepAttributes.empty(), "ctx", null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void getBodyShouldBindContextToContextualBody() {
    final RunnableStep step = new RunnableStep.Of(
      StepAttributes.empty(), "ctx",
      (ThrowingConsumer<Object, Error>) value -> assertThat(value).isEqualTo("ctx"));
    assertThatCode(() -> step.getBody().run()).doesNotThrowAnyException();
  }

  @Test
  void withAttributesShouldReplaceAttributes() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    final StepAttributes newAttributes = StepAttributes.of(StepAttribute.NAME, "new name");
    final RunnableStep updated = step.withAttributes(newAttributes);
    assertThat(updated.getAttributes()).isSameAs(newAttributes);
    assertThat(updated.getName()).isEqualTo("new name");
  }

  @Test
  void withAttributesShouldThrowExceptionForNullArg() {
    assertThatThrownBy(() -> RunnableStep.of("name", EMPTY_RUNNABLE).withAttributes(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withAttributesShouldKeepContextOfContextualStep() {
    final RunnableStep step = new RunnableStep.Of(
      StepAttributes.empty(), "ctx", (ThrowingConsumer<Object, Error>) value -> { });
    final RunnableStep updated = step.withAttributes(StepAttributes.of(StepAttribute.NAME, "new name"));
    assertThat(updated.hasContext()).isTrue();
    assertThat(updated.getContext()).isEqualTo("ctx");
  }

  @Test
  void withBodyShouldReplaceBody() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    final ThrowingRunnable<Error> newBody = () -> { };
    assertThat(step.withBody(newBody).getBody()).isSameAs(newBody);
  }

  @Test
  void withBodyShouldThrowExceptionForNullArg() {
    assertThatThrownBy(() -> RunnableStep.of("name", EMPTY_RUNNABLE).withBody(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBodyShouldDropContextOfContextualStep() {
    final RunnableStep step = new RunnableStep.Of(
      StepAttributes.empty(), "ctx", (ThrowingConsumer<Object, Error>) value -> { });
    final RunnableStep updated = step.withBody(EMPTY_RUNNABLE);
    assertThat(updated.hasContext()).isFalse();
    assertThatThrownBy(updated::getContext)
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void withBodyOfShouldApplyGeneratorToCurrentBody() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    final RunnableStep updated = step.withBodyOf(body -> EMPTY_RUNNABLE);
    assertThat(updated.getBody()).isSameAs(EMPTY_RUNNABLE);
  }

  @Test
  void withEmptyBodyShouldReplaceBodyWithEmpty() {
    assertThatCode(failingStep("fail").withEmptyBody().getBody()::run).doesNotThrowAnyException();
  }

  @Test
  void withNotImplementedBodyShouldReplaceBodyWithNotImplemented() {
    assertThatThrownBy(RunnableStep.of("name", EMPTY_RUNNABLE).withNotImplementedBody().getBody()::run)
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void withIgnoredShouldIgnoreMatchingException() {
    assertThatCode(failingStep("ignored")
      .withIgnored(IllegalStateException.class)
      .getBody()::run).doesNotThrowAnyException();
  }

  @Test
  void withIgnoredShouldRethrowNotMatchingException() {
    assertThatThrownBy(failingStep("not ignored")
      .withIgnored(IllegalArgumentException.class)
      .getBody()::run)
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfTwoTypesShouldIgnoreMatchingExceptions() {
    assertThatCode(failingStep("ignored")
      .withIgnored(IllegalStateException.class, IllegalArgumentException.class)
      .getBody()::run).doesNotThrowAnyException();
  }

  @Test
  void withIgnoredOfTwoTypesShouldRethrowExceptionOfThirdType() {
    assertThatThrownBy(failingStep("not ignored")
      .withIgnored(IllegalArgumentException.class, NumberFormatException.class)
      .getBody()::run)
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfThreeTypesShouldIgnoreMatchingExceptions() {
    assertThatCode(RunnableStep.of("name", () -> {
        throw new NumberFormatException("ignored");
      }).withIgnored(IllegalStateException.class, IllegalArgumentException.class, NumberFormatException.class)
      .getBody()::run).doesNotThrowAnyException();
  }

  @Test
  void withIgnoredOfThreeTypesShouldRethrowExceptionOfFourthType() {
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new UnsupportedOperationException("not ignored");
      }).withIgnored(IllegalStateException.class, IllegalArgumentException.class, NumberFormatException.class)
      .getBody()::run)
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void withIgnoredShouldThrowExceptionForNullArgs() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    assertThatThrownBy(() -> step.withIgnored((Class<? extends Throwable>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(null, IllegalStateException.class))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(IllegalStateException.class, null, NumberFormatException.class))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withRethrownShouldReplaceExceptionUsingGenerator() {
    assertThatThrownBy(failingStep("origin")
      .withRethrown(ex -> new RuntimeException("generated", ex))
      .getBody()::run)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated")
      .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void withRethrownOfTypeShouldReplaceMatchingExceptionOnly() {
    assertThatThrownBy(failingStep("origin")
      .withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new NumberFormatException("origin");
      }).withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(NumberFormatException.class)
      .hasMessage("origin");
  }

  @Test
  void withRethrownOfTwoTypesShouldReplaceMatchingExceptionsOnly() {
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new NumberFormatException("origin");
      }).withRethrown(IllegalStateException.class, IllegalArgumentException.class,
        ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new UnsupportedOperationException("origin");
      }).withRethrown(IllegalStateException.class, IllegalArgumentException.class,
        ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void withRethrownOfThreeTypesShouldReplaceMatchingExceptionsOnly() {
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new NumberFormatException("origin");
      }).withRethrown(IllegalStateException.class, IllegalArgumentException.class, NumberFormatException.class,
        ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(RunnableStep.of("name", () -> {
        throw new UnsupportedOperationException("origin");
      }).withRethrown(IllegalStateException.class, IllegalArgumentException.class, NumberFormatException.class,
        ex -> new RuntimeException("generated"))
      .getBody()::run)
      .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void withRethrownShouldThrowExceptionForNullArgs() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    assertThatThrownBy(() -> step.withRethrown(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withRethrown(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBeforeShouldRunBlockBeforeBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    RunnableStep.of("name", () -> order.append("body"))
      .withBefore(() -> order.append("before"))
      .getBody()
      .run();
    assertThat(order).hasToString("beforebody");
  }

  @Test
  void withAfterShouldRunBlockAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    RunnableStep.of("name", () -> order.append("body"))
      .withAfter(() -> order.append("after"))
      .getBody()
      .run();
    assertThat(order).hasToString("bodyafter");
  }

  @Test
  void withOnSuccessShouldRunBlockAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    RunnableStep.of("name", () -> order.append("body"))
      .withOnSuccess(() -> order.append("after"))
      .getBody()
      .run();
    assertThat(order).hasToString("bodyafter");
  }

  @Test
  void withOnFailureShouldRunBlockOnlyOnFailure() throws Throwable {
    final AtomicInteger failures = new AtomicInteger();
    RunnableStep.of("name", EMPTY_RUNNABLE)
      .withOnFailure(failures::incrementAndGet)
      .getBody()
      .run();
    assertThat(failures.get()).isZero();
    assertThatThrownBy(() -> failingStep("fail")
      .withOnFailure(failures::incrementAndGet)
      .getBody().run())
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("fail");
    assertThat(failures.get()).isEqualTo(1);
  }

  @Test
  void withOnFailureShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> RunnableStep.of("name", () -> {
      throw stepEx;
    }).withOnFailure(() -> {
      throw new IllegalArgumentException("block");
    }).getBody().run())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRunBlockOnSuccess() throws Throwable {
    final AtomicInteger finallyRuns = new AtomicInteger();
    RunnableStep.of("name", EMPTY_RUNNABLE)
      .withFinally(finallyRuns::incrementAndGet)
      .getBody()
      .run();
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldRunBlockAndRethrowStepException() {
    final AtomicInteger finallyRuns = new AtomicInteger();
    assertThatThrownBy(() -> failingStep("step")
      .withFinally(finallyRuns::incrementAndGet)
      .getBody().run())
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("step");
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> RunnableStep.of("name", () -> {
      throw stepEx;
    }).withFinally(() -> {
      throw new IllegalArgumentException("block");
    }).getBody().run())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRethrowBlockExceptionOnSuccess() {
    assertThatThrownBy(() -> RunnableStep.of("name", EMPTY_RUNNABLE)
      .withFinally(() -> {
        throw new IllegalArgumentException("block");
      }).getBody().run())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block");
  }

  @Test
  void withBeforeAndSimilarShouldThrowExceptionForNullArgs() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    assertThatThrownBy(() -> step.withBefore(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnSuccess(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnFailure(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withFinally(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void stepWithoutContextShouldHaveNoContext() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE);
    assertThat(step.hasContext()).isFalse();
    assertThatThrownBy(step::getContext).isInstanceOf(NoSuchElementException.class);
    assertThatThrownBy(step::getContextualBody).isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void toStringShouldContainStepName() {
    assertThat(RunnableStep.of("name", EMPTY_RUNNABLE))
      .hasToString("RunnableStep[name]");
  }

  @Test
  void attributeMethodsShouldBeSupported() {
    final RunnableStep step = RunnableStep.of("name", EMPTY_RUNNABLE)
      .withKeyword(Keyword.of("Given"))
      .withParam("param1", "value1")
      .withExpectedResult("expected")
      .withComment("comment")
      .withHiding();
    assertThat(step.getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(step.getParams()).containsEntry("param1", "value1");
    assertThat(step.getExpectedResult()).isEqualTo("expected");
    assertThat(step.getComment()).isEqualTo("comment");
    assertThat(step.isHidden()).isTrue();
  }
}
