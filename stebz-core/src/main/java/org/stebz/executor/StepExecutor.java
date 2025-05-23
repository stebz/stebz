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
package org.stebz.executor;

import org.stebz.exception.StebzStartupException;
import org.stebz.extension.AfterStepFailure;
import org.stebz.extension.AfterStepStart;
import org.stebz.extension.AfterStepSuccess;
import org.stebz.extension.BeforeStepFailure;
import org.stebz.extension.BeforeStepStart;
import org.stebz.extension.BeforeStepSuccess;
import org.stebz.extension.InterceptStep;
import org.stebz.extension.InterceptStepContext;
import org.stebz.extension.InterceptStepException;
import org.stebz.extension.InterceptStepResult;
import org.stebz.extension.StebzExtension;
import org.stebz.listener.StepListener;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingFunction2;
import org.stebz.util.function.ThrowingSupplier;
import org.stebz.util.property.PropertiesReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.IntFunction;

import static java.util.Collections.emptyList;
import static org.stebz.util.Throw.unchecked;

/**
 * Step executor.
 */
public interface StepExecutor {

  /**
   * Executes given step.
   *
   * @param step the step
   * @throws NullPointerException if {@code step} arg is null
   */
  void execute(RunnableStep step);

  /**
   * Executes given step and returns step result.
   *
   * @param step the step
   * @param <R>  the type of the step result
   * @return step result
   * @throws NullPointerException if {@code step} arg is null
   */
  <R> R execute(SupplierStep<? extends R> step);

  /**
   * Executes given step
   *
   * @param step         the step
   * @param contextValue the input step argument
   * @param <T>          the type of the input step argument
   * @throws NullPointerException if {@code step} arg is null
   */
  <T> void execute(ConsumerStep<? super T> step,
                   T contextValue);

  /**
   * Executes given step and returns step result.
   *
   * @param step         the step
   * @param contextValue the input step argument
   * @param <T>          the type of the input step argument
   * @param <R>          the type of the step result
   * @return step result
   * @throws NullPointerException if {@code step} arg is null
   */
  <T, R> R execute(FunctionStep<? super T, ? extends R> step,
                   T contextValue);

  /**
   * Returns main Stebz framework {@code StepExecutor}.
   *
   * @return main Stebz framework {@code StepExecutor}
   * @throws StebzStartupException if there is a problem with Stebz framework main {@code StepExecutor} initialization
   */
  static StepExecutor get() {
    return Of.INSTANCE.get();
  }

  /**
   * Default {@code StepExecutor} implementation.
   */
  final class Of implements StepExecutor {
    private static final ThrowingSupplier<StepExecutor, Error> INSTANCE =
      new ThrowingSupplier.Caching<>(Of::createMainStepExecutor);
    private final StepListener[] listeners;
    private final InterceptStepContext[] interceptContextExts;
    private final InterceptStep[] interceptStepExts;
    private final InterceptStepResult[] interceptResultExts;
    private final InterceptStepException[] interceptExceptionExts;
    private final BeforeStepStart[] beforeStartExts;
    private final AfterStepStart[] afterStartExts;
    private final BeforeStepSuccess[] beforeSuccessExts;
    private final AfterStepSuccess[] afterSuccessExts;
    private final BeforeStepFailure[] beforeFailureExts;
    private final AfterStepFailure[] afterFailureExts;

    /**
     * Ctor.
     *
     * @param listeners  listener list
     * @param extensions extension list
     */
    public Of(final StepListener[] listeners,
              final StebzExtension[] extensions) {
      if (listeners == null) { throw new NullPointerException("listeners arg is null"); }
      if (extensions == null) { throw new NullPointerException("extensions arg is null"); }
      this.listeners = listeners;
      this.interceptContextExts = extsOfType(InterceptStepContext.class, InterceptStepContext[]::new, extensions);
      this.interceptStepExts = extsOfType(InterceptStep.class, InterceptStep[]::new, extensions);
      this.interceptResultExts = extsOfType(InterceptStepResult.class, InterceptStepResult[]::new, extensions);
      this.interceptExceptionExts = extsOfType(InterceptStepException.class, InterceptStepException[]::new, extensions);
      this.beforeStartExts = extsOfType(BeforeStepStart.class, BeforeStepStart[]::new, extensions);
      this.afterStartExts = extsOfType(AfterStepStart.class, AfterStepStart[]::new, extensions);
      this.beforeSuccessExts = extsOfType(BeforeStepSuccess.class, BeforeStepSuccess[]::new, extensions);
      this.afterSuccessExts = extsOfType(AfterStepSuccess.class, AfterStepSuccess[]::new, extensions);
      this.beforeFailureExts = extsOfType(BeforeStepFailure.class, BeforeStepFailure[]::new, extensions);
      this.afterFailureExts = extsOfType(AfterStepFailure.class, AfterStepFailure[]::new, extensions);
    }

    private static <E extends StebzExtension> E[] extsOfType(final Class<E> type,
                                                             final IntFunction<E[]> arrayGenerator,
                                                             final StebzExtension[] extensions) {
      return Arrays.stream(extensions)
        .filter(type::isInstance)
        .toArray(arrayGenerator);
    }

