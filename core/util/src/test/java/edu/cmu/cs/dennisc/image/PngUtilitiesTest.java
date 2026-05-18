package edu.cmu.cs.dennisc.image;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class PngUtilitiesTest {
  private static final File BASE_DIRECTORY = new File("target/test-artifacts/PngUtilitiesTest");

  private BufferedImage createImage() {
    BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, 0x80FF0000);
    image.setRGB(1, 0, 0xFF00FF00);
    image.setRGB(2, 0, 0xFF0000FF);
    image.setRGB(0, 1, 0x00000000);
    image.setRGB(1, 1, 0x40ABCDEF);
    image.setRGB(2, 1, 0xFFFFFFFF);
    return image;
  }

  private File file(String name) {
    if (!BASE_DIRECTORY.exists()) {
      BASE_DIRECTORY.mkdirs();
    }
    return new File(BASE_DIRECTORY, name);
  }

  @Test
  public void writeToByteArrayStartsWithPngSignature() throws Exception {
    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, createImage());
    assertArrayEquals(new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47}, new byte[] {png[0], png[1], png[2], png[3]});
  }

  @Test
  public void writeToByteArrayProducesReadableImage() throws Exception {
    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, createImage());
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.PNG_CODEC_NAME, new ByteArrayInputStream(png));

    assertEquals(3, decoded.getWidth());
    assertEquals(2, decoded.getHeight());
  }

  @Test
  public void roundTripViaByteArrayPreservesArgbPixels() throws Exception {
    BufferedImage source = createImage();
    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, source);
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.PNG_CODEC_NAME, new ByteArrayInputStream(png));

    assertEquals(source.getRGB(1, 0), decoded.getRGB(1, 0));
    assertEquals(source.getRGB(2, 1), decoded.getRGB(2, 1));
  }

  @Test
  public void alphaChannelIsPreservedAcrossRoundTrip() throws Exception {
    BufferedImage source = createImage();
    byte[] png = ImageUtilities.writeToByteArray(ImageUtilities.PNG_CODEC_NAME, source);
    BufferedImage decoded = ImageUtilities.read(ImageUtilities.PNG_CODEC_NAME, new ByteArrayInputStream(png));

    assertEquals(source.getRGB(0, 0), decoded.getRGB(0, 0));
    assertEquals(source.getRGB(1, 1), decoded.getRGB(1, 1));
  }

  @Test
  public void writeCreatesParentDirectoriesAndFileCanBeReadBack() throws Exception {
    File file = file("nested/path/image.png");
    ImageUtilities.write(file, createImage());

    assertTrue(file.exists());
    BufferedImage decoded = ImageUtilities.read(file);
    assertEquals(3, decoded.getWidth());
    assertEquals(2, decoded.getHeight());
  }

  @Test
  public void readSupportsStringPathForPngFiles() throws Exception {
    File file = file("path-read.png");
    ImageUtilities.write(file, createImage());

    BufferedImage decoded = ImageUtilities.read(file.getAbsolutePath());
    assertEquals(0x40ABCDEF, decoded.getRGB(1, 1));
  }

  @Test
  public void readSupportsFileUrlForPngFiles() throws Exception {
    File file = file("url-read.png");
    ImageUtilities.write(file, createImage());

    URL url = file.toURI().toURL();
    BufferedImage decoded = ImageUtilities.read(url);
    assertEquals(0xFF0000FF, decoded.getRGB(2, 0));
  }

  @Test
  public void pngExtensionMapsToExpectedContentType() {
    assertEquals("image/png", ImageUtilities.getContentType("png"));
  }
}
