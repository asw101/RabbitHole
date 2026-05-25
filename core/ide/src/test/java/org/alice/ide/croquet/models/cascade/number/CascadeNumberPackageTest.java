package org.alice.ide.croquet.models.cascade.number;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeNumberPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.number", NextDouble01FillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.number", PowerCascadeMenu.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(NextDouble01FillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(PowerCascadeMenu.class.getModifiers()));
  }
}
