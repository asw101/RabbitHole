package org.alice.ide.croquet.models.ast.declaration;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AstDeclarationPackageTest {
  @Test
  public void representativeClassesRemainInPackage() {
    assertEquals("org.alice.ide.croquet.models.ast.declaration", OtherTypesMenuModel.class.getPackage().getName());
    assertEquals("org.alice.ide.croquet.models.ast.declaration", TypeFillIn.class.getPackage().getName());
  }

  @Test
  public void representativeClassesRemainConcreteTypes() {
    assertFalse(Modifier.isInterface(OtherTypesMenuModel.class.getModifiers()));
    assertFalse(Modifier.isInterface(TypeFillIn.class.getModifiers()));
  }
}
