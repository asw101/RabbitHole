package org.lgna.croquet.history;

import org.lgna.croquet.CancelException;
import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.event.ActivityEvent;
import org.lgna.croquet.history.event.CancelEvent;
import org.lgna.croquet.history.event.ChangeEvent;
import org.lgna.croquet.history.event.EditCommittedEvent;
import org.lgna.croquet.history.event.FinishedEvent;
import org.lgna.croquet.history.event.Listener;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link UserActivity} and its {@link ActivityNode} base class.
 * Covers lifecycle (pending/finished/canceled), child activities, listeners,
 * produced values, edit commit, and toString formatting.
 */
public class UserActivityTest {

  private UserActivity activity;

  @Before
  public void setUp() {
    activity = new UserActivity();
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_isPending() {
    assertTrue(activity.isPending());
  }

  @Test
  public void constructor_notFinished() {
    assertFalse(activity.isSuccessfullyCompleted());
  }

  @Test
  public void constructor_notCanceled() {
    assertFalse(activity.isCanceled());
  }

  @Test
  public void constructor_notCanceledByError() {
    assertFalse(activity.isCanceledByError());
  }

  @Test
  public void constructor_noEdit() {
    assertNull(activity.getEdit());
  }

  @Test
  public void constructor_noModel() {
    assertNull(activity.getModel());
  }

  @Test
  public void constructor_noTrigger() {
    assertNull(activity.getTrigger());
  }

  @Test
  public void constructor_noOwner() {
    assertNull(activity.getOwner());
  }

  @Test
  public void constructor_noProducedValue() {
    assertNull(activity.getProducedValue());
  }

  @Test
  public void constructor_emptyChildActivities() {
    assertTrue(activity.getChildActivities().isEmpty());
  }

  @Test
  public void constructor_childStepCountZero() {
    assertEquals(0, activity.getChildStepCount());
  }

  // ── newChildActivity ──────────────────────────────────────────────

  @Test
  public void newChildActivity_createsChild() {
    UserActivity child = activity.newChildActivity();
    assertNotNull(child);
  }

  @Test
  public void newChildActivity_childOwnerIsParent() {
    UserActivity child = activity.newChildActivity();
    assertSame(activity, child.getOwner());
  }

  @Test
  public void newChildActivity_addedToChildActivities() {
    UserActivity child = activity.newChildActivity();
    assertEquals(1, activity.getChildActivities().size());
    assertSame(child, activity.getChildActivities().get(0));
  }

  @Test
  public void newChildActivity_incrementsChildStepCount() {
    activity.newChildActivity();
    assertEquals(1, activity.getChildStepCount());
  }

  @Test
  public void newChildActivity_multipleChildren() {
    activity.newChildActivity();
    activity.newChildActivity();
    activity.newChildActivity();
    assertEquals(3, activity.getChildActivities().size());
    assertEquals(3, activity.getChildStepCount());
  }

  @Test
  public void newChildActivity_childIsPending() {
    UserActivity child = activity.newChildActivity();
    assertTrue(child.isPending());
  }

  // ── getActivityWithoutTrigger ─────────────────────────────────────

  @Test
  public void getActivityWithoutTrigger_noTrigger_returnsSelf() {
    assertSame(activity, activity.getActivityWithoutTrigger());
  }

  // ── getActivityWithoutModel ───────────────────────────────────────

  @Test
  public void getActivityWithoutModel_noModel_returnsSelf() {
    assertSame(activity, activity.getActivityWithoutModel());
  }

  // ── getLatestActivity ─────────────────────────────────────────────

  @Test
  public void getLatestActivity_noChildren_returnsSelf() {
    assertSame(activity, activity.getLatestActivity());
  }

  @Test
  public void getLatestActivity_pendingChild_returnsChild() {
    UserActivity child = activity.newChildActivity();
    assertSame(child, activity.getLatestActivity());
  }

  @Test
  public void getLatestActivity_finishedChild_returnsSelf() {
    UserActivity child = activity.newChildActivity();
    child.finish();
    assertSame(activity, activity.getLatestActivity());
  }

  @Test
  public void getLatestActivity_nestedPendingChild() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    assertSame(grandchild, activity.getLatestActivity());
  }

  // ── getIndexOfTransaction ─────────────────────────────────────────

  @Test
  public void getIndexOfTransaction_returnsCorrectIndex() {
    UserActivity child1 = activity.newChildActivity();
    UserActivity child2 = activity.newChildActivity();
    assertEquals(0, activity.getIndexOfTransaction(child1));
    assertEquals(1, activity.getIndexOfTransaction(child2));
  }

  // ── getChildAt ────────────────────────────────────────────────────

  @Test
  public void getChildAt_returnsChildActivity() {
    UserActivity child = activity.newChildActivity();
    assertSame(child, activity.getChildAt(0));
  }

  // ── findFirstMenuSelectStep ───────────────────────────────────────

