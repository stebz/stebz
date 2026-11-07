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

import org.stebz.annotation.Step;
import org.stebz.annotation.WithName;
import org.stebz.core.step.executable.RunnableStep;

/**
 * Step methods with Gherkin keyword annotations, used as annotation instances source.
 */
final class MethodSteps {

  MethodSteps() {
  }

  @Background
  static RunnableStep gherkinBackgroundAnnotationWithoutName() {
    return RunnableStep.empty();
  }

  @Rule
  @WithName("step name")
  static RunnableStep gherkinRuleAnnotationWithWithNameAnnotation() {
    return RunnableStep.empty();
  }

  @Given
  static RunnableStep gherkinGivenAnnotationWithoutName() {
    return RunnableStep.empty();
  }

  @When("custom name")
  static RunnableStep gherkinWhenAnnotationWithName() {
    return RunnableStep.empty();
  }

  @Then("custom name")
  @Step("step name")
  static RunnableStep gherkinThenAnnotationWithNameAndStepAnnotation() {
    return RunnableStep.empty();
  }

  @And("custom name")
  @WithName("step name")
  static RunnableStep gherkinAndAnnotationWithNameAndWithNameAnnotation() {
    return RunnableStep.empty();
  }

  @But
  static RunnableStep gherkinButAnnotationWithoutName() {
    return RunnableStep.empty();
  }

  @___
  static RunnableStep gherkinAsteriskAnnotationWithoutName() {
    return RunnableStep.empty();
  }

  @Technical("custom name")
  static RunnableStep gherkinTechnicalAnnotationWithName() {
    return RunnableStep.empty();
  }
}
