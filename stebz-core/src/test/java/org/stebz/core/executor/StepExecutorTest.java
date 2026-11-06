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
package org.stebz.core.executor;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.extension.AfterStepFailure;
import org.stebz.core.extension.AfterStepStart;
import org.stebz.core.extension.AfterStepSuccess;
import org.stebz.core.extension.BeforeStepFailure;
import org.stebz.core.extension.BeforeStepStart;
import org.stebz.core.extension.BeforeStepSuccess;
import org.stebz.core.extension.InterceptStep;
import org.stebz.core.extension.InterceptStepContext;
import org.stebz.core.extension.InterceptStepException;
import org.stebz.core.extension.InterceptStepResult;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.listener.StepListener;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StepExecutor}.
 */
final class StepExecutorTest {

  private static final class RecordingListener implements StepListener {
    final List<String> events = new ArrayList<>();

    @Override
    public void onStepStart(final StepObj<?> step,
                            final NullableOptional<Object> context) {
      this.events.add("start:" + step.getName());
    }

    @Override
    public void onStepSuccess(final StepObj<?> step,
                              final NullableOptional<Object> context,
                              final NullableOptional<Object> result) {
      this.events.add("success:" + step.getName()
        + (result.isPresent() ? ":" + result.get() : ""));
    }

    @Override
    public void onStepFailure(final StepObj<?> step,
                              final NullableOptional<Object> context,
                              final Throwable exception) {
      this.events.add("failure:" + step.getName() + ":" + exception.getMessage());
    }
  }

  private static final class RecordingExtension implements BeforeStepStart, AfterStepStart,
    BeforeStepSuccess, AfterStepSuccess {
    final List<String> events = new ArrayList<>();

    @Override
    public void beforeStepStart(final StepObj<?> step,
                                final NullableOptional<Object> context) {
      this.events.add("beforeStart:" + step.getName());
    }

    @Override
    public void afterStepStart(final StepObj<?> step,
                               final NullableOptional<Object> context) {
      this.events.add("afterStart:" + step.getName());
    }

    @Override
    public void beforeStepSuccess(final StepObj<?> step,
                                  final NullableOptional<Object> context,
                                  final NullableOptional<Object> result) {
      this.events.add("beforeSuccess:" + step.getName());
    }

    @Override
    public void afterStepSuccess(final StepObj<?> step,
                                 final NullableOptional<Object> context,
                                 final NullableOptional<Object> result) {
      this.events.add("afterSuccess:" + step.getName());
    }
  }

  private static final class RecordingFailureExtension implements BeforeStepFailure, AfterStepFailure {
    final List<String> events = new ArrayList<>();

    @Override
    public void beforeStepFailure(final StepObj<?> step,
                                  final NullableOptional<Object> context,
                                  final Throwable exception) {
      this.events.add("beforeFailure:" + step.getName());
    }

    @Override
    public void afterStepFailure(final StepObj<?> step,
                                 final NullableOptional<Object> context,
                                 final Throwable exception) {
      this.events.add("afterFailure:" + step.getName());
    }
  }

  private static RunnableStep named(final RunnableStep step,
                                    final String name) {
    return step.withAttributes(step.getAttributes().with(StepAttribute.NAME, name));
  }

  private static RunnableStep failingStep(final String message) {
    return RunnableStep.of("step", () -> {
      throw new IllegalStateException(message);
    });
  }

