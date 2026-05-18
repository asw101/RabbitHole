package org.lgna.croquet.history.event;

import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link EditCommittedEvent} — event wrapping a committed {@link Edit}.
 * Covers construction, getEdit, null handling, and identity.
 */
public class EditCommittedEventTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-e001-ffffffffffff"), "editEvtTest");

  @Test
  public void constructor_storesEdit() {
    Edit edit = createEdit();
    EditCommittedEvent event = new EditCommittedEvent(edit);
    assertSame(edit, event.getEdit());
  }

  @Test
  public void constructor_nullEdit() {
    EditCommittedEvent event = new EditCommittedEvent(null);
    assertNull(event.getEdit());
  }

  @Test
  public void implementsActivityEvent() {
    EditCommittedEvent event = new EditCommittedEvent(createEdit());
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void getEdit_returnsExactReference() {
    Edit edit = createEdit();
    EditCommittedEvent event = new EditCommittedEvent(edit);
    Edit retrieved = event.getEdit();
    assertSame(edit, retrieved);
  }

  @Test
  public void toString_returnsNonNull() {
    EditCommittedEvent event = new EditCommittedEvent(createEdit());
    assertNotNull(event.toString());
  }

  @Test
  public void twoEvents_sameEdit_bothReturnIt() {
    Edit edit = createEdit();
    EditCommittedEvent e1 = new EditCommittedEvent(edit);
    EditCommittedEvent e2 = new EditCommittedEvent(edit);
    assertSame(e1.getEdit(), e2.getEdit());
  }

  private static Edit createEdit() {
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
