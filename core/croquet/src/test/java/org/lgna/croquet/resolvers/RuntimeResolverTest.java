package org.lgna.croquet.resolvers;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link RuntimeResolver} interface — simple resolver contract.
 */
public class RuntimeResolverTest {

  @Test
  public void resolver_returnsValue() {
    RuntimeResolver<String> resolver = () -> "resolved";
    assertEquals("resolved", resolver.getResolved());
  }

  @Test
  public void resolver_returnsNull() {
    RuntimeResolver<String> resolver = () -> null;
    assertNull(resolver.getResolved());
  }

  @Test
  public void resolver_returnsInteger() {
    RuntimeResolver<Integer> resolver = () -> 42;
    assertEquals(Integer.valueOf(42), resolver.getResolved());
  }

  @Test
  public void resolver_dynamicValue() {
    int[] counter = {0};
    RuntimeResolver<Integer> resolver = () -> ++counter[0];
    assertEquals(Integer.valueOf(1), resolver.getResolved());
    assertEquals(Integer.valueOf(2), resolver.getResolved());
  }

  @Test
  public void resolver_implementsInterface() {
    RuntimeResolver<String> resolver = new StringResolver("test");
    assertTrue(resolver instanceof RuntimeResolver);
    assertEquals("test", resolver.getResolved());
  }

  @Test
  public void resolver_lambdaImplementation() {
    RuntimeResolver<Boolean> resolver = () -> true;
    assertTrue(resolver.getResolved());
  }

  @Test
  public void resolver_mutableState() {
    MutableResolver resolver = new MutableResolver("initial");
    assertEquals("initial", resolver.getResolved());
    resolver.setValue("updated");
    assertEquals("updated", resolver.getResolved());
  }

  private static class StringResolver implements RuntimeResolver<String> {
    private final String value;
    StringResolver(String value) { this.value = value; }
    @Override public String getResolved() { return value; }
  }

  private static class MutableResolver implements RuntimeResolver<String> {
    private String value;
    MutableResolver(String value) { this.value = value; }
    @Override public String getResolved() { return value; }
    void setValue(String value) { this.value = value; }
  }
}
