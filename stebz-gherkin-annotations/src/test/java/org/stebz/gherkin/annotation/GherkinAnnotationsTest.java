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
package org.stebz.gherkin.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.gherkin.annotation.GherkinAnnotations.GHERKIN_KEYWORD;

/**
 * Integration tests for {@link GherkinAnnotations}: step annotation processing by the Stebz annotations aspect and
 * the extension step interception.
 */
final class GherkinAnnotationsTest {

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
  void backgroundAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.gherkinBackgroundAnnotationWithoutName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Background.class);
    assertThat(((Background) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "Background:", "gherkinBackgroundAnnotationWithoutName");
  }

  @Test
  void ruleAnnotationWithWithNameAnnotation() {
    final RunnableStep step = MethodSteps.gherkinRuleAnnotationWithWithNameAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Rule.class);
    assertThat(((Rule) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "Rule:", "step name");
  }

  @Test
  void givenAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.gherkinGivenAnnotationWithoutName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Given.class);
    assertThat(((Given) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "Given", "gherkinGivenAnnotationWithoutName");
  }

  @Test
  void whenAnnotationWithName() {
    final RunnableStep step = MethodSteps.gherkinWhenAnnotationWithName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(When.class);
    assertThat(((When) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "When", "custom name");
  }

  @Test
  void thenAnnotationWithNameAndStepAnnotation() {
    final RunnableStep step = MethodSteps.gherkinThenAnnotationWithNameAndStepAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Then.class);
    assertThat(((Then) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "Then", "custom name");
  }

  @Test
  void andAnnotationWithNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.gherkinAndAnnotationWithNameAndWithNameAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(And.class);
    assertThat(((And) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "And", "custom name");
  }

  @Test
  void butAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.gherkinButAnnotationWithoutName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(But.class);
    assertThat(((But) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "But", "gherkinButAnnotationWithoutName");
  }

  @Test
  void asteriskAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.gherkinAsteriskAnnotationWithoutName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(___.class);
    assertThat(((___) annotation).value())
      .isEmpty();

    assertExecutedStep(step, "*", "gherkinAsteriskAnnotationWithoutName");
  }

  @Test
  void technicalAnnotationWithName() {
    final RunnableStep step = MethodSteps.gherkinTechnicalAnnotationWithName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Technical.class);
    assertThat(((Technical) annotation).value())
      .isEqualTo("custom name");

    assertExecutedStep(step, "Technical:", "custom name");
  }
}
