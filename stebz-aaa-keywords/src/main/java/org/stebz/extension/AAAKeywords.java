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
package org.stebz.extension;

import org.stebz.attribute.Keyword;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.executor.StepExecutor;
import org.stebz.util.property.PropertiesReader;

/**
 * Stebz AAA keywords extension.
 */
public final class AAAKeywords implements StebzExtension {
  private static final Object LOCK = new Object();
  private static volatile Keywords keywords = null;

  /**
   * Ctor.
   */
  public AAAKeywords() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  private AAAKeywords(final PropertiesReader properties) {
    setKeywords(new Keywords(
      new Keyword.Of(properties.getString("stebz.aaa.keywords.act", "Act")),
      new Keyword.Of(properties.getString("stebz.aaa.keywords.and", "And")),
      new Keyword.Of(properties.getString("stebz.aaa.keywords.arrange", "Arrange")),
      new Keyword.Of(properties.getString("stebz.aaa.keywords.assert", "Assert")),
      new Keyword.Of(properties.getString("stebz.aaa.keywords.setup", "Setup"))
    ));
  }

  /**
   * Returns "Act" keyword.
   *
   * @return "Act" keyword
   */
  public static Keyword act() {
    return getKeywords().act;
  }

  /**
   * Returns "And" keyword.
   *
   * @return "And" keyword
   */
  public static Keyword and() {
    return getKeywords().and;
  }

  /**
   * Returns "Arrange" keyword.
   *
   * @return "Arrange" keyword
   */
  public static Keyword arrange() {
    return getKeywords().arrange;
  }

  /**
   * Returns "Assert" keyword.
   *
   * @return "Assert" keyword
   */
  public static Keyword _assert() {
    return getKeywords()._assert;
  }

  /**
   * Returns "Setup" keyword.
   *
   * @return "Setup" keyword
   */
  public static Keyword setup() {
    return getKeywords().setup;
  }

  private static void setKeywords(final Keywords keywordsToSet) {
    if (keywords == null) {
      synchronized (LOCK) {
        if (keywords == null) {
          keywords = keywordsToSet;
        }
      }
    }
  }

  private static Keywords getKeywords() {
    if (keywords == null) {
      StepExecutor.get();
      if (keywords == null) {
        synchronized (LOCK) {
          if (keywords == null) {
            final Keywords defaultKeywords = defaultKeywords();
            keywords = defaultKeywords;
            return defaultKeywords;
          }
        }
      }
    }
    return keywords;
  }

  private static Keywords defaultKeywords() {
    return new Keywords(
      new Keyword.Of("Act"), new Keyword.Of("And"), new Keyword.Of("Arrange"),
      new Keyword.Of("Assert"), new Keyword.Of("Setup")
    );
  }

  private static final class Keywords {
    private final Keyword act;
    private final Keyword and;
    private final Keyword arrange;
    private final Keyword _assert;
    private final Keyword setup;

    private Keywords(final Keyword act,
                     final Keyword and,
                     final Keyword arrange,
                     final Keyword _assert,
                     final Keyword setup) {
      this.act = act;
      this.and = and;
      this.arrange = arrange;
      this._assert = _assert;
      this.setup = setup;
    }
  }
}
