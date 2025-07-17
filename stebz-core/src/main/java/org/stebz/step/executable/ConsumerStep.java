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

import org.stebz.attribute.StepAttribute;
import org.stebz.attribute.StepAttributes;
import org.stebz.exception.StepNotImplementedError;
import org.stebz.step.ExecutableStep;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Consumer step.
 *
 * @param <T> the type of the step input value
 */
public interface ConsumerStep<T> extends ExecutableStep<ThrowingConsumer<T, ?>, ConsumerStep<T>> {

  /**
   * Returns {@code ConsumerStep} with given action before body.
   *
   * @param before the action
   * @return {@code ConsumerStep} with given action before body
   * @throws NullPointerException if {@code before} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  ConsumerStep<T> withBefore(ThrowingConsumer<? super T, ?> before);

  /**
   * Returns {@code ConsumerStep} with given action after step body.
   *
   * @param after the action
   * @return {@code ConsumerStep} with given action after step body
   * @throws NullPointerException if {@code after} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  ConsumerStep<T> withAfter(ThrowingConsumer<? super T, ?> after);

  /**
   * Returns {@code ConsumerStep} with given body.
   *
   * @param body the step body
   * @param <T>  the type of the step input value
   * @return {@code ConsumerStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static <T> ConsumerStep<T> of(final ThrowingConsumer<T, ?> body) {
    return new Of<>(body);
  }

  /**
   * Returns {@code ConsumerStep} with given name and body.
   *
   * @param name the step name
   * @param body the step body
   * @param <T>  the type of the step input value
   * @return {@code ConsumerStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code body} arg is null
   */
  static <T> ConsumerStep<T> of(final String name,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(name, body);
  }

  /**
   * Returns {@code ConsumerStep} with given name, params and body.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <T>    the type of the step input value
   * @return {@code ConsumerStep} with given name, params and body
   * @throws NullPointerException if {@code name} arg or {@code params} arg or {@code body} arg is null
   */
  static <T> ConsumerStep<T> of(final String name,
                                final Map<String, ?> params,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(name, params, body);
  }

  /**
   * Returns {@code ConsumerStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @param <T>    the type of the step input value
   * @return {@code ConsumerStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static <T> ConsumerStep<T> of(final ConsumerStep<T> origin) {
    return new Of<>(origin);
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
  static <T> ConsumerStep<T> of(final StepAttributes attributes,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(attributes, body);
  }

  /**
   * Returns {@code ThrowingConsumer} that does nothing.
   *
   * @param <T> the type of the step input value
   * @return {@code ThrowingConsumer} that does nothing
   * @see #empty()
   */
  @SuppressWarnings("unchecked")
  static <T> ThrowingConsumer<T, Error> emptyBody() {
    return (ThrowingConsumer<T, Error>) Of.EMPTY_BODY;
  }

  /**
   * Returns {@code ThrowingConsumer} that throws an {@code StepNotImplementedError}.
   *
   * @param <T> the type of the step input value
   * @return {@code ThrowingConsumer} that throws an {@code StepNotImplementedError}
   * @see #notImplemented()
   */
  static <T> ThrowingConsumer<T, Error> notImplementedBody() {
    return v -> { throw new StepNotImplementedError("ConsumerStep not implemented"); };
  }

  /**
   * Returns {@code ConsumerStep} with empty body.
   *
   * @param <T> the type of the step input value
   * @return {@code ConsumerStep} with empty body
   * @see #emptyBody()
   */
  static <T> ConsumerStep<T> empty() {
    return ConsumerStep.of(emptyBody());
  }

  /**
   * Returns {@code ConsumerStep} with not implemented body.
   *
   * @param <T> the type of the step input value
   * @return {@code ConsumerStep} with not implemented body
   * @see #notImplementedBody()
   */
  static <T> ConsumerStep<T> notImplemented() {
    return ConsumerStep.of(notImplementedBody());
  }

  /**
   * Default {@code ConsumerStep} implementation.
   *
   * @param <T> the type of the step input value
   */
  class Of<T> implements ConsumerStep<T> {
    private static final ThrowingConsumer<?, Error> EMPTY_BODY = v -> { };
    private final StepAttributes attributes;
    private final ThrowingConsumer<T, ?> body;

    /**
     * Ctor.
     *
     * @param body the step body
     * @throws NullPointerException if {@code body} arg is null
     */
    public Of(final ThrowingConsumer<T, ?> body) {
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
              final ThrowingConsumer<T, ?> body) {
      this(new StepAttributes.Of(
        NAME, name
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
              final ThrowingConsumer<T, ?> body) {
      this(new StepAttributes.Of(
        NAME, name,
        PARAMS, (Map<String, Object>) params
      ), body);
    }

    /**
     * Ctor.
     *
     * @param origin the origin step
     * @throws NullPointerException if {@code origin} arg is null
     */
    public Of(final ConsumerStep<T> origin) {
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
              final ThrowingConsumer<T, ?> body) {
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
    public ConsumerStep<T> withAttributes(final StepAttributes attributes) {
      return new Of<>(attributes, this.body);
    }

    @Override
    public ConsumerStep<T> without(final StepAttribute<?> attribute) {
      return new Of<>(this.attributes.without(attribute), this.body);
    }

    @Override
    public ThrowingConsumer<T, ?> body() {
      return this.body;
    }

    @Override
    public ConsumerStep<T> withBody(final ThrowingConsumer<T, ?> body) {
      return new Of<>(this.attributes, body);
    }

    @Override
    public ConsumerStep<T> withBefore(final ThrowingConsumer<? super T, ?> before) {
      if (before == null) { throw new NullPointerException("before arg is null"); }
      final ThrowingConsumer<T, ?> after = this.body;
      return new Of<>(this.attributes, context -> {
        before.accept(context);
        after.accept(context);
      });
    }

    @Override
    public ConsumerStep<T> withAfter(final ThrowingConsumer<? super T, ?> after) {
      if (after == null) { throw new NullPointerException("after arg is null"); }
      final ThrowingConsumer<T, ?> before = this.body;
      return new Of<>(this.attributes, context -> {
        before.accept(context);
        after.accept(context);
      });
    }

    @Override
    public String toString() {
      return "ConsumerStep[" + this.getName() + "]";
    }
  }
}
