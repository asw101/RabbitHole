package edu.cmu.cs.dennisc.texture;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import edu.cmu.cs.dennisc.texture.event.TextureEvent;
import edu.cmu.cs.dennisc.texture.event.TextureListener;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class BufferedImageTextureTest {
  private BufferedImage createImage(int width, int height) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, 0xFF000000 | (x * 70 << 16) | (y * 70 << 8) | 0x22);
      }
    }
    return image;
  }

  private BufferedImageTexture roundTrip(BufferedImageTexture texture) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    texture.encode(encoder);
    encoder.flush();
    return new BufferedImageTexture(new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray())));
  }

  @Test
  public void defaultTextureIsInvalidAndZeroSized() {
    BufferedImageTexture texture = new BufferedImageTexture();
    assertFalse(texture.isValid());
    assertEquals(0, texture.getWidth());
    assertEquals(0, texture.getHeight());
  }

  @Test
  public void defaultFlagsAndPolicyMatchImplementation() {
    BufferedImageTexture texture = new BufferedImageTexture();
    assertTrue(texture.isMipMappingDesired());
    assertFalse(texture.isPotentiallyAlphaBlended());
    assertEquals(MipMapGenerationPolicy.PAINT_EACH_INDIVIDUAL_LEVEL, texture.getMipMapGenerationPolicy());
  }

  @Test
  public void setBufferedImageUpdatesSizeAndValidity() {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(createImage(3, 5));

    assertTrue(texture.isValid());
    assertEquals(3, texture.getWidth());
    assertEquals(5, texture.getHeight());
  }

  @Test
  public void setBufferedImageFiresListenerWhenChanged() {
    BufferedImageTexture texture = new BufferedImageTexture();
    AtomicInteger count = new AtomicInteger();
    texture.addTextureListener(new TextureListener() {
      @Override
      public void textureChanged(TextureEvent textureEvent) {
        count.incrementAndGet();
      }
    });

    texture.setBufferedImage(createImage(2, 2));
    assertEquals(1, count.get());
  }

  @Test
  public void settingSameBufferedImageDoesNotFireListener() {
    BufferedImageTexture texture = new BufferedImageTexture();
    BufferedImage image = createImage(2, 2);
    AtomicInteger count = new AtomicInteger();
    texture.addTextureListener(event -> count.incrementAndGet());

    texture.setBufferedImage(image);
    texture.setBufferedImage(image);

    assertEquals(1, count.get());
  }

  @Test
  public void setMipMappingDesiredFiresOnlyOnChange() {
    BufferedImageTexture texture = new BufferedImageTexture();
    AtomicInteger count = new AtomicInteger();
    texture.addTextureListener(event -> count.incrementAndGet());

    texture.setMipMappingDesired(false);
    texture.setMipMappingDesired(false);

    assertFalse(texture.isMipMappingDesired());
    assertEquals(1, count.get());
  }

  @Test
  public void directSetMipMappingDesiredDoesNotFireListener() {
    BufferedImageTexture texture = new BufferedImageTexture();
    AtomicInteger count = new AtomicInteger();
    texture.addTextureListener(event -> count.incrementAndGet());

    texture.directSetMipMappingDesired(false);

    assertFalse(texture.isMipMappingDesired());
    assertEquals(0, count.get());
  }

  @Test
  public void setPotentiallyAlphaBlendedFiresOnlyOnChange() {
    BufferedImageTexture texture = new BufferedImageTexture();
    AtomicInteger count = new AtomicInteger();
    texture.addTextureListener(event -> count.incrementAndGet());

    texture.setPotentiallyAlphaBlended(true);
    texture.setPotentiallyAlphaBlended(true);

    assertTrue(texture.isPotentiallyAlphaBlended());
    assertEquals(1, count.get());
  }

  @Test
  public void encodeAndDecodeRoundTripPreservesPixels() throws Exception {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(createImage(3, 2));

    BufferedImageTexture decoded = roundTrip(texture);

    assertEquals(texture.getWidth(), decoded.getWidth());
    assertEquals(texture.getHeight(), decoded.getHeight());
    assertEquals(texture.getBufferedImage().getRGB(1, 1), decoded.getBufferedImage().getRGB(1, 1));
  }

  @Test
  public void paintDrawsTextureIntoDestinationGraphics() {
    BufferedImageTexture texture = new BufferedImageTexture();
    BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    source.setRGB(0, 0, 0xFFFF00FF);
    texture.setBufferedImage(source);

    BufferedImage destination = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = destination.createGraphics();
    try {
      texture.paint(graphics, destination.getWidth(), destination.getHeight());
    } finally {
      graphics.dispose();
    }

    assertEquals(0xFFFF00FF, destination.getRGB(3, 2));
  }
}
