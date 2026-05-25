package org.alice.ide.croquet.models.cascade;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CascadePackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.cascade", ExpressionBlank.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.cascade", MethodInvocationFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(ExpressionBlank.class.getModifiers()));
    assertFalse(Modifier.isInterface(MethodInvocationFillIn.class.getModifiers()));
  }
}
