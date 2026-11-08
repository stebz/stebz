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
package org.stebz.systemout;

import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.core.attribute.StepAttribute.COMMENT;
import static org.stebz.core.attribute.StepAttribute.EXPECTED_RESULT;
import static org.stebz.core.attribute.StepAttribute.HIDING;
import static org.stebz.core.attribute.StepAttribute.KEYWORD;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.core.attribute.StepAttribute.PARAMS;

/**
 * Tests for {@link SystemOutStepListener}.
 */
final class SystemOutStepListenerTest {
  private static final String LINE_SEPARATOR = System.lineSeparator();

  private static SystemOutStepListener newListener(final Map<String, String> properties,
                                                   final ByteArrayOutputStream output) {
    try {
      return new SystemOutStepListener(
        new PrintStream(output, true, "UTF-8"),
        new PropertiesReader.Of(properties)
      );
    } catch (final UnsupportedEncodingException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static SystemOutStepListener newListener(final Map<String, String> properties) {
    return newListener(properties, new ByteArrayOutputStream());
  }

  private static SystemOutStepListener newListenerWithProperty(final String name,
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

  private static String outputOf(final ByteArrayOutputStream output) {
    return new String(output.toByteArray(), StandardCharsets.UTF_8)
      .replace(LINE_SEPARATOR, "\n");
  }

  @Test
  void orderDefaultValue() {
    assertThat(newListener(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newListenerWithProperty("stebz.systemOut.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void onStepStartShouldPrintStepName() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);

    listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: name\n");
  }

  @Test
  void onStepStartShouldPrintKeywordParamsExpectedResultAndComment() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    params.put("param2", "value2");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);

    listener.onStepStart(step("keyword", "name", false, params, "expected result", "comment"),
      NullableOptional.empty());

    assertThat(outputOf(output))
      .isEqualTo("Step: keyword name (param1 = value1, param2 = value2), "
        + "expected result = expected result, comment = comment\n");
  }

  @Test
  void onStepStartShouldPrintKeywordAtEndAndUppercase() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.keywordPosition", "AT_END");
    properties.put("stebz.systemOut.keywordToUppercase", "true");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: name KEYWORD\n");
  }

  @Test
  void onStepStartShouldNotPrintParamsWhenDisabled() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.params", "false");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step(null, "name", false, params, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: name\n");
  }

  @Test
  void onStepStartShouldPrintArrayAndNullParamValues() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("array", new int[]{1, 2});
    params.put("nullValue", null);
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);

    listener.onStepStart(step(null, "name", false, params, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: name (array = [1, 2], nullValue = null)\n");
  }

  @Test
  void onStepStartShouldNotPrintStepWhenListenerDisabled() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.enabled", "false");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());
    listener.onStepSuccess(step(null, "name", false, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());

    assertThat(outputOf(output)).isEmpty();
  }

  @Test
  void onStepStartShouldNotPrintStepWhenStepIsHidden() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);

    listener.onStepStart(step(null, "name", true, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEmpty();
  }

  @Test
  void onStepStartShouldNotPrintNonKeywordStepWhenOnlyKeywordStepsEnabled() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.onlyKeywordSteps", "true");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step(null, "name", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEmpty();
  }

  @Test
  void onStepStartShouldPrintKeywordStepWhenOnlyKeywordStepsEnabled() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.onlyKeywordSteps", "true");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step("keyword", "name", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: keyword name\n");
  }

  @Test
  void onStepStartShouldIndentNestedStepsWithIndentProperty() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.systemOut.indent", "4");
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(properties, output);

    listener.onStepStart(step(null, "outer", false, null, null, null), NullableOptional.empty());
    listener.onStepStart(step(null, "inner", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: outer\n    Step: inner\n");
  }

  @Test
  void onStepSuccessShouldUnindentNestedSteps() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);

    listener.onStepStart(step(null, "outer", false, null, null, null), NullableOptional.empty());
    listener.onStepSuccess(step(null, "outer", false, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());
    listener.onStepStart(step(null, "next", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output)).isEqualTo("Step: outer\nStep: next\n");
  }

  @Test
  void onStepFailureShouldPrintFailureWithIndentAndUnindent() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    final SystemOutStepListener listener = newListener(new HashMap<>(), output);
    final Throwable exception = new IllegalStateException("failure message");

    listener.onStepStart(step(null, "outer", false, null, null, null), NullableOptional.empty());
    listener.onStepStart(step(null, "inner", false, null, null, null), NullableOptional.empty());
    listener.onStepFailure(step(null, "inner", false, null, null, null),
      NullableOptional.empty(), exception);
    listener.onStepSuccess(step(null, "outer", false, null, null, null),
      NullableOptional.empty(), NullableOptional.empty());
    listener.onStepStart(step(null, "next", false, null, null, null), NullableOptional.empty());

    assertThat(outputOf(output))
      .isEqualTo("Step: outer\n  Step: inner\n  Failure: " + exception + "\nStep: next\n");
  }
}
