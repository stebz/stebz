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
package org.stebz.reportportal;

import com.epam.reportportal.service.Launch;
import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.stebz.core.attribute.StepAttribute.COMMENT;
import static org.stebz.core.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.core.attribute.StepAttribute.HIDING;
import static org.stebz.core.attribute.StepAttribute.KEYWORD;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.core.attribute.StepAttribute.PARAMS;

/**
 * Tests for {@link ReportPortalStepListener}.
 */
final class ReportPortalStepListenerTest {

  private static ReportPortalStepListener newListener(final Map<String, String> properties) {
    return new ReportPortalStepListener(new PropertiesReader.Of(properties));
  }

  private static ReportPortalStepListener newListenerWithProperty(final String name,
                                                                  final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newListener(properties);
  }

  private static RunnableStep step(final String keyword,
                                   final String name,
                                   final boolean hidden,
                                   final Map<String, Object> params,
                                   final String expectedResult,
                                   final String comment) {
    final StepAttributes.BuilderOf builder = new StepAttributes.BuilderOf();
    if (keyword != null) {
      builder.add(KEYWORD, new Keyword.Of(keyword));
    }
    if (name != null) {
      builder.add(NAME, name);
    }
    builder.add(HIDING, hidden);
    if (params != null) {
      builder.add(PARAMS, params);
    }
    if (expectedResult != null) {
      builder.add(EXPECTED_RESULT, expectedResult);
    }
    if (comment != null) {
      builder.add(COMMENT, comment);
    }
    return RunnableStep.of(builder.build(), RunnableStep.emptyBody());
  }

  private static void setCurrentLaunchToNoop() throws Exception {
    final Field field = Launch.class.getDeclaredField("CURRENT_LAUNCH");
    field.setAccessible(true);
    ((ThreadLocal<Launch>) field.get(null)).set(Launch.NOOP_LAUNCH);
  }

  private static void clearCurrentLaunch() throws Exception {
    final Field field = Launch.class.getDeclaredField("CURRENT_LAUNCH");
    field.setAccessible(true);
    ((ThreadLocal<Launch>) field.get(null)).remove();
  }

  @Test
  void orderDefaultValue() {
    assertThat(newListener(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newListenerWithProperty("stebz.reportportal.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepSuccessShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepSuccess(step(null, "name", false, null, null, null),
        NullableOptional.empty(), NullableOptional.of("result"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepFailureShouldTakeNoEffectWithoutLaunch() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepFailure(step(null, "name", false, null, null, null),
        NullableOptional.empty(), new IllegalStateException("failure message"))
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenListenerDisabled() {
    final ReportPortalStepListener listener = newListenerWithProperty("stebz.reportportal.enabled", "false");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectWhenStepIsHidden() {
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void onStepStartShouldTakeNoEffectForNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final ReportPortalStepListener listener =
      newListenerWithProperty("stebz.reportportal.onlyKeywordSteps", "true");

    assertThatCode(() ->
      listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty())
    ).doesNotThrowAnyException();
  }

  @Test
  void listenerShouldProcessStepLifecycleWithNoopLaunch() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RunnableStep step = step("keyword", "name", false, params, "expected result", "comment");
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() -> {
      setCurrentLaunchToNoop();
      try {
        listener.onStepStart(step, NullableOptional.of("context value"));
        listener.onStepSuccess(step, NullableOptional.of("context value"), NullableOptional.empty());
      } finally {
        clearCurrentLaunch();
      }
    }).doesNotThrowAnyException();
  }

  @Test
  void listenerShouldProcessFailedStepLifecycleWithNoopLaunch() {
    final RunnableStep step = step(null, "name", false, null, null, null);
    final ReportPortalStepListener listener = newListener(new HashMap<>());

    assertThatCode(() -> {
      setCurrentLaunchToNoop();
      try {
        listener.onStepStart(step, NullableOptional.empty());
        listener.onStepFailure(step, NullableOptional.empty(), new IllegalStateException("message"));
      } finally {
        clearCurrentLaunch();
      }
    }).doesNotThrowAnyException();
  }
}
