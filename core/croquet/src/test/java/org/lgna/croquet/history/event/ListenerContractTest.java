package org.lgna.croquet.history.event;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Listener} interface contract — verifies that
 * implementations receive the correct event types through UserActivity lifecycle.
 */
public class ListenerContractTest {

  @Test
  public void listener_isFunctionalInterface() {
    // Listener has single method changed(ActivityEvent) — can be a lambda
    Listener listener = e -> {};
    assertNotNull(listener);
  }

  @Test
  public void listener_receivesCancelEvent() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.cancel();
    assertNotNull(captured.get());
    assertTrue(captured.get() instanceof CancelEvent);
  }

  @Test
  public void listener_receivesFinishedEvent() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.finish();
    assertNotNull(captured.get());
    assertTrue(captured.get() instanceof FinishedEvent);
  }

  @Test
  public void listener_receivesChangeEvent_onNewChild() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    activity.newChildActivity();
    assertNotNull(captured.get());
    assertTrue(captured.get() instanceof ChangeEvent);
  }

  @Test
  public void listener_changeEventNode_isChild() {
    UserActivity activity = new UserActivity();
    AtomicReference<ActivityEvent> captured = new AtomicReference<>();
    activity.addListener(captured::set);
    UserActivity child = activity.newChildActivity();
    ChangeEvent<?> ce = (ChangeEvent<?>) captured.get();
    assertSame(child, ce.getNode());
  }

  @Test
  public void listener_multipleEvents_fireInOrder() {
    UserActivity activity = new UserActivity();
    java.util.List<Class<?>> eventTypes = new java.util.ArrayList<>();
    activity.addListener(e -> eventTypes.add(e.getClass()));

    activity.newChildActivity();
    activity.finish();

    assertEquals(2, eventTypes.size());
    assertEquals(ChangeEvent.class, eventTypes.get(0));
    assertEquals(FinishedEvent.class, eventTypes.get(1));
  }

  @Test
  public void listener_removedListenerDoesNotFire() {
    UserActivity activity = new UserActivity();
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    activity.addListener(listener);
    activity.removeListener(listener);
    activity.finish();
    assertEquals(0, count.get());
  }

  @Test
  public void listener_multipleListeners_allFire() {
    UserActivity activity = new UserActivity();
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);
    activity.addListener(e -> count1.incrementAndGet());
    activity.addListener(e -> count2.incrementAndGet());
    activity.cancel();
    assertTrue(count1.get() > 0);
    assertTrue(count2.get() > 0);
  }

  @Test
  public void listener_isListening_true() {
    UserActivity activity = new UserActivity();
    Listener listener = e -> {};
    activity.addListener(listener);
    assertTrue(activity.isListening(listener));
  }

  @Test
  public void listener_isListening_false_afterRemove() {
    UserActivity activity = new UserActivity();
    Listener listener = e -> {};
    activity.addListener(listener);
    activity.removeListener(listener);
    assertFalse(activity.isListening(listener));
  }

  @Test
  public void listener_isListening_false_neverAdded() {
    UserActivity activity = new UserActivity();
    Listener listener = e -> {};
    assertFalse(activity.isListening(listener));
  }
}
