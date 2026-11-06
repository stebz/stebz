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
package org.stebz.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Cached}.
 */
final class CachedTest {

  @Test
  void getMethodShouldReturnOriginSupplierResult() {
    final Object value = new Object();
    final Cached<Object> cached = new Cached<>(() -> value);

    assertThat(cached.get())
      .isSameAs(value);
  }

  @Test
  void getMethodShouldCallOriginSupplierOnlyOnce() {
    final AtomicInteger count = new AtomicInteger();
    final Cached<Object> cached = new Cached<>(() -> {
      count.incrementAndGet();
      return new Object();
    });

    final Object firstValue = cached.get();
    final Object secondValue = cached.get();

    assertThat(secondValue)
      .isSameAs(firstValue);
    assertThat(count.get())
      .isEqualTo(1);
  }

  @Test
  void getMethodShouldInitializeValueInsideGivenLock() {
    final Object lock = new Object();
    final Cached<Object> cached = new Cached<>(lock, () -> {
      assertThat(Thread.holdsLock(lock))
        .isTrue();
      return new Object();
    });

    assertThat(cached.get())
      .isNotNull();
  }

  @Test
  void getMethodShouldPropagateOriginSupplierExceptionAndNotInitializeValue() {
    final Cached<Object> cached = new Cached<>(() -> {
      throw new IOException();
    });

    assertThatThrownBy(cached::get)
      .isInstanceOf(IOException.class);
    assertThat(cached.isInitialized())
      .isFalse();
  }

  @Test
  void getMethodShouldCallOriginSupplierAgainAfterExceptionOnPreviousCall() {
    final AtomicInteger count = new AtomicInteger();
    final Cached<Object> cached = new Cached<>(() -> {
      if (count.incrementAndGet() == 1) {
        throw new IOException();
      }
      return new Object();
    });

    assertThatThrownBy(cached::get)
      .isInstanceOf(IOException.class);
    assertThat(cached.get())
      .isNotNull();
    assertThat(count.get())
      .isEqualTo(2);
  }

  @Test
  void isInitializedMethodShouldReturnFalseBeforeInitializationAndTrueAfter() {
    final Cached<Object> cached = new Cached<>(Object::new);

    assertThat(cached.isInitialized())
      .isFalse();
    cached.get();
    assertThat(cached.isInitialized())
      .isTrue();
  }

  @Test
  void getMethodShouldInitializeValueOnlyOnceFromMultipleThreads() throws Exception {
    final int threadsCount = 8;
    final AtomicInteger count = new AtomicInteger();
    final Cached<Object> cached = new Cached<>(() -> {
      count.incrementAndGet();
      return new Object();
    });
    final ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
    try {
      final CyclicBarrier barrier = new CyclicBarrier(threadsCount);
      final List<Future<Object>> futures = new ArrayList<>();
      for (int idx = 0; idx < threadsCount; idx++) {
        futures.add(executor.submit(() -> {
          barrier.await();
          return cached.get();
        }));
      }

      final Object value = futures.get(0).get();
      for (final Future<Object> future : futures) {
        assertThat(future.get())
          .isSameAs(value);
      }
    } finally {
      executor.shutdownNow();
    }
    assertThat(count.get())
      .isEqualTo(1);
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullOriginArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> new Cached<>(null));
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullLockArg() {
    assertThatNullPointerException()
      .isThrownBy(() -> new Cached<>(null, Object::new));
  }

  @Test
  void ctorShouldThrowNullPointerExceptionForNullOriginArgWithLock() {
    assertThatNullPointerException()
      .isThrownBy(() -> new Cached<>(new Object(), null));
  }
}
