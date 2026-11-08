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
package org.stebz.cleanstacktrace;

import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.BeforeStepFailure;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An extension that removes Stebz frames from exception stack traces when a step fails.
 * <p>AspectJ frames are removed when the {@code StepAspects} class is available to this extension's class loader or
 * when explicitly enabled. By default, the extension cleans the stack traces of the exception, its causes, and its
 * suppressed exceptions.
 * <p>The {@code stebz.cleanStackTrace.enabled} property enables or disables this extension (default: {@code true}).
 * The {@code stebz.cleanStackTrace.order} property sets its execution order (default: {@code MIDDLE_ORDER}). The
 * {@code stebz.cleanStackTrace.clearRelated} property controls whether causes and suppressed exceptions are also
 * cleaned (default: {@code true}). The {@code stebz.cleanStackTrace.clearStebzLines} property controls removal of Stebz
 * frames (default: {@code true}). The {@code stebz.cleanStackTrace.clearAspectjLines} property controls removal of
 * AspectJ frames; when unset, removal is enabled if {@code StepAspects} is available to this extension's class loader.
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
   * Creates the extension using the startup properties.
   */
  public CleanStackTraceExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Creates the extension using the specified properties.
   *
   * @param properties the reader used to configure this extension
   */
  public CleanStackTraceExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.cleanStackTrace.enabled", true);
    this.order = properties.getInteger("stebz.cleanStackTrace.order", MIDDLE_ORDER);
    this.clearRelated = properties.getBoolean("stebz.cleanStackTrace.clearRelated", true);
    final boolean clearStebzLines = properties.getBoolean("stebz.cleanStackTrace.clearStebzLines", true);
    Boolean clearAspectjLines = properties.getBoolean("stebz.cleanStackTrace.clearAspectjLines", null);
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
      Class.forName("org.stebz.annotation.aspect.StepAspects", false, CleanStackTraceExtension.class.getClassLoader());
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
        relatedExceptions(Collections.newSetFromMap(new IdentityHashMap<>()), exception)
          .forEach(this::clearException);
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
    for (Throwable currentEx = mainEx;
         currentEx != null && exceptions.add(currentEx);
         currentEx = currentEx.getCause()) {
      for (final Throwable suppressedEx : currentEx.getSuppressed()) {
        relatedExceptions(exceptions, suppressedEx);
      }
    }
    return exceptions;
  }
}
