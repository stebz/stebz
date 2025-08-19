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
package org.stebz.listener;

import org.stebz.attribute.Keyword;
import org.stebz.executor.StartupPropertiesReader;
import org.stebz.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * System.out {@code StepListener} implementation.
 */
public class SystemOutStepListener implements StepListener {
  private final ThreadLocal<AtomicInteger> depth;
  private final PrintStream printStream;
  private final boolean enabled;
  private final int order;
  private final KeywordPosition keywordPosition;
  private final String indent;
  private final boolean logParams;
  private final boolean logExpectedResult;
  private final boolean logComment;

  /**
   * Ctor.
   */
  public SystemOutStepListener() {
    this(System.out, StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public SystemOutStepListener(final PropertiesReader properties) {
    this(System.out, properties);
  }

  /**
   * Ctor.
   *
   * @param printStream the print stream
   * @param properties  the properties reader
   */
  public SystemOutStepListener(final PrintStream printStream,
                               final PropertiesReader properties) {
    this.depth = ThreadLocal.withInitial(AtomicInteger::new);
    this.printStream = printStream;
    this.enabled = properties.getBoolean("stebz.listeners.systemout.enabled", true);
    this.order = properties.getInteger("stebz.listeners.systemout.order", DEFAULT_ORDER);
    this.keywordPosition = properties.getEnum("stebz.listeners.systemout.keywordPosition",
      KeywordPosition.class, KeywordPosition.AT_START);
    this.indent = multiplyString(" ", properties.getInteger("stebz.listeners.systemout.indent", 2));
    this.logParams = properties.getBoolean("stebz.listeners.systemout.params", true);
    this.logExpectedResult = properties.getBoolean("stebz.listeners.systemout.expectedResult", true);
    this.logComment = properties.getBoolean("stebz.listeners.systemout.comment", true);
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public void onStepStart(final StepObj<?> step,
                          final NullableOptional<Object> context) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final int currentDepth = this.depth.get().getAndIncrement();
    final String indentStr = multiplyString(this.indent, currentDepth);
    final StringBuilder sb = new StringBuilder();
    sb.append(indentStr)
      .append("Step: ")
      .append(this.keywordPosition.concat(step.getKeyword(), step.getName()));
    if (this.logParams) {
      final Map<String, Object> params = step.getParams();
      if (!params.isEmpty()) {
        sb.append(" (")
          .append(
            params.entrySet()
              .stream()
              .map(entry -> entry.getKey() + " = " + asString(entry.getValue()))
              .collect(Collectors.joining(", "))
          )
          .append(')');
      }
    }
    if (this.logExpectedResult) {
      final String expectedResult = step.getExpectedResult();
      if (!expectedResult.isEmpty()) {
        sb.append(", ")
          .append("expected result = ")
          .append(expectedResult);
      }
    }
    if (this.logComment) {
      final String comment = step.getComment();
      if (!comment.isEmpty()) {
        sb.append(", ")
          .append("comment = ")
          .append(comment);
      }
    }
    this.printStream.println(sb);
  }

  @Override
  public void onStepSuccess(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final NullableOptional<Object> result) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final int currentDepth = this.depth.get().decrementAndGet();
    if (currentDepth == 0) {
      this.depth.remove();
    }
  }

  @Override
  public void onStepFailure(final StepObj<?> step,
                            final NullableOptional<Object> context,
                            final Throwable exception) {
    if (!this.enabled || step.getHidden()) {
      return;
    }
    final int currentDepth = this.depth.get().decrementAndGet();
    this.printStream.println(multiplyString(this.indent, currentDepth) + "Failure: " + exception);
    if (currentDepth == 0) {
      this.depth.remove();
    }
  }

  private static String multiplyString(final String origin,
                                       final int number) {
    switch (number) {
      case 0:
        return "";
      case 1:
        return origin;
      default:
        final StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < number; idx++) {
          sb.append(origin);
        }
        return sb.toString();
    }
  }

  private static String asString(final Object obj) {
    if (obj == null) {
      return "null";
    }
    if (obj.getClass().isArray()) {
      if (obj instanceof int[]) {
        return Arrays.toString((int[]) obj);
      } else if (obj instanceof long[]) {
        return Arrays.toString((long[]) obj);
      } else if (obj instanceof double[]) {
        return Arrays.toString((double[]) obj);
      } else if (obj instanceof float[]) {
        return Arrays.toString((float[]) obj);
      } else if (obj instanceof boolean[]) {
        return Arrays.toString((boolean[]) obj);
      } else if (obj instanceof short[]) {
        return Arrays.toString((short[]) obj);
      } else if (obj instanceof char[]) {
        return Arrays.toString((char[]) obj);
      } else if (obj instanceof byte[]) {
        return Arrays.toString((byte[]) obj);
      } else {
        return Arrays.toString((Object[]) obj);
      }
    }
    return obj.toString();
  }

  private enum KeywordPosition {
    AT_START {
      @Override
      String concat(final Keyword keyword,
                    final String name) {
        if (name.isEmpty()) {
          return keyword.value();
        }
        final String keywordValue = keyword.value();
        return keywordValue.isEmpty()
          ? name
          : keywordValue + ' ' + name;
      }
    },
    AT_END {
      @Override
      String concat(final Keyword keyword,
                    final String name) {
        if (name.isEmpty()) {
          return keyword.value();
        }
        final String keywordValue = keyword.value();
        return keywordValue.isEmpty()
          ? name
          : name + ' ' + keywordValue;
      }
    };

    abstract String concat(Keyword keyword,
                           String name);
  }
}
