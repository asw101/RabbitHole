package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.views.FolderTabbedPane;
import org.lgna.croquet.views.Label;

import javax.swing.JPanel;
import java.awt.Color;

import static org.junit.Assert.*;

public class FolderTabbedPaneXvfbTest {
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void folderTabbedPaneBuildsHeaderAndCardContent() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestTabState state = new TestTabState();
      XvfbCroquetTestSupport.TestTabComposite alpha = new XvfbCroquetTestSupport.TestTabComposite("alpha");
      XvfbCroquetTestSupport.TestTabComposite beta = new XvfbCroquetTestSupport.TestTabComposite("beta");
      state.data.internalAddItem(state.data.getItemCount(), alpha);
      state.data.internalAddItem(state.data.getItemCount(), beta);
      state.setValueTransactionlessly(alpha);

      FolderTabbedPane<XvfbCroquetTestSupport.TestTabComposite> pane = state.createFolderTabbedPane();
      pane.setHeaderLeadingComponent(new Label("lead"));
      pane.setHeaderTrailingComponent(new Label("trail"));
      pane.setBackgroundColor(Color.CYAN);
      pane.setForegroundColor(Color.BLACK);

      JPanel awtComponent = pane.getAwtComponent();
      assertEquals(2, awtComponent.getComponentCount());
      assertSame(alpha, state.getValue());
      assertEquals(Color.CYAN, pane.getAwtComponent().getBackground());
    });
  }

  @Test
  public void folderTabbedPaneTracksSelectionAndCleanup() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestTabState state = new TestTabState();
      XvfbCroquetTestSupport.TestTabComposite alpha = new XvfbCroquetTestSupport.TestTabComposite("alpha");
      XvfbCroquetTestSupport.TestTabComposite beta = new XvfbCroquetTestSupport.TestTabComposite("beta");
      state.data.internalAddItem(state.data.getItemCount(), alpha);
      state.data.internalAddItem(state.data.getItemCount(), beta);
      state.setValueTransactionlessly(alpha);

      FolderTabbedPane<XvfbCroquetTestSupport.TestTabComposite> pane = state.createFolderTabbedPane();
      state.setValueTransactionlessly(beta);
      pane.revalidateAndRepaint();

      assertSame(beta, state.getValue());
      assertNotNull(pane.getAwtComponent());
    });
  }

  private static final class TestTabState extends MutableDataTabState<XvfbCroquetTestSupport.TestTabComposite> {
    private final MutableListData<XvfbCroquetTestSupport.TestTabComposite> data;

    private TestTabState() {
      this(new MutableListData<>(org.lgna.croquet.codecs.SimpleTabCompositeCodec.getInstance(XvfbCroquetTestSupport.TestTabComposite.class)));
    }

    private TestTabState(MutableListData<XvfbCroquetTestSupport.TestTabComposite> data) {
      super(Application.DOCUMENT_UI_GROUP, java.util.UUID.randomUUID(), -1, data);
      this.data = data;
    }
  }
}
