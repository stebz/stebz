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
package org.stebz.aaa.method;

import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.stebz.aaa.keyword.AAAKeywords;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;

import java.util.Map;

import static org.stebz.aaa.keyword.AAAKeywords._assert;
import static org.stebz.aaa.keyword.AAAKeywords.act;
import static org.stebz.aaa.keyword.AAAKeywords.arrange;
import static org.stebz.aaa.keyword.AAAKeywords.setUp;
import static org.stebz.aaa.keyword.AAAKeywords.tearDown;
import static org.stebz.core.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.core.attribute.StepAttribute.KEYWORD;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.core.attribute.StepAttribute.PARAMS;

/**
 * Stebz Arrange-Act-Assert methods.
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
   * Executes given step with {@link AAAKeywords#setUp()} keyword.
   *
   * @param step the step
   */
  public static void SetUp(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(setUp()));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void SetUp(final String name,
                           final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, setUp(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name created by {@code nameGenerator}.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static void SetUp(final ThrowingFunction<? super String, String, ?> nameGenerator,
                           final RunnableStep step) {
    StepExecutor.get().execute(step.with(
      KEYWORD, setUp(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R SetUp(final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, setUp()));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R SetUp(final String name,
                            final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, setUp(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name created by {@code nameGenerator} and returns
   * step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static <R> R SetUp(final ThrowingFunction<? super String, String, ?> nameGenerator,
                            final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(
      KEYWORD, setUp(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void SetUp(final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, setUp()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void SetUp(final String name,
                               final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, setUp(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name created by {@code nameGenerator} on given
   * value.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   */
  public static <T> void SetUp(final ThrowingFunction<? super String, String, ?> nameGenerator,
                               final ConsumerStep<? super T> step,
                               final T value) {
    StepExecutor.get().execute(
      step.with(
        KEYWORD, setUp(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R SetUp(final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, setUp()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R SetUp(final String name,
                               final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, setUp(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#setUp()} keyword and name created by {@code nameGenerator} on given
   * value and returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   * @param <R>           the type of the step result
   * @return step result
   */
  public static <T, R> R SetUp(final ThrowingFunction<? super String, String, ?> nameGenerator,
                               final FunctionStep<? super T, ? extends R> step,
                               final T value) {
    return StepExecutor.get().execute(
      step.with(
        KEYWORD, setUp(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword.
   *
   * @param body the step body
   */
  public static void SetUp(final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp()),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void SetUp(final String name,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void SetUp(final String name,
                           final Map<String, ?> params,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void SetUp(final String name,
                           final String expectedResult,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void SetUp(final String name,
                           final Map<String, ?> params,
                           final String expectedResult,
                           final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R SetUp(final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setUp()),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R SetUp(final String name,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R SetUp(final String name,
                            final Map<String, ?> params,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R SetUp(final String name,
                            final String expectedResult,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R SetUp(final String name,
                            final Map<String, ?> params,
                            final String expectedResult,
                            final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void SetUp(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void SetUp(final String name,
                           final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void SetUp(final String name,
                           final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, setUp(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#setUp()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void SetUp(final String name,
                           final Map<String, ?> params,
                           final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, setUp(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword.
   *
   * @param step the step
   */
  public static void TearDown(final RunnableStep step) {
    StepExecutor.get().execute(step.withKeyword(tearDown()));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name.
   *
   * @param name the name
   * @param step the step
   */
  public static void TearDown(final String name,
                              final RunnableStep step) {
    StepExecutor.get().execute(step.with(KEYWORD, tearDown(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name created by {@code nameGenerator}.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static void TearDown(final ThrowingFunction<? super String, String, ?> nameGenerator,
                              final RunnableStep step) {
    StepExecutor.get().execute(step.with(
      KEYWORD, tearDown(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R TearDown(final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, tearDown()));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R TearDown(final String name,
                               final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, tearDown(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name created by {@code nameGenerator} and
   * returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static <R> R TearDown(final ThrowingFunction<? super String, String, ?> nameGenerator,
                               final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(
      KEYWORD, tearDown(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword on given value.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void TearDown(final ConsumerStep<? super T> step,
                                  final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, tearDown()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name on given value.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   */
  public static <T> void TearDown(final String name,
                                  final ConsumerStep<? super T> step,
                                  final T value) {
    StepExecutor.get().execute(
      step.with(KEYWORD, tearDown(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name created by {@code nameGenerator} on given
   * value.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   */
  public static <T> void TearDown(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                  final ConsumerStep<? super T> step,
                                  final T value) {
    StepExecutor.get().execute(
      step.with(
        KEYWORD, tearDown(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword on given value and returns step result.
   *
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R TearDown(final FunctionStep<? super T, ? extends R> step,
                                  final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, tearDown()),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name on given value and returns step result.
   *
   * @param name  the name
   * @param step  the step
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the step result
   * @return step result
   */
  public static <T, R> R TearDown(final String name,
                                  final FunctionStep<? super T, ? extends R> step,
                                  final T value) {
    return StepExecutor.get().execute(
      step.with(KEYWORD, tearDown(), NAME, name),
      value
    );
  }

  /**
   * Executes given step with {@link AAAKeywords#tearDown()} keyword and name created by {@code nameGenerator} on given
   * value and returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   * @param <R>           the type of the step result
   * @return step result
   */
  public static <T, R> R TearDown(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                  final FunctionStep<? super T, ? extends R> step,
                                  final T value) {
    return StepExecutor.get().execute(
      step.with(
        KEYWORD, tearDown(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword.
   *
   * @param body the step body
   */
  public static void TearDown(final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown()),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name the step name
   * @param body the step body
   */
  public static void TearDown(final String name,
                              final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   */
  @SuppressWarnings("unchecked")
  public static void TearDown(final String name,
                              final Map<String, ?> params,
                              final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  public static void TearDown(final String name,
                              final String expectedResult,
                              final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   */
  @SuppressWarnings("unchecked")
  public static void TearDown(final String name,
                              final Map<String, ?> params,
                              final String expectedResult,
                              final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R TearDown(final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, tearDown()),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R TearDown(final String name,
                               final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R TearDown(final String name,
                               final Map<String, ?> params,
                               final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  public static <R> R TearDown(final String name,
                               final String expectedResult,
                               final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes and returns step result.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return step result
   */
  @SuppressWarnings("unchecked")
  public static <R> R TearDown(final String name,
                               final Map<String, ?> params,
                               final String expectedResult,
                               final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(
        KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
      body
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name the step name
   */
  public static void TearDown(final String name) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name   the step name
   * @param params the step params
   */
  @SuppressWarnings("unchecked")
  public static void TearDown(final String name,
                              final Map<String, ?> params) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   */
  public static void TearDown(final String name,
                              final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, tearDown(), NAME, name, EXPECTED_RESULT, expectedResult),
      RunnableStep.emptyBody()
    ));
  }

  /**
   * Executes step with {@link AAAKeywords#tearDown()} keyword and given attributes.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   */
  @SuppressWarnings("unchecked")
  public static void TearDown(final String name,
                              final Map<String, ?> params,
                              final String expectedResult) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(
        KEYWORD, tearDown(), NAME, name, PARAMS, (Map<String, Object>) params, EXPECTED_RESULT, expectedResult),
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
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name created by {@code nameGenerator}.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static void Arrange(final ThrowingFunction<? super String, String, ?> nameGenerator,
                             final RunnableStep step) {
    StepExecutor.get().execute(step.with(
      KEYWORD, arrange(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Arrange(final SupplierStep<? extends R> step) {
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
                              final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, arrange(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name created by {@code nameGenerator} and
   * returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static <R> R Arrange(final ThrowingFunction<? super String, String, ?> nameGenerator,
                              final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(
      KEYWORD, arrange(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
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
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name created by {@code nameGenerator} on given
   * value.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   */
  public static <T> void Arrange(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                 final ConsumerStep<? super T> step,
                                 final T value) {
    StepExecutor.get().execute(
      step.with(
        KEYWORD, arrange(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
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
   * Executes given step with {@link AAAKeywords#arrange()} keyword and name created by {@code nameGenerator} on given
   * value and returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   * @param <R>           the type of the step result
   * @return step result
   */
  public static <T, R> R Arrange(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                 final FunctionStep<? super T, ? extends R> step,
                                 final T value) {
    return StepExecutor.get().execute(
      step.with(
        KEYWORD, arrange(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#arrange()} keyword.
   *
   * @param body the step body
   */
  public static void Arrange(final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, arrange()),
      body
    ));
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
   * Executes step with {@link AAAKeywords#arrange()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Arrange(final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, arrange()),
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
   * Executes given step with {@link AAAKeywords#act()} keyword and name created by {@code nameGenerator}.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static void Act(final ThrowingFunction<? super String, String, ?> nameGenerator,
                         final RunnableStep step) {
    StepExecutor.get().execute(step.with(
      KEYWORD, act(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Act(final SupplierStep<? extends R> step) {
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
                          final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, act(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#act()} keyword and name created by {@code nameGenerator} and returns
   * step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static <R> R Act(final ThrowingFunction<? super String, String, ?> nameGenerator,
                          final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(
      KEYWORD, act(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
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
   * Executes given step with {@link AAAKeywords#act()} keyword and name created by {@code nameGenerator} on given
   * value.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   */
  public static <T> void Act(final ThrowingFunction<? super String, String, ?> nameGenerator,
                             final ConsumerStep<? super T> step,
                             final T value) {
    StepExecutor.get().execute(
      step.with(
        KEYWORD, act(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
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
   * Executes given step with {@link AAAKeywords#act()} keyword and name created by {@code nameGenerator} on given value
   * and returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   * @param <R>           the type of the step result
   * @return step result
   */
  public static <T, R> R Act(final ThrowingFunction<? super String, String, ?> nameGenerator,
                             final FunctionStep<? super T, ? extends R> step,
                             final T value) {
    return StepExecutor.get().execute(
      step.with(
        KEYWORD, act(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#act()} keyword.
   *
   * @param body the step body
   */
  public static void Act(final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, act()),
      body
    ));
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
   * Executes step with {@link AAAKeywords#act()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Act(final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, act()),
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
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name created by {@code nameGenerator}.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static void Assert(final ThrowingFunction<? super String, String, ?> nameGenerator,
                            final RunnableStep step) {
    StepExecutor.get().execute(step.with(
      KEYWORD, _assert(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  public static <R> R Assert(final SupplierStep<? extends R> step) {
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
                             final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(KEYWORD, _assert(), NAME, name));
  }

  /**
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name created by {@code nameGenerator} and
   * returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   */
  public static <R> R Assert(final ThrowingFunction<? super String, String, ?> nameGenerator,
                             final SupplierStep<? extends R> step) {
    return StepExecutor.get().execute(step.with(
      KEYWORD, _assert(),
      NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
    ));
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
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name created by {@code nameGenerator} on given
   * value.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   */
  public static <T> void Assert(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                final ConsumerStep<? super T> step,
                                final T value) {
    StepExecutor.get().execute(
      step.with(
        KEYWORD, _assert(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
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
   * Executes given step with {@link AAAKeywords#_assert()} keyword and name created by {@code nameGenerator} on given
   * value and returns step result.
   *
   * @param nameGenerator the name generator
   * @param step          the step
   * @param value         the value
   * @param <T>           the type of the value
   * @param <R>           the type of the step result
   * @return step result
   */
  public static <T, R> R Assert(final ThrowingFunction<? super String, String, ?> nameGenerator,
                                final FunctionStep<? super T, ? extends R> step,
                                final T value) {
    return StepExecutor.get().execute(
      step.with(
        KEYWORD, _assert(),
        NAME, ThrowingFunction.unchecked(nameGenerator).apply(step.get(NAME))
      ),
      value
    );
  }

  /**
   * Executes step with {@link AAAKeywords#_assert()} keyword.
   *
   * @param body the step body
   */
  public static void Assert(final ThrowingRunnable<?> body) {
    StepExecutor.get().execute(new RunnableStep.Of(
      new StepAttributes.Of(KEYWORD, _assert()),
      body
    ));
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
   * Executes step with {@link AAAKeywords#_assert()} keyword and returns step result.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  public static <R> R Assert(final ThrowingSupplier<? extends R, ?> body) {
    return StepExecutor.get().execute(new SupplierStep.Of<>(
      new StepAttributes.Of(KEYWORD, _assert()),
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
}
