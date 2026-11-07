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
package org.stebz.aaa.keyword;

import org.stebz.core.attribute.Keyword;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.extension.StebzExtension;
import org.stebz.util.property.PropertiesReader;

/**
 * Stebz Arrange-Act-Assert keywords extension.
 * <p>Keywords are initialized once with values of the following properties (a missing or blank property value is
 * treated as a missing one, the default value is used instead):
 * <ul>
 *   <li>{@code stebz.aaa.keyword.setUp} — default value: {@code Set Up:}</li>
 *   <li>{@code stebz.aaa.keyword.tearDown} — default value: {@code Tear Down:}</li>
 *   <li>{@code stebz.aaa.keyword.arrange} — default value: {@code Arrange:}</li>
 *   <li>{@code stebz.aaa.keyword.act} — default value: {@code Act:}</li>
 *   <li>{@code stebz.aaa.keyword.assert} — default value: {@code Assert:}</li>
 *   <li>{@code stebz.aaa.keyword.asterisk} — default value: {@code *}</li>
 * </ul>
 * <p>If a keyword accessor is called while the Stebz framework startup process is in progress, keyword values are read
 * from the startup properties even if this extension has not been instantiated yet.
 * <p>If this extension is not registered in the Stebz framework, keyword accessors throw {@code IllegalStateException}.
 */
public final class AAAKeywords implements StebzExtension {
  private static final Object LOCK = new Object();
  private static volatile Keywords keywords = null;

  /**
   * Ctor.
   *
   * @throws IllegalStateException if the Stebz framework startup process has not started yet or is already complete
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
    setKeywordsOnce(readKeywords(properties));
  }

  /**
   * Returns "Set up" keyword.
   *
   * @return "Set up" keyword
   */
  public static Keyword setUp() {
    return getKeywords().setUp;
  }

  /**
   * Returns "Tear down" keyword.
   *
   * @return "Tear down" keyword
   */
  public static Keyword tearDown() {
    return getKeywords().tearDown;
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
   * Returns "Act" keyword.
   *
   * @return "Act" keyword
   */
  public static Keyword act() {
    return getKeywords().act;
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
   * Returns "*" keyword.
   *
   * @return "*" keyword
   */
  public static Keyword asterisk() {
    return getKeywords().asterisk;
  }

  @Override
  public int order() {
    return EARLY_ORDER;
  }

  private static void setKeywordsOnce(final Keywords keywordsToSet) {
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
      final PropertiesReader startupProperties = StartupPropertiesReader.getOrNull();
      if (startupProperties != null) {
        setKeywordsOnce(readKeywords(startupProperties));
      }
      if (keywords == null) {
        StepExecutor.get();
      }
      if (keywords == null) {
        throw new IllegalStateException("AAAKeywords extension is not registered in the Stebz framework");
      }
    }
    return keywords;
  }

  private static Keywords readKeywords(final PropertiesReader properties) {
    return new Keywords(
      new Keyword.Of(properties.getString("stebz.aaa.keyword.setUp", "Set Up:")),
      new Keyword.Of(properties.getString("stebz.aaa.keyword.tearDown", "Tear Down:")),
      new Keyword.Of(properties.getString("stebz.aaa.keyword.arrange", "Arrange:")),
      new Keyword.Of(properties.getString("stebz.aaa.keyword.act", "Act:")),
      new Keyword.Of(properties.getString("stebz.aaa.keyword.assert", "Assert:")),
      new Keyword.Of(properties.getString("stebz.aaa.keyword.asterisk", "*"))
    );
  }

  private static final class Keywords {
    private final Keyword setUp;
    private final Keyword tearDown;
    private final Keyword arrange;
    private final Keyword act;
    private final Keyword _assert;
    private final Keyword asterisk;

    private Keywords(final Keyword setUp,
                     final Keyword tearDown,
                     final Keyword arrange,
                     final Keyword act,
                     final Keyword _assert,
                     final Keyword asterisk) {
      this.setUp = setUp;
      this.tearDown = tearDown;
      this.arrange = arrange;
      this.act = act;
      this._assert = _assert;
      this.asterisk = asterisk;
    }
  }
}
