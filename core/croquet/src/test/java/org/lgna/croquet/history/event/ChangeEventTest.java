package org.lgna.croquet.history.event;

import org.lgna.croquet.history.UserActivity;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ChangeEvent} — typed event holding the {@link org.lgna.croquet.history.ActivityNode}
 * that triggered the change. Covers construction, getter, null handling, and generics.
 */
public class ChangeEventTest {

  @Test
  public void constructor_storesNode() {
    UserActivity node = new UserActivity();
    ChangeEvent<UserActivity> event = new ChangeEvent<>(node);
    assertSame(node, event.getNode());
  }

  @Test
  public void constructor_nullNode() {
    ChangeEvent<UserActivity> event = new ChangeEvent<>(null);
    assertNull(event.getNode());
  }

  @Test
  public void implementsActivityEvent() {
    ChangeEvent<UserActivity> event = new ChangeEvent<>(new UserActivity());
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void getNode_returnsExactReference() {
    UserActivity node = new UserActivity();
    ChangeEvent<UserActivity> event = new ChangeEvent<>(node);
    assertSame(node, event.getNode());
  }

  @Test
  public void twoEvents_differentNodes_areDistinct() {
    UserActivity n1 = new UserActivity();
    UserActivity n2 = new UserActivity();
    ChangeEvent<UserActivity> e1 = new ChangeEvent<>(n1);
    ChangeEvent<UserActivity> e2 = new ChangeEvent<>(n2);
    assertNotSame(e1.getNode(), e2.getNode());
  }

  @Test
  public void toString_returnsNonNull() {
    ChangeEvent<UserActivity> event = new ChangeEvent<>(new UserActivity());
    assertNotNull(event.toString());
  }

  @Test
  public void getNode_childActivity() {
    UserActivity parent = new UserActivity();
    UserActivity child = parent.newChildActivity();
    ChangeEvent<UserActivity> event = new ChangeEvent<>(child);
    assertSame(child, event.getNode());
    assertSame(parent, event.getNode().getOwner());
  }
}
