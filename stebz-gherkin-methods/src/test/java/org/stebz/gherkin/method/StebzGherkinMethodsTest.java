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
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

/**
 * Tests for {@link StebzGherkinMethods}.
 */
final class StebzGherkinMethodsTest {

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
    final GherkinAround<String> around = StebzGherkinMethods.around("context value");
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = received::add;

    around.Given(new ConsumerStep.Of<>(body));

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void gherkinAroundMethodIsAliasForAroundMethod() {
    final GherkinAround<String> around = StebzGherkinMethods.gherkinAround("context value");
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = received::add;

    around.When(new ConsumerStep.Of<>(body));

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void givenMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.Given(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.Given("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Given(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.Given(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void givenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.Given("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Given(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.Given(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void givenMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.Given("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Given(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.Given(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void givenMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.Given("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void givenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Given(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void givenMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Given(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Given("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Given("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Given("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Given("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void givenMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Given(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void givenMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Given("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Given("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void givenMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Given("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Given("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.Given("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.Given("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.Given("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void givenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.Given("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.given());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.When(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.When("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.When(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.When(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void whenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.When("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.When(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.When(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void whenMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.When("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.When(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.When(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void whenMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.When("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void whenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.When(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void whenMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.When(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.When("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.When("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.When("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.When("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void whenMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.When(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void whenMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.When("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.When("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void whenMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.When("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.When("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.When("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.When("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.When("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void whenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.When("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.when());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.Then(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.Then("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Then(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.Then(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void thenMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.Then("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Then(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.Then(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void thenMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.Then("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Then(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.Then(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void thenMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.Then("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void thenMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Then(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void thenMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Then(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Then("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Then("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Then("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Then("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void thenMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Then(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void thenMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Then("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Then("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void thenMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Then("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Then("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.Then("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.Then("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.Then("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void thenMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.Then("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.then());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.And(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.And("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.And(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.And(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void andMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.And("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.And(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.And(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void andMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.And("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.And(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.And(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void andMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.And("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void andMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.And(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void andMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.And(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.And("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.And("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.And("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.And("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void andMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.And(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void andMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.And("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.And("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void andMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.And("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.And("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.And("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.And("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.And("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void andMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.And("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.and());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.But(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.But("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.But(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.But(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void butMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.But("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.But(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.But(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void butMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.But("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.But(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.But(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void butMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.But("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void butMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.But(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void butMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.But(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.But("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.But("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.But("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.But("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void butMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.But(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void butMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.But("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.But("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void butMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.But("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.But("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.But("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.But("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.But("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void butMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.But("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.but());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.Background(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.Background("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Background(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.Background(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.Background("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Background(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.Background(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.Background("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Background(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.Background(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.Background("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void backgroundMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Background(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void backgroundMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Background(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Background("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Background("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Background("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Background("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void backgroundMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Background(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Background("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Background("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Background("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Background("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.Background("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.Background("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.Background("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void backgroundMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.Background("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.background());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.Rule(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.Rule("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Rule(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.Rule(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.Rule("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Rule(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.Rule(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.Rule("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Rule(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.Rule(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.Rule("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void ruleMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Rule(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void ruleMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Rule(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Rule("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Rule("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Rule("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Rule("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void ruleMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Rule(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void ruleMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Rule("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Rule("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Rule("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Rule("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.Rule("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.Rule("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.Rule("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void ruleMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.Rule("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.rule());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.Technical(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.Technical("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Technical(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.Technical(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.Technical("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Technical(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.Technical(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.Technical("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.Technical(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.Technical(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.Technical("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void technicalMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.Technical(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void technicalMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Technical(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Technical("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Technical("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Technical("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.Technical("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void technicalMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Technical(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void technicalMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Technical("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Technical("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Technical("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.Technical("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.Technical("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.Technical("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.Technical("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void technicalMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.Technical("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.technical());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzGherkinMethods.___(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzGherkinMethods.___("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.___(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzGherkinMethods.___(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzGherkinMethods.___("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.___(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzGherkinMethods.___(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzGherkinMethods.___("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzGherkinMethods.___(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzGherkinMethods.___(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzGherkinMethods.___("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzGherkinMethods.___(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.___(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.___("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.___("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.___("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzGherkinMethods.___("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.___(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.___("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.___("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.___("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzGherkinMethods.___("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithName() {
    StebzGherkinMethods.___("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParams() {
    StebzGherkinMethods.___("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzGherkinMethods.___("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzGherkinMethods.___("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(GherkinKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
