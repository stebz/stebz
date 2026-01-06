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
package org.stebz.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepSourceType;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.stebz.attribute.ReflectiveStepAttributes.JOIN_POINT;
import static org.stebz.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.attribute.ReflectiveStepAttributes.STEP_SOURCE_TYPE;

/**
 * Tests for {@link StepAspects}.
 */
final class StepAspectsTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  @Test
  void staticFieldRunnableStep() {
    final RunnableStep step = FieldSteps.staticFieldRunnableStep;

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getComment())
      .isEqualTo("comment value");
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void instanceFieldRunnableStep() {
    final RunnableStep step = new FieldSteps().instanceFieldRunnableStep;

    assertThat(step.getName())
      .isEqualTo("instanceFieldRunnableStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param name", "param value")
      );
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void staticMethodRunnableStep() {
    final RunnableStep step = MethodSteps.staticMethodRunnableStep();

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getComment())
      .isEqualTo("comment value");
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void staticMethodConsumerStep() {
    final ConsumerStep<String> step = MethodSteps.staticMethodConsumerStep("value2", "value3");

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getParams())
      .containsExactly(
        entry("param1", "value1"),
        entry("param2", "value2"),
        entry("param3", "value3")
      );
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void quickStaticMethodStep() {
    MethodSteps.quickStaticMethodStep();

    final StepObj<?> step = StaticStepListener.lastStep;
    assertThat(step)
      .isInstanceOf(RunnableStep.class);
    assertThat(step.getName())
      .isEqualTo("quickStaticMethodStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param name", "param value")
      );
  }

  @Test
  void instanceMethodSupplierStep() {
    final SupplierStep<String> step = new MethodSteps().instanceMethodSupplierStep();

    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getComment())
      .isEqualTo("comment value");
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void instanceMethodFunctionStep() {
    final FunctionStep<String, String> step = new MethodSteps().instanceMethodFunctionStep("value1", "value2");

    assertThat(step.getName())
      .isEqualTo("instanceMethodFunctionStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param2", "value2")
      );
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void quickInstanceMethodStep() {
    new MethodSteps().quickInstanceMethodStep("value");

    final StepObj<?> step = StaticStepListener.lastStep;
    assertThat(step)
      .isInstanceOf(SupplierStep.class);
    assertThat(step.getName())
      .isEqualTo("quickInstanceMethodStep");
    assertThat(step.getParams())
      .containsExactly(
        entry("param", "value")
      );
    assertThat(step.getHidden())
      .isTrue();
  }

  @Test
  void constructorStep() {
    final RunnableStep step = new ConstructorStep("value1", "value2");

    assertThat(step.getName())
      .isEqualTo("ConstructorStep");
    assertThat(step.getKeyword())
      .isEqualTo(new Keyword.Of("keyword value"));
    assertThat(step.getParams())
      .containsExactly(
        entry("param2", "value2")
      );
    assertThat(StaticStepListener.lastStep)
      .isNull();
  }

  @Test
  void quickConstructorStep() {
    new QuickConstructorStep("value1", "value2");

    final StepObj<?> step = StaticStepListener.lastStep;
    assertThat(step)
      .isInstanceOf(SupplierStep.class);
    assertThat(step.getName())
      .isEqualTo("name value");
    assertThat(step.getParams())
      .containsExactly(
        entry("param1", "******"),
        entry("param name", "param value")
      );
  }

  @Test
  void fieldReflectiveAttributes() {
    final RunnableStep step = FieldSteps.staticFieldRunnableStep;

    assertThat(step.get(JOIN_POINT))
      .isNotNull();
    assertThat(step.get(STEP_SOURCE_TYPE))
      .isSameAs(StepSourceType.FIELD);
    assertThat(step.get(REFLECTIVE_NAME))
      .isEqualTo("staticFieldRunnableStep");
  }

  @Test
  void methodReflectiveAttributes() {
    final RunnableStep step = MethodSteps.staticMethodRunnableStep();

    assertThat(step.get(JOIN_POINT))
      .isNotNull();
    assertThat(step.get(STEP_SOURCE_TYPE))
      .isSameAs(StepSourceType.METHOD);
    assertThat(step.get(REFLECTIVE_NAME))
      .isEqualTo("staticMethodRunnableStep");
  }

  @Test
  void constructorReflectiveAttributes() {
    final RunnableStep step = new ConstructorStep("value1", "value2");

    assertThat(step.get(JOIN_POINT))
      .isNotNull();
    assertThat(step.get(STEP_SOURCE_TYPE))
      .isSameAs(StepSourceType.CONSTRUCTOR);
    assertThat(step.get(REFLECTIVE_NAME))
      .isEqualTo("ConstructorStep");
  }
}
