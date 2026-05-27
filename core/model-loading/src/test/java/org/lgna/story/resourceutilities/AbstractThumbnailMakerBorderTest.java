package org.lgna.story.resourceutilities;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractThumbnailMakerBorderTest {
  @Test
  public void transparentAlphaChannelControlsPixelClassification() {
    assertTrue(AbstractThumbnailMaker.isTransparent(0x00010203));
    assertFalse(AbstractThumbnailMaker.isTransparent(0xFF010203));
  }

  @Test
  public void borderDetectionFindsTheInsetOpaqueRectangle() {
    BufferedImage image = new BufferedImage(5, 4, BufferedImage.TYPE_INT_ARGB);
    for (int x = 1; x <= 3; x++) {
      for (int y = 1; y <= 2; y++) {
        image.setRGB(x, y, 0xFFFFFFFF);
      }
    }

    assertEquals(1, AbstractThumbnailMaker.getLeftBorder(image));
    assertEquals(2, AbstractThumbnailMaker.getRightBorder(image));
    assertEquals(1, AbstractThumbnailMaker.getTopBorder(image));
    assertEquals(2, AbstractThumbnailMaker.getBottomBorder(image));
  }
}
