package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Expression;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link BlockStatementIndexPairContext} — wrapper that delegates to a
 * {@link BlockStatementIndexPair} and always returns null for previousExpression.
 */
public class BlockStatementIndexPairContextTest {

  @Test
  public void getPreviousExpression_alwaysReturnsNull() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);

    assertNull("BlockStatementIndexPairContext always returns null for previousExpression",
        ctx.getPreviousExpression());
  }

  @Test
  public void getBlockStatementIndexPair_returnsWrappedPair() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);

    assertSame(pair, ctx.getBlockStatementIndexPair());
  }

  @Test
  public void getBlockStatementIndexPair_preservesBlockAndIndex() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 5);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);

    BlockStatementIndexPair returned = ctx.getBlockStatementIndexPair();
    assertSame(block, returned.getBlockStatement());
    assertEquals(5, returned.getIndex());
  }

  @Test
  public void implementsExpressionCascadeContext() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);

    assertTrue(ctx instanceof ExpressionCascadeContext);
  }

  @Test
  public void getBlockStatementIndexPair_zeroIndex() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);

    assertEquals(0, ctx.getBlockStatementIndexPair().getIndex());
  }
}
