package org.alice.ide.member;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AddFunctionMenuModelTest {

  @Test
  public void addFunctionMenuModel_constructs() {
    assertNotNull(new AddFunctionMenuModel());
  }

  @Test
  public void addFunctionMenuModel_isFinal() {
    assertTrue(Modifier.isFinal(AddFunctionMenuModel.class.getModifiers()));
  }

  @Test
  public void addFunctionMenuModel_extendsAddMethodMenuModel() {
    assertEquals(AddMethodMenuModel.class, AddFunctionMenuModel.class.getSuperclass());
  }

  @Test
  public void addFunctionMenuModel_declaresAddMethodCompositeFactory() throws Exception {
    Method method = AddFunctionMenuModel.class.getDeclaredMethod("getAddMethodComposite", org.lgna.project.ast.NamedUserType.class);
    assertEquals(org.alice.ide.ast.declaration.AddMethodComposite.class, method.getReturnType());
  }
}
