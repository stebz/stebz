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
package org.stebz.gherkin.method;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.gherkin.keyword.GherkinKeywords;
import org.stebz.util.container.NullableOptional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link GherkinAround}.
 */
final class GherkinAroundTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  private static GherkinAround<String> around() {
    return new GherkinAround.Of<>(StepExecutor.get(), "context value");
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
    assertThatThrownBy(() -> new GherkinAround.Of<>(null, "context value"))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("executor arg is null");
  }

  @Test
  void givenMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.Given(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Given("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Given(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Given(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void givenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Given("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Given(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Given(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void givenMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Given("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Given(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Given(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void givenMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Given("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Given(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Given(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void givenMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Given("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Given("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void givenMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Given("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Given("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void givenMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Given(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void givenMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Given("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Given("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void givenMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Given("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Given("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.Given("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Given("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Given("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Given("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.When(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().When("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().When(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().When(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void whenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().When("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().When(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().When(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void whenMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().When("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().When(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().When(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void whenMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().When("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().When(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().When(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void whenMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().When("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().When("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void whenMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().When("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().When("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void whenMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().When(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void whenMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().When("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().When("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void whenMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().When("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().When("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.When("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().When("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().When("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().When("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.Then(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Then("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Then(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Then(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void thenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Then("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Then(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Then(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void thenMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Then("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Then(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Then(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void thenMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Then("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Then(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Then(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void thenMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Then("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Then("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void thenMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Then("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Then("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void thenMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Then(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void thenMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Then("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Then("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void thenMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Then("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Then("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.Then("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Then("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Then("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Then("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.And(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().And("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().And(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().And(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void andMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().And("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().And(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().And(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void andMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().And("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().And(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().And(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void andMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().And("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().And(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().And(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void andMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().And("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().And("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void andMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().And("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().And("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void andMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().And(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void andMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().And("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().And("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void andMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().And("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().And("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.And("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().And("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().And("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().And("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.But(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().But("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().But(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().But(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void butMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().But("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().But(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().But(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void butMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().But("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().But(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().But(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void butMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().But("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().But(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().But(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void butMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().But("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().But("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void butMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().But("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().But("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void butMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().But(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void butMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().But("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().But("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void butMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().But("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().But("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.But("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().But("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().But("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().But("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.Background(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Background("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Background(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Background(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Background("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Background(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Background(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Background("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Background(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Background(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Background("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Background(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Background(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Background("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Background("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Background("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Background("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void backgroundMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Background(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Background("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Background("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Background("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Background("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.Background("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Background("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Background("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Background("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.Rule(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Rule("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Rule(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Rule(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Rule("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Rule(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Rule(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Rule("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Rule(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Rule(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Rule("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Rule(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Rule(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void ruleMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Rule("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Rule("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Rule("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Rule("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void ruleMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Rule(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void ruleMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Rule("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Rule("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Rule("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Rule("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.Rule("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Rule("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Rule("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Rule("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.Technical(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Technical("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Technical(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Technical(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Technical("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Technical(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Technical(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Technical("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Technical(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Technical(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Technical("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Technical(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Technical(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void technicalMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Technical("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Technical("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Technical("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Technical("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void technicalMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Technical(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void technicalMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Technical("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Technical("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Technical("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Technical("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.Technical("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Technical("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Technical("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Technical("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final GherkinAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final GherkinAround<String> result = around.___(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().___("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().___(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().___(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().___("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().___(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().___(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().___("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().___(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().___(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().___("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().___(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void asteriskMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameOnContextValue() {
    final GherkinAround<String> around = around();

    final GherkinAround<String> result = around.___("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().___("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().___("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().___("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
