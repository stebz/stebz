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
package org.stebz.util;

/**
 * Utility class. Contains single {@link #unchecked(Throwable)} method.
 */
public final class Throw {

  /**
   * Utility class ctor.
   */
  private Throw() {
  }

  /**
   * Throws given exception.
   *
   * <pre>{@code
   * Throw.unchecked(new Throwable());
   * throw unchecked(new Throwable());
   * }</pre>
   *
   * @param exception the exception
   * @param <E>       the type of the exception
   * @return fake RuntimeException syntax
   * @throws E given exception
   */
  @SuppressWarnings("unchecked")
  public static <E extends Throwable> RuntimeException unchecked(final Throwable exception) throws E {
    if (exception == null) { throw new NullPointerException("exception arg is null"); }
    throw (E) exception;
  }
}
