package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link CurrentThisExpression}.
 * Uses reflection only — getType() calls IDE.getActiveInstance() which is unavailable headless.
 */
public class CurrentThisExpressionCoverageTest {

  @Test
  public void canConstructViaNoArgConstructor() throws Exception {
    Constructor<?> ctor = CurrentThisExpression.class.getConstructor();
    CurrentThisExpression expr = (CurrentThisExpression) ctor.newInstance();
    assertNotNull(expr);
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(CurrentThisExpression.class.getModifiers()));
  }

  @Test
  public void classIsNotFinal() {
    assertFalse(Modifier.isFinal(CurrentThisExpression.class.getModifiers()));
  }

  @Test
  public void extendsIdeExpression() {
    assertEquals(IdeExpression.class, CurrentThisExpression.class.getSuperclass());
  }

  @Test
  public void getTypeMethodReturnsAbstractType() throws Exception {
    Method m = CurrentThisExpression.class.getMethod("getType");
    assertTrue(AbstractType.class.isAssignableFrom(m.getReturnType()));
  }

  @Test
  public void getTypeMethodIsPublic() throws Exception {
    Method m = CurrentThisExpression.class.getMethod("getType");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getTypeMethodTakesNoParameters() throws Exception {
    Method m = CurrentThisExpression.class.getMethod("getType");
    assertEquals(0, m.getParameterCount());
  }

  @Test
  public void hasExactlyOneConstructor() {
    assertEquals(1, CurrentThisExpression.class.getDeclaredConstructors().length);
  }

  @Test
  public void constructorIsPublic() throws Exception {
    Constructor<?> ctor = CurrentThisExpression.class.getConstructor();
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(CurrentThisExpression.class.getModifiers()));
  }
}
