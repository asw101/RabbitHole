package org.lgna.croquet;

import org.lgna.croquet.edits.StateEdit;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link org.lgna.croquet.edits.AbstractEdit} — base class for all edits.
 * Tested through the concrete {@link StateEdit} subclass.
 * Covers constructor paths, description methods, undo/redo behavior, and
 * the known CannotRedoException bug in undo().
 */
public class AbstractEditTest {

  @Test
  public void constructor_nullActivity_succeeds() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNotNull(edit);
  }

  @Test
  public void getModel_nullActivity_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertNull(edit.getModel());
  }

  @Test
  public void canUndo_nullActivity_false() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canUndo());
  }

  @Test
  public void canRedo_nullActivity_false() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertFalse(edit.canRedo());
  }

  @Test
  public void doOrRedo_isDo_succeeds() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.doOrRedo(true); // isDo path doesn't check canRedo
  }

  @Test(expected = javax.swing.undo.CannotRedoException.class)
  public void doOrRedo_isRedo_throwsWhenCannotRedo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.doOrRedo(false);
  }

  @Test(expected = javax.swing.undo.CannotRedoException.class)
  public void undo_cannotUndo_throwsCannotRedoException() {
    // Characterization: known bug — undo() throws CannotRedoException (not CannotUndoException)
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    edit.undo();
  }

  @Test
  public void getTerseDescription_returnsNonEmpty() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    String desc = edit.getTerseDescription();
    assertNotNull(desc);
    assertFalse(desc.isEmpty());
  }

  @Test
  public void getDetailedDescription_containsClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getDetailedDescription().contains("StateEdit"));
  }

  @Test
  public void getLogDescription_containsClassName() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getLogDescription().contains("StateEdit"));
  }

  @Test
  public void getUndoPresentation_startsWithUndo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getUndoPresentation().startsWith("Undo:"));
  }

  @Test
  public void getRedoPresentation_startsWithRedo() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getRedoPresentation().startsWith("Redo:"));
  }

  @Test
  public void toString_containsDetailedDescription() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    String str = edit.toString();
    assertNotNull(str);
    assertTrue(str.contains("StateEdit"));
  }

  @Test
  public void terseDescription_containsValues() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "beta");
    String desc = edit.getTerseDescription();
    assertTrue(desc.contains("alpha"));
    assertTrue(desc.contains("beta"));
  }

  @Test
  public void terseDescription_containsArrow() {
    StateEdit<String> edit = new StateEdit<>(null, "a", "b");
    assertTrue(edit.getTerseDescription().contains("===>"));
  }

  @Test
  public void nullValues_inEdit() {
    StateEdit<String> edit = new StateEdit<>(null, null, null);
    assertNull(edit.getPreviousValue());
    assertNull(edit.getNextValue());
  }
}
