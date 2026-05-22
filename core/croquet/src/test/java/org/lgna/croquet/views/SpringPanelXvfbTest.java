package org.lgna.croquet.views;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.XvfbCroquetTestSupport;

import javax.swing.JFrame;

import static org.junit.Assert.*;

public class SpringPanelXvfbTest {
  @BeforeClass
  public static void installApplication() {
    XvfbCroquetTestSupport.installApplication();
  }

  @Test
  public void springPanelAddsCentersAndRemovesComponents() {
    XvfbCroquetTestSupport.tryOnEdt(() -> {
      TestSpringPanel panel = new TestSpringPanel();
      Label westNorth = new Label("north-west");
      Label centered = new Label("centered");
      Label eastSouth = new Label("south-east");
      JFrame frame = new JFrame("spring");
      try {
        panel.addComponent(westNorth, SpringPanel.Horizontal.WEST, 10, SpringPanel.Vertical.NORTH, 12);
        panel.addComponent(centered, SpringPanel.Horizontal.CENTER, SpringPanel.Horizontal.CENTER, 0, SpringPanel.Vertical.CENTER, SpringPanel.Vertical.CENTER, 0, panel);
        panel.addComponent(eastSouth, SpringPanel.Horizontal.EAST, -8, SpringPanel.Vertical.SOUTH, -6, panel);

        frame.setContentPane(panel.getAwtComponent());
        frame.setSize(240, 140);
        frame.doLayout();
        panel.getAwtComponent().doLayout();

        assertEquals(3, panel.getAwtComponent().getComponentCount());
        assertTrue(centered.getAwtComponent().getX() > westNorth.getAwtComponent().getX());
        assertTrue(eastSouth.getAwtComponent().getY() >= centered.getAwtComponent().getY());

        panel.removeComponent(westNorth);
        assertEquals(2, panel.getAwtComponent().getComponentCount());
      } finally {
        frame.dispose();
      }
    });
  }

  private static final class TestSpringPanel extends SpringPanel {
  }
}
