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
package org.stebz.extension;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.stebz.annotation.WithRetry;
import org.stebz.attribute.StepAttribute;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.time.Duration;

/**
 * Retry {@link StebzExtension}.
 *
 * @see #RETRY
 * @see #retry
 * @see WithRetry
 */
public class RetryExtension implements InterceptStep {

  /**
   * Retry step attribute.
   *
   * @see WithRetry
   */
  public static final StepAttribute<RetryOptions> RETRY = StepAttribute.nullable("extension:retry");

  /**
   * Retry step attribute. Alias for {@link #RETRY}.
   *
   * @see WithRetry
   */
  public static final StepAttribute<RetryOptions> retry = RETRY;
  private static final StepAttribute<WithRetry> RETRY_ANNOT = StepAttribute.nullable(WithRetry.KEY);
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public RetryExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor
   *
   * @param properties the properties reader
   */
  public RetryExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.retry.enabled", true);
    this.order = properties.getInteger("stebz.extensions.retry.order", MIDDLE_ORDER);
  }

  /**
   * Returns {@code RetryOptions} with default values: count = 2, delay = ZERO, on = {Throwable.class}, but = {}. Alias
   * for {@link #retryOptions()} method.
   *
   * @return {@code RetryOptions}
   */
  public static RetryOptions retry() {
    return retryOptions();
  }

  /**
   * Returns {@code RetryOptions} with default values: count = 2, delay = ZERO, on = {Throwable.class}, but = {}.
   *
   * @return {@code RetryOptions}
   */
  public static RetryOptions retryOptions() {
    return RetryOptions.DEFAULT;
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    if (!this.enabled) {
      return step;
    }

    final int count;
    final long delayMillis;
    final Class<? extends Throwable>[] on;
    final Class<? extends Throwable>[] but;
    final WithRetry annot = step.get(RETRY_ANNOT);
    if (annot != null) {
      count = annot.count();
      delayMillis = annot.unit().toMillis(annot.delay());
      on = annot.on();
      but = annot.but();
    } else {
      final RetryOptions options = step.get(RETRY);
      if (options != null) {
        count = options.count;
        delayMillis = options.delay.toMillis();
        on = options.on;
        but = options.but;
      } else {
        return step;
      }
    }
    if (count < 2 || on.length == 0) {
      return step;
    }

    if (step instanceof RunnableStep) {
      final RunnableStep runnableStep = (RunnableStep) step;
      return runnableStep.withBody(retryRunnable(
        runnableStep.body(), count, delayMillis, on, but
      ));
    } else if (step instanceof ConsumerStep) {
      @SuppressWarnings("unchecked")
      final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) step;
      return consumerStep.withBody(retryConsumer(
        consumerStep.body(), count, delayMillis, on, but
      ));
    } else if (step instanceof SupplierStep) {
      @SuppressWarnings("unchecked")
      final SupplierStep<Object> supplierStep = (SupplierStep<Object>) step;
      return supplierStep.withBody(retrySupplier(
        supplierStep.body(), count, delayMillis, on, but
      ));
    } else if (step instanceof FunctionStep) {
      @SuppressWarnings("unchecked")
      final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) step;
      return functionStep.withBody(retryFunction(
        functionStep.body(), count, delayMillis, on, but
      ));
    }
    return step;
  }

  private static ThrowingRunnable<?> retryRunnable(final ThrowingRunnable<?> origin,
                                                   final int count,
                                                   final long delayMillis,
                                                   final Class<? extends Throwable>[] on,
                                                   final Class<? extends Throwable>[] but) {
    return () -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          origin.run();
        } else {
          try {
            origin.run();
            return;
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, on)) {
              throw ex;
            }
          }
        }
        if (delayMillis > 0L) {
          Thread.sleep(delayMillis);
        }
      }
    };
  }

  private static ThrowingConsumer<Object, ?> retryConsumer(final ThrowingConsumer<Object, ?> origin,
                                                           final int count,
                                                           final long delayMillis,
                                                           final Class<? extends Throwable>[] on,
                                                           final Class<? extends Throwable>[] but) {
    return context -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          origin.accept(context);
        } else {
          try {
            origin.accept(context);
            return;
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, on)) {
              throw ex;
            }
          }
        }
        if (delayMillis > 0L) {
          Thread.sleep(delayMillis);
        }
      }
    };
  }

  private static ThrowingSupplier<Object, ?> retrySupplier(final ThrowingSupplier<Object, ?> origin,
                                                           final int count,
                                                           final long delayMillis,
                                                           final Class<? extends Throwable>[] on,
                                                           final Class<? extends Throwable>[] but) {
    return () -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          return origin.get();
        } else {
          try {
            return origin.get();
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, on)) {
              throw ex;
            }
          }
        }
        if (delayMillis > 0L) {
          Thread.sleep(delayMillis);
        }
      }
      return null; /* unreachable */
    };
  }

  private static ThrowingFunction<Object, Object, ?> retryFunction(final ThrowingFunction<Object, Object, ?> origin,
                                                                   final int count,
                                                                   final long delayMillis,
                                                                   final Class<? extends Throwable>[] on,
                                                                   final Class<? extends Throwable>[] but) {
    return context -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          return origin.apply(context);
        } else {
          try {
            return origin.apply(context);
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, on)) {
              throw ex;
            }
          }
        }
        if (delayMillis > 0L) {
          Thread.sleep(delayMillis);
        }
      }
      return null; /* unreachable */
    };
  }

  private static boolean exceptionMatches(final Throwable exception,
                                          final Class<? extends Throwable>[] types) {
    if (types.length > 0) {
      final Class<? extends Throwable> exceptionType = exception.getClass();
      for (final Class<? extends Throwable> currentType : types) {
        if (currentType.isAssignableFrom(exceptionType)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retry options.
   *
   * @see #RETRY
   */
  public static final class RetryOptions {
    @SuppressWarnings("unchecked")
    private static final RetryOptions DEFAULT =
      new RetryOptions(2, Duration.ZERO, new Class[]{Throwable.class}, new Class[0]);
    private final int count;
    private final Duration delay;
    private final Class<? extends Throwable>[] on;
    private final Class<? extends Throwable>[] but;

    /**
     * Ctor.
     */
    private RetryOptions(final int count,
                         final Duration delay,
                         final Class<? extends Throwable>[] on,
                         final Class<? extends Throwable>[] but) {
      this.count = count;
      this.delay = delay;
      this.on = on;
      this.but = but;
    }

    /**
     * Returns {@code RetryOptions} with given maximum step executions count (including the first execution).
     *
     * @param count the retry count
     * @return {@code RetryOptions} with given retry count
     */
    public RetryOptions count(final int count) {
      return new RetryOptions(count, this.delay, this.on, this.but);
    }

    /**
     * Returns {@code RetryOptions} with given delay between retries.
     *
     * @param delay the delay between retries
     * @return {@code RetryOptions} with given delay between retries
     * @throws NullPointerException if {@code delay} arg is null
     */
    public RetryOptions delay(final Duration delay) {
      if (delay == null) { throw new NullPointerException("delay arg is null"); }
      return new RetryOptions(this.count, delay, this.on, this.but);
    }

    /**
     * Returns {@code RetryOptions} with given exceptions list when to retry (in case of what exception types).
     *
     * @param types the exceptions list when to retry
     * @return {@code RetryOptions} with given exceptions list when to retry
     * @throws NullPointerException if {@code types} arg is null
     */
    @SafeVarargs
    public final RetryOptions on(final Class<? extends Throwable>... types) {
      if (types == null) { throw new NullPointerException("types arg is null"); }
      return new RetryOptions(this.count, this.delay, types, this.but);
    }

    /**
     * Returns {@code RetryOptions} with given exceptions list when not to retry (in case of what exception types).
     *
     * @param types the exceptions list when not to retry
     * @return {@code RetryOptions} with given exceptions list when not to retry
     * @throws NullPointerException if {@code types} arg is null
     */
    @SafeVarargs
    public final RetryOptions but(final Class<? extends Throwable>... types) {
      if (types == null) { throw new NullPointerException("types arg is null"); }
      return new RetryOptions(this.count, this.delay, this.on, types);
    }
  }
}
