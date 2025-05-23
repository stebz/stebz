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
package org.stebz.attribute;

/**
 * Keyword attribute value.
 */
public interface Keyword {

  /**
   * Returns keyword value.
   *
   * @return keyword value
   */
  String value();

  /**
   * Indicates whether some other object is "equal to" this one. Implementations of the {@link Keyword} should have the
   * same contract.
   *
   * @return true if this object is the same as the {@code obj} argument, false otherwise
   * @see Of#equals(Object)
   */
  @Override
  boolean equals(Object obj);

  /**
   * Returns a hash code value for the object. Implementations of the {@link Keyword} should have the same contract.
   *
   * @return a hash code value for this object
   * @see Of#hashCode()
   */
  @Override
  int hashCode();

  /**
   * Returns empty keyword.
   *
   * @return empty keyword
   */
  static Keyword empty() {
    return Of.EMPTY;
  }

  /**
   * Returns keyword of given value.
   *
   * @param value the keyword value
   * @return keyword of given value
   * @throws NullPointerException if {@code value} arg is null
   */
  static Keyword of(final String value) {
    return new Keyword.Of(value);
  }

  /**
   * Default {@code Keyword} implementation.
   */
  class Of implements Keyword {
    private static final Of EMPTY = new Of("");
    private final String value;

    /**
     * Ctor.
     *
     * @param value the keyword value
     * @throws NullPointerException if {@code value} arg is null
     */
    public Of(final String value) {
      if (value == null) { throw new NullPointerException("value arg is null"); }
      this.value = value;
    }

    @Override
    public final String value() {
      return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Keyword) {
        return this.value.equals(((Keyword) obj).value());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.value.hashCode();
    }

    @Override
    public String toString() {
      return "Keyword[" + this.value + "]";
    }
  }
}