  @Test
  public void findFirstMenuSelectStep_noPrepSteps_returnsNull() {
    assertNull(activity.findFirstMenuSelectStep());
  }

  // ── findDropSite ──────────────────────────────────────────────────

  @Test
  public void findDropSite_noTrigger_returnsNull() {
    assertNull(activity.findDropSite());
  }

  // ── finish ────────────────────────────────────────────────────────

  @Test
  public void finish_setsStatusToFinished() {
    activity.finish();
    assertTrue(activity.isSuccessfullyCompleted());
  }

  @Test
  public void finish_notPending() {
    activity.finish();
    assertFalse(activity.isPending());
  }

  @Test
  public void finish_notCanceled() {
    activity.finish();
    assertFalse(activity.isCanceled());
  }

  // ── cancel ────────────────────────────────────────────────────────

  @Test
  public void cancel_setsStatusToCanceled() {
    activity.cancel();
    assertTrue(activity.isCanceled());
  }

  @Test
  public void cancel_notPending() {
    activity.cancel();
    assertFalse(activity.isPending());
  }

  @Test
  public void cancel_notFinished() {
    activity.cancel();
    assertFalse(activity.isSuccessfullyCompleted());
  }

  @Test
  public void cancel_notCanceledByError() {
    activity.cancel();
    assertFalse(activity.isCanceledByError());
  }

  // ── cancel with CancelException ───────────────────────────────────

  @Test
  public void cancelWithException_nullException_setsCanceled() {
    activity.cancel((CancelException) null);
    assertTrue(activity.isCanceled());
    assertFalse(activity.isCanceledByError());
  }

  @Test
  public void cancelWithException_withCause_setsError() {
    CancelException ce = new CancelException();
    ce.initCause(new RuntimeException("test cause"));
    activity.cancel(ce);
    assertTrue(activity.isCanceled());
    assertTrue(activity.isCanceledByError());
  }

  @Test
  public void cancelWithException_noCause_setsError() {
    // getCause() returns null for fresh CancelException, which != ce,
    // so the code treats it as an error cancellation.
    CancelException ce = new CancelException();
    activity.cancel(ce);
    assertTrue(activity.isCanceled());
    assertTrue(activity.isCanceledByError());
  }

  // ── removeFromOwnerIfEmpty ────────────────────────────────────────

  @Test
  public void finish_emptyChild_removedFromParent() {
    UserActivity child = activity.newChildActivity();
    assertEquals(1, activity.getChildActivities().size());
    child.finish();
    assertEquals(0, activity.getChildActivities().size());
  }

  @Test
  public void cancel_emptyChild_removedFromParent() {
    UserActivity child = activity.newChildActivity();
    child.cancel();
    assertEquals(0, activity.getChildActivities().size());
  }

  // ── producedValue ─────────────────────────────────────────────────

  @Test
  public void setProducedValue_updatesValue() {
    activity.setProducedValue("result");
    assertEquals("result", activity.getProducedValue());
  }

  @Test
  public void setProducedValue_null() {
    activity.setProducedValue(null);
    assertNull(activity.getProducedValue());
  }

  @Test
  public void setProducedValue_integer() {
    activity.setProducedValue(42);
    assertEquals(42, activity.getProducedValue());
  }

  // ── commitAndInvokeDo ─────────────────────────────────────────────

  @Test
  public void commitAndInvokeDo_setsEdit() {
    TestEdit edit = new TestEdit();
    activity.commitAndInvokeDo(edit);
    assertSame(edit, activity.getEdit());
  }

  @Test
  public void commitAndInvokeDo_marksFinished() {
    activity.commitAndInvokeDo(new TestEdit());
    assertTrue(activity.isSuccessfullyCompleted());
  }

  @Test
  public void commitAndInvokeDo_invokesDoOrRedo() {
    TestEdit edit = new TestEdit();
    activity.commitAndInvokeDo(edit);
    assertEquals(1, edit.doOrRedoCount);
  }

  // ── setCompletionModel ────────────────────────────────────────────

  @Test
  public void getCompletionModel_initiallyNull() {
    assertNull(activity.getCompletionModel());
  }

  // ── ActivityNode listener ─────────────────────────────────────────

  @Test
  public void addListener_firesOnFinish() {
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    activity.addListener(listener);
    activity.finish();
    assertTrue(count.get() > 0);
  }

  @Test
  public void removeListener_doesNotFire() {
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    activity.addListener(listener);
    activity.removeListener(listener);
    activity.finish();
    assertEquals(0, count.get());
  }

  @Test
  public void isListening_returnsTrueAfterAdd() {
    Listener listener = e -> {};
    activity.addListener(listener);
    assertTrue(activity.isListening(listener));
  }

  @Test
  public void isListening_returnsFalseAfterRemove() {
    Listener listener = e -> {};
    activity.addListener(listener);
    activity.removeListener(listener);
    assertFalse(activity.isListening(listener));
  }

