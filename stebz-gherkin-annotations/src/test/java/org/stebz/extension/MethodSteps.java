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

import org.stebz.annotation.And;
import org.stebz.annotation.But;
import org.stebz.annotation.Given;
import org.stebz.annotation.Step;
import org.stebz.annotation.Then;
import org.stebz.annotation.When;
import org.stebz.annotation.WithName;
import org.stebz.step.executable.RunnableStep;

final class MethodSteps {

  MethodSteps() {
  }

  @Given
  static RunnableStep gherkinKeywordAnnotationWithoutName() {
    return RunnableStep.empty();
  }

  @But
  @WithName("step name")
  static RunnableStep gherkinKeywordAnnotationWithoutNameAndWithNameAnnotation() {
    return RunnableStep.empty();
  }

  @When("custom name")
  static RunnableStep gherkinKeywordAnnotationWithName() {
    return RunnableStep.empty();
  }

  @Then("custom name")
  @Step("step name")
  static RunnableStep gherkinKeywordAnnotationWithNameAndStepAnnotation() {
    return RunnableStep.empty();
  }

  @And("custom name")
  @WithName("step name")
  static RunnableStep gherkinKeywordAnnotationWithNameAndWithNameAnnotation() {
    return RunnableStep.empty();
  }
}
