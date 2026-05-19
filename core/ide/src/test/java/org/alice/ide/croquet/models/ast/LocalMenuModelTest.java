package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class LocalMenuModelTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(LocalMenuModel.class.getModifiers()));
  }
  @Test
  public void extendsPredeterminedMenuModel() {
    assertTrue(org.lgna.croquet.PredeterminedMenuModel.class.isAssignableFrom(LocalMenuModel.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = LocalMenuModel.class.getMethod("getInstance", org.lgna.project.ast.UserLocal.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
