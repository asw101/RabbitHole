package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.croquet.SingleSelectListStateComboBoxPrepModel;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.data.MutableListData;

import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Insets;
import java.awt.Shape;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ComboBoxTrackableShapeHeadlessTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void popupTrackableShape_uses_popup_view_geometry_without_frame() {
    XvfbCroquetTestSupport.onEdt(() -> {
      MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "beta", "gamma"});
      MutableDataSingleSelectListState<String> state = new MutableDataSingleSelectListState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 1, data) {
      };
      ExposedComboBox comboBox = new ExposedComboBox(state.getPrepModel());
      comboBox.getAwtComponent().setSize(120, 32);
      comboBox.handleDisplayable();
      comboBox.refreshModel();

      TrackableShape trackableShape = comboBox.getTrackableShapeFor("beta");
      Shape shape = trackableShape.getShape(comboBox, new Insets(0, 0, 0, 0));
      Shape visibleShape = trackableShape.getVisibleShape(comboBox, new Insets(1, 1, 1, 1));
      comboBox.getAwtComponent().setMaximumRowCount(6);

      assertNotNull(shape);
      assertNotNull(visibleShape);
      assertTrue(shape.getBounds().height > 0);
      assertTrue(trackableShape.isInView());
      assertNull(trackableShape.getScrollPaneAncestor());

      comboBox.handleUndisplayable();
      return null;
    });
  }

  private static final class ExposedComboBox extends ComboBox<String> {
    private ExposedComboBox(SingleSelectListStateComboBoxPrepModel<String, ?> model) {
      super(model);
    }

    @Override
    protected JComboBox createAwtComponent() {
      return new FakePopupComboBox();
    }
  }

  private static final class FakePopupComboBox extends JComboBox<String> {
    private final FakePopupMenu popupMenu = new FakePopupMenu();
    private final ComboBoxUI comboBoxUi = new BasicComboBoxUI() {
      @Override
      public Accessible getAccessibleChild(javax.swing.JComponent c, int i) {
        return popupMenu;
      }
    };

    private FakePopupComboBox() {
      super(new String[]{"alpha", "beta", "gamma"});
      this.setUI(this.comboBoxUi);
      this.add(this.popupMenu);
      this.popupMenu.setVisible(true);
      this.popupMenu.setSize(120, 72);
    }

    @Override
    public void updateUI() {
    }

    @Override
    public boolean isPopupVisible() {
      return true;
    }
  }

  private static final class FakePopupMenu extends JPopupMenu {
    private final FakeList list = new FakeList();

    private FakePopupMenu() {
      JScrollPane scrollPane = new JScrollPane(this.list);
      scrollPane.setBounds(0, 0, 120, 72);
      scrollPane.setSize(120, 72);
      JViewport viewport = scrollPane.getViewport();
      viewport.setView(this.list);
      viewport.setViewPosition(new java.awt.Point(0, 0));
      this.list.setSize(120, 72);
      this.add(scrollPane);
    }
  }

  private static final class FakeList extends JList<String> {
    private FakeList() {
      super(new String[]{"alpha", "beta", "gamma"});
    }

    @Override
    public boolean isShowing() {
      return true;
    }
  }
}
