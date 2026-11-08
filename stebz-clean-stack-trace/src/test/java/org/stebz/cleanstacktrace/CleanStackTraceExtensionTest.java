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

import org.junit.jupiter.api.Test;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.StebzExtension;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CleanStackTraceExtension}.
 */
final class CleanStackTraceExtensionTest {

  private static CleanStackTraceExtension newExtension(final Map<String, String> properties) {
    return new CleanStackTraceExtension(new PropertiesReader.Of(properties));
  }

  private static CleanStackTraceExtension newExtensionWithProperty(final String name,
                                                                   final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static void setStartupPropertiesReader(final PropertiesReader reader) throws Exception {
    final Method setMethod = StartupPropertiesReader.class.getDeclaredMethod("set", PropertiesReader.class);
    setMethod.setAccessible(true);
    setMethod.invoke(null, reader);
  }

  private static void clearStartupPropertiesReader() throws Exception {
    final Method clearMethod = StartupPropertiesReader.class.getDeclaredMethod("clear");
    clearMethod.setAccessible(true);
    clearMethod.invoke(null);
  }

  private static StackTraceElement frame(final String className,
                                         final String methodName) {
    return new StackTraceElement(className, methodName, "FileName.java", 42);
  }

  private static void cleanStackTrace(final CleanStackTraceExtension extension,
                                      final Throwable exception) {
    extension.beforeStepFailure(null, NullableOptional.empty(), exception);
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.cleanStackTrace.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void disabledExtensionShouldNotModifyStackTrace() {
    final StackTraceElement[] originStackTrace = {
      frame("org.stebz.core.Foo", "bar"),
      frame("com.example.Foo", "bar"),
    };
    final Throwable exception = new Throwable();
    exception.setStackTrace(originStackTrace.clone());

    cleanStackTrace(newExtensionWithProperty("stebz.cleanStackTrace.enabled", "false"), exception);

    assertThat(exception.getStackTrace()).isEqualTo(originStackTrace);
  }

  @Test
  void defaultSettingsShouldRemoveStebzLinesAndKeepOtherLines() {
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("com.example.Foo", "first"),
      frame("org.stebz.core.Foo", "stebzLine"),
      frame("org.stebz.annotation.aspect.StepAspects", "around"),
      frame("com.example.Foo", "last"),
    });

    cleanStackTrace(newExtension(new HashMap<>()), exception);

    assertThat(exception.getStackTrace())
      .containsExactly(
        frame("com.example.Foo", "first"),
        frame("com.example.Foo", "last")
      );
  }

