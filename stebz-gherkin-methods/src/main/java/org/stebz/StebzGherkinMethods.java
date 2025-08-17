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
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;
import static org.stebz.extension.GherkinKeywords.and;
import static org.stebz.extension.GherkinKeywords.but;
import static org.stebz.extension.GherkinKeywords.given;
import static org.stebz.extension.GherkinKeywords.then;
import static org.stebz.extension.GherkinKeywords.when;

/**
 * Stebz Gherkin methods.
 */
public final class StebzGherkinMethods {

  /**
   * Utility class ctor.
   */
  private StebzGherkinMethods() {
  }

  /**
   * Returns {@link GherkinAround} object of given value.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @return {@link GherkinAround} object of given value
   */
  public static <T> GherkinAround<T> around(final T value) {
    return new GherkinAround.Of<>(StepExecutor.get(), value);
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword.
   *
   * @param step the step
   */
  public static void Given(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(given()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Given(final String name,
                           final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, given(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Given(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, given()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Given(final String name,
                            final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, given(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Given(final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, given()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Given(final String name,
                               final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, given(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Given(final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, given()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#given()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Given(final String name,
                               final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, given(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Given(final String name,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, given(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Given(final String name,
                           final Map<String, ?> params,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Given(final String name,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, given(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Given(final String name,
                            final Map<String, ?> params,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Given(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, given(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#given()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Given(final String name,
                           final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, given(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword.
   *
   * @param step the step
   */
  public static void When(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(when()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void When(final String name,
                          final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, when(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R When(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, when()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R When(final String name,
                           final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, when(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void When(final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, when()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void When(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, when(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R When(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, when()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#when()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R When(final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, when(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void When(final String name,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, when(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void When(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R When(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, when(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R When(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void When(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, when(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#when()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void When(final String name,
                          final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, when(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword.
   *
   * @param step the step
   */
  public static void Then(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(then()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Then(final String name,
                          final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, then(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Then(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, then()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Then(final String name,
                           final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, then(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Then(final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, then()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Then(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, then(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Then(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, then()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#then()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Then(final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, then(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Then(final String name,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, then(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void Then(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Then(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, then(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R Then(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Then(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, then(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#then()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void Then(final String name,
                          final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, then(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword.
   *
   * @param step the step
   */
  public static void And(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(and()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void And(final String name,
                         final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, and(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R And(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, and()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#and()} keyword and name and returns step result.
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
   * Executes given step with {@link GherkinKeywords#and()} keyword on given value.
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
   * Executes given step with {@link GherkinKeywords#and()} keyword and name on given value.
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
   * Executes given step with {@link GherkinKeywords#and()} keyword on given value and returns step result.
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
   * Executes given step with {@link GherkinKeywords#and()} keyword and name on given value and returns step result.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes and returns step result.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes and returns step result.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
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
   * Executes step with {@link GherkinKeywords#and()} keyword and given attributes.
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
   * Executes given step with {@link GherkinKeywords#but()} keyword.
   *
   * @param step the step
   */
  public static void But(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(but()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void But(final String name,
                         final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, but(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R But(final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, but()));
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R But(final String name,
                          final SupplierStep<R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, but(), NAME, name));
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void But(final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, but()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void But(final String name,
                             final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, but(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R But(final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, but()),
      value
    );
  }

  /**
   * Executes given step with {@link GherkinKeywords#but()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R But(final String name,
                             final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, but(), NAME, name),
      value
    );
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void But(final String name,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, but(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void But(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R But(final String name,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, but(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R But(final String name,
                          final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void But(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, but(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link GherkinKeywords#but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void But(final String name,
                         final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, but(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }
}
