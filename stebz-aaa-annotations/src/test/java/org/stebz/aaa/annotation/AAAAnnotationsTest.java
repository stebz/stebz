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
package org.stebz.aaa.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.aaa.annotation.AAAAnnotations.AAA_KEYWORD;

/**
 * Integration tests for {@link AAAAnnotations}: step annotation processing by the Stebz annotations aspect and the
 * extension step interception.
 */
final class AAAAnnotationsTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  private static void assertExecutedStep(final RunnableStep step,
                                         final String expectedKeywordValue,
                                         final String expectedName) {
    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo(expectedKeywordValue);
    assertThat(stepFromListener.getName())
      .isEqualTo(expectedName);
  }

  @Test
  void arrangeAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.aaaArrangeAnnotationWithoutName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Arrange.class);
    assertThat(((Arrange) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "Arrange:", "aaaArrangeAnnotationWithoutName");
  }

  @Test
  void actAnnotationWithWithNameAnnotation() {
    final RunnableStep step = MethodSteps.aaaActAnnotationWithWithNameAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Act.class);
    assertThat(((Act) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "Act:", "step name");
  }

  @Test
  void assertAnnotationWithName() {
    final RunnableStep step = MethodSteps.aaaAssertAnnotationWithName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Assert.class);
    assertThat(((Assert) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "Assert:", "custom name");
  }

  @Test
  void setUpAnnotationWithNameAndStepAnnotation() {
    final RunnableStep step = MethodSteps.aaaSetUpAnnotationWithNameAndStepAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(SetUp.class);
    assertThat(((SetUp) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "Set Up:", "custom name");
  }

  @Test
  void tearDownAnnotationWithNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.aaaTearDownAnnotationWithNameAndWithNameAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(TearDown.class);
    assertThat(((TearDown) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "Tear Down:", "custom name");
  }

  @Test
  void asteriskAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.aaaAsteriskAnnotationWithoutName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(___.class);
    assertThat(((___) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "*", "aaaAsteriskAnnotationWithoutName");
  }

  @Test
  void asteriskAnnotationWithName() {
    final RunnableStep step = MethodSteps.aaaAsteriskAnnotationWithName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(___.class);
    assertThat(((___) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "*", "custom name");
  }
}
