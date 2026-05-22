package org.alice.ide.croquet.edits.ast;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.alice.ide.croquet.models.ast.ConvertStatementWithBodyOperation;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AbstractStatementWithBody;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class ConvertStatementWithBodyEditTest {
  private static class TestConvertOperation extends ConvertStatementWithBodyOperation {
    private final AbstractStatementWithBody replacement;

    TestConvertOperation(AbstractStatementWithBody original, AbstractStatementWithBody replacement) {
      super(UUID.randomUUID(), original);
      this.replacement = replacement;
    }

    @Override
    protected AbstractStatementWithBody createReplacement() {
      return replacement;
    }
  }

  private BlockStatement owner;
  private DoInOrder original;
  private DoTogether replacement;
  private ExpressionStatement before;
  private ExpressionStatement after;
  private UserActivity activity;

  @Before
  public void setUp() {
    owner = new BlockStatement();
    before = new ExpressionStatement(new IntegerLiteral(1));
    after = new ExpressionStatement(new IntegerLiteral(99));
    original = new DoInOrder();
    original.body.setValue(new BlockStatement());
    original.body.getValue().statements.add(new ExpressionStatement(new IntegerLiteral(7)));
    replacement = new DoTogether();
    replacement.body.setValue(new BlockStatement());
    owner.statements.add(before);
    owner.statements.add(original);
    owner.statements.add(after);
    activity = new UserActivity();
    activity.setCompletionModel(new TestConvertOperation(original, replacement));
  }

  @Test
  public void construct_withUserActivity_setsBlockStatementFromOriginalParent() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertSame(owner, edit.getBlockStatement());
  }

  @Test
  public void extendsBlockStatementEdit() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit instanceof BlockStatementEdit);
  }

  @Test
  public void extendsAbstractEdit() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit instanceof AbstractEdit);
  }

  @Test
  public void doOrRedoInternal_replacesOriginalStatement() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    assertSame(replacement, owner.statements.get(1));
  }

  @Test
  public void doOrRedoInternal_movesBodyToReplacement() {
    BlockStatement originalBody = original.body.getValue();
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    assertNull(original.body.getValue());
    assertSame(originalBody, replacement.body.getValue());
  }

  @Test
  public void doOrRedoInternal_preservesNeighbors() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    assertSame(before, owner.statements.get(0));
    assertSame(after, owner.statements.get(2));
  }

  @Test
  public void undoInternal_restoresOriginalStatement() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(original, owner.statements.get(1));
  }

  @Test
  public void undoInternal_movesBodyBackToOriginal() {
    BlockStatement originalBody = original.body.getValue();
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertSame(originalBody, original.body.getValue());
    assertNull(replacement.body.getValue());
  }

  @Test
  public void roundTrip_preservesStatementCount() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    assertEquals(3, owner.statements.size());
  }

  @Test
  public void redoAfterUndo_reappliesReplacement() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    edit.undoInternal();
    edit.doOrRedoInternal(false);
    assertSame(replacement, owner.statements.get(1));
  }

  @Test
  public void replacementInsertedAtOriginalIndex() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    edit.doOrRedoInternal(true);
    assertEquals(1, owner.statements.indexOf(replacement));
  }

  @Test
  public void getTerseDescription_containsConvertPrefix() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit.getTerseDescription().startsWith("convert:"));
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit.getDetailedDescription().contains(ConvertStatementWithBodyEdit.class.getName()));
  }

  @Test
  public void getRedoPresentation_containsRedoPrefix() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_containsUndoPrefix() {
    ConvertStatementWithBodyEdit edit = new ConvertStatementWithBodyEdit(activity, replacement);
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void encodeMethodExists() throws Exception {
    Method method = ConvertStatementWithBodyEdit.class.getMethod("encode", BinaryEncoder.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void constructorSignature_matchesSource() throws Exception {
    Constructor<ConvertStatementWithBodyEdit> constructor = ConvertStatementWithBodyEdit.class.getConstructor(
        UserActivity.class,
        AbstractStatementWithBody.class);
    assertNotNull(constructor);
  }
}
