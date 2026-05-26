package org.alice.ide.ast;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FauxExpression;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class CurrentThisExpressionComprehensiveTest {
  @Test
  public void classIsPublicAndConcrete() {
    assertTrue(Modifier.isPublic(CurrentThisExpression.class.getModifiers()));
    assertFalse(Modifier.isAbstract(CurrentThisExpression.class.getModifiers()));
  }

  @Test
  public void superclassIsIdeExpression() {
    assertSame(IdeExpression.class, CurrentThisExpression.class.getSuperclass());
  }

  @Test
  public void instanceIsAlsoFauxExpressionAndExpression() {
    CurrentThisExpression expression = new CurrentThisExpression();

    assertTrue(expression instanceof IdeExpression);
    assertTrue(expression instanceof FauxExpression);
    assertTrue(expression instanceof Expression);
  }

  @Test
  public void processThrowsRuntimeException() {
    CurrentThisExpression expression = new CurrentThisExpression();

    try {
      expression.process(null);
      fail();
    } catch (RuntimeException expected) {
    }
  }

  @Test
  public void expressionIsValidByDefault() {
    assertTrue(new CurrentThisExpression().isValid());
  }

  @Test
  public void classDeclaresNoFields() {
    assertEquals(0, declaredFields(CurrentThisExpression.class).length);
  }

  @Test
  public void classHasSinglePublicNoArgConstructor() throws Exception {
    Constructor<CurrentThisExpression> constructor = CurrentThisExpression.class.getConstructor();

    assertNotNull(constructor);
    assertEquals(1, CurrentThisExpression.class.getDeclaredConstructors().length);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void getTypeMethodIsDeclaredPublicly() throws Exception {
    Method method = CurrentThisExpression.class.getMethod("getType");

    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertSame(AbstractType.class, method.getReturnType());
  }

  @Test
  public void packageNameMatchesAstPackage() {
    assertEquals("org.alice.ide.ast", CurrentThisExpression.class.getPackage().getName());
  }

  @Test
  public void classImplementsNoInterfacesDirectly() {
    assertEquals(0, CurrentThisExpression.class.getInterfaces().length);
  }

  @Test
  public void distinctInstancesRemainDistinct() {
    assertNotSame(new CurrentThisExpression(), new CurrentThisExpression());
  }

  @Test
  public void equalsUsesIdentitySemantics() {
    assertNotEquals(new CurrentThisExpression(), new CurrentThisExpression());
  }

  @Test
  public void hashCodeIsStableForSameInstance() {
    CurrentThisExpression expression = new CurrentThisExpression();

    assertEquals(expression.hashCode(), expression.hashCode());
  }

  @Test
  public void toStringIsNeverNull() {
    assertNotNull(new CurrentThisExpression().toString());
  }

  @Test
  public void declaredMethodSetIncludesGetType() {
    boolean found = false;
    for (Method method : declaredMethods(CurrentThisExpression.class)) {
      if ("getType".equals(method.getName())) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void reflectiveConstructionProducesCurrentThisExpression() throws Exception {
    assertTrue(CurrentThisExpression.class.getConstructor().newInstance() instanceof CurrentThisExpression);
  }

  @Test
  public void getTypeMethodAcceptsNoParameters() throws Exception {
    assertEquals(0, CurrentThisExpression.class.getMethod("getType").getParameterTypes().length);
  }

  @Test
  public void classNameMatchesExpectedBinaryName() {
    assertEquals("org.alice.ide.ast.CurrentThisExpression", CurrentThisExpression.class.getName());
  }

  @Test
  public void simpleNameMatchesSourceFileName() {
    assertEquals("CurrentThisExpression", CurrentThisExpression.class.getSimpleName());
  }

  @Test
  public void classDeclaresOnlyOneMethod() {
    assertEquals(1, declaredMethods(CurrentThisExpression.class).length);
  }
  @Test
  public void enclosingClassIsNull() {
    assertNull(CurrentThisExpression.class.getEnclosingClass());
  }

  @Test
  public void classIsNotEnum() {
    assertFalse(CurrentThisExpression.class.isEnum());
  }

  @Test
  public void classDeclaresNoTypeParameters() {
    assertEquals(0, CurrentThisExpression.class.getTypeParameters().length);
  }
}
