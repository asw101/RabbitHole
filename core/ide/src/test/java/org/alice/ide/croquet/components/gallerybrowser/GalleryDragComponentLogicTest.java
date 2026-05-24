package org.alice.ide.croquet.components.gallerybrowser;

import org.junit.Test;

import java.awt.Dimension;
import java.awt.Insets;

import static org.junit.Assert.*;

public class GalleryDragComponentLogicTest {
  @Test
  public void buildSuperclassToolTipTextStripsResourceSuffix() {
    assertEquals("superclass: Biped", GalleryDragComponentLogic.buildSuperclassToolTipText("BipedResource"));
  }

  @Test
  public void computePreferredLayoutSizeAddsInsets() {
    assertEquals(new Dimension(14, 10), GalleryDragComponentLogic.computePreferredLayoutSize(new Dimension(10, 4), new Insets(1, 2, 5, 2)));
  }
}
