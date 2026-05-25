package org.alice.ide.croquet.models.ast.cascade.resource;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AstCascadeResourcePackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ast.cascade.resource", ImageResourceExpressionFillIn.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ast.cascade.resource", ResourceExpressionFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(ImageResourceExpressionFillIn.class.getModifiers()));
    assertFalse(Modifier.isInterface(ResourceExpressionFillIn.class.getModifiers()));
  }
}
