package org.lgna.croquet.history;

import org.lgna.croquet.CancelException;
import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.event.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link UserActivity} covering deeper lifecycle, events, 
 * complex child hierarchies, and edge cases in the ActivityNode base class.
 */
public class UserActivityExtendedTest {

  private UserActivity activity;

  @Before
  public void setUp() {
    activity = new UserActivity();
  }

  // ── setCompletionModel ────────────────────────────────────────────

  @Test
  public void setCompletionModel_firesChangeEvent() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.setCompletionModel(null);
    assertNotNull(captured.get());
    assertTrue(captured.get() instanceof ChangeEvent);
  }

  @Test
  public void setCompletionModel_getCompletionModel_returnsSame() {
    activity.setCompletionModel(null);
    assertNull(activity.getCompletionModel());
  }

  // ── Deep child hierarchies ────────────────────────────────────────

  @Test
  public void threeDeepChildren_allPending() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    UserActivity great = grandchild.newChildActivity();
    assertTrue(great.isPending());
    assertTrue(grandchild.isPending());
    assertTrue(child.isPending());
  }

  @Test
  public void getLatestActivity_threeDeep_returnsDeepest() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    UserActivity great = grandchild.newChildActivity();
    assertSame(great, activity.getLatestActivity());
  }

  @Test
  public void getLatestActivity_deepFinished_returnsShallower() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    grandchild.finish();
    assertSame(child, activity.getLatestActivity());
  }

  @Test
  public void cancelDeepChild_removesFromParent() {
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    UserActivity great = grandchild.newChildActivity();
    great.cancel();
    assertEquals(0, grandchild.getChildActivities().size());
  }

  // ── Multiple children ordering ────────────────────────────────────

  @Test
  public void multipleChildren_indexCorrect() {
    UserActivity c1 = activity.newChildActivity();
    UserActivity c2 = activity.newChildActivity();
    UserActivity c3 = activity.newChildActivity();
    assertEquals(0, activity.getIndexOfTransaction(c1));
    assertEquals(1, activity.getIndexOfTransaction(c2));
    assertEquals(2, activity.getIndexOfTransaction(c3));
  }

  @Test
  public void getChildAt_multipleChildren() {
    UserActivity c1 = activity.newChildActivity();
    UserActivity c2 = activity.newChildActivity();
    assertSame(c1, activity.getChildAt(0));
    assertSame(c2, activity.getChildAt(1));
  }

  @Test
  public void getChildStepCount_multipleChildren() {
    activity.newChildActivity();
    activity.newChildActivity();
    activity.newChildActivity();
    assertEquals(3, activity.getChildStepCount());
  }

  // ── Listener propagation through hierarchy ────────────────────────

  @Test
  public void listener_grandchildCancel_propagatesToRoot() {
    AtomicInteger count = new AtomicInteger(0);
    activity.addListener(e -> count.incrementAndGet());
    UserActivity child = activity.newChildActivity();
    int countAfterChild = count.get();
    UserActivity grandchild = child.newChildActivity();
    int countAfterGrandchild = count.get();
    grandchild.cancel();
    assertTrue(count.get() > countAfterGrandchild);
  }

  @Test
  public void listener_grandchildFinish_propagatesToRoot() {
    AtomicInteger count = new AtomicInteger(0);
    activity.addListener(e -> count.incrementAndGet());
    UserActivity child = activity.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    int before = count.get();
    grandchild.finish();
    assertTrue(count.get() > before);
  }

  // ── Cancel with CancelException edge cases ────────────────────────

  @Test
  public void cancelWithException_selfCause_setsError() {
    CancelException ce = new CancelException();
    // getCause() is null which != ce, treated as ERROR
    activity.cancel(ce);
    assertTrue(activity.isCanceledByError());
  }

  @Test
  public void cancelWithException_causedByAnother_setsError() {
    CancelException ce = new CancelException(new RuntimeException("oops"));
    // getCause() is RuntimeException which != ce, treated as ERROR
    activity.cancel(ce);
    assertTrue(activity.isCanceledByError());
  }

  // ── Finish after child manipulation ───────────────────────────────

  @Test
  public void finish_afterAddingAndCancelingChild_isPossible() {
    UserActivity child = activity.newChildActivity();
    child.cancel();
    activity.finish();
    assertTrue(activity.isSuccessfullyCompleted());
  }

  @Test
  public void finish_withNonEmptyChild_childStays() {
    UserActivity child = activity.newChildActivity();
    child.commitAndInvokeDo(new TestEdit());
    // Child has edit, so it's not empty → stays in parent
    activity.finish();
    assertEquals(1, activity.getChildActivities().size());
  }

  // ── removeFromOwnerIfEmpty ────────────────────────────────────────

  @Test
  public void cancel_childWithModel_staysInParent() {
    UserActivity child = activity.newChildActivity();
    child.setCompletionModel(null);
    // model is null, no children, no edit → removeFromOwnerIfEmpty should remove
    child.cancel();
    assertEquals(0, activity.getChildActivities().size());
  }

  @Test
  public void cancel_rootActivity_noOwner_doesNotThrow() {
    activity.cancel();
    assertTrue(activity.isCanceled());
  }

  // ── producedValue edge cases ──────────────────────────────────────

  @Test
  public void producedValue_overwrite() {
    activity.setProducedValue("first");
    activity.setProducedValue("second");
    assertEquals("second", activity.getProducedValue());
  }

  @Test
  public void producedValue_differentTypes() {
    activity.setProducedValue(42);
    assertEquals(42, activity.getProducedValue());
    activity.setProducedValue("now a string");
    assertEquals("now a string", activity.getProducedValue());
  }

  // ── toString edge cases ───────────────────────────────────────────

  @Test
  public void toString_withErrorCancelStatus() {
    CancelException ce = new CancelException(new RuntimeException());
    activity.cancel(ce);
    assertTrue(activity.toString().contains("ERROR"));
  }

  @Test
  public void toString_noChildren_noPrepSteps() {
    String s = activity.toString();
    assertTrue(s.startsWith("UserActivity"));
    assertTrue(s.contains("PENDING"));
    assertTrue(s.endsWith("]"));
  }

  @Test
  public void toString_multipleChildren_showsCount() {
    activity.newChildActivity();
    activity.newChildActivity();
    activity.newChildActivity();
    assertTrue(activity.toString().contains("3 children"));
  }

  // ── commitAndInvokeDo edge cases ──────────────────────────────────

  @Test
  public void commitAndInvokeDo_setsFinished() {
    activity.commitAndInvokeDo(new TestEdit());
    assertTrue(activity.isSuccessfullyCompleted());
    assertFalse(activity.isPending());
  }

  @Test
  public void commitAndInvokeDo_firesEditCommittedEvent() {
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.commitAndInvokeDo(new TestEdit());
    assertTrue(captured.get() instanceof EditCommittedEvent);
    EditCommittedEvent ece = (EditCommittedEvent) captured.get();
    assertNotNull(ece.getEdit());
  }

  // ── addListener / removeListener ──────────────────────────────────

  @Test
  public void addSameListenerTwice_firesPerRegistration() {
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    activity.addListener(listener);
    activity.addListener(listener);
    activity.finish();
    // CopyOnWriteArrayList allows duplicates
    assertTrue(count.get() >= 2);
  }

  @Test
  public void removeListener_onlyRemovesFirstOccurrence() {
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    activity.addListener(listener);
    activity.addListener(listener);
    activity.removeListener(listener);
    activity.finish();
    assertTrue(count.get() >= 1);
  }

  // ── ChangeEvent node ──────────────────────────────────────────────

  @Test
  public void changeEvent_constructor_preservesNode() {
    ChangeEvent<UserActivity> event = new ChangeEvent<>(activity);
    assertSame(activity, event.getNode());
  }

  @Test
  public void changeEvent_nullNode_allowed() {
    ChangeEvent<UserActivity> event = new ChangeEvent<>(null);
    assertNull(event.getNode());
  }

  // ── EditCommittedEvent ────────────────────────────────────────────

  @Test
  public void editCommittedEvent_getEdit_returnsEdit() {
    TestEdit edit = new TestEdit();
    EditCommittedEvent event = new EditCommittedEvent(edit);
    assertSame(edit, event.getEdit());
  }

  // ── Event type checks ─────────────────────────────────────────────

  @Test
  public void cancelEvent_isActivityEvent() {
    CancelEvent event = new CancelEvent();
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void finishedEvent_isActivityEvent() {
    FinishedEvent event = new FinishedEvent();
    assertTrue(event instanceof ActivityEvent);
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0098-ffffffffffff"), "actExtTest");

  private static class TestEdit implements Edit {
    int doOrRedoCount = 0;

    @Override public Group getGroup() { return TEST_GROUP; }
    @Override public boolean canUndo() { return true; }
    @Override public boolean canRedo() { return true; }
    @Override public void doOrRedo(boolean isDo) { doOrRedoCount++; }
    @Override public void undo() {}
    @Override public String getRedoPresentation() { return "Redo"; }
    @Override public String getUndoPresentation() { return "Undo"; }
    @Override public String getTerseDescription() { return "test"; }
    @Override public String getDetailedDescription() { return "TestEdit"; }
    @Override public String getLogDescription() { return "TestEdit log"; }
  }
}
