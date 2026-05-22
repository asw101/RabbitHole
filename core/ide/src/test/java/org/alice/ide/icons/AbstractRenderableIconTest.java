package org.alice.ide.icons;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertTrue;

public abstract class AbstractRenderableIconTest {
  protected Dimension getSize() {
    return new Dimension(64, 64);
  }

  protected abstract Icon createIcon();

  protected final BufferedImage render(Icon icon) {
    BufferedImage image = new BufferedImage(Math.max(1, icon.getIconWidth()), Math.max(1, icon.getIconHeight()), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    try {
      icon.paintIcon(null, g2, 0, 0);
    } finally {
      g2.dispose();
    }
    return image;
  }

  protected final int countOpaquePixels(BufferedImage image) {
    int count = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (((image.getRGB(x, y) >>> 24) & 0xff) != 0) {
          count++;
        }
      }
    }
    return count;
  }

  @Test
  public void iconHasPositiveDimensions() {
    Icon icon = createIcon();
    assertTrue(icon.getIconWidth() > 0);
    assertTrue(icon.getIconHeight() > 0);
  }

  @Test
  public void iconRendersVisiblePixels() {
    Icon icon = createIcon();
    assertTrue(countOpaquePixels(render(icon)) > 0);
  }
}
