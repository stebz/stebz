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
package org.stebz;

import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttributes;
import org.stebz.executor.StepExecutor;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;

import java.util.Map;

import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Allows to call steps around a specific context.
 *
 * @param <T> the type of the context
 */
public interface Around<T> {

  /**
   * Executes given step.
   *
   * @param step the step
   * @return {@code Around} object
   */
  Around<T> step(RunnableStep step);

  /**
   * Executes given step with keyword.
   *
   * @param keyword the keyword
   * @param step    the step
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 RunnableStep step);

  /**
   * Executes given step with name.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  Around<T> step(String name,
                 RunnableStep step);

  /**
   * Executes given step with keyword and name.
   *
   * @param name    the name
   * @param keyword the keyword
   * @param step    the step
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name,
                 RunnableStep step);

  /**
   * Executes given step and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R step(SupplierStep<? extends R> step);

  /**
   * Executes given step with keyword and returns step result.
   *
   * @param keyword the keyword
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  <R> R step(Keyword keyword,
             SupplierStep<? extends R> step);

  /**
   * Executes given step with name and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R step(String name,
             SupplierStep<? extends R> step);

  /**
   * Executes given step with keyword and name and returns step result.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  <R> R step(Keyword keyword,
             String name,
             SupplierStep<? extends R> step);

  /**
   * Executes given step on the context value.
   *
   * @param step the step
   * @return {@code Around} object
   */
  Around<T> step(ConsumerStep<? super T> step);

  /**
   * Executes given step with keyword on the context value.
   *
   * @param keyword the keyword
   * @param step    the step
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 ConsumerStep<? super T> step);

  /**
   * Executes given step with name on the context value.
   *
   * @param name the name
   * @param step the step
   * @return {@code Around} object
   */
  Around<T> step(String name,
                 ConsumerStep<? super T> step);

  /**
   * Executes given step with keyword and name on the context value.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name,
                 ConsumerStep<? super T> step);

  /**
   * Executes given step on the context value and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R step(FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with keyword on the context value and returns step result.
   *
   * @param keyword the keyword
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  <R> R step(Keyword keyword,
             FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with name on the context value and returns step result.
   *
   * @param name the name
   * @param step the step
   * @param <R>  the type of the result
   * @return step result
   */
  <R> R step(String name,
             FunctionStep<? super T, ? extends R> step);

  /**
   * Executes given step with keyword and name on the context value and returns step result.
   *
   * @param keyword the keyword
   * @param name    the name
   * @param step    the step
   * @param <R>     the type of the result
   * @return step result
   */
  <R> R step(Keyword keyword,
             String name,
             FunctionStep<? super T, ? extends R> step);

  /**
   * Executes step with given attributes on the context value.
   *
   * @param name the step name
   * @param body the step body
   * @return {@code Around} object
   */
  Around<T> step(String name,
                 ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with given attributes on the context value.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param body    the step body
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name,
                 ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with given attributes on the context value.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @return {@code Around} object
   */
  Around<T> step(String name,
                 Map<String, ?> params,
                 ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with given attributes on the context value.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   * @param body    the step body
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name,
                 Map<String, ?> params,
                 ThrowingConsumer<? super T, ?> body);

  /**
   * Executes step with given attributes on the context value and returns step result.
   *
   * @param name the step name
   * @param body the step body
   * @param <R>  the type of the step result
   * @return step result
   */
  <R> R step(String name,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with given attributes on the context value and returns step result.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param body    the step body
   * @param <R>     the type of the step result
   * @return step result
   */
  <R> R step(Keyword keyword,
             String name,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with given attributes on the context value and returns step result.
   *
   * @param name   the step name
   * @param params the step params
   * @param body   the step body
   * @param <R>    the type of the step result
   * @return step result
   */
  <R> R step(String name,
             Map<String, ?> params,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with given attributes on the context value and returns step result.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   * @param body    the step body
   * @param <R>     the type of the step result
   * @return step result
   */
  <R> R step(Keyword keyword,
             String name,
             Map<String, ?> params,
             ThrowingFunction<? super T, ? extends R, ?> body);

  /**
   * Executes step with given attributes.
   *
   * @param name the step name
   * @return {@code Around} object
   */
  Around<T> step(String name);

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name);

  /**
   * Executes step with given attributes.
   *
   * @param name   the step name
   * @param params the step params
   * @return {@code Around} object
   */
  Around<T> step(String name,
                 Map<String, ?> params);

  /**
   * Executes step with given attributes.
   *
   * @param keyword the step keyword
   * @param name    the step name
   * @param params  the step params
   * @return {@code Around} object
   */
  Around<T> step(Keyword keyword,
                 String name,
                 Map<String, ?> params);

  /**
   * Default {@code Around} implementation.
   *
   * @param <T> the type of the context
   */
  class Of<T> implements Around<T> {
    private final StepExecutor executor;
    private final T context;

    /**
     * Ctor.
     *
     * @param executor the executor
     * @param context  the context
     * @throws NullPointerException if {@code executor} arg is null
     */
    public Of(final StepExecutor executor,
              final T context) {
      if (executor == null) { throw new NullPointerException("executor arg is null"); }
      this.executor = executor;
      this.context = context;
    }

    @Override
    public Around<T> step(final RunnableStep step) {
      this.executor.execute(step);
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, keyword));
      return this;
    }

    @Override
    public Around<T> step(final String name,
                          final RunnableStep step) {
      this.executor.execute(step.with(NAME, name));
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final String name,
                          final RunnableStep step) {
      this.executor.execute(step.with(KEYWORD, keyword, NAME, name));
      return this;
    }

    @Override
    public <R> R step(final SupplierStep<? extends R> step) {
      return this.executor.execute(step);
    }

    @Override
    public <R> R step(final Keyword keyword,
                      final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, keyword));
    }

