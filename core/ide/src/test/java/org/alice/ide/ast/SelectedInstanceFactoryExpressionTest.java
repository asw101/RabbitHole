package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class SelectedInstanceFactoryExpressionTest {
  @Test
  public void constructor_setsRequiredType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(type, expr.getRequiredType());
  }

  @Test
  public void getType_returnsRequiredType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(type, expr.getType());
  }

  @Test
  public void getRequiredType_sameAsGetType() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(Double.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertSame(expr.getRequiredType(), expr.getType());
  }
}
