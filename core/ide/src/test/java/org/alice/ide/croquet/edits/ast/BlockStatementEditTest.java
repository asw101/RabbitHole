package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link BlockStatementEdit} — abstract base for block-scoped edits.
 * Tested via a concrete test-local subclass.
 */
public class BlockStatementEditTest {

  /**
   * Minimal concrete subclass of BlockStatementEdit for testing.
   */
  private static class TestBlockStatementEdit extends BlockStatementEdit<CompletionModel> {
    TestBlockStatementEdit(UserActivity userActivity, BlockStatement blockStatement) {
      super(userActivity, blockStatement);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      // no-op for tests
    }

    @Override
    protected void undoInternal() {
      // no-op for tests
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("TestBlockStatementEdit");
    }

    public String describeForTest() {
      StringBuilder sb = new StringBuilder();
      appendDescription(sb, DescriptionStyle.TERSE);
      return sb.toString();
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
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, new BlockStatement());
    assertNotNull(edit);
  }

  // ---- getBlockStatement ----

  @Test
  public void getBlockStatement_returnsConstructionValue() {
    BlockStatement block = new BlockStatement();
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, block);
    assertSame(block, edit.getBlockStatement());
  }

  @Test
  public void getBlockStatement_differentBlocks_returnCorrectOne() {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    TestBlockStatementEdit edit1 = new TestBlockStatementEdit(null, block1);
    TestBlockStatementEdit edit2 = new TestBlockStatementEdit(null, block2);
    assertSame(block1, edit1.getBlockStatement());
    assertSame(block2, edit2.getBlockStatement());
    assertNotSame(edit1.getBlockStatement(), edit2.getBlockStatement());
  }

  // ---- null block ----

  @Test
  public void construct_withNullBlock_succeeds() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, null);
    assertNull(edit.getBlockStatement());
  }

  // ---- inheritance ----

  @Test
  public void extendsAbstractEdit() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, new BlockStatement());
    assertTrue(edit instanceof AbstractEdit);
  }

  // ---- doOrRedo and undo stubs ----

  @Test
  public void doOrRedoInternal_doesNotThrow() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, new BlockStatement());
    edit.doOrRedoInternal(true);
  }

  @Test
  public void undoInternal_doesNotThrow() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, new BlockStatement());
    edit.undoInternal();
  }

  // ---- appendDescription ----

  @Test
  public void appendDescription_producesOutput() {
    TestBlockStatementEdit edit = new TestBlockStatementEdit(null, new BlockStatement());
    String desc = edit.describeForTest();
    assertFalse(desc.isEmpty());
  }

  // ---- multiple instances ----

  @Test
  public void multipleInstances_independent() {
    BlockStatement b1 = new BlockStatement();
    BlockStatement b2 = new BlockStatement();
    BlockStatement b3 = new BlockStatement();
    TestBlockStatementEdit e1 = new TestBlockStatementEdit(null, b1);
    TestBlockStatementEdit e2 = new TestBlockStatementEdit(null, b2);
    TestBlockStatementEdit e3 = new TestBlockStatementEdit(null, b3);

    assertSame(b1, e1.getBlockStatement());
    assertSame(b2, e2.getBlockStatement());
    assertSame(b3, e3.getBlockStatement());
  }
}
