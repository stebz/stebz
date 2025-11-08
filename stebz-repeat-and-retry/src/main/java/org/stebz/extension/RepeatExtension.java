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

import org.stebz.annotation.WithRepeat;
import org.stebz.attribute.StepAttribute;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingConsumer;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingRunnable;
import org.stebz.util.function.ThrowingSupplier;
import org.stebz.util.property.PropertiesReader;

import java.time.Duration;

/**
 * Repeat {@link StebzExtension}.
 *
 * @see #REPEAT
 * @see #repeat
 * @see WithRepeat
 */
public class RepeatExtension implements InterceptStep {

  /**
   * Repeat step attribute.
   *
   * @see WithRepeat
   */
  public static final StepAttribute<RepeatOptions> REPEAT = StepAttribute.nullable("extension:repeat");

  /**
   * Repeat step attribute. Alias for {@link #REPEAT}.
   *
   * @see WithRepeat
   */
  public static final StepAttribute<RepeatOptions> repeat = REPEAT;
  private static final StepAttribute<WithRepeat> REPEAT_ANNOT = StepAttribute.nullable(WithRepeat.KEY);
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public RepeatExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor
   *
   * @param properties the properties reader
   */
  public RepeatExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.repeat.enabled", true);
    this.order = properties.getInteger("stebz.extensions.repeat.order", DEFAULT_ORDER);
  }

  /**
   * Returns {@code RepeatOptions} with default values: count = 2, delay = ZERO, skip = {}, but = {}. Alias for
   * {@link #repeatOptions()} method.
   *
   * @return {@code RepeatOptions}
   */
  public static RepeatOptions repeat() {
    return repeatOptions();
  }

  /**
   * Returns {@code RepeatOptions} with default values: count = 2, delay = ZERO, skip = {}, but = {}.
   *
   * @return {@code RepeatOptions}
   */
  public static RepeatOptions repeatOptions() {
    return RepeatOptions.DEFAULT;
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
    final Class<? extends Throwable>[] skip;
    final Class<? extends Throwable>[] but;
    final WithRepeat annot = step.get(REPEAT_ANNOT);
    if (annot != null) {
      count = annot.count();
      delayMillis = annot.unit().toMillis(annot.delay());
      skip = annot.skip();
      but = annot.but();
    } else {
      final RepeatOptions options = step.get(REPEAT);
      if (options != null) {
        count = options.count;
        delayMillis = options.delay.toMillis();
        skip = options.skip;
        but = options.but;
      } else {
        return step;
      }
    }
    if (count < 2) {
      return step;
    }

    if (step instanceof RunnableStep) {
      final RunnableStep runnableStep = (RunnableStep) step;
      return runnableStep.withBody(repeatRunnable(
        runnableStep.body(), count, delayMillis, skip, but
      ));
    } else if (step instanceof ConsumerStep) {
      @SuppressWarnings("unchecked")
      final ConsumerStep<Object> consumerStep = (ConsumerStep<Object>) step;
      return consumerStep.withBody(repeatConsumer(
        consumerStep.body(), count, delayMillis, skip, but
      ));
    } else if (step instanceof SupplierStep) {
      @SuppressWarnings("unchecked")
      final SupplierStep<Object> supplierStep = (SupplierStep<Object>) step;
      return supplierStep.withBody(repeatSupplier(
        supplierStep.body(), count, delayMillis, skip, but
      ));
    } else if (step instanceof FunctionStep) {
      @SuppressWarnings("unchecked")
      final FunctionStep<Object, Object> functionStep = (FunctionStep<Object, Object>) step;
      return functionStep.withBody(repeatFunction(
        functionStep.body(), count, delayMillis, skip, but
      ));
    }
    return step;
  }

  private static ThrowingRunnable<?> repeatRunnable(final ThrowingRunnable<?> origin,
                                                    final int count,
                                                    final long delayMillis,
                                                    final Class<? extends Throwable>[] skip,
                                                    final Class<? extends Throwable>[] but) {
    return () -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          origin.run();
        } else {
          try {
            origin.run();
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, skip)) {
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

  private static ThrowingConsumer<Object, ?> repeatConsumer(final ThrowingConsumer<Object, ?> origin,
                                                            final int count,
                                                            final long delayMillis,
                                                            final Class<? extends Throwable>[] skip,
                                                            final Class<? extends Throwable>[] but) {
    return context -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          origin.accept(context);
        } else {
          try {
            origin.accept(context);
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, skip)) {
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

  private static ThrowingSupplier<Object, ?> repeatSupplier(final ThrowingSupplier<Object, ?> origin,
                                                            final int count,
                                                            final long delayMillis,
                                                            final Class<? extends Throwable>[] skip,
                                                            final Class<? extends Throwable>[] but) {
    return () -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          return origin.get();
        } else {
          try {
            origin.get();
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, skip)) {
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

  private static ThrowingFunction<Object, Object, ?> repeatFunction(final ThrowingFunction<Object, Object, ?> origin,
                                                                    final int count,
                                                                    final long delayMillis,
                                                                    final Class<? extends Throwable>[] skip,
                                                                    final Class<? extends Throwable>[] but) {
    return context -> {
      for (int attempt = 0; attempt < count; attempt++) {
        if (attempt == count - 1) {
          return origin.apply(context);
        } else {
          try {
            origin.apply(context);
          } catch (final Throwable ex) {
            if (exceptionMatches(ex, but)) {
              throw ex;
            }
            if (!exceptionMatches(ex, skip)) {
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
   * Repeat options.
   *
   * @see #REPEAT
   */
  public static final class RepeatOptions {
    private static final RepeatOptions DEFAULT = new RepeatOptions(2, Duration.ZERO);
    private final int count;
    private final Duration delay;
    private final Class<? extends Throwable>[] skip;
    private final Class<? extends Throwable>[] but;

    /**
     * Ctor.
     */
    private RepeatOptions(final int count,
                          final Duration delay) {
      @SuppressWarnings("unchecked")
      final Class<? extends Throwable>[] emptyThrowableArray = new Class[0];
      this.count = count;
      this.delay = delay;
      this.skip = emptyThrowableArray;
      this.but = emptyThrowableArray;
    }

    /**
     * Ctor.
     */
    private RepeatOptions(final int count,
                          final Duration delay,
                          final Class<? extends Throwable>[] skip,
                          final Class<? extends Throwable>[] but) {
      this.count = count;
      this.delay = delay;
      this.skip = skip;
      this.but = but;
    }

    /**
     * Returns {@code RepeatOptions} with given step executions count (including the first execution).
     *
     * @param count the repeat count
     * @return {@code RepeatOptions} with given repeat count
     */
    public RepeatOptions count(final int count) {
      return new RepeatOptions(count, this.delay, this.skip, this.but);
    }

    /**
     * Returns {@code RepeatOptions} with given delay between repeats.
     *
     * @param delay the delay between repeats
     * @return {@code RepeatOptions} with given delay between repeats
     * @throws NullPointerException if {@code delay} arg is null
     */
    public RepeatOptions delay(final Duration delay) {
      if (delay == null) { throw new NullPointerException("delay arg is null"); }
      return new RepeatOptions(this.count, delay, this.skip, this.but);
    }

    /**
     * Returns {@code RepeatOptions} with given exceptions list which exception to ignore (in case of what exception
     * types).
     *
     * @param types the exceptions list to ignore
     * @return {@code RepeatOptions} with given exceptions list to ignore
     * @throws NullPointerException if {@code types} arg is null
     */
    @SafeVarargs
    public final RepeatOptions skip(final Class<? extends Throwable>... types) {
      if (types == null) { throw new NullPointerException("types arg is null"); }
      return new RepeatOptions(this.count, this.delay, types, this.but);
    }

    /**
     * Returns {@code RepeatOptions} with given exceptions list which exception not to ignore (in case of what exception
     * types).
     *
     * @param types the exceptions list not to ignore
     * @return {@code RepeatOptions} with given exceptions list not to ignore
     * @throws NullPointerException if {@code types} arg is null
     */
    @SafeVarargs
    public final RepeatOptions but(final Class<? extends Throwable>... types) {
      if (types == null) { throw new NullPointerException("types arg is null"); }
      return new RepeatOptions(this.count, this.delay, this.skip, types);
    }
  }
}
