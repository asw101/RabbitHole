package edu.cmu.cs.dennisc.image;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Set;

import static org.junit.Assert.*;

public class ImageUtilitiesTest {
  private BufferedImage createImage(int width, int height) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, 0xFF000000 | (x * 37 << 16) | (y * 41 << 8) | (x + y));
      }
    }
    return image;
  }

  @Test
  public void codecNameLookupIsCaseInsensitive() {
    assertEquals(ImageUtilities.PNG_CODEC_NAME, ImageUtilities.getCodecNameForExtension("PNG"));
    assertEquals(ImageUtilities.JPEG_CODEC_NAME, ImageUtilities.getCodecNameForExtension("JpG"));
  }

  @Test
  public void contentTypeLookupMatchesKnownExtensions() {
    assertEquals("image/png", ImageUtilities.getContentType("png"));
    assertEquals("image/tga", ImageUtilities.getContentType("TGA"));
    assertNull(ImageUtilities.getContentType("unknown"));
  }

  @Test
  public void fileExtensionsContainExpectedValues() {
    Set<String> extensions = ImageUtilities.getFileExtensions();
    assertTrue(extensions.contains("png"));
    assertTrue(extensions.contains("jpg"));
    assertTrue(extensions.contains("tga"));
  }

  @Test
  public void acceptableRecognizesSupportedAndUnsupportedPaths() {
    assertTrue(ImageUtilities.isAcceptable("sample.png"));
    assertTrue(ImageUtilities.isAcceptable("sample.TIFF"));
    assertFalse(ImageUtilities.isAcceptable("sample.txt"));
  }

  @Test
  public void stretchToPowersOfTwoReturnsSameInstanceWhenAlreadySized() {
    BufferedImage image = createImage(4, 8);
    assertSame(image, ImageUtilities.stretchToPowersOfTwo(image));
  }

  @Test
  public void stretchToPowersOfTwoExpandsToNextPowerAndPreservesCorners() {
    BufferedImage image = createImage(3, 5);
    BufferedImage stretched = ImageUtilities.stretchToPowersOfTwo(image);

    assertEquals(4, stretched.getWidth());
    assertEquals(8, stretched.getHeight());
    assertEquals(image.getRGB(0, 0), stretched.getRGB(0, 0));
    assertEquals(image.getRGB(image.getWidth() - 1, image.getHeight() - 1),
        stretched.getRGB(stretched.getWidth() - 1, stretched.getHeight() - 1));
  }

  @Test
  public void getPixelsReturnsExpectedArgbValues() {
    BufferedImage image = createImage(2, 2);
    int[] pixels = ImageUtilities.getPixels(image, 2, 2);

    assertEquals(4, pixels.length);
    assertEquals(image.getRGB(0, 0), pixels[0]);
    assertEquals(image.getRGB(1, 1), pixels[3]);
  }

  @Test
  public void createBufferedImageCopiesPixelsAndRequestedType() {
    BufferedImage image = createImage(3, 2);
    BufferedImage copy = ImageUtilities.createBufferedImage(image, BufferedImage.TYPE_3BYTE_BGR);

    assertEquals(BufferedImage.TYPE_3BYTE_BGR, copy.getType());
    assertEquals(image.getWidth(), copy.getWidth());
    assertEquals(image.getHeight(), copy.getHeight());
    assertEquals(image.getRGB(1, 1), copy.getRGB(1, 1));
  }
}
