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
import dev.jlet.function.ThrowingConsumer2;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingFunction2;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.exception.StepNotImplementedError;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link FunctionStep}.
 */
final class FunctionStepTest {
  private static final ThrowingFunction<Object, String, Error> IDENTITY = String::valueOf;

  private static ThrowingFunction<Object, String, Error> valueFunction(final String value) {
    return input -> value;
  }

  private static <T, R> ThrowingFunction<T, R, Error> failing(final String message) {
    return input -> { throw new IllegalStateException(message); };
  }

  private static ThrowingConsumer<Object, Error> counting(final AtomicInteger counter) {
    return value -> counter.incrementAndGet();
  }

  @Test
  void emptyShouldReturnStepWithEmptyBodyReturningNull() throws Throwable {
    assertThat(FunctionStep.empty().getBody().apply("v")).isNull();
  }

  @Test
  void emptyReturningShouldReturnStepWithEmptyBodyReturningGivenResult() throws Throwable {
    assertThat(FunctionStep.emptyReturning("result").getBody().apply("v")).isEqualTo("result");
  }

  @Test
  void notImplementedShouldReturnStepThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> FunctionStep.notImplemented().getBody().apply("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void emptyBodyShouldReturnFunctionReturningNull() {
    assertThat(FunctionStep.emptyBody().apply("v")).isNull();
  }

  @Test
  void emptyBodyReturningShouldReturnFunctionReturningGivenResult() {
    assertThat(FunctionStep.emptyBodyReturning("result").apply("v")).isEqualTo("result");
  }

