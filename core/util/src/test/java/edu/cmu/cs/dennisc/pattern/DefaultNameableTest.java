package edu.cmu.cs.dennisc.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultNameableTest {

  @Test
  public void defaultConstructor_nameIsNull() {
    DefaultNameable dn = new DefaultNameable();
    assertNull(dn.getName());
  }

  @Test
  public void constructorWithName_setsName() {
    DefaultNameable dn = new DefaultNameable("test");
    assertEquals("test", dn.getName());
  }

  @Test
  public void setName_updatesName() {
    DefaultNameable dn = new DefaultNameable();
    dn.setName("updated");
    assertEquals("updated", dn.getName());
  }

  @Test
  public void setName_toNull() {
    DefaultNameable dn = new DefaultNameable("initial");
    dn.setName(null);
    assertNull(dn.getName());
  }

  @Test
  public void toString_withName() {
    DefaultNameable dn = new DefaultNameable("hello");
    String s = dn.toString();
    assertTrue(s.contains("hello"));
    assertTrue(s.contains("name="));
  }

  @Test
  public void toString_withoutName() {
    DefaultNameable dn = new DefaultNameable();
    String s = dn.toString();
    assertNotNull(s);
    // Without a name, falls back to Object.toString()
    assertFalse(s.contains("name="));
  }

  @Test
  public void getName_afterMultipleSets() {
    DefaultNameable dn = new DefaultNameable();
    dn.setName("first");
    dn.setName("second");
    dn.setName("third");
    assertEquals("third", dn.getName());
  }

  @Test
  public void constructorWithEmptyName() {
    DefaultNameable dn = new DefaultNameable("");
    assertEquals("", dn.getName());
    assertTrue(dn.toString().contains("name="));
  }
}
