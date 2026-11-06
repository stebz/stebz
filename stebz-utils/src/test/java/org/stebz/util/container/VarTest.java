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
package org.stebz.util.container;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Var}.
 */
final class VarTest {

  @Test
  void ofStaticMethodShouldReturnPresentVar() {
    final Object value = new Object();
    final Var<Object> var = Var.of(value);

    assertThat(var.isPresent())
      .isTrue();
    assertThat(var.get())
      .isSameAs(value);
  }

  @Test
  void ofStaticMethodShouldPresentNullValue() {
    final Var<Object> var = Var.of(null);

    assertThat(var.isPresent())
      .isTrue();
    assertThat(var.get())
      .isNull();
  }

  @Test
  void emptyStaticMethodShouldReturnEmptyVar() {
    final Var<Object> var = Var.empty();

    assertThat(var.isEmpty())
      .isTrue();
    assertThatThrownBy(var::get)
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void setMethodShouldSetAndReturnValue() {
    final Object value = new Object();
    final Var<Object> var = Var.empty();

    assertThat(var.set(value))
      .isSameAs(value);
    assertThat(var.get())
      .isSameAs(value);
    assertThat(var.isPresent())
      .isTrue();
  }

  @Test
  void setMethodShouldSetNullValue() {
    final Var<Object> var = Var.empty();

    var.set(null);

    assertThat(var.isPresent())
      .isTrue();
    assertThat(var.get())
      .isNull();
  }

  @Test
  void setMethodShouldOverwritePreviousValue() {
    final Object value = new Object();
    final Var<Object> var = Var.of(new Object());

    var.set(value);

    assertThat(var.get())
      .isSameAs(value);
  }

  @Test
  void clearMethodShouldRemoveValue() {
    final Var<Object> var = Var.of(new Object());

    var.clear();

    assertThat(var.isEmpty())
      .isTrue();
    assertThatThrownBy(var::get)
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void clearMethodShouldKeepEmptyVarEmpty() {
    final Var<Object> var = Var.empty();

    var.clear();

    assertThat(var.isEmpty())
      .isTrue();
  }

  @Test
  void takeMethodShouldThrowExceptionForEmptyVar() {
    assertThatThrownBy(() -> Var.empty().take())
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void takeMethodShouldReturnValueAndRemoveIt() {
    final Object value = new Object();
    final Var<Object> var = Var.of(value);

    assertThat(var.take())
      .isSameAs(value);
    assertThat(var.isEmpty())
      .isTrue();
    assertThatThrownBy(var::get)
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void ifPresentMethodShouldInvokeConsumerWithPresentValue() {
    final Object value = new Object();
    final List<Object> captured = new ArrayList<>();
    final Var<Object> var = Var.of(value);

    final Var<Object> result = var.ifPresent(captured::add);

    assertThat(result)
      .isSameAs(var);
    assertThat(captured)
      .containsExactly(value);
  }

  @Test
  void ifPresentMethodShouldNotInvokeConsumerForEmptyVar() {
    final Var<Object> var = Var.empty();
    final AtomicInteger count = new AtomicInteger();

    final Var<Object> result = var.ifPresent(v -> count.incrementAndGet());

    assertThat(result)
      .isSameAs(var);
    assertThat(count.get())
      .isZero();
  }

  @Test
  void ifPresentMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().ifPresent(null));
  }

  @Test
  void ifEmptyMethodShouldInvokeRunnableForEmptyVar() {
    final Var<Object> var = Var.empty();
    final AtomicInteger count = new AtomicInteger();

    final Var<Object> result = var.ifEmpty(count::incrementAndGet);

    assertThat(result)
      .isSameAs(var);
    assertThat(count.get())
      .isEqualTo(1);
  }

  @Test
  void ifEmptyMethodShouldNotInvokeRunnableForPresentVar() {
    final Var<Object> var = Var.of(new Object());
    final AtomicInteger count = new AtomicInteger();

    final Var<Object> result = var.ifEmpty(count::incrementAndGet);

    assertThat(result)
      .isSameAs(var);
    assertThat(count.get())
      .isZero();
  }

  @Test
  void ifEmptyMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().ifEmpty(null));
  }

