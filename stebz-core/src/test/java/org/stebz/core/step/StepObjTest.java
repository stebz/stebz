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
package org.stebz.core.step;

import org.junit.jupiter.api.Test;
import org.stebz.core.attribute.Keyword;
import org.stebz.core.attribute.SimpleStepAttribute;
import org.stebz.core.attribute.StepAttribute;
import org.stebz.core.attribute.StepAttributes;
import org.stebz.core.step.executable.RunnableStep;
import org.stebz.core.step.executable.alias.RStep;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StepObj} attribute methods on a {@link RunnableStep} as a concrete step type.
 */
final class StepObjTest {
  private static final SimpleStepAttribute<String> CUSTOM =
    SimpleStepAttribute.nullable("custom", "default");

  private static RStep step() {
    return RunnableStep.of("name", () -> { });
  }

  @Test
  void containsShouldReturnPresenceOfAttribute() {
    assertThat(step().contains(StepAttribute.NAME)).isTrue();
    assertThat(step().contains(StepAttribute.COMMENT)).isFalse();
  }

  @Test
  void getShouldReturnValueOrAttributeDefault() {
    assertThat(step().get(StepAttribute.NAME)).isEqualTo("name");
    assertThat(step().get(CUSTOM)).isEqualTo("default");
  }

  @Test
  void getShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> step().get(null))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void isShouldReturnBooleanAttributeValue() {
    assertThat(step().is(StepAttribute.HIDING)).isFalse();
    assertThat(step().withHiding().is(StepAttribute.HIDING)).isTrue();
  }

  @Test
  void withAttributeShouldSetDefaultValue() {
    assertThat(step().with(StepAttribute.EXPECTED_RESULT).getExpectedResult()).isEmpty();
  }

  @Test
  void withAttributeValueShouldSetValue() {
    assertThat(step().with(CUSTOM, "value").get(CUSTOM)).isEqualTo("value");
  }

  @Test
  void withTwoAttributesShouldSetBothValues() {
    final RStep result = step()
      .with(StepAttribute.EXPECTED_RESULT, "expected", StepAttribute.COMMENT, "comment");
    assertThat(result.getExpectedResult()).isEqualTo("expected");
    assertThat(result.getComment()).isEqualTo("comment");
  }

  @Test
  void withThreeAttributesShouldSetAllValues() {
    final RStep result = step()
      .with(StepAttribute.EXPECTED_RESULT, "expected", StepAttribute.COMMENT, "comment",
        StepAttribute.HIDING, true);
    assertThat(result.getExpectedResult()).isEqualTo("expected");
    assertThat(result.getComment()).isEqualTo("comment");
    assertThat(result.isHidden()).isTrue();
  }

  @Test
  void withOfShouldGenerateValueFromCurrent() {
    final RStep result = step().withOf(StepAttribute.NAME, name -> name + "!");
    assertThat(result.getName()).isEqualTo("name!");
  }

  @Test
  void withoutShouldRemoveAttribute() {
    assertThat(step().without(StepAttribute.NAME).contains(StepAttribute.NAME)).isFalse();
  }

  @Test
  void withMethodsShouldThrowExceptionForNullAttribute() {
    assertThatThrownBy(() -> step().with((StepAttribute<?, ?, ?>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step().with(null, "value"))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step().without(null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> step().withOf(null, value -> value))
      .isInstanceOf(NullPointerException.class);
  }

  @Test
  void withAttributesShouldReplaceAllAttributes() {
    final RStep result = step().withAttributes(StepAttributes.of(StepAttribute.COMMENT, "comment"));
    assertThat(result.getName()).isEmpty();
    assertThat(result.getComment()).isEqualTo("comment");
  }

  @Test
  void keywordMethodsShouldSetAndGetKeyword() {
    assertThat(step().withKeyword(Keyword.of("Given")).getKeyword()).isEqualTo(Keyword.of("Given"));
    assertThat(step().withKeyword(keyword -> Keyword.of("When")).getKeyword())
      .isEqualTo(Keyword.of("When"));
    assertThat(step().withoutKeyword().contains(StepAttribute.KEYWORD)).isFalse();
  }

  @Test
  void nameMethodsShouldSetAndGetName() {
    assertThat(step().withName("new").getName()).isEqualTo("new");
    assertThat(step().withName(name -> name + "!").getName()).isEqualTo("name!");
    assertThat(step().withoutName().contains(StepAttribute.NAME)).isFalse();
  }

  @Test
  void paramsMethodsShouldSetUpdateAndRemoveParams() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    final RStep withParams = step().withParams(params);
    assertThat(withParams.getParams()).containsEntry("param1", "value1");

    final RStep withConsumer = withParams.withParams(map -> map.put("param2", "value2"));
    assertThat(withConsumer.getParams())
      .containsEntry("param1", "value1")
      .containsEntry("param2", "value2");

    assertThat(withConsumer.withParam("param3", "value3").getParams()).containsEntry("param3", "value3");
    assertThat(withConsumer.withAddedParam("param3", "value3").getParams()).containsEntry("param3", "value3");

    final Map<String, Object> added = new LinkedHashMap<>();
    added.put("param4", "value4");
    assertThat(withConsumer.withAddedParams(added).getParams()).containsEntry("param4", "value4");

    assertThat(withConsumer.withoutParam("param1").getParams()).doesNotContainKey("param1");
    assertThat(withConsumer.withoutParams("param1", "param2").getParams()).isEmpty();
    assertThat(withConsumer.withoutParams().contains(StepAttribute.PARAMS)).isFalse();
  }

  @Test
  void maskedParamsMethodsShouldMaskValues() {
    final Map<String, Object> params = new LinkedHashMap<>();
    params.put("param1", "value1");
    params.put("param2", "value2");
    final RStep withParams = step().withParams(params);

    assertThat(withParams.withMaskedParam("param1").getParams())
      .containsEntry("param1", StepObj.MASKED_PARAM_VALUE)
      .containsEntry("param2", "value2");

    assertThat(withParams.withMaskedParams("param1", "param2").getParams())
      .containsEntry("param1", StepObj.MASKED_PARAM_VALUE)
      .containsEntry("param2", StepObj.MASKED_PARAM_VALUE);

    final Map<String, Object> toMask = new LinkedHashMap<>();
    toMask.put("param1", "ignored");
    assertThat(withParams.withMaskedParams(toMask).getParams())
      .containsEntry("param1", StepObj.MASKED_PARAM_VALUE)
      .containsEntry("param2", "value2");

    assertThat(withParams.withMaskedParams().getParams())
      .containsEntry("param1", StepObj.MASKED_PARAM_VALUE)
      .containsEntry("param2", StepObj.MASKED_PARAM_VALUE);
  }

  @Test
  void expectedResultMethodsShouldSetAndGetExpectedResult() {
    assertThat(step().withExpectedResult("expected").getExpectedResult()).isEqualTo("expected");
    assertThat(step().withExpectedResult("expected").withExpectedResult(result -> result + "!")
      .getExpectedResult()).isEqualTo("expected!");
    assertThat(step().withoutExpectedResult().contains(StepAttribute.EXPECTED_RESULT)).isFalse();
  }

  @Test
  void commentMethodsShouldSetAndGetComment() {
    assertThat(step().withComment("comment").getComment()).isEqualTo("comment");
    assertThat(step().withComment("comment").withComment(comment -> comment + "!").getComment())
      .isEqualTo("comment!");
    assertThat(step().withoutComment().contains(StepAttribute.COMMENT)).isFalse();
  }

  @Test
  void hidingMethodsShouldSetAndGetHiding() {
    assertThat(step().isHidden()).isFalse();
    assertThat(step().withHiding().isHidden()).isTrue();
    assertThat(step().withHiding(false).isHidden()).isFalse();
    assertThat(step().withHiding().withoutHiding().contains(StepAttribute.HIDING)).isFalse();
  }

  @Test
  void stepOfShouldCreateStepsOfAllTypes() {
    assertThat(StepObj.stepOf(() -> { })).isNotNull();
    assertThat(StepObj.stepOf("name", () -> { }).getName()).isEqualTo("name");
    assertThat(StepObj.stepOf("name", "expected", () -> { }).getExpectedResult()).isEqualTo("expected");
    assertThat(StepObj.stepOf("name", java.util.Collections.emptyMap(), () -> { }).getParams()).isEmpty();
    assertThat(StepObj.stepOf("name", java.util.Collections.emptyMap(), "expected", () -> { })
      .getExpectedResult()).isEqualTo("expected");
    assertThat(StepObj.stepOf(RunnableStep.of("name", () -> { })).getName()).isEqualTo("name");
    assertThat(StepObj.stepOf(StepAttributes.empty(), () -> { })).isNotNull();

    assertThat(StepObj.stepOf(value -> { })).isNotNull();
    assertThat(StepObj.stepOf((Object value) -> value)).isNotNull();
    assertThat(StepObj.stepOf("name", value -> { }).getName()).isEqualTo("name");

    assertThat(StepObj.stepOf(() -> "result")).isNotNull();
    assertThat(StepObj.stepOf((Object value) -> value)).isNotNull();
    assertThat(StepObj.stepOf("name", () -> "result").getName()).isEqualTo("name");
  }

  @Test
  void stepOfShouldThrowExceptionForNullArgs() {
    assertThatThrownBy(() -> StepObj.stepOf((RunnableStep) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> StepObj.stepOf((String) null, () -> { }))
      .isInstanceOf(NullPointerException.class);
  }
}
