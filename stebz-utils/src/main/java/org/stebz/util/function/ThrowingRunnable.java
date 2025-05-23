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
package org.stebz.util.function;

/**
 * The {@link Runnable} specialization that might throw an exception.
 *
 * @param <E> the type of the throwing exception
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

  /**
   * Performs this operation.
   *
   * @throws E if runnable threw exception
   */
  void run() throws E;

  /**
   * Returns given runnable.
   *
   * @param runnable the runnable
   * @param <E>      the type of the throwing exception
   * @return runnable
   * @throws NullPointerException if {@code runnable} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static <E extends Throwable> ThrowingRunnable<E> of(final ThrowingRunnable<? extends E> runnable) {
    if (runnable == null) { throw new NullPointerException("runnable arg is null"); }
    return (ThrowingRunnable<E>) runnable;
  }

  /**
   * Returns given runnable as an unchecked runnable.
   *
   * @param origin the origin runnable
   * @return unchecked runnable
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  @SuppressWarnings("unchecked")
  static ThrowingRunnable<Error> unchecked(final ThrowingRunnable<?> origin) {
    if (origin == null) { throw new NullPointerException("origin arg is null"); }
    return (ThrowingRunnable<Error>) origin;
  }

  /**
   * Returns runnable which executes only once.
   *
   * @param origin the origin runnable
   * @return runnable which executes only once
   * @throws NullPointerException if {@code origin} arg is {@code null}
   */
  static RunningOnce runningOnce(final ThrowingRunnable<?> origin) {
    return new RunningOnce(origin);
  }

  /**
   * Returns runnable which executes only once.
   *
   * @param lock   the lock
   * @param origin the origin runnable
   * @return runnable which executes only once
   * @throws NullPointerException if {@code lock} or {@code origin} arg is {@code null}
   */
  static RunningOnce runningOnce(final Object lock,
                                 final ThrowingRunnable<?> origin) {
    return new RunningOnce(lock, origin);
  }

  /**
   * ThrowingRunnable which is executes only once.
   */
  class RunningOnce implements ThrowingRunnable<Error> {
    private final Object lock;
    private volatile ThrowingRunnable<?> initializer;
    private volatile boolean isInitialized;

    /**
     * Ctor.
     *
     * @param origin the origin runnable
     * @throws NullPointerException if {@code origin} arg is {@code null}
     */
    public RunningOnce(final ThrowingRunnable<?> origin) {
      if (origin == null) { throw new NullPointerException("origin arg is null"); }
      this.lock = this;
      this.initializer = origin;
      this.isInitialized = false;
    }

    /**
     * Ctor.
     *
     * @param lock   the lock
     * @param origin the origin runnable
     * @throws NullPointerException if {@code lock} or {@code origin} arg is {@code null}
     */
    public RunningOnce(final Object lock,
                       final ThrowingRunnable<?> origin) {
      if (lock == null) { throw new NullPointerException("lock arg is null"); }
      if (origin == null) { throw new NullPointerException("origin arg is null"); }
      this.lock = lock;
      this.initializer = origin;
      this.isInitialized = false;
    }

    /**
     * Returns {@code true} if this runnable has been already run, and {@code false} otherwise.
     *
     * @return {@code true} if this runnable has been already run, and {@code false} otherwise
     */
    public boolean hasRun() {
      return this.isInitialized;
    }

    @Override
    public void run() {
      if (this.isInitialized) {
        return;
      }
      synchronized (this.lock) {
        if (this.isInitialized) {
          return;
        }
        ThrowingRunnable.unchecked(this.initializer).run();
        this.isInitialized = true;
        this.initializer = null;
      }
    }
  }
}
