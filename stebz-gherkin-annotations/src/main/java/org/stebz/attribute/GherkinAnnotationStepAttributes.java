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
package org.stebz.attribute;

import java.lang.annotation.Annotation;

/**
 * Gherkin annotations step attributes.
 */
public final class GherkinAnnotationStepAttributes {
  /**
   * Gherkin keyword attribute key.
   */
  public static final String GHERKIN_KEYWORD_ATTRIBUTE_KEY = "extension:gherkin_keyword";

  /**
   * Gherkin keyword annotation attribute.
   *
   * @see org.stebz.annotation._And
   * @see org.stebz.annotation.And
   * @see org.stebz.annotation.Asterisk
   * @see org.stebz.annotation.Background
   * @see org.stebz.annotation.But
   * @see org.stebz.annotation.Given
   * @see org.stebz.annotation.Then
   * @see org.stebz.annotation.When
   */
  public static final StepAttribute<Annotation> GHERKIN_KEYWORD =
    StepAttribute.nullable(GHERKIN_KEYWORD_ATTRIBUTE_KEY);

  /**
   * Utility class ctor.
   */
  private GherkinAnnotationStepAttributes() {
  }
}
