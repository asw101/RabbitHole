package org.alice.ide.identifier;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdentifierNameGeneratorTest {

  private final IdentifierNameGenerator gen = IdentifierNameGenerator.SINGLETON;

  @Test
  public void convertConstantNameBasic() {
    assertEquals("myConstant", gen.convertConstantNameToMethodName("MY_CONSTANT"));
  }

  @Test
  public void convertConstantNameWithPrefix() {
    String result = gen.convertConstantNameToMethodName("MY_CONSTANT", "get");
    assertEquals("getMyConstant", result);
  }

  @Test
  public void convertConstantNameEmpty() {
    assertEquals("", gen.convertConstantNameToMethodName(""));
  }

  @Test
  public void convertConstantNameSingleWord() {
    assertEquals("hello", gen.convertConstantNameToMethodName("HELLO"));
  }

  @Test
  public void convertConstantNameWithNullPrefix() {
    assertEquals("myConstant", gen.convertConstantNameToMethodName("MY_CONSTANT", null));
  }

  @Test
  public void createIdentifierNameFromClassNameTypical() {
    assertEquals("myClass", gen.createIdentifierNameFromClassName("MyClass"));
  }

  @Test
  public void createIdentifierNameFromClassNameNull() {
    assertNull(gen.createIdentifierNameFromClassName(null));
  }

  @Test
  public void createIdentifierNameFromClassNameEmpty() {
    assertEquals("", gen.createIdentifierNameFromClassName(""));
  }

  @Test
  public void createIdentifierNameFromClassNameSingleChar() {
    assertEquals("a", gen.createIdentifierNameFromClassName("A"));
  }

  @Test
  public void createIdentifierNameFromInstanceCreationWithNull() {
    assertEquals("", gen.createIdentifierNameFromInstanceCreation(null));
  }

  @Test
  public void createIdentifierNameFromResourceKeyWithNull() {
    assertEquals("", gen.createIdentifierNameFromResourceKey(null));
  }
}
