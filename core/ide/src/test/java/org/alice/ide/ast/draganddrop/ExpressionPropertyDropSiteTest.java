package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.croquet.DropSite;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ExpressionPropertyDropSiteTest {
  @Test
  public void implementsDropSite() {
    assertTrue(DropSite.class.isAssignableFrom(ExpressionPropertyDropSite.class));
  }
  @Test
  public void isPublicNotFinal() {
    int mods = ExpressionPropertyDropSite.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isFinal(mods));
  }
  @Test
  public void hasGetExpressionPropertyMethod() throws Exception {
    Method m = ExpressionPropertyDropSite.class.getMethod("getExpressionProperty");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasEqualsOverride() throws Exception {
    Method m = ExpressionPropertyDropSite.class.getMethod("equals", Object.class);
    assertEquals(ExpressionPropertyDropSite.class, m.getDeclaringClass());
  }
  @Test
  public void hasHashCodeOverride() throws Exception {
    Method m = ExpressionPropertyDropSite.class.getMethod("hashCode");
    assertEquals(ExpressionPropertyDropSite.class, m.getDeclaringClass());
  }
  @Test
  public void hasToStringOverride() throws Exception {
    Method m = ExpressionPropertyDropSite.class.getMethod("toString");
    assertEquals(ExpressionPropertyDropSite.class, m.getDeclaringClass());
  }
}
