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
package org.stebz.method;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StebzMethods}.
 */
final class StebzMethodsTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  private static StepObj<?> executedStep() {
    return StaticStepListener.lastStep;
  }

  private static Map<String, Object> params() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param name", "param value");
    return params;
  }

  @Test
  void aroundMethodReturnsAroundObjectWithCurrentValue() {
    final Around<String> around = StebzMethods.around("context value");
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = received::add;

    around.step(new ConsumerStep.Of<>(body));

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void stepsMethodExecutesGivenBlockOfSteps() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("block body");

    StebzMethods.steps(body);

    assertThat(bodies).containsExactly("block body");
  }

  @Test
  void stepsMethodPassesValueToGivenBlockOfSteps() {
    final List<Object> bodies = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = bodies::add;

    StebzMethods.steps("value", body);

    assertThat(bodies).containsExactly("value");
  }

  @Test
  void stepsResultMethodReturnsResultOfGivenBlockOfSteps() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingSupplier<String, ?> body = () -> {
      bodies.add("block body");
      return "block result";
    };

    final String result = StebzMethods.stepsResult(body);

    assertThat(result).isEqualTo("block result");
    assertThat(bodies).containsExactly("block body");
  }

  @Test
  void stepsResultMethodPassesValueToGivenBlockOfSteps() {
    final ThrowingFunction<String, String, ?> body = v -> v + " body";

    final String result = StebzMethods.stepsResult("value", body);

    assertThat(result).isEqualTo("value body");
  }

  @Test
  void stepsMethodThrowsNPEOnNullBlockOfSteps() {
    assertThatThrownBy(() -> StebzMethods.steps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("steps arg is null");
  }

  @Test
  void stepsMethodThrowsNPEOnNullBlockOfStepsWithValue() {
    assertThatThrownBy(() -> StebzMethods.steps("value", (ThrowingConsumer<String, ?>) null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("steps arg is null");
  }

  @Test
  void stepsResultMethodThrowsNPEOnNullBlockOfSteps() {
    assertThatThrownBy(() -> StebzMethods.stepsResult((ThrowingSupplier<?, ?>) null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("steps arg is null");
  }

  @Test
  void stepsResultMethodThrowsNPEOnNullBlockOfStepsWithValue() {
    assertThatThrownBy(() -> StebzMethods.stepsResult("value", (ThrowingFunction<String, ?, ?>) null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("steps arg is null");
  }

  @Test
  void stepMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzMethods.step(originStep);

    assertThat(StaticStepListener.lastStep)
      .isSameAs(originStep);
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithKeyword() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzMethods.step(Keyword.of("Given"), originStep);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzMethods.step("new name", originStep);

    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzMethods.step(nameGenerator, originStep);

    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithKeywordAndName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzMethods.step(Keyword.of("When"), "new name", originStep);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("When"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzMethods.step(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithKeywordAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzMethods.step(Keyword.of("Given"), originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzMethods.step("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzMethods.step(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithKeywordAndNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzMethods.step(Keyword.of("Then"), "new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Then"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzMethods.step(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithKeywordOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzMethods.step(Keyword.of("Given"), originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzMethods.step("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzMethods.step(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithKeywordAndNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzMethods.step(Keyword.of("When"), "new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("When"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzMethods.step(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithKeywordOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzMethods.step(Keyword.of("Given"), originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzMethods.step("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzMethods.step(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithKeywordAndNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzMethods.step(Keyword.of("Then"), "new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Then"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step("step name", body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step(Keyword.of("Given"), "step name", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step("step name", params(), body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step(Keyword.of("Given"), "step name", params(), body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step("step name", "expected result", body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step(Keyword.of("Given"), "step name", "expected result", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step("step name", params(), "expected result", body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzMethods.step(Keyword.of("Given"), "step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void stepMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step(Keyword.of("Given"), "step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step(Keyword.of("Given"), "step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step(Keyword.of("Given"), "step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzMethods.step(
      Keyword.of("Given"), "step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithName() {
    StebzMethods.step("step name");

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.empty());
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndName() {
    StebzMethods.step(Keyword.of("Given"), "step name");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndParams() {
    StebzMethods.step("step name", params());

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndParams() {
    StebzMethods.step(Keyword.of("Given"), "step name", params());

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzMethods.step("step name", "expected result");

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndExpectedResult() {
    StebzMethods.step(Keyword.of("Given"), "step name", "expected result");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzMethods.step("step name", params(), "expected result");

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndParamsAndExpectedResult() {
    StebzMethods.step(Keyword.of("Given"), "step name", params(), "expected result");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
