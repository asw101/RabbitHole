package org.alice.ide.ast.code;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ShiftDragStatementUtilitiesTest {

  @Test
  public void calculateShiftMoveCount_sameBlock_fromAfterTo_returnsTrailingCount() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s2 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s0);
    block.statements.add(s1);
    block.statements.add(s2);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);

    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals(1, count);
  }

  @Test
  public void calculateShiftMoveCount_sameBlock_fromBeforeTo_returnsZero() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s0);
    block.statements.add(s1);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);

    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals(0, count);
  }

  @Test
  public void calculateShiftMoveCount_sameBlock_sameIndex_returnsZero() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s0);

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);

    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals(0, count);
  }

  @Test
  public void calculateShiftMoveCount_differentBlocks_countsUntilAncestor() {
    BlockStatement fromBlock = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    fromBlock.statements.add(s0);
    fromBlock.statements.add(s1);

    BlockStatement toBlock = new BlockStatement();

    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);

    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals(2, count);
  }

  @Test
  public void calculateShiftMoveCount_differentBlocks_toBlockIsChild_stopsAtAncestor() {
    BlockStatement outerBlock = new BlockStatement();
    BlockStatement innerBody = new BlockStatement();
    DoInOrder doInOrder = new DoInOrder(innerBody);
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    outerBlock.statements.add(s0);
    outerBlock.statements.add(doInOrder);

    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(innerBody, 0);

    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    // s0 is not an ancestor of innerBody, but doInOrder (index 1) IS an ancestor
    assertEquals(1, count);
  }

  @Test
  public void isCandidateForEnvelop_null_returnsFalse() {
    boolean result = ShiftDragStatementUtilities.isCandidateForEnvelop(null);
    assertFalse(result);
  }
}
