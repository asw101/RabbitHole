package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.junit.Test;
import org.lgna.common.resources.ImageResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ImageTextureFactoryBehaviorTest {
  private static BufferedImage createImage(int width, int height, int imageType, int rgb) {
    BufferedImage image = new BufferedImage(width, height, imageType);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, rgb);
      }
    }
    return image;
  }

  private static byte[] toPngBytes(BufferedImage image) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    return baos.toByteArray();
  }

  private static ImageResource createImageResource(BufferedImage image) throws IOException {
    ImageResource resource = new ImageResource(UUID.randomUUID());
    resource.setName("sample.png");
    resource.setOriginalFileName("sample.png");
    resource.setContent("image/png", toPngBytes(image));
    return resource;
  }

  @Test
  public void imageFactoryCachesBufferedImagesPerResource() throws IOException {
    ImageResource resource = createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_RGB, 0xFF2266AA));

    BufferedImage first = ImageFactory.getBufferedImage(resource);
    BufferedImage second = ImageFactory.getBufferedImage(resource);

    assertNotNull(first);
    assertSame(first, second);
    assertEquals(2, resource.getWidth());
    assertEquals(2, resource.getHeight());
  }

  @Test
  public void imageFactoryForgetsCachedImageWhenResourceContentChanges() throws IOException {
    ImageResource resource = createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_RGB, 0xFF112233));
    BufferedImage first = ImageFactory.getBufferedImage(resource);

    resource.setContent("image/png", toPngBytes(createImage(4, 1, BufferedImage.TYPE_INT_ARGB, 0x88123456)));
    BufferedImage second = ImageFactory.getBufferedImage(resource);

    assertNotNull(first);
    assertNotNull(second);
    assertNotSame(first, second);
    assertEquals(4, resource.getWidth());
    assertEquals(1, resource.getHeight());
  }

  @Test
  public void imageFactoryReturnsNullForInvalidImageBytes() {
    ImageResource resource = new ImageResource(UUID.randomUUID());
    resource.setName("broken.png");
    resource.setOriginalFileName("broken.png");
    resource.setContent("image/png", new byte[]{1, 2, 3, 4});

    assertNull(ImageFactory.getBufferedImage(resource));
  }

  @Test
  public void textureFactoryCachesTexturesPerResource() throws IOException {
    ImageResource resource = createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_RGB, 0xFF336699));

    BufferedImageTexture first = TextureFactory.getTexture(resource, true);
    BufferedImageTexture second = TextureFactory.getTexture(resource, false);

    assertNotNull(first);
    assertSame(first, second);
    assertTrue(first.isMipMappingDesired());
    assertFalse(first.isPotentiallyAlphaBlended());
  }

  @Test
  public void textureFactoryUpdatesExistingTextureWhenResourceChanges() throws IOException {
    ImageResource resource = createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_RGB, 0xFF445566));
    BufferedImageTexture texture = TextureFactory.getTexture(resource, true);

    resource.setContent("image/png", toPngBytes(createImage(4, 1, BufferedImage.TYPE_INT_ARGB, 0x88445566)));
    BufferedImageTexture updated = TextureFactory.getTexture(resource, true);

    assertSame(texture, updated);
    assertEquals(4, updated.getWidth());
    assertEquals(1, updated.getHeight());
    assertTrue(updated.isPotentiallyAlphaBlended());
  }

  @Test
  public void textureFactoryReturnsNullForInvalidImages() {
    ImageResource resource = new ImageResource(UUID.randomUUID());
    resource.setName("broken.png");
    resource.setOriginalFileName("broken.png");
    resource.setContent("image/png", new byte[]{9, 8, 7});

    assertNull(TextureFactory.getTexture(resource, true));
  }
}
