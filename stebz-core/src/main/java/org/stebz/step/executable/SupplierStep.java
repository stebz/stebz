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
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Supplier step.
 *
 * @param <R> the type of the step result
 */
public interface SupplierStep<R> extends ExecutableStep<ThrowingSupplier<R, ?>, SupplierStep<R>> {

  /**
   * Returns {@code SupplierStep} with given block before step body.
   *
   * @param beforeBlock the before block
   * @return {@code SupplierStep} with given block before step body
   * @throws NullPointerException if {@code beforeBlock} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default SupplierStep<R> withBefore(final ThrowingRunnable<?> beforeBlock) {
    if (beforeBlock == null) { throw new NullPointerException("beforeBlock arg is null"); }
    final ThrowingSupplier<R, ?> body = this.body();
    return this.withBody(() -> {
      beforeBlock.run();
      return body.get();
    });
  }

  /**
   * Returns {@code SupplierStep} with given block after step body.
   *
   * @param afterBlock the after block
   * @return {@code SupplierStep} with given block after step body
   * @throws NullPointerException if {@code afterBlock} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default SupplierStep<R> withAfter(final ThrowingConsumer<? super R, ?> afterBlock) {
    if (afterBlock == null) { throw new NullPointerException("afterBlock arg is null"); }
    final ThrowingSupplier<R, ?> body = this.body();
    return this.withBody(() -> {
      final R result = body.get();
      afterBlock.accept(result);
      return result;
    });
  }

  /**
   * Returns {@code SupplierStep} with given block after step body.
   *
   * @param afterBlock the after block
   * @return {@code SupplierStep} with given block after step body
   * @throws NullPointerException if {@code afterBlock} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default SupplierStep<R> withAfter(final ThrowingFunction<? super R, ? extends R, ?> afterBlock) {
    if (afterBlock == null) { throw new NullPointerException("afterBlock arg is null"); }
    final ThrowingSupplier<R, ?> body = this.body();
    return this.withBody(() ->
      afterBlock.apply(body.get())
    );
  }

  /**
   * Returns {@code SupplierStep} with given finally block after step body.
   *
   * @param finallyBlock the finally block
   * @return {@code SupplierStep} with given finally block after step body
   * @throws NullPointerException if {@code finallyBlock} arg is null
   * @see #withBody(Object)
   * @see #withNewBody(ThrowingFunction)
   */
  default SupplierStep<R> withFinally(final ThrowingRunnable<?> finallyBlock) {
    if (finallyBlock == null) { throw new NullPointerException("finallyBlock arg is null"); }
    final ThrowingSupplier<R, ?> body = this.body();
    return this.withBody(() -> {
      Throwable mainEx = null;
      R result = null;
      try {
        result = body.get();
      } catch (final Throwable ex) {
        mainEx = ex;
      }
      try {
        finallyBlock.run();
      } catch (final Throwable ex) {
        if (mainEx != null) {
          ex.addSuppressed(mainEx);
        }
        mainEx = ex;
      }
      if (mainEx != null) {
        throw mainEx;
      }
      return result;
    });
  }

  /**
   * Returns this step as {@code RunnableStep}.
   *
   * @return this step as {@code RunnableStep}
   */
  default RunnableStep noResult() {
    return new RunnableStep.Of(this.attributes(), this.body()::get);
  }

  /**
   * Returns {@code SupplierStep} with given body.
   *
   * @param body the step body
   * @param <R>  the type of the step result
   * @return {@code SupplierStep} with given body
   * @throws NullPointerException if {@code body} arg is null
   */
  static <R> SupplierStep<R> of(final ThrowingSupplier<R, ?> body) {
    return new Of<>(body);
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
  static <R> SupplierStep<R> of(final String name,
                                final ThrowingSupplier<R, ?> body) {
    return new Of<>(name, body);
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
  static <R> SupplierStep<R> of(final String name,
                                final String expectedResult,
                                final ThrowingSupplier<R, ?> body) {
    return new Of<>(name, expectedResult, body);
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
  static <R> SupplierStep<R> of(final String name,
                                final Map<String, ?> params,
                                final String expectedResult,
                                final ThrowingSupplier<R, ?> body) {
    return new Of<>(name, params, expectedResult, body);
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
  static <R> SupplierStep<R> of(final String name,
                                final Map<String, ?> params,
                                final ThrowingSupplier<R, ?> body) {
    return new Of<>(name, params, body);
  }

  /**
   * Returns {@code SupplierStep} of given step attributes and body.
   *
   * @param origin the origin step
   * @param <R>    the type of the step result
   * @return {@code SupplierStep} of given step attributes and body
   * @throws NullPointerException if {@code origin} arg is null
   */
  static <R> SupplierStep<R> of(final SupplierStep<R> origin) {
    return new Of<>(origin);
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
  static <R> SupplierStep<R> of(final StepAttributes attributes,
                                final ThrowingSupplier<R, ?> body) {
    return new Of<>(attributes, body);
  }

  /**
   * Returns {@code ThrowingSupplier} that throws an {@code StepNotImplementedError}.
   *
   * @param <R> the type of the step result
   * @return {@code ThrowingSupplier} that throws an {@code StepNotImplementedError}
   * @see #notImplemented()
   */
  static <R> ThrowingSupplier<R, Error> notImplementedBody() {
    return () -> { throw new StepNotImplementedError("SupplierStep not implemented"); };
  }

  /**
   * Returns {@code SupplierStep} with not implemented body.
   *
   * @param <R> the type of the step result
   * @return {@code SupplierStep} with not implemented body
   * @see #notImplementedBody()
   */
  static <R> SupplierStep<R> notImplemented() {
    return SupplierStep.of(notImplementedBody());
  }

  /**
   * Default {@code SupplierStep} implementation.
   *
   * @param <R> the type of the step result
   */
  class Of<R> implements SupplierStep<R> {
    private final StepAttributes attributes;
    private final ThrowingSupplier<R, ?> body;

    /**
     * Ctor.
     *
     * @param body the step body
     * @throws NullPointerException if {@code body} arg is null
     */
    public Of(final ThrowingSupplier<R, ?> body) {
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
              final ThrowingSupplier<R, ?> body) {
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
              final ThrowingSupplier<R, ?> body) {
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
              final ThrowingSupplier<R, ?> body) {
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
              final ThrowingSupplier<R, ?> body) {
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
    public Of(final SupplierStep<R> origin) {
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
              final ThrowingSupplier<R, ?> body) {
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
    public SupplierStep<R> withAttributes(final StepAttributes attributes) {
      return new Of<>(attributes, this.body);
    }

    @Override
    public ThrowingSupplier<R, ?> body() {
      return this.body;
    }

    @Override
    public SupplierStep<R> withBody(final ThrowingSupplier<R, ?> body) {
      return new Of<>(this.attributes, body);
    }

    @Override
    public String toString() {
      return "SupplierStep[" + this.getName() + "]";
    }
  }
}
