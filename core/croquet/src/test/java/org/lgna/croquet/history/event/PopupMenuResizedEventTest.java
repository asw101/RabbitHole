package org.lgna.croquet.history.event;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link PopupMenuResizedEvent} — event fired when a popup menu is resized.
 */
public class PopupMenuResizedEventTest {

  @Test
  public void constructor_createsInstance() {
    PopupMenuResizedEvent event = new PopupMenuResizedEvent();
    assertNotNull(event);
  }

  @Test
  public void implementsActivityEvent() {
    PopupMenuResizedEvent event = new PopupMenuResizedEvent();
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void twoInstances_areDistinct() {
    PopupMenuResizedEvent e1 = new PopupMenuResizedEvent();
    PopupMenuResizedEvent e2 = new PopupMenuResizedEvent();
    assertNotSame(e1, e2);
  }

  @Test
  public void toString_returnsNonNull() {
    assertNotNull(new PopupMenuResizedEvent().toString());
  }

  @Test
  public void hashCode_doesNotThrow() {
    new PopupMenuResizedEvent().hashCode();
  }

  @Test
  public void equals_sameInstance() {
    PopupMenuResizedEvent event = new PopupMenuResizedEvent();
    assertEquals(event, event);
  }
}
