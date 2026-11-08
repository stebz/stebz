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
package org.stebz.util.container;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link NullableOptional}.
 */
final class NullableOptionalTest {

  @Test
  void emptyStaticMethodShouldReturnEmptyInstance() {
    final NullableOptional<Object> nullableOptional = NullableOptional.empty();

    assertThat(nullableOptional)
      .isInstanceOf(NullableOptional.Empty.class);
    assertThat(nullableOptional.isEmpty())
      .isTrue();
  }

  @Test
  void ofStaticMethodShouldReturnPresentInstance() {
    final Object value = new Object();
    final NullableOptional<Object> nullableOptional = NullableOptional.of(value);

    assertThat(nullableOptional.isPresent())
      .isTrue();
    assertThat(nullableOptional.get())
      .isSameAs(value);
  }

  /**
   * Tests for {@link NullableOptional.Empty}.
   */
  @Nested
  class EmptyTest {

    @Test
    void isEmptyMethodShouldReturnTrueAndIsPresentMethodShouldReturnFalse() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();

      assertThat(nullableOptional.isEmpty())
        .isTrue();
      assertThat(nullableOptional.isPresent())
        .isFalse();
    }

    @Test
    void getMethodShouldThrowException() {
      assertThatThrownBy(() -> NullableOptional.empty().get())
        .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void orElseMethodShouldReturnOtherValue() {
      final Object other = new Object();

      assertThat(NullableOptional.empty().orElse(other))
        .isSameAs(other);
    }

    @Test
    void orElseGetMethodShouldReturnSupplierResult() {
      final Object other = new Object();

      assertThat(NullableOptional.empty().orElseGet(() -> other))
        .isSameAs(other);
    }

    @Test
    void orElseGetMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().orElseGet(null));
    }

    @Test
    void orElseThrowMethodShouldThrowException() {
      assertThatThrownBy(() -> NullableOptional.empty().orElseThrow())
        .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void orElseThrowMethodShouldThrowSuppliedException() {
      final Throwable exception = new Exception();

      assertThatThrownBy(() -> NullableOptional.empty().orElseThrow(() -> exception))
        .isSameAs(exception);
    }

    @Test
    void orElseThrowMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().orElseThrow(null));
    }

    @Test
    void nonNullMethodShouldReturnThisObject() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();

      assertThat(nullableOptional.nonNull())
        .isSameAs(nullableOptional);
    }

    @Test
    void filterMethodShouldReturnThisObject() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();

      assertThat(nullableOptional.filter(v -> true))
        .isSameAs(nullableOptional);
    }

    @Test
    void filterMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().filter(null));
    }

    @Test
    void mapMethodShouldReturnThisObject() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();

      assertThat(nullableOptional.map(v -> new Object()))
        .isSameAs(nullableOptional);
    }

    @Test
    void mapMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().map(null));
    }

    @Test
    void flatMapMethodShouldReturnThisObject() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();

      assertThat(nullableOptional.flatMap(v -> NullableOptional.empty()))
        .isSameAs(nullableOptional);
    }

    @Test
    void flatMapMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().flatMap(null));
    }

    @Test
    void ifPresentMethodShouldNotInvokeConsumer() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();
      final AtomicInteger count = new AtomicInteger();

      final NullableOptional<Object> result = nullableOptional.ifPresent(v -> count.incrementAndGet());

      assertThat(result)
        .isSameAs(nullableOptional);
      assertThat(count.get())
        .isZero();
    }

    @Test
    void ifPresentMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().ifPresent(null));
    }

    @Test
    void ifEmptyMethodShouldInvokeRunnable() {
      final NullableOptional<Object> nullableOptional = NullableOptional.empty();
      final AtomicInteger count = new AtomicInteger();

      final NullableOptional<Object> result = nullableOptional.ifEmpty(count::incrementAndGet);

      assertThat(result)
        .isSameAs(nullableOptional);
      assertThat(count.get())
        .isEqualTo(1);
    }

    @Test
    void ifEmptyMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().ifEmpty(null));
    }

    @Test
    void foldMethodShouldReturnIfEmptySupplierResult() {
      final Object other = new Object();

      assertThat(NullableOptional.empty().fold(v -> new Object(), () -> other))
        .isSameAs(other);
    }

    @Test
    void foldMethodShouldThrowNullPointerExceptionForNullArgs() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().fold(null, Object::new));
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.empty().fold(v -> new Object(), null));
    }

    @Test
    void equalsMethodShouldFollowContract() {
      assertThat(NullableOptional.empty())
        .isEqualTo(NullableOptional.empty());
      assertThat(NullableOptional.empty())
        .isEqualTo(new NullableOptional.Empty<>());
      assertThat(NullableOptional.empty())
        .isNotEqualTo(NullableOptional.of(new Object()));
      assertThat(NullableOptional.empty())
        .isNotEqualTo(NullableOptional.of(null));
      assertThat(NullableOptional.empty())
        .isNotEqualTo(null);
      assertThat(NullableOptional.empty())
        .isNotEqualTo(new Object());
    }

    @Test
    void hashCodeMethodShouldReturnZero() {
      assertThat(NullableOptional.empty().hashCode())
        .isZero();
    }

    @Test
    void toStringMethodShouldReturnDescription() {
      assertThat(NullableOptional.empty())
        .hasToString("NullableOptional[<no value>]");
    }
  }

  /**
   * Tests for {@link NullableOptional.Of}.
   */
  @Nested
  class OfTest {

    @Test
    void isEmptyMethodShouldReturnFalseAndIsPresentMethodShouldReturnTrueForNonNullValue() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(new Object());

      assertThat(nullableOptional.isEmpty())
        .isFalse();
      assertThat(nullableOptional.isPresent())
        .isTrue();
    }

    @Test
    void isEmptyMethodShouldReturnFalseAndIsPresentMethodShouldReturnTrueForNullValue() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(null);

      assertThat(nullableOptional.isEmpty())
        .isFalse();
      assertThat(nullableOptional.isPresent())
        .isTrue();
    }

    @Test
    void getMethodShouldReturnValue() {
      final Object value = new Object();

      assertThat(NullableOptional.of(value).get())
        .isSameAs(value);
    }

    @Test
    void getMethodShouldReturnNullValue() {
      assertThat(NullableOptional.of(null).get())
        .isNull();
    }

    @Test
    void orElseMethodShouldReturnValue() {
      final Object value = new Object();
      final Object other = new Object();

      assertThat(NullableOptional.of(value).orElse(other))
        .isSameAs(value);
    }

    @Test
    void orElseGetMethodShouldReturnValueWithoutSupplierInvocation() {
      final Object value = new Object();

      assertThat(NullableOptional.of(value).orElseGet(() -> {
        throw new IllegalStateException("supplier should not be called");
      })).isSameAs(value);
    }

    @Test
    void orElseGetMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).orElseGet(null));
    }

    @Test
    void orElseThrowMethodShouldReturnValue() {
      final Object value = new Object();

      assertThat(NullableOptional.of(value).orElseThrow())
        .isSameAs(value);
    }

    @Test
    void orElseThrowMethodShouldReturnValueWithoutSupplierInvocation() {
      final Object value = new Object();

      assertThat(NullableOptional.of(value).orElseThrow(() -> {
        throw new IllegalStateException("supplier should not be called");
      })).isSameAs(value);
    }

    @Test
    void orElseThrowMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).orElseThrow(null));
    }

    @Test
    void nonNullMethodShouldReturnThisObjectForNonNullValue() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(new Object());

      assertThat(nullableOptional.nonNull())
        .isSameAs(nullableOptional);
    }

    @Test
    void nonNullMethodShouldReturnEmptyObjectForNullValue() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(null);

      assertThat(nullableOptional.nonNull())
        .isNotSameAs(nullableOptional)
        .isEqualTo(NullableOptional.empty());
    }

    @Test
    void filterMethodShouldReturnThisObjectIfValueMatchesPredicate() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(new Object());

      assertThat(nullableOptional.filter(v -> true))
        .isSameAs(nullableOptional);
    }

    @Test
    void filterMethodShouldReturnEmptyObjectIfValueDoesNotMatchPredicate() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(new Object());

      assertThat(nullableOptional.filter(v -> false))
        .isNotSameAs(nullableOptional)
        .isEqualTo(NullableOptional.empty());
    }

    @Test
    void filterMethodShouldPassValueToPredicate() {
      final Object value = new Object();

      NullableOptional.of(value).filter(v -> {
        assertThat(v).isSameAs(value);
        return true;
      });
    }

    @Test
    void filterMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).filter(null));
    }

    @Test
    void mapMethodShouldReturnMappedValue() {
      assertThat(NullableOptional.of(1).map(Object::toString).get())
        .isEqualTo("1");
    }

    @Test
    void mapMethodShouldReturnPresentObjectForNullResult() {
      final NullableOptional<Object> mapped = NullableOptional.of(new Object()).map(v -> null);

      assertThat(mapped.isPresent())
        .isTrue();
      assertThat(mapped.get())
        .isNull();
    }

    @Test
    void mapMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).map(null));
    }

    @Test
    void flatMapMethodShouldReturnMapperResult() {
      assertThat(NullableOptional.of(1).flatMap(v -> NullableOptional.of("value")).get())
        .isEqualTo("value");
    }

    @Test
    void flatMapMethodShouldReturnEmptyObjectIfMapperReturnsEmptyObject() {
      assertThat(NullableOptional.of(new Object()).flatMap(v -> NullableOptional.empty()).isEmpty())
        .isTrue();
    }

    @Test
    void flatMapMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).flatMap(null));
    }

    @Test
    void flatMapMethodShouldThrowNullPointerExceptionIfMapperReturnsNull() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).flatMap(v -> null));
    }

    @Test
    void ifPresentMethodShouldInvokeConsumerWithValue() {
      final Object value = new Object();
      final List<Object> captured = new ArrayList<>();
      final NullableOptional<Object> nullableOptional = NullableOptional.of(value);

      final NullableOptional<Object> result = nullableOptional.ifPresent(captured::add);

      assertThat(result)
        .isSameAs(nullableOptional);
      assertThat(captured)
        .containsExactly(value);
    }

    @Test
    void ifPresentMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).ifPresent(null));
    }

    @Test
    void ifEmptyMethodShouldNotInvokeRunnable() {
      final NullableOptional<Object> nullableOptional = NullableOptional.of(new Object());
      final AtomicInteger count = new AtomicInteger();

      final NullableOptional<Object> result = nullableOptional.ifEmpty(count::incrementAndGet);

      assertThat(result)
        .isSameAs(nullableOptional);
      assertThat(count.get())
        .isZero();
    }

    @Test
    void ifEmptyMethodShouldThrowNullPointerExceptionForNullArg() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).ifEmpty(null));
    }

    @Test
    void foldMethodShouldReturnIfPresentFunctionResult() {
      final Object value = new Object();

      assertThat(NullableOptional.of(value).fold(v -> v, Object::new))
        .isSameAs(value);
    }

    @Test
    void foldMethodShouldThrowNullPointerExceptionForNullArgs() {
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).fold(null, Object::new));
      assertThatNullPointerException()
        .isThrownBy(() -> NullableOptional.of(new Object()).fold(v -> new Object(), null));
    }

    @Test
    void equalsMethodShouldFollowContract() {
      assertThat(NullableOptional.of(1))
        .isEqualTo(NullableOptional.of(1));
      assertThat(NullableOptional.of(null))
        .isEqualTo(NullableOptional.of(null));
      assertThat(NullableOptional.of(1))
        .isNotEqualTo(NullableOptional.of(2));
      assertThat(NullableOptional.of(1))
        .isNotEqualTo(NullableOptional.empty());
      assertThat(NullableOptional.of(1))
        .isNotEqualTo(null);
      assertThat(NullableOptional.of(1))
        .isNotEqualTo(new Object());
    }

    @Test
    void equalsMethodShouldReturnTrueForSameInstance() {
      final NullableOptional<Integer> optional = NullableOptional.of(1);

      assertThat(optional.equals(optional))
        .isTrue();
    }

    @Test
    void hashCodeMethodShouldFollowContract() {
      assertThat(NullableOptional.of(1).hashCode())
        .hasSameHashCodeAs(NullableOptional.of(1));
      assertThat(NullableOptional.of(null).hashCode())
        .isZero();
    }

    @Test
    void toStringMethodShouldReturnDescription() {
      assertThat(NullableOptional.of(1))
        .hasToString("NullableOptional[1]");
      assertThat(NullableOptional.of(null))
        .hasToString("NullableOptional[null]");
    }
  }
}
