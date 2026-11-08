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
package org.stebz.readablereflectivename;

import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.core.attribute.StepAttribute.NAME;

/**
 * Tests for {@link ReadableReflectiveNameExtension}.
 */
final class ReadableReflectiveNameExtensionTest {

  private static ReadableReflectiveNameExtension newExtension(final Map<String, String> properties) {
    return new ReadableReflectiveNameExtension(new PropertiesReader.Of(properties));
  }

  private static ReadableReflectiveNameExtension newExtensionWithProperty(final String name,
                                                                          final String value) {
    final Map<String, String> properties = new HashMap<>();
    properties.put(name, value);
    return newExtension(properties);
  }

  private static StepObj<?> intercept(final ReadableReflectiveNameExtension extension,
                                      final RunnableStep step) {
    return extension.interceptStep(step, NullableOptional.empty());
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MIDDLE_ORDER);
  }

  @Test
  void customOrderValue() {
    assertThat(newExtensionWithProperty("stebz.readableReflectiveName.order", "12345").order())
      .isEqualTo(12345);
  }

  @Test
  void disabledExtensionShouldReturnStepUnchanged() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "this_is_step_name", REFLECTIVE_NAME, "this_is_step_name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep =
      intercept(newExtensionWithProperty("stebz.readableReflectiveName.enabled", "false"), step);

    assertThat(resultStep.getName()).isEqualTo("this_is_step_name");
  }

  @Test
  void underscoresShouldBeReplacedWithSpacesWhenNameIsNotCustomized() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "this_is_step_name", REFLECTIVE_NAME, "this_is_step_name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep.getName()).isEqualTo("this is step name");
    assertThat(resultStep.get(REFLECTIVE_NAME)).isEqualTo("this_is_step_name");
  }

  @Test
  void consecutiveUnderscoresShouldBeReplacedEachWithSingleSpace() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "step__name", REFLECTIVE_NAME, "step__name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep.getName()).isEqualTo("step  name");
  }

  @Test
  void customizedStepNameShouldBeKept() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "Custom step name", REFLECTIVE_NAME, "reflective_name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep.getName()).isEqualTo("Custom step name");
  }

  @Test
  void stepWithoutReflectiveNameShouldBeKeptUnchanged() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "step name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep.getName()).isEqualTo("step name");
  }

  @Test
  void stepNameWithoutUnderscoresShouldBeKeptUnchanged() {
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, "step name", REFLECTIVE_NAME, "step name"),
      RunnableStep.emptyBody()
    );

    final StepObj<?> resultStep = intercept(newExtension(new HashMap<>()), step);

    assertThat(resultStep.getName()).isEqualTo("step name");
  }

  @Test
  void ctorShouldUseStartupPropertiesReader() throws Exception {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.readableReflectiveName.order", "12345");
    final Method setMethod = StartupPropertiesReader.class.getDeclaredMethod("set", PropertiesReader.class);
    setMethod.setAccessible(true);
    final Method clearMethod = StartupPropertiesReader.class.getDeclaredMethod("clear");
    clearMethod.setAccessible(true);
    try {
      setMethod.invoke(null, new PropertiesReader.Of(properties));
      assertThat(new ReadableReflectiveNameExtension().order())
        .isEqualTo(12345);
    } finally {
      clearMethod.invoke(null);
    }
  }
}
