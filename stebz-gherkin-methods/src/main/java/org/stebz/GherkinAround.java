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
package org.stebz;

import org.stebz.attribute.StepAttributes;
import org.stebz.executor.StepExecutor;
import org.stebz.extension.GherkinKeywords;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;
import static org.stebz.extension.GherkinKeywords.and;
import static org.stebz.extension.GherkinKeywords.background;
import static org.stebz.extension.GherkinKeywords.but;
import static org.stebz.extension.GherkinKeywords.conclusion;
import static org.stebz.extension.GherkinKeywords.given;
import static org.stebz.extension.GherkinKeywords.then;
import static org.stebz.extension.GherkinKeywords.when;

/**
 * Allows to call steps around a specific context with Gherkin keywords.
 *
 * @param <T> the type of the context
 */
public interface GherkinAround<T> {

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Background(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Background(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Background(String name,
                   SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Background(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword on the context value and returns step
   * result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Background(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#background()} keyword and name on the context value and returns
   * step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Background(String name,
                   FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Background(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              Map<String, ?> params,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              String expectedResult,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              Map<String, ?> params,
                              String expectedResult,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Background(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value and
   * returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Background(String name,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value and
   * returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Background(String name,
                   Map<String, ?> params,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value and
   * returns step result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Background(String name,
                   String expectedResult,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes on the context value and
   * returns step result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Background(String name,
                   Map<String, ?> params,
                   String expectedResult,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#background()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Background(String name,
                              Map<String, ?> params,
                              String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Conclusion(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Conclusion(String name,
                   SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword on the context value and returns step
   * result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Conclusion(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#conclusion()} keyword and name on the context value and returns
   * step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Conclusion(String name,
                   FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              Map<String, ?> params,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              String expectedResult,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              Map<String, ?> params,
                              String expectedResult,
                              ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Conclusion(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value and
   * returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Conclusion(String name,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value and
   * returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Conclusion(String name,
                   Map<String, ?> params,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value and
   * returns step result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Conclusion(String name,
                   String expectedResult,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes on the context value and
   * returns step result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Conclusion(String name,
                   Map<String, ?> params,
                   String expectedResult,
                   ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#conclusion()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Conclusion(String name,
                              Map<String, ?> params,
                              String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Given(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Given(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Given(String name,
              SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Given(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Given(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Given(String name,
              FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Given(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         Map<String, ?> params,
                         ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         String expectedResult,
                         ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         Map<String, ?> params,
                         String expectedResult,
                         ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Given(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Given(String name,
              ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Given(String name,
              Map<String, ?> params,
              ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Given(String name,
              String expectedResult,
              ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Given(String name,
              Map<String, ?> params,
              String expectedResult,
              ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Given(String name,
                         Map<String, ?> params,
                         String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> When(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R When(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R When(String name,
             SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> When(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R When(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R When(String name,
             FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> When(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        Map<String, ?> params,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        String expectedResult,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        Map<String, ?> params,
                        String expectedResult,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R When(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R When(String name,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R When(String name,
             Map<String, ?> params,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R When(String name,
             String expectedResult,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R When(String name,
             Map<String, ?> params,
             String expectedResult,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> When(String name,
                        Map<String, ?> params,
                        String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Then(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Then(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Then(String name,
             SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Then(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Then(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Then(String name,
             FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Then(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        Map<String, ?> params,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        String expectedResult,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        Map<String, ?> params,
                        String expectedResult,
                        ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Then(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Then(String name,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value and returns
   * step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Then(String name,
             Map<String, ?> params,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Then(String name,
             String expectedResult,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes on the context value and returns
   * step result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Then(String name,
             Map<String, ?> params,
             String expectedResult,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> Then(String name,
                        Map<String, ?> params,
                        String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> And(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(String name,
            SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> And(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R And(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(String name,
            FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> And(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       Map<String, ?> params,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       Map<String, ?> params,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R And(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R And(String name,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R And(String name,
            Map<String, ?> params,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R And(String name,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R And(String name,
            Map<String, ?> params,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> And(String name,
                       Map<String, ?> params,
                       String expectedResult);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> But(RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       RunnableStep step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(String name,
            SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> But(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R But(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(String name,
            FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword on the context value.
   *
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> But(ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       Map<String, ?> params,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       Map<String, ?> params,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword on the context value and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R But(ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R But(String name,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R But(String name,
            Map<String, ?> params,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R But(String name,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R But(String name,
            Map<String, ?> params,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       Map<String, ?> params);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       String expectedResult);

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  GherkinAround<T> But(String name,
                       Map<String, ?> params,
                       String expectedResult);

  /**
   * Default {@code GherkinAround} implementation.
   *
   * @param <T> the type of the context
   */
  class Of<T> implements GherkinAround<T> {
    private final StepExecutor executor;
    private final T context;

    /**
     * Ctor.
     *
     * @param executor the executor
     * @param context  the context
     * @throws NullPointerException if {@code executor} arg is null
     */
    public Of(final StepExecutor executor,
              final T context) {
      if (executor == null) { throw new NullPointerException("executor arg is null"); }
      this.executor = executor;
      this.context = context;
    }

    @Override
    public GherkinAround<T> Background(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, background()));
      return this;
    }

    @Override
    public GherkinAround<T> Background(final String name,
                                       final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, background(), NAME, name));
      return this;
    }

    @Override
    public <R> R Background(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, background()));
    }

    @Override
    public <R> R Background(final String name,
                            final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, background(), NAME, name));
    }

    @Override
    public GherkinAround<T> Background(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, background()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> Background(final String name,
                                       final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, background(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Background(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, background()),
        this.context
      );
    }

    @Override
    public <R> R Background(final String name,
                            final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, background(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> Background(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, background()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Background(final String name,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Background(final String name,
                                       final Map<String, ?> params,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Background(final String name,
                                       final String expectedResult,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Background(final String name,
                                       final Map<String, ?> params,
                                       final String expectedResult,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Background(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, background()),
        body
      ), this.context);
    }

    @Override
    public <R> R Background(final String name,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Background(final String name,
                            final Map<String, ?> params,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Background(final String name,
                            final String expectedResult,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Background(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> Background(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, background(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Background(final String name,
                                       final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Background(final String name,
                                       final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, background(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Background(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, background(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, conclusion()));
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final String name,
                                       final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, conclusion(), NAME, name));
      return this;
    }

    @Override
    public <R> R Conclusion(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, conclusion()));
    }

    @Override
    public <R> R Conclusion(final String name,
                            final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, conclusion(), NAME, name));
    }

    @Override
    public GherkinAround<T> Conclusion(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, conclusion()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final String name,
                                       final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, conclusion(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Conclusion(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, conclusion()),
        this.context
      );
    }

    @Override
    public <R> R Conclusion(final String name,
                            final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, conclusion(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> Conclusion(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final String name,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Conclusion(final String name,
                                       final Map<String, ?> params,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final String name,
                                       final String expectedResult,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Conclusion(final String name,
                                       final Map<String, ?> params,
                                       final String expectedResult,
                                       final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Conclusion(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion()),
        body
      ), this.context);
    }

    @Override
    public <R> R Conclusion(final String name,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Conclusion(final String name,
                            final Map<String, ?> params,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Conclusion(final String name,
                            final String expectedResult,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Conclusion(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> Conclusion(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Conclusion(final String name,
                                       final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Conclusion(final String name,
                                       final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, conclusion(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Conclusion(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, conclusion(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Given(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, given()));
      return this;
    }

    @Override
    public GherkinAround<T> Given(final String name,
                                  final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, given(), NAME, name));
      return this;
    }

    @Override
    public <R> R Given(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, given()));
    }

    @Override
    public <R> R Given(final String name,
                       final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, given(), NAME, name));
    }

    @Override
    public GherkinAround<T> Given(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, given()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> Given(final String name,
                                  final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, given(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Given(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, given()),
        this.context
      );
    }

    @Override
    public <R> R Given(final String name,
                       final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, given(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> Given(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, given()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Given(final String name,
                                  final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Given(final String name,
                                  final Map<String, ?> params,
                                  final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Given(final String name,
                                  final String expectedResult,
                                  final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Given(final String name,
                                  final Map<String, ?> params,
                                  final String expectedResult,
                                  final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Given(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, given()),
        body
      ), this.context);
    }

    @Override
    public <R> R Given(final String name,
                       final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Given(final String name,
                       final Map<String, ?> params,
                       final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Given(final String name,
                       final String expectedResult,
                       final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Given(final String name,
                       final Map<String, ?> params,
                       final String expectedResult,
                       final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> Given(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, given(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Given(final String name,
                                  final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Given(final String name,
                                  final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, given(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Given(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> When(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, when()));
      return this;
    }

    @Override
    public GherkinAround<T> When(final String name,
                                 final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, when(), NAME, name));
      return this;
    }

    @Override
    public <R> R When(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, when()));
    }

    @Override
    public <R> R When(final String name,
                      final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, when(), NAME, name));
    }

    @Override
    public GherkinAround<T> When(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, when()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> When(final String name,
                                 final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, when(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R When(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, when()),
        this.context
      );
    }

    @Override
    public <R> R When(final String name,
                      final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, when(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> When(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, when()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> When(final String name,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> When(final String name,
                                 final Map<String, ?> params,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> When(final String name,
                                 final String expectedResult,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> When(final String name,
                                 final Map<String, ?> params,
                                 final String expectedResult,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R When(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, when()),
        body
      ), this.context);
    }

    @Override
    public <R> R When(final String name,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R When(final String name,
                      final Map<String, ?> params,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R When(final String name,
                      final String expectedResult,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R When(final String name,
                      final Map<String, ?> params,
                      final String expectedResult,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> When(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, when(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> When(final String name,
                                 final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> When(final String name,
                                 final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, when(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> When(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Then(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, then()));
      return this;
    }

    @Override
    public GherkinAround<T> Then(final String name,
                                 final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, then(), NAME, name));
      return this;
    }

    @Override
    public <R> R Then(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, then()));
    }

    @Override
    public <R> R Then(final String name,
                      final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, then(), NAME, name));
    }

    @Override
    public GherkinAround<T> Then(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, then()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> Then(final String name,
                                 final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, then(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Then(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, then()),
        this.context
      );
    }

    @Override
    public <R> R Then(final String name,
                      final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, then(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> Then(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, then()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Then(final String name,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Then(final String name,
                                 final Map<String, ?> params,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> Then(final String name,
                                 final String expectedResult,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Then(final String name,
                                 final Map<String, ?> params,
                                 final String expectedResult,
                                 final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Then(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, then()),
        body
      ), this.context);
    }

    @Override
    public <R> R Then(final String name,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Then(final String name,
                      final Map<String, ?> params,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Then(final String name,
                      final String expectedResult,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Then(final String name,
                      final Map<String, ?> params,
                      final String expectedResult,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> Then(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, then(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Then(final String name,
                                 final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> Then(final String name,
                                 final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, then(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> Then(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> And(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, and()));
      return this;
    }

    @Override
    public GherkinAround<T> And(final String name,
                                final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, and(), NAME, name));
      return this;
    }

    @Override
    public <R> R And(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, and()));
    }

    @Override
    public <R> R And(final String name,
                     final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, and(), NAME, name));
    }

    @Override
    public GherkinAround<T> And(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, and()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> And(final String name,
                                final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, and(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R And(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, and()),
        this.context
      );
    }

    @Override
    public <R> R And(final String name,
                     final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, and(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> And(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> And(final String name,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> And(final String name,
                                final Map<String, ?> params,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> And(final String name,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> And(final String name,
                                final Map<String, ?> params,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R And(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, and()),
        body
      ), this.context);
    }

    @Override
    public <R> R And(final String name,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R And(final String name,
                     final Map<String, ?> params,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R And(final String name,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R And(final String name,
                     final Map<String, ?> params,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> And(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> And(final String name,
                                final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> And(final String name,
                                final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> And(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> But(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, but()));
      return this;
    }

    @Override
    public GherkinAround<T> But(final String name,
                                final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, but(), NAME, name));
      return this;
    }

    @Override
    public <R> R But(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, but()));
    }

    @Override
    public <R> R But(final String name,
                     final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, but(), NAME, name));
    }

    @Override
    public GherkinAround<T> But(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, but()),
        this.context
      );
      return this;
    }

    @Override
    public GherkinAround<T> But(final String name,
                                final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, but(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R But(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, but()),
        this.context
      );
    }

    @Override
    public <R> R But(final String name,
                     final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, but(), NAME, name),
        this.context
      );
    }

    @Override
    public GherkinAround<T> But(final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but()),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> But(final String name,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> But(final String name,
                                final Map<String, ?> params,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public GherkinAround<T> But(final String name,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> But(final String name,
                                final Map<String, ?> params,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R But(final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, but()),
        body
      ), this.context);
    }

    @Override
    public <R> R But(final String name,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R But(final String name,
                     final Map<String, ?> params,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R But(final String name,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R But(final String name,
                     final Map<String, ?> params,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public GherkinAround<T> But(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> But(final String name,
                                final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public GherkinAround<T> But(final String name,
                                final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GherkinAround<T> But(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }
  }
}
