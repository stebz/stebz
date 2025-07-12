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
 * Utility class. Contains single {@link #unsafe(Object)} method.
 */
public final class Cast {

  /**
   * Utility class ctor.
   */
  private Cast() {
  }

  /**
   * Returns given object of type {@code R}.
   *
   * <pre>{@code
   * Object value = 1;
   * Integer intValue = Cast.unsafe(value);
   * Long longValue = Cast.<Integer>unsafe(value).longValue();
   * }</pre>
   *
   * @param obj the object
   * @param <R> return object type
   * @return given object of type {@code R}
   * @throws ClassCastException if given object is not an instance of type {@code R}
   */
  @SuppressWarnings("unchecked")
  public static <R> R unsafe(final Object obj) {
    return (R) obj;
  }
}
