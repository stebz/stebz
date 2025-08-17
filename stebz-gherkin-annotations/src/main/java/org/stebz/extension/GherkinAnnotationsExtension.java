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

import org.stebz.annotation.And;
import org.stebz.annotation.But;
import org.stebz.annotation.Given;
import org.stebz.annotation.Then;
import org.stebz.annotation.When;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttribute;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.function.ThrowingSupplier.Caching;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

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
   * @see Given
   * @see When
   * @see Then
   * @see And
   * @see But
   */
  public static final StepAttribute<Annotation> GHERKIN_KEYWORD =
    StepAttribute.nullable(GHERKIN_KEYWORD_ATTRIBUTE_KEY);
  private static final Caching<Map<Class<? extends Annotation>, Keyword>> KEYWORDS =
    caching(() -> {
      final Map<Class<? extends Annotation>, Keyword> keywords = new HashMap<>();
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
      values.put(Given.class, annot -> ((Given) annot).value());
      values.put(When.class, annot -> ((When) annot).value());
      values.put(Then.class, annot -> ((Then) annot).value());
      values.put(And.class, annot -> ((And) annot).value());
      values.put(But.class, annot -> ((But) annot).value());
      return values;
    });

  /**
   * Ctor.
   */
  public GherkinAnnotationsExtension() {
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    if (annotation == null) {
      return step;
    }

    final Class<? extends Annotation> annotationType = annotation.annotationType();
    final Keyword keyword = KEYWORDS.get().get(annotationType);
    if (keyword == null) {
      return step;
    }

    final ThrowingFunction<Annotation, String, Error> getValueFunction = VALUES.get().get(annotationType);
    if (getValueFunction == null) {
      return step;
    }

    final String annotationValue = getValueFunction.apply(annotation);
    if (annotationValue.isEmpty()) {
      return step.with(KEYWORD, keyword);
    } else {
      return step.with(KEYWORD, keyword, NAME, annotationValue);
    }
  }
}
