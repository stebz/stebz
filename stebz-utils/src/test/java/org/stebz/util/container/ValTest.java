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
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Val}.
 */
final class ValTest {

  @Test
  void ofStaticMethodShouldReturnPresentVal() {
    final Object value = new Object();
    final Val<Object> val = Val.of(value);

    assertThat(val.isPresent())
      .isTrue();
    assertThat(val.get())
      .isSameAs(value);
  }

  @Test
  void ofStaticMethodShouldPresentNullValue() {
    final Val<Object> val = Val.of(null);

    assertThat(val.isPresent())
      .isTrue();
    assertThat(val.get())
      .isNull();
  }

  @Test
  void emptyStaticMethodShouldReturnEmptyVal() {
    final Val<Object> val = Val.empty();

    assertThat(val.isEmpty())
      .isTrue();
    assertThatThrownBy(val::get)
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void setMethodShouldSetAndReturnValue() {
    final Object value = new Object();
    final Val<Object> val = Val.empty();

    assertThat(val.set(value))
      .isSameAs(value);
    assertThat(val.get())
      .isSameAs(value);
    assertThat(val.isPresent())
      .isTrue();
  }

  @Test
  void setMethodShouldSetNullValue() {
    final Val<Object> val = Val.empty();

    val.set(null);

    assertThat(val.isPresent())
      .isTrue();
    assertThat(val.get())
      .isNull();
  }

  @Test
  void setMethodShouldThrowExceptionIfValueAlreadySet() {
    final Val<Object> val = Val.of(new Object());

    assertThatThrownBy(() -> val.set(new Object()))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void setMethodShouldThrowExceptionOnSecondCall() {
    final Val<Object> val = Val.empty();
    val.set(new Object());

    assertThatThrownBy(() -> val.set(new Object()))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void setMethodShouldSetAtMostOnceFromMultipleThreads() throws Exception {
    final Val<Integer> val = Val.empty();
    final ExecutorService executor = Executors.newFixedThreadPool(2);
    try {
      final CyclicBarrier barrier = new CyclicBarrier(2);
      final Future<Integer> first = executor.submit(() -> {
        barrier.await();
        return val.set(1);
      });
      final Future<Integer> second = executor.submit(() -> {
        barrier.await();
        return val.set(2);
      });

      int successes = 0;
      for (final Future<Integer> future : Arrays.asList(first, second)) {
        try {
          future.get();
          successes = successes + 1;
        } catch (final ExecutionException ex) {
          assertThat(ex.getCause())
            .isInstanceOf(IllegalStateException.class);
        }
      }
      assertThat(successes)
        .isEqualTo(1);
    } finally {
      executor.shutdownNow();
    }
    assertThat(val.get())
      .isIn(1, 2);
  }

  @Test
  void ifPresentMethodShouldInvokeConsumerWithPresentValue() {
    final Object value = new Object();
    final List<Object> captured = new ArrayList<>();
    final Val<Object> val = Val.of(value);

    final Val<Object> result = val.ifPresent(captured::add);

    assertThat(result)
      .isSameAs(val);
    assertThat(captured)
      .containsExactly(value);
  }

  @Test
  void ifPresentMethodShouldNotInvokeConsumerForEmptyVal() {
    final Val<Object> val = Val.empty();
    final AtomicInteger count = new AtomicInteger();

    final Val<Object> result = val.ifPresent(v -> count.incrementAndGet());

    assertThat(result)
      .isSameAs(val);
    assertThat(count.get())
      .isZero();
  }

  @Test
  void ifPresentMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().ifPresent(null));
  }

  @Test
  void ifEmptyMethodShouldInvokeRunnableForEmptyVal() {
    final Val<Object> val = Val.empty();
    final AtomicInteger count = new AtomicInteger();

    final Val<Object> result = val.ifEmpty(count::incrementAndGet);

    assertThat(result)
      .isSameAs(val);
    assertThat(count.get())
      .isEqualTo(1);
  }

  @Test
  void ifEmptyMethodShouldNotInvokeRunnableForPresentVal() {
    final Val<Object> val = Val.of(new Object());
    final AtomicInteger count = new AtomicInteger();

    final Val<Object> result = val.ifEmpty(count::incrementAndGet);

    assertThat(result)
      .isSameAs(val);
    assertThat(count.get())
      .isZero();
  }

  @Test
  void ifEmptyMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().ifEmpty(null));
  }

  @Test
  void foldMethodShouldReturnIfPresentFunctionResult() {
    final Object value = new Object();

    assertThat(Val.of(value).fold(v -> v, Object::new))
      .isSameAs(value);
  }

  @Test
  void foldMethodShouldReturnIfEmptySupplierResult() {
    final Object other = new Object();

    assertThat(Val.empty().fold(v -> v, () -> other))
      .isSameAs(other);
  }

  @Test
  void foldMethodShouldThrowNullPointerExceptionForNullArgs() {
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().fold(null, Object::new));
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().fold(v -> new Object(), null));
  }

  @Test
  void orElseMethodShouldReturnValueForPresentVal() {
    final Object value = new Object();
    final Object other = new Object();

    assertThat(Val.of(value).orElse(other))
      .isSameAs(value);
  }

  @Test
  void orElseMethodShouldReturnOtherValueForEmptyVal() {
    final Object other = new Object();

    assertThat(Val.empty().orElse(other))
      .isSameAs(other);
  }

  @Test
  void orElseGetMethodShouldReturnValueWithoutSupplierInvocationForPresentVal() {
    final Object value = new Object();

    assertThat(Val.of(value).orElseGet(() -> {
      throw new IllegalStateException("supplier should not be called");
    })).isSameAs(value);
  }

  @Test
  void orElseGetMethodShouldReturnSupplierResultForEmptyVal() {
    final Object other = new Object();

    assertThat(Val.empty().orElseGet(() -> other))
      .isSameAs(other);
  }

  @Test
  void orElseGetMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().orElseGet(null));
  }

  @Test
  void orElseThrowMethodShouldReturnValueForPresentVal() {
    final Object value = new Object();

    assertThat(Val.of(value).orElseThrow())
      .isSameAs(value);
  }

  @Test
  void orElseThrowMethodShouldThrowExceptionForEmptyVal() {
    assertThatThrownBy(() -> Val.empty().orElseThrow())
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void orElseThrowMethodShouldReturnValueForPresentValWithExceptionSupplier() {
    final Object value = new Object();

    assertThat(Val.of(value).orElseThrow(() -> {
      throw new IllegalStateException("supplier should not be called");
    })).isSameAs(value);
  }

  @Test
  void orElseThrowMethodShouldThrowSuppliedExceptionForEmptyVal() {
    final Throwable exception = new Exception();

    assertThatThrownBy(() -> Val.empty().orElseThrow(() -> exception))
      .isSameAs(exception);
  }

  @Test
  void orElseThrowMethodShouldThrowNullPointerExceptionForNullArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> Val.empty().orElseThrow(null));
  }

  @Test
  void equalsMethodShouldFollowContract() {
    assertThat(Val.of(1))
      .isEqualTo(Val.of(1));
    assertThat(Val.of(null))
      .isEqualTo(Val.of(null));
    assertThat(Val.empty())
      .isEqualTo(Val.empty());
    assertThat(Val.empty())
      .isEqualTo(new Val.Of<>());
    assertThat(Val.of(1))
      .isNotEqualTo(Val.of(2));
    assertThat(Val.of(1))
      .isNotEqualTo(Val.empty());
    assertThat(Val.empty())
      .isNotEqualTo(Val.of(null));
    assertThat(Val.of(1))
      .isNotEqualTo(null);
    assertThat(Val.of(1))
      .isNotEqualTo(new Object());
  }

  @Test
  void hashCodeMethodShouldFollowContract() {
    assertThat(Val.of(1))
      .hasSameHashCodeAs(Val.of(1));
    assertThat(Val.empty())
      .hasSameHashCodeAs(Val.empty());
  }

  @Test
  void toStringMethodShouldReturnDescription() {
    assertThat(Val.of(1).toString())
      .hasToString("Val[1]");
    assertThat(Val.empty().toString())
      .hasToString("Val[<no value>]");
  }
}