    @Override
    public <R> R step(final String name,
                      final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(NAME, name));
    }

    @Override
    public <R> R step(final Keyword keyword,
                      final String name,
                      final SupplierStep<? extends R> step) {
      return this.executor.execute(step.with(KEYWORD, keyword, NAME, name));
    }

    @Override
    public Around<T> step(final ConsumerStep<? super T> step) {
      this.executor.execute(
        step,
        this.context
      );
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, keyword),
        this.context
      );
      return this;
    }

    @Override
    public Around<T> step(final String name,
                          final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final String name,
                          final ConsumerStep<? super T> step) {
      this.executor.execute(
        step.with(KEYWORD, keyword, NAME, name),
        this.context
      );
      return this;
    }

    @Override
    public <R> R step(final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step,
        this.context
      );
    }

    @Override
    public <R> R step(final Keyword keyword,
                      final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, keyword),
        this.context
      );
    }

    @Override
    public <R> R step(final String name,
                      final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(NAME, name),
        this.context
      );
    }

    @Override
    public <R> R step(final Keyword keyword,
                      final String name,
                      final FunctionStep<? super T, ? extends R> step) {
      return this.executor.execute(
        step.with(KEYWORD, keyword, NAME, name),
        this.context
      );
    }

    @Override
    public Around<T> step(final String name,
                          final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final String name,
                          final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Around<T> step(final String name,
                          final Map<String, ?> params,
                          final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Around<T> step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params,
                          final ThrowingConsumer<? super T, ?> body) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
      return this;
    }

    @Override
    public <R> R step(final String name,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(NAME, name),
        body
      ), this.context);
    }

    @Override
    public <R> R step(final Keyword keyword,
                      final String name,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R step(final String name,
                      final Map<String, ?> params,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R step(final Keyword keyword,
                      final String name,
                      final Map<String, ?> params,
                      final ThrowingFunction<? super T, ? extends R, ?> body) {
      return this.executor.execute(new FunctionStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
        body
      ), this.context);
    }

    @Override
    public Around<T> step(final String name) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(NAME, name),
        ConsumerStep.emptyBody()
      ), this.context);
      return this;
    }

    @Override
    public Around<T> step(final Keyword keyword,
                          final String name) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name),
        ConsumerStep.emptyBody()
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Around<T> step(final String name,
                          final Map<String, ?> params) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(NAME, name, PARAMS, (Map<String, Object>) params),
        ConsumerStep.emptyBody()
      ), this.context);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Around<T> step(final Keyword keyword,
                          final String name,
                          final Map<String, ?> params) {
      this.executor.execute(new ConsumerStep.Of<>(
        new StepAttributes.Of(KEYWORD, keyword, NAME, name, PARAMS, (Map<String, Object>) params),
        ConsumerStep.emptyBody()
      ), this.context);
      return this;
    }
  }
}
