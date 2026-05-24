package org.alice.ide.croquet.components.gallerybrowser;

import java.awt.Dimension;
import java.awt.Insets;

final class GalleryDragComponentLogic {
  private GalleryDragComponentLogic() {
    throw new AssertionError();
  }

  static String buildSuperclassToolTipText(String simpleName) {
    String normalizedName = simpleName.endsWith("Resource") ? simpleName.substring(0, simpleName.length() - "Resource".length()) : simpleName;
    return "superclass: " + normalizedName;
  }

  static Dimension computePreferredLayoutSize(Dimension baseSize, Insets insets) {
    return new Dimension(baseSize.width + insets.left + insets.right, baseSize.height + insets.top + insets.bottom);
  }
}
