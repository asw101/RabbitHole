package org.alice.ide.croquet.models.cascade.conditional;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadeConditionalPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade.conditional", ConditionalExpressionLeftAndRightOperandsFillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade.conditional", ReplaceOperatorInPreviousConditionalExpressionFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(ConditionalExpressionLeftAndRightOperandsFillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(ReplaceOperatorInPreviousConditionalExpressionFillIn.class.getModifiers()));
  }
}
