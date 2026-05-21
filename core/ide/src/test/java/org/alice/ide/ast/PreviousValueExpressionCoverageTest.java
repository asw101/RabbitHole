package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PreviousValueExpression}.
 * Complements PreviousValueExpressionTest with deeper type semantics.
 */
public class PreviousValueExpressionCoverageTest {

  @Test
  public void constructWithAbstractType_returnsSameInstance() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    PreviousValueExpression expr = new PreviousValueExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void constructWithClass_typeMatchesJavaTypeInstance() {
    PreviousValueExpression expr = new PreviousValueExpression(Integer.class);
    assertEquals(JavaType.getInstance(Integer.class), expr.getType());
  }

  @Test
  public void getTypeCalledMultipleTimes_returnsSameReference() {
    PreviousValueExpression expr = new PreviousValueExpression(String.class);
    assertSame(expr.getType(), expr.getType());
  }

  @Test
  public void constructWithVoidType() {
    PreviousValueExpression expr = new PreviousValueExpression(JavaType.VOID_TYPE);
    assertSame(JavaType.VOID_TYPE, expr.getType());
  }

  @Test
  public void constructWithLongClass() {
    PreviousValueExpression expr = new PreviousValueExpression(long.class);
    assertEquals(JavaType.getInstance(long.class), expr.getType());
  }

  @Test
  public void constructWithFloatClass() {
    PreviousValueExpression expr = new PreviousValueExpression(float.class);
    assertEquals(JavaType.getInstance(float.class), expr.getType());
  }

  @Test
  public void constructWithCharClass() {
    PreviousValueExpression expr = new PreviousValueExpression(char.class);
    assertEquals(JavaType.getInstance(char.class), expr.getType());
  }

  @Test
  public void constructWithArrayType_notNull() {
    PreviousValueExpression expr = new PreviousValueExpression(int[].class);
    assertNotNull(expr.getType());
  }

  @Test
  public void instanceIsIdeExpression() {
    PreviousValueExpression expr = new PreviousValueExpression(String.class);
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void twoInstancesSameType_typesEqual() {
    PreviousValueExpression a = new PreviousValueExpression(Double.class);
    PreviousValueExpression b = new PreviousValueExpression(Double.class);
    assertEquals(a.getType(), b.getType());
  }
}
