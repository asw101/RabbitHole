package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class DissolveStatementWithBodyOperationTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(DissolveStatementWithBodyOperation.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(DissolveStatementWithBodyOperation.class.getModifiers()));
  }
  @Test
  public void extendsActionOperation() {
    assertTrue(org.lgna.croquet.ActionOperation.class.isAssignableFrom(DissolveStatementWithBodyOperation.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = DissolveStatementWithBodyOperation.class.getMethod("getInstance",
        org.lgna.project.ast.AbstractStatementWithBody.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
