package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class RevertFieldOperationTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(RevertFieldOperation.class.getModifiers()));
  }
  @Test
  public void extendsActionOperation() {
    assertTrue(org.lgna.croquet.ActionOperation.class.isAssignableFrom(RevertFieldOperation.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = RevertFieldOperation.class.getMethod("getInstance", org.lgna.project.ast.UserField.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
