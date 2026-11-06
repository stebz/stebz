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
package org.stebz.core.context;

import dev.jlet.function.ThrowingConsumer2;
import dev.jlet.function.ThrowingConsumer3;
import dev.jlet.function.ThrowingConsumer4;
import dev.jlet.function.ThrowingConsumer5;
import dev.jlet.function.ThrowingConsumer6;
import dev.jlet.function.ThrowingConsumer7;
import dev.jlet.function.ThrowingConsumer8;
import dev.jlet.function.ThrowingFunction2;
import dev.jlet.function.ThrowingFunction3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Context} and {@link Of2}...{@link Of8} tuples.
 */
final class ContextTest {

  @Test
  void contextOf2ShouldCreateTupleWithGivenValues() {
    final Of2<String, Integer> tuple = Context.context("v1", 2);
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v2).isEqualTo(2);
  }

  @Test
  void contextOf3ShouldCreateTupleWithGivenValues() {
    final Of3<String, Integer, Boolean> tuple = Context.context("v1", 2, true);
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v2).isEqualTo(2);
    assertThat(tuple.v3).isTrue();
  }

  @Test
  void contextOf4ShouldCreateTupleWithGivenValues() {
    final Of4<String, Integer, Boolean, String> tuple = Context.context("v1", 2, true, "v4");
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v4).isEqualTo("v4");
  }

  @Test
  void contextOf5ShouldCreateTupleWithGivenValues() {
    final Of5<String, Integer, Boolean, String, Integer> tuple = Context.context("v1", 2, true, "v4", 5);
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v5).isEqualTo(5);
  }

  @Test
  void contextOf6ShouldCreateTupleWithGivenValues() {
    final Of6<String, Integer, Boolean, String, Integer, String> tuple =
      Context.context("v1", 2, true, "v4", 5, "v6");
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v6).isEqualTo("v6");
  }

  @Test
  void contextOf7ShouldCreateTupleWithGivenValues() {
    final Of7<String, Integer, Boolean, String, Integer, String, Integer> tuple =
      Context.context("v1", 2, true, "v4", 5, "v6", 7);
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v7).isEqualTo(7);
  }

  @Test
  void contextOf8ShouldCreateTupleWithGivenValues() {
    final Of8<String, Integer, Boolean, String, Integer, String, Integer, String> tuple =
      Context.context("v1", 2, true, "v4", 5, "v6", 7, "v8");
    assertThat(tuple.v1).isEqualTo("v1");
    assertThat(tuple.v8).isEqualTo("v8");
  }

  @Test
  void tuplesShouldAllowNullValues() {
    final Of2<String, Integer> tuple = Context.context(null, null);
    assertThat(tuple.v1).isNull();
    assertThat(tuple.v2).isNull();
  }

  @Test
  void contextOfConsumer2ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer2<String, Integer, Error> block = (v1, v2) -> {
      received.add(v1);
      received.add(v2);
    };
    Context.context(block).accept(Context.context("v1", 2));
    assertThat(received).containsExactly("v1", 2);
  }

  @Test
  void contextOfConsumer3ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    final ThrowingConsumer3<String, Integer, Boolean, Error> block = (v1, v2, v3) -> {
      received.add(v1);
      received.add(v2);
      received.add(v3);
    };
    Context.context(block).accept(Context.context("v1", 2, true));
    assertThat(received).containsExactly("v1", 2, true);
  }

  @Test
  void contextOfConsumer4ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    Context.context((ThrowingConsumer4<String, Integer, Boolean, String, Error>)
      (v1, v2, v3, v4) -> received.add(v4)).accept(Context.context("v1", 2, true, "v4"));
    assertThat(received).containsExactly("v4");
  }

  @Test
  void contextOfConsumer5ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    Context.context((ThrowingConsumer5<String, Integer, Boolean, String, Integer, Error>)
      (v1, v2, v3, v4, v5) -> received.add(v5)).accept(Context.context("v1", 2, true, "v4", 5));
    assertThat(received).containsExactly(5);
  }

  @Test
  void contextOfConsumer6ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    Context.context((ThrowingConsumer6<String, Integer, Boolean, String, Integer, String, Error>)
        (v1, v2, v3, v4, v5, v6) -> received.add(v6))
      .accept(Context.context("v1", 2, true, "v4", 5, "v6"));
    assertThat(received).containsExactly("v6");
  }

  @Test
  void contextOfConsumer7ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    Context.context((ThrowingConsumer7<String, Integer, Boolean, String, Integer, String, Integer, Error>)
        (v1, v2, v3, v4, v5, v6, v7) -> received.add(v7))
      .accept(Context.context("v1", 2, true, "v4", 5, "v6", 7));
    assertThat(received).containsExactly(7);
  }

  @Test
  void contextOfConsumer8ShouldUnpackTupleToBlock() {
    final List<Object> received = new ArrayList<>();
    Context.context((ThrowingConsumer8<String, Integer, Boolean, String, Integer, String, Integer, String, Error>)
        (v1, v2, v3, v4, v5, v6, v7, v8) -> received.add(v8))
      .accept(Context.context("v1", 2, true, "v4", 5, "v6", 7, "v8"));
    assertThat(received).containsExactly("v8");
  }

  @Test
  void contextOfFunction2ShouldUnpackTupleToBlock() {
    final ThrowingFunction2<String, Integer, String, Error> block = (v1, v2) -> v1 + v2;
    assertThat(Context.context(block).apply(Context.context("v", 2))).isEqualTo("v2");
  }

  @Test
  void contextOfFunction3ShouldUnpackTupleToBlock() {
    final ThrowingFunction3<String, Integer, Boolean, String, Error> block =
      (v1, v2, v3) -> v1 + v2 + v3;
    assertThat(Context.context(block).apply(Context.context("v", 2, true))).isEqualTo("v2true");
  }

  @Test
  void tupledBlockShouldPropagateExceptionAsIs() {
    final IllegalStateException exception = new IllegalStateException("fail");
    assertThatThrownBy(() -> Context.context((ThrowingConsumer2<String, Integer, Error>) (v1, v2) -> {
      throw exception;
    }).accept(Context.context("v1", 2)))
      .isSameAs(exception);
  }

  @Test
  void tupledBlockFactoriesShouldThrowExceptionForNullBlock() {
    assertThatThrownBy(() -> Context.context((ThrowingConsumer2<String, Integer, Error>) null))
      .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> Context.context((ThrowingFunction2<String, Integer, String, Error>) null))
      .isInstanceOf(NullPointerException.class);
  }
}
