package org.lgna.croquet.undo.event;

import org.lgna.croquet.Group;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link HistoryClearEvent} — event fired when an UndoHistory is cleared.
 */
public class HistoryClearEventTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-c001-ffffffffffff"), "clearEvtTest");

  @Test
  public void constructor_setsSource() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryClearEvent event = new HistoryClearEvent(history);
    assertSame(history, event.getSource());
  }

  @Test
  public void extendsHistoryEvent() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryClearEvent event = new HistoryClearEvent(history);
    assertTrue(event instanceof HistoryEvent);
  }

  @Test
  public void getSource_returnsUndoHistory() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryClearEvent event = new HistoryClearEvent(history);
    assertNotNull(event.getSource());
    assertSame(history, event.getSource());
  }

  @Test
  public void twoEvents_sameSource() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryClearEvent e1 = new HistoryClearEvent(history);
    HistoryClearEvent e2 = new HistoryClearEvent(history);
    assertSame(e1.getSource(), e2.getSource());
  }

  @Test
  public void toString_returnsNonNull() {
    UndoHistory history = new UndoHistory(TEST_GROUP);
    HistoryClearEvent event = new HistoryClearEvent(history);
    assertNotNull(event.toString());
  }
}
