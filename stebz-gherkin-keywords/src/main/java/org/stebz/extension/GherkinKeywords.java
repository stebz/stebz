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
 * Stebz Gherkin keywords extension.
 */
public final class GherkinKeywords implements StebzExtension {
  private static final Object LOCK = new Object();
  private static final String BACKGROUND_KEYWORD_DEFAULT_VALUE = "Background";
  private static final String CONCLUSION_KEYWORD_DEFAULT_VALUE = "Conclusion";
  private static final String GIVEN_KEYWORD_DEFAULT_VALUE = "Given";
  private static final String WHEN_KEYWORD_DEFAULT_VALUE = "When";
  private static final String THEN_KEYWORD_DEFAULT_VALUE = "Then";
  private static final String AND_KEYWORD_DEFAULT_VALUE = "And";
  private static final String BUT_KEYWORD_DEFAULT_VALUE = "But";
  private static volatile Keywords keywords = null;

  /**
   * Ctor.
   */
  public GherkinKeywords() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  private GherkinKeywords(final PropertiesReader properties) {
    setKeywords(new Keywords(
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.background", BACKGROUND_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.conclusion", CONCLUSION_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.given", GIVEN_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.when", WHEN_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.then", THEN_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.and", AND_KEYWORD_DEFAULT_VALUE)),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.but", BUT_KEYWORD_DEFAULT_VALUE))
    ));
  }

  /**
   * Returns "Background" keyword.
   *
   * @return "Background" keyword
   */
  public static Keyword background() {
    return getKeywords().background;
  }

  /**
   * Returns "Conclusion" keyword.
   *
   * @return "Conclusion" keyword
   */
  public static Keyword conclusion() {
    return getKeywords().conclusion;
  }

  /**
   * Returns "Given" keyword.
   *
   * @return "Given" keyword
   */
  public static Keyword given() {
    return getKeywords().given;
  }

  /**
   * Returns "When" keyword.
   *
   * @return "When" keyword
   */
  public static Keyword when() {
    return getKeywords().when;
  }

  /**
   * Returns "Then" keyword.
   *
   * @return "Then" keyword
   */
  public static Keyword then() {
    return getKeywords().then;
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
   * Returns "But" keyword.
   *
   * @return "But" keyword
   */
  public static Keyword but() {
    return getKeywords().but;
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
      new Keyword.Of(BACKGROUND_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(CONCLUSION_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(GIVEN_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(WHEN_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(THEN_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(AND_KEYWORD_DEFAULT_VALUE),
      new Keyword.Of(BUT_KEYWORD_DEFAULT_VALUE)
    );
  }

  private static final class Keywords {
    private final Keyword background;
    private final Keyword conclusion;
    private final Keyword given;
    private final Keyword when;
    private final Keyword then;
    private final Keyword and;
    private final Keyword but;

    private Keywords(final Keyword background,
                     final Keyword conclusion,
                     final Keyword given,
                     final Keyword when,
                     final Keyword then,
                     final Keyword and,
                     final Keyword but) {
      this.background = background;
      this.conclusion = conclusion;
      this.given = given;
      this.when = when;
      this.then = then;
      this.and = and;
      this.but = but;
    }
  }
}
