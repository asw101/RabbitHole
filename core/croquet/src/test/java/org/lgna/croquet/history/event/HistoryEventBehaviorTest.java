package org.lgna.croquet.history.event;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

public class HistoryEventBehaviorTest {
  @Test
  public void changeEvent_retainsTheProvidedNode() {
    UserActivity activity = new UserActivity();

    ChangeEvent<UserActivity> event = new ChangeEvent<>(activity);

    assertSame(activity, event.getNode());
    assertTrue(event instanceof ActivityEvent);
  }

  @Test
  public void popupMenuResizedEvent_isAnActivityEventMarker() {
    PopupMenuResizedEvent event = new PopupMenuResizedEvent();

    assertNotNull(event);
    assertTrue(event instanceof ActivityEvent);
  }
}
