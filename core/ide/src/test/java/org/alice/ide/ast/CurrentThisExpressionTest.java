package org.alice.ide.ast;

import org.junit.Test;

import static org.junit.Assert.*;

public class CurrentThisExpressionTest {
  @Test
  public void constructor_doesNotThrow() {
    CurrentThisExpression expr = new CurrentThisExpression();
    assertNotNull(expr);
  }
}
