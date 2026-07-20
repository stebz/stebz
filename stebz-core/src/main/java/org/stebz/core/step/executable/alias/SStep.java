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

import dev.jlet.function.ThrowingSupplier;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.step.executable.SupplierStep;

import java.util.Map;

/**
 * Alias for {@link SupplierStep}.
 *
 * @param <R> the type of the step result
 */
public interface SStep<R> extends SupplierStep<R> {

  /**
   * Returns {@code SupplierStep} with empty body that returns {@code null} result.
   *
   * @param <R> the type of the step result
   * @return {@code SupplierStep} with empty body that returns {@code null} result
   */
  static <R> SStep<R> empty() {
    return SupplierStep.empty();
  }

  /**
   * Returns {@code SupplierStep} with empty body that returns given result.
   *
   * @param result the step result
   * @param <R>    the type of the step result
   * @return {@code SupplierStep} with empty body that returns given result
   */
  static <R> SStep<R> emptyReturning(final R result) {
    return SupplierStep.emptyReturning(result);
  }

  /**
   * Returns {@code SupplierStep} with body that throws an {@code StepNotImplementedError}.
   *
   * @param <R> the type of the step result
   * @return {@code SupplierStep} with body that throws an {@code StepNotImplementedError}
   */
  static <R> SStep<R> notImplemented() {
    return SupplierStep.notImplemented();
  }

  /**
   * Returns {@code ThrowingSupplier} that returns {@code null} result.
   *
   * @param <R> the type of the result
   * @return {@code ThrowingSupplier} that returns {@code null} result
   */
  static <R> ThrowingSupplier<R, Error> emptyBody() {
    return SupplierStep.emptyBody();
  }

  /**
   * Returns {@code ThrowingSupplier} that returns given result.
   *
   * @param result the step result
   * @param <R>    the type of the result
   * @return {@code ThrowingSupplier} that returns given result
   */
  static <R> ThrowingSupplier<R, Error> emptyBodyReturning(final R result) {
    return SupplierStep.emptyBodyReturning(result);
  }

  /**
   * Returns {@code ThrowingSupplier} that throws an {@code StepNotImplementedError}.
   *
   * @param <R> the type of the result
   * @return {@code ThrowingSupplier} that throws an {@code StepNotImplementedError}
   */
  static <R> ThrowingSupplier<R, Error> notImplementedBody() {
    return SupplierStep.notImplementedBody();
  }

  /**
   * Returns {@code SupplierStep} with given body.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return {@code SupplierStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static <R> SStep<R> of(final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(body);
  }

  /**
   * Returns {@code SupplierStep} with given attributes and body.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return {@code SupplierStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static <R> SStep<R> of(final String name,
                         final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(name, body);
  }

  /**
   * Returns {@code SupplierStep} with given attributes and body.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return {@code SupplierStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
   */
  static <R> SStep<R> of(final String name,
                         final String expectedResult,
                         final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(name, expectedResult, body);
  }

  /**
   * Returns {@code SupplierStep} with given attributes and body.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <R>            the type of the step result
   * @return {@code SupplierStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code expectedResult} arg or
   *                              {@code body} arg is null
   */
  static <R> SStep<R> of(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(name, params, expectedResult, body);
  }

  /**
   * Returns {@code SupplierStep} with given attributes and body.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return {@code SupplierStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
   */
  static <R> SStep<R> of(final String name,
                         final Map<String, ?> params,
                         final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(name, params, body);
  }

  /**
   * Returns {@code SupplierStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @param <R>    the type of the step result
   * @return {@code SupplierStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static <R> SStep<R> of(final SupplierStep<R> origin) {
    return new SupplierStep.Of<>(origin);
  }

  /**
   * Returns {@code SupplierStep} with given attributes and body.
   *
   * @param attributes the step attributes
   * @param body       the step body
   * @param <R>        the type of the step result
   * @return {@code SupplierStep} with given attributes and body
   * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
   */
  static <R> SStep<R> of(final StepAttributes attributes,
                         final ThrowingSupplier<R, ?> body) {
    return new SupplierStep.Of<>(attributes, body);
  }
}
