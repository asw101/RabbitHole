package org.lgna.croquet.resolvers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link RuntimeResolver} interface contract.
 */
public class RuntimeResolverTest {

  @Test
  public void isInterface() {
    assertTrue(RuntimeResolver.class.isInterface());
  }

  @Test
  public void hasGetResolved() throws NoSuchMethodException {
    assertNotNull(RuntimeResolver.class.getDeclaredMethod("getResolved"));
  }

  @Test
  public void singleMethod() {
    assertEquals(1, RuntimeResolver.class.getDeclaredMethods().length);
  }

  @Test
  public void lambdaImplementation() {
    RuntimeResolver<String> resolver = () -> "resolved";
    assertEquals("resolved", resolver.getResolved());
  }

  @Test
  public void nullReturn() {
    RuntimeResolver<Object> resolver = () -> null;
    assertNull(resolver.getResolved());
  }

  @Test
  public void mutableResolution() {
    java.util.concurrent.atomic.AtomicReference<String> ref = new java.util.concurrent.atomic.AtomicReference<>("first");
    RuntimeResolver<String> resolver = ref::get;
    assertEquals("first", resolver.getResolved());
    ref.set("second");
    assertEquals("second", resolver.getResolved());
  }

  @Test
  public void genericType_integer() {
    RuntimeResolver<Integer> resolver = () -> 42;
    assertEquals(Integer.valueOf(42), resolver.getResolved());
  }
}
