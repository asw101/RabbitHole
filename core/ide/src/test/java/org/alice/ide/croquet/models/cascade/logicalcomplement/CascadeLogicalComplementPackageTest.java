package org.alice.ide.croquet.models.cascade.logicalcomplement;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeLogicalComplementPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.logicalcomplement", LogicalComplementOperandFillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.logicalcomplement", ReduceToInnerOperandInPreviousLogicalComplementFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(LogicalComplementOperandFillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(ReduceToInnerOperandInPreviousLogicalComplementFillIn.class.getModifiers()));
  }
}
