package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionPropertyContext} covering expression property resolution
 * and block statement index pair computation from the AST hierarchy.
 */
public class ExpressionPropertyContextDeepTest {

  @Test
  public void getPreviousExpression_returnsPropertyValue() {
    DoubleLiteral literal = new DoubleLiteral(3.14);
    ExpressionProperty prop = createExpressionPropertyWithValue(literal);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(prop);
    assertSame(literal, ctx.getPreviousExpression());
  }

  @Test
  public void getPreviousExpression_nullValue_returnsNullLiteral() {
    // When property created with NullLiteral, getPreviousExpression returns it
    ExpressionProperty prop = createExpressionPropertyWithValue(null);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(prop);
    assertNotNull("Value should be the NullLiteral used as fallback", ctx.getPreviousExpression());
  }

  @Test
  public void getBlockStatementIndexPair_statementInBlock_returnsPair() {
    BlockStatement block = new BlockStatement();
    // Create a simple ExpressionStatement and add it to block
    NullLiteral literal = new NullLiteral();
    ExpressionStatement stmt = new ExpressionStatement(literal);
    block.statements.add(stmt);

    // The expression property is on the stmt, which is inside the block
    ExpressionProperty prop = stmt.expression;
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(prop);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull(pair);
    assertSame(block, pair.getBlockStatement());
    assertEquals(0, pair.getIndex());
  }

  @Test
  public void getBlockStatementIndexPair_secondStatementInBlock_returnsCorrectIndex() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement first = new ExpressionStatement(new NullLiteral());
    block.statements.add(first);

    StringLiteral literal = new StringLiteral("test");
    ExpressionStatement second = new ExpressionStatement(literal);
    block.statements.add(second);

    ExpressionPropertyContext ctx = new ExpressionPropertyContext(second.expression);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull(pair);
    assertSame(block, pair.getBlockStatement());
    assertEquals(1, pair.getIndex());
  }

  @Test
  public void getBlockStatementIndexPair_noBlockStatementParent_returnsNull() {
    // An expression property on a standalone expression with no block parent
    DoubleLiteral literal = new DoubleLiteral(1.0);
    ExpressionProperty prop = createExpressionPropertyWithValue(literal);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(prop);
    // The owner (a standalone expression) has no block statement ancestor
    assertNull(ctx.getBlockStatementIndexPair());
  }

  @Test
  public void implementsExpressionCascadeContext() {
    ExpressionProperty prop = createExpressionPropertyWithValue(new NullLiteral());
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(prop);
    assertTrue(ctx instanceof ExpressionCascadeContext);
  }

  // Helper: create a simple ExpressionProperty containing a given value
  private ExpressionProperty createExpressionPropertyWithValue(Expression value) {
    ExpressionStatement dummy = new ExpressionStatement(value != null ? value : new NullLiteral());
    return dummy.expression;
  }
}
