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
package org.stebz.emptysteps;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.ConsumerStep;
import org.stebz.core.step.executable.FunctionStep;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.SupplierStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.stebz.emptysteps.EmptyStepsExtension.EmptySteps;
import static org.stebz.emptysteps.EmptyStepsExtension.EmptyStepsResult;
import static org.stebz.emptysteps.EmptyStepsExtension.emptySteps;
import static org.stebz.emptysteps.EmptyStepsExtension.emptyStepsResult;

/**
 * Tests for {@link EmptyStepsExtension}.
 */
final class EmptyStepsExtensionTest {

  private static EmptyStepsExtension newExtension(final Map<String, String> properties) {
    return new EmptyStepsExtension(new PropertiesReader.Of(properties));
  }

  private static EmptyStepsExtension newExtensionWithProperty(final String name,
                                                              final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static StepObj<?> intercept(final EmptyStepsExtension extension,
                                      final StepObj<?> step) {
    return extension.interceptStep(step, NullableOptional.empty());
  }

  private static void assertThatThreadLocalsAreCleared() throws Exception {
    final Field depthField = EmptyStepsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    depthField.setAccessible(true);
    assertThat(((ThreadLocal<Integer>) depthField.get(null)).get())
      .isNull();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.LATE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.emptySteps.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void interceptStepShouldReturnRunnableStepUnchangedOutsideBlock() throws Exception {
    final ThrowingRunnable<?> body = () -> { };
    final RunnableStep step = RunnableStep.of(body);

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep).isSameAs(step);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReplaceRunnableStepBodyInsideBlock() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = newExtension(new HashMap<>());

    emptySteps(() -> {
      assertThat(((RunnableStep) intercept(extension, step)).getBody())
        .isSameAs(RunnableStep.emptyBody());
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReplaceConsumerStepBodyInsideBlock() throws Exception {
    final ConsumerStep<Object> step = ConsumerStep.of((ThrowingConsumer<Object, ?>) value -> { });
    final EmptyStepsExtension extension = newExtension(new HashMap<>());

    emptySteps(() -> {
      assertThat(((ConsumerStep<?>) intercept(extension, step)).getBody())
        .isSameAs(ConsumerStep.emptyBody());
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReplaceSupplierStepBodyInsideBlock() throws Exception {
    final SupplierStep<Object> step = SupplierStep.of(() -> new Object());
    final EmptyStepsExtension extension = newExtension(new HashMap<>());

    emptySteps(() -> {
      assertThat(((SupplierStep<?>) intercept(extension, step)).getBody())
        .isSameAs(SupplierStep.emptyBody());
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReplaceFunctionStepBodyInsideBlock() throws Exception {
    final FunctionStep<Object, Object> step = FunctionStep.of(value -> new Object());
    final EmptyStepsExtension extension = newExtension(new HashMap<>());

    emptySteps(() -> {
      assertThat(((FunctionStep<?, ?>) intercept(extension, step)).getBody())
        .isSameAs(FunctionStep.emptyBody());
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReplaceAllStepBodiesOutsideBlockWhenForEachPropertyEnabled() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = newExtensionWithProperty("stebz.emptySteps.forEach", "true");

    assertThat(((RunnableStep) intercept(extension, step)).getBody())
      .isSameAs(RunnableStep.emptyBody());
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReturnStepUnchangedOutsideBlockWhenForEachPropertyEnabledButExtensionDisabled()
    throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.emptySteps.enabled", "false");
    properties.put("stebz.emptySteps.forEach", "true");
    final EmptyStepsExtension extension = newExtension(properties);

    assertThat(intercept(extension, step))
      .isSameAs(step);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReturnStepUnchangedInsideBlockWhenExtensionDisabled() throws Exception {
    final RunnableStep step = RunnableStep.of(() -> { });
    final EmptyStepsExtension extension = newExtensionWithProperty("stebz.emptySteps.enabled", "false");

    emptySteps(() -> {
      assertThat(intercept(extension, step))
        .isSameAs(step);
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsShouldExecuteBlock() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    emptySteps(() -> executed.set(true));

    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    emptySteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsResultShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(emptyStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsResultShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(emptyStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsShouldKeepEmptyingStepsOfEnclosingBlockAfterNestedBlock() throws Exception {
    final ThrowingRunnable<?> body = () -> { };
    final RunnableStep step = RunnableStep.of(body);
    final EmptyStepsExtension extension = newExtension(new HashMap<>());

    emptySteps(() -> {
      emptySteps(() -> {
        assertThat(((RunnableStep) intercept(extension, step)).getBody())
          .isSameAs(RunnableStep.emptyBody());
      });
      assertThat(((RunnableStep) intercept(extension, step)).getBody())
        .isSameAs(RunnableStep.emptyBody());
    });
    assertThat(((RunnableStep) intercept(extension, step)).getBody())
      .isSameAs(body);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsAliasShouldExecuteBlock() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    EmptySteps(() -> executed.set(true));

    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsAliasShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    EmptySteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsResultAliasShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(EmptyStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsResultAliasShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(EmptyStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsShouldPropagateBlockExceptionAndClearThreadLocals() throws Exception {
    final Throwable exception = new IllegalStateException("block exception");

    assertThatThrownBy(() ->
      emptySteps((ThrowingRunnable<?>) () -> {
        throw exception;
      })
    ).isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsResultShouldPropagateBlockExceptionAndClearThreadLocals() throws Exception {
    final Throwable exception = new IllegalStateException("block exception");

    assertThatThrownBy(() ->
      emptyStepsResult((ThrowingSupplier<Object, ?>) () -> {
        throw exception;
      })
    ).isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void emptyStepsMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> emptySteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> emptySteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> emptyStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> emptyStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void emptyStepsAliasMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> EmptySteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> EmptySteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> EmptyStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> EmptyStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }
}
