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
import org.lgna.croquet.views.CompositeView;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.lgna.croquet.views.Dialog;
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

public class AbstractDialogCompositeXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void modalDialogLifecycleReleasesViewAndFormatsTitle() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestDialogComposite composite = new TestDialogComposite();
      UserActivity activity = XvfbCroquetTestSupport.newActivity();

      composite.open(activity);
      XvfbCroquetTestSupport.flushEdt();

      assertSame(activity, composite.getOpeningActivity());
      assertEquals(1, composite.preShowCount);
      assertEquals(1, composite.postHideCount);
      assertEquals(1, composite.finallyCount);
      assertNotNull(composite.releasedView);
      assertEquals("Dialog", composite.getDialogTitle());
      assertTrue(composite.isDefaultButtonDesired());
    });
  }

  private static final class TestDialogComposite extends AbstractDialogComposite<XvfbCroquetTestSupport.TestPanel> {
    private int preShowCount;
    private int postHideCount;
    private int finallyCount;
    private CompositeView<?, ?> releasedView;

    private TestDialogComposite() {
      super(java.util.UUID.randomUUID(), IsModal.TRUE);
    }

    private void open(UserActivity activity) {
      this.showDialog(activity);
    }

    @Override
    protected XvfbCroquetTestSupport.TestPanel createView() {
      return new XvfbCroquetTestSupport.TestPanel(this, 220, 120);
    }

    @Override
    protected XvfbCroquetTestSupport.TestPanel allocateView() {
      return this.createView();
    }

    @Override
    protected void releaseView(CompositeView<?, ?> view) {
      this.releasedView = view;
    }

    @Override
    protected String getDefaultTitleText() {
      return "<html>Dialog...</html>";
    }

    @Override
    protected void handlePreShowDialog(Dialog dialog) {
      this.preShowCount++;
      SwingUtilities.invokeLater(() -> dialog.getAwtComponent().dispatchEvent(new WindowEvent(dialog.getAwtComponent(), WindowEvent.WINDOW_CLOSING)));
    }

    @Override
    protected void handlePostHideDialog() {
      this.postHideCount++;
    }

    @Override
    protected void handleFinally(Dialog dialog) {
      this.finallyCount++;
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestDialogComposite.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}
