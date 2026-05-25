package org.alice.ide.croquet.models.cascade.integer;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeIntegerPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.integer", MathCascadeMenu.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.integer", RandomCascadeMenu.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(MathCascadeMenu.class.getModifiers()));
    assertFalse(Modifier.isInterface(RandomCascadeMenu.class.getModifiers()));
  }
}
