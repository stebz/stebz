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
package org.stebz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Retry the step in case of exception. Annotation attribute alternative to
 * {@link org.stebz.extension.RetryExtension#RETRY} attribute.
 *
 * @see org.stebz.extension.RetryExtension
 */
@Documented
@Target(value = {ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@StepAttributeAnnotation(WithRetry.KEY)
public @interface WithRetry {
  /**
   * {@link WithRetry} step attribute annotation key.
   */
  String KEY = "extension:with_retry";

  /**
   * Retry count. How many times to retry.
   *
   * @return attempts count
   */
  int count() default 1;

  /**
   * Delay between retries, in time units.
   *
   * @return delay
   * @see #unit()
   */
  long delay() default 0L;

  /**
   * Delay time unit.
   *
   * @return delay time unit
   * @see #delay()
   */
  TimeUnit unit() default TimeUnit.SECONDS;

  /**
   * When to retry (in case of what exception types).
   *
   * @return exceptions list when to retry
   */
  Class<? extends Throwable>[] on() default {Throwable.class};

  /**
   * When not to retry (in case of what exception types).
   *
   * @return exceptions list when not to retry
   */
  Class<? extends Throwable>[] but() default {};
}
