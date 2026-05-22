package org.lgna.croquet;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import org.lgna.croquet.history.UserActivity;

import static org.junit.Assert.*;

public class ApplicationSingletonBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void ensureTestApplication_setsActiveSingletonAndSubPath() {
    Application<?> application = CroquetTestUtils.ensureTestApplication();

    assertSame(application, Application.getActiveInstance());
    assertEquals("test", application.getApplicationSubPath());
    assertNotNull(application.getOverallUserActivity());
  }

  @Test
  public void acquireOpenActivity_tracksCurrentOpenActivityUntilFinished() {
    Application<?> application = CroquetTestUtils.ensureTestApplication();

    UserActivity activity = application.acquireOpenActivity();
    try {
      assertSame(activity, application.getOpenActivity());
      assertNotNull(activity.getOwner());
    } finally {
      activity.finish();
    }

    assertNull(application.getOpenActivity());
  }
}
