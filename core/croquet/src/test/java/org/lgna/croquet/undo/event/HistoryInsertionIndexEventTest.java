package org.lgna.croquet.undo.event;

import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link HistoryInsertionIndexEvent} — event fired when the undo/redo
 * insertion index changes. Covers construction, prev/next index getters, and source.
 */
public class HistoryInsertionIndexEventTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-c003-ffffffffffff"), "idxEvtTest");

  @Test
  public void constructor_setsSource() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 0, 1);
    assertSame(history, event.getSource());
  }

  @Test
  public void constructor_setsPrevIndex() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 3, 5);
    assertEquals(3, event.getPrevIndex());
  }

  @Test
  public void constructor_setsNextIndex() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 3, 5);
    assertEquals(5, event.getNextIndex());
  }

  @Test
  public void extendsHistoryEvent() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 0, 0);
    assertTrue(event instanceof HistoryEvent);
  }

  @Test
  public void zeroToZero_indicesMatch() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 0, 0);
    assertEquals(0, event.getPrevIndex());
    assertEquals(0, event.getNextIndex());
  }

  @Test
  public void decrementIndex_undoScenario() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 5, 4);
    assertEquals(5, event.getPrevIndex());
    assertEquals(4, event.getNextIndex());
    assertTrue(event.getPrevIndex() > event.getNextIndex());
  }

  @Test
  public void incrementIndex_redoScenario() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 2, 3);
    assertTrue(event.getNextIndex() > event.getPrevIndex());
  }

  @Test
  public void largeIndices() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 1000, 999);
    assertEquals(1000, event.getPrevIndex());
    assertEquals(999, event.getNextIndex());
  }

  @Test
  public void toString_returnsNonNull() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryInsertionIndexEvent event = new HistoryInsertionIndexEvent(history, 0, 1);
    assertNotNull(event.toString());
  }
}