  @Test
  void foldMethodShouldReturnIfPresentFunctionResult() {
    final Object value = new Object();

    assertThat(Var.of(value).fold(v -> v, Object::new))
      .isSameAs(value);
  }

  @Test
  void foldMethodShouldReturnIfEmptySupplierResult() {
    final Object other = new Object();

    assertThat(Var.empty().fold(v -> v, () -> other))
      .isSameAs(other);
  }

  @Test
  void foldMethodShouldThrowNullPointerExceptionForNullArgs() {
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().fold(null, Object::new));
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().fold(v -> new Object(), null));
  }

  @Test
  void orElseMethodShouldReturnValueForPresentVar() {
    final Object value = new Object();
    final Object other = new Object();

    assertThat(Var.of(value).orElse(other))
      .isSameAs(value);
  }

  @Test
  void orElseMethodShouldReturnOtherValueForEmptyVar() {
    final Object other = new Object();

    assertThat(Var.empty().orElse(other))
      .isSameAs(other);
  }

  @Test
  void orElseGetMethodShouldReturnValueWithoutSupplierInvocationForPresentVar() {
    final Object value = new Object();

    assertThat(Var.of(value).orElseGet(() -> {
      throw new IllegalStateException("supplier should not be called");
    })).isSameAs(value);
  }

  @Test
  void orElseGetMethodShouldReturnSupplierResultForEmptyVar() {
    final Object other = new Object();

    assertThat(Var.empty().orElseGet(() -> other))
      .isSameAs(other);
  }

  @Test
  void orElseGetMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().orElseGet(null));
  }

  @Test
  void orElseThrowMethodShouldReturnValueForPresentVar() {
    final Object value = new Object();

    assertThat(Var.of(value).orElseThrow())
      .isSameAs(value);
  }

  @Test
  void orElseThrowMethodShouldThrowExceptionForEmptyVar() {
    assertThatThrownBy(() -> Var.empty().orElseThrow())
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void orElseThrowMethodShouldReturnValueForPresentVarWithExceptionSupplier() {
    final Object value = new Object();

    assertThat(Var.of(value).orElseThrow(() -> {
      throw new IllegalStateException("supplier should not be called");
    })).isSameAs(value);
  }

  @Test
  void orElseThrowMethodShouldThrowSuppliedExceptionForEmptyVar() {
    final Throwable exception = new Exception();

    assertThatThrownBy(() -> Var.empty().orElseThrow(() -> exception))
      .isSameAs(exception);
  }

  @Test
  void orElseThrowMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Var.empty().orElseThrow(null));
  }

  @Test
  void equalsMethodShouldFollowContract() {
    assertThat(Var.of(1))
      .isEqualTo(Var.of(1));
    assertThat(Var.of(null))
      .isEqualTo(Var.of(null));
    assertThat(Var.empty())
      .isEqualTo(Var.empty());
    assertThat(Var.empty())
      .isEqualTo(new Var.Of<>());
    assertThat(Var.of(1))
      .isNotEqualTo(Var.of(2));
    assertThat(Var.of(1))
      .isNotEqualTo(Var.empty());
    assertThat(Var.empty())
      .isNotEqualTo(Var.of(null));
    assertThat(Var.of(1))
      .isNotEqualTo(null);
    assertThat(Var.of(1))
      .isNotEqualTo(new Object());
  }

  @Test
  void hashCodeMethodShouldFollowContract() {
    assertThat(Var.of(1))
      .hasSameHashCodeAs(Var.of(1));
    assertThat(Var.empty().hashCode())
      .hasSameHashCodeAs(Var.empty());
  }

  @Test
  void toStringMethodShouldReturnDescription() {
    assertThat(Var.of(1))
      .hasToString("Var[1]");
    assertThat(Var.empty())
      .hasToString("Var[<no value>]");
  }
}
