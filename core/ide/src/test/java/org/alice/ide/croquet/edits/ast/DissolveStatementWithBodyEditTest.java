package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.alice.ide.croquet.models.ast.DissolveStatementWithBodyOperation;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.Statement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DissolveStatementWithBodyEditTest {
  private BlockStatement owner;
  private DoInOrder statementWithBody;
  private ExpressionStatement before;
  private ExpressionStatement insideFirst;
  private ExpressionStatement insideSecond;
  private ExpressionStatement after;
  private UserActivity activity;

  @Before
  public void setUp() {
    owner = new BlockStatement();
    before = new ExpressionStatement(new IntegerLiteral(1));
    insideFirst = new ExpressionStatement(new IntegerLiteral(2));
    insideSecond = new ExpressionStatement(new IntegerLiteral(3));
    after = new ExpressionStatement(new IntegerLiteral(4));
    statementWithBody = new DoInOrder();
    statementWithBody.body.setValue(new BlockStatement());
    statementWithBody.body.getValue().statements.add(insideFirst);
    statementWithBody.body.getValue().statements.add(insideSecond);
    owner.statements.add(before);
    owner.statements.add(statementWithBody);
    owner.statements.add(after);
    activity = new UserActivity();
    activity.setCompletionModel(DissolveStatementWithBodyOperation.getInstance(statementWithBody));
  }

  @Test
  public void construct_withUserActivity_setsBlockStatement() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertSame(owner, edit.getBlockStatement());
  }

  @Test
  public void extendsBlockStatementEdit() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit instanceof BlockStatementEdit);
  }

  @Test
  public void extendsAbstractEdit() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_removesWrapperStatement() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    assertFalse(owner.statements.contains(statementWithBody));
  }

  @Test
  public void doOrRedoInternal_insertsInnerStatementsAtOriginalIndex() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    assertSame(insideFirst, owner.statements.get(1));
    assertSame(insideSecond, owner.statements.get(2));
  }

  @Test
  public void doOrRedoInternal_clearsBodyStatements() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    assertTrue(statementWithBody.body.getValue().statements.isEmpty());
  }

  @Test
  public void doOrRedoInternal_preservesNeighbors() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    assertSame(before, owner.statements.get(0));
    assertSame(after, owner.statements.get(3));
  }

  @Test
  public void undoInternal_restoresWrapperStatement() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(statementWithBody, owner.statements.get(1));
  }

  @Test
  public void undoInternal_restoresBodyStatements() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals(2, statementWithBody.body.getValue().statements.size());
    assertSame(insideFirst, statementWithBody.body.getValue().statements.get(0));
    assertSame(insideSecond, statementWithBody.body.getValue().statements.get(1));
  }

  @Test
  public void roundTrip_preservesOuterOrder() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(before, owner.statements.get(0));
    assertSame(statementWithBody, owner.statements.get(1));
    assertSame(after, owner.statements.get(2));
  }

  @Test
  public void redoAfterUndo_reinsertsStatements() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);
    assertSame(insideFirst, owner.statements.get(1));
    assertSame(insideSecond, owner.statements.get(2));
  }

  @Test
  public void getTerseDescription_containsDissolvePrefix() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit.getTerseDescription().startsWith("dissolve:"));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit.getDetailedDescription().contains(DissolveStatementWithBodyEdit.class.getName()));
  }

  @Test
  public void getRedoPresentation_containsRedoPrefix() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_containsUndoPrefix() {
    DissolveStatementWithBodyEdit edit = new DissolveStatementWithBodyEdit(activity);
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void encodeMethodExists() throws Exception {
    Method method = DissolveStatementWithBodyEdit.class.getMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void constructorSignature_matchesSource() throws Exception {
    Constructor<DissolveStatementWithBodyEdit> constructor = DissolveStatementWithBodyEdit.class.getConstructor(UserActivity.class);
    assertNotNull(constructor);
  }
}
