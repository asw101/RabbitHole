package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MethodMenuModelsTest {
  @Test
  public void methodHeaderMenuModel_isPublic() {
    assertTrue(Modifier.isPublic(MethodHeaderMenuModel.class.getModifiers()));
  }
  @Test
  public void methodHeaderMenuModel_hasGetInstance() throws Exception {
    Method m = MethodHeaderMenuModel.class.getMethod("getInstance", org.lgna.project.ast.UserMethod.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void methodTemplateMenuModel_isPublic() {
    assertTrue(Modifier.isPublic(MethodTemplateMenuModel.class.getModifiers()));
  }
  @Test
  public void methodTemplateMenuModel_hasGetInstance() throws Exception {
    Method m = MethodTemplateMenuModel.class.getMethod("getInstance", org.lgna.project.ast.UserMethod.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
  @Test
  public void methodTemplateMenuModel_extendsPredeterminedMenuModel() {
    assertTrue(org.lgna.croquet.PredeterminedMenuModel.class.isAssignableFrom(MethodTemplateMenuModel.class));
  }
}
