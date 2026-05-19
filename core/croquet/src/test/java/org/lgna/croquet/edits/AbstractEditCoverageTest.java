package org.lgna.croquet.edits;

import org.junit.Test;
import org.lgna.croquet.CompletionModel;
import org.lgna.croquet.history.UserActivity;

import javax.swing.undo.CannotRedoException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link AbstractEdit} — DescriptionStyle enum,
 * default canUndo/canRedo, description methods, doOrRedo/undo contracts.
 * Uses a minimal concrete subclass to test abstract class behavior.
 */
public class AbstractEditCoverageTest {

  private static class TestEdit extends AbstractEdit<CompletionModel> {
    private final String description;
    private boolean doOrRedoCalled;
    private boolean undoCalled;

    TestEdit(UserActivity activity, String description) {
      super(activity);
      this.description = description;
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
      rv.append(description);
    }
  }

  private static class NonUndoableEdit extends AbstractEdit<CompletionModel> {
    NonUndoableEdit() {
      super((UserActivity) null);
    }

    @Override
    public boolean canUndo() {
      return false;
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
    }

    @Override
    protected void undoInternal() {
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("non-undoable");
    }
  }

  private static class NonRedoableEdit extends AbstractEdit<CompletionModel> {
    NonRedoableEdit() {
      super((UserActivity) null);
    }

    @Override
    public boolean canRedo() {
      return false;
    }

    @Override
    protected void doOrRedoInternal(boolean isDo) {
    }

    @Override
    protected void undoInternal() {
    }

    @Override
    protected void appendDescription(StringBuilder rv, DescriptionStyle descriptionStyle) {
      rv.append("non-redoable");
    }
  }

  // ── canUndo / canRedo defaults ────────────────────────────────────

  @Test
  public void canUndo_defaultTrue() {
    TestEdit edit = new TestEdit(null, "test");
    assertTrue(edit.canUndo());
  }

  @Test
  public void canRedo_defaultTrue() {
    TestEdit edit = new TestEdit(null, "test");
    assertTrue(edit.canRedo());
  }

  // ── doOrRedo ──────────────────────────────────────────────────────

  @Test
  public void doOrRedo_isDo_callsInternal() {
    TestEdit edit = new TestEdit(null, "test");
    edit.doOrRedo(true);
    assertTrue(edit.doOrRedoCalled);
  }

  @Test
  public void doOrRedo_isRedo_callsInternal() {
    TestEdit edit = new TestEdit(null, "test");
    edit.doOrRedo(false);
    assertTrue(edit.doOrRedoCalled);
  }

  @Test(expected = CannotRedoException.class)
  public void doOrRedo_nonRedoable_throws() {
    NonRedoableEdit edit = new NonRedoableEdit();
    edit.doOrRedo(false);
  }

  @Test
  public void doOrRedo_nonRedoable_isDo_doesNotThrow() {
    NonRedoableEdit edit = new NonRedoableEdit();
    edit.doOrRedo(true);
    // isDo bypasses canRedo check
  }

  // ── undo ──────────────────────────────────────────────────────────

  @Test
  public void undo_callsInternal() {
    TestEdit edit = new TestEdit(null, "test");
    edit.undo();
    assertTrue(edit.undoCalled);
  }

  @Test(expected = CannotRedoException.class)
  public void undo_nonUndoable_throws() {
    NonUndoableEdit edit = new NonUndoableEdit();
    edit.undo();
  }

  // ── Description methods ───────────────────────────────────────────

  @Test
  public void getTerseDescription_returnsDescription() {
    TestEdit edit = new TestEdit(null, "myEdit");
    assertEquals("myEdit", edit.getTerseDescription());
  }

  @Test
  public void getTerseDescription_empty_usesClassName() {
    TestEdit edit = new TestEdit(null, "");
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("TestEdit"));
  }

  @Test
  public void getDetailedDescription_includesClassName() {
    TestEdit edit = new TestEdit(null, "detail");
    String desc = edit.getDetailedDescription();
    assertTrue(desc.contains("TestEdit"));
    assertTrue(desc.contains("detail"));
  }

  @Test
  public void getLogDescription_includesClassName() {
    TestEdit edit = new TestEdit(null, "logInfo");
    String desc = edit.getLogDescription();
    assertTrue(desc.contains("TestEdit"));
    assertTrue(desc.contains("logInfo"));
  }

  @Test
  public void getRedoPresentation_startsWithRedo() {
    TestEdit edit = new TestEdit(null, "myEdit");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void getUndoPresentation_startsWithUndo() {
    TestEdit edit = new TestEdit(null, "myEdit");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  // ── Model / Group with null activity ──────────────────────────────

  @Test
  public void getModel_nullActivity_returnsNull() {
    TestEdit edit = new TestEdit(null, "test");
    assertNull(edit.getModel());
  }

  @Test
  public void getGroup_nullActivity_returnsNull() {
    TestEdit edit = new TestEdit(null, "test");
    assertNull(edit.getGroup());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_returnsDetailedDescription() {
    TestEdit edit = new TestEdit(null, "desc");
    assertEquals(edit.getDetailedDescription(), edit.toString());
  }

  // ── DescriptionStyle enum ─────────────────────────────────────────

  @Test
  public void descriptionStyle_terseValues() throws Exception {
    Class<?> dsClass = null;
    for (Class<?> c : AbstractEdit.class.getDeclaredClasses()) {
      if (c.getSimpleName().equals("DescriptionStyle")) {
        dsClass = c;
        break;
      }
    }
    assertNotNull("DescriptionStyle enum should exist", dsClass);
    assertTrue(dsClass.isEnum());
    Object[] constants = dsClass.getEnumConstants();
    assertEquals(3, constants.length);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractEdit.class.getModifiers()));
  }

  @Test
  public void class_implementsEdit() {
    assertTrue(Edit.class.isAssignableFrom(AbstractEdit.class));
  }

  @Test
  public void class_implementsBinaryEncodableAndDecodable() {
    assertTrue(edu.cmu.cs.dennisc.codec.BinaryEncodableAndDecodable.class.isAssignableFrom(AbstractEdit.class));
  }
}
