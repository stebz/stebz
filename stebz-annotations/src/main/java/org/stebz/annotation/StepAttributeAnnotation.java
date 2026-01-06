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
package org.stebz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Step attribute annotation. Annotations marked with {@link StepAttributeAnnotation} annotation except default
 * annotations are converted into step attributes via {@link org.stebz.aspect.StepAspects} in accordance with
 * {@link org.stebz.attribute.StepAttribute#nullable(String)} method.
 * <p>Default annotations list:
 * <ul>
 *   <li>{@link Step}</li>
 *   <li>{@link WithComment}</li>
 *   <li>{@link WithExpectedResult}</li>
 *   <li>{@link WithHidden}</li>
 *   <li>{@link WithKeyword}</li>
 *   <li>{@link WithName}</li>
 *   <li>{@link WithParam}</li>
 *   <li>{@link WithParam.List}</li>
 *   <li>{@link WithParams}</li>
 * </ul>
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StepAttributeAnnotation {

  /**
   * Returns attribute key.
   *
   * @return attribute key
   */
  String value();
}
