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

import org.stebz.annotation.NameWordSeparator;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.annotation.Annotation;

import static org.stebz.attribute.ReflectiveStepAttributes.DECLARED_ANNOTATIONS;
import static org.stebz.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;

/**
 * Readable reflective name {@link StebzExtension}.
 *
 * @see NameWordSeparator
 */
public class ReadableReflectiveNameExtension implements InterceptStep {
  private final boolean enabled;
  private final int order;
  private final String defaultWordSeparator;

  /**
   * Ctor.
   */
  public ReadableReflectiveNameExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor
   *
   * @param properties the properties reader
   */
  public ReadableReflectiveNameExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.readableReflectiveName.enabled", true);
    this.order = properties.getInteger("stebz.extensions.readableReflectiveName.order", MIDDLE_ORDER);
    this.defaultWordSeparator = properties.getString("stebz.extensions.readableReflectiveName.wordSeparator", "_");
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
    final String reflectiveName = step.get(REFLECTIVE_NAME);
    if (!reflectiveName.equals(step.getName())) {
      return step;
    }
    String wordSeparator = getWordSeparator(step);
    return wordSeparator.length() == 1
      ? step.withName(reflectiveName.replace(wordSeparator.charAt(0), ' '))
      : step.withName(reflectiveName.replace(wordSeparator, " "));
  }

  private String getWordSeparator(final StepObj<?> step) {
    final Annotation[] annotations = step.get(DECLARED_ANNOTATIONS);
    NameWordSeparator nameWordSeparator = null;
    for (final Annotation annotation : annotations) {
      if (annotation instanceof NameWordSeparator) {
        nameWordSeparator = (NameWordSeparator) annotation;
        break;
      }
    }
    if (nameWordSeparator == null) {
      return this.defaultWordSeparator;
    }
    final String withNameWordSeparatorValue = nameWordSeparator.value().trim();
    return withNameWordSeparatorValue.isEmpty()
      ? this.defaultWordSeparator
      : withNameWordSeparatorValue;
  }
}
