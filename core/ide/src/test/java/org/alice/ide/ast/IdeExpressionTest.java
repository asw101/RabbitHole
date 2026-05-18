package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class IdeExpressionTest {
  @Test
  public void emptyExpression_isIdeExpression() {
    EmptyExpression expr = new EmptyExpression(String.class);
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void previousValueExpression_isIdeExpression() {
    PreviousValueExpression expr = new PreviousValueExpression(String.class);
    assertTrue(expr instanceof IdeExpression);
  }

  @Test
  public void selectedInstanceFactory_isIdeExpression() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    SelectedInstanceFactoryExpression expr = new SelectedInstanceFactoryExpression(type);
    assertTrue(expr instanceof IdeExpression);
  }
}
