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
package org.stebz.annotation.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.stebz.annotation.WithComment;
import org.stebz.annotation.WithName;
import org.stebz.annotation.attribute.StepSourceType;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.SimpleStepAttribute;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.DECLARED_ANNOTATIONS;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.JOIN_POINT;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.STEP_SOURCE;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.STEP_SOURCE_TYPE;

/**
 * Tests for {@link StepAspects}.
 */
final class StepAspectsTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
    MethodSteps.quickStaticMethodBodyCount.set(0);
    MethodSteps.paramOnlyMethodBodyCount.set(0);
    QuickConstructorStep.ctorBodyCount.set(0);
  }

  /**
   * Tests for {@link StepAspects#fieldStepObjWithAttributes}.
   */
  @Nested
  class FieldStepTests {

    @Test
    void staticFieldStepAttributes() {
      final RunnableStep step = FieldSteps.staticFieldRunnableStep;

      assertThat(step.getName())
        .isEqualTo("name value");
      assertThat(step.getComment())
        .isEqualTo("comment value");
      assertThat(step.getParams())
        .isEmpty();
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void instanceFieldStepAttributes() {
      final RunnableStep step = new FieldSteps().instanceFieldRunnableStep;

      assertThat(step.getName())
        .isEqualTo("instanceFieldRunnableStep");
      assertThat(step.getParams())
        .containsExactly(
          entry("param name", "param value")
        );
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void customAttributeOnField() {
      final SupplierStep<String> step = FieldSteps.customAttrFieldStep;
      final SimpleStepAttribute<CustomAttr> attr = SimpleStepAttribute.nullable(CustomAttr.class);

      assertThat(step.get(attr).value())
        .isEqualTo("attr value");
    }

    @Test
    void fieldReflectiveAttributes() {
      final RunnableStep step = FieldSteps.staticFieldRunnableStep;
      final List<Class<? extends Annotation>> declaredAnnotations = Arrays.stream(step.get(DECLARED_ANNOTATIONS))
        .map(Annotation::annotationType)
        .collect(Collectors.toList());

      assertThat(step.get(JOIN_POINT))
        .isNotNull();
      assertThat(step.get(STEP_SOURCE_TYPE))
        .isSameAs(StepSourceType.FIELD);
      assertThat(step.get(STEP_SOURCE))
        .isInstanceOf(Field.class)
        .extracting(f -> ((Field) f).getName())
        .isEqualTo("staticFieldRunnableStep");
      assertThat(step.get(REFLECTIVE_NAME))
        .isEqualTo("staticFieldRunnableStep");
      assertThat(declaredAnnotations)
        .containsExactlyInAnyOrder(WithName.class, WithComment.class);
    }
  }

  /**
   * Tests for {@link StepAspects#methodStepObjWithAttribute}.
   */
  @Nested
  class StepObjMethodTests {

    @Test
    void staticMethodStepAttributes() {
      final RunnableStep step = MethodSteps.staticMethodRunnableStep();

      assertThat(step.getName())
        .isEqualTo("name value");
      assertThat(step.getComment())
        .isEqualTo("comment value");
      assertThat(step.getParams())
        .isEmpty();
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void staticMethodStepParamsAreMergedWithStepParams() {
      final ConsumerStep<String> step = MethodSteps.staticMethodConsumerStep("value2", "value3");

      assertThat(step.getName())
        .isEqualTo("name value");
      assertThat(step.getParams())
        .containsExactly(
          entry("param1", "value1"),
          entry("param2", "value2"),
          entry("param3", "value3")
        );
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void staticMethodStepExpectedResult() {
      final SupplierStep<String> step = MethodSteps.staticMethodSupplierStep();

      assertThat(step.getExpectedResult())
        .isEqualTo("expected value");
      assertThat(step.getName())
        .isEmpty();
    }

    @Test
    void instanceMethodStepWithEmptyName() {
      final FunctionStep<String, String> step = new MethodSteps().instanceMethodFunctionStep("value1", "value2");

      assertThat(step.getName())
        .isEqualTo("instanceMethodFunctionStep");
      assertThat(step.getParams())
        .containsExactly(
          entry("param2", "value2")
        );
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void methodWithoutMarkerAnnotationIsOnlyEnriched() {
      final RunnableStep step = MethodSteps.paramOnlyMethodStep("value");

      assertThat(step.getName())
        .isEmpty();
      assertThat(step.getParams())
        .containsExactly(
          entry("renamed param", "value")
        );
      assertThat(step.get(STEP_SOURCE_TYPE))
        .isSameAs(StepSourceType.METHOD);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void methodReflectiveAttributes() throws Exception {
      final RunnableStep step = MethodSteps.staticMethodRunnableStep();

      assertThat(step.get(JOIN_POINT))
        .isNotNull();
      assertThat(step.get(STEP_SOURCE_TYPE))
        .isSameAs(StepSourceType.METHOD);
      assertThat(step.get(STEP_SOURCE))
        .isInstanceOf(Method.class)
        .extracting(m -> ((Method) m).getName())
        .isEqualTo("staticMethodRunnableStep");
      assertThat(step.get(REFLECTIVE_NAME))
        .isEqualTo("staticMethodRunnableStep");
    }
  }

  /**
   * Tests for {@link StepAspects#quickMethodStep}.
   */
  @Nested
  class QuickMethodTests {

    @Test
    void quickStaticMethodIsExecutedAsStep() {
      MethodSteps.quickStaticMethodStep();

      assertThat(MethodSteps.quickStaticMethodBodyCount)
        .hasValue(1);
      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      assertThat(StaticStepListener.succeededSteps)
        .hasSize(1);
      final StepObj<?> step = StaticStepListener.startedSteps.get(0);
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
    void quickInstanceMethodResultIsReturned() {
      final String result = new MethodSteps().quickInstanceMethodStep("value");

      assertThat(result)
        .isEqualTo("value");
      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      final StepObj<?> step = StaticStepListener.startedSteps.get(0);
      assertThat(step)
        .isInstanceOf(SupplierStep.class);
      assertThat(step.getName())
        .isEqualTo("quickInstanceMethodStep");
      assertThat(step.getParams())
        .containsExactly(
          entry("param", "value")
        );
      assertThat(step.isHidden())
        .isTrue();
    }

    @Test
    void quickMethodKeyword() {
      MethodSteps.quickMethodWithKeyword();

      assertThat(StaticStepListener.startedSteps.get(0).getKeyword())
        .isEqualTo(new Keyword.Of("keyword value"));
      assertThat(StaticStepListener.startedSteps.get(0).getName())
        .isEqualTo("name value");
    }

    @Test
    void quickMethodEmptyKeyword() {
      MethodSteps.quickMethodWithEmptyKeyword();

      assertThat(StaticStepListener.startedSteps.get(0).getKeyword())
        .isEqualTo(Keyword.empty());
      assertThat(StaticStepListener.startedSteps.get(0).getName())
        .isEqualTo("name value");
    }

    @Test
    void quickMethodParamsAddedByDefault() {
      MethodSteps.quickMethodWithParams("value1", "value2");

      assertThat(StaticStepListener.startedSteps.get(0).getParams())
        .containsExactly(
          entry("param1", "value1"),
          entry("param2", "value2")
        );
    }

    @Test
    void quickMethodParamModifiers() {
      MethodSteps.quickMethodWithParamModifiers("value1", "value2", "value3");

      assertThat(StaticStepListener.startedSteps.get(0).getParams())
        .containsExactly(
          entry("masked param", "******"),
          entry("val param", "constant value")
        );
    }

    @Test
    void quickMethodRepeatedParams() {
      MethodSteps.quickMethodWithRepeatedParam();

      assertThat(StaticStepListener.startedSteps.get(0).getParams())
        .containsExactly(
          entry("first param", "first value"),
          entry("second param", "second value")
        );
    }

    @Test
    void quickMethodCustomAttributes() {
      MethodSteps.quickMethodWithCustomAttributes();

      final StepObj<?> step = StaticStepListener.startedSteps.get(0);
      final SimpleStepAttribute<CustomAttr> attr = SimpleStepAttribute.nullable(CustomAttr.class);
      final SimpleStepAttribute<KeyedCustomAttr> keyedAttr = SimpleStepAttribute.nullable(CustomAttrKey.class);
      assertThat(step.get(attr).value())
        .isEqualTo("attr value");
      assertThat(step.get(keyedAttr).value())
        .isEqualTo("keyed attr value");
    }

    @Test
    void quickMethodCommentAndExpectedResult() {
      final String result = MethodSteps.quickMethodWithExpectedResult();

      assertThat(result)
        .isEqualTo("result value");
      final StepObj<?> step = StaticStepListener.startedSteps.get(0);
      assertThat(step.getComment())
        .isEqualTo("comment value");
      assertThat(step.getExpectedResult())
        .isEqualTo("expected value");
      assertThat(StaticStepListener.succeededSteps.get(0))
        .isSameAs(step);
    }

    @Test
    void methodWithParamOnlyIsNotAStep() {
      final String result = MethodSteps.paramOnlyMethod("value");

      assertThat(result)
        .isEqualTo("value");
      assertThat(MethodSteps.paramOnlyMethodBodyCount)
        .hasValue(1);
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
      assertThat(StaticStepListener.succeededSteps)
        .isEmpty();
    }

    @Test
    void quickThrowingMethodFailsStep() {
      assertThatThrownBy(MethodSteps::quickThrowingMethodStep)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("step exception");

      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      assertThat(StaticStepListener.failedSteps)
        .hasSize(1);
      assertThat(StaticStepListener.succeededSteps)
        .isEmpty();
      assertThat(StaticStepListener.startedSteps.get(0).getName())
        .isEqualTo("name value");
    }
  }

  /**
   * Tests for {@link StepAspects#ctorStepObjWithAttributes} and {@link StepAspects#quickCtorStep}.
   */
  @Nested
  class ConstructorTests {

    @Test
    void constructorStepAttributes() {
      final RunnableStep step = new ConstructorStep("value1", "value2");

      assertThat(step.getName())
        .isEqualTo("ConstructorStep");
      assertThat(step.getKeyword())
        .isEqualTo(new Keyword.Of("keyword value"));
      assertThat(step.getParams())
        .containsExactly(
          entry("param2", "value2")
        );
      assertThat(StaticStepListener.startedSteps)
        .isEmpty();
    }

    @Test
    void constructorReflectiveAttributes() {
      final RunnableStep step = new ConstructorStep("value1", "value2");

      assertThat(step.get(JOIN_POINT))
        .isNotNull();
      assertThat(step.get(STEP_SOURCE_TYPE))
        .isSameAs(StepSourceType.CONSTRUCTOR);
      assertThat(step.get(STEP_SOURCE))
        .isInstanceOf(Constructor.class)
        .extracting(c -> ((Constructor<?>) c).getDeclaringClass().getSimpleName())
        .isEqualTo("ConstructorStep");
      assertThat(step.get(REFLECTIVE_NAME))
        .isEqualTo("ConstructorStep");
    }

    @Test
    void quickConstructorStepAttributes() {
      new QuickConstructorStep("value1", "value2");

      assertThat(QuickConstructorStep.ctorBodyCount)
        .hasValue(1);
      assertThat(StaticStepListener.startedSteps)
        .hasSize(1);
      final StepObj<?> step = StaticStepListener.startedSteps.get(0);
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
  }
}
