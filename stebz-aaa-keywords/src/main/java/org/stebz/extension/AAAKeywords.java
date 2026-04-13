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
package org.stebz.extension;

import org.stebz.attribute.Keyword;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.executor.StepExecutor;
import org.stebz.util.property.PropertiesReader;

/**
 * Stebz Arrange-Act-Assert keywords extension.
 */
public final class AAAKeywords implements StebzExtension {
  private static final Object LOCK = new Object();
  private static final String SET_UP_KEYWORD_DEFAULT_VALUE = "Set Up:";
  private static final String TEAR_DOWN_KEYWORD_DEFAULT_VALUE = "Tear Down:";
  private static final String ARRANGE_KEYWORD_DEFAULT_VALUE = "Arrange:";
  private static final String ACT_KEYWORD_DEFAULT_VALUE = "Act:";
  private static final String ASSERT_KEYWORD_DEFAULT_VALUE = "Assert:";
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
    setKeywordsOnce(new Keywords(
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.setUp", SET_UP_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.tearDown", TEAR_DOWN_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.arrange", ARRANGE_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.act", ACT_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.assert", ASSERT_KEYWORD_DEFAULT_VALUE))
    ));
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
      new Keyword.Of(SET_UP_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(TEAR_DOWN_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ARRANGE_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ACT_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ASSERT_KEYWORD_DEFAULT_VALUE)
    );
  }

  private static final class Keywords {
    private final Keyword setUp;
    private final Keyword tearDown;
    private final Keyword arrange;
    private final Keyword act;
    private final Keyword _assert;

    private Keywords(final Keyword setUp,
                     final Keyword tearDown,
                     final Keyword arrange,
                     final Keyword act,
                     final Keyword _assert) {
      this.setUp = setUp;
      this.tearDown = tearDown;
      this.arrange = arrange;
      this.act = act;
      this._assert = _assert;
    }
  }
}
