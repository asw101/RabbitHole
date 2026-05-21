package edu.cmu.cs.dennisc.capture;

import org.junit.Test;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ImageCaptureUtilitiesLightweightTest {

  @Test
  public void captureRectangleUsesRequestedBounds() {
    TestComponent component = new TestComponent();
    component.setSize(20, 10);

    if (GraphicsEnvironment.isHeadless()) {
      assertThrows(HeadlessException.class,
          () -> ImageCaptureUtilities.captureRectangle(component, new Rectangle(5, 2, 10, 4), null));
    } else {
      BufferedImage image = (BufferedImage) ImageCaptureUtilities.captureRectangle(component, new Rectangle(5, 2, 10, 4), null);
      assertEquals(10, image.getWidth());
      assertEquals(4, image.getHeight());
      assertEquals(Color.RED.getRGB(), image.getRGB(1, 1));
    }
  }

  @Test
  public void captureCompleteCapturesWholeLightweightComponent() {
    TestComponent component = new TestComponent();
    component.setSize(16, 12);

    if (GraphicsEnvironment.isHeadless()) {
      assertThrows(HeadlessException.class, () -> ImageCaptureUtilities.captureComplete(component, null));
    } else {
      BufferedImage image = (BufferedImage) ImageCaptureUtilities.captureComplete(component, null);
      assertEquals(16, image.getWidth());
      assertEquals(12, image.getHeight());
      assertEquals(Color.RED.getRGB(), image.getRGB(8, 6));
    }
  }

  private static final class TestComponent extends JComponent {
    @Override
    public boolean isLightweight() {
      return true;
    }

    @Override
    public Image createImage(int width, int height) {
      return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    protected void paintComponent(Graphics g) {
      g.setColor(Color.RED);
      g.fillRect(0, 0, getWidth(), getHeight());
    }
  }
}
