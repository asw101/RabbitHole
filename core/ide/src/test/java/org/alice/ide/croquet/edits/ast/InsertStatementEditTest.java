package org.alice.ide.croquet.edits.ast;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.project.ast.*;

import javax.swing.undo.CannotUndoException;

import static org.junit.Assert.*;

public class InsertStatementEditTest {

  @Test
  public void atEnd_isMaxShortValue() {
    assertEquals(Short.MAX_VALUE, InsertStatementEdit.AT_END);
  }

  @Test
  public void construct_setsBlockStatementAndIndex() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt);
    assertSame(block, edit.getBlockStatement());
    assertEquals(0, edit.getSpecifiedIndex());
    assertSame(stmt, edit.getStatement());
  }

  @Test
  public void construct_withExpressions() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    Expression[] initExprs = {new NullLiteral(), new NullLiteral()};

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt, initExprs);
    assertSame(block, edit.getBlockStatement());
    assertEquals(0, edit.getSpecifiedIndex());
    assertArrayEquals(initExprs, edit.getInitialExpressions());
  }

  @Test
  public void construct_noExpressions_emptyArray() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt);
    assertNotNull(edit.getInitialExpressions());
    assertEquals(0, edit.getInitialExpressions().length);
  }

  @Test
  public void doOrRedo_insertsStatementAtIndex() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement existing = new ExpressionStatement(new NullLiteral());
    block.statements.add(existing);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement newStmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, newStmt);
    edit.doOrRedoInternal(true);

    assertEquals(2, block.statements.size());
    assertSame(newStmt, block.statements.get(0));
    assertSame(existing, block.statements.get(1));
  }

  @Test
  public void doOrRedo_insertsAtEnd_whenIndexExceedsSize() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, InsertStatementEdit.AT_END);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt);
    edit.doOrRedoInternal(true);

    assertEquals(1, block.statements.size());
    assertSame(stmt, block.statements.get(0));
  }

  @Test
  public void undoInternal_removesInsertedStatement() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt);
    edit.doOrRedoInternal(true);
    assertEquals(1, block.statements.size());

    edit.undoInternal();
    assertEquals(0, block.statements.size());
  }

  @Test(expected = CannotUndoException.class)
  public void undoInternal_throwsWhenStatementMismatch() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement other = new ExpressionStatement(new NullLiteral());
    block.statements.add(other);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, stmt);
    edit.undoInternal();
  }

  @Test
  public void doAndUndo_roundTrip() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s2 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s1);
    block.statements.add(s2);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    ExpressionStatement newStmt = new ExpressionStatement(new NullLiteral());

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, newStmt);
    edit.doOrRedoInternal(true);

    assertEquals(3, block.statements.size());
    assertSame(newStmt, block.statements.get(1));

    edit.undoInternal();
    assertEquals(2, block.statements.size());
    assertSame(s1, block.statements.get(0));
    assertSame(s2, block.statements.get(1));
  }

  @Test
  public void doOrRedo_enveloping_movesTrailingStatements() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s2 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s3 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s1);
    block.statements.add(s2);
    block.statements.add(s3);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    BlockStatement innerBody = new BlockStatement();
    DoInOrder doInOrder = new DoInOrder(innerBody);

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, doInOrder, new Expression[]{}, true);
    edit.doOrRedoInternal(true);

    assertEquals(2, block.statements.size());
    assertSame(s1, block.statements.get(0));
    assertSame(doInOrder, block.statements.get(1));
    assertEquals(2, innerBody.statements.size());
  }

  @Test
  public void undoInternal_enveloping_restoresStatements() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement s1 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement s2 = new ExpressionStatement(new NullLiteral());
    block.statements.add(s1);
    block.statements.add(s2);

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 1);
    BlockStatement innerBody = new BlockStatement();
    DoInOrder doInOrder = new DoInOrder(innerBody);

    InsertStatementEdit<?> edit = new InsertStatementEdit<>(null, pair, doInOrder, new Expression[]{}, true);
    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(2, block.statements.size());
    assertSame(s1, block.statements.get(0));
    assertSame(s2, block.statements.get(1));
  }
}
