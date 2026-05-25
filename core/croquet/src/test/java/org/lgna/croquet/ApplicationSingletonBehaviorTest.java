package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JComponent;

import static org.junit.Assert.*;

public class ApplicationSingletonBehaviorTest {
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

  @Test
  public void initialize_and_locale_accessors_are_safe_in_headless_tests() {
    Application<?> application = CroquetTestUtils.ensureTestApplication();

    application.initialize(new String[0]);
    application.setLocale(null);

    assertNotNull(Application.getLocale());
    assertSame(JComponent.getDefaultLocale(), Application.getLocale());
  }
}
