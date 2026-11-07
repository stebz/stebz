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
package org.stebz.gherkin.keyword;

import org.stebz.core.attribute.Keyword;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.executor.StepExecutor;
import org.stebz.core.extension.StebzExtension;
import org.stebz.util.property.PropertiesReader;

/**
 * Stebz Gherkin keywords extension.
 * <p>Keywords are initialized once with values of the following properties (a missing or blank property value is
 * treated as a missing one, the default value is used instead):
 * <ul>
 *   <li>{@code stebz.gherkin.keyword.background} — default value: {@code Background:}</li>
 *   <li>{@code stebz.gherkin.keyword.rule} — default value: {@code Rule:}</li>
 *   <li>{@code stebz.gherkin.keyword.given} — default value: {@code Given}</li>
 *   <li>{@code stebz.gherkin.keyword.when} — default value: {@code When}</li>
 *   <li>{@code stebz.gherkin.keyword.then} — default value: {@code Then}</li>
 *   <li>{@code stebz.gherkin.keyword.and} — default value: {@code And}</li>
 *   <li>{@code stebz.gherkin.keyword.but} — default value: {@code But}</li>
 *   <li>{@code stebz.gherkin.keyword.asterisk} — default value: {@code *}</li>
 *   <li>{@code stebz.gherkin.keyword.technical} — default value: {@code Technical:}</li>
 * </ul>
 * <p>If a keyword accessor is called while the Stebz framework startup process is in progress, keyword values are read
 * from the startup properties even if this extension has not been instantiated yet.
 * <p>If this extension is not registered in the Stebz framework, keyword accessors throw {@code IllegalStateException}.
 */
public final class GherkinKeywords implements StebzExtension {
  private static final Object LOCK = new Object();
  private static volatile Keywords keywords = null;

  /**
   * Ctor.
   *
   * @throws IllegalStateException if the Stebz framework startup process has not started yet or is already complete
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
    setKeywordsOnce(readKeywords(properties));
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
   * Returns "Rule" keyword.
   *
   * @return "Rule" keyword
   */
  public static Keyword rule() {
    return getKeywords().rule;
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
   * Returns "*" keyword.
   *
   * @return "*" keyword
   */
  public static Keyword asterisk() {
    return getKeywords().asterisk;
  }

  /**
   * Returns "Technical" keyword.
   *
   * @return "Technical" keyword
   */
  public static Keyword technical() {
    return getKeywords().technical;
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
        throw new IllegalStateException("GherkinKeywords extension is not registered in the Stebz framework");
      }
    }
    return keywords;
  }

  private static Keywords readKeywords(final PropertiesReader properties) {
    return new Keywords(
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.background", "Background:")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.rule", "Rule:")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.given", "Given")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.when", "When")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.then", "Then")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.and", "And")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.but", "But")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.asterisk", "*")),
      new Keyword.Of(properties.getString("stebz.gherkin.keyword.technical", "Technical:"))
    );
  }

  private static final class Keywords {
    private final Keyword background;
    private final Keyword rule;
    private final Keyword given;
    private final Keyword when;
    private final Keyword then;
    private final Keyword and;
    private final Keyword but;
    private final Keyword asterisk;
    private final Keyword technical;

    private Keywords(final Keyword background,
                     final Keyword rule,
                     final Keyword given,
                     final Keyword when,
                     final Keyword then,
                     final Keyword and,
                     final Keyword but,
                     final Keyword asterisk,
                     final Keyword technical) {
      this.background = background;
      this.rule = rule;
      this.given = given;
      this.when = when;
      this.then = then;
      this.and = and;
      this.but = but;
      this.asterisk = asterisk;
      this.technical = technical;
    }
  }
}
