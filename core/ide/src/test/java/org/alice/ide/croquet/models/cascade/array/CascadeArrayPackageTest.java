package org.alice.ide.croquet.models.cascade.array;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeArrayPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.array", ArrayAccessFillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.array", ThisFieldArrayLengthFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(ArrayAccessFillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(ThisFieldArrayLengthFillIn.class.getModifiers()));
  }
}
