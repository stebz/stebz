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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.executor.StepExecutor;
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
 * Tests for {@link Around}.
 */
final class AroundTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  private static Around<String> around() {
    return new Around.Of<>(StepExecutor.get(), "context value");
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
  void ctorThrowsNPEOnNullExecutor() {
    assertThatThrownBy(() -> new Around.Of<>(null, "context value"))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("executor arg is null");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final Around<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final Around<String> result = around.step(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(StaticStepListener.lastStep)
      .isSameAs(originStep);
    assertThat(bodies).containsExactly("step body");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.empty());
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithKeyword() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().step(Keyword.of("Given"), originStep);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().step("new name", originStep);

    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().step(nameGenerator, originStep);

    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenRunnableStepWithKeywordAndName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().step(Keyword.of("When"), "new name", originStep);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("When"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().step(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithKeywordAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().step(Keyword.of("Given"), originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().step("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().step(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenSupplierStepWithKeywordAndNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().step(Keyword.of("Then"), "new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Then"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().step(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithKeywordOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().step(Keyword.of("Given"), originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().step("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().step(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenConsumerStepWithKeywordAndNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().step(Keyword.of("When"), "new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("When"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().step(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithKeywordOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().step(Keyword.of("Given"), originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("origin name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().step("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().step(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void stepMethodExecutesGivenFunctionStepWithKeywordAndNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep =
      new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().step(Keyword.of("Then"), "new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Then"));
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void stepMethodExecutesStepWithNameAndBodyOnContextValue() {
    final Around<String> around = around();
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> {
      received.add(v);
    };

    final Around<String> result = around.step("step name", body);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step(Keyword.of("Given"), "step name", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step("step name", params(), body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step(Keyword.of("Given"), "step name", params(), body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step("step name", "expected result", body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step(Keyword.of("Given"), "step name", "expected result", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step("step name", params(), "expected result", body);

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().step(Keyword.of("Given"), "step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step(Keyword.of("Given"), "step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step(Keyword.of("Given"), "step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step(Keyword.of("Given"), "step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesStepWithKeywordAndNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().step(
      Keyword.of("Given"), "step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameOnContextValue() {
    final Around<String> around = around();

    final Around<String> result = around.step("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameOnContextValue() {
    around().step(Keyword.of("Given"), "step name");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().step("step name", params());

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndParamsOnContextValue() {
    around().step(Keyword.of("Given"), "step name", params());

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().step("step name", "expected result");

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndExpectedResultOnContextValue() {
    around().step(Keyword.of("Given"), "step name", "expected result");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().step("step name", params(), "expected result");

    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void stepMethodExecutesEmptyStepWithKeywordAndNameAndParamsAndExpectedResultOnContextValue() {
    around().step(Keyword.of("Given"), "step name", params(), "expected result");

    assertThat(executedStep().getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
