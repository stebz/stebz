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
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;
import static org.stebz.extension.AAAKeywords._assert;
import static org.stebz.extension.AAAKeywords.act;
import static org.stebz.extension.AAAKeywords.and;
import static org.stebz.extension.AAAKeywords.arrange;
import static org.stebz.extension.AAAKeywords.setup;

/**
 * Stebz AAA methods.
 */
public final class StebzAAAMethods {

  /**
   * Utility class ctor.
   */
  private StebzAAAMethods() {
  }

  /**
   * Returns {@link AAAAround} object of given value.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @return {@link AAAAround} object of given value
   */
  public static <T> AAAAround<T> around(final T value) {
    return new AAAAround.Of<>(StepExecutor.get(), value);
  }

  /**
   * Returns {@link AAAAround} object of given value. Alias for {@link #around(Object)} method.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @return {@link AAAAround} object of given value
   */
  public static <T> AAAAround<T> aaaAround(final T value) {
    return around(value);
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword.
   *
   * @param step the step
   */
  public static void Act(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(act()));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Act(final String name,
                         final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, act(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Act(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, act()));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Act(final String name,
                          final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, act(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Act(final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, act()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Act(final String name,
                             final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, act(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Act(final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, act()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Act(final String name,
                             final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, act(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Act(final String name,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Act(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void Act(final String name,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void Act(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Act(final String name,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, act(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Act(final String name,
                          final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R Act(final String name,
                          final String expectedResult,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Act(final String name,
                          final Map<String, ?> params,
                          final String expectedResult,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Act(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Act(final String name,
                         final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void Act(final String name,
                         final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void Act(final String name,
                         final Map<String, ?> params,
                         final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, act(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword.
   *
   * @param step the step
   */
  public static void And(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(and()));
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void And(final String name,
                         final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, and(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R And(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, and()));
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R And(final String name,
                          final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, and(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void And(final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, and()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void And(final String name,
                             final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, and(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R And(final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, and()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#and()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R And(final String name,
                             final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, and(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void And(final String name,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void And(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void And(final String name,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void And(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R And(final String name,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, and(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R And(final String name,
                          final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R And(final String name,
                          final String expectedResult,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R And(final String name,
                          final Map<String, ?> params,
                          final String expectedResult,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void And(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void And(final String name,
                         final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void And(final String name,
                         final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, and(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#and()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void And(final String name,
                         final Map<String, ?> params,
                         final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, and(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword.
   *
   * @param step the step
   */
  public static void Arrange(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(arrange()));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Arrange(final String name,
                             final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, arrange(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Arrange(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, arrange()));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Arrange(final String name,
                              final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, arrange(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Arrange(final ConsumerStep<? super T> step,
                                 final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, arrange()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Arrange(final String name,
                                 final ConsumerStep<? super T> step,
                                 final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, arrange(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Arrange(final FunctionStep<? super T, ? extends R> step,
                                 final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, arrange()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Arrange(final String name,
                                 final FunctionStep<? super T, ? extends R> step,
                                 final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, arrange(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Arrange(final String name,
                             final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Arrange(final String name,
                             final Map<String, ?> params,
                             final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void Arrange(final String name,
                             final String expectedResult,
                             final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void Arrange(final String name,
                             final Map<String, ?> params,
                             final String expectedResult,
                             final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Arrange(final String name,
                              final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Arrange(final String name,
                              final Map<String, ?> params,
                              final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R Arrange(final String name,
                              final String expectedResult,
                              final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Arrange(final String name,
                              final Map<String, ?> params,
                              final String expectedResult,
                              final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Arrange(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Arrange(final String name,
                             final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void Arrange(final String name,
                             final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void Arrange(final String name,
                             final Map<String, ?> params,
                             final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, arrange(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword.
   *
   * @param step the step
   */
  public static void Assert(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(_assert()));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Assert(final String name,
                            final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, _assert(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Assert(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, _assert()));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Assert(final String name,
                             final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, _assert(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Assert(final ConsumerStep<? super T> step,
                                final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, _assert()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Assert(final String name,
                                final ConsumerStep<? super T> step,
                                final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, _assert(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Assert(final FunctionStep<? super T, ? extends R> step,
                                final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, _assert()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Assert(final String name,
                                final FunctionStep<? super T, ? extends R> step,
                                final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, _assert(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Assert(final String name,
                            final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Assert(final String name,
                            final Map<String, ?> params,
                            final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void Assert(final String name,
                            final String expectedResult,
                            final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void Assert(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Assert(final String name,
                             final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Assert(final String name,
                             final Map<String, ?> params,
                             final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R Assert(final String name,
                             final String expectedResult,
                             final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Assert(final String name,
                             final Map<String, ?> params,
                             final String expectedResult,
                             final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Assert(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Assert(final String name,
                            final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void Assert(final String name,
                            final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void Assert(final String name,
                            final Map<String, ?> params,
                            final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, _assert(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword.
   *
   * @param step the step
   */
  public static void Setup(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(setup()));
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Setup(final String name,
                           final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, setup(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Setup(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, setup()));
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Setup(final String name,
                            final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, setup(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Setup(final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, setup()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Setup(final String name,
                               final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, setup(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Setup(final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, setup()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setup()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Setup(final String name,
                               final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, setup(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Setup(final String name,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Setup(final String name,
                           final Map<String, ?> params,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void Setup(final String name,
                           final String expectedResult,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void Setup(final String name,
                           final Map<String, ?> params,
                           final String expectedResult,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Setup(final String name,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Setup(final String name,
                            final Map<String, ?> params,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R Setup(final String name,
                            final String expectedResult,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Setup(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Setup(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Setup(final String name,
                           final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void Setup(final String name,
                           final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setup(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setup()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void Setup(final String name,
                           final Map<String, ?> params,
                           final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, setup(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }
}
