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

import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttributes;
import org.stebz.executor.StepExecutor;
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

/**
 * Stebz methods.
 */
public final class StebzMethods {

  /**
   * Utility class ctor.
   */
  private StebzMethods() {
  }

  /**
   * Returns {@link Around} object of given value.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @return {@link Around} object of given value
   */
  public static <T> Around<T> around(final T value) {
    return new Around.Of<>(StepExecutor.get(), value);
  }

  /**
   * Executes given step.
   *
   * @param step the step
   */
  public static void step(final RunnableStep step) {
    StepExecutor.get().execute(step);
  }

  /**
   * Executes given step with keyword.
   *
   * @param keyword the keyword
   * @param step    the step
   */
  public static void step(final Keyword keyword,
                          final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, keyword));
  }

  /**
   * Executes given step with name.
   *
   * @param name the name
   * @param step the step
   */
  public static void step(final String name,
                          final RunnableStep step) {
    StepExecutor.get().execute(step.with(NAME, name));
  }

  /**
   * Executes given step with keyword and name.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   */
  public static void step(final Keyword keyword,
                          final String name,
                          final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, keyword, NAME, name));
  }

  /**
   * Executes given step and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R step(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step);
  }

  /**
   * Executes given step with keyword and returns step result.
   *
   * @param keyword the keyword
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  public static <R> R step(final Keyword keyword,
                           final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, keyword));
  }

  /**
   * Executes given step with name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R step(final String name,
                           final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(NAME, name));
  }

  /**
   * Executes given step with keyword and name and returns step result.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  public static <R> R step(final Keyword keyword,
                           final String name,
                           final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, keyword, NAME, name));
  }

  /**
   * Executes given step on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void step(final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step,
      value
    );
  }

  /**
   * Executes given step with keyword on given value.
   *
   * @param keyword the keyword
   * @param step    the step
   * @param value   the value
   * @param <T>     the type of the value
   */
  public static <T> void step(final Keyword keyword,
                              final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, keyword),
      value
    );
  }

  /**
   * Executes given step with name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void step(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(NAME, name),
      value
    );
  }

  /**
   * Executes given step with keyword and name on given value.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @param value   the value
   * @param <T>     the type of the value
   */
  public static <T> void step(final Keyword keyword,
                              final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, keyword, NAME, name),
      value
    );
  }

  /**
   * Executes given step on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R step(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step,
      value
    );
  }

  /**
   * Executes given step with keyword on given value and returns step result.
   *
   * @param keyword the keyword
   * @param step    the step
   * @param value   the value
   * @param <T>     the type of the value
   * @param <R>     the type of the step result
   * @return step result
   */
  public static <T, R> R step(final Keyword keyword,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, keyword),
      value
    );
  }

  /**
   * Executes given step with name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R step(final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(NAME, name),
      value
    );
  }

  /**
   * Executes given step with keyword and name on given value and returns step result.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @param value   the value
   * @param <T>     the type of the value
   * @param <R>     the type of the step result
   * @return step result
   */
  public static <T, R> R step(final Keyword keyword,
                              final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, keyword, NAME, name),
      value
    );
  }

  /**
   * Executes step with given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void step(final String name,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param body    the step body
   */
  public static void step(final Keyword keyword,
                          final String name,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void step(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   * @param body    the step body
   */
  @SuppressWarnings("unchecked")
  public static void step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void step(final String name,
                          final String expectedResult,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void step(final Keyword keyword,
                          final String name,
                          final String expectedResult,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void step(final String name,
                          final Map<String, ?> params,
                          final String expectedResult,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params,
                          final String expectedResult,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R step(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(NAME, name),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param body    the step body
   * @param <R>     the type of the step result
   * @return step result
   */
  public static <R> R step(final Keyword keyword,
                           final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R step(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   * @param body    the step body
   * @param <R>     the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R step(final Keyword keyword,
                           final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static <R> R step(final String name,
                           final String expectedResult,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static <R> R step(final Keyword keyword,
                           final String name,
                           final String expectedResult,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static <R> R step(final String name,
                           final Map<String, ?> params,
                           final String expectedResult,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes and returns step result.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static <R> R step(final Keyword keyword,
                           final String name,
                           final Map<String, ?> params,
                           final String expectedResult,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name the step name
   */
  public static void step(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   */
  public static void step(final Keyword keyword,
                          final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void step(final String name,
                          final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   */
  @SuppressWarnings("unchecked")
  public static void step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void step(final String name,
                          final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void step(final Keyword keyword,
                          final String name,
                          final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, keyword, NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void step(final String name,
                          final Map<String, ?> params,
                          final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with given attributes.
   *
   * @param keyword        the step keyword
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params,
                          final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }
}
