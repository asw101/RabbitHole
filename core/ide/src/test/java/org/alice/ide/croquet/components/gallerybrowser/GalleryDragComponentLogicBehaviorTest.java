package org.alice.ide.croquet.components.gallerybrowser;

import org.junit.Test;

import java.awt.Dimension;
import java.awt.Insets;

import static org.junit.Assert.assertEquals;

public class GalleryDragComponentLogicBehaviorTest {
  @Test
  public void buildSuperclassToolTipTextStripsOnlyTrailingResourceSuffix() {
    assertEquals("superclass: Biped", GalleryDragComponentLogic.buildSuperclassToolTipText("BipedResource"));
    assertEquals("superclass: ResourcefulBiped", GalleryDragComponentLogic.buildSuperclassToolTipText("ResourcefulBiped"));
  }

  @Test
  public void computePreferredLayoutSizeIncludesInsetsOnEachSide() {
    assertEquals(new Dimension(14, 10),
        GalleryDragComponentLogic.computePreferredLayoutSize(new Dimension(10, 4), new Insets(1, 2, 5, 2)));
  }
}
