package org.lgna.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link Initializer} interface contract.
 */
public class InitializerTest {

  @Test
  public void isInterface() {
    assertTrue(Initializer.class.isInterface());
  }

  @Test
  public void hasInitializeMethod() throws NoSuchMethodException {
    assertNotNull(Initializer.class.getDeclaredMethod("initialize", Object.class));
  }

  @Test
  public void singleMethod() {
    assertEquals(1, Initializer.class.getDeclaredMethods().length);
  }

  @Test
  public void lambdaImplementation_noThrow() {
    Initializer<String> init = value -> {};
    init.initialize("test");
  }

  @Test
  public void lambdaImplementation_capturesValue() {
    java.util.concurrent.atomic.AtomicReference<String> captured = new java.util.concurrent.atomic.AtomicReference<>();
    Initializer<String> init = captured::set;
    init.initialize("hello");
    assertEquals("hello", captured.get());
  }

  @Test
  public void nullValue() {
    java.util.concurrent.atomic.AtomicReference<String> captured = new java.util.concurrent.atomic.AtomicReference<>("initial");
    Initializer<String> init = captured::set;
    init.initialize(null);
    assertNull(captured.get());
  }

  @Test
  public void multipleInvocations() {
    java.util.concurrent.atomic.AtomicInteger count = new java.util.concurrent.atomic.AtomicInteger();
    Initializer<Object> init = v -> count.incrementAndGet();
    init.initialize("a");
    init.initialize("b");
    init.initialize("c");
    assertEquals(3, count.get());
  }
}
