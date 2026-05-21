package org.alice.ide.croquet.edits.ast;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.UserMethod;

import static org.junit.Assert.*;

public class ChangeMethodBodyEditCoverageTest {
  @Test
  public void redoWithFalseFlag_stillSetsNewBody() {
    UserMethod method = new UserMethod();
    BlockStatement original = new BlockStatement();
    BlockStatement replacement = new BlockStatement();
    replacement.statements.add(new Comment("replacement"));
    method.body.setValue(original);
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, replacement);
    edit.doOrRedoInternal(false);
    assertSame(replacement, method.body.getValue());
  }

  @Test
  public void undo_restoresOriginalBody() {
    UserMethod method = new UserMethod();
    BlockStatement original = new BlockStatement();
    BlockStatement replacement = new BlockStatement();
    method.body.setValue(original);
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, replacement);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(original, method.body.getValue());
  }

  @Test
  public void terseDescription_fallsBackToClassName() {
    UserMethod method = new UserMethod();
    method.body.setValue(new BlockStatement());
    ChangeMethodBodyEdit edit = new ChangeMethodBodyEdit(null, method, new BlockStatement());
    assertEquals("ChangeMethodBodyEdit", edit.getTerseDescription());
  }
}
