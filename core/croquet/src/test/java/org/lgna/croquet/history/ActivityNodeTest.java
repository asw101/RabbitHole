package org.lgna.croquet.history;

import org.lgna.croquet.history.event.ActivityEvent;
import org.lgna.croquet.history.event.Listener;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link ActivityNode} — abstract base for UserActivity and PrepStep.
 * Tested through {@link UserActivity} which is a concrete subclass.
 * Focuses on listener management, owner chain, and trigger binding.
 */
public class ActivityNodeTest {

  // ── Listener management ──────────────────────────────────────────

  @Test
  public void addListener_isListening_true() {
    UserActivity node = new UserActivity();
    Listener listener = e -> {};
    node.addListener(listener);
    assertTrue(node.isListening(listener));
  }

  @Test
  public void removeListener_isListening_false() {
    UserActivity node = new UserActivity();
    Listener listener = e -> {};
    node.addListener(listener);
    node.removeListener(listener);
    assertFalse(node.isListening(listener));
  }

  @Test
  public void isListening_neverAdded_false() {
    UserActivity node = new UserActivity();
    assertFalse(node.isListening(e -> {}));
  }

  @Test
  public void addMultipleListeners_allListening() {
    UserActivity node = new UserActivity();
    Listener l1 = e -> {};
    Listener l2 = e -> {};
    Listener l3 = e -> {};
    node.addListener(l1);
    node.addListener(l2);
    node.addListener(l3);
    assertTrue(node.isListening(l1));
    assertTrue(node.isListening(l2));
    assertTrue(node.isListening(l3));
  }

  @Test
  public void removeOneOfMultiple_othersStillListening() {
    UserActivity node = new UserActivity();
    Listener l1 = e -> {};
    Listener l2 = e -> {};
    node.addListener(l1);
    node.addListener(l2);
    node.removeListener(l1);
    assertFalse(node.isListening(l1));
    assertTrue(node.isListening(l2));
  }

  // ── Listener dispatch ────────────────────────────────────────────

  @Test
  public void listeners_fireOnFinish() {
    UserActivity node = new UserActivity();
    AtomicInteger count = new AtomicInteger(0);
    node.addListener(e -> count.incrementAndGet());
    node.finish();
    assertTrue(count.get() > 0);
  }

  @Test
  public void listeners_fireOnCancel() {
    UserActivity node = new UserActivity();
    AtomicInteger count = new AtomicInteger(0);
    node.addListener(e -> count.incrementAndGet());
    node.cancel();
    assertTrue(count.get() > 0);
  }

  @Test
  public void listeners_fireOnNewChild() {
    UserActivity node = new UserActivity();
    AtomicInteger count = new AtomicInteger(0);
    node.addListener(e -> count.incrementAndGet());
    node.newChildActivity();
    assertTrue(count.get() > 0);
  }

  @Test
  public void removedListener_doesNotFire() {
    UserActivity node = new UserActivity();
    AtomicInteger count = new AtomicInteger(0);
    Listener listener = e -> count.incrementAndGet();
    node.addListener(listener);
    node.removeListener(listener);
    node.finish();
    assertEquals(0, count.get());
  }

  // ── Owner/model/trigger ──────────────────────────────────────────

  @Test
  public void getModel_initiallyNull() {
    UserActivity node = new UserActivity();
    assertNull(node.getModel());
  }

  @Test
  public void getOwner_rootActivity_null() {
    UserActivity node = new UserActivity();
    assertNull(node.getOwner());
  }

  @Test
  public void getOwner_childActivity_isParent() {
    UserActivity parent = new UserActivity();
    UserActivity child = parent.newChildActivity();
    assertSame(parent, child.getOwner());
  }

  @Test
  public void getTrigger_initiallyNull() {
    UserActivity node = new UserActivity();
    assertNull(node.getTrigger());
  }

  // ── Event propagation to owner ───────────────────────────────────

  @Test
  public void childEvent_propagatesToParentListeners() {
    UserActivity parent = new UserActivity();
    AtomicInteger parentCount = new AtomicInteger(0);
    parent.addListener(e -> parentCount.incrementAndGet());

    UserActivity child = parent.newChildActivity();
    int countAfterChild = parentCount.get();
    assertTrue(countAfterChild > 0); // ChangeEvent from newChildActivity

    child.finish();
    assertTrue(parentCount.get() > countAfterChild);
  }
}
