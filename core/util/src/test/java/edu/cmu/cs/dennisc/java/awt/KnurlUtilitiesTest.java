package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class KnurlUtilitiesTest {
  @FunctionalInterface
  private interface Painter {
    void paint(Graphics2D graphics);
  }

  private static BufferedImage render(Painter painter) {
    BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
      graphics.setColor(Color.BLACK);
      painter.paint(graphics);
      return image;
    } finally {
      graphics.dispose();
    }
  }

  private static void assertPixel(BufferedImage image, int x, int y, Color color) {
    assertEquals("unexpected pixel at (" + x + ", " + y + ")", color.getRGB(), image.getRGB(x, y));
  }

  @Test
  public void paintKnurl3DrawsExpectedStaggeredThreeColumnPattern() {
    BufferedImage image = render(graphics -> KnurlUtilities.paintKnurl3(graphics, 2, 1, 4, 9));

    assertPixel(image, 4, 1, Color.BLACK);
    assertPixel(image, 2, 3, Color.BLACK);
    assertPixel(image, 6, 3, Color.BLACK);
    assertPixel(image, 4, 5, Color.BLACK);
    assertPixel(image, 2, 7, Color.BLACK);
    assertPixel(image, 6, 7, Color.BLACK);
    assertPixel(image, 4, 9, Color.BLACK);
    assertPixel(image, 3, 3, Color.WHITE);
  }

  @Test
  public void paintKnurl5DrawsExpectedAlternatingFiveColumnPattern() {
    BufferedImage image = render(graphics -> KnurlUtilities.paintKnurl5(graphics, 1, 2, 8, 9));

    assertPixel(image, 3, 2, Color.BLACK);
    assertPixel(image, 7, 2, Color.BLACK);
    assertPixel(image, 1, 4, Color.BLACK);
    assertPixel(image, 5, 4, Color.BLACK);
    assertPixel(image, 9, 4, Color.BLACK);
    assertPixel(image, 3, 6, Color.BLACK);
    assertPixel(image, 7, 6, Color.BLACK);
    assertPixel(image, 1, 8, Color.BLACK);
    assertPixel(image, 5, 8, Color.BLACK);
    assertPixel(image, 9, 8, Color.BLACK);
    assertPixel(image, 4, 4, Color.WHITE);
  }
}
