package edu.cmu.cs.dennisc.image;

import org.junit.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
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

  @Test
  public void readThrowsForUnknownExtensionOnFileAndUrl() throws Exception {
    try {
      ImageUtilities.read("sample.unknown");
      fail();
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Could not find codec"));
    }

    try {
      ImageUtilities.read(new URL("file:/sample.unknown"));
      fail();
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Could not find codec"));
    }
  }

  @Test
  public void writeToByteArraySupportsNonRenderedImagesAndRoundTripsThroughRead() throws Exception {
    BufferedImage source = createImage(3, 3);
    Image scaled = source.getScaledInstance(3, 3, Image.SCALE_REPLICATE);

    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, scaled);
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.PNG_CODEC_NAME, new ByteArrayInputStream(png));

    assertEquals(3, ImageUtilities.getWidth(scaled));
    assertEquals(3, ImageUtilities.getHeight(scaled));
    assertEquals(source.getRGB(0, 0), decoded.getRGB(0, 0));
  }

  @Test
  public void codecLookupHandlesNullAndFileAcceptabilityUsesFileName() {
    assertNull(ImageUtilities.getCodecNameForExtension(null));
    assertTrue(ImageUtilities.isAcceptable(new java.io.File("thing.png")));
    assertFalse(ImageUtilities.isAcceptable(new java.io.File("thing.unknown")));
    assertNotNull(ImageUtilities.accessImageObserver());
  }

  @Test
  public void writePathCreatesFileAndUnknownExtensionWriteThrows() throws Exception {
    java.io.File file = new java.io.File("target/test-artifacts/ImageUtilitiesTest/path-write.png");
    file.getParentFile().mkdirs();
    ImageUtilities.write(file.getAbsolutePath(), createImage(2, 2));
    assertTrue(file.exists());

    try {
      ImageUtilities.write(new java.io.File("target/test-artifacts/ImageUtilitiesTest/image.unknown"), createImage(1, 1));
      fail();
    } catch (RuntimeException expected) {
      assertTrue(expected.getMessage().contains("Could not find codec"));
    }
  }

  @Test
  public void readAcceptsAlreadyBufferedStreams() throws Exception {
    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, createImage(2, 2));
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.PNG_CODEC_NAME, new java.io.BufferedInputStream(new ByteArrayInputStream(png)));
    assertEquals(2, decoded.getWidth());
  }

  @Test
  public void jpegWriteConvertsArgbBufferedImage() throws Exception {
    BufferedImage source = createImage(4, 3);
    byte[] jpeg = ImageUtilities.writeToByteArray(ImageUtilities.JPEG_CODEC_NAME, source);
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.JPEG_CODEC_NAME, new ByteArrayInputStream(jpeg));

    assertEquals(4, decoded.getWidth());
    assertEquals(3, decoded.getHeight());
  }

  @Test
  public void jpegWriteAcceptsAlreadyBgrBufferedImage() throws Exception {
    BufferedImage source = new BufferedImage(3, 2, BufferedImage.TYPE_3BYTE_BGR);
    source.setRGB(1, 1, 0xFF112233);
    byte[] jpeg = ImageUtilities.writeToByteArray(ImageUtilities.JPEG_CODEC_NAME, source);
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.JPEG_CODEC_NAME, new ByteArrayInputStream(jpeg));

    assertEquals(3, decoded.getWidth());
    assertEquals(2, decoded.getHeight());
  }
}
