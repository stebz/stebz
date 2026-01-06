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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Step attribute annotation. Adds single parameter to a step. Maps to {@link org.stebz.attribute.StepAttribute#PARAMS}
 * attribute via {@link org.stebz.aspect.StepAspects}.
 *
 * @see WithParam.List
 * @see org.stebz.attribute.StepAttribute#PARAMS
 */
@Documented
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(WithParam.List.class)
@StepAttributeAnnotation(WithParam.KEY)
public @interface WithParam {
  /**
   * {@link WithParam} step attribute annotation key.
   */
  String KEY = "annotation:with_param";

  /**
   * Returns parameter name.
   *
   * @return parameter name
   */
  String name();

  /**
   * Returns parameter value.
   *
   * @return parameter value
   */
  String value();

  /**
   * Step attribute annotation. Adds several parameters to a step. Mapped to
   * {@link org.stebz.attribute.StepAttribute#PARAMS} attribute.
   *
   * @see WithParam
   * @see org.stebz.attribute.StepAttribute#PARAMS
   */
  @Documented
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @StepAttributeAnnotation(List.KEY)
  @interface List {
    /**
     * {@link WithParam.List} step attribute annotation key.
     */
    String KEY = "annotation:with_param_list";

    /**
     * Returns {@link WithParam} annotations list.
     *
     * @return {@link WithParam} annotations list
     */
    WithParam[] value();
  }
}
