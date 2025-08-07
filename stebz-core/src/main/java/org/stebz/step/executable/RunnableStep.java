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
package org.stebz.step.executable;

import org.stebz.attribute.StepAttributes;
import org.stebz.exception.StepNotImplementedError;
import org.stebz.step.ExecutableStep;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingRunnable;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Runnable step.
 */
public interface RunnableStep extends ExecutableStep<ThrowingRunnable<?>, RunnableStep> {

  /**
   * Returns {@code RunnableStep} with given action before body.
   *
   * @param before the action
   * @return {@code RunnableStep} with given action before body
   * @throws NullPointerException if {@code before} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default RunnableStep withBefore(final ThrowingRunnable<?> before) {
    if (before == null) { throw new NullPointerException("before arg is null"); }
    final ThrowingRunnable<?> after = this.body();
    return this.withBody(() -> {
      before.run();
      after.run();
    });
  }

  /**
   * Returns {@code RunnableStep} with given action after step body.
   *
   * @param after the action
   * @return {@code RunnableStep} with given action after step body
   * @throws NullPointerException if {@code after} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default RunnableStep withAfter(final ThrowingRunnable<?> after) {
    if (after == null) { throw new NullPointerException("after arg is null"); }
    final ThrowingRunnable<?> before = this.body();
    return this.withBody(() -> {
      before.run();
      after.run();
    });
  }

  /**
   * Returns {@code RunnableStep} with given body.
   *
   * @param body the step body
   * @return {@code RunnableStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static RunnableStep of(final ThrowingRunnable<?> body) {
    return new Of(body);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code RunnableStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static RunnableStep of(final String name,
                         final ThrowingRunnable<?> body) {
    return new Of(name, body);
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
  static RunnableStep of(final String name,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    return new Of(name, expectedResult, body);
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
  static RunnableStep of(final String name,
                         final Map<String, ?> params,
                         final ThrowingRunnable<?> body) {
    return new Of(name, params, body);
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
  static RunnableStep of(final String name,
                         final Map<String, ?> params,
                         final String expectedResult,
                         final ThrowingRunnable<?> body) {
    return new Of(name, params, expectedResult, body);
  }

  /**
   * Returns {@code RunnableStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @return {@code RunnableStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static RunnableStep of(final RunnableStep origin) {
    return new Of(origin);
  }

  /**
   * Returns {@code RunnableStep} with given attributes and body.
   *
   * @param attributes the step attributes
   * @param body       the step body
   * @return {@code RunnableStep} with given attributes and body
   * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
   */
  static RunnableStep of(final StepAttributes attributes,
                         final ThrowingRunnable<?> body) {
    return new Of(attributes, body);
  }

  /**
   * Returns {@code ThrowingRunnable} that does nothing.
   *
   * @return {@code ThrowingRunnable} that does nothing
   * @see #empty()
   */
  static ThrowingRunnable<Error> emptyBody() {
    return Of.EMPTY_BODY;
  }

  /**
   * Returns {@code ThrowingRunnable} that throws an {@code StepNotImplementedError}.
   *
   * @return {@code ThrowingRunnable} that throws an {@code StepNotImplementedError}
   * @see #notImplemented()
   */
  static ThrowingRunnable<Error> notImplementedBody() {
    return () -> { throw new StepNotImplementedError("RunnableStep not implemented"); };
  }

  /**
   * Returns {@code RunnableStep} with empty body.
   *
   * @return {@code RunnableStep} with empty body
   * @see #emptyBody()
   */
  static RunnableStep empty() {
    return RunnableStep.of(emptyBody());
  }

  /**
   * Returns {@code RunnableStep} with not implemented body.
   *
   * @return {@code RunnableStep} with not implemented body
   * @see #notImplementedBody()
   */
  static RunnableStep notImplemented() {
    return RunnableStep.of(notImplementedBody());
  }

  /**
   * Default {@code RunnableStep} implementation.
   */
  class Of implements RunnableStep {
    private static final ThrowingRunnable<Error> EMPTY_BODY = () -> { };
    private final StepAttributes attributes;
    private final ThrowingRunnable<?> body;

    /**
     * Ctor.
     *
     * @param body the step body
     * @throws NullPointerException if {@code body} arg is null
     */
    public Of(final ThrowingRunnable<?> body) {
      this(StepAttributes.empty(), body);
    }

    /**
     * Ctor.
     *
     * @param name the step name
     * @param body the step body
     * @throws NullPointerException if {@code name} arg or {@code body} arg is null
     */
    public Of(final String name,
              final ThrowingRunnable<?> body) {
      this(new StepAttributes.Of(
        NAME, name
      ), body);
    }

    /**
     * Ctor.
     *
     * @param name           the step name
     * @param expectedResult the step expected result
     * @param body           the step body
     * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
     */
    public Of(final String name,
              final String expectedResult,
              final ThrowingRunnable<?> body) {
      this(new StepAttributes.Of(
        NAME, name,
        EXPECTED_RESULT, expectedResult
      ), body);
    }

    /**
     * Ctor.
     *
     * @param name   the step name
     * @param params the step params
     * @param body   the step body
     * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
     */
    @SuppressWarnings("unchecked")
    public Of(final String name,
              final Map<String, ?> params,
              final ThrowingRunnable<?> body) {
      this(new StepAttributes.Of(
        NAME, name,
        PARAMS, (Map<String, Object>) params
      ), body);
    }

    /**
     * Ctor.
     *
     * @param name           the step name
     * @param params         the step params
     * @param expectedResult the step expected result
     * @param body           the step body
     * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code expectedResult} arg or
     *                              {@code body} arg is null
     */
    @SuppressWarnings("unchecked")
    public Of(final String name,
              final Map<String, ?> params,
              final String expectedResult,
              final ThrowingRunnable<?> body) {
      this(new StepAttributes.Of(
        NAME, name,
        PARAMS, (Map<String, Object>) params,
        EXPECTED_RESULT, expectedResult
      ), body);
    }

    /**
     * Ctor.
     *
     * @param origin the origin step
     * @throws NullPointerException if {@code origin} arg is null
     */
    public Of(final RunnableStep origin) {
      this(origin.attributes(), origin.body());
    }

    /**
     * Ctor.
     *
     * @param attributes the step attributes
     * @param body       the step body
     * @throws NullPointerException if {@code attributes} arg or {@code body} arg is null
     */
    public Of(final StepAttributes attributes,
              final ThrowingRunnable<?> body) {
      if (attributes == null) { throw new NullPointerException("attributes arg is null"); }
      if (body == null) { throw new NullPointerException("body arg is null"); }
      this.attributes = attributes;
      this.body = body;
    }

    @Override
    public StepAttributes attributes() {
      return this.attributes;
    }

    @Override
    public RunnableStep withAttributes(final StepAttributes attributes) {
      return new Of(attributes, this.body);
    }

    @Override
    public ThrowingRunnable<?> body() {
      return this.body;
    }

    @Override
    public RunnableStep withBody(final ThrowingRunnable<?> body) {
      return new Of(this.attributes, body);
    }

    @Override
    public String toString() {
      return "RunnableStep[" + this.getName() + "]";
    }
  }
}
