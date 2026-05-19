package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultNameableDeepTest {

  @Test
  public void defaultConstructor_nameIsNull() {
    DefaultNameable d = new DefaultNameable();
    assertNull(d.getName());
  }

  @Test
  public void constructorWithName() {
    DefaultNameable d = new DefaultNameable("Alice");
    assertEquals("Alice", d.getName());
  }

  @Test
  public void setName_updatesName() {
    DefaultNameable d = new DefaultNameable();
    d.setName("test");
    assertEquals("test", d.getName());
  }

  @Test
  public void setName_toNull() {
    DefaultNameable d = new DefaultNameable("initial");
    d.setName(null);
    assertNull(d.getName());
  }

  @Test
  public void toString_withName() {
    DefaultNameable d = new DefaultNameable("Alice");
    String s = d.toString();
    assertTrue(s.contains("Alice"));
    assertTrue(s.contains("name="));
  }

  @Test
  public void toString_withoutName() {
    DefaultNameable d = new DefaultNameable();
    String s = d.toString();
    assertNotNull(s);
    assertFalse(s.contains("name="));
  }

  @Test
  public void setName_emptyString() {
    DefaultNameable d = new DefaultNameable("");
    assertEquals("", d.getName());
    String s = d.toString();
    assertTrue(s.contains("name="));
  }

  @Test
  public void implementsNameable() {
    DefaultNameable d = new DefaultNameable("x");
    assertTrue(d instanceof Nameable);
  }
}
