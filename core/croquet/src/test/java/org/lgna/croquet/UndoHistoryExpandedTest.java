package org.lgna.croquet;

import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.undo.UndoHistory;
import org.lgna.croquet.undo.event.HistoryClearEvent;
import org.lgna.croquet.undo.event.HistoryInsertionIndexEvent;
import org.lgna.croquet.undo.event.HistoryListener;
import org.lgna.croquet.undo.event.HistoryPushEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link UndoHistory} — deeper coverage of truncation,
 * multi-step undo/redo, listener event ordering, and edge cases.
 */
public class UndoHistoryExpandedTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-e010-ffffffffffff"), "undoExpTest");

  private UndoHistory history;

  @Before
  public void setUp() {
    history = new UndoHistory(TEST_GROUP);
  }

  // ── Multi-step undo/redo ─────────────────────────────────────────

  @Test
  public void undoRedo_fullCycle() {
    history.push(createEdit());
    history.push(createEdit());
    history.push(createEdit());
    assertEquals(3, history.getInsertionIndex());

    history.performUndo();
    assertEquals(2, history.getInsertionIndex());

    history.performUndo();
    assertEquals(1, history.getInsertionIndex());

    history.performRedo();
    assertEquals(2, history.getInsertionIndex());

    history.performRedo();
    assertEquals(3, history.getInsertionIndex());
  }

  @Test
  public void undoAll_thenRedoAll() {
    for (int i = 0; i < 5; i++) {
      history.push(createEdit());
    }
    // Undo all
    history.setInsertionIndex(0);
    assertEquals(0, history.getInsertionIndex());
    assertEquals(5, history.getStack().size()); // stack preserved

    // Redo all
    history.setInsertionIndex(5);
    assertEquals(5, history.getInsertionIndex());
  }

  // ── Truncation behavior ──────────────────────────────────────────

  @Test
  public void push_afterPartialUndo_truncatesFutureEdits() {
    history.push(createEdit());
    history.push(createEdit());
    history.push(createEdit()); // stack = [e1, e2, e3], index = 3

    history.setInsertionIndex(1); // undo 2 steps, index = 1
    assertEquals(3, history.getStack().size());

    history.push(createEdit()); // truncates e2, e3; pushes e4
    assertEquals(2, history.getStack().size());
    assertEquals(2, history.getInsertionIndex());
  }

  @Test
  public void push_atBottom_truncatesAll() {
    history.push(createEdit());
    history.push(createEdit());
    history.setInsertionIndex(0); // undo all
    history.push(createEdit()); // truncates all; pushes new
    assertEquals(1, history.getStack().size());
    assertEquals(1, history.getInsertionIndex());
  }

  // ── Listener event ordering ──────────────────────────────────────

  @Test
  public void pushListeners_fireInOrder_pushingBeforePushed() {
    List<String> order = new ArrayList<>();
    history.addHistoryListener(new NoOpHistoryListener() {
      @Override public void operationPushing(HistoryPushEvent e) { order.add("pushing"); }
      @Override public void operationPushed(HistoryPushEvent e) { order.add("pushed"); }
    });
    history.push(createEdit());
    assertEquals(2, order.size());
    assertEquals("pushing", order.get(0));
    assertEquals("pushed", order.get(1));
  }

  @Test
  public void indexListeners_fireInOrder_changingBeforeChanged() {
    history.push(createEdit());
    List<String> order = new ArrayList<>();
    history.addHistoryListener(new NoOpHistoryListener() {
      @Override public void insertionIndexChanging(HistoryInsertionIndexEvent e) { order.add("changing"); }
      @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e) { order.add("changed"); }
    });
    history.setInsertionIndex(0);
    assertEquals(2, order.size());
    assertEquals("changing", order.get(0));
    assertEquals("changed", order.get(1));
  }

  @Test
  public void pushEvent_containsEdit() {
    AtomicReference<Edit> captured = new AtomicReference<>();
    history.addHistoryListener(new NoOpHistoryListener() {
      @Override public void operationPushed(HistoryPushEvent e) { captured.set(e.getEdit()); }
    });
    Edit edit = createEdit();
    history.push(edit);
    assertSame(edit, captured.get());
  }

  @Test
  public void indexEvent_containsPrevAndNextIndex() {
    history.push(createEdit());
    AtomicInteger prevIdx = new AtomicInteger(-1);
    AtomicInteger nextIdx = new AtomicInteger(-1);
    history.addHistoryListener(new NoOpHistoryListener() {
      @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e) {
        prevIdx.set(e.getPrevIndex());
        nextIdx.set(e.getNextIndex());
      }
    });
    history.setInsertionIndex(0);
    assertEquals(1, prevIdx.get());
    assertEquals(0, nextIdx.get());
  }

  // ── Stack contents ───────────────────────────────────────────────

  @Test
  public void getStack_preservesInsertionOrder() {
    Edit e1 = createEdit();
    Edit e2 = createEdit();
    Edit e3 = createEdit();
    history.push(e1);
    history.push(e2);
    history.push(e3);
    assertSame(e1, history.getStack().get(0));
    assertSame(e2, history.getStack().get(1));
    assertSame(e3, history.getStack().get(2));
  }

  @Test
  public void getStack_afterUndo_stillContainsAll() {
    history.push(createEdit());
    history.push(createEdit());
    history.performUndo();
    assertEquals(2, history.getStack().size());
  }

  // ── Wrong-group push ─────────────────────────────────────────────

  @Test
  public void push_wrongGroup_stackUnchanged() {
    Group other = Group.getInstance(CroquetTestUtils.nextTestUUID(), "otherGroup");
    Edit wrongGroupEdit = new Edit() {
      @Override public Group getGroup() { return other; }
      @Override public boolean canUndo() { return true; }
      @Override public boolean canRedo() { return true; }
      @Override public void doOrRedo(boolean isDo) {}
      @Override public void undo() {}
      @Override public String getRedoPresentation() { return "Redo"; }
      @Override public String getUndoPresentation() { return "Undo"; }
      @Override public String getTerseDescription() { return "wrong"; }
      @Override public String getDetailedDescription() { return "wrong"; }
      @Override public String getLogDescription() { return "wrong"; }
    };
    history.push(wrongGroupEdit);
    assertTrue(history.getStack().isEmpty());
    assertEquals(0, history.getInsertionIndex());
  }

  // ── Helpers ──────────────────────────────────────────────────────

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

  private static class NoOpHistoryListener implements HistoryListener {
    @Override public void operationPushing(HistoryPushEvent e) {}
    @Override public void operationPushed(HistoryPushEvent e) {}
    @Override public void insertionIndexChanging(HistoryInsertionIndexEvent e) {}
    @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e) {}
    @Override public void clearing(HistoryClearEvent e) {}
    @Override public void cleared(HistoryClearEvent e) {}
  }
}
