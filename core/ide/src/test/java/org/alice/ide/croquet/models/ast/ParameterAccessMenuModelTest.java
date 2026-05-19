package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ParameterAccessMenuModelTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(ParameterAccessMenuModel.class.getModifiers()));
  }
  @Test
  public void extendsPredeterminedMenuModel() {
    assertTrue(org.lgna.croquet.PredeterminedMenuModel.class.isAssignableFrom(ParameterAccessMenuModel.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = ParameterAccessMenuModel.class.getMethod("getInstance", org.lgna.project.ast.UserParameter.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
