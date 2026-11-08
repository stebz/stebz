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
package org.stebz.hiddensteps;

import dev.jlet.function.ThrowingConsumer;
import dev.jlet.function.ThrowingFunction;
import dev.jlet.function.ThrowingRunnable;
import dev.jlet.function.ThrowingSupplier;
import org.junit.jupiter.api.Test;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.stebz.hiddensteps.HiddenStepsExtension.HiddenSteps;
import static org.stebz.hiddensteps.HiddenStepsExtension.HiddenStepsResult;
import static org.stebz.hiddensteps.HiddenStepsExtension.hiddenSteps;
import static org.stebz.hiddensteps.HiddenStepsExtension.hiddenStepsResult;

/**
 * Tests for {@link HiddenStepsExtension}.
 */
final class HiddenStepsExtensionTest {

  private static HiddenStepsExtension newExtension(final Map<String, String> properties) {
    return new HiddenStepsExtension(new PropertiesReader.Of(properties));
  }

  private static HiddenStepsExtension newExtensionWithProperty(final String name,
                                                               final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static StepObj<?> intercept(final HiddenStepsExtension extension,
                                      final StepObj<?> step) {
    return extension.interceptStep(step, NullableOptional.empty());
  }

  private static void assertThatThreadLocalsAreCleared() throws Exception {
    final Field depthField = HiddenStepsExtension.class.getDeclaredField("THREAD_LOCAL_DEPTH");
    depthField.setAccessible(true);
    assertThat(((ThreadLocal<Integer>) depthField.get(null)).get())
      .isNull();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.hiddenSteps.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void interceptStepShouldNotHideStepOutsideBlock() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final HiddenStepsExtension extension = newExtension(new HashMap<>());

    final StepObj<?> resultStep = intercept(extension, step);

    assertThat(resultStep.isHidden())
      .isFalse();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldHideStepInsideBlock() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final HiddenStepsExtension extension = newExtension(new HashMap<>());

    hiddenSteps(() -> {
      assertThat(intercept(extension, step).isHidden())
        .isTrue();
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldNotHideStepInsideBlockWhenExtensionDisabled() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final HiddenStepsExtension extension = newExtensionWithProperty("stebz.hiddenSteps.enabled", "false");

    hiddenSteps(() -> {
      assertThat(intercept(extension, step).isHidden())
        .isFalse();
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void interceptStepShouldReturnAlreadyHiddenStepUnchanged() throws Exception {
    final StepObj<?> hiddenStep = RunnableStep.empty().withHiding();
    final HiddenStepsExtension extension = newExtension(new HashMap<>());

    hiddenSteps(() -> {
      assertThat(intercept(extension, hiddenStep))
        .isSameAs(hiddenStep);
    });
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsShouldExecuteBlock() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    hiddenSteps(() -> executed.set(true));

    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    hiddenSteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsResultShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(hiddenStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsResultShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(hiddenStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsShouldKeepHidingStepsOfEnclosingBlockAfterNestedBlock() throws Exception {
    final RunnableStep step = RunnableStep.empty();
    final HiddenStepsExtension extension = newExtension(new HashMap<>());

    hiddenSteps(() -> {
      hiddenSteps(() -> {
        assertThat(intercept(extension, step).isHidden())
          .isTrue();
      });
      assertThat(intercept(extension, step).isHidden())
        .isTrue();
    });
    assertThat(intercept(extension, step).isHidden())
      .isFalse();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsAliasShouldExecuteBlock() throws Exception {
    final AtomicBoolean executed = new AtomicBoolean(false);

    HiddenSteps(() -> executed.set(true));

    assertThat(executed).isTrue();
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsAliasShouldPassAdditionalValueToBlock() throws Exception {
    final Object value = new Object();
    final AtomicReference<Object> consumedValue = new AtomicReference<>();

    HiddenSteps(value, (ThrowingConsumer<Object, ?>) consumedValue::set);

    assertThat(consumedValue.get()).isSameAs(value);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsResultAliasShouldReturnBlockResult() throws Exception {
    final Object result = new Object();

    assertThat(HiddenStepsResult((ThrowingSupplier<Object, ?>) () -> result))
      .isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsResultAliasShouldPassAdditionalValueToBlockAndReturnBlockResult() throws Exception {
    final Object value = new Object();
    final Object result = new Object();

    assertThat(HiddenStepsResult(value, (ThrowingFunction<Object, Object, ?>) v -> {
      assertThat(v).isSameAs(value);
      return result;
    })).isSameAs(result);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsShouldPropagateBlockExceptionAndClearThreadLocals() throws Exception {
    final Throwable exception = new IllegalStateException("block exception");

    assertThatThrownBy(() ->
      hiddenSteps((ThrowingRunnable<?>) () -> {
        throw exception;
      })
    ).isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsResultShouldPropagateBlockExceptionAndClearThreadLocals() throws Exception {
    final Throwable exception = new IllegalStateException("block exception");

    assertThatThrownBy(() ->
      hiddenStepsResult((ThrowingSupplier<Object, ?>) () -> {
        throw exception;
      })
    ).isSameAs(exception);
    assertThatThreadLocalsAreCleared();
  }

  @Test
  void hiddenStepsMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> hiddenSteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> hiddenSteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> hiddenStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> hiddenStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void hiddenStepsAliasMethodsShouldThrowNpeOnNullStepsArg() {
    final Object value = new Object();

    assertThatThrownBy(() -> HiddenSteps((ThrowingRunnable<?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> HiddenSteps(value, (ThrowingConsumer<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> HiddenStepsResult((ThrowingSupplier<Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> HiddenStepsResult(value, (ThrowingFunction<Object, Object, ?>) null))
      .isInstanceOf(NullPointerException.class);
  }
}
