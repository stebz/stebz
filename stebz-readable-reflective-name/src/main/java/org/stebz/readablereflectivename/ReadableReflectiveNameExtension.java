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

import org.stebz.core.executor.StartupPropertiesReader;
import org.stebz.core.extension.InterceptStep;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import static org.stebz.annotation.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;

/**
 * Readable reflective name {@link StebzExtension}.
 * <p>Replaces underscores with spaces in the step name if the step name is equal to the reflective name
 * (i.e. the step name was not customized).
 */
public class ReadableReflectiveNameExtension implements InterceptStep {
  private final boolean enabled;
  private final int order;

  /**
   * Ctor.
   */
  public ReadableReflectiveNameExtension() {
    this(StartupPropertiesReader.get());
  }

  /**
   * Ctor.
   *
   * @param properties the properties reader
   */
  public ReadableReflectiveNameExtension(final PropertiesReader properties) {
    this.enabled = properties.getBoolean("stebz.readableReflectiveName.enabled", true);
    this.order = properties.getInteger("stebz.readableReflectiveName.order", MIDDLE_ORDER);
  }

  @Override
  public int order() {
    return this.order;
  }

  @Override
  public StepObj<?> interceptStep(final StepObj<?> step,
                                  final NullableOptional<Object> context) {
    if (!this.enabled) {
      return step;
    }
    final String reflectiveName = step.get(REFLECTIVE_NAME);
    if (!reflectiveName.equals(step.getName()) || reflectiveName.indexOf('_') == -1) {
      return step;
    }
    return step.withName(reflectiveName.replace('_', ' '));
  }
}
