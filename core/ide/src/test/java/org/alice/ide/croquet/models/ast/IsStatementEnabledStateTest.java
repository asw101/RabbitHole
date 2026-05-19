package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class IsStatementEnabledStateTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(IsStatementEnabledState.class.getModifiers()));
  }
  @Test
  public void extendsBooleanState() {
    assertTrue(org.lgna.croquet.BooleanState.class.isAssignableFrom(IsStatementEnabledState.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = IsStatementEnabledState.class.getMethod("getInstance", org.lgna.project.ast.Statement.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isSynchronized(m.getModifiers()));
  }
}
