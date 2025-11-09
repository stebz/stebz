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
package org.stebz.aspect;

import org.stebz.annotation.Param;
import org.stebz.annotation.Step;
import org.stebz.annotation.WithComment;
import org.stebz.annotation.WithHidden;
import org.stebz.annotation.WithName;
import org.stebz.annotation.WithParam;
import org.stebz.annotation.WithParams;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;

import static org.stebz.attribute.StepAttribute.params;

final class MethodSteps {

  MethodSteps() {
  }

  @WithName("name value")
  @WithComment("comment value")
  public static RunnableStep staticMethodRunnableStep() {
    return RunnableStep.empty();
  }

  @WithName("name value")
  @WithParams
  public static ConsumerStep<String> staticMethodConsumerStep(final String param2,
                                                              final String param3) {
    return ConsumerStep.of("old name", params("param1", "value1"), ConsumerStep.emptyBody());
  }

  @Step
  @WithParam(name = "param name", value = "param value")
  public static void quickStaticMethodStep() {
  }

  @Step("name value")
  @WithComment("comment value")
  public SupplierStep<String> instanceMethodSupplierStep() {
    return SupplierStep.of(() -> "result value");
  }

  @WithName
  public FunctionStep<String, String> instanceMethodFunctionStep(final String param1,
                                                                 @Param(name = "param2") final String abcdef) {
    return FunctionStep.of(v -> v);
  }

  @Step
  @WithHidden
  public String quickInstanceMethodStep(final String param) {
    return param;
  }
}
