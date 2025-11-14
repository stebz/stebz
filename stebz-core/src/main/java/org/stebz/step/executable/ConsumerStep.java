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

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import org.stebz.attribute.StepAttributes;
import org.stebz.exception.StepNotImplementedError;
import org.stebz.step.ExecutableStep;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Consumer step.
 *
 * @param <T> the type of the step input value
 */
public interface ConsumerStep<T> extends ExecutableStep<ThrowingConsumer<T, ?>, ConsumerStep<T>> {

  /**
   * Returns {@code ConsumerStep} with given block before body.
   *
   * @param block the before block
   * @return {@code ConsumerStep} with given block before body
   * @throws NullPointerException if {@code block} arg is null
   * @see #withBody(Object)
   * @see #withBodyOf(ThrowingFunction)
   */
  default ConsumerStep<T> withBefore(final ThrowingConsumer<? super T, ?> block) {
    if (block == null) { throw new NullPointerException("block arg is null"); }
    final ThrowingConsumer<T, ?> body = this.body();
    return this.withBody(context -> {
      block.accept(context);
      body.accept(context);
    });
  }

  /**
   * Returns {@code ConsumerStep} with given block after step body.
   *
   * @param block the after block
   * @return {@code ConsumerStep} with given block after step body
   * @throws NullPointerException if {@code block} arg is null
   * @see #withBody(Object)
   * @see #withBodyOf(ThrowingFunction)
   */
  default ConsumerStep<T> withAfter(final ThrowingConsumer<? super T, ?> block) {
    if (block == null) { throw new NullPointerException("block arg is null"); }
    final ThrowingConsumer<T, ?> body = this.body();
    return this.withBody(context -> {
      body.accept(context);
      block.accept(context);
    });
  }

  /**
   * Returns {@code ConsumerStep} with given block after step body. Alias for {@link #withAfter(ThrowingConsumer)}
   * methods.
   *
   * @param block the after block
   * @return {@code ConsumerStep} with given block after step body
   * @throws NullPointerException if {@code block} arg is null
   * @see #withBody(Object)
   * @see #withBodyOf(ThrowingFunction)
   */
  default ConsumerStep<T> withOnSuccess(final ThrowingConsumer<? super T, ?> block) {
    return this.withAfter(block);
  }

  /**
   * Returns {@code ConsumerStep} with given block that be executed after step body in case of step failure.
   *
   * @param block the on failure block
   * @return {@code ConsumerStep} with given block that be executed after step body in case of step failure
   * @throws NullPointerException if {@code block} arg is null
   * @see #withBody(Object)
   * @see #withBodyOf(ThrowingFunction)
   */
  default ConsumerStep<T> withOnFailure(final ThrowingConsumer<? super T, ?> block) {
    if (block == null) { throw new NullPointerException("block arg is null"); }
    final ThrowingConsumer<T, ?> body = this.body();
    return this.withBody(context -> {
      try {
        body.accept(context);
      } catch (final Throwable stepEx) {
        try {
          block.accept(context);
        } catch (final Throwable afterEx) {
          afterEx.addSuppressed(stepEx);
          throw afterEx;
        }
        throw stepEx;
      }
    });
  }

  /**
   * Returns {@code ConsumerStep} with given finally block after step body.
   *
   * @param block the finally block
   * @return {@code ConsumerStep} with given finally block after step body
   * @throws NullPointerException if {@code block} arg is null
   * @see #withBody(Object)
   * @see #withBodyOf(ThrowingFunction)
   */
  default ConsumerStep<T> withFinally(final ThrowingConsumer<? super T, ?> block) {
    if (block == null) { throw new NullPointerException("block arg is null"); }
    final ThrowingConsumer<T, ?> body = this.body();
    return this.withBody(context -> {
      Throwable mainEx = null;
      try {
        body.accept(context);
      } catch (final Throwable ex) {
        mainEx = ex;
      }
      try {
        body.accept(context);
      } catch (final Throwable ex) {
        if (mainEx != null) {
          ex.addSuppressed(mainEx);
        }
        mainEx = ex;
      }
      if (mainEx != null) {
        throw mainEx;
      }
    });
  }

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
   * Returns {@code ConsumerStep} with given attributes and body.
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
   * Returns {@code ConsumerStep} with given attributes and body.
   *
   * @param name           the step name
   * @param expectedResult the step expected result
   * @param body           the step body
   * @param <T>            the type of the step input value
   * @return {@code ConsumerStep} with given name and body
   * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
   */
  static <T> ConsumerStep<T> of(final String name,
                                final String expectedResult,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(name, expectedResult, body);
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
  static <T> ConsumerStep<T> of(final String name,
                                final Map<String, ?> params,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(name, params, body);
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
  static <T> ConsumerStep<T> of(final String name,
                                final Map<String, ?> params,
                                final String expectedResult,
                                final ThrowingConsumer<T, ?> body) {
    return new Of<>(name, params, expectedResult, body);
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
     * @param name           the step name
     * @param expectedResult the step expected result
     * @param body           the step body
     * @throws NullPointerException if {@code name} arg or {@code expectedResult} arg or {@code body} arg is null
     */
    public Of(final String name,
              final String expectedResult,
              final ThrowingConsumer<T, ?> body) {
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
              final ThrowingConsumer<T, ?> body) {
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
              final ThrowingConsumer<T, ?> body) {
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
    public ThrowingConsumer<T, ?> body() {
      return this.body;
    }

    @Override
    public ConsumerStep<T> withBody(final ThrowingConsumer<T, ?> body) {
      return new Of<>(this.attributes, body);
    }

    @Override
    public String toString() {
      return "ConsumerStep[" + this.getName() + "]";
    }
  }
}
