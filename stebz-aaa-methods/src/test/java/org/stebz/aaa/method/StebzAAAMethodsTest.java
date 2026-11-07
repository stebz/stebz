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
package org.stebz.aaa.method;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.aaa.keyword.AAAKeywords;
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

/**
 * Tests for {@link StebzAAAMethods}.
 */
final class StebzAAAMethodsTest {

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
    final AAAAround<String> around = StebzAAAMethods.around("context value");
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = received::add;

    around.Arrange(new ConsumerStep.Of<>(body));

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void aaaAroundMethodIsAliasForAroundMethod() {
    final AAAAround<String> around = StebzAAAMethods.aaaAround("context value");
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = received::add;

    around.Act(new ConsumerStep.Of<>(body));

    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void setUpMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.SetUp(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.SetUp("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.SetUp(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.SetUp(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.SetUp("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.SetUp(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.SetUp(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.SetUp("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.SetUp(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.SetUp(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.SetUp("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.SetUp(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.SetUp(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.SetUp("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.SetUp("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.SetUp("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.SetUp("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.SetUp(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void setUpMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.SetUp("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.SetUp("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.SetUp("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.SetUp("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.SetUp("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.SetUp("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.SetUp("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.SetUp("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.TearDown(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.TearDown("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.TearDown(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.TearDown(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.TearDown("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.TearDown(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.TearDown(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.TearDown("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.TearDown(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.TearDown(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.TearDown("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.TearDown(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.TearDown(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.TearDown("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.TearDown("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.TearDown("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.TearDown("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.TearDown(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.TearDown("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.TearDown("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.TearDown("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.TearDown("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.TearDown("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.TearDown("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.TearDown("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.TearDown("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.Arrange(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.Arrange("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Arrange(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.Arrange(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.Arrange("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Arrange(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.Arrange(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.Arrange("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Arrange(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.Arrange(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.Arrange("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Arrange(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Arrange(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Arrange("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Arrange("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Arrange("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Arrange("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Arrange(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Arrange("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Arrange("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Arrange("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Arrange("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.Arrange("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.Arrange("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.Arrange("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.Arrange("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.Act(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.Act("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Act(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.Act(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void actMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.Act("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Act(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.Act(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void actMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.Act("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Act(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.Act(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void actMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.Act("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Act(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Act(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Act("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Act("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Act("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Act("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Act(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void actMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Act("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Act("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void actMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Act("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Act("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.Act("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.Act("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.Act("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.Act("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.Assert(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.Assert("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Assert(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.Assert(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void assertMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.Assert("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Assert(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.Assert(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void assertMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.Assert("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.Assert(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.Assert(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void assertMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.Assert("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.Assert(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Assert(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Assert("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Assert("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Assert("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.Assert("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Assert(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void assertMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Assert("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Assert("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void assertMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Assert("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.Assert("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.Assert("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.Assert("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.Assert("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.Assert("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStep() {
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    StebzAAAMethods.___(originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    StebzAAAMethods.___("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.___(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = StebzAAAMethods.___(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = StebzAAAMethods.___("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.___(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    StebzAAAMethods.___(originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    StebzAAAMethods.___("new name", originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnGivenValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    StebzAAAMethods.___(nameGenerator, originStep, "value");

    assertThat(received).containsExactly("value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = StebzAAAMethods.___(originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("value result"));
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = StebzAAAMethods.___("new name", originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnGivenValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = StebzAAAMethods.___(nameGenerator, originStep, "value");

    assertThat(result).isEqualTo("value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesStepWithBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.___(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.___("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.___("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.___("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndBody() {
    final List<String> bodies = new ArrayList<>();
    final ThrowingRunnable<?> body = () -> bodies.add("step body");

    StebzAAAMethods.___("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesStepWithSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.___(body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.___("step name", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.___("step name", params(), body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.___("step name", "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndSupplierBodyAndReturnsStepResult() {
    final ThrowingSupplier<String, ?> body = () -> "step result";

    final String result = StebzAAAMethods.___("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithName() {
    StebzAAAMethods.___("step name");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParams() {
    StebzAAAMethods.___("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndExpectedResult() {
    StebzAAAMethods.___("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsAndExpectedResult() {
    StebzAAAMethods.___("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
