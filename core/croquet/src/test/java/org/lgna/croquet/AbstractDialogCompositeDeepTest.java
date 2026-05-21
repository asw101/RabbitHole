package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.views.Panel;

import javax.swing.JPanel;
import java.awt.FlowLayout;

import static org.junit.Assert.*;

public class AbstractDialogCompositeDeepTest {
  @Test
  public void getDialogTitle_stripsMarkupAndEllipsisFromDefaultTitle() {
    TestDialogComposite composite = new TestDialogComposite("<html><b>Visible Title...</b></html>");

    assertEquals("Visible Title", composite.getDialogTitle());
  }

  @Test
  public void getDialogTitle_returnsNullWhenNoDefaultTitleExists() {
    TestDialogComposite composite = new TestDialogComposite(null);

    assertNull(composite.getDialogTitle());
  }

  @Test
  public void defaultButtonIsDesiredByDefault() {
    TestDialogComposite composite = new TestDialogComposite("plain");

    assertTrue(composite.isDefaultButtonDesired());
  }

  private static final class TestDialogComposite extends AbstractDialogComposite<Panel> {
    private final String defaultTitle;

    private TestDialogComposite(String defaultTitle) {
      super(CroquetTestUtils.nextTestUUID(), IsModal.FALSE);
      this.defaultTitle = defaultTitle;
    }

    @Override
    protected String getDefaultTitleText() {
      return this.defaultTitle;
    }

    @Override
    protected Panel createView() {
      return new Panel(this) {
        @Override
        protected java.awt.LayoutManager createLayoutManager(JPanel jPanel) {
          return new FlowLayout();
        }
      };
    }

    @Override
    protected Panel allocateView() {
      return this.createView();
    }

    @Override
    protected void releaseView(org.lgna.croquet.views.CompositeView<?, ?> view) {
    }

    @Override
    protected void handlePreShowDialog(org.lgna.croquet.views.Dialog dialog) {
    }

    @Override
    protected void handlePostHideDialog() {
    }
  }
}
