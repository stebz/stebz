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
import org.stebz.annotation.Asterisk;
import org.stebz.annotation.Background;
import org.stebz.annotation.But;
import org.stebz.annotation.Given;
import org.stebz.annotation.Then;
import org.stebz.annotation.When;
import org.stebz.annotation._And;
import org.stebz.attribute.Keyword;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.function.ThrowingFunction;
import org.stebz.util.property.PropertiesReader;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static org.stebz.attribute.GherkinAnnotationStepAttributes.GHERKIN_KEYWORD;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;

/**
 * Gherkin annotations {@link StebzExtension}.
 */
public class GherkinAnnotationsExtension implements InterceptStep {
  private static final Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>> GET_VALUE_MAP;
  private final Map<Class<? extends Annotation>, Keyword> keywords;

  static {
    final Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>> getValueMap = new HashMap<>();
    getValueMap.put(Given.class, annot -> ((Given) annot).value());
    getValueMap.put(When.class, annot -> ((When) annot).value());
    getValueMap.put(Then.class, annot -> ((Then) annot).value());
    getValueMap.put(And.class, annot -> ((And) annot).value());
    getValueMap.put(But.class, annot -> ((But) annot).value());
    getValueMap.put(Background.class, annot -> ((Background) annot).value());
    getValueMap.put(Asterisk.class, annot -> ((Asterisk) annot).value());
    getValueMap.put(_And.class, annot -> ((_And) annot).value());
    GET_VALUE_MAP = getValueMap;
  }

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
  private GherkinAnnotationsExtension(final PropertiesReader properties) {
    final Map<Class<? extends Annotation>, Keyword> keywords = new HashMap<>();
    keywords.put(Given.class, new Keyword.Of(properties.getString("stebz.gherkin.keywords.given", "Given")));
    keywords.put(When.class, new Keyword.Of(properties.getString("stebz.gherkin.keywords.when", "When")));
    keywords.put(Then.class, new Keyword.Of(properties.getString("stebz.gherkin.keywords.then", "Then")));
    keywords.put(And.class, new Keyword.Of(properties.getString("stebz.gherkin.keywords.and", "And")));
    keywords.put(But.class, new Keyword.Of(properties.getString("stebz.gherkin.keywords.but", "But")));
    keywords.put(Background.class,
      new Keyword.Of(properties.getString("stebz.gherkin.keywords.background", "Background")));
    Keyword asteriskKeyword = new Keyword.Of(properties.getString("stebz.gherkin.keywords.asterisk", "*"));
    keywords.put(Asterisk.class, asteriskKeyword);
    keywords.put(_And.class, asteriskKeyword);
    this.keywords = keywords;
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    final Annotation annotation = step.get(GHERKIN_KEYWORD);
    if (annotation == null) {
      return step;
    }

    final Class<? extends Annotation> annotationType = annotation.annotationType();
    final Keyword keyword = this.keywords.get(annotationType);
    if (keyword == null) {
      return step;
    }

    final ThrowingFunction<Annotation, String, Error> getValueFunction = GET_VALUE_MAP.get(annotationType);
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
