package org.alice.ide.croquet.models.cascade.blanks;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeBlanksPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.blanks", TypeUnsetBlank.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.blanks", TypeUnsetBlank.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(TypeUnsetBlank.class.getModifiers()));
    assertFalse(Modifier.isInterface(TypeUnsetBlank.class.getModifiers()));
  }
}
