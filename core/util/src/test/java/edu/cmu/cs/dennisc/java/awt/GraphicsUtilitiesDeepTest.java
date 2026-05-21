package edu.cmu.cs.dennisc.java.awt;

import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class GraphicsUtilitiesDeepTest {

  @Test
  public void constructorThrowsError() throws Exception {
    Constructor<GraphicsUtilities> ctor = GraphicsUtilities.class.getDeclaredConstructor();
    ctor.setAccessible(true);

    try {
      ctor.newInstance();
      fail();
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof Error);
    }
  }

  @Test
  public void getGraphicsReturnsReusableGraphics() {
    assertNotNull(GraphicsUtilities.getGraphics());
    assertNotNull(GraphicsUtilities.getGraphics());
  }

  @Test
  public void renderingHelpersUpdateGraphicsState() {
    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      GraphicsUtilities.setRenderingHint(g, RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      assertEquals(RenderingHints.VALUE_RENDER_QUALITY, g.getRenderingHint(RenderingHints.KEY_RENDERING));

      Object previous = GraphicsUtilities.setAntialiasing(g, RenderingHints.VALUE_ANTIALIAS_ON);
      assertEquals(RenderingHints.VALUE_ANTIALIAS_ON, g.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
      assertNotNull(previous);

      Shape oldClip = new Rectangle(0, 0, 5, 5);
      g.setClip(oldClip);
      Shape returned = GraphicsUtilities.setClip(g, new Rectangle(1, 1, 3, 3));
      assertEquals(oldClip.getBounds(), returned.getBounds());
      assertEquals(new Rectangle(1, 1, 3, 3), g.getClip().getBounds());
    } finally {
      g.dispose();
    }
  }

  @Test
  public void drawCenteredScaledToFitImageCentersSource() {
    BufferedImage src = new BufferedImage(10, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D srcGraphics = src.createGraphics();
    srcGraphics.setColor(Color.RED);
    srcGraphics.fillRect(0, 0, src.getWidth(), src.getHeight());
    srcGraphics.dispose();

    BufferedImage dest = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
    GraphicsUtilities.drawCenteredScaledToFitImage(src, dest);

    assertEquals(Color.RED.getRGB(), dest.getRGB(20, 20));
    assertEquals(0, dest.getRGB(0, 0));
  }

  @Test
  public void getImageForImageIconReturnsWrappedImage() {
    BufferedImage src = new BufferedImage(4, 6, BufferedImage.TYPE_INT_ARGB);
    ImageIcon icon = new ImageIcon(src);

    assertSame(src, GraphicsUtilities.getImageForIcon(icon));
  }

  @Test
  public void getImageForCustomIconPaintsIntoBufferedImage() {
    Icon icon = new Icon() {
      @Override
      public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
      }

      @Override
      public int getIconWidth() {
        return 3;
      }

      @Override
      public int getIconHeight() {
        return 4;
      }
    };

    BufferedImage image = (BufferedImage) GraphicsUtilities.getImageForIcon(icon);
    assertEquals(3, image.getWidth());
    assertEquals(4, image.getHeight());
    assertEquals(Color.BLUE.getRGB(), image.getRGB(1, 1));
  }

  @Test
  public void drawCenteredTextHandlesDimensionRectangleAndNull() {
    BufferedImage image = new BufferedImage(60, 30, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      g.setColor(Color.BLACK);
      GraphicsUtilities.drawCenteredText(g, "abc", new java.awt.Dimension(60, 30));
      GraphicsUtilities.drawCenteredText(g, "abc", new java.awt.Rectangle(0, 0, 60, 30));
      GraphicsUtilities.drawCenteredText(g, "abc", new Rectangle2D.Double(0, 0, 60, 30));
      GraphicsUtilities.drawCenteredText(g, null, 0, 0, 60, 30);
    } finally {
      g.dispose();
    }

    assertTrue(hasNonTransparentPixel(image));
  }

  @Test
  public void drawAndFillTriangleRenderPixels() {
    BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    try {
      g.setColor(Color.BLACK);
      GraphicsUtilities.drawTriangle(g, GraphicsUtilities.Heading.NORTH, 1, 1, 10, 10);
      GraphicsUtilities.drawTriangle(g, GraphicsUtilities.Heading.EAST, 12, 1, 10, 10);
      GraphicsUtilities.fillTriangle(g, GraphicsUtilities.Heading.SOUTH, 1, 12, 10, 10);
      GraphicsUtilities.fillTriangle(g, GraphicsUtilities.Heading.WEST, 12, 12, 10, 10);
    } finally {
      g.dispose();
    }

    assertTrue(hasNonTransparentPixel(image));
  }

  private static boolean hasNonTransparentPixel(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }
}