  @Test
  void ctorShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> new StepExecutor.Of(null, new StebzExtension[0]))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new StepExecutor.Of(new StepListener[0], null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void executeShouldThrowExceptionForNullStep() {
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    assertThatThrownBy(() -> executor.execute((RunnableStep) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> executor.execute((SupplierStep<Object>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> executor.execute((ConsumerStep<Object>) null, "v"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> executor.execute((FunctionStep<Object, Object>) null, "v"))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void executeShouldRunRunnableStepBody() {
    final List<Object> executed = new ArrayList<>();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    executor.execute(RunnableStep.of("step", () -> executed.add("run")));
    assertThat(executed).containsExactly("run");
  }

  @Test
  void executeShouldReturnSupplierStepResult() {
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    assertThat(executor.execute(SupplierStep.of("step", () -> "result"))).isEqualTo("result");
  }

  @Test
  void executeShouldPassContextValueToConsumerStep() {
    final List<Object> received = new ArrayList<>();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    executor.execute(ConsumerStep.of("step", (ThrowingConsumer<Object, Error>) received::add), "value");
    assertThat(received).containsExactly("value");
  }

  @Test
  void executeShouldPassContextValueToFunctionStepAndReturnResult() {
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    assertThat(executor.execute(
      FunctionStep.of("step", (ThrowingFunction<Object, String, Error>) value -> "value:" + value), "x"))
      .isEqualTo("value:x");
  }

  @Test
  void executeShouldRunContextualBodyOfContextualRunnableStep() {
    final List<Object> received = new ArrayList<>();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    executor.execute(new RunnableStep.Of(StepAttributes.empty(), "ctx",
      (ThrowingConsumer<Object, Error>) received::add));
    assertThat(received).containsExactly("ctx");
  }

  @Test
  void executeShouldRunContextualBodyOfContextualSupplierStep() {
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0], new StebzExtension[0]);
    assertThat(executor.execute(new SupplierStep.Of<>(StepAttributes.empty(), "ctx",
      (ThrowingFunction<Object, String, Error>) value -> "value:" + value)))
      .isEqualTo("value:ctx");
  }

  @Test
  void listenerShouldBeNotifiedOnStartSuccessAndFailure() {
    final RecordingListener listener = new RecordingListener();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener}, new StebzExtension[0]);
    executor.execute(RunnableStep.of("step", () -> { }));
    try {
      executor.execute(failingStep("fail"));
    } catch (final IllegalStateException ignored) {
    }
    assertThat(listener.events).containsExactly(
      "start:step", "success:step",
      "start:step", "failure:step:fail");
  }

  @Test
  void listenerShouldSeeNullResultOfResultlessStepAsEmpty() {
    final RecordingListener listener = new RecordingListener();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener}, new StebzExtension[0]);
    executor.execute(RunnableStep.of("step", () -> { }));
    assertThat(listener.events).containsExactly("start:step", "success:step");
  }

  @Test
  void listenerShouldSeeResultOfSupplierStep() {
    final RecordingListener listener = new RecordingListener();
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener}, new StebzExtension[0]);
    executor.execute(SupplierStep.of("step", () -> "result"));
    assertThat(listener.events).containsExactly("start:step", "success:step:result");
  }

  @Test
  void listenersAndExtensionsShouldBeCalledInGivenOrder() {
    final RecordingListener listener1 = new RecordingListener();
    final RecordingListener listener2 = new RecordingListener();
    final RecordingExtension extension = new RecordingExtension();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener1, listener2}, new StebzExtension[]{extension});
    executor.execute(RunnableStep.of("step", () -> { }));
    assertThat(extension.events).containsExactly(
      "beforeStart:step", "afterStart:step", "beforeSuccess:step", "afterSuccess:step");
    assertThat(listener1.events).containsExactly("start:step", "success:step");
    assertThat(listener2.events).containsExactly("start:step", "success:step");
  }

  @Test
  void interceptContextExtensionShouldReplaceContext() {
    final List<Object> received = new ArrayList<>();
    final InterceptStepContext extension = new InterceptStepContext() {
      @Override
      public Object interceptStepContext(final StepObj<?> step, final Object context) {
        return context + ":intercepted";
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0],
      new StebzExtension[]{extension});
    executor.execute(new RunnableStep.Of(StepAttributes.empty(), "ctx",
      (ThrowingConsumer<Object, Error>) received::add));
    assertThat(received).containsExactly("ctx:intercepted");
  }

  @Test
  void interceptStepExtensionShouldReplaceStep() {
    final RecordingListener listener = new RecordingListener();
    final InterceptStep extension = new InterceptStep() {
      @Override
      public StepObj<?> interceptStep(final StepObj<?> step, final NullableOptional<Object> context) {
        return named((RunnableStep) step, "replaced");
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener},
      new StebzExtension[]{extension});
    executor.execute(RunnableStep.of("step", () -> { }));
    assertThat(listener.events).containsExactly("start:replaced", "success:replaced");
  }

  @Test
  void interceptResultExtensionShouldReplaceResult() {
    final InterceptStepResult extension = new InterceptStepResult() {
      @Override
      public Object interceptStepResult(final StepObj<?> step,
                                        final NullableOptional<Object> context,
                                        final Object result) {
        return "intercepted:" + result;
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0],
      new StebzExtension[]{extension});
    assertThat(executor.execute(SupplierStep.of("step", () -> "result")))
      .isEqualTo("intercepted:result");
  }

  @Test
  void interceptExceptionExtensionShouldReplaceException() {
    final InterceptStepException extension = new InterceptStepException() {
      @Override
      public Throwable interceptStepException(final StepObj<?> step,
                                              final NullableOptional<Object> context,
                                              final Throwable exception) {
        return new RuntimeException("replaced", exception);
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[0],
      new StebzExtension[]{extension});
    assertThatThrownBy(() -> executor.execute(failingStep("origin")))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("replaced")
      .hasCauseInstanceOf(IllegalStateException.class);
  }

  @Test
  void failureExtensionsShouldBeCalledBeforeAndAfterListenerOnFailure() {
    final RecordingFailureExtension extension = new RecordingFailureExtension();
    final RecordingListener listener = new RecordingListener();
    final StepExecutor executor = new StepExecutor.Of(
      new StepListener[]{listener},
      new StebzExtension[]{extension}
    );

    assertThatThrownBy(() -> executor.execute(failingStep("fail")))
      .isInstanceOf(IllegalStateException.class);

    assertThat(extension.events).containsExactly("beforeFailure:step", "afterFailure:step");
    assertThat(listener.events).containsExactly("start:step", "failure:step:fail");
  }

  @Test
  void hiddenExceptionShouldBeLoggedAsSuccess() {
    final RecordingListener listener = new RecordingListener();
    final RecordingFailureExtension failureExtension = new RecordingFailureExtension();
    final InterceptStepException extension = new InterceptStepException() {
      @Override
      public boolean hiddenStepException(final StepObj<?> step,
                                         final NullableOptional<Object> context,
                                         final Throwable exception,
                                         final boolean currentState) {
        return true;
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener},
      new StebzExtension[]{extension, failureExtension});
    assertThatThrownBy(() -> executor.execute(failingStep("fail")))
      .isInstanceOf(IllegalStateException.class);
    assertThat(listener.events).containsExactly("start:step", "success:step");
    assertThat(failureExtension.events).isEmpty();
  }

  @Test
  void notThrownExceptionShouldBeSwallowed() {
    final RecordingListener listener = new RecordingListener();
    final InterceptStepException extension = new InterceptStepException() {
      @Override
      public boolean thrownStepException(final StepObj<?> step,
                                         final NullableOptional<Object> context,
                                         final Throwable exception,
                                         final boolean currentState) {
        return false;
      }
    };
    final StepExecutor executor = new StepExecutor.Of(new StepListener[]{listener},
      new StebzExtension[]{extension});
    assertThatCode(() -> executor.execute(failingStep("fail"))).doesNotThrowAnyException();
    assertThat(listener.events).containsExactly("start:step", "failure:step:fail");
  }

  @Test
  void getShouldReturnMainStepExecutor() {
    assertThat(StepExecutor.get()).isNotNull();
  }
}
