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

import org.junit.jupiter.api.Test;
import org.stebz.attribute.StepAttributes;
import org.stebz.step.StepObj;
import org.stebz.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.attribute.StepAttribute.NAME;

/**
 * Tests for {@link ReadableReflectiveNameExtension}.
 */
final class ReadableReflectiveNameExtensionTest {

  @Test
  void defaultWordSeparator() {
    final String sameName = "this_is_step_name";
    final RunnableStep step = RunnableStep.of(
      StepAttributes.of(NAME, sameName, REFLECTIVE_NAME, sameName),
      RunnableStep.emptyBody()
    );
    final ReadableReflectiveNameExtension extension =
      new ReadableReflectiveNameExtension(new PropertiesReader.Of(new Properties()));

    final StepObj<?> resultStep = extension.interceptStep(step, NullableOptional.empty());
    assertThat(resultStep.getName())
      .isEqualTo("this is step name");
  }
}
