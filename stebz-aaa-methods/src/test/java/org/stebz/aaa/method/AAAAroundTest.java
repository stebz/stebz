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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.aaa.keyword.AAAKeywords;
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
 * Tests for {@link AAAAround}.
 */
final class AAAAroundTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  private static AAAAround<String> around() {
    return new AAAAround.Of<>(StepExecutor.get(), "context value");
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
    assertThatThrownBy(() -> new AAAAround.Of<>(null, "context value"))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("executor arg is null");
  }

  @Test
  void setUpMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.SetUp(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void setUpMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().SetUp("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().SetUp(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().SetUp(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().SetUp("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().SetUp(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().SetUp(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().SetUp("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().SetUp(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().SetUp(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().SetUp("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void setUpMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().SetUp(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void setUpMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().SetUp(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void setUpMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().SetUp("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().SetUp("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().SetUp("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().SetUp("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void setUpMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().SetUp(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void setUpMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().SetUp("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().SetUp("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().SetUp("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().SetUp("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.SetUp("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().SetUp("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().SetUp("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void setUpMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().SetUp("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.setUp());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.TearDown(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().TearDown("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().TearDown(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().TearDown(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().TearDown("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().TearDown(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().TearDown(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().TearDown("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().TearDown(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().TearDown(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().TearDown("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void tearDownMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().TearDown(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void tearDownMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().TearDown(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().TearDown("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().TearDown("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().TearDown("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().TearDown("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void tearDownMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().TearDown(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().TearDown("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().TearDown("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().TearDown("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().TearDown("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.TearDown("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().TearDown("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().TearDown("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void tearDownMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().TearDown("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.tearDown());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.Arrange(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Arrange("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Arrange(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Arrange(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Arrange("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Arrange(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Arrange(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Arrange("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Arrange(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Arrange(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Arrange("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void arrangeMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Arrange(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void arrangeMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Arrange(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Arrange("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Arrange("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Arrange("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Arrange("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void arrangeMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Arrange(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Arrange("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Arrange("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Arrange("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Arrange("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.Arrange("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Arrange("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Arrange("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void arrangeMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Arrange("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.arrange());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.Act(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void actMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Act("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Act(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Act(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void actMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Act("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Act(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Act(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void actMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Act("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Act(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Act(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void actMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Act("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void actMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Act(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void actMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Act(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void actMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Act("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Act("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void actMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Act("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Act("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void actMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Act(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void actMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Act("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Act("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void actMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Act("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Act("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.Act("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Act("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Act("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void actMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Act("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.act());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.Assert(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void assertMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().Assert("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Assert(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().Assert(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void assertMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().Assert("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Assert(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().Assert(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void assertMethodExecutesGivenConsumerStepWithNameOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));

    around().Assert("new name", originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenConsumerStepWithNameCreatedByNameGeneratorOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>("origin name", v -> received.add(v));
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().Assert(nameGenerator, originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().Assert(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void assertMethodExecutesGivenFunctionStepWithNameOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");

    final String result = around().Assert("new name", originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void assertMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().Assert(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void assertMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Assert(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(received).containsExactly("context value");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void assertMethodExecutesStepWithNameAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Assert("step name", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Assert("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void assertMethodExecutesStepWithNameAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Assert("step name", "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndExpectedResultAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().Assert("step name", params(), "expected result", body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void assertMethodExecutesStepWithFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Assert(body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void assertMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Assert("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Assert("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void assertMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Assert("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().Assert("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.Assert("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().Assert("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().Assert("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void assertMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().Assert("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords._assert());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepAndReturnsSameAroundObject() {
    final AAAAround<String> around = around();
    final List<String> bodies = new ArrayList<>();
    final RunnableStep originStep = new RunnableStep.Of(() -> bodies.add("step body"));

    final AAAAround<String> result = around.___(originStep);

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(bodies).containsExactly("step body");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithName() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });

    around().___("new name", originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenRunnableStepWithNameCreatedByNameGenerator() {
    final RunnableStep originStep = new RunnableStep.Of("origin name", () -> { });
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    around().___(nameGenerator, originStep);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>(() -> "step result");

    final String result = around().___(originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("step result"));
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");

    final String result = around().___("new name", originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenSupplierStepWithNameCreatedByNameGeneratorAndReturnsStepResult() {
    final SupplierStep<String> originStep = new SupplierStep.Of<>("origin name", () -> "step result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().___(nameGenerator, originStep);

    assertThat(result).isEqualTo("step result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenConsumerStepOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ConsumerStep<Object> originStep = new ConsumerStep.Of<>(v -> received.add(v));

    around().___(originStep);

    assertThat(received).containsExactly("context value");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>(v -> v + " result");

    final String result = around().___(originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("new name");
  }

  @Test
  void asteriskMethodExecutesGivenFunctionStepWithNameCreatedByNameGeneratorOnContextValueAndReturnsStepResult() {
    final FunctionStep<Object, String> originStep = new FunctionStep.Of<>("origin name", v -> v + " result");
    final ThrowingFunction<? super String, String, ?> nameGenerator = n -> n + " suffix";

    final String result = around().___(nameGenerator, originStep);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("origin name suffix");
  }

  @Test
  void asteriskMethodExecutesStepWithBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___(body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(received).containsExactly("context value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndBodyOnContextValue() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer<String, ?> body = v -> received.add(v);

    around().___("step name", params(), body);

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
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
      .isSameAs(AAAKeywords.asterisk());
    assertThat(StaticStepListener.lastResult)
      .isEqualTo(NullableOptional.of("context value result"));
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", params(), body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesStepWithNameAndParamsAndExpectedResultAndFunctionBodyOnContextValueAndReturnsStepResult() {
    final ThrowingFunction<String, String, ?> body = v -> v + " result";

    final String result = around().___("step name", params(), "expected result", body);

    assertThat(result).isEqualTo("context value result");
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameOnContextValue() {
    final AAAAround<String> around = around();

    final AAAAround<String> result = around.___("step name");

    assertThat(result)
      .isSameAs(around);
    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(StaticStepListener.lastContext)
      .isEqualTo(NullableOptional.of("context value"));
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsOnContextValue() {
    around().___("step name", params());

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndExpectedResultOnContextValue() {
    around().___("step name", "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }

  @Test
  void asteriskMethodExecutesEmptyStepWithNameAndParamsAndExpectedResultOnContextValue() {
    around().___("step name", params(), "expected result");

    assertThat(executedStep().getKeyword())
      .isSameAs(AAAKeywords.asterisk());
    assertThat(executedStep().getName()).isEqualTo("step name");
    assertThat(executedStep().getParams()).containsEntry("param name", "param value");
    assertThat(executedStep().getExpectedResult()).isEqualTo("expected result");
  }
}
