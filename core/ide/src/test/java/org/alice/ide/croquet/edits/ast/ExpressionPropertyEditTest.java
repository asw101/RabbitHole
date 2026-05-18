package org.alice.ide.croquet.edits.ast;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionPropertyEdit} — edit that changes an ExpressionProperty value.
 * Covers construction with null UserActivity, doOrRedo/undo round-trip,
 * and accessor contracts.
 */
public class ExpressionPropertyEditTest {

  private ExpressionStatement owner;
  private ExpressionProperty property;
  private Expression prevExpression;
  private Expression nextExpression;

  @Before
  public void setUp() {
    prevExpression = new NullLiteral();
    nextExpression = new IntegerLiteral(42);
    owner = new ExpressionStatement(prevExpression);
    property = owner.expression;
  }

  // ---- construction ----

  @Test
  public void construct_withNullUserActivity_succeeds() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    assertNotNull(edit);
  }

  // ---- doOrRedoInternal ----

  @Test
  public void doOrRedoInternal_isDo_true_setsNextExpression() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    edit.doOrRedoInternal(true);
    assertSame(nextExpression, property.getValue());
  }

  @Test
  public void doOrRedoInternal_isDo_false_setsNextExpression() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    edit.doOrRedoInternal(false);
    assertSame(nextExpression, property.getValue());
  }

  // ---- undoInternal ----

  @Test
  public void undoInternal_restoresPrevExpression() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    edit.doOrRedoInternal(true);
    assertSame(nextExpression, property.getValue());

    edit.undoInternal();
    assertSame(prevExpression, property.getValue());
  }

  // ---- doOrRedo + undo round trip ----

  @Test
  public void doAndUndo_roundTrip_restoresOriginal() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    assertSame(prevExpression, property.getValue());

    edit.doOrRedoInternal(true);
    assertSame(nextExpression, property.getValue());

    edit.undoInternal();
    assertSame(prevExpression, property.getValue());
  }

  @Test
  public void doAndRedo_afterUndo_setsNext() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);

    assertSame(nextExpression, property.getValue());
  }

  @Test
  public void multipleUndoRedo_cycles() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);

    for (int i = 0; i < 5; i++) {
      edit.doOrRedoInternal(i == 0);
      assertSame("After do/redo cycle " + i, nextExpression, property.getValue());

      edit.undoInternal();
      assertSame("After undo cycle " + i, prevExpression, property.getValue());
    }
  }

  // ---- different expression types ----

  @Test
  public void doOrRedo_withDoubleLiteral() {
    Expression prev = new DoubleLiteral(1.0);
    Expression next = new DoubleLiteral(2.0);
    ExpressionStatement stmt = new ExpressionStatement(prev);

    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, stmt.expression, prev, next);
    edit.doOrRedoInternal(true);
    assertSame(next, stmt.expression.getValue());
  }

  @Test
  public void doOrRedo_withBooleanLiteral() {
    Expression prev = new BooleanLiteral(false);
    Expression next = new BooleanLiteral(true);
    ExpressionStatement stmt = new ExpressionStatement(prev);

    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, stmt.expression, prev, next);
    edit.doOrRedoInternal(true);
    assertSame(next, stmt.expression.getValue());
  }

  @Test
  public void doOrRedo_withStringLiteral() {
    Expression prev = new StringLiteral("old");
    Expression next = new StringLiteral("new");
    ExpressionStatement stmt = new ExpressionStatement(prev);

    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, stmt.expression, prev, next);
    edit.doOrRedoInternal(true);
    assertSame(next, stmt.expression.getValue());

    edit.undoInternal();
    assertSame(prev, stmt.expression.getValue());
  }

  // ---- setValue is idempotent ----

  @Test
  public void doOrRedo_twiceWithoutUndo_setsSameValue() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    edit.doOrRedoInternal(true);
    edit.doOrRedoInternal(false);
    assertSame(nextExpression, property.getValue());
  }

  @Test
  public void undo_twiceWithoutRedo_setsSameValue() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.undoInternal();
    assertSame(prevExpression, property.getValue());
  }

  // ---- property.getValue before any edit ----

  @Test
  public void propertyValue_beforeEdit_isPrev() {
    new ExpressionPropertyEdit(null, property, prevExpression, nextExpression);
    assertSame(prevExpression, property.getValue());
  }

  // ---- null expressions ----

  @Test
  public void construct_withNullPrevExpression_succeeds() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, null, nextExpression);
    assertNotNull(edit);
  }

  @Test
  public void construct_withNullNextExpression_succeeds() {
    ExpressionPropertyEdit edit = new ExpressionPropertyEdit(null, property, prevExpression, null);
    assertNotNull(edit);
  }
}
