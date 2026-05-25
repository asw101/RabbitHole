package org.alice.ide.croquet.models.cascade.string;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeStringPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.string", StringComparisonCascadeMenu.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.string", StringConcatinationRightOperandOnlyFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(StringComparisonCascadeMenu.class.getModifiers()));
    assertFalse(Modifier.isInterface(StringConcatinationRightOperandOnlyFillIn.class.getModifiers()));
  }
}
