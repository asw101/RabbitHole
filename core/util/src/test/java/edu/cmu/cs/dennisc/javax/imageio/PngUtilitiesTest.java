package edu.cmu.cs.dennisc.javax.imageio;

import org.junit.Test;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.*;

public class PngUtilitiesTest {
  @Test
  public void writeAddsPhysMetadataAndProducesReadableImage() throws Exception {
    BufferedImage image = new BufferedImage(4, 3, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(2, 1, 0xFF336699);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ImageOutputStream ios = new MemoryCacheImageOutputStream(bytes)) {
      PngUtilities.write(image, 300, ios);
    }

    byte[] png = bytes.toByteArray();
    assertTrue(png.length > 8);
    Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("png");
    ImageReader reader = readers.next();
    try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(png))) {
      reader.setInput(input);
      BufferedImage decoded = reader.read(0);
      assertEquals(0xFF336699, decoded.getRGB(2, 1));

      IIOMetadata metadata = reader.getImageMetadata(0);
      IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());
      NodeList physNodes = root.getElementsByTagName("pHYs");
      assertTrue(physNodes.getLength() >= 1);
      IIOMetadataNode phys = (IIOMetadataNode) physNodes.item(0);
      assertEquals("11811", phys.getAttribute("pixelsPerUnitXAxis"));
      assertEquals("11811", phys.getAttribute("pixelsPerUnitYAxis"));
      assertEquals("meter", phys.getAttribute("unitSpecifier"));
    } finally {
      reader.dispose();
    }
  }

  @Test
  public void writeToFileProducesExistingPng() throws Exception {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
    File file = new File("target/test-artifacts/JavaxPngUtilitiesTest/image.png");
    file.getParentFile().mkdirs();

    PngUtilities.write(image, 96, file);

    assertTrue(file.exists());
    assertEquals(2, ImageIO.read(file).getWidth());
  }
}
