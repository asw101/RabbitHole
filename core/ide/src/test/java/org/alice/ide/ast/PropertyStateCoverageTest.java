package org.alice.ide.ast;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PropertyState}.
 * Reflection-only — PropertyState is abstract and needs Group/IDE infrastructure to construct.
 */
public class PropertyStateCoverageTest {

  @Test
  public void isAbstractClass() {
    assertTrue(Modifier.isAbstract(PropertyState.class.getModifiers()));
  }

  @Test
  public void isPublicClass() {
    assertTrue(Modifier.isPublic(PropertyState.class.getModifiers()));
  }

  @Test
  public void extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(PropertyState.class));
  }

  @Test
  public void hasSetterField_privateFinal() throws Exception {
    Field f = PropertyState.class.getDeclaredField("setter");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
    assertEquals(JavaMethod.class, f.getType());
  }

  @Test
  public void hasGetSetterPublicMethod() throws Exception {
    Method m = PropertyState.class.getMethod("getSetter");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(JavaMethod.class, m.getReturnType());
  }

  @Test
  public void hasGetValueOrNullLiteralPublicMethod() throws Exception {
    Method m = PropertyState.class.getMethod("getValueOrNullLiteral");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(Expression.class, m.getReturnType());
  }

  @Test
  public void hasProtectedGetTypeMethod() {
    boolean found = java.util.Arrays.stream(PropertyState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getType") && Modifier.isProtected(m.getModifiers()));
    assertTrue("Must have protected getType()", found);
  }

  @Test
  public void hasProtectedGetValueDetailsMethod() {
    boolean found = java.util.Arrays.stream(PropertyState.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getValueDetails") && Modifier.isProtected(m.getModifiers()));
    assertTrue("Must have protected getValueDetails()", found);
  }

  @Test
  public void constructorTakesThreeParams() {
    Constructor<?>[] ctors = PropertyState.class.getDeclaredConstructors();
    assertEquals(1, ctors.length);
    assertEquals(3, ctors[0].getParameterCount());
  }

  @Test
  public void constructorIsPublic() {
    Constructor<?>[] ctors = PropertyState.class.getDeclaredConstructors();
    assertTrue(Modifier.isPublic(ctors[0].getModifiers()));
  }
}