  @Test
  void defaultAspectjFilteringShouldFollowStepAspectsAvailability() {
    boolean stepAspectsAvailable = true;
    try {
      Class.forName("org.stebz.annotation.aspect.StepAspects", false,
        CleanStackTraceExtension.class.getClassLoader());
    } catch (final ClassNotFoundException ex) {
      stepAspectsAvailable = false;
    }

    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("com.example.Foo", "first"),
      frame("org.aspectj.runtime.Foo", "aspectjLine"),
      frame("com.example.Foo$AjcClosure1", "run"),
      frame("com.example.Foo", "_aroundBody0"),
      frame("org.stebz.annotation.aspect.StepAspects", "around"),
      frame("com.example.Foo", "last"),
    });

    cleanStackTrace(newExtension(new HashMap<>()), exception);

    if (stepAspectsAvailable) {
      assertThat(exception.getStackTrace())
        .containsExactly(
          frame("com.example.Foo", "first"),
          frame("com.example.Foo", "last")
        );
    } else {
      assertThat(exception.getStackTrace())
        .containsExactly(
          frame("com.example.Foo", "first"),
          frame("org.aspectj.runtime.Foo", "aspectjLine"),
          frame("com.example.Foo$AjcClosure1", "run"),
          frame("com.example.Foo", "_aroundBody0"),
          frame("com.example.Foo", "last")
        );
    }
  }

  @Test
  void clearAspectjLinesShouldRemoveAspectjLines() {
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("com.example.Foo", "first"),
      frame("org.aspectj.runtime.Foo", "aspectjLine"),
      frame("com.example.Foo$AjcClosure1", "run"),
      frame("com.example.Foo", "_aroundBody0"),
      frame("com.example.Foo", "_aroundBody1$advice"),
      frame("com.example.Foo", "last"),
    });

    cleanStackTrace(newExtensionWithProperty("stebz.cleanStackTrace.clearAspectjLines", "true"), exception);

    assertThat(exception.getStackTrace())
      .containsExactly(
        frame("com.example.Foo", "first"),
        frame("com.example.Foo", "last")
      );
  }

  @Test
  void clearAspectjLinesWithoutStebzLinesShouldRemoveOnlyAspectjLines() {
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("com.example.Foo", "first"),
      frame("org.stebz.core.Foo", "stebzLine"),
      frame("org.aspectj.runtime.Foo", "aspectjLine"),
      frame("com.example.Foo", "last"),
    });

    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.cleanStackTrace.clearStebzLines", "false");
    properties.put("stebz.cleanStackTrace.clearAspectjLines", "true");
    cleanStackTrace(newExtension(properties), exception);

    assertThat(exception.getStackTrace())
      .containsExactly(
        frame("com.example.Foo", "first"),
        frame("org.stebz.core.Foo", "stebzLine"),
        frame("com.example.Foo", "last")
      );
  }

  @Test
  void allFiltersDisabledShouldKeepAllLines() {
    final StackTraceElement[] originStackTrace = {
      frame("com.example.Foo", "first"),
      frame("org.stebz.core.Foo", "stebzLine"),
      frame("org.aspectj.runtime.Foo", "aspectjLine"),
      frame("com.example.Foo", "last"),
    };
    final Throwable exception = new Throwable();
    exception.setStackTrace(originStackTrace.clone());

    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.cleanStackTrace.clearStebzLines", "false");
    properties.put("stebz.cleanStackTrace.clearAspectjLines", "false");
    cleanStackTrace(newExtension(properties), exception);

    assertThat(exception.getStackTrace()).isEqualTo(originStackTrace);
  }

  @Test
  void allLinesFilteredShouldResultInEmptyStackTrace() {
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("org.stebz.core.Foo", "stebzLine"),
      frame("org.stebz.annotation.aspect.StepAspects", "around"),
    });

    cleanStackTrace(newExtension(new HashMap<>()), exception);

    assertThat(exception.getStackTrace()).isEmpty();
  }

  @Test
  void emptyStackTraceShouldBeKept() {
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[0]);

    cleanStackTrace(newExtension(new HashMap<>()), exception);

    assertThat(exception.getStackTrace()).isEmpty();
  }

  @Test
  void relatedExceptionsShouldBeCleanedByDefault() {
    final Throwable mainException = new Throwable();
    final Throwable causeException = new Throwable();
    final Throwable suppressedException = new Throwable();
    final Throwable causeSuppressedException = new Throwable();
    mainException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Main", "main")});
    causeException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Cause", "cause")});
    suppressedException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Suppressed", "suppressed")});
    causeSuppressedException.setStackTrace(
      new StackTraceElement[]{frame("org.stebz.core.CauseSuppressed", "causeSuppressed")});
    mainException.initCause(causeException);
    mainException.addSuppressed(suppressedException);
    causeException.addSuppressed(causeSuppressedException);

    cleanStackTrace(newExtension(new HashMap<>()), mainException);

    assertThat(mainException.getStackTrace()).isEmpty();
    assertThat(causeException.getStackTrace()).isEmpty();
    assertThat(suppressedException.getStackTrace()).isEmpty();
    assertThat(causeSuppressedException.getStackTrace()).isEmpty();
  }

  @Test
  void relatedExceptionsCleaningDisabledShouldCleanOnlyProvidedException() {
    final Throwable mainException = new Throwable();
    final Throwable causeException = new Throwable();
    final Throwable suppressedException = new Throwable();
    mainException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Main", "main")});
    causeException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Cause", "cause")});
    suppressedException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Suppressed", "suppressed")});
    mainException.initCause(causeException);
    mainException.addSuppressed(suppressedException);

    cleanStackTrace(newExtensionWithProperty("stebz.cleanStackTrace.clearRelated", "false"), mainException);

    assertThat(mainException.getStackTrace()).isEmpty();
    assertThat(causeException.getStackTrace())
      .containsExactly(frame("org.stebz.core.Cause", "cause"));
    assertThat(suppressedException.getStackTrace())
      .containsExactly(frame("org.stebz.core.Suppressed", "suppressed"));
  }

  @Test
  void cyclicRelatedExceptionsShouldNotCauseInfiniteLoop() {
    final Throwable firstException = new Throwable();
    final Throwable secondException = new Throwable();
    firstException.addSuppressed(secondException);
    secondException.addSuppressed(firstException);
    firstException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.First", "first")});
    secondException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Second", "second")});

    cleanStackTrace(newExtension(new HashMap<>()), firstException);

    assertThat(firstException.getStackTrace()).isEmpty();
    assertThat(secondException.getStackTrace()).isEmpty();
  }

  @Test
  void cyclicCausesShouldNotCauseInfiniteLoop() {
    final Throwable firstException = new Throwable();
    final Throwable secondException = new Throwable();
    firstException.initCause(secondException);
    secondException.initCause(firstException);
    firstException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.First", "first")});
    secondException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Second", "second")});

    cleanStackTrace(newExtension(new HashMap<>()), firstException);

    assertThat(firstException.getStackTrace()).isEmpty();
    assertThat(secondException.getStackTrace()).isEmpty();
  }

  @Test
  void distinctExceptionsWithEqualValuesShouldBothBeCleaned() {
    final Throwable mainException = new Throwable();
    final Throwable causeException = new EqualThrowable();
    mainException.initCause(causeException);
    mainException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Main", "main")});
    causeException.setStackTrace(new StackTraceElement[]{frame("org.stebz.core.Cause", "cause")});

    cleanStackTrace(newExtension(new HashMap<>()), mainException);

    assertThat(mainException.getStackTrace()).isEmpty();
    assertThat(causeException.getStackTrace()).isEmpty();
  }

  private static final class EqualThrowable extends Throwable {
    @Override
    public boolean equals(final Object other) {
      return other instanceof Throwable;
    }

    @Override
    public int hashCode() {
      return 1;
    }
  }

  @Test
  void ctorShouldUseStartupPropertiesReader() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.cleanStackTrace.order", "12345");
    try {
      setStartupPropertiesReader(new PropertiesReader.Of(properties));
      assertThat(new CleanStackTraceExtension().order())
        .isEqualTo(12345);
    } finally {
      clearStartupPropertiesReader();
    }
  }

  @Test
  void aspectjClearingDisabledShouldKeepAspectjFrames() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.cleanStackTrace.clearAspectjLines", "false");
    final Throwable exception = new Throwable();
    exception.setStackTrace(new StackTraceElement[]{
      frame("com.example.Foo", "first"),
      frame("org.aspectj.runtime.Foo", "aspectjLine"),
      frame("org.stebz.core.Foo", "stebzLine"),
      frame("com.example.Foo", "last")
    });
    cleanStackTrace(newExtension(properties), exception);
    assertThat(exception.getStackTrace())
      .extracting(StackTraceElement::getClassName)
      .containsExactly("com.example.Foo", "org.aspectj.runtime.Foo", "com.example.Foo");
  }
}
