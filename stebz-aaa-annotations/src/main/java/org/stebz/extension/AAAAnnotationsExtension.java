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

import org.stebz.annotation.aaa.Act;
import org.stebz.annotation.aaa.Arrange;
import org.stebz.annotation.aaa.Assert;
import org.stebz.annotation.aaa.Setup;
import org.stebz.annotation.aaa.Teardown;
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
 * Arrange-Act-Assert annotations {@link StebzExtension}.
 */
public class AAAAnnotationsExtension implements InterceptStep {
  /**
   * Arrange-Act-Assert keyword attribute key.
   */
  public static final String AAA_KEYWORD_ATTRIBUTE_KEY = "extension:aaa_keyword";
  /**
   * Arrange-Act-Assert keyword annotation attribute.
   *
   * @see Setup
   * @see Teardown
   * @see Arrange
   * @see Act
   * @see Assert
   */
  public static final StepAttribute<Annotation> AAA_KEYWORD = StepAttribute.nullable(AAA_KEYWORD_ATTRIBUTE_KEY);
  private static final Caching<Map<Class<? extends Annotation>, Keyword>> KEYWORDS =
    caching(() -> {
      final Map<Class<? extends Annotation>, Keyword> keywords = new HashMap<>();
      keywords.put(Setup.class, AAAKeywords.setup());
      keywords.put(Teardown.class, AAAKeywords.teardown());
      keywords.put(Arrange.class, AAAKeywords.arrange());
      keywords.put(Act.class, AAAKeywords.act());
      keywords.put(Assert.class, AAAKeywords._assert());
      return keywords;
    });
  private static final Caching<Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>>> VALUES =
    caching(() -> {
      final Map<Class<? extends Annotation>, ThrowingFunction<Annotation, String, Error>> values = new HashMap<>();
      values.put(Setup.class, annot -> ((Setup) annot).value());
      values.put(Teardown.class, annot -> ((Teardown) annot).value());
      values.put(Arrange.class, annot -> ((Arrange) annot).value());
      values.put(Act.class, annot -> ((Act) annot).value());
      values.put(Assert.class, annot -> ((Assert) annot).value());
      return values;
    });
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public AAAAnnotationsExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public AAAAnnotationsExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.aaa.annotations.enabled", true);
    this.order = properties.getInteger("stebz.extensions.aaa.annotations.order", MID_EARLY_ORDER);
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
    final Annotation annotation = step.get(AAA_KEYWORD);
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
