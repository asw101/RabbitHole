package org.alice.imageeditor.croquet.views.renderers;

import org.alice.imageeditor.croquet.TestSupport;
import org.junit.Test;

import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Color;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FilenameListCellRendererTest {
  @Test
  public void nullValuesRenderWorkingPlaceholderAndRealValuesPassThrough() throws Exception {
    TestSupport.onEdt(() -> {
      FilenameListCellRenderer renderer = new FilenameListCellRenderer();
      JList<String> list = new JList<String>();

      JLabel workingLabel = (JLabel) renderer.getListCellRendererComponent(list, null, 0, false, false);
      assertEquals("working", workingLabel.getText());
      assertEquals(Color.WHITE, workingLabel.getBackground());
      assertEquals(Color.GRAY, workingLabel.getForeground());

      JLabel fileLabel = (JLabel) renderer.getListCellRendererComponent(list, "image.png", 1, true, true);
      assertEquals("image.png", fileLabel.getText());
      assertNotEquals("working", fileLabel.getText());
    });
  }
}
