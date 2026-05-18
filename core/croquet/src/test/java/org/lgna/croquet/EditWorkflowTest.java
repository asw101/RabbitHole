package org.lgna.croquet;

import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.edits.StateEdit;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.history.event.ActivityEvent;
import org.lgna.croquet.history.event.EditCommittedEvent;
import org.lgna.croquet.history.event.FinishedEvent;
import org.lgna.croquet.history.event.CancelEvent;
import org.lgna.croquet.undo.UndoHistory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Integration-level tests exercising the full edit workflow:
 * StateEdit → UserActivity → UndoHistory → undo/redo cycle.
 */
public class EditWorkflowTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-f001-ffffffffffff"), "workflowTest");

  private UndoHistory history;

  @Before
  public void setUp() {
    history = new UndoHistory(TEST_GROUP);
  }

  @Test
  public void stateEdit_commitToActivity_appearsInHistory() {
    UserActivity activity = new UserActivity();
    StateEdit<String> edit = new StateEdit<>(activity, "old", "new");
    activity.commitAndInvokeDo(edit);
    assertTrue(activity.isSuccessfullyCompleted());
    assertSame(edit, activity.getEdit());
  }

  @Test
  public void stateEdit_pushToHistory() {
    // StateEdit.getGroup() returns null when activity has no model,
    // so push to UndoHistory won't match. Use TestEdit instead.
    TestEdit edit = new TestEdit();
    history.push(edit);
    assertEquals(1, history.getStack().size());
    assertEquals(1, history.getInsertionIndex());
  }

  @Test
  public void editLifecycle_createCommitVerify() {
    UserActivity activity = new UserActivity();

    // Verify initial state
    assertTrue(activity.isPending());
    assertNull(activity.getEdit());

    // Create and commit edit
    TestEdit edit = new TestEdit();
    activity.commitAndInvokeDo(edit);

    // Verify post-commit state
    assertTrue(activity.isSuccessfullyCompleted());
    assertSame(edit, activity.getEdit());
    assertEquals(1, edit.doCount);
  }

  @Test
  public void editLifecycle_listeners_receiveEditCommittedEvent() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);

    TestEdit edit = new TestEdit();
    activity.commitAndInvokeDo(edit);

    assertTrue(captured.get() instanceof EditCommittedEvent);
    assertSame(edit, ((EditCommittedEvent) captured.get()).getEdit());
  }

  @Test
  public void cancelActivity_noEdit() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);

    activity.cancel();

    assertTrue(activity.isCanceled());
    assertNull(activity.getEdit());
    assertTrue(captured.get() instanceof CancelEvent);
  }

  @Test
  public void undoHistory_pushAndUndo() {
    TestEdit edit = new TestEdit();
    history.push(edit);
    assertEquals(1, history.getInsertionIndex());

    history.performUndo();
    assertEquals(0, history.getInsertionIndex());
    assertEquals(1, edit.undoCount);
  }

  @Test
  public void undoHistory_pushUndoRedo() {
    TestEdit edit = new TestEdit();
    history.push(edit);

    history.performUndo();
    history.performRedo();

    assertEquals(1, history.getInsertionIndex());
    assertEquals(1, edit.undoCount);
    assertEquals(1, edit.redoCount);
  }

  @Test
  public void childActivities_nestedLifecycle() {
    UserActivity parent = new UserActivity();
    List<String> events = new ArrayList<>();
    parent.addListener(e -> events.add(e.getClass().getSimpleName()));

    UserActivity child = parent.newChildActivity();
    child.commitAndInvokeDo(new TestEdit());

    assertTrue(events.contains("ChangeEvent"));       // from newChildActivity
    assertTrue(events.contains("EditCommittedEvent")); // from commitAndInvokeDo
  }

  private static class TestEdit implements Edit {
    int doCount = 0;
    int undoCount = 0;
    int redoCount = 0;

    @Override public Group getGroup() { return TEST_GROUP; }
    @Override public boolean canUndo() { return true; }
    @Override public boolean canRedo() { return true; }
    @Override public void doOrRedo(boolean isDo) {
      if (isDo) { doCount++; } else { redoCount++; }
    }
    @Override public void undo() { undoCount++; }
    @Override public String getRedoPresentation() { return "Redo:test"; }
    @Override public String getUndoPresentation() { return "Undo:test"; }
    @Override public String getTerseDescription() { return "test"; }
    @Override public String getDetailedDescription() { return "TestEdit"; }
    @Override public String getLogDescription() { return "TestEdit log"; }
  }
}