  @Test
  void notImplementedBodyShouldReturnFunctionThrowingStepNotImplementedError() {
    assertThatThrownBy(() -> FunctionStep.notImplementedBody().apply("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void ofShouldCreateStepWithGivenBody() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of(IDENTITY);
    assertThat(step.getBody().apply("v")).isEqualTo("v");
  }

  @Test
  void ofShouldThrowExceptionForNullBody() {
    assertThatThrownBy(() -> FunctionStep.of((ThrowingFunction<Object, String, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofVariantsShouldCreateStepWithGivenAttributes() {
    assertThat(FunctionStep.of("name", IDENTITY).getName()).isEqualTo("name");
    assertThat(FunctionStep.of("name", "expected", IDENTITY).getExpectedResult()).isEqualTo("expected");
    assertThat(FunctionStep.of("name", java.util.Collections.emptyMap(), IDENTITY).getParams())
      .isEmpty();
    assertThat(FunctionStep.of("name", java.util.Collections.emptyMap(), "expected", IDENTITY)
      .getExpectedResult()).isEqualTo("expected");
  }

  @Test
  void ofOriginShouldCopyAttributesAndBody() throws Throwable {
    final FunctionStep<Object, String> origin = FunctionStep.of("name", IDENTITY);
    final FunctionStep<Object, String> copy = new FunctionStep.Of<>(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
    assertThat(copy.getBody().apply("v")).isEqualTo("v");
  }

  @Test
  void ofAttributesAndBodyShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final FunctionStep<Object, String> step = new FunctionStep.Of<>(attributes, IDENTITY);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void ofAttributesAndBodyShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new FunctionStep.Of<>((StepAttributes) null, IDENTITY))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new FunctionStep.Of<>(StepAttributes.empty(), null))
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
  void withAttributesShouldReplaceAttributes() {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    final StepAttributes newAttributes = StepAttributes.of(StepAttribute.NAME, "new name");
    assertThat(step.withAttributes(newAttributes).getName()).isEqualTo("new name");
  }

  @Test
  void withBodyShouldReplaceBody() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    final ThrowingFunction<Object, String, Error> newBody = value -> "new";
    assertThat(step.withBody(newBody).getBody()).isSameAs(newBody);
    assertThat(step.withBody(newBody).getBody().apply("v")).isEqualTo("new");
  }

  @Test
  void withBodyOfShouldApplyGeneratorToCurrentBody() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    assertThat(step.withBodyOf(body -> valueFunction("generated")).getBody().apply("v"))
      .isEqualTo("generated");
  }

  @Test
  void withEmptyBodyShouldReplaceBodyWithEmptyReturningNull() throws Throwable {
    assertThat(FunctionStep.of("name", IDENTITY).withEmptyBody().getBody().apply("v")).isNull();
  }

  @Test
  void withEmptyBodyReturningShouldReplaceBodyWithEmptyReturningGivenResult() throws Throwable {
    assertThat(FunctionStep.of("name", IDENTITY).withEmptyBodyReturning("new").getBody().apply("v"))
      .isEqualTo("new");
  }

  @Test
  void withNotImplementedBodyShouldReplaceBodyWithNotImplemented() {
    assertThatThrownBy(() -> FunctionStep.of("name", IDENTITY).withNotImplementedBody().getBody().apply("v"))
      .isInstanceOf(StepNotImplementedError.class);
  }

  @Test
  void withIgnoredShouldIgnoreMatchingExceptionAndReturnNullResult() throws Throwable {
    assertThat(FunctionStep.of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class).getBody().apply("v")).isNull();
  }

  @Test
  void withIgnoredShouldRethrowNotMatchingException() {
    assertThatThrownBy(() -> FunctionStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class).getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfTwoTypesShouldIgnoreMatchingException() throws Throwable {
    assertThat(FunctionStep.of("name", failing("ignored"))
      .withIgnored(IllegalStateException.class, IllegalArgumentException.class).getBody().apply("v"))
      .isNull();
  }

  @Test
  void withIgnoredOfThreeTypesShouldRethrowExceptionOfFourthType() {
    assertThatThrownBy(() -> FunctionStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class, NumberFormatException.class, UnsupportedOperationException.class)
      .getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredShouldThrowExceptionForNullArgs() {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    assertThatThrownBy(() -> step.withIgnored(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withIgnored(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withRethrownShouldReplaceExceptionUsingGenerator() {
    assertThatThrownBy(() -> FunctionStep.of("name", failing("origin"))
      .withRethrown(ex -> new RuntimeException("generated", ex)).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated")
      .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void withRethrownOfTypeShouldReplaceMatchingExceptionOnly() {
    assertThatThrownBy(() -> FunctionStep.of("name", failing("origin"))
      .withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> FunctionStep.of("name", failing("other"))
      .withRethrown(java.lang.NumberFormatException.class, ex -> new RuntimeException("generated"))
      .getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfTwoTypesShouldIgnoreExceptionOfSecondTypeAndReturnNullResult() throws Throwable {
    assertThat(FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new NumberFormatException("ignored");
    }).withIgnored(IllegalStateException.class, NumberFormatException.class).getBody().apply("v"))
      .isNull();
  }

  @Test
  void withIgnoredOfTwoTypesShouldRethrowExceptionOfThirdType() {
    assertThatThrownBy(() -> FunctionStep.of("name", failing("not ignored"))
      .withIgnored(IllegalArgumentException.class, NumberFormatException.class)
      .getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void withIgnoredOfThreeTypesShouldIgnoreMatchingExceptionAndReturnNullResult() throws Throwable {
    assertThat(FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
        throw new UnsupportedOperationException("ignored");
      }).withIgnored(IllegalStateException.class, IllegalArgumentException.class, UnsupportedOperationException.class)
      .getBody().apply("v"))
      .isNull();
  }

  @Test
  void withRethrownOfTwoTypesShouldReplaceMatchingExceptionsOnly() {
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new IllegalStateException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new IllegalArgumentException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new UnsupportedOperationException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("origin");
  }

  @Test
  void withRethrownOfThreeTypesShouldReplaceMatchingExceptionsOnly() {
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new IllegalStateException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class, UnsupportedOperationException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new UnsupportedOperationException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class, UnsupportedOperationException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("generated");
    assertThatThrownBy(() -> FunctionStep.of("name", (ThrowingFunction<Object, String, Error>) value -> {
      throw new ClassCastException("origin");
    }).withRethrown(IllegalStateException.class, IllegalArgumentException.class, UnsupportedOperationException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isInstanceOf(ClassCastException.class)
      .hasMessage("origin");
  }

  @Test
  void withOnSuccessShouldTransformResultWithFunction() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of("name", value -> "result")
      .withOnSuccess((ThrowingFunction2<Object, String, String, Error>) (input, result) -> result + "!");
    assertThat(step.getBody().apply("v")).isEqualTo("result!");
  }

  @Test
  void withIgnoredAndWithRethrownShouldKeepSucceedingBodyBehavior() throws Throwable {
    final AtomicInteger runs = new AtomicInteger();
    final ThrowingFunction<Object, String, Error> body = value -> {
      runs.incrementAndGet();
      return "result";
    };
    final FunctionStep<Object, String> step = FunctionStep.of("name", body);
    assertThat(step.withIgnored(IllegalStateException.class).getBody().apply("v")).isEqualTo("result");
    assertThat(step.withIgnored(IllegalStateException.class, IllegalArgumentException.class)
      .getBody().apply("v")).isEqualTo("result");
    assertThat(step.withIgnored(IllegalStateException.class, IllegalArgumentException.class,
      UnsupportedOperationException.class).getBody().apply("v")).isEqualTo("result");
    assertThat(step.withRethrown(ex -> new RuntimeException("generated")).getBody().apply("v"))
      .isEqualTo("result");
    assertThat(step.withRethrown(IllegalStateException.class, ex -> new RuntimeException("generated"))
      .getBody().apply("v")).isEqualTo("result");
    assertThat(step.withRethrown(IllegalStateException.class, IllegalArgumentException.class,
      ex -> new RuntimeException("generated")).getBody().apply("v")).isEqualTo("result");
    assertThat(step.withRethrown(IllegalStateException.class, IllegalArgumentException.class,
        UnsupportedOperationException.class, ex -> new RuntimeException("generated"))
      .getBody().apply("v")).isEqualTo("result");
    assertThat(runs.get()).isEqualTo(7);
  }

  @Test
  void withAttributesAndWithBodyShouldThrowExceptionForNullArgs() {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    assertThatThrownBy(() -> step.withAttributes(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withBody(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void ofOriginStaticShouldCopyAttributesAndBody() throws Throwable {
    final FunctionStep<Object, String> origin = FunctionStep.of("name", IDENTITY);
    final FunctionStep<Object, String> copy = FunctionStep.of(origin);
    assertThat(copy.getName()).isEqualTo("name");
    assertThat(copy.getBody()).isSameAs(origin.getBody());
    assertThat(copy.getBody().apply("v")).isEqualTo("v");
  }

  @Test
  void ofAttributesAndBodyStaticShouldUseGivenInstances() {
    final StepAttributes attributes = StepAttributes.of(StepAttribute.NAME, "name");
    final FunctionStep<Object, String> step = FunctionStep.of(attributes, IDENTITY);
    assertThat(step.getAttributes()).isSameAs(attributes);
    assertThat(step.getBody()).isNotNull();
  }

  @Test
  void withRethrownShouldThrowExceptionForNullArgs() {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    assertThatThrownBy(() -> step.withRethrown(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withRethrown(IllegalStateException.class, null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withBeforeShouldRunBlockBeforeBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    FunctionStep.of("name", value -> {
        order.append("body");
        return "result";
      })
      .withBefore(value -> order.append("before"))
      .getBody()
      .apply("v");
    assertThat(order).hasToString("beforebody");
  }

  @Test
  void withAfterShouldRunBlockWithInputAndResultAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    FunctionStep.of("name", value -> {
        order.append("body");
        return "result";
      })
      .withAfter((ThrowingConsumer2<Object, String, Error>) (input, result) -> order.append(input)
        .append(result))
      .getBody()
      .apply("v");
    assertThat(order).hasToString("bodyvresult");
  }

  @Test
  void withAfterShouldTransformResultWithFunction() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of("name", value -> "result")
      .withAfter((ThrowingFunction2<Object, String, String, Error>) (input, result) -> result + "!");
    assertThat(step.getBody().apply("v")).isEqualTo("result!");
  }

  @Test
  void withOnSuccessShouldRunBlockWithInputAndResultAfterBody() throws Throwable {
    final StringBuilder order = new StringBuilder();
    FunctionStep.of("name", value -> {
        order.append("body");
        return "result";
      })
      .withOnSuccess((ThrowingConsumer2<Object, String, Error>) (input, result) -> order.append(result))
      .getBody()
      .apply("v");
    assertThat(order).hasToString("bodyresult");
  }

  @Test
  void withOnFailureShouldRunBlockOnlyOnFailure() throws Throwable {
    final AtomicInteger failures = new AtomicInteger();
    FunctionStep.of("name", value -> "result").withOnFailure(counting(failures)).getBody().apply("v");
    assertThat(failures.get()).isZero();
    assertThatThrownBy(() -> FunctionStep.of("name", failing("fail"))
      .withOnFailure(counting(failures)).getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("fail");
    assertThat(failures.get()).isEqualTo(1);
  }

  @Test
  void withOnFailureShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> FunctionStep.of("name", value -> {
      throw stepEx;
    }).withOnFailure(value -> {
      throw new IllegalArgumentException("block");
    }).getBody().apply("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRunBlockOnSuccess() throws Throwable {
    final AtomicInteger finallyRuns = new AtomicInteger();
    FunctionStep.of("name", value -> "result")
      .withFinally(counting(finallyRuns))
      .getBody()
      .apply("v");
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldRunBlockAndRethrowStepException() {
    final AtomicInteger finallyRuns = new AtomicInteger();
    assertThatThrownBy(() -> FunctionStep.of("name", failing("step"))
      .withFinally(counting(finallyRuns)).getBody().apply("v"))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("step");
    assertThat(finallyRuns.get()).isEqualTo(1);
  }

  @Test
  void withFinallyShouldSuppressStepExceptionIfBlockFails() {
    final IllegalStateException stepEx = new IllegalStateException("step");
    assertThatThrownBy(() -> FunctionStep.of("name", value -> {
      throw stepEx;
    }).withFinally(value -> {
      throw new IllegalArgumentException("block");
    }).getBody().apply("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block")
      .satisfies(ex -> assertThat(ex.getSuppressed()).contains(stepEx));
  }

  @Test
  void withFinallyShouldRethrowBlockExceptionOnSuccess() {
    assertThatThrownBy(() -> FunctionStep.of("name", value -> "result")
      .withFinally(value -> {
        throw new IllegalArgumentException("block");
      }).getBody().apply("v"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("block");
  }

  @Test
  void withBeforeAndSimilarShouldThrowExceptionForNullArgs() {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    assertThatThrownBy(() -> step.withBefore(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter((ThrowingConsumer2<Object, String, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withAfter((ThrowingFunction2<Object, String, String, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withOnFailure(null)).isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step.withFinally(null)).isInstanceOf(NullPointerException.class);
  }

  @Test
  void withContextShouldReturnSupplierStepWithGivenContext() throws Throwable {
    final FunctionStep<Object, String> step = FunctionStep.of("name", IDENTITY);
    final SupplierStep<String> contextual = step.withContext("input");
    assertThat(contextual.hasContext()).isTrue();
    assertThat(contextual.getContext()).isEqualTo("input");
    assertThat(contextual.getName()).isEqualTo("name");
    assertThat(contextual.getBody().get()).isEqualTo("input");
  }

  @Test
  void withoutResultShouldReturnConsumerStep() {
    final ConsumerStep<Object> consumer = FunctionStep.of("name", IDENTITY).withoutResult();
    assertThat(consumer.getName()).isEqualTo("name");
    assertThatCode(() -> consumer.getBody().accept("v")).doesNotThrowAnyException();
  }

  @Test
  void toStringShouldContainStepName() {
    assertThat(FunctionStep.of("name", IDENTITY))
      .hasToString("FunctionStep[name]");
  }
}
