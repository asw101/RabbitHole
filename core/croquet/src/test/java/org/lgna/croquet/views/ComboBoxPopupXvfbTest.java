package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.croquet.XvfbCroquetTestSupport;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JFrame;
import java.awt.Insets;
import java.awt.Shape;

import static org.junit.Assert.*;

public class ComboBoxPopupXvfbTest {
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void popupTrackableShapeUsesVisiblePopupGeometry() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "beta", "gamma"});
      MutableDataSingleSelectListState<String> state = new MutableDataSingleSelectListState<>(Application.DOCUMENT_UI_GROUP, java.util.UUID.randomUUID(), 1, data) {
      };
      ComboBox<String> comboBox = state.getPrepModel().createComboBoxWithItemCodecListCellRenderer();

      JFrame frame = new JFrame("combo");
      try {
        frame.setContentPane(comboBox.getAwtComponent());
        frame.pack();
        frame.setLocation(220, 220);
        frame.setVisible(true);
        comboBox.handleDisplayable();
        comboBox.getAwtComponent().setPopupVisible(true);

        TrackableShape trackableShape = comboBox.getTrackableShapeFor("beta");
        Shape shape = trackableShape.getShape(comboBox, new Insets(0, 0, 0, 0));

        assertNotNull(shape);
        assertTrue(shape.getBounds().height > 0);
        assertTrue(trackableShape.isInView());
      } finally {
        comboBox.getAwtComponent().setPopupVisible(false);
        comboBox.handleUndisplayable();
        frame.dispose();
      }
    });
  }
}
