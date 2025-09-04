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
import org.stebz.extension.AAAKeywords;
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
import static org.stebz.extension.AAAKeywords._assert;
import static org.stebz.extension.AAAKeywords.act;
import static org.stebz.extension.AAAKeywords.and;
import static org.stebz.extension.AAAKeywords.arrange;
import static org.stebz.extension.AAAKeywords.but;

/**
 * Allows to call steps around a specific context with Arrange-Act-Assert keywords.
 *
 * @param <T> the type of the context
 */
public interface AAAAround<T> {

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Act(RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Act(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Act(String name,
            SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Act(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Act(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name on the context value and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Act(String name,
            FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   Map<String, ?> params,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   Map<String, ?> params,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Act(String name,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Act(String name,
            Map<String, ?> params,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Act(String name,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Act(String name,
            Map<String, ?> params,
            String expectedResult,
            ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   Map<String, ?> params);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   String expectedResult);

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Act(String name,
                   Map<String, ?> params,
                   String expectedResult);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> And(RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(String name,
            SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> And(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R And(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name on the context value and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R And(String name,
            FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   Map<String, ?> params,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   Map<String, ?> params,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  AAAAround<T> And(String name);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   Map<String, ?> params);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   String expectedResult);

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> And(String name,
                   Map<String, ?> params,
                   String expectedResult);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Arrange(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Arrange(String name,
                SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Arrange(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Arrange(String name,
                FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       Map<String, ?> params,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       Map<String, ?> params,
                       String expectedResult,
                       ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Arrange(String name,
                ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Arrange(String name,
                Map<String, ?> params,
                ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Arrange(String name,
                String expectedResult,
                ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Arrange(String name,
                Map<String, ?> params,
                String expectedResult,
                ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       Map<String, ?> params);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       String expectedResult);

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Arrange(String name,
                       Map<String, ?> params,
                       String expectedResult);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Assert(RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Assert(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Assert(String name,
               SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Assert(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Assert(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name on the context value and returns step
   * result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R Assert(String name,
               FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      Map<String, ?> params,
                      ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      String expectedResult,
                      ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      Map<String, ?> params,
                      String expectedResult,
                      ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R Assert(String name,
               ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value and returns step
   * result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R Assert(String name,
               Map<String, ?> params,
               ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Assert(String name,
               String expectedResult,
               ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes on the context value and returns step
   * result
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  <R> R Assert(String name,
               Map<String, ?> params,
               String expectedResult,
               ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      Map<String, ?> params);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      String expectedResult);

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> Assert(String name,
                      Map<String, ?> params,
                      String expectedResult);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> But(RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword and name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   RunnableStep step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(String name,
            SupplierStep<? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> But(ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword and name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   ConsumerStep<? super T> step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R But(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with {@link AAAKeywords#but()} keyword and name on the context value and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R But(String name,
            FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   Map<String, ?> params,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   Map<String, ?> params,
                   String expectedResult,
                   ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes on the context value and returns step
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
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  AAAAround<T> But(String name);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   Map<String, ?> params);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   String expectedResult);

  /**
   * Executes step with {@link AAAKeywords#but()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @return {@code Around} object
   */
  AAAAround<T> But(String name,
                   Map<String, ?> params,
                   String expectedResult);

  /**
   * Default {@code AAAAround} implementation.
   *
   * @param <T> the type of the context
   */
  class Of<T> implements AAAAround<T> {
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
    public AAAAround<T> Act(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, act()));
      return this;
    }

    @Override
    public AAAAround<T> Act(final String name,
                            final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, act(), NAME, name));
      return this;
    }

    @Override
    public <R> R Act(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, act()));
    }

    @Override
    public <R> R Act(final String name,
                     final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, act(), NAME, name));
    }

    @Override
    public AAAAround<T> Act(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, act()),
        this.context
      );
      return this;
    }

    @Override
    public AAAAround<T> Act(final String name,
                            final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, act(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Act(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, act()),
        this.context
      );
    }

    @Override
    public <R> R Act(final String name,
                     final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, act(), NAME, name),
        this.context
      );
    }

    @Override
    public AAAAround<T> Act(final String name,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Act(final String name,
                            final Map<String, ?> params,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public AAAAround<T> Act(final String name,
                            final String expectedResult,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Act(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Act(final String name,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Act(final String name,
                     final Map<String, ?> params,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Act(final String name,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Act(final String name,
                     final Map<String, ?> params,
                     final String expectedResult,
                     final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public AAAAround<T> Act(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, act(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Act(final String name,
                            final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> Act(final String name,
                            final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Act(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> And(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, and()));
      return this;
    }

    @Override
    public AAAAround<T> And(final String name,
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
    public AAAAround<T> And(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, and()),
        this.context
      );
      return this;
    }

    @Override
    public AAAAround<T> And(final String name,
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
    public AAAAround<T> And(final String name,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> And(final String name,
                            final Map<String, ?> params,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public AAAAround<T> And(final String name,
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
    public AAAAround<T> And(final String name,
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
    public AAAAround<T> And(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> And(final String name,
                            final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> And(final String name,
                            final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> And(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> Arrange(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, arrange()));
      return this;
    }

    @Override
    public AAAAround<T> Arrange(final String name,
                                final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, arrange(), NAME, name));
      return this;
    }

    @Override
    public <R> R Arrange(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, arrange()));
    }

    @Override
    public <R> R Arrange(final String name,
                         final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, arrange(), NAME, name));
    }

    @Override
    public AAAAround<T> Arrange(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, arrange()),
        this.context
      );
      return this;
    }

    @Override
    public AAAAround<T> Arrange(final String name,
                                final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, arrange(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Arrange(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, arrange()),
        this.context
      );
    }

    @Override
    public <R> R Arrange(final String name,
                         final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, arrange(), NAME, name),
        this.context
      );
    }

    @Override
    public AAAAround<T> Arrange(final String name,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Arrange(final String name,
                                final Map<String, ?> params,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public AAAAround<T> Arrange(final String name,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Arrange(final String name,
                                final Map<String, ?> params,
                                final String expectedResult,
                                final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Arrange(final String name,
                         final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Arrange(final String name,
                         final Map<String, ?> params,
                         final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Arrange(final String name,
                         final String expectedResult,
                         final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Arrange(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public AAAAround<T> Arrange(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Arrange(final String name,
                                final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> Arrange(final String name,
                                final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Arrange(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> Assert(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, _assert()));
      return this;
    }

    @Override
    public AAAAround<T> Assert(final String name,
                               final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, _assert(), NAME, name));
      return this;
    }

    @Override
    public <R> R Assert(final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, _assert()));
    }

    @Override
    public <R> R Assert(final String name,
                        final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, _assert(), NAME, name));
    }

    @Override
    public AAAAround<T> Assert(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, _assert()),
        this.context
      );
      return this;
    }

    @Override
    public AAAAround<T> Assert(final String name,
                               final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, _assert(), NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R Assert(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, _assert()),
        this.context
      );
    }

    @Override
    public <R> R Assert(final String name,
                        final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, _assert(), NAME, name),
        this.context
      );
    }

    @Override
    public AAAAround<T> Assert(final String name,
                               final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Assert(final String name,
                               final Map<String, ?> params,
                               final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public AAAAround<T> Assert(final String name,
                               final String expectedResult,
                               final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Assert(final String name,
                               final Map<String, ?> params,
                               final String expectedResult,
                               final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R Assert(final String name,
                        final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Assert(final String name,
                        final Map<String, ?> params,
                        final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public <R> R Assert(final String name,
                        final String expectedResult,
                        final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R Assert(final String name,
                        final Map<String, ?> params,
                        final String expectedResult,
                        final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(
          KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        body
      ), this.context);
    }

    @Override
    public AAAAround<T> Assert(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Assert(final String name,
                               final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> Assert(final String name,
                               final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> Assert(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> But(final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, but()));
      return this;
    }

    @Override
    public AAAAround<T> But(final String name,
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
    public AAAAround<T> But(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, but()),
        this.context
      );
      return this;
    }

    @Override
    public AAAAround<T> But(final String name,
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
    public AAAAround<T> But(final String name,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> But(final String name,
                            final Map<String, ?> params,
                            final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public AAAAround<T> But(final String name,
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
    public AAAAround<T> But(final String name,
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
    public AAAAround<T> But(final String name) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> But(final String name,
                            final Map<String, ?> params) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    public AAAAround<T> But(final String name,
                            final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(KEYWORD, but(), NAME, name, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AAAAround<T> But(final String name, final Map<String, ?> params, final String expectedResult) {
      this.executor.execute(new RunnableStep.Of(
        new StepAttributes.Of(
          KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
        RunnableStep.emptyBody()
      ));
      return this;
    }
  }
}
