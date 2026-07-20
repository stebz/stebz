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

import dev.jlet.function.ThrowingConsumer;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.step.executable.ConsumerStep;

import java.util.Map;

/**
 * Alias for {@link ConsumerStep}.
 *
 * @param <T> the type of the step input value
 */
public interface CStep<T> extends ConsumerStep<T> {

  /**
   * Returns {@code ConsumerStep} with empty body.
   *
   * @param <T> the type of the step input value
   * @return {@code ConsumerStep} with empty body
   */
  static <T> CStep<T> empty() {
    return ConsumerStep.empty();
  }

  /**
   * Returns {@code ConsumerStep} with body that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the step input value
   * @return {@code ConsumerStep} with body that throws an {@code StepNotImplementedError}
   */
  static <T> CStep<T> notImplemented() {
    return ConsumerStep.notImplemented();
  }

  /**
   * Returns {@code ThrowingConsumer} that does nothing.
   *
   * @param <T> the type of the input value
   * @return {@code ThrowingConsumer} that does nothing
   */
  static <T> ThrowingConsumer<T, Error> emptyBody() {
    return ConsumerStep.emptyBody();
  }

  /**
   * Returns {@code ThrowingConsumer} that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the input value
   * @return {@code ThrowingConsumer} that throws an {@code StepNotImplementedError}
   */
  static <T> ThrowingConsumer<T, Error> notImplementedBody() {
    return ConsumerStep.notImplementedBody();
  }

  /**
   * Returns {@code ConsumerStep} with given body.
   *
   * @param body the step body
   * @param <T>  the type of the step input value
   * @return {@code ConsumerStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static <T> CStep<T> of(final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(body);
  }

  /**
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param name the step name
   * @param body the step body
   * @param <T>  the type of the step input value
   * @return {@code ConsumerStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static <T> CStep<T> of(final String name,
                         final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(name, body);
  }

  /**
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <T>            the type of the step input value
   * @return {@code ConsumerStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
   */
  static <T> CStep<T> of(final String name,
                         final String expectedResult,
                         final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(name, expectedResult, body);
  }

  /**
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <T>    the type of the step input value
   * @return {@code ConsumerStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
   */
  static <T> CStep<T> of(final String name,
                         final Map<String, ?> params,
                         final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(name, params, body);
  }

  /**
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <T>            the type of the step input value
   * @return {@code ConsumerStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code expectedResult} arg or
   *                              {@code body} arg is null
   */
  static <T> CStep<T> of(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(name, params, expectedResult, body);
  }

  /**
   * Returns {@code ConsumerStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @param <T>    the type of the step input value
   * @return {@code ConsumerStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static <T> CStep<T> of(final ConsumerStep<T> origin) {
    return new ConsumerStep.Of<>(origin);
  }

  /**
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param attributes the step attributes
   * @param body       the step body
   * @param <T>        the type of the step input value
   * @return {@code ConsumerStep} with given attributes and body
   * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
   */
  static <T> CStep<T> of(final StepAttributes attributes,
                         final ThrowingConsumer<T, ?> body) {
    return new ConsumerStep.Of<>(attributes, body);
  }
}
