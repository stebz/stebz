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

import org.stebz.annotation.gherkin.And;
import org.stebz.annotation.gherkin.Background;
import org.stebz.annotation.gherkin.But;
import org.stebz.annotation.gherkin.Conclusion;
import org.stebz.annotation.gherkin.Given;
import org.stebz.annotation.gherkin.Rule;
import org.stebz.annotation.gherkin.Then;
import org.stebz.annotation.gherkin.When;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttribute;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingSupplier.Caching;
import org.stebz.util.property.PropertiesReader;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.stebz.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.util.function.ThrowingSupplier.caching;

/**
 * Gherkin annotations {@link StebzExtension}.
 */
public class GherkinAnnotationsExtension implements InterceptStep {
  /**
   * Gherkin keyword attribute key.
   */
  public static final String GHERKIN_KEYWORD_ATTRIBUTE_KEY = "extension:gherkin_keyword";
  /**
   * Gherkin keyword annotation attribute.
   *
   * @see Background
   * @see Conclusion
   * @see Rule
   * @see Given
   * @see When
   * @see Then
   * @see And
   * @see But
   */
  public static final StepAttribute<Annotation> GHERKIN_KEYWORD = StepAttribute.nullable(GHERKIN_KEYWORD_ATTRIBUTE_KEY);
  private static final Caching<Map<Class<? extends Annotation>, Keyword>> KEYWORDS =
    caching(() -> {
      final Map<Class<? extends Annotation>, Keyword> keywords = new HashMap<>();
      keywords.put(Background.class, GherkinKeywords.background());
      keywords.put(Conclusion.class, GherkinKeywords.conclusion());
      keywords.put(Rule.class, GherkinKeywords.rule());
      keywords.put(Given.class, GherkinKeywords.given());
      keywords.put(When.class, GherkinKeywords.when());
      keywords.put(Then.class, GherkinKeywords.then());
      keywords.put(And.class, GherkinKeywords.and());
      keywords.put(But.class, GherkinKeywords.but());
      return keywords;
    });
  private static final Caching<Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>>> VALUES =
    caching(() -> {
      final Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>> values = new HashMap<>();
      values.put(Background.class, annot -> ((Background) annot).value());
      values.put(Conclusion.class, annot -> ((Conclusion) annot).value());
      values.put(Rule.class, annot -> ((Rule) annot).value());
      values.put(Given.class, annot -> ((Given) annot).value());
      values.put(When.class, annot -> ((When) annot).value());
      values.put(Then.class, annot -> ((Then) annot).value());
      values.put(And.class, annot -> ((And) annot).value());
      values.put(But.class, annot -> ((But) annot).value());
      return values;
    });
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public GherkinAnnotationsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public GherkinAnnotationsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.gherkin.annotations.enabled", true);
    this.order = properties.getInteger("stebz.extensions.gherkin.annotations.order", MID_EARLY_ORDER);
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    if (!this.enabled) {
      return step;
    }
    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    if (annotation == null) {
      return step;
    }
    final Class<? extends Annotation> annotationType = annotation.annotationType();
    final Keyword keyword = KEYWORDS.get().get(annotationType);
    if (keyword == null) {
      return step;
    }
    final ThrowingFunction<Annotation, String, Error> getAnnotValueFunction = VALUES.get().get(annotationType);
    if (getAnnotValueFunction == null) {
      return step;
    }

    String name = getAnnotValueFunction.apply(annotation);
    if (!name.isEmpty()) {
      return step.with(KEYWORD, keyword, NAME, name);
    }
    name = step.get(NAME);
    if (!name.isEmpty()) {
      return step.with(KEYWORD, keyword);
    }
    name = step.get(REFLECTIVE_NAME);
    if (!name.isEmpty()) {
      return step.with(KEYWORD, keyword, NAME, name);
    }
    return step.with(KEYWORD, keyword);
  }
}
