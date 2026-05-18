package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class TabIconTest {

  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(32, 32), Color.BLUE);
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_differentColor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(24, 24), Color.RED);
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void paintIcon_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(32, 32), Color.BLUE);
    BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    try {
      icon.paintIcon(null, g2, 0, 0);
    } finally {
      g2.dispose();
    }
  }
}
