package org.alice.ide.ast;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class CurrentThisExpressionTest {
  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(CurrentThisExpression.class.getModifiers()));
  }
  @Test
  public void extendsIdeExpression() {
    assertEquals(IdeExpression.class, CurrentThisExpression.class.getSuperclass());
  }
  @Test
  public void hasNoArgConstructor() throws Exception {
    assertNotNull(CurrentThisExpression.class.getConstructor());
  }
  @Test
  public void hasGetTypeMethod() throws Exception {
    Method m = CurrentThisExpression.class.getMethod("getType");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}
