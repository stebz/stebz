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
package org.stebz.gherkin.annotation;

import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.step.StepObj;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.util.container.NullableOptional;
import org.stebz.util.property.PropertiesReader;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.stebz.annotation.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.core.attribute.StepAttribute.NAME;
import static org.stebz.gherkin.annotation.GherkinAnnotations.GHERKIN_KEYWORD;

/**
 * Tests for {@link GherkinAnnotations} {@code interceptStep} logic.
 */
final class GherkinAnnotationsInterceptTest {

  private static GherkinAnnotations newExtension(final Map<String, String> properties) {
    return new GherkinAnnotations(new PropertiesReader.Of(properties));
  }

  private static Annotation givenAnnotation() {
    return MethodSteps.gherkinGivenAnnotationWithoutName().get(GHERKIN_KEYWORD);
  }

  private static Annotation whenAnnotation() {
    return MethodSteps.gherkinWhenAnnotationWithName().get(GHERKIN_KEYWORD);
  }

  @Test
  void orderDefaultValue() {
    assertThat(newExtension(new HashMap<>()).order())
      .isEqualTo(StebzExtension.MID_EARLY_ORDER);
  }

  @Test
  void customOrderValue() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.annotation.order", "12345");
    assertThat(newExtension(properties).order())
      .isEqualTo(12345);
  }

  @Test
  void disabledExtensionReturnsStepUnchanged() {
    final Map<String, String> properties = new HashMap<>();
    properties.put("stebz.gherkin.annotation.enabled", "false");
    final RunnableStep step = RunnableStep.empty().with(GHERKIN_KEYWORD, givenAnnotation());

    assertThat(newExtension(properties).interceptStep(step, NullableOptional.empty()))
      .isSameAs(step);
    assertThat(step.getKeyword().value())
      .isEmpty();
  }

  @Test
  void stepWithoutAnnotationReturnsStepUnchanged() {
    final RunnableStep step = RunnableStep.empty().with(NAME, "step name");

    assertThat(newExtension(new HashMap<>()).interceptStep(step, NullableOptional.empty()))
      .isSameAs(step);
    assertThat(step.getKeyword().value())
      .isEmpty();
    assertThat(step.getName())
      .isEqualTo("step name");
  }

  @Test
  void unknownAnnotationTypeReturnsStepUnchanged() {
    final Annotation unknownAnnotation = (Annotation) Proxy.newProxyInstance(
      UnknownAnnotation.class.getClassLoader(),
      new Class<?>[]{UnknownAnnotation.class},
      (proxy, method, args) -> UnknownAnnotation.class);
    final RunnableStep step = RunnableStep.empty().with(GHERKIN_KEYWORD, unknownAnnotation);

    assertThat(newExtension(new HashMap<>()).interceptStep(step, NullableOptional.empty()))
      .isSameAs(step);
    assertThat(step.getKeyword().value())
      .isEmpty();
  }

  @Test
  void annotationValueOverridesExistingName() {
    final RunnableStep step = RunnableStep.empty()
      .with(NAME, "step name")
      .with(GHERKIN_KEYWORD, whenAnnotation());

    final StepObj<?> intercepted = newExtension(new HashMap<>())
      .interceptStep(step, NullableOptional.empty());
    assertThat(intercepted.getKeyword().value())
      .isEqualTo("When");
    assertThat(intercepted.getName())
      .isEqualTo("custom name");
  }

  @Test
  void emptyAnnotationValueKeepsExistingName() {
    final RunnableStep step = RunnableStep.empty()
      .with(NAME, "step name")
      .with(GHERKIN_KEYWORD, givenAnnotation());

    final StepObj<?> intercepted = newExtension(new HashMap<>())
      .interceptStep(step, NullableOptional.empty());
    assertThat(intercepted.getKeyword().value())
      .isEqualTo("Given");
    assertThat(intercepted.getName())
      .isEqualTo("step name");
  }

  @Test
  void emptyAnnotationValueUsesReflectiveName() {
    final RunnableStep step = RunnableStep.empty()
      .with(REFLECTIVE_NAME, "reflective name")
      .with(GHERKIN_KEYWORD, givenAnnotation());

    final StepObj<?> intercepted = newExtension(new HashMap<>())
      .interceptStep(step, NullableOptional.empty());
    assertThat(intercepted.getKeyword().value())
      .isEqualTo("Given");
    assertThat(intercepted.getName())
      .isEqualTo("reflective name");
  }

  @Test
  void emptyAnnotationValueWithoutNamesSetsKeywordOnly() {
    final RunnableStep step = RunnableStep.empty()
      .with(GHERKIN_KEYWORD, givenAnnotation());

    final StepObj<?> intercepted = newExtension(new HashMap<>())
      .interceptStep(step, NullableOptional.empty());
    assertThat(intercepted.getKeyword())
      .isEqualTo(Keyword.of("Given"));
    assertThat(intercepted.getName())
      .isEmpty();
  }

  @Retention(RetentionPolicy.RUNTIME)
  private @interface UnknownAnnotation {
  }
}
