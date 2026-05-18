package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class BoxIconTest {

  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(64, 48));
    assertEquals(64, icon.getIconWidth());
    assertEquals(48, icon.getIconHeight());
  }

  @Test
  public void paintIcon_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(32, 32));
    BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    try {
      icon.paintIcon(null, g2, 0, 0);
    } finally {
      g2.dispose();
    }
  }

  @Test
  public void paintIcon_largeSize_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(128, 128));
    BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    try {
      icon.paintIcon(null, g2, 0, 0);
    } finally {
      g2.dispose();
    }
  }

  @Test
  public void paintIcon_smallSize_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(8, 8));
    BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    try {
      icon.paintIcon(null, g2, 0, 0);
    } finally {
      g2.dispose();
    }
  }
}
