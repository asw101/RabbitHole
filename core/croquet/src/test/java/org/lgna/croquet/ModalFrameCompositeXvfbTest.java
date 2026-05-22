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
import org.lgna.croquet.views.Frame;
import org.junit.Rule;
import org.junit.rules.Timeout;

import javax.swing.SwingUtilities;
import org.junit.Rule;
import org.junit.rules.Timeout;
import java.awt.event.WindowEvent;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class ModalFrameCompositeXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void performShowsAndClosesModalFrame() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestModalFrameComposite composite = new TestModalFrameComposite();
      UserActivity activity = XvfbCroquetTestSupport.newActivity();

      composite.perform(activity);
      XvfbCroquetTestSupport.flushEdt();

      assertEquals(1, composite.preShowCount);
      assertEquals(1, composite.postHideCount);
      assertEquals(1, composite.finallyCount);
      assertTrue(activity.isSuccessfullyCompleted());
    });
  }

  private static final class TestModalFrameComposite extends ModalFrameComposite<XvfbCroquetTestSupport.TestPanel> {
    private int preShowCount;
    private int postHideCount;
    private int finallyCount;

    private TestModalFrameComposite() {
      super(java.util.UUID.randomUUID(), Application.DOCUMENT_UI_GROUP);
    }

    @Override
    protected XvfbCroquetTestSupport.TestPanel createView() {
      return new XvfbCroquetTestSupport.TestPanel(this, 240, 120);
    }

    @Override
    protected String getName() {
      return "modal-frame";
    }

    @Override
    protected void handlePreShowWindow(Frame parentFrame, Frame frame) {
      this.preShowCount++;
      SwingUtilities.invokeLater(() -> frame.getAwtComponent().dispatchEvent(new WindowEvent(frame.getAwtComponent(), WindowEvent.WINDOW_CLOSING)));
    }

    @Override
    protected void handlePostHideWindow(Frame frame) {
      this.postHideCount++;
    }

    @Override
    protected void handleFinally() {
      this.finallyCount++;
    }
  }
}
