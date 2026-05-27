package edu.cmu.cs.dennisc.java.awt.datatransfer;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TransferableImageWithDpiBehaviorTest {
  private static final DataFlavor PNG_FLAVOR = new DataFlavor("image/png", "PNG Image");

  @Test
  public void transferFlavorsDependOnWhetherDpiMetadataIsRequested() {
    BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);

    TransferableImageWithDpi withDpi = new TransferableImageWithDpi(image, 300);
    TransferableImageWithDpi imageOnly = new TransferableImageWithDpi(image, null);

    assertTrue(withDpi.isDataFlavorSupported(DataFlavor.imageFlavor));
    assertTrue(withDpi.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
    assertFalse(imageOnly.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
    assertArrayEquals(new DataFlavor[] {DataFlavor.imageFlavor}, imageOnly.getTransferDataFlavors());
  }

  @Test
  public void imageFlavorReturnsTheOriginalImageInstance() throws Exception {
    BufferedImage image = new BufferedImage(3, 1, BufferedImage.TYPE_INT_ARGB);
    TransferableImageWithDpi transferable = new TransferableImageWithDpi(image, 144);

    Object transferData = transferable.getTransferData(DataFlavor.imageFlavor);

    assertSame(image, transferData);
  }

  @Test
  public void pngFlavorProvidesReadablePngStream() throws Exception {
    BufferedImage image = new BufferedImage(2, 1, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, 0xFF336699);
    image.setRGB(1, 0, 0xFFFFCC00);
    TransferableImageWithDpi transferable = new TransferableImageWithDpi(image, null);

    Object transferData = transferable.getTransferData(PNG_FLAVOR);
    assertTrue(transferData instanceof InputStream);

    byte[] bytes = ((InputStream) transferData).readAllBytes();
    BufferedImage decoded = ImageIO.read(new ByteArrayInputStream(bytes));
    assertNotNull(decoded);
    assertEquals(2, decoded.getWidth());
    assertEquals(1, decoded.getHeight());
    assertEquals(0xFF336699, decoded.getRGB(0, 0));
    assertEquals(0xFFFFCC00, decoded.getRGB(1, 0));
  }

  @Test(expected = UnsupportedFlavorException.class)
  public void unsupportedFlavorThrowsUnsupportedFlavorException() throws UnsupportedFlavorException, IOException {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    TransferableImageWithDpi transferable = new TransferableImageWithDpi(image, null);

    transferable.getTransferData(DataFlavor.stringFlavor);
  }
}
