package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class StatementEditTest {
  private static class TestStatementEdit extends StatementEdit<CompletionModel> {
    private int doCallCount;
    private int undoCallCount;
    private Boolean lastIsDo;

    TestStatementEdit(UserActivity userActivity, Statement statement) {
      super(userActivity, statement);
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
      this.doCallCount++;
      this.lastIsDo = isDo;
    }

    @Override
    protected void undoInternal() {
      this.undoCallCount++;
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("TestStatementEdit");
    }

    int getDoCallCount() {
      return this.doCallCount;
    }

    int getUndoCallCount() {
      return this.undoCallCount;
    }

    Boolean getLastIsDo() {
      return this.lastIsDo;
    }
  }

  @Test
  public void construct_withNullUserActivity_succeeds() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());
    assertNotNull(edit);
  }

  @Test
  public void construct_withStatement_succeeds() {
    Statement statement = new ExpressionStatement(new NullLiteral());
    TestStatementEdit edit = new TestStatementEdit(null, statement);
    assertSame(statement, edit.getStatement());
  }

  @Test
  public void getStatement_returnsConstructionValue() {
    Statement statement = new ReturnStatement(JavaType.getInstance(Integer.class), new IntegerLiteral(7));
    TestStatementEdit edit = new TestStatementEdit(null, statement);

    assertSame(statement, edit.getStatement());
  }

  @Test
  public void getStatement_withNullStatement_returnsNull() {
    TestStatementEdit edit = new TestStatementEdit(null, null);

    assertNull(edit.getStatement());
  }

  @Test
  public void construct_multipleInstances_keepStatementsIndependent() {
    Statement first = new ExpressionStatement(new StringLiteral("first"));
    Statement second = new ExpressionStatement(new StringLiteral("second"));

    TestStatementEdit firstEdit = new TestStatementEdit(null, first);
    TestStatementEdit secondEdit = new TestStatementEdit(null, second);

    assertSame(first, firstEdit.getStatement());
    assertSame(second, secondEdit.getStatement());
    assertNotSame(firstEdit.getStatement(), secondEdit.getStatement());
  }

  @Test
  public void construct_extendsAbstractEdit() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_whenDo_recordsTrueFlag() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    edit.doOrRedoInternal(true);

    assertEquals(1, edit.getDoCallCount());
    assertEquals(Boolean.TRUE, edit.getLastIsDo());
  }

  @Test
  public void doOrRedoInternal_whenRedo_recordsFalseFlag() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    edit.doOrRedoInternal(false);

    assertEquals(1, edit.getDoCallCount());
    assertEquals(Boolean.FALSE, edit.getLastIsDo());
  }

  @Test
  public void undoInternal_afterDo_incrementsUndoCount() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    edit.doOrRedoInternal(true);
    edit.undoInternal();

    assertEquals(1, edit.getDoCallCount());
    assertEquals(1, edit.getUndoCallCount());
  }

  @Test
  public void appendDescription_viaTerseDescription_returnsSubclassText() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    assertEquals("TestStatementEdit", edit.getTerseDescription());
  }

  @Test
  public void getRedoPresentation_containsSubclassDescription() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    assertEquals("Redo:TestStatementEdit", edit.getRedoPresentation());
  }

  @Test
  public void getUndoPresentation_containsSubclassDescription() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    assertEquals("Undo:TestStatementEdit", edit.getUndoPresentation());
  }

  @Test
  public void getDetailedDescription_containsClassNameAndDescription() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());
    String description = edit.getDetailedDescription();

    assertTrue(description.contains(TestStatementEdit.class.getName()));
    assertTrue(description.contains("TestStatementEdit"));
  }

  @Test
  public void toString_matchesDetailedDescription() {
    TestStatementEdit edit = new TestStatementEdit(null, new ExpressionStatement());

    assertEquals(edit.getDetailedDescription(), edit.toString());
  }
}
