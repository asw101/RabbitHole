package org.lgna.croquet;

import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.edits.StateEdit;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Edit} interface contract — verifies that implementations
 * provide the required operations and descriptions.
 */
public class EditInterfaceTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-d001-ffffffffffff"), "editIfcTest");

  @Test
  public void edit_interface_hasGetGroup() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getGroup());
  }

  @Test
  public void edit_interface_canUndo() {
    Edit edit = createTestEdit();
    assertTrue(edit.canUndo());
  }

  @Test
  public void edit_interface_canRedo() {
    Edit edit = createTestEdit();
    assertTrue(edit.canRedo());
  }

  @Test
  public void edit_interface_doOrRedo() {
    Edit edit = createTestEdit();
    edit.doOrRedo(true); // should not throw
  }

  @Test
  public void edit_interface_undo() {
    Edit edit = createTestEdit();
    edit.undo(); // should not throw
  }

  @Test
  public void edit_interface_getRedoPresentation() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getRedoPresentation());
  }

  @Test
  public void edit_interface_getUndoPresentation() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getUndoPresentation());
  }

  @Test
  public void edit_interface_getTerseDescription() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getTerseDescription());
  }

  @Test
  public void edit_interface_getDetailedDescription() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getDetailedDescription());
  }

  @Test
  public void edit_interface_getLogDescription() {
    Edit edit = createTestEdit();
    assertNotNull(edit.getLogDescription());
  }

  @Test
  public void stateEdit_implementsEdit() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertTrue(edit instanceof Edit);
  }

  @Test
  public void stateEdit_getGroup_nullActivity_returnsNull() {
    StateEdit<String> edit = new StateEdit<>(null, "old", "new");
    assertNull(edit.getGroup());
  }

  @Test
  public void stateEdit_descriptions_notEmpty() {
    StateEdit<String> edit = new StateEdit<>(null, "alpha", "beta");
    assertFalse(edit.getTerseDescription().isEmpty());
    assertFalse(edit.getDetailedDescription().isEmpty());
    assertFalse(edit.getLogDescription().isEmpty());
  }

  private static Edit createTestEdit() {
    return new Edit() {
      @Override public Group getGroup() { return TEST_GROUP; }
      @Override public boolean canUndo() { return true; }
      @Override public boolean canRedo() { return true; }
      @Override public void doOrRedo(boolean isDo) {}
      @Override public void undo() {}
      @Override public String getRedoPresentation() { return "Redo:test"; }
      @Override public String getUndoPresentation() { return "Undo:test"; }
      @Override public String getTerseDescription() { return "test"; }
      @Override public String getDetailedDescription() { return "TestEdit"; }
      @Override public String getLogDescription() { return "TestEdit log"; }
    };
  }
}
