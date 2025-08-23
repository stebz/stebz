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
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.feature", "Feature")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.rule", "Rule")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.scenario", "Scenario")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.scenarioOutline", "Scenario Outline")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.examples", "Examples")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.background", "Background")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.given", "Given")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.when", "When")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.then", "Then")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.and", "And")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.but", "But")),
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.asterisk", "*"))
    ));
  }

  /**
   * Returns "Feature" keyword.
   *
   * @return "Feature" keyword
   */
  public static Keyword feature() {
    return getKeywords().feature;
  }

  /**
   * Returns "Rule" keyword.
   *
   * @return "Rule" keyword
   */
  public static Keyword rule() {
    return getKeywords().rule;
  }

  /**
   * Returns "Scenario" keyword.
   *
   * @return "Scenario" keyword
   */
  public static Keyword scenario() {
    return getKeywords().scenario;
  }

  /**
   * Returns "Scenario outline" keyword.
   *
   * @return "Scenario outline" keyword
   */
  public static Keyword scenarioOutline() {
    return getKeywords().scenarioOutline;
  }

  /**
   * Returns "Examples" keyword.
   *
   * @return "Examples" keyword
   */
  public static Keyword examples() {
    return getKeywords().examples;
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

  /**
   * Returns "Asterisk" keyword.
   *
   * @return "Asterisk" keyword
   */
  public static Keyword asterisk() {
    return getKeywords().asterisk;
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
      new Keyword.Of("Feature"), new Keyword.Of("Rule"), new Keyword.Of("Scenario"),
      new Keyword.Of("Scenario Outline"), new Keyword.Of("Examples"), new Keyword.Of("Background"),
      new Keyword.Of("Given"), new Keyword.Of("When"), new Keyword.Of("Then"),
      new Keyword.Of("And"), new Keyword.Of("But"), new Keyword.Of("*")
    );
  }

  private static final class Keywords {
    private final Keyword feature;
    private final Keyword rule;
    private final Keyword scenario;
    private final Keyword scenarioOutline;
    private final Keyword examples;
    private final Keyword background;
    private final Keyword given;
    private final Keyword when;
    private final Keyword then;
    private final Keyword and;
    private final Keyword but;
    private final Keyword asterisk;

    private Keywords(final Keyword feature,
                     final Keyword rule,
                     final Keyword scenario,
                     final Keyword scenarioOutline,
                     final Keyword examples,
                     final Keyword background,
                     final Keyword given,
                     final Keyword when,
                     final Keyword then,
                     final Keyword and,
                     final Keyword but,
                     final Keyword asterisk) {
      this.feature = feature;
      this.rule = rule;
      this.scenario = scenario;
      this.scenarioOutline = scenarioOutline;
      this.examples = examples;
      this.background = background;
      this.given = given;
      this.when = when;
      this.then = then;
      this.and = and;
      this.but = but;
      this.asterisk = asterisk;
    }
  }
}
