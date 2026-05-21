package edu.cmu.cs.dennisc.javax.swing.icons;

import org.junit.Test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ShapeIconDeepTest {

  @Test
  public void dimensionsIncludeInsets() {
    ShapeIcon icon = new ShapeIcon(new Rectangle2D.Double(0, 0, 10, 6), Color.RED, Color.BLUE, new Insets(1, 2, 3, 4));

    assertEquals(17, icon.getIconWidth());
    assertEquals(11, icon.getIconHeight());
  }

  @Test
  public void paintIconFillsAndDrawsShape() {
    ShapeIcon icon = new ShapeIcon(new Rectangle2D.Double(0, 0, 6, 6), Color.RED, Color.BLUE);
    BufferedImage image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      icon.paintIcon(new Component() {}, g, 2, 2);
    } finally {
      g.dispose();
    }

    assertTrue(hasColor(image, Color.RED) || hasColor(image, Color.BLUE));
  }

  private static boolean hasColor(BufferedImage image, Color color) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) == color.getRGB()) {
          return true;
        }
      }
    }
    return false;
  }
}
