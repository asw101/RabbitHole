package org.alice.ide.ast.code;

import org.alice.ide.ast.EmptyExpression;
import org.alice.ide.ast.ReflectionTestHelper;
import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.croquet.DragModel;
import org.lgna.project.ast.*;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ShiftDragStatementUtilities}.
 * Tests calculateShiftMoveCount edge cases, deep nesting scenarios,
 * ConditionalStatement body handling, and isCandidateForEnvelop semantics.
 *
 * Complements existing ShiftDragStatementUtilitiesTest/DeepTest/AdditionalTest.
 * Pure behavioral — no GUI dependency.
 */
public class ShiftDragStatementUtilitiesCoverageTest {

  // ── Structural characterization ────────────────────────────────

  @Test
  public void isUtilityClass() throws Exception {
    ReflectionTestHelper.assertUtilityClass(ShiftDragStatementUtilities.class);
  }

  // ── calculateShiftMoveCount: same block ────────────────────────

  @Test
  public void sameBlock_fromAfterTo_returnsRemainingStatements() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());

    // from index 2 to index 0: from > to, returns block.size - fromIndex = 4-2 = 2
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("fromIndex > toIndex in same block: size - fromIndex", 2, count);
  }

  @Test
  public void sameBlock_fromBeforeOrEqualTo_returnsZero() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());

    // from index 0 to index 1: from <= to, returns 0
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 1);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("fromIndex <= toIndex in same block: 0", 0, count);
  }

  @Test
  public void sameBlock_sameIndex_returnsZero() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement());

    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("Same index in same block: 0", 0, count);
  }

  @Test
  public void sameBlock_fromLastIndex_returnsOne() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());
    block.statements.add(new ExpressionStatement());

    // from index 2 (last) to index 0: size - fromIndex = 3 - 2 = 1
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("From last index: should return 1", 1, count);
  }

  // ── calculateShiftMoveCount: different blocks ──────────────────

  @Test
  public void differentBlocks_noAncestorRelationship_countsAll() {
    BlockStatement fromBlock = new BlockStatement();
    fromBlock.statements.add(new ExpressionStatement());
    fromBlock.statements.add(new ExpressionStatement());
    fromBlock.statements.add(new ExpressionStatement());

    BlockStatement toBlock = new BlockStatement();

    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 1);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    // No ancestor relationship: counts fromIndex to end = 3 - 1 = 2
    assertEquals("Different blocks, no ancestor: count all from fromIndex", 2, count);
  }

  @Test
  public void differentBlocks_toIsInsideFromStatement_stopsAtAncestor() {
    // Create: fromBlock with [stmt0, whileLoop(body=toBlock)]
    BlockStatement fromBlock = new BlockStatement();
    ExpressionStatement stmt0 = new ExpressionStatement();
    fromBlock.statements.add(stmt0);

    WhileLoop whileLoop = AstUtilities.createWhileLoop(
        new EmptyExpression(JavaType.BOOLEAN_OBJECT_TYPE));
    fromBlock.statements.add(whileLoop);

    BlockStatement toBlock = whileLoop.body.getValue();

    // from index 0, to is inside whileLoop (index 1)
    // Iteration: i=0 (stmt0 is not ancestor of toBlock → count++); i=1 (whileLoop IS ancestor → break)
    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("Should stop counting at ancestor statement", 1, count);
  }

  @Test
  public void differentBlocks_fromIndexAtAncestor_returnsZero() {
    BlockStatement fromBlock = new BlockStatement();
    WhileLoop whileLoop = AstUtilities.createWhileLoop(
        new EmptyExpression(JavaType.BOOLEAN_OBJECT_TYPE));
    fromBlock.statements.add(whileLoop);

    BlockStatement toBlock = whileLoop.body.getValue();

    // from index 0 (the whileLoop itself is ancestor of toBlock) → breaks immediately, count=0
    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("From starts at ancestor: should return 0", 0, count);
  }

  // ── Deep nesting ───────────────────────────────────────────────

  @Test
  public void deeplyNestedToBlock_ancestorCheckTraversesMultipleLevels() {
    BlockStatement outerBlock = new BlockStatement();
    ExpressionStatement filler1 = new ExpressionStatement();
    ExpressionStatement filler2 = new ExpressionStatement();
    outerBlock.statements.add(filler1);
    outerBlock.statements.add(filler2);

    // Create nested: outer → while → countLoop → deepBody
    WhileLoop whileLoop = AstUtilities.createWhileLoop(
        new EmptyExpression(JavaType.BOOLEAN_OBJECT_TYPE));
    outerBlock.statements.add(whileLoop);

    CountLoop countLoop = AstUtilities.createCountLoop(
        new EmptyExpression(JavaType.INTEGER_OBJECT_TYPE));
    whileLoop.body.getValue().statements.add(countLoop);

    BlockStatement deepBody = countLoop.body.getValue();

    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(deepBody, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    // filler1 (not ancestor) → count++; filler2 (not ancestor) → count++; whileLoop (ancestor) → break
    assertEquals("Should count non-ancestor statements then stop", 2, count);
  }

  // ── isCandidateForEnvelop ──────────────────────────────────────

  @Test
  public void isCandidateForEnvelop_nullDragModel_returnsFalse() {
    assertFalse("null should not be candidate for envelop",
        ShiftDragStatementUtilities.isCandidateForEnvelop(null));
  }

  // ── Method signatures ──────────────────────────────────────────

  @Test
  public void calculateShiftMoveCount_takesTwo_BlockStatementIndexPairs() throws Exception {
    Method m = ShiftDragStatementUtilities.class.getMethod(
        "calculateShiftMoveCount",
        BlockStatementIndexPair.class,
        BlockStatementIndexPair.class);
    assertNotNull(m);
    assertEquals("Should return int", int.class, m.getReturnType());
  }

  @Test
  public void isCandidateForEnvelop_takesDragModel() throws Exception {
    Method m = ShiftDragStatementUtilities.class.getMethod(
        "isCandidateForEnvelop",
        DragModel.class);
    assertNotNull(m);
    assertEquals("Should return boolean", boolean.class, m.getReturnType());
  }

  // ── Edge cases ─────────────────────────────────────────────────

  @Test
  public void sameBlock_emptyBlock_fromIndexZero() {
    BlockStatement block = new BlockStatement();
    // No statements in block — fromIndex = 0, toIndex = 0
    // fromIndex <= toIndex → returns 0
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("Empty block, same indices: 0", 0, count);
  }

  @Test
  public void differentBlocks_emptyFromBlock_returnsZero() {
    BlockStatement fromBlock = new BlockStatement();
    BlockStatement toBlock = new BlockStatement();

    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    // fromBlock is empty, loop body doesn't execute → count = 0
    assertEquals("Empty from block: 0", 0, count);
  }

  @Test
  public void sameBlock_singleStatement_fromAfterTo() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement());

    // from 0 to 0: from <= to → 0
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 0);
    assertEquals(0, ShiftDragStatementUtilities.calculateShiftMoveCount(from, to));
  }

  // ── ConditionalStatement body as target ────────────────────────

  @Test
  public void conditionalStatement_thenBody_asTarget() {
    BlockStatement outerBlock = new BlockStatement();
    ExpressionStatement filler = new ExpressionStatement();
    outerBlock.statements.add(filler);

    ConditionalStatement conditional = AstUtilities.createConditionalStatement(
        new EmptyExpression(JavaType.BOOLEAN_OBJECT_TYPE));
    outerBlock.statements.add(conditional);

    // Get the "then" body block from the conditional
    BlockStatement thenBody = conditional.booleanExpressionBodyPairs.get(0).body.getValue();

    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(thenBody, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    // filler is not ancestor of thenBody → count++; conditional IS ancestor → break
    assertEquals("Should count filler then stop at conditional", 1, count);
  }

  @Test
  public void conditionalStatement_elseBody_asTarget() {
    BlockStatement outerBlock = new BlockStatement();
    ExpressionStatement filler = new ExpressionStatement();
    outerBlock.statements.add(filler);

    ConditionalStatement conditional = AstUtilities.createConditionalStatement(
        new EmptyExpression(JavaType.BOOLEAN_OBJECT_TYPE));
    outerBlock.statements.add(conditional);

    BlockStatement elseBody = conditional.elseBody.getValue();

    BlockStatementIndexPair from = new BlockStatementIndexPair(outerBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(elseBody, 0);
    int count = ShiftDragStatementUtilities.calculateShiftMoveCount(from, to);
    assertEquals("Should count filler then stop at conditional", 1, count);
  }
}
