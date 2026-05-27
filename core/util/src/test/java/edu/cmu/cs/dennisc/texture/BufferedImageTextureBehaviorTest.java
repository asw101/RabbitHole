package edu.cmu.cs.dennisc.texture;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class BufferedImageTextureBehaviorTest {
  @Test
  public void listenerNotificationsTrackBufferedImageAndTextureFlagChanges() {
    BufferedImageTexture texture = new BufferedImageTexture();
    AtomicInteger notifications = new AtomicInteger();
    texture.addTextureListener(event -> notifications.incrementAndGet());

    texture.setBufferedImage(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    texture.setMipMappingDesired(false);
    texture.setPotentiallyAlphaBlended(true);

    assertEquals(3, notifications.get());
    assertTrue(texture.isValid());
    assertEquals(2, texture.getWidth());
    assertEquals(3, texture.getHeight());
    assertFalse(texture.isMipMappingDesired());
    assertTrue(texture.isPotentiallyAlphaBlended());
  }

  @Test
  public void binaryEncodeAndDecodeRoundTripsPixels() {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, 0xFFAA0000);
    image.setRGB(1, 1, 0xFF00BB00);

    BufferedImageTexture original = new BufferedImageTexture();
    original.setBufferedImage(image);

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    original.encode(encoder);
    BufferedImageTexture decoded = new BufferedImageTexture(encoder.createDecoder());

    assertEquals(2, decoded.getWidth());
    assertEquals(2, decoded.getHeight());
    assertEquals(0xFFAA0000, decoded.getBufferedImage().getRGB(0, 0));
    assertEquals(0xFF00BB00, decoded.getBufferedImage().getRGB(1, 1));
  }

  @Test
  public void paintDrawsTheBufferedImageIntoTheRequestedOutputSize() {
    BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    source.setRGB(0, 0, 0xFF112233);

    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(source);

    BufferedImage destination = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = destination.createGraphics();
    try {
      texture.paint(graphics, 4, 4);
    } finally {
      graphics.dispose();
    }

    assertEquals(0xFF112233, destination.getRGB(3, 3));
  }
}
