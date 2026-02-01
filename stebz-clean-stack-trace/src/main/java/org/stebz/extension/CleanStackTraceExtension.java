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

import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Clean stack trace {@link StebzExtension}.
 */
public class CleanStackTraceExtension implements BeforeStepFailure {
  private static final String STEBZ_CLASS_NAME_PREFIX = "org.stebz.";
  private static final String ASPECTJ_CLASS_NAME_PREFIX = "org.aspectj.";
  private static final String ASPECTJ_CLASS_NAME_PART = "$AjcClosure";
  private static final String ASPECTJ_METHOD_NAME_PART = "_aroundBody";
  private final Predicate<StackTraceElement> stackTraceElementFilter;
  private final boolean enabled;
  private final int order;
  private final boolean clearRelated;

  /**
   * Ctor.
   */
  public CleanStackTraceExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public CleanStackTraceExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.extensions.cleanStackTrace.enabled", true);
    this.order = properties.getInteger("stebz.extensions.cleanStackTrace.order", MIDDLE_ORDER);
    this.clearRelated = properties.getBoolean("stebz.extensions.cleanStackTrace.clearRelated", true);
    final boolean clearStebzLines = properties.getBoolean("stebz.extensions.cleanStackTrace.clearStebzLines", true);
    Boolean clearAspectjLines = properties.getBoolean("stebz.extensions.cleanStackTrace.clearAspectjLines", null);
    if (clearAspectjLines == null) {
      clearAspectjLines = isStebzAnnotationsUsed();
    }
    if (clearStebzLines && clearAspectjLines) {
      this.stackTraceElementFilter = stElem ->
        !stElem.getClassName().startsWith(STEBZ_CLASS_NAME_PREFIX)
          && !stElem.getClassName().startsWith(ASPECTJ_CLASS_NAME_PREFIX)
          && !stElem.getClassName().contains(ASPECTJ_CLASS_NAME_PART)
          && !stElem.getMethodName().contains(ASPECTJ_METHOD_NAME_PART);
    } else if (clearStebzLines) {
      this.stackTraceElementFilter = stElem ->
        !stElem.getClassName().startsWith(STEBZ_CLASS_NAME_PREFIX);
    } else if (clearAspectjLines) {
      this.stackTraceElementFilter = stElem ->
        !stElem.getClassName().startsWith(ASPECTJ_CLASS_NAME_PREFIX)
          && !stElem.getClassName().contains(ASPECTJ_CLASS_NAME_PART)
          && !stElem.getMethodName().contains(ASPECTJ_METHOD_NAME_PART);
    } else {
      this.stackTraceElementFilter = stElem -> true;
    }
  }

  private static boolean isStebzAnnotationsUsed() {
    try {
      Class.forName("org.stebz.aspect.StepAspects");
      return true;
    } catch (final ClassNotFoundException ex) {
      return false;
    }
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public void beforeStepFailure(final StepObj<?> step,
                                final NullableOptional<Object> context,
                                final Throwable exception) {
    if (this.enabled) {
      if (this.clearRelated) {
        relatedExceptions(new HashSet<>(), exception).forEach(this::clearException);
      } else {
        this.clearException(exception);
      }
    }
  }

  private void clearException(final Throwable exception) {
    final StackTraceElement[] originST = exception.getStackTrace();
    if (originST.length != 0) {
      exception.setStackTrace(
        Arrays.stream(originST)
          .filter(this.stackTraceElementFilter)
          .toArray(StackTraceElement[]::new)
      );
    }
  }

  private static Set<Throwable> relatedExceptions(final Set<Throwable> exceptions,
                                                  final Throwable mainEx) {
    for (Throwable currentEx = mainEx; currentEx != null; currentEx = currentEx.getCause()) {
      if (exceptions.add(currentEx)) {
        for (final Throwable suppressedEx : currentEx.getSuppressed()) {
          relatedExceptions(exceptions, suppressedEx);
        }
      }
    }
    return exceptions;
  }
}
