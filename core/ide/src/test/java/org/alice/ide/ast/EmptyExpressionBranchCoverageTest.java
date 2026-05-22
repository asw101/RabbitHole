package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link EmptyExpression} covering all type variants.
 */
public class EmptyExpressionBranchCoverageTest {

  @Test
  public void construct_withObjectType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Object.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void construct_withIntType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(int.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void construct_withDoubleType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(double.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void construct_withBooleanType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(boolean.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void construct_withStringType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void construct_withVoidType() {
    EmptyExpression expr = new EmptyExpression(JavaType.VOID_TYPE);
    assertNotNull(expr);
  }

  @Test
  public void construct_withArrayType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Object[].class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr);
  }

  @Test
  public void getType_returnsConstructionType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void getType_forIntType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(int.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void getType_forBooleanType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(boolean.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void isInstanceOfIdeExpression() {
    EmptyExpression expr = new EmptyExpression(JavaType.getInstance(Object.class));
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void isInstanceOfExpression() {
    EmptyExpression expr = new EmptyExpression(JavaType.getInstance(Object.class));
    assertTrue(expr instanceof Expression);
  }

  @Test
  public void twoInstances_areDifferentObjects() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Object.class);
    EmptyExpression e1 = new EmptyExpression(type);
    EmptyExpression e2 = new EmptyExpression(type);
    assertNotSame(e1, e2);
  }

  @Test
  public void twoInstances_sameType_sameGetType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Object.class);
    EmptyExpression e1 = new EmptyExpression(type);
    EmptyExpression e2 = new EmptyExpression(type);
    assertSame(e1.getType(), e2.getType());
  }

  @Test
  public void differentTypes_differentGetType() {
    EmptyExpression e1 = new EmptyExpression(JavaType.getInstance(int.class));
    EmptyExpression e2 = new EmptyExpression(JavaType.getInstance(double.class));
    assertNotSame(e1.getType(), e2.getType());
  }

  @Test
  public void className_isEmptyExpression() {
    assertEquals("EmptyExpression", EmptyExpression.class.getSimpleName());
  }

  @Test
  public void construct_withFloatType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(float.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void construct_withLongType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(long.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void construct_withByteType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(byte.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void construct_withShortType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(short.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void construct_withCharType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(char.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void multipleConstructions_succeed() {
    for (Class<?> cls : new Class<?>[]{int.class, double.class, boolean.class, String.class, Object.class}) {
      EmptyExpression expr = new EmptyExpression(JavaType.getInstance(cls));
      assertNotNull("Should construct for " + cls.getName(), expr);
      assertNotNull("Type should be non-null for " + cls.getName(), expr.getType());
    }
  }
}
