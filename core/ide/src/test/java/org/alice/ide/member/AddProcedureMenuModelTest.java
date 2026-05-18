package org.alice.ide.member;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class AddProcedureMenuModelTest {

  @Test
  public void addProcedureMenuModel_constructs() {
    assertNotNull(new AddProcedureMenuModel());
  }

  @Test
  public void addProcedureMenuModel_isFinal() {
    assertTrue(Modifier.isFinal(AddProcedureMenuModel.class.getModifiers()));
  }

  @Test
  public void addProcedureMenuModel_extendsAddMethodMenuModel() {
    assertEquals(AddMethodMenuModel.class, AddProcedureMenuModel.class.getSuperclass());
  }

  @Test
  public void addProcedureMenuModel_declaresAddMethodCompositeFactory() throws Exception {
    Method method = AddProcedureMenuModel.class.getDeclaredMethod("getAddMethodComposite", org.lgna.project.ast.NamedUserType.class);
    assertEquals(org.alice.ide.ast.declaration.AddMethodComposite.class, method.getReturnType());
  }
}
