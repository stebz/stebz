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
import org.stebz.annotation.aaa.Act;
import org.stebz.annotation.aaa.And;
import org.stebz.annotation.aaa.Arrange;
import org.stebz.annotation.aaa.Assert;
import org.stebz.annotation.aaa.But;
import org.stebz.executor.StepExecutor;
import org.stebz.step.StepObj;
import org.stebz.step.executable.RunnableStep;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.extension.AAAAnnotationsExtension.AAA_KEYWORD;

/**
 * Tests for {@link AAAAnnotationsExtension}.
 */
final class AAAAnnotationsExtensionTest {

  @BeforeEach
  void clearListener() {
    StaticStepListener.clear();
  }

  @Test
  void aaaKeywordAnnotationWithoutName() {
    final RunnableStep step = MethodSteps.aaaKeywordAnnotationWithoutName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Arrange.class);
    assertThat(((Arrange) annotation).value())
      .isEmpty();

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("Arrange");
    assertThat(stepFromListener.getName())
      .isEqualTo("aaaKeywordAnnotationWithoutName");
  }

  @Test
  void aaaKeywordAnnotationWithoutNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.aaaKeywordAnnotationWithoutNameAndWithNameAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Act.class);
    assertThat(((Act) annotation).value())
      .isEmpty();

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("Act");
    assertThat(stepFromListener.getName())
      .isEqualTo("step name");
  }

  @Test
  void aaaKeywordAnnotationWithName() {
    final RunnableStep step = MethodSteps.aaaKeywordAnnotationWithName();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(Assert.class);
    assertThat(((Assert) annotation).value())
      .isEqualTo("custom name");

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("Assert");
    assertThat(stepFromListener.getName())
      .isEqualTo("custom name");
  }

  @Test
  void aaaKeywordAnnotationWithNameAndStepAnnotation() {
    final RunnableStep step = MethodSteps.aaaKeywordAnnotationWithNameAndStepAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
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

  @Test
  void aaaKeywordAnnotationWithNameAndWithNameAnnotation() {
    final RunnableStep step = MethodSteps.aaaKeywordAnnotationWithNameAndWithNameAnnotation();

    final Annotation annotation = step.get(AAA_KEYWORD);
    assertThat(annotation)
      .isInstanceOf(But.class);
    assertThat(((But) annotation).value())
      .isEqualTo("custom name");

    StepExecutor.get().execute(step);
    final StepObj<?> stepFromListener = StaticStepListener.lastStep;
    assertThat(stepFromListener)
      .isNotNull();
    assertThat(stepFromListener.getKeyword().value())
      .isEqualTo("But");
    assertThat(stepFromListener.getName())
      .isEqualTo("custom name");
  }
}