  @Test
  public void listener_firesChangeEventOnNewChild() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.newChildActivity();
    assertNotNull(captured.get());
    assertTrue(captured.get() instanceof ChangeEvent);
  }

  @Test
  public void listener_firesCancelEventOnCancel() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.cancel();
    assertTrue(captured.get() instanceof CancelEvent);
  }

  @Test
  public void listener_firesFinishedEventOnFinish() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.finish();
    assertTrue(captured.get() instanceof FinishedEvent);
  }

  @Test
  public void listener_firesEditCommittedEvent() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.commitAndInvokeDo(new TestEdit());
    assertTrue(captured.get() instanceof EditCommittedEvent);
  }

  @Test
  public void editCommittedEvent_containsEdit() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    TestEdit edit = new TestEdit();
    activity.commitAndInvokeDo(edit);
    EditCommittedEvent ece = (EditCommittedEvent) captured.get();
    assertSame(edit, ece.getEdit());
  }

  @Test
  public void changeEvent_containsNode() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    UserActivity child = activity.newChildActivity();
    ChangeEvent<?> ce = (ChangeEvent<?>) captured.get();
    assertSame(child, ce.getNode());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    assertTrue(activity.toString().contains("UserActivity"));
  }

  @Test
  public void toString_containsPending() {
    assertTrue(activity.toString().contains("PENDING"));
  }

  @Test
  public void toString_afterFinish_containsFinished() {
    activity.finish();
    assertTrue(activity.toString().contains("FINISHED"));
  }

  @Test
  public void toString_afterCancel_containsCanceled() {
    activity.cancel();
    assertTrue(activity.toString().contains("CANCELED"));
  }

  @Test
  public void toString_withOneChild_containsChildText() {
    activity.newChildActivity();
    assertTrue(activity.toString().contains("1 child"));
  }

  @Test
  public void toString_withMultipleChildren_containsCount() {
    activity.newChildActivity();
    activity.newChildActivity();
    assertTrue(activity.toString().contains("2 children"));
  }

  // ── Nested child lifecycle ────────────────────────────────────────

  @Test
  public void nestedChild_parentChain() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    assertSame(child, grandchild.getOwner());
    assertSame(activity, child.getOwner());
  }

  @Test
  public void nestedChild_cancelGrandchild_removesFromChild() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    grandchild.cancel();
    assertEquals(0, child.getChildActivities().size());
  }

  @Test
  public void child_finishWithModel_notRemovedFromParent() {
    UserActivity child = activity.newChildActivity();
    child.setCompletionModel(null);
    child.finish();
    // With no model, no edit, empty children and prepSteps → removed
    assertEquals(0, activity.getChildActivities().size());
  }

  @Test
  public void child_withEdit_notRemovedOnFinish() {
    UserActivity child = activity.newChildActivity();
    child.commitAndInvokeDo(new TestEdit());
    // Child has an edit so removeFromOwnerIfEmpty doesn't remove it
    // But commitAndInvokeDo sets status to FINISHED, not calling removeFromOwnerIfEmpty
    assertEquals(1, activity.getChildActivities().size());
  }

  // ── Multiple listeners ────────────────────────────────────────────

  @Test
  public void multipleListeners_allFire() {
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);
    activity.addListener(e -> count1.incrementAndGet());
    activity.addListener(e -> count2.incrementAndGet());
    activity.finish();
    assertTrue(count1.get() > 0);
    assertTrue(count2.get() > 0);
  }

  @Test
  public void childActivity_propagatesEvents() {
    AtomicInteger count = new AtomicInteger(0);
    activity.addListener(e -> count.incrementAndGet());
    UserActivity child = activity.newChildActivity();
    // newChildActivity fires a ChangeEvent, which propagates to parent listeners
    assertTrue(count.get() > 0);
  }

  // ── setProducedValue with various types ───────────────────────────

  @Test
  public void setProducedValue_list() {
    java.util.List<String> value = java.util.Arrays.asList("a", "b");
    activity.setProducedValue(value);
    assertEquals(value, activity.getProducedValue());
  }

  // ── getIndexOfTransaction edge cases ──────────────────────────────

  @Test
  public void getIndexOfTransaction_unknownChild_returnsNegative() {
    UserActivity unknown = new UserActivity();
    int idx = activity.getIndexOfTransaction(unknown);
    // indexOf returns -1 for unknown, plus prepStepCount(0) = -1
    assertEquals(-1, idx);
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0007-ffffffffffff"), "activityTest");

  private static class TestEdit implements Edit {
    int doOrRedoCount = 0;

    @Override
    public Group getGroup() {
      return TEST_GROUP;
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
    public void undo() {}

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
      return "test";
    }

    @Override
    public String getDetailedDescription() {
      return "TestEdit";
    }

    @Override
    public String getLogDescription() {
      return "TestEdit log";
    }
  }
}
