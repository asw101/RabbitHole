package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.MutableDataTabState;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.codecs.SimpleTabCompositeCodec;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JViewport;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FolderTabbedPaneHeadlessBehaviorTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void displayableFolderTabbedPane_builds_titles_headers_and_selected_card() {
    XvfbCroquetTestSupport.onEdt(() -> {
      XvfbCroquetTestSupport.TestTabComposite alpha = new XvfbCroquetTestSupport.TestTabComposite("alpha");
      XvfbCroquetTestSupport.TestTabComposite beta = new XvfbCroquetTestSupport.TestTabComposite("beta");
      TestTabState state = new TestTabState(alpha, null, beta);
      state.setValueTransactionlessly(alpha);

      ExposedFolderTabbedPane pane = new ExposedFolderTabbedPane(state);
      pane.setHeaderLeadingComponent(new Label("lead"));
      pane.setHeaderTrailingComponent(new Label("trail"));
      pane.setBackgroundColor(Color.CYAN);
      pane.setForegroundColor(Color.BLACK);

      JPanel awtComponent = pane.getAwtComponent();
      pane.simulateDisplayable();

      FolderTitlesPanel titlesPanel = (FolderTitlesPanel) getField(pane, "titlesPanel");
      BorderPanel innerHeader = (BorderPanel) getField(pane, "innerHeaderPanel");
      BorderPanel outerHeader = (BorderPanel) getField(pane, "outerHeaderPanel");

      assertEquals(2, awtComponent.getComponentCount());
      assertSame(alpha, pane.getCardOwner().getShowingCard());
      assertEquals(3, titlesPanel.getAwtComponent().getComponentCount());
      assertEquals(Color.CYAN, titlesPanel.getAwtComponent().getBackground());
      assertEquals(Color.BLACK, titlesPanel.getAwtComponent().getForeground());
      assertNotNull(innerHeader.getLineStartComponent());
      assertNotNull(innerHeader.getLineEndComponent());
      assertNotNull(outerHeader.getLineEndComponent());

      pane.simulateUndisplayable();
      return null;
    });
  }

  @Test
  public void selection_changes_scroll_drag_and_header_cleanup_work_headlessly() {
    XvfbCroquetTestSupport.onEdt(() -> {
      List<XvfbCroquetTestSupport.TestTabComposite> tabs = new ArrayList<>();
      for (int i = 0; i < 8; i++) {
        tabs.add(new XvfbCroquetTestSupport.TestTabComposite("tab-" + i));
      }
      TestTabState state = new TestTabState(tabs.toArray(new XvfbCroquetTestSupport.TestTabComposite[0]));
      state.setValueTransactionlessly(tabs.get(0));

      ExposedFolderTabbedPane pane = new ExposedFolderTabbedPane(state);
      pane.setHeaderLeadingComponent(new Label("lead"));
      pane.setHeaderTrailingComponent(new Label("trail"));
      pane.getAwtComponent();
      pane.simulateDisplayable();

      state.setValueTransactionlessly(tabs.get(7));
      assertSame(tabs.get(7), pane.getCardOwner().getShowingCard());

      FolderTitlesPanel titlesPanel = (FolderTitlesPanel) getField(pane, "titlesPanel");
      ScrollPane titlesScrollPane = (ScrollPane) getField(pane, "titlesScrollPane");
      BorderPanel innerHeader = (BorderPanel) getField(pane, "innerHeaderPanel");
      BorderPanel outerHeader = (BorderPanel) getField(pane, "outerHeaderPanel");

      JViewport viewport = titlesScrollPane.getAwtComponent().getViewport();
      titlesPanel.getAwtComponent().setPreferredSize(new Dimension(960, 36));
      titlesPanel.getAwtComponent().setSize(960, 36);
      titlesScrollPane.getAwtComponent().setSize(140, 36);
      viewport.setView(titlesPanel.getAwtComponent());
      viewport.setViewPosition(new java.awt.Point(0, 0));
      viewport.setSize(140, 36);

      MouseListener mouseListener = titlesScrollPane.getAwtComponent().getMouseListeners()[0];
      MouseMotionListener mouseMotionListener = titlesScrollPane.getAwtComponent().getMouseMotionListeners()[0];
      mouseListener.mousePressed(new MouseEvent(titlesScrollPane.getAwtComponent(), MouseEvent.MOUSE_PRESSED, 1L, 0, 120, 8, 1, false));
      mouseMotionListener.mouseDragged(new MouseEvent(titlesScrollPane.getAwtComponent(), MouseEvent.MOUSE_DRAGGED, 2L, 0, 12, 8, 0, false));
      mouseListener.mouseReleased(new MouseEvent(titlesScrollPane.getAwtComponent(), MouseEvent.MOUSE_RELEASED, 3L, 0, 12, 8, 1, false));
      assertTrue(viewport.getViewPosition().x > 0);

      paintCardBorder(pane);

      pane.setHeaderLeadingComponent(null);
      pane.setHeaderTrailingComponent(null);
      assertNull(innerHeader.getLineStartComponent());
      assertNull(outerHeader.getLineEndComponent());

      pane.simulateUndisplayable();
      return null;
    });
  }

  private static void paintCardBorder(ExposedFolderTabbedPane pane) {
    JPanel cardPanel = pane.getCardOwner().getView().getAwtComponent();
    cardPanel.setSize(220, 120);
    BufferedImage image = new BufferedImage(220, 120, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D graphics = image.createGraphics();
    try {
      cardPanel.getBorder().paintBorder(cardPanel, graphics, 0, 0, image.getWidth(), image.getHeight());
    } finally {
      graphics.dispose();
    }
    assertNotNull(image);
  }

  private static Object getField(Object target, String fieldName) {
    try {
      Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(target);
    } catch (ReflectiveOperationException primary) {
      try {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
      } catch (ReflectiveOperationException secondary) {
        throw new AssertionError(secondary);
      }
    }
  }

  private static final class ExposedFolderTabbedPane extends FolderTabbedPane<XvfbCroquetTestSupport.TestTabComposite> {
    private ExposedFolderTabbedPane(TestTabState model) {
      super(model);
    }

    private void simulateDisplayable() {
      this.handleDisplayable();
    }

    private void simulateUndisplayable() {
      this.handleUndisplayable();
    }
  }

  private static final class TestTabState extends MutableDataTabState<XvfbCroquetTestSupport.TestTabComposite> {
    private TestTabState(XvfbCroquetTestSupport.TestTabComposite... tabs) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), -1,
          new MutableListData<>(SimpleTabCompositeCodec.getInstance(XvfbCroquetTestSupport.TestTabComposite.class), tabs));
    }
  }
}
