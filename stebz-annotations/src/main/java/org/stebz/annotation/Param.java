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

import org.stebz.annotation.aspect.StepAspects;
import org.stebz.annotation.marker.StepParameterMarker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.stebz.core.step.StepObj.MASKED_PARAM_VALUE;

/**
 * Parameter annotation. Allows to add or override step parameters of a method or constructor via {@link StepAspects}.
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@StepParameterMarker
public @interface Param {

  /**
   * Returns parameter name. Non-empty value overrides {@link #name()}.
   *
   * @return parameter name
   */
  String value() default "";

  /**
   * Returns parameter name.
   *
   * @return parameter name
   */
  String name() default "";

  /**
   * Returns parameter value. An empty value means that the actual method or constructor argument is used.
   *
   * @return parameter value
   */
  String val() default "";

  /**
   * Masks the parameter value in the step params attribute. The actual parameter value is replaced by {@link #value()};
   * an empty annotation value is used as an empty displayed value.
   */
  @Documented
  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @StepParameterMarker
  @interface Masked {

    /**
     * Returns the value displayed for the parameter in the step params attribute. The actual parameter value is not
     * used while this annotation is present, including when this value is empty.
     *
     * @return displayed masked value
     */
    String value() default MASKED_PARAM_VALUE;
  }

  /**
   * Excludes the parameter from the step params attribute.
   */
  @Documented
  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @StepParameterMarker
  @interface Hidden {
  }
}
