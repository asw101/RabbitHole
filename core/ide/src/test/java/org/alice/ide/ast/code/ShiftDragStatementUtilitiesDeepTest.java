package org.alice.ide.ast.code;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link ShiftDragStatementUtilities} covering complex nested
 * block scenarios, conditional statement hierarchies, and edge cases.
 */
public class ShiftDragStatementUtilitiesDeepTest {

  // ---- calculateShiftMoveCount with various block configurations ----

  @Test
  public void calculateShiftMoveCount_sameBlock_fromEnd_returnsOne() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new NullLiteral()));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(1)));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(2)));

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);
    assertEquals(1, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_sameBlock_fromMiddle() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new IntegerLiteral(0)));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(1)));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(2)));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(3)));

    // from=2, to=0, statements.size()=4 → 4-2=2
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    assertEquals(2, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_emptyFromBlock_returnsZero() {
    BlockStatement fromBlock = new BlockStatement();
    BlockStatement toBlock = new BlockStatement();

    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_differentBlocks_allStatementsMove() {
    BlockStatement fromBlock = new BlockStatement();
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(0)));
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(1)));
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(2)));

    BlockStatement toBlock = new BlockStatement();

    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    assertEquals(3, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_differentBlocks_partialMove() {
    BlockStatement fromBlock = new BlockStatement();
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(0)));
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(1)));
    fromBlock.statements.add(new ExpressionStatement(new IntegerLiteral(2)));

    BlockStatement toBlock = new BlockStatement();

    // Start from index 1 — should count 2 statements (indices 1 and 2)
    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 1);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    assertEquals(2, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_toBlockNestedInFromStatement_stopsAtAncestor() {
    BlockStatement outerBlock = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    outerBlock.statements.add(s0);

    BlockStatement innerBody = new BlockStatement();
    WhileLoop whileLoop = new WhileLoop(new BooleanLiteral(true), innerBody);
    outerBlock.statements.add(whileLoop);

    ExpressionStatement s2 = new ExpressionStatement(new IntegerLiteral(99));
    outerBlock.statements.add(s2);

    // From index 0, toBlock is inside whileLoop (index 1)
    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(innerBody, 0);
    // s0 is NOT an ancestor of innerBody, but whileLoop IS → count = 1
    assertEquals(1, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_sameBlock_equalIndices_returnsZero() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new IntegerLiteral(0)));
    block.statements.add(new ExpressionStatement(new IntegerLiteral(1)));

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 1);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);
    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  // ---- isCandidateForEnvelop ----

  @Test
  public void isCandidateForEnvelop_nullDragModel_returnsFalse() {
    assertFalse(ShiftDragStatementUtilities.isCandidateForEnvelop(null));
  }

  // ---- calculateShiftMoveCount nested conditional ----

  @Test
  public void calculateShiftMoveCount_toBlockInsideConditional_stopsAtConditional() {
    BlockStatement outerBlock = new BlockStatement();
    ExpressionStatement s0 = new ExpressionStatement(new NullLiteral());
    outerBlock.statements.add(s0);

    BlockStatement ifBody = new BlockStatement();
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(new BooleanLiteral(true), ifBody);
    ConditionalStatement conditional = new ConditionalStatement(new BooleanExpressionBodyPair[]{pair}, new BlockStatement());
    outerBlock.statements.add(conditional);

    ExpressionStatement s2 = new ExpressionStatement(new IntegerLiteral(1));
    outerBlock.statements.add(s2);

    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(ifBody, 0);
    // s0 is NOT ancestor, conditional IS → count = 1
    assertEquals(1, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  @Test
  public void calculateShiftMoveCount_sameBlockFromHigherThanTo_returnsTrailing() {
    BlockStatement block = new BlockStatement();
    for (int i = 0; i < 5; i++) {
      block.statements.add(new ExpressionStatement(new IntegerLiteral(i)));
    }
    // from=3, to=1 → size(5) - 3 = 2
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);
    assertEquals(2, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }
}
