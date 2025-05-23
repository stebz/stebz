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
package org.stebz.executor;

import org.stebz.util.property.PropertiesReader;

/**
 * Storage for {@code PropertiesReader}, which is only available at the time of the Stebz framework initialization.
 */
public final class StartupPropertiesReader {
  private static volatile PropertiesReader instance = null;

  /**
   * Utility class ctor.
   */
  private StartupPropertiesReader() {
  }

  /**
   * Returns Stebz framework initialization {@code PropertiesReader}.
   *
   * @return Stebz framework initialization {@code PropertiesReader}
   * @throws IllegalStateException if the Stebz framework startup process has not started or has been completed
   */
  public static PropertiesReader get() {
    final PropertiesReader propertiesReader = instance;
    if (propertiesReader == null) {
      throw new IllegalStateException("The Stebz framework startup process has not started or has been completed");
    }
    return propertiesReader;
  }

  static void set(final PropertiesReader propertiesReader) {
    instance = propertiesReader;
  }

  static void clear() {
    instance = null;
  }
}
