package org.alice.ide.croquet.models.ast.cascade;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AstCascadePackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ast.cascade", AbstractArgumentCascade.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ast.cascade", ProjectExpressionPropertyOperation.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(AbstractArgumentCascade.class.getModifiers()));
    assertFalse(Modifier.isInterface(ProjectExpressionPropertyOperation.class.getModifiers()));
  }
}