    @Override
    public void execute(final RunnableStep step) {
      if (step == null) { throw new NullPointerException("step arg is null"); }
      this.exec(step, false, null, false, (updatedStep, updatedContext) -> {
        ((RunnableStep) updatedStep).body().run();
        return null;
      });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R execute(final SupplierStep<? extends R> step) {
      if (step == null) { throw new NullPointerException("step arg is null"); }
      return (R) this.exec(step, false, null, true, (updatedStep, updatedContext) ->
        ((SupplierStep<Object>) updatedStep).body().get()
      );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void execute(final ConsumerStep<? super T> step,
                            final T contextValue) {
      if (step == null) { throw new NullPointerException("step arg is null"); }
      this.exec(step, true, contextValue, false, (updatedStep, updatedContext) -> {
        ((ConsumerStep<Object>) updatedStep).body().accept(updatedContext);
        return null;
      });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, R> R execute(final FunctionStep<? super T, ? extends R> step,
                            final T contextValue) {
      if (step == null) { throw new NullPointerException("step arg is null"); }
      return (R) this.exec(step, true, contextValue, true, (updatedStep, updatedContext) ->
        ((FunctionStep<Object, Object>) updatedStep).body().apply(updatedContext)
      );
    }

    private Object exec(StepObj<?> step,
                        final boolean hasContext,
                        Object context,
                        boolean hasResult,
                        final ThrowingFunction2<StepObj<?>, Object, ?, ?> action) { /* (step, context) -> result */
      /* start */
      final NullableOptional<Object> optionalContext;
      if (hasContext) {
        for (final InterceptStepContext ext : this.interceptContextExts) {
          context = ext.interceptStepContext(step, context);
        }
        optionalContext = NullableOptional.of(context);
      } else {
        optionalContext = NullableOptional.empty();
      }
      for (final InterceptStep ext : this.interceptStepExts) {
        step = ext.interceptStep(step, optionalContext);
      }
      for (final BeforeStepStart ext : this.beforeStartExts) {
        ext.beforeStepStart(step, optionalContext);
      }
      for (final StepListener listener : this.listeners) {
        listener.onStepStart(step, optionalContext);
      }
      for (final AfterStepStart ext : this.afterStartExts) {
        ext.afterStepStart(step, optionalContext);
      }

      /* action */
      Object result = null;
      Throwable exception = null;
      try {
        result = action.apply(step, context);
      } catch (final Throwable ex) {
        exception = ex;
      }

      if (exception == null) {

        /* success */
        final NullableOptional<Object> optionalResult;
        if (hasResult) {
          for (final InterceptStepResult ext : this.interceptResultExts) {
            result = ext.interceptStepResult(step, optionalContext, result);
          }
          optionalResult = NullableOptional.of(result);
        } else {
          optionalResult = NullableOptional.empty();
        }
        for (final BeforeStepSuccess ext : this.beforeSuccessExts) {
          ext.beforeStepSuccess(step, optionalContext, optionalResult);
        }
        for (final StepListener listener : this.listeners) {
          listener.onStepSuccess(step, optionalContext, optionalResult);
        }
        for (final AfterStepSuccess ext : this.afterSuccessExts) {
          ext.afterStepSuccess(step, optionalContext, optionalResult);
        }
        return result;
      } else {

        /* failure */
        for (final InterceptStepException ext : this.interceptExceptionExts) {
          exception = ext.interceptStepException(step, optionalContext, exception);
        }
        for (final BeforeStepFailure ext : this.beforeFailureExts) {
          ext.beforeStepFailure(step, optionalContext, exception);
        }
        for (final StepListener listener : this.listeners) {
          listener.onStepFailure(step, optionalContext, exception);
        }
        for (final AfterStepFailure ext : this.afterFailureExts) {
          ext.afterStepFailure(step, optionalContext, exception);
        }
        throw unchecked(exception);
      }
    }

    private static StepExecutor createMainStepExecutor() {
      String propsFileName = System.getProperties().getProperty("stebz.properties.path");
      if (propsFileName == null || (propsFileName = propsFileName.trim()).isEmpty()) {
        propsFileName = "stebz.properties";
      }
      final PropertiesReader properties = new PropertiesReader.Of(propsFileName);
      StartupPropertiesReader.set(properties);
      try {
        /* extensions */
        final List<StebzExtension> extensions;
        if (properties.getBoolean("stebz.extensions.enabled", true)) {
          extensions = new ArrayList<>();
          for (final Class<?> cls : properties.getClassList("stebz.extensions.list", ",")) {
            extensions.add((StebzExtension) cls.getConstructor().newInstance());
          }
          if (properties.getBoolean("stebz.extensions.autodetection", true)) {
            ServiceLoader.load(StebzExtension.class).forEach(extensions::add);
          }
          extensions.forEach(extension -> extension.configure(properties));
          extensions.sort(Comparator.comparingInt(StebzExtension::order));
        } else {
          extensions = emptyList();
        }

        /* listeners */
        final List<StepListener> listeners;
        if (properties.getBoolean("stebz.listeners.enabled", true)) {
          listeners = new ArrayList<>();
          for (final Class<?> cls : properties.getClassList("stebz.listeners.list", ",")) {
            listeners.add((StepListener) cls.getConstructor().newInstance());
          }
          if (properties.getBoolean("stebz.listeners.autodetection", true)) {
            ServiceLoader.load(StepListener.class).forEach(listeners::add);
          }
          listeners.forEach(listener -> listener.configure(properties));
          listeners.sort(Comparator.comparingInt(StepListener::order));
        } else {
          listeners = emptyList();
        }

        return new Of(
          listeners.toArray(new StepListener[0]),
          extensions.toArray(new StebzExtension[0])
        );
      } catch (final Exception ex) {
        throw new StebzStartupException("Failed to instantiate the main StepExecutor cause " + ex, ex);
      } finally {
        StartupPropertiesReader.clear();
      }
    }
  }
}
