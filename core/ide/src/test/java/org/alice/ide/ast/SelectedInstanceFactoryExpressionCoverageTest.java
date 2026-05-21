package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link SelectedInstanceFactoryExpression}.
 * Complements SelectedInstanceFactoryExpressionTest with deeper requiredType semantics.
 */
public class SelectedInstanceFactoryExpressionCoverageTest {

  @Test
  public void getRequiredType_returnsSameAsConstructorArg() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(type, expr.getRequiredType());
  }

  @Test
  public void getType_returnsSameAsRequiredType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(expr.getRequiredType(), expr.getType());
  }

  @Test
  public void getType_calledMultipleTimes_stableResult() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Double.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(expr.getType(), expr.getType());
  }

  @Test
  public void constructWithBooleanType() {
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(JavaType.BOOLEAN_OBJECT_TYPE);
    assertSame(JavaType.BOOLEAN_OBJECT_TYPE, expr.getType());
  }

  @Test
  public void constructWithVoidType() {
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(JavaType.VOID_TYPE);
    assertSame(JavaType.VOID_TYPE, expr.getRequiredType());
  }

  @Test
  public void extendsIdeExpression() {
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(JavaType.getInstance(String.class));
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void twoExpressionsSameType_requiredTypesEqual() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    SelectedInstanceFactoryExpression a = new SelectedInstanceFactoryExpression(type);
    SelectedInstanceFactoryExpression b = new SelectedInstanceFactoryExpression(type);
    assertSame(a.getRequiredType(), b.getRequiredType());
  }

  @Test
  public void twoExpressionsDifferentType_requiredTypesNotEqual() {
    SelectedInstanceFactoryExpression a = new SelectedInstanceFactoryExpression(JavaType.getInstance(String.class));
    SelectedInstanceFactoryExpression b = new SelectedInstanceFactoryExpression(JavaType.getInstance(Integer.class));
    assertNotEquals(a.getRequiredType(), b.getRequiredType());
  }

  @Test
  public void getRequiredType_isNotNull() {
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(JavaType.getInstance(Object.class));
    assertNotNull(expr.getRequiredType());
  }

  @Test
  public void getType_isNotNull() {
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(JavaType.getInstance(Object.class));
    assertNotNull(expr.getType());
  }
}
