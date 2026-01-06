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
  private static final String SETUP_KEYWORD_DEFAULT_VALUE = "Setup:";
  private static final String TEARDOWN_KEYWORD_DEFAULT_VALUE = "Teardown:";
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
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.setup", SETUP_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.teardown", TEARDOWN_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.arrange", ARRANGE_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.act", ACT_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.extensions.aaa.keywords.assert", ASSERT_KEYWORD_DEFAULT_VALUE))
    ));
  }

  /**
   * Returns "Setup" keyword.
   *
   * @return "Setup" keyword
   */
  public static Keyword setup() {
    return getKeywords().setup;
  }

  /**
   * Returns "Teardown" keyword.
   *
   * @return "Teardown" keyword
   */
  public static Keyword teardown() {
    return getKeywords().teardown;
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
      new Keyword.Of(SETUP_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(TEARDOWN_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ARRANGE_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ACT_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(ASSERT_KEYWORD_DEFAULT_VALUE)
    );
  }

  private static final class Keywords {
    private final Keyword setup;
    private final Keyword teardown;
    private final Keyword arrange;
    private final Keyword act;
    private final Keyword _assert;

    private Keywords(final Keyword setup,
                     final Keyword teardown,
                     final Keyword arrange,
                     final Keyword act,
                     final Keyword _assert) {
      this.setup = setup;
      this.teardown = teardown;
      this.arrange = arrange;
      this.act = act;
      this._assert = _assert;
    }
  }
}
