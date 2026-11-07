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

import org.stebz.annotation.Param;
import org.stebz.annotation.Step;
import org.stebz.annotation.WithComment;
import org.stebz.annotation.WithExpectedResult;
import org.stebz.annotation.WithHidden;
import org.stebz.annotation.WithKeyword;
import org.stebz.annotation.WithName;
import org.stebz.annotation.WithParam;
import org.stebz.annotation.WithParams;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;

import java.util.concurrent.atomic.AtomicInteger;

import static org.stebz.core.attribute.StepAttribute.params;

/**
 * Step methods fixtures.
 */
final class MethodSteps {
  static final AtomicInteger quickStaticMethodBodyCount = new AtomicInteger();
  static final AtomicInteger paramOnlyMethodBodyCount = new AtomicInteger();

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

  @WithExpectedResult("expected value")
  public static SupplierStep<String> staticMethodSupplierStep() {
    return SupplierStep.of(() -> "result value");
  }

  @WithName
  public FunctionStep<String, String> instanceMethodFunctionStep(final String param1,
                                                                 @Param(name = "param2") final String abcdef) {
    return FunctionStep.of(v -> v);
  }

  public static RunnableStep paramOnlyMethodStep(@Param(name = "renamed param") final String param) {
    return RunnableStep.empty();
  }

  @Step
  @WithParam(name = "param name", val = "param value")
  public static void quickStaticMethodStep() {
    quickStaticMethodBodyCount.incrementAndGet();
  }

  @Step
  @WithHidden
  public String quickInstanceMethodStep(final String param) {
    return param;
  }

  @Step("name value")
  @WithKeyword("keyword value")
  public static void quickMethodWithKeyword() {
  }

  @Step("name value")
  @WithKeyword("")
  public static void quickMethodWithEmptyKeyword() {
  }

  @Step
  @WithParams
  public static void quickMethodWithParams(final String param1,
                                           final String param2) {
  }

  @Step
  @WithParams
  public static void quickMethodWithParamModifiers(
    @Param(value = "masked param", masked = true) final String param1,
    @Param(name = "val param", val = "constant value") final String param2,
    @Param(hidden = true) final String param3
  ) {
  }

  @Step
  @WithParam(name = "first param", val = "first value")
  @WithParam(name = "second param", val = "second value")
  public static void quickMethodWithRepeatedParam() {
  }

  @Step
  @CustomAttr("attr value")
  @KeyedCustomAttr("keyed attr value")
  public static void quickMethodWithCustomAttributes() {
  }

  @Step
  @WithComment("comment value")
  @WithExpectedResult("expected value")
  public static String quickMethodWithExpectedResult() {
    return "result value";
  }

  public static String paramOnlyMethod(@Param(name = "renamed param") final String param) {
    paramOnlyMethodBodyCount.incrementAndGet();
    return param;
  }

  @Step("name value")
  public static void quickThrowingMethodStep() {
    throw new IllegalStateException("step exception");
  }
}
