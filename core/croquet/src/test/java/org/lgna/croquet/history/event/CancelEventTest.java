package org.lgna.croquet.history.event;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link CancelEvent} — event fired when a {@link org.lgna.croquet.history.UserActivity}
 * is canceled. Covers construction, interface contract, and identity.
 */
public class CancelEventTest {

  @Test
  public void constructor_createsInstance() {
    CancelEvent event = new CancelEvent();
    assertNotNull(event);
  }

  @Test
  public void implementsActivityEvent() {
    CancelEvent event = new CancelEvent();
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void twoInstances_areDistinct() {
    CancelEvent e1 = new CancelEvent();
    CancelEvent e2 = new CancelEvent();
    assertNotSame(e1, e2);
  }

  @Test
  public void toString_returnsNonNull() {
    CancelEvent event = new CancelEvent();
    assertNotNull(event.toString());
  }

  @Test
  public void hashCode_doesNotThrow() {
    CancelEvent event = new CancelEvent();
    event.hashCode(); // should not throw
  }

  @Test
  public void equals_sameInstance_true() {
    CancelEvent event = new CancelEvent();
    assertEquals(event, event);
  }

  @Test
  public void equals_differentInstance_defaultBehavior() {
    CancelEvent e1 = new CancelEvent();
    CancelEvent e2 = new CancelEvent();
    // Default Object.equals — different instances are not equal
    assertNotEquals(e1, e2);
  }
}
