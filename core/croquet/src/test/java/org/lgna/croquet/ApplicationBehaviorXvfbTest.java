package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;

public class ApplicationBehaviorXvfbTest {
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
