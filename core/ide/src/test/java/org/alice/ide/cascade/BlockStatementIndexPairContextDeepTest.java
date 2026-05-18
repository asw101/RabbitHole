package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link BlockStatementIndexPairContext} covering its ExpressionCascadeContext
 * contract: always returns null for previous expression, and returns the stored pair.
 */
public class BlockStatementIndexPairContextDeepTest {

  @Test
  public void getPreviousExpression_alwaysReturnsNull() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertNull(ctx.getPreviousExpression());
  }

  @Test
  public void getBlockStatementIndexPair_returnsStoredPair() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertSame(pair, ctx.getBlockStatementIndexPair());
  }

  @Test
  public void getBlockStatementIndexPair_preservesBlockAndIndex() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new NullLiteral()));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(42)));
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertEquals(2, ctx.getBlockStatementIndexPair().getIndex());
    assertSame(block, ctx.getBlockStatementIndexPair().getBlockStatement());
  }

  @Test
  public void implementsExpressionCascadeContext() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertTrue(ctx instanceof ExpressionCascadeContext);
  }

  @Test
  public void getPreviousExpression_withPopulatedBlock_stillReturnsNull() {
    BlockStatement block = new BlockStatement();
    UserLocal local = new UserLocal("x", JavaType.DOUBLE_OBJECT_TYPE, false);
    block.statements.add(new LocalDeclarationStatement(local, new DoubleLiteral(1.0)));
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertNull("Previous expression should always be null for this context type", ctx.getPreviousExpression());
  }

  @Test
  public void zeroIndex_returnsCorrectPair() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPairContext ctx = new BlockStatementIndexPairContext(pair);
    assertEquals(0, ctx.getBlockStatementIndexPair().getIndex());
  }
}
