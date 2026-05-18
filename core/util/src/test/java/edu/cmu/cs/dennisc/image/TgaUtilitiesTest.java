package edu.cmu.cs.dennisc.image;

import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class TgaUtilitiesTest {
  private BufferedImage read(byte[] data) throws Exception {
    return TgaUtilities.readTGA(new BufferedInputStream(new ByteArrayInputStream(data)));
  }

  private byte[] tga(int imageType, int width, int height, int bitsPerPixel, int descriptor, byte[] id, byte[] pixels) throws Exception {
    return tga(0, imageType, width, height, bitsPerPixel, descriptor, id, pixels);
  }

  private byte[] tga(int colorMapType, int imageType, int width, int height, int bitsPerPixel, int descriptor, byte[] id, byte[] pixels) throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    byte[] header = new byte[18];
    header[0] = (byte) (id != null ? id.length : 0);
    header[1] = (byte) colorMapType;
    header[2] = (byte) imageType;
    header[12] = (byte) (width & 0xFF);
    header[13] = (byte) ((width >> 8) & 0xFF);
    header[14] = (byte) (height & 0xFF);
    header[15] = (byte) ((height >> 8) & 0xFF);
    header[16] = (byte) bitsPerPixel;
    header[17] = (byte) descriptor;
    output.write(header);
    if (id != null) {
      output.write(id);
    }
    output.write(pixels);
    return output.toByteArray();
  }

  @Test
  public void read24BitSinglePixel() throws Exception {
    BufferedImage image = read(tga(2, 1, 1, 24, 0, null, new byte[] {0, 0, (byte) 255}));
    assertEquals(0xFFFF0000, image.getRGB(0, 0));
  }

  @Test
  public void read32BitSinglePixelWithAlpha() throws Exception {
    BufferedImage image = read(tga(2, 1, 1, 32, 0, null, new byte[] {0, (byte) 255, 0, 0x40}));
    assertEquals(0x4000FF00, image.getRGB(0, 0));
  }

  @Test
  public void read8BitGrayscaleSinglePixel() throws Exception {
    BufferedImage image = read(tga(3, 1, 1, 8, 0, null, new byte[] {(byte) 0x7F}));
    assertEquals(0xFF7F7F7F, image.getRGB(0, 0));
  }

  @Test
  public void readSkipsIdFieldBeforePixels() throws Exception {
    BufferedImage image = read(tga(2, 1, 1, 24, 0, new byte[] {9, 8, 7}, new byte[] {(byte) 255, 0, 0}));
    assertEquals(0xFF0000FF, image.getRGB(0, 0));
  }

  @Test
  public void readFlipsBottomLeftOriginToTopLeftCoordinates() throws Exception {
    byte[] pixels = new byte[] {
        (byte) 255, 0, 0,
        0, (byte) 255, 0,
        0, 0, (byte) 255,
        (byte) 255, (byte) 255, (byte) 255
    };
    BufferedImage image = read(tga(2, 2, 2, 24, 0, null, pixels));

    assertEquals(0xFFFF0000, image.getRGB(0, 0));
    assertEquals(0xFFFFFFFF, image.getRGB(1, 0));
    assertEquals(0xFF0000FF, image.getRGB(0, 1));
    assertEquals(0xFF00FF00, image.getRGB(1, 1));
  }

  @Test(expected = RuntimeException.class)
  public void horizontalFlipDescriptorIsUnsupported() throws Exception {
    read(tga(2, 1, 1, 24, 0x10, null, new byte[] {0, 0, 0}));
  }

  @Test(expected = RuntimeException.class)
  public void verticalFlipDescriptorIsUnsupported() throws Exception {
    read(tga(2, 1, 1, 24, 0x20, null, new byte[] {0, 0, 0}));
  }

  @Test(expected = RuntimeException.class)
  public void colorMappedTgaIsUnsupported() throws Exception {
    read(tga(1, 2, 1, 1, 24, 0, null, new byte[] {0, 0, 0}));
  }

  @Test(expected = RuntimeException.class)
  public void unsupportedImageTypeThrows() throws Exception {
    read(tga(1, 1, 1, 24, 0, null, new byte[] {0, 0, 0}));
  }

  @Test(expected = RuntimeException.class)
  public void unsupported16BitPixelsThrow() throws Exception {
    read(tga(2, 1, 1, 16, 0, null, new byte[] {0, 0}));
  }
}
