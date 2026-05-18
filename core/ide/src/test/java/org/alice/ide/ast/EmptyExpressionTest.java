package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class EmptyExpressionTest {
  @Test
  public void constructor_withType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void constructor_withClass() {
    EmptyExpression expr = new EmptyExpression(Integer.class);
    assertEquals(JavaType.getInstance(Integer.class), expr.getType());
  }

  @Test
  public void constructor_withObjectType() {
    EmptyExpression expr = new EmptyExpression(Object.class);
    assertEquals(JavaType.getInstance(Object.class), expr.getType());
  }

  @Test
  public void getType_returnsProvidedType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Double.class);
    EmptyExpression expr = new EmptyExpression(type);
    assertNotNull(expr.getType());
    assertSame(type, expr.getType());
  }

  @Test
  public void isNotNull() {
    EmptyExpression expr = new EmptyExpression(String.class);
    assertNotNull(expr);
  }
}
