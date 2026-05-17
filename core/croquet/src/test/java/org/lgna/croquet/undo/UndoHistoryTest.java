package org.lgna.croquet.undo;

import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.undo.event.HistoryClearEvent;
import org.lgna.croquet.undo.event.HistoryInsertionIndexEvent;
import org.lgna.croquet.undo.event.HistoryListener;
import org.lgna.croquet.undo.event.HistoryPushEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Tests for {@link UndoHistory} — undo/redo stack that manages
 * {@link Edit} objects with listener dispatch. Covers push, insertion
 * index management, undo/redo via setInsertionIndex, listener events,
 * and stack truncation.
 */
public class UndoHistoryTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0006-ffffffffffff"), "undoTest");

  private UndoHistory history;

  @Before
  public void setUp() {
    history = new UndoHistory(TEST_GROUP);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsGroup() {
    assertSame(TEST_GROUP, history.getGroup());
  }

  @Test
  public void constructor_emptyStack() {
    assertTrue(history.getStack().isEmpty());
  }

  @Test
  public void constructor_insertionIndexZero() {
    assertEquals(0, history.getInsertionIndex());
  }

  // ── push ──────────────────────────────────────────────────────────

  @Test
  public void push_addsEditToStack() {
    history.push(createEdit(TEST_GROUP));
    assertEquals(1, history.getStack().size());
  }

  @Test
  public void push_incrementsInsertionIndex() {
    history.push(createEdit(TEST_GROUP));
    assertEquals(1, history.getInsertionIndex());
  }

  @Test
  public void push_multipleEdits_incrementsIndex() {
    history.push(createEdit(TEST_GROUP));
    history.push(createEdit(TEST_GROUP));
    history.push(createEdit(TEST_GROUP));
    assertEquals(3, history.getInsertionIndex());
    assertEquals(3, history.getStack().size());
  }

  @Test
  public void push_wrongGroup_doesNotAdd() {
    Group otherGroup = Group.getInstance(CroquetTestUtils.nextTestUUID(), "other");
    history.push(createEdit(otherGroup));
    assertEquals(0, history.getStack().size());
    assertEquals(0, history.getInsertionIndex());
  }

  // ── push truncates future ─────────────────────────────────────────

  @Test
  public void push_afterUndo_truncatesFuture() {
    history.push(createEdit(TEST_GROUP));
    history.push(createEdit(TEST_GROUP));
    // Undo one step
    history.setInsertionIndex(1);
    // Push a new edit — should truncate the undone edit
    history.push(createEdit(TEST_GROUP));
    assertEquals(2, history.getStack().size());
    assertEquals(2, history.getInsertionIndex());
  }

  // ── setInsertionIndex (undo) ──────────────────────────────────────

  @Test
  public void setInsertionIndex_decrements_performsUndo() {
    TestEdit edit = createEdit(TEST_GROUP);
    history.push(edit);
    history.setInsertionIndex(0);
    assertEquals(0, history.getInsertionIndex());
    assertEquals(1, edit.undoCount);
  }

  @Test
  public void setInsertionIndex_multipleUndo() {
    TestEdit edit1 = createEdit(TEST_GROUP);
    TestEdit edit2 = createEdit(TEST_GROUP);
    history.push(edit1);
    history.push(edit2);
    history.setInsertionIndex(0);
    assertEquals(0, history.getInsertionIndex());
    assertEquals(1, edit1.undoCount);
    assertEquals(1, edit2.undoCount);
  }

  // ── setInsertionIndex (redo) ──────────────────────────────────────

  @Test
  public void setInsertionIndex_increments_performsRedo() {
    TestEdit edit = createEdit(TEST_GROUP);
    history.push(edit);
    history.setInsertionIndex(0);
    history.setInsertionIndex(1);
    assertEquals(1, history.getInsertionIndex());
    assertEquals(1, edit.doOrRedoCount);
  }

  // ── setInsertionIndex bounds ──────────────────────────────────────

  @Test
  public void setInsertionIndex_negativeIndex_ignored() {
    history.push(createEdit(TEST_GROUP));
    int result = history.setInsertionIndex(-1);
    assertEquals(1, history.getInsertionIndex());
  }

  @Test
  public void setInsertionIndex_beyondStack_ignored() {
    history.push(createEdit(TEST_GROUP));
    int result = history.setInsertionIndex(99);
    assertEquals(1, history.getInsertionIndex());
  }

  @Test
  public void setInsertionIndex_sameIndex_noOp() {
    history.push(createEdit(TEST_GROUP));
    int result = history.setInsertionIndex(1);
    assertEquals(1, result);
  }

  // ── performUndo / performRedo ─────────────────────────────────────

  @Test
  public void performUndo_decrementsIndex() {
    history.push(createEdit(TEST_GROUP));
    history.performUndo();
    assertEquals(0, history.getInsertionIndex());
  }

  @Test
  public void performRedo_incrementsIndex() {
    history.push(createEdit(TEST_GROUP));
    history.performUndo();
    history.performRedo();
    assertEquals(1, history.getInsertionIndex());
  }

  @Test
  public void performUndo_onEmptyStack_doesNotCrash() {
    history.performUndo();
    assertEquals(0, history.getInsertionIndex());
  }

  @Test
  public void performRedo_atTop_doesNotCrash() {
    history.push(createEdit(TEST_GROUP));
    history.performRedo();
    // Already at top, no change
    assertEquals(1, history.getInsertionIndex());
  }

  // ── HistoryListener ───────────────────────────────────────────────

  @Test
  public void listener_operationPushingAndPushed_fire() {
    AtomicInteger pushingCount = new AtomicInteger(0);
    AtomicInteger pushedCount = new AtomicInteger(0);
    history.addHistoryListener(new TestHistoryListener() {
      @Override
      public void operationPushing(HistoryPushEvent e) {
        pushingCount.incrementAndGet();
      }

      @Override
      public void operationPushed(HistoryPushEvent e) {
        pushedCount.incrementAndGet();
      }
    });
    history.push(createEdit(TEST_GROUP));
    assertEquals(1, pushingCount.get());
    assertEquals(1, pushedCount.get());
  }

  @Test
  public void listener_insertionIndexChangingAndChanged_fire() {
    history.push(createEdit(TEST_GROUP));
    AtomicInteger changingCount = new AtomicInteger(0);
    AtomicInteger changedCount = new AtomicInteger(0);
    history.addHistoryListener(new TestHistoryListener() {
      @Override
      public void insertionIndexChanging(HistoryInsertionIndexEvent e) {
        changingCount.incrementAndGet();
      }

      @Override
      public void insertionIndexChanged(HistoryInsertionIndexEvent e) {
        changedCount.incrementAndGet();
      }
    });
    history.setInsertionIndex(0);
    assertEquals(1, changingCount.get());
    assertEquals(1, changedCount.get());
  }

  @Test
  public void listener_removed_doesNotFire() {
    AtomicInteger count = new AtomicInteger(0);
    HistoryListener listener = new TestHistoryListener() {
      @Override
      public void operationPushed(HistoryPushEvent e) {
        count.incrementAndGet();
      }
    };
    history.addHistoryListener(listener);
    history.removeHistoryListener(listener);
    history.push(createEdit(TEST_GROUP));
    assertEquals(0, count.get());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    assertTrue(history.toString().contains("UndoHistory"));
  }

  @Test
  public void toString_containsGroupInfo() {
    assertNotNull(history.toString());
  }

  // ── getStack ──────────────────────────────────────────────────────

  @Test
  public void getStack_returnsNonNull() {
    assertNotNull(history.getStack());
  }

  @Test
  public void getStack_reflectsEdits() {
    TestEdit edit = createEdit(TEST_GROUP);
    history.push(edit);
    assertSame(edit, history.getStack().get(0));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static TestEdit createEdit(Group group) {
    return new TestEdit(group);
  }

  private static class TestEdit implements Edit {
    private final Group group;
    int undoCount = 0;
    int doOrRedoCount = 0;

    TestEdit(Group group) {
      this.group = group;
    }

    @Override
    public Group getGroup() {
      return group;
    }

    @Override
    public boolean canUndo() {
      return true;
    }

    @Override
    public boolean canRedo() {
      return true;
    }

    @Override
    public void doOrRedo(boolean isDo) {
      doOrRedoCount++;
    }

    @Override
    public void undo() {
      undoCount++;
    }

    @Override
    public String getRedoPresentation() {
      return "Redo:test";
    }

    @Override
    public String getUndoPresentation() {
      return "Undo:test";
    }

    @Override
    public String getTerseDescription() {
      return "test edit";
    }

    @Override
    public String getDetailedDescription() {
      return "TestEdit: test";
    }

    @Override
    public String getLogDescription() {
      return "TestEdit: test log";
    }
  }

  private static class TestHistoryListener implements HistoryListener {
    @Override public void operationPushing(HistoryPushEvent e) {}
    @Override public void operationPushed(HistoryPushEvent e) {}
    @Override public void insertionIndexChanging(HistoryInsertionIndexEvent e) {}
    @Override public void insertionIndexChanged(HistoryInsertionIndexEvent e) {}
    @Override public void clearing(HistoryClearEvent e) {}
    @Override public void cleared(HistoryClearEvent e) {}
  }
}
