package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ClosedTrashIconTest {
  private static void assumeAwtAvailable() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void constructorStoresDimensions() {
    assumeAwtAvailable();
    ClosedTrashIcon icon = new ClosedTrashIcon(20, 24, Color.RED);

    assertEquals(20, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void originalBoundsMatchGeneratedSvgDimensions() {
    assumeAwtAvailable();
    ClosedTrashIcon icon = new ClosedTrashIcon(20, 24, Color.RED);

    assertEquals(0, icon.getOrigX());
    assertEquals(0, icon.getOrigY());
    assertEquals(15, icon.getOrigWidth());
    assertEquals(16, icon.getOrigHeight());
  }

  @Test
  public void setDimensionUpdatesReportedSize() {
    assumeAwtAvailable();
    ClosedTrashIcon icon = new ClosedTrashIcon(20, 24, Color.RED);

    icon.setDimension(new Dimension(18, 22));

    assertEquals(18, icon.getIconWidth());
    assertEquals(22, icon.getIconHeight());
  }

  @Test
  public void paintIconRendersWithoutThrowing() {
    assumeAwtAvailable();
    ClosedTrashIcon icon = new ClosedTrashIcon(20, 24, Color.RED);
    BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      icon.paintIcon(null, graphics, 0, 0);
    } finally {
      graphics.dispose();
    }

    assertEquals(32, image.getWidth());
  }
}
