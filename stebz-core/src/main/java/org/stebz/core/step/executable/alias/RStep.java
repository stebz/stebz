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

import dev.jlet.function.ThrowingRunnable;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.step.executable.RunnableStep;

import java.util.Map;

/**
 * Alias for {@link RunnableStep}.
 */
public interface RStep extends RunnableStep {

  /**
   * Returns {@code RunnableStep} with empty body.
   *
   * @return {@code RunnableStep} with empty body
   */
  static RStep empty() {
    return RunnableStep.empty();
  }

  /**
   * Returns {@code RunnableStep} with body that throws an {@code StepNotImplementedError}.
   *
   * @return {@code RunnableStep} with body that throws an {@code StepNotImplementedError}
   */
  static RStep notImplemented() {
    return RunnableStep.notImplemented();
  }

  /**
   * Returns {@code ThrowingRunnable} that does nothing.
   *
   * @return {@code ThrowingRunnable} that does nothing
   */
  static ThrowingRunnable<Error> emptyBody() {
    return RunnableStep.emptyBody();
  }

  /**
   * Returns {@code ThrowingRunnable} that throws an {@code StepNotImplementedError}.
   *
   * @return {@code ThrowingRunnable} that throws an {@code StepNotImplementedError}
   */
  static ThrowingRunnable<Error> notImplementedBody() {
    return RunnableStep.notImplementedBody();
  }

  /**
   * Returns {@code RunnableStep} with given body.
   *
   * @param body the step body
   * @return {@code RunnableStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static RStep of(final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(body);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code RunnableStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static RStep of(final String name,
                  final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(name, body);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code RunnableStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
   */
  static RStep of(final String name,
                  final String expectedResult,
                  final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(name, expectedResult, body);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code RunnableStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
   */
  static RStep of(final String name,
                  final Map<String, ?> params,
                  final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(name, params, body);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param name           the step name
   * @param params         the step params
   * @param expectedResult the step expected result
   * @param body           the step body
   * @return {@code RunnableStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code expectedResult} arg or
   *                              {@code body} arg is null
   */
  static RStep of(final String name,
                  final Map<String, ?> params,
                  final String expectedResult,
                  final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(name, params, expectedResult, body);
  }

  /**
   * Returns {@code RunnableStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @return {@code RunnableStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static RStep of(final RunnableStep origin) {
    return new RunnableStep.Of(origin);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param attributes the step attributes
   * @param body       the step body
   * @return {@code RunnableStep} with given attributes and body
   * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
   */
  static RStep of(final StepAttributes attributes,
                  final ThrowingRunnable<?> body) {
    return new RunnableStep.Of(attributes, body);
  }
}
