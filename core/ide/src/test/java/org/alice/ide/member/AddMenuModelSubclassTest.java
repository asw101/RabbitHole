package org.alice.ide.member;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Consolidated test for AddFunctionMenuModel and AddProcedureMenuModel —
 * both are final subclasses of AddMethodMenuModel with identical contract.
 */
@RunWith(Parameterized.class)
public class AddMenuModelSubclassTest {

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> subclasses() {
    return Arrays.asList(new Object[][] {
        {"AddFunctionMenuModel", AddFunctionMenuModel.class},
        {"AddProcedureMenuModel", AddProcedureMenuModel.class},
    });
  }

  private final String name;
  private final Class<?> subclass;

  public AddMenuModelSubclassTest(String name, Class<?> subclass) {
    this.name = name;
    this.subclass = subclass;
  }

  @Test
  public void constructs() throws Exception {
    assertNotNull(subclass.getDeclaredConstructor().newInstance());
  }

  @Test
  public void isFinal() {
    assertTrue(Modifier.isFinal(subclass.getModifiers()));
  }

  @Test
  public void extendsAddMethodMenuModel() {
    assertEquals(AddMethodMenuModel.class, subclass.getSuperclass());
  }

  @Test
  public void declaresGetAddMethodComposite() throws Exception {
    Method method = subclass.getDeclaredMethod("getAddMethodComposite", org.lgna.project.ast.NamedUserType.class);
    assertEquals(org.alice.ide.ast.declaration.AddMethodComposite.class, method.getReturnType());
  }
}
