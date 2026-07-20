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
package org.stebz.core.step.executable.alias;

import dev.jlet.function.ThrowingFunction;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.step.executable.FunctionStep;

import java.util.Map;

/**
 * Alias for {@link FunctionStep}.
 *
 * @param <T> the type of the step input value
 * @param <R> the type of the step result
 */
public interface FStep<T, R> extends FunctionStep<T, R> {

  /**
   * Returns {@code FunctionStep} with empty body that returns {@code null} result.
   *
   * @param <T> the type of the step input value
   * @param <R> the type of the step result
   * @return {@code FunctionStep} with empty body that returns {@code null} result
   */
  static <T, R> FStep<T, R> empty() {
    return FunctionStep.empty();
  }

  /**
   * Returns {@code FunctionStep} with empty body that returns given result.
   *
   * @param result the step result
   * @param <T>    the type of the step input value
   * @param <R>    the type of the step result
   * @return {@code FunctionStep} with empty body that returns given result
   */
  static <T, R> FStep<T, R> emptyReturning(final R result) {
    return FunctionStep.emptyReturning(result);
  }

  /**
   * Returns {@code FunctionStep} with body that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the step input value
   * @param <R> the type of the step result
   * @return {@code FunctionStep} with body that throws an {@code StepNotImplementedError}
   */
  static <T, R> FStep<T, R> notImplemented() {
    return FunctionStep.notImplemented();
  }

  /**
   * Returns {@code ThrowingFunction} that returns {@code null} result.
   *
   * @param <T> the type of the input value
   * @param <R> the type of the result
   * @return {@code ThrowingFunction} that returns {@code null} result
   */
  static <T, R> ThrowingFunction<T, R, Error> emptyBody() {
    return FunctionStep.emptyBody();
  }

  /**
   * Returns {@code ThrowingFunction} that returns given result.
   *
   * @param result the step result
   * @param <T>    the type of the input value
   * @param <R>    the type of the result
   * @return {@code ThrowingFunction} that returns given result
   */
  static <T, R> ThrowingFunction<T, R, Error> emptyBodyReturning(final R result) {
    return FunctionStep.emptyBodyReturning(result);
  }

  /**
   * Returns {@code ThrowingFunction} that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the input value
   * @param <R> the type of the result
   * @return {@code ThrowingFunction} that throws an {@code StepNotImplementedError}
   */
  static <T, R> ThrowingFunction<T, R, Error> notImplementedBody() {
    return FunctionStep.notImplementedBody();
  }

  /**
   * Returns {@code FunctionStep} with given body.
   *
   * @param body the step body
   * @param <T>  the type of the step input value
   * @param <R>  the type of the step result
   * @return {@code FunctionStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(body);
  }

  /**
   * Returns {@code FunctionStep} with given attributes and body.
   *
   * @param name the step name
   * @param body the step body
   * @param <T>  the type of the step input value
   * @param <R>  the type of the step result
   * @return {@code FunctionStep} with given attributes and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final String name,
                               final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(name, body);
  }

  /**
   * Returns {@code FunctionStep} with given attributes and body.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <T>            the type of the step input value
   * @param <R>            the type of the step result
   * @return {@code FunctionStep} with given attributes and body
   * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final String name,
                               final String expectedResult,
                               final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(name, expectedResult, body);
  }

  /**
   * Returns {@code FunctionStep} with given attributes and body.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <T>    the type of the step input value
   * @param <R>    the type of the step result
   * @return {@code FunctionStep} with given attributes and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final String name,
                               final Map<String, ?> params,
                               final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(name, params, body);
  }

  /**
   * Returns {@code FunctionStep} with given attributes and body.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <T>            the type of the step input value
   * @param <R>            the type of the step result
   * @return {@code FunctionStep} with given attributes and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code expectedResult} arg or
   *                              {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final String name,
                               final Map<String, ?> params,
                               final String expectedResult,
                               final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(name, params, expectedResult, body);
  }

  /**
   * Returns {@code FunctionStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @param <T>    the type of the step input value
   * @param <R>    the type of the step result
   * @return {@code FunctionStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static <T, R> FStep<T, R> of(final FunctionStep<T, R> origin) {
    return new FunctionStep.Of<>(origin);
  }

  /**
   * Returns {@code FunctionStep} with given attributes and body.
   *
   * @param attributes the step attributes
   * @param body       the step body
   * @param <T>        the type of the step input value
   * @param <R>        the type of the step result
   * @return {@code FunctionStep} with given attributes and body
   * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
   */
  static <T, R> FStep<T, R> of(final StepAttributes attributes,
                               final ThrowingFunction<T, R, ?> body) {
    return new FunctionStep.Of<>(attributes, body);
  }
}
