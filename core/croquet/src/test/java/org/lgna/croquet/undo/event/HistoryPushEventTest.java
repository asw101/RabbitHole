package org.lgna.croquet.undo.event;

import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link HistoryPushEvent} — event fired when an edit is pushed onto the undo stack.
 */
public class HistoryPushEventTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-c002-ffffffffffff"), "pushEvtTest");

  @Test
  public void constructor_setsSource() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    Edit edit = createEdit();
    HistoryPushEvent event = new HistoryPushEvent(history, edit);
    assertSame(history, event.getSource());
  }

  @Test
  public void constructor_setsEdit() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    Edit edit = createEdit();
    HistoryPushEvent event = new HistoryPushEvent(history, edit);
    assertSame(edit, event.getEdit());
  }

  @Test
  public void constructor_nullEdit() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryPushEvent event = new HistoryPushEvent(history, null);
    assertNull(event.getEdit());
  }

  @Test
  public void extendsHistoryEvent() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryPushEvent event = new HistoryPushEvent(history, createEdit());
    assertTrue(event instanceof HistoryEvent);
  }

  @Test
  public void getEdit_returnsExactReference() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    Edit edit = createEdit();
    HistoryPushEvent event = new HistoryPushEvent(history, edit);
    assertSame(edit, event.getEdit());
  }

  @Test
  public void toString_returnsNonNull() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryPushEvent event = new HistoryPushEvent(history, createEdit());
    assertNotNull(event.toString());
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
