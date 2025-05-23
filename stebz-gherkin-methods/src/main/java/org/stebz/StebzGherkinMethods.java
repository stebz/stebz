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
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.executor.StepExecutor;
import org.stebz.extension.StebzExtension;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;
import org.stebz.util.property.PropertiesReader;

import java.util.Map;

import static org.stebz.StebzMethods.step;

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
   * Returns "Given" keyword.
   *
   * @return "Given" keyword
   */
  public static Keyword given() {
    return GherkinMethodsExtension.keywords().given;
  }

  /**
   * Returns "When" keyword.
   *
   * @return "When" keyword
   */
  public static Keyword when() {
    return GherkinMethodsExtension.keywords().when;
  }

  /**
   * Returns "Then" keyword.
   *
   * @return "Then" keyword
   */
  public static Keyword then() {
    return GherkinMethodsExtension.keywords().then;
  }

  /**
   * Returns "And" keyword.
   *
   * @return "And" keyword
   */
  public static Keyword and() {
    return GherkinMethodsExtension.keywords().and;
  }

  /**
   * Returns "But" keyword.
   *
   * @return "But" keyword
   */
  public static Keyword but() {
    return GherkinMethodsExtension.keywords().but;
  }

  /**
   * Returns "Background" keyword.
   *
   * @return "Background" keyword
   */
  public static Keyword background() {
    return GherkinMethodsExtension.keywords().background;
  }

  /**
   * Returns "Asterisk" keyword.
   *
   * @return "Asterisk" keyword
   */
  public static Keyword asterisk() {
    return GherkinMethodsExtension.keywords().asterisk;
  }

  /**
   * Executes given step with {@link #given()} keyword.
   *
   * @param step the step
   */
  public static void Given(final RunnableStep step) {
    step(given(), step);
  }

  /**
   * Executes given step with {@link #given()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Given(final String name,
                           final RunnableStep step) {
    step(given(), name, step);
  }

  /**
   * Executes given step with {@link #given()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Given(final SupplierStep<R> step) {
    return step(given(), step);
  }

  /**
   * Executes given step with {@link #given()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Given(final String name,
                            final SupplierStep<R> step) {
    return step(given(), name, step);
  }

  /**
   * Executes given step with {@link #given()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Given(final ConsumerStep<? super T> step,
                               final T value) {
    step(given(), step, value);
  }

  /**
   * Executes given step with {@link #given()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Given(final String name,
                               final ConsumerStep<? super T> step,
                               final T value) {
    step(given(), name, step, value);
  }

  /**
   * Executes given step with {@link #given()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Given(final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return step(given(), step, value);
  }

  /**
   * Executes given step with {@link #given()} keyword and name on given value and returns step result.
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
    return step(given(), name, step, value);
  }

  /**
   * Executes step with {@link #given()} keyword.
   *
   * @param body the step body
   */
  public static void Given(final ThrowingRunnable<?> body) {
    step(given(), body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Given(final String name,
                           final ThrowingRunnable<?> body) {
    step(given(), name, body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void Given(final Map<String, ?> params,
                           final ThrowingRunnable<?> body) {
    step(given(), params, body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void Given(final String name,
                           final Map<String, ?> params,
                           final ThrowingRunnable<?> body) {
    step(given(), name, params, body);
  }

  /**
   * Executes step with {@link #given()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Given(final ThrowingSupplier<? extends R, ?> body) {
    return step(given(), body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Given(final String name,
                            final ThrowingSupplier<? extends R, ?> body) {
    return step(given(), name, body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Given(final Map<String, ?> params,
                            final ThrowingSupplier<? extends R, ?> body) {
    return step(given(), params, body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Given(final String name,
                            final Map<String, ?> params,
                            final ThrowingSupplier<? extends R, ?> body) {
    return step(given(), name, params, body);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Given(final String name) {
    step(given(), name);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void Given(final Map<String, ?> params) {
    step(given(), params);
  }

  /**
   * Executes step with {@link #given()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void Given(final String name,
                           final Map<String, ?> params) {
    step(given(), name, params);
  }

  /**
   * Executes given step with {@link #when()} keyword.
   *
   * @param step the step
   */
  public static void When(final RunnableStep step) {
    step(when(), step);
  }

  /**
   * Executes given step with {@link #when()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void When(final String name,
                          final RunnableStep step) {
    step(when(), name, step);
  }

  /**
   * Executes given step with {@link #when()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R When(final SupplierStep<R> step) {
    return step(when(), step);
  }

  /**
   * Executes given step with {@link #when()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R When(final String name,
                           final SupplierStep<R> step) {
    return step(when(), name, step);
  }

  /**
   * Executes given step with {@link #when()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void When(final ConsumerStep<? super T> step,
                              final T value) {
    step(when(), step, value);
  }

  /**
   * Executes given step with {@link #when()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void When(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    step(when(), name, step, value);
  }

  /**
   * Executes given step with {@link #when()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R When(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(when(), step, value);
  }

  /**
   * Executes given step with {@link #when()} keyword and name on given value and returns step result.
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
    return step(when(), name, step, value);
  }

  /**
   * Executes step with {@link #when()} keyword.
   *
   * @param body the step body
   */
  public static void When(final ThrowingRunnable<?> body) {
    step(when(), body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void When(final String name,
                          final ThrowingRunnable<?> body) {
    step(when(), name, body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void When(final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(when(), params, body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void When(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(when(), name, params, body);
  }

  /**
   * Executes step with {@link #when()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R When(final ThrowingSupplier<? extends R, ?> body) {
    return step(when(), body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R When(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(when(), name, body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R When(final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(when(), params, body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R When(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(when(), name, params, body);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void When(final String name) {
    step(when(), name);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void When(final Map<String, ?> params) {
    step(when(), params);
  }

  /**
   * Executes step with {@link #when()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void When(final String name,
                          final Map<String, ?> params) {
    step(when(), name, params);
  }

  /**
   * Executes given step with {@link #then()} keyword.
   *
   * @param step the step
   */
  public static void Then(final RunnableStep step) {
    step(then(), step);
  }

  /**
   * Executes given step with {@link #then()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Then(final String name,
                          final RunnableStep step) {
    step(then(), name, step);
  }

  /**
   * Executes given step with {@link #then()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Then(final SupplierStep<R> step) {
    return step(then(), step);
  }

  /**
   * Executes given step with {@link #then()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Then(final String name,
                           final SupplierStep<R> step) {
    return step(then(), name, step);
  }

  /**
   * Executes given step with {@link #then()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Then(final ConsumerStep<? super T> step,
                              final T value) {
    step(then(), step, value);
  }

  /**
   * Executes given step with {@link #then()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Then(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    step(then(), name, step, value);
  }

  /**
   * Executes given step with {@link #then()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Then(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(then(), step, value);
  }

  /**
   * Executes given step with {@link #then()} keyword and name on given value and returns step result.
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
    return step(then(), name, step, value);
  }

  /**
   * Executes step with {@link #then()} keyword.
   *
   * @param body the step body
   */
  public static void Then(final ThrowingRunnable<?> body) {
    step(then(), body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Then(final String name,
                          final ThrowingRunnable<?> body) {
    step(then(), name, body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void Then(final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(then(), params, body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void Then(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(then(), name, params, body);
  }

  /**
   * Executes step with {@link #then()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Then(final ThrowingSupplier<? extends R, ?> body) {
    return step(then(), body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Then(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(then(), name, body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Then(final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(then(), params, body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Then(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(then(), name, params, body);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Then(final String name) {
    step(then(), name);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void Then(final Map<String, ?> params) {
    step(then(), params);
  }

  /**
   * Executes step with {@link #then()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void Then(final String name,
                          final Map<String, ?> params) {
    step(then(), name, params);
  }

  /**
   * Executes given step with {@link #and()} keyword.
   *
   * @param step the step
   */
  public static void And(final RunnableStep step) {
    step(and(), step);
  }

  /**
   * Executes given step with {@link #and()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void And(final String name,
                         final RunnableStep step) {
    step(and(), name, step);
  }

  /**
   * Executes given step with {@link #and()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R And(final SupplierStep<R> step) {
    return step(and(), step);
  }

  /**
   * Executes given step with {@link #and()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R And(final String name,
                          final SupplierStep<R> step) {
    return step(and(), name, step);
  }

  /**
   * Executes given step with {@link #and()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void And(final ConsumerStep<? super T> step,
                             final T value) {
    step(and(), step, value);
  }

  /**
   * Executes given step with {@link #and()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void And(final String name,
                             final ConsumerStep<? super T> step,
                             final T value) {
    step(and(), name, step, value);
  }

  /**
   * Executes given step with {@link #and()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R And(final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return step(and(), step, value);
  }

  /**
   * Executes given step with {@link #and()} keyword and name on given value and returns step result.
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
    return step(and(), name, step, value);
  }

  /**
   * Executes step with {@link #and()} keyword.
   *
   * @param body the step body
   */
  public static void And(final ThrowingRunnable<?> body) {
    step(and(), body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void And(final String name,
                         final ThrowingRunnable<?> body) {
    step(and(), name, body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void And(final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    step(and(), params, body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void And(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    step(and(), name, params, body);
  }

  /**
   * Executes step with {@link #and()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R And(final ThrowingSupplier<? extends R, ?> body) {
    return step(and(), body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R And(final String name,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(and(), name, body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R And(final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(and(), params, body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R And(final String name,
                          final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(and(), name, params, body);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void And(final String name) {
    step(and(), name);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void And(final Map<String, ?> params) {
    step(and(), params);
  }

  /**
   * Executes step with {@link #and()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void And(final String name,
                         final Map<String, ?> params) {
    step(and(), name, params);
  }

  /**
   * Executes given step with {@link #but()} keyword.
   *
   * @param step the step
   */
  public static void But(final RunnableStep step) {
    step(but(), step);
  }

  /**
   * Executes given step with {@link #but()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void But(final String name,
                         final RunnableStep step) {
    step(but(), name, step);
  }

  /**
   * Executes given step with {@link #but()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R But(final SupplierStep<R> step) {
    return step(but(), step);
  }

  /**
   * Executes given step with {@link #but()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R But(final String name,
                          final SupplierStep<R> step) {
    return step(but(), name, step);
  }

  /**
   * Executes given step with {@link #but()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void But(final ConsumerStep<? super T> step,
                             final T value) {
    step(but(), step, value);
  }

  /**
   * Executes given step with {@link #but()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void But(final String name,
                             final ConsumerStep<? super T> step,
                             final T value) {
    step(but(), name, step, value);
  }

  /**
   * Executes given step with {@link #but()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R But(final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return step(but(), step, value);
  }

  /**
   * Executes given step with {@link #but()} keyword and name on given value and returns step result.
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
    return step(but(), name, step, value);
  }

  /**
   * Executes step with {@link #but()} keyword.
   *
   * @param body the step body
   */
  public static void But(final ThrowingRunnable<?> body) {
    step(but(), body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void But(final String name,
                         final ThrowingRunnable<?> body) {
    step(but(), name, body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void But(final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    step(but(), params, body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void But(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    step(but(), name, params, body);
  }

  /**
   * Executes step with {@link #but()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R But(final ThrowingSupplier<? extends R, ?> body) {
    return step(but(), body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R But(final String name,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(but(), name, body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R But(final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(but(), params, body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R But(final String name,
                          final Map<String, ?> params,
                          final ThrowingSupplier<? extends R, ?> body) {
    return step(but(), name, params, body);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void But(final String name) {
    step(but(), name);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void But(final Map<String, ?> params) {
    step(but(), params);
  }

  /**
   * Executes step with {@link #but()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void But(final String name,
                         final Map<String, ?> params) {
    step(but(), name, params);
  }

  /**
   * Executes given step with {@link #background()} keyword.
   *
   * @param step the step
   */
  public static void Background(final RunnableStep step) {
    step(background(), step);
  }

  /**
   * Executes given step with {@link #background()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Background(final String name,
                                final RunnableStep step) {
    step(background(), name, step);
  }

  /**
   * Executes given step with {@link #background()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Background(final SupplierStep<R> step) {
    return step(background(), step);
  }

  /**
   * Executes given step with {@link #background()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Background(final String name,
                                 final SupplierStep<R> step) {
    return step(background(), name, step);
  }

  /**
   * Executes given step with {@link #background()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Background(final ConsumerStep<? super T> step,
                                    final T value) {
    step(background(), step, value);
  }

  /**
   * Executes given step with {@link #background()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Background(final String name,
                                    final ConsumerStep<? super T> step,
                                    final T value) {
    step(background(), name, step, value);
  }

  /**
   * Executes given step with {@link #background()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Background(final FunctionStep<? super T, ? extends R> step,
                                    final T value) {
    return step(background(), step, value);
  }

  /**
   * Executes given step with {@link #background()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Background(final String name,
                                    final FunctionStep<? super T, ? extends R> step,
                                    final T value) {
    return step(background(), name, step, value);
  }

  /**
   * Executes step with {@link #background()} keyword.
   *
   * @param body the step body
   */
  public static void Background(final ThrowingRunnable<?> body) {
    step(background(), body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Background(final String name,
                                final ThrowingRunnable<?> body) {
    step(background(), name, body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void Background(final Map<String, ?> params,
                                final ThrowingRunnable<?> body) {
    step(background(), params, body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void Background(final String name,
                                final Map<String, ?> params,
                                final ThrowingRunnable<?> body) {
    step(background(), name, params, body);
  }

  /**
   * Executes step with {@link #background()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Background(final ThrowingSupplier<? extends R, ?> body) {
    return step(background(), body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Background(final String name,
                                 final ThrowingSupplier<? extends R, ?> body) {
    return step(background(), name, body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Background(final Map<String, ?> params,
                                 final ThrowingSupplier<? extends R, ?> body) {
    return step(background(), params, body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Background(final String name,
                                 final Map<String, ?> params,
                                 final ThrowingSupplier<? extends R, ?> body) {
    return step(background(), name, params, body);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Background(final String name) {
    step(background(), name);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void Background(final Map<String, ?> params) {
    step(background(), params);
  }

  /**
   * Executes step with {@link #background()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void Background(final String name,
                                final Map<String, ?> params) {
    step(background(), name, params);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword.
   *
   * @param step the step
   */
  public static void Step(final RunnableStep step) {
    step(asterisk(), step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void Step(final String name,
                          final RunnableStep step) {
    step(asterisk(), name, step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Step(final SupplierStep<R> step) {
    return step(asterisk(), step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Step(final String name,
                           final SupplierStep<R> step) {
    return step(asterisk(), name, step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Step(final ConsumerStep<? super T> step,
                              final T value) {
    step(asterisk(), step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void Step(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    step(asterisk(), name, step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Step(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(asterisk(), step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R Step(final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(asterisk(), name, step, value);
  }

  /**
   * Executes step with {@link #asterisk()} keyword.
   *
   * @param body the step body
   */
  public static void Step(final ThrowingRunnable<?> body) {
    step(asterisk(), body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void Step(final String name,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), name, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void Step(final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void Step(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), name, params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Step(final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Step(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), name, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Step(final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R Step(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), name, params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void Step(final String name) {
    step(asterisk(), name);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void Step(final Map<String, ?> params) {
    step(asterisk(), params);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void Step(final String name,
                          final Map<String, ?> params) {
    step(asterisk(), name, params);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword.
   *
   * @param step the step
   */
  public static void _And(final RunnableStep step) {
    step(asterisk(), step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void _And(final String name,
                          final RunnableStep step) {
    step(asterisk(), name, step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R _And(final SupplierStep<R> step) {
    return step(asterisk(), step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R _And(final String name,
                           final SupplierStep<R> step) {
    return step(asterisk(), name, step);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void _And(final ConsumerStep<? super T> step,
                              final T value) {
    step(asterisk(), step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void _And(final String name,
                              final ConsumerStep<? super T> step,
                              final T value) {
    step(asterisk(), name, step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R _And(final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(asterisk(), step, value);
  }

  /**
   * Executes given step with {@link #asterisk()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R _And(final String name,
                              final FunctionStep<? super T, ? extends R> step,
                              final T value) {
    return step(asterisk(), name, step, value);
  }

  /**
   * Executes step with {@link #asterisk()} keyword.
   *
   * @param body the step body
   */
  public static void _And(final ThrowingRunnable<?> body) {
    step(asterisk(), body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void _And(final String name,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), name, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param params the step params
   * @param body   the step body
   */
  public static void _And(final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  public static void _And(final String name,
                          final Map<String, ?> params,
                          final ThrowingRunnable<?> body) {
    step(asterisk(), name, params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R _And(final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R _And(final String name,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), name, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R _And(final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  public static <R> R _And(final String name,
                           final Map<String, ?> params,
                           final ThrowingSupplier<? extends R, ?> body) {
    return step(asterisk(), name, params, body);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void _And(final String name) {
    step(asterisk(), name);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param params the step params
   */
  public static void _And(final Map<String, ?> params) {
    step(asterisk(), params);
  }

  /**
   * Executes step with {@link #asterisk()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  public static void _And(final String name,
                          final Map<String, ?> params) {
    step(asterisk(), name, params);
  }

  /**
   * Gherkin Methods extension.
   */
  public static class GherkinMethodsExtension implements StebzExtension {
    private static final ThrowingRunnable.RunningOnce INIT_STEP_EXECUTOR_ONCE =
      ThrowingRunnable.runningOnce(StepExecutor::get);
    private static volatile Keywords KEYWORDS = null;

    /**
     * Ctor.
     */
    public GherkinMethodsExtension() {
      this(StartupPropertiesReader.get());
    }

    private GherkinMethodsExtension(final PropertiesReader properties) {
      KEYWORDS = new Keywords(
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.given", "Given")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.when", "When")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.then", "Then")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.and", "And")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.but", "But")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.background", "Background")),
        new Keyword.Of(properties.getString("stebz.gherkin.keywords.asterisk", "*"))
      );
    }

    private static Keywords keywords() {
      INIT_STEP_EXECUTOR_ONCE.run();
      if (KEYWORDS == null) {
        throw new IllegalStateException("GherkinMethodsExtension is disabled");
      }
      return KEYWORDS;
    }
  }

  private static final class Keywords {
    public final Keyword given;
    public final Keyword when;
    public final Keyword then;
    public final Keyword and;
    public final Keyword but;
    public final Keyword background;
    public final Keyword asterisk;

    private Keywords(final Keyword given,
                     final Keyword when,
                     final Keyword then,
                     final Keyword and,
                     final Keyword but,
                     final Keyword background,
                     final Keyword asterisk) {
      this.given = given;
      this.when = when;
      this.then = then;
      this.and = and;
      this.but = but;
      this.background = background;
      this.asterisk = asterisk;
    }
  }
}
