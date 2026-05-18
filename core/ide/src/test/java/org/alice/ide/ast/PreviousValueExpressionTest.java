package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class PreviousValueExpressionTest {
  @Test
  public void constructor_withType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    PreviousValueExpression expr = new PreviousValueExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void constructor_withClass() {
    PreviousValueExpression expr = new PreviousValueExpression(Double.class);
    assertEquals(JavaType.getInstance(Double.class), expr.getType());
  }

  @Test
  public void getType_consistentAcrossCalls() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    PreviousValueExpression expr = new PreviousValueExpression(type);
    assertSame(expr.getType(), expr.getType());
  }
}
