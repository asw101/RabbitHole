package org.alice.ide.cascade.fillerinners;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class DoubleFillerInnerStructureTest {
  @Test
  public void classExtendsAbstractNumberFillerInner() {
    assertEquals(AbstractNumberFillerInner.class, DoubleFillerInner.class.getSuperclass());
    assertTrue(Modifier.isPublic(DoubleFillerInner.class.getModifiers()));
  }

  @Test
  public void getLiteralsWithoutDetailsUsesExpectedDefaults() {
    assertArrayEquals(new double[] {0.0, 0.25, 0.5, 1.0, 2.0, 10.0}, DoubleFillerInner.getLiterals(null), 0.0);
  }

  @Test
  public void constructorAndCustomCreatorMethodAreAvailable() throws Exception {
    assertNotNull(DoubleFillerInner.class.getConstructor());
    Method method = DoubleFillerInner.class.getDeclaredMethod("getCustomCreatorCompositeFor", org.lgna.project.annotations.ValueDetails.class);
    assertEquals(org.alice.ide.custom.ExpressionWithRecentValuesCreatorComposite.class, method.getReturnType());
  }
}
