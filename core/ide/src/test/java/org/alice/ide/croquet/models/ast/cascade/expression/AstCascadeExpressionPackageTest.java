package org.alice.ide.croquet.models.ast.cascade.expression;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AstCascadeExpressionPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ast.cascade.expression", FieldAccessOperation.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ast.cascade.expression", ThisOperation.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(FieldAccessOperation.class.getModifiers()));
    assertFalse(Modifier.isInterface(ThisOperation.class.getModifiers()));
  }
}
