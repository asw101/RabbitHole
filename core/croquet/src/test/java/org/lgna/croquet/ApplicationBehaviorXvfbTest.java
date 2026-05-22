package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.history.UserActivity;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.awt.event.WindowEvent;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.io.File;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.util.Collections;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.util.Locale;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class ApplicationBehaviorXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void initializeLocaleAndActivityLifecycleWork() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      XvfbCroquetTestSupport.TestApplication application = XvfbCroquetTestSupport.installApplication();

      application.initialize(new String[0]);
      assertSame(application, Application.getActiveInstance());
      assertNull(application.getOpenActivity());

      UserActivity childActivity = application.acquireOpenActivity();
      assertSame(childActivity, application.getOpenActivity());

      childActivity.finish();
      assertNull(application.getOpenActivity());

      application.setLocale(Locale.FRANCE);
      assertEquals(Locale.FRANCE, Application.getLocale());
      application.setLocale(null);

      UserActivity quitActivity = new UserActivity();
      application.handleQuit(quitActivity);
      assertTrue(quitActivity.isSuccessfullyCompleted());

      application.handleOpenFiles(Collections.singletonList(new File("sample.a3p")));
      application.handleWindowOpened(new WindowEvent(application.getDocumentFrame().getFrame().getAwtComponent(), WindowEvent.WINDOW_OPENED));
      assertEquals("target/test-xvfb-croquet/app", application.getApplicationSubPath());
    });
  }
}
