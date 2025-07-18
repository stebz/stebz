/*
 * MIT License
 *
 * Copyright (c) 2025 Evgenii Plugatar
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
package org.stebz.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.stebz.annotation.And;
import org.stebz.annotation.But;
import org.stebz.annotation.Given;
import org.stebz.annotation.Then;
import org.stebz.annotation.When;
import org.stebz.executor.StepExecutor;
import org.stebz.step.StepObj;
import org.stebz.step.executable.RunnableStep;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.attribute.GherkinAnnotationStepAttributes.GHERKIN_KEYWORD;

/**
 * Tests for {@link GherkinAnnotationsExtension}.
 */
final class GherkinAnnotationsExtensionTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  @Test
  void gherkinKeywordAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.gherkinKeywordAnnotationWithoutName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Given.class);
    assertThat(((Given) annotation).value())
      .isEmpty();

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("Given");
    assertThat(stepFromListener.getName())
      .isEqualTo("gherkinKeywordAnnotationWithoutName");
  }

  @Test
  void gherkinKeywordAnnotationWithoutNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.gherkinKeywordAnnotationWithoutNameAndWithNameAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(But.class);
    assertThat(((But) annotation).value())
      .isEmpty();

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("But");
    assertThat(stepFromListener.getName())
      .isEqualTo("step name");
  }

  @Test
  void gherkinKeywordAnnotationWithName() {
    final RunnableStep step = MethodSteps.gherkinKeywordAnnotationWithName();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(When.class);
    assertThat(((When) annotation).value())
      .isEqualTo("custom name");

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("When");
    assertThat(stepFromListener.getName())
      .isEqualTo("custom name");
  }

  @Test
  void gherkinKeywordAnnotationWithNameAndStepAnnotation() {
    final RunnableStep step = MethodSteps.gherkinKeywordAnnotationWithNameAndStepAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Then.class);
    assertThat(((Then) annotation).value())
      .isEqualTo("custom name");

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("Then");
    assertThat(stepFromListener.getName())
      .isEqualTo("custom name");
  }

  @Test
  void gherkinKeywordAnnotationWithNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.gherkinKeywordAnnotationWithNameAndWithNameAnnotation();

    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(And.class);
    assertThat(((And) annotation).value())
      .isEqualTo("custom name");

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("And");
    assertThat(stepFromListener.getName())
      .isEqualTo("custom name");
  }
}
