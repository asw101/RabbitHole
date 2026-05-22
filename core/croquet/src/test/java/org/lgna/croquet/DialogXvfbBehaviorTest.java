package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.Dialog;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.Frame;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.awt.Insets;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.awt.Rectangle;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.awt.event.WindowEvent;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.awt.event.WindowListener;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class DialogXvfbBehaviorTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void visibleDialog_pushesAndPopsDocumentFrameWindowStack() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      XvfbCroquetTestSupport.TestApplication application = XvfbCroquetTestSupport.installApplication();
      Frame owner = application.getDocumentFrame().getFrame();
      Dialog dialog = new Dialog(owner, false);
      try {
        dialog.setTitle("croquet-dialog");
        dialog.setSize(180, 90);
        dialog.setLocation(150, 170);
        dialog.setVisible(true);

        assertSame(dialog, application.getDocumentFrame().peekWindow());
        assertEquals("croquet-dialog", dialog.getTitle());
        assertEquals(Dialog.DefaultCloseOperation.HIDE, dialog.getDefaultCloseOperation());

        dialog.setVisible(false);
        assertSame(owner, application.getDocumentFrame().peekWindow());
      } finally {
        dialog.dispose();
      }
    });
  }

  @Test
  public void windowClosingHonorsHideBehaviorAndTrackableShapeUsesLiveGeometry() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      XvfbCroquetTestSupport.TestApplication application = XvfbCroquetTestSupport.installApplication();
      Frame owner = application.getDocumentFrame().getFrame();
      Dialog dialog = new Dialog(owner, false);
      try {
        dialog.setSize(220, 120);
        dialog.setLocation(owner.getX() + 30, owner.getY() + 40);
        dialog.setVisible(true);
        dialog.getRootPane().getAwtComponent().setSize(220, 24);

        dialog.getAwtComponent().dispatchEvent(new WindowEvent(dialog.getAwtComponent(), WindowEvent.WINDOW_CLOSING));
        assertFalse(dialog.isVisible());

        Rectangle closeButtonBounds = dialog.getCloseButtonTrackableShape().getShape(owner, new Insets(0, 0, 0, 0)).getBounds();
        assertTrue(closeButtonBounds.width > 0);
        assertTrue(closeButtonBounds.height > 0);
      } finally {
        dialog.dispose();
      }
    });
  }

  @Test
  public void listenerAndDefaultCloseOperationRoundTrip() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      Dialog dialog = new Dialog();
      AtomicInteger closeCount = new AtomicInteger();
      WindowListener listener = new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          closeCount.incrementAndGet();
        }
      };
      try {
        dialog.addWindowListener(listener);
        dialog.setDefaultCloseOperation(Dialog.DefaultCloseOperation.DO_NOTHING);
        assertEquals(Dialog.DefaultCloseOperation.DO_NOTHING, dialog.getDefaultCloseOperation());

        dialog.getAwtComponent().dispatchEvent(new WindowEvent(dialog.getAwtComponent(), WindowEvent.WINDOW_CLOSING));
        assertEquals(1, closeCount.get());

        dialog.removeWindowListener(listener);
        dialog.getAwtComponent().dispatchEvent(new WindowEvent(dialog.getAwtComponent(), WindowEvent.WINDOW_CLOSING));
        assertEquals(1, closeCount.get());
      } finally {
        dialog.dispose();
      }
    });
  }
}
