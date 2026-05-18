package org.lgna.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Initializer} interface — simple functional interface
 * with a single initialize(T) method.
 */
public class InitializerTest {

  @Test
  public void initializer_canBeImplemented() {
    Initializer<StringBuilder> init = sb -> sb.append("initialized");
    StringBuilder sb = new StringBuilder();
    init.initialize(sb);
    assertEquals("initialized", sb.toString());
  }

  @Test
  public void initializer_nullArgument() {
    int[] count = {0};
    Initializer<Object> init = obj -> count[0]++;
    init.initialize(null);
    assertEquals(1, count[0]);
  }

  @Test
  public void initializer_multipleInvocations() {
    java.util.List<String> items = new java.util.ArrayList<>();
    Initializer<String> init = items::add;
    init.initialize("a");
    init.initialize("b");
    init.initialize("c");
    assertEquals(3, items.size());
  }

  @Test
  public void initializer_lambda() {
    int[] result = {0};
    Initializer<Integer> init = value -> result[0] = value;
    init.initialize(42);
    assertEquals(42, result[0]);
  }

  @Test
  public void initializer_methodReference() {
    java.util.List<String> list = new java.util.ArrayList<>();
    Initializer<String> init = list::add;
    init.initialize("test");
    assertTrue(list.contains("test"));
  }

  @Test
  public void initializer_anonymousClass() {
    final int[] counter = {0};
    Initializer<String> init = new Initializer<String>() {
      @Override
      public void initialize(String value) {
        counter[0] = value.length();
      }
    };
    init.initialize("hello");
    assertEquals(5, counter[0]);
  }
}
