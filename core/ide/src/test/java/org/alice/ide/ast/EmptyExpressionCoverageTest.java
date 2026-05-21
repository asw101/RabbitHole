package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EmptyExpression}.
 * Tests both constructor paths, type round-trips, and edge cases.
 */
public class EmptyExpressionCoverageTest {

  @Test
  public void constructWithAbstractType_getTypeReturnsSame() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void constructWithClass_getTypeReturnsMatchingJavaType() {
    EmptyExpression expr = new EmptyExpression(String.class);
    assertEquals(JavaType.getInstance(String.class), expr.getType());
  }

  @Test
  public void constructWithPrimitiveClass_int() {
    EmptyExpression expr = new EmptyExpression(int.class);
    assertEquals(JavaType.getInstance(int.class), expr.getType());
  }

  @Test
  public void constructWithPrimitiveClass_boolean() {
    EmptyExpression expr = new EmptyExpression(boolean.class);
    assertEquals(JavaType.getInstance(boolean.class), expr.getType());
  }

  @Test
  public void constructWithPrimitiveClass_double() {
    EmptyExpression expr = new EmptyExpression(double.class);
    assertEquals(JavaType.getInstance(double.class), expr.getType());
  }

  @Test
  public void constructWithArrayType() {
    EmptyExpression expr = new EmptyExpression(String[].class);
    assertNotNull(expr.getType());
  }

  @Test
  public void constructWithObjectClass() {
    EmptyExpression expr = new EmptyExpression(Object.class);
    assertEquals(JavaType.getInstance(Object.class), expr.getType());
  }

  @Test
  public void getTypeReturnsSameOnMultipleCalls() {
    EmptyExpression expr = new EmptyExpression(String.class);
    AbstractType<?, ?, ?> first = expr.getType();
    AbstractType<?, ?, ?> second = expr.getType();
    assertSame(first, second);
  }

  @Test
  public void twoExpressionsSameType_typesAreEqual() {
    EmptyExpression e1 = new EmptyExpression(Integer.class);
    EmptyExpression e2 = new EmptyExpression(Integer.class);
    assertEquals(e1.getType(), e2.getType());
  }

  @Test
  public void twoExpressionsDifferentType_typesNotEqual() {
    EmptyExpression e1 = new EmptyExpression(String.class);
    EmptyExpression e2 = new EmptyExpression(Integer.class);
    assertNotEquals(e1.getType(), e2.getType());
  }

  @Test
  public void extendsIdeExpression() {
    assertTrue(new EmptyExpression(String.class) instanceof IdeExpression);
  }

  @Test
  public void constructWithVoidType() {
    EmptyExpression expr = new EmptyExpression(JavaType.VOID_TYPE);
    assertSame(JavaType.VOID_TYPE, expr.getType());
  }
}
