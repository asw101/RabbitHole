package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractThumbnailMakerFramingTest {
  @Test
  public void fullyTransparentImagesReportZeroBordersAndAreConsideredFullyFramed() {
    BufferedImage image = new BufferedImage(3, 2, BufferedImage.TYPE_INT_ARGB);

    assertEquals(0, AbstractThumbnailMaker.getLeftBorder(image));
    assertEquals(0, AbstractThumbnailMaker.getRightBorder(image));
    assertEquals(0, AbstractThumbnailMaker.getTopBorder(image));
    assertEquals(0, AbstractThumbnailMaker.getBottomBorder(image));
    assertTrue(AbstractThumbnailMaker.isFullyFramed(image));
  }

  @Test
  public void opaquePixelsTouchingTheImageBorderMakeTheFrameIncomplete() {
    BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 2, 0xFFFFFFFF);

    assertFalse(AbstractThumbnailMaker.isFullyFramed(image));
  }
}
