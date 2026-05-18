package org.alice.ide.croquet.edits.ast;

import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.NullLiteral;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link BlockStatementEdit} — abstract edit that stores a BlockStatement.
 * Uses a concrete test subclass since BlockStatementEdit is abstract.
 */
public class BlockStatementEditTest {

  /**
   * Minimal concrete subclass for testing the abstract BlockStatementEdit.
   */
  private static class TestBlockStatementEdit extends BlockStatementEdit<CompletionModel> {
    private boolean doOrRedoCalled = false;
    private boolean undoCalled = false;

    public TestBlockStatementEdit(UserActivity userActivity, BlockStatement blockStatement) {
      super(userActivity, blockStatement);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      doOrRedoCalled = true;
    }

    @Override
    protected void undoInternal() {
      undoCalled = true;
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("test block edit");
    }
  }

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertNotNull(edit);
  }

  @Test
  public void construct_withEmptyBlock_succeeds() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertNotNull(edit);
    assertTrue(block.statements.isEmpty());
  }

  @Test
  public void construct_withPopulatedBlock_succeeds() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new ExpressionStatement(new NullLiteral()));
    block.statements.add(new ExpressionStatement(new NullLiteral()));
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertNotNull(edit);
  }

  // ---- getBlockStatement ----

  @Test
  public void getBlockStatement_returnsOriginal() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertSame(block, edit.getBlockStatement());
  }

  @Test
  public void getBlockStatement_differentBlocks_returnDifferent() {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    TestBlockStatementEdit edit1 = new TestBlockStatementEdit(null, block1);
    TestBlockStatementEdit edit2 = new TestBlockStatementEdit(null, block2);
    assertSame(block1, edit1.getBlockStatement());
    assertSame(block2, edit2.getBlockStatement());
    assertNotSame(edit1.getBlockStatement(), edit2.getBlockStatement());
  }

  @Test
  public void getBlockStatement_isNotNull() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertNotNull(edit.getBlockStatement());
  }

  @Test
  public void getBlockStatement_reflectsExternalModifications() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertTrue(edit.getBlockStatement().statements.isEmpty());

    block.statements.add(new ExpressionStatement(new NullLiteral()));
    assertEquals(1, edit.getBlockStatement().statements.size());
  }

  // ---- abstract methods are callable ----

  @Test
  public void doOrRedoInternal_isCalled() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertFalse(edit.doOrRedoCalled);
    edit.doOrRedoInternal(true);
    assertTrue(edit.doOrRedoCalled);
  }

  @Test
  public void undoInternal_isCalled() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertFalse(edit.undoCalled);
    edit.undoInternal();
    assertTrue(edit.undoCalled);
  }

  // ---- AbstractEdit inheritance ----

  @Test
  public void isInstanceOfAbstractEdit() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void isInstanceOfBlockStatementEdit() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertTrue(edit instanceof BlockStatementEdit);
  }

  // ---- null block ----

  @Test
  public void construct_withNullBlock_succeeds() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, null);
    assertNull(edit.getBlockStatement());
  }
}
