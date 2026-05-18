package org.lgna.croquet.history.event;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link FinishedEvent} — event fired when a
 * {@link org.lgna.croquet.history.UserActivity} finishes successfully.
 */
public class FinishedEventTest {

  @Test
  public void constructor_createsInstance() {
    FinishedEvent event = new FinishedEvent();
    assertNotNull(event);
  }

  @Test
  public void implementsActivityEvent() {
    FinishedEvent event = new FinishedEvent();
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void twoInstances_areDistinct() {
    FinishedEvent e1 = new FinishedEvent();
    FinishedEvent e2 = new FinishedEvent();
    assertNotSame(e1, e2);
  }

  @Test
  public void toString_returnsNonNull() {
    assertNotNull(new FinishedEvent().toString());
  }

  @Test
  public void hashCode_doesNotThrow() {
    new FinishedEvent().hashCode();
  }

  @Test
  public void equals_sameInstance() {
    FinishedEvent event = new FinishedEvent();
    assertEquals(event, event);
  }

  @Test
  public void equals_differentInstance() {
    assertNotEquals(new FinishedEvent(), new FinishedEvent());
  }
}
