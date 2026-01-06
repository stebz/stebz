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
package org.stebz.attribute;

import org.aspectj.lang.JoinPoint;

import java.lang.annotation.Annotation;

/**
 * Reflective step attributes.
 */
public final class ReflectiveStepAttributes {

  /**
   * Join point step attribute.
   */
  public static final StepAttribute<JoinPoint> JOIN_POINT =
    StepAttribute.nullable("reflective:join_point");

  /**
   * Step source step attribute.
   *
   * @see #STEP_SOURCE
   */
  public static final StepAttribute<StepSourceType> STEP_SOURCE_TYPE =
    StepAttribute.nonNull("reflective:step_source_type", StepSourceType.UNKNOWN);

  /**
   * Step source step attribute. May be {@link java.lang.reflect.Field}, {@link java.lang.reflect.Method},
   * {@link java.lang.reflect.Constructor} or null.
   *
   * @see #STEP_SOURCE_TYPE
   */
  public static final StepAttribute<Object> STEP_SOURCE =
    StepAttribute.nullable("reflective:step_source");

  /**
   * Declared annotations step attribute.
   */
  public static final StepAttribute<Annotation[]> DECLARED_ANNOTATIONS =
    StepAttribute.nonNull("reflective:annotations", new Annotation[0], Annotation[]::clone);

  /**
   * Reflective name step attribute.
   */
  public static final StepAttribute<String> REFLECTIVE_NAME =
    StepAttribute.nonNull("reflective:reflective_name", "");

  /**
   * Utility class ctor.
   */
  private ReflectiveStepAttributes() {
  }
}
