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
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Function step.
 *
 * @param <T> the type of the step input value
 * @param <R> the type of the step result
 */
public interface FunctionStep<T, R> extends ExecutableStep<ThrowingFunction<T, R, ?>, FunctionStep<T, R>> {

  /**
   * Returns {@code FunctionStep} with given action before body.
   *
   * @param before the action
   * @return {@code FunctionStep} with given action before body
   * @throws NullPointerException if {@code before} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default FunctionStep<T, R> withBefore(final ThrowingConsumer<? super T, ?> before) {
    if (before == null) { throw new NullPointerException("before arg is null"); }
    final ThrowingFunction<? super T, ? extends R, ?> after = this.body();
    return this.withBody(context -> {
      before.accept(context);
      return after.apply(context);
    });
  }

  /**
   * Returns {@code FunctionStep} with given action after step body.
   *
   * @param after the action
   * @return {@code FunctionStep} with given action after step body
   * @throws NullPointerException if {@code after} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default FunctionStep<T, R> withAfter(final ThrowingConsumer<? super R, ?> after) {
    if (after == null) { throw new NullPointerException("after arg is null"); }
    final ThrowingFunction<? super T, ? extends R, ?> before = this.body();
    return this.withBody(context -> {
      final R result = before.apply(context);
      after.accept(result);
      return result;
    });
  }

  /**
   * Returns {@code FunctionStep} with given action after step body.
   *
   * @param after the action
   * @return {@code FunctionStep} with given action after step body
   * @throws NullPointerException if {@code after} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default FunctionStep<T, R> withAfter(final ThrowingFunction<? super R, ? extends R, ?> after) {
    if (after == null) { throw new NullPointerException("after arg is null"); }
    final ThrowingFunction<? super T, ? extends R, ?> before = this.body();
    return this.withBody(context ->
      after.apply(before.apply(context))
    );
  }

  /**
   * Returns this step as {@code ConsumerStep}.
   *
   * @return this step as {@code ConsumerStep}
   */
  default ConsumerStep<T> noResult() {
    return new ConsumerStep.Of<>(this.attributes(), this.body()::apply);
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
  static <T, R> FunctionStep<T, R> of(final ThrowingFunction<T, R, ?> body) {
    return new Of<>(body);
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
  static <T, R> FunctionStep<T, R> of(final String name,
                                      final ThrowingFunction<T, R, ?> body) {
    return new Of<>(name, body);
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
  static <T, R> FunctionStep<T, R> of(final String name,
                                      final String expectedResult,
                                      final ThrowingFunction<T, R, ?> body) {
    return new Of<>(name, expectedResult, body);
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
  static <T, R> FunctionStep<T, R> of(final String name,
                                      final Map<String, ?> params,
                                      final ThrowingFunction<T, R, ?> body) {
    return new Of<>(name, params, body);
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
  static <T, R> FunctionStep<T, R> of(final String name,
                                      final Map<String, ?> params,
                                      final String expectedResult,
                                      final ThrowingFunction<T, R, ?> body) {
    return new Of<>(name, params, expectedResult, body);
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
  static <T, R> FunctionStep<T, R> of(final FunctionStep<T, R> origin) {
    return new Of<>(origin);
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
  static <T, R> FunctionStep<T, R> of(final StepAttributes attributes,
                                      final ThrowingFunction<T, R, ?> body) {
    return new Of<>(attributes, body);
  }

  /**
   * Returns {@code ThrowingFunction} that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the step input value
   * @param <R> the type of the step result
   * @return {@code ThrowingFunction} that throws an {@code StepNotImplementedError}
   * @see #notImplemented()
   */
  static <T, R> ThrowingFunction<T, R, Error> notImplementedBody() {
    return v -> { throw new StepNotImplementedError("FunctionStep not implemented"); };
  }

  /**
   * Returns {@code FunctionStep} with not implemented body.
   *
   * @param <T> the type of the step input value
   * @param <R> the type of the step result
   * @return {@code FunctionStep} with not implemented body
   * @see #notImplementedBody()
   */
  static <T, R> FunctionStep<T, R> notImplemented() {
    return FunctionStep.of(notImplementedBody());
  }

  /**
   * Default {@code FunctionStep} implementation.
   *
   * @param <T> the type of the step input value
   * @param <R> the type of the step result
   */
  class Of<T, R> implements FunctionStep<T, R> {
    private final StepAttributes attributes;
    private final ThrowingFunction<T, R, ?> body;

    /**
     * Ctor.
     *
     * @param body the step body
     * @throws NullPointerException if {@code body} arg is null
     */
    public Of(final ThrowingFunction<T, R, ?> body) {
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
              final ThrowingFunction<T, R, ?> body) {
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
              final ThrowingFunction<T, R, ?> body) {
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
              final ThrowingFunction<T, R, ?> body) {
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
              final ThrowingFunction<T, R, ?> body) {
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
    public Of(final FunctionStep<T, R> origin) {
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
              final ThrowingFunction<T, R, ?> body) {
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
    public FunctionStep<T, R> withAttributes(final StepAttributes attributes) {
      return new Of<>(attributes, this.body);
    }

    @Override
    public ThrowingFunction<T, R, ?> body() {
      return this.body;
    }

    @Override
    public FunctionStep<T, R> withBody(final ThrowingFunction<T, R, ?> body) {
      return new Of<>(this.attributes, body);
    }

    @Override
    public String toString() {
      return "FunctionStep[" + this.getName() + "]";
    }
  }
}
