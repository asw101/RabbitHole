package org.lgna.croquet;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link org.lgna.croquet.history.UserActivity} — deeper
 * coverage of nested lifecycle, event propagation, and edge cases.
 */
public class UserActivityExpandedAdditionalTest {

  @Test
  public void deeplyNested_findLatest() {
    UserActivity root = new UserActivity();
    UserActivity child = root.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    UserActivity greatGrandchild = grandchild.newChildActivity();
    assertSame(greatGrandchild, root.getLatestActivity());
  }

  @Test
  public void finishGrandchild_latestReturnsChild() {
    UserActivity root = new UserActivity();
    UserActivity child = root.newChildActivity();
    UserActivity grandchild = child.newChildActivity();
    grandchild.finish();
    assertSame(child, root.getLatestActivity());
  }

  @Test
  public void cancelAllChildren_latestReturnsSelf() {
    UserActivity root = new UserActivity();
    UserActivity c1 = root.newChildActivity();
    UserActivity c2 = root.newChildActivity();
    c1.cancel();
    c2.cancel();
    assertSame(root, root.getLatestActivity());
  }

  @Test
  public void multipleChildActivities_indexing() {
    UserActivity root = new UserActivity();
    UserActivity c1 = root.newChildActivity();
    UserActivity c2 = root.newChildActivity();
    UserActivity c3 = root.newChildActivity();
    assertEquals(0, root.getIndexOfTransaction(c1));
    assertEquals(1, root.getIndexOfTransaction(c2));
    assertEquals(2, root.getIndexOfTransaction(c3));
  }

  @Test
  public void getChildAt_correctChild() {
    UserActivity root = new UserActivity();
    UserActivity c1 = root.newChildActivity();
    UserActivity c2 = root.newChildActivity();
    assertSame(c1, root.getChildAt(0));
    assertSame(c2, root.getChildAt(1));
  }

  @Test
  public void producedValue_roundTrip() {
    UserActivity activity = new UserActivity();
    assertNull(activity.getProducedValue());
    activity.setProducedValue("result");
    assertEquals("result", activity.getProducedValue());
    activity.setProducedValue(null);
    assertNull(activity.getProducedValue());
  }

  @Test
  public void producedValue_complexType() {
    UserActivity activity = new UserActivity();
    java.util.Map<String, Integer> map = new java.util.HashMap<>();
    map.put("key", 42);
    activity.setProducedValue(map);
    assertEquals(map, activity.getProducedValue());
  }

  @Test
  public void toString_pendingActivity() {
    UserActivity activity = new UserActivity();
    String str = activity.toString();
    assertTrue(str.contains("UserActivity"));
    assertTrue(str.contains("PENDING"));
  }

  @Test
  public void toString_afterMultipleChildren() {
    UserActivity activity = new UserActivity();
    activity.newChildActivity();
    activity.newChildActivity();
    activity.newChildActivity();
    String str = activity.toString();
    assertTrue(str.contains("3 children"));
  }

  @Test
  public void getActivityWithoutTrigger_noTrigger_returnsSelf() {
    UserActivity activity = new UserActivity();
    assertSame(activity, activity.getActivityWithoutTrigger());
  }

  @Test
  public void getActivityWithoutModel_noModel_returnsSelf() {
    UserActivity activity = new UserActivity();
    assertSame(activity, activity.getActivityWithoutModel());
  }

  @Test
  public void findFirstMenuSelectStep_empty_returnsNull() {
    UserActivity activity = new UserActivity();
    assertNull(activity.findFirstMenuSelectStep());
  }

  @Test
  public void findDropSite_noTrigger_returnsNull() {
    UserActivity activity = new UserActivity();
    assertNull(activity.findDropSite());
  }

  @Test
  public void cancelWithCancelException_null_setsCanceled() {
    UserActivity activity = new UserActivity();
    activity.cancel((CancelException) null);
    assertTrue(activity.isCanceled());
    assertFalse(activity.isCanceledByError());
  }

  @Test
  public void multipleListeners_receiveAllEventTypes() {
    UserActivity activity = new UserActivity();
    java.util.List<String> eventTypes = new java.util.ArrayList<>();
    activity.addListener(e -> eventTypes.add(e.getClass().getSimpleName()));

    activity.newChildActivity();
    activity.setProducedValue("val");
    activity.finish();

    assertTrue(eventTypes.contains("ChangeEvent"));
    assertTrue(eventTypes.contains("FinishedEvent"));
  }
}
