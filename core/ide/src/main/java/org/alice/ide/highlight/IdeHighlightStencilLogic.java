package org.alice.ide.highlight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

final class IdeHighlightStencilLogic {
  private IdeHighlightStencilLogic() {
    throw new AssertionError();
  }

  static TexturePaint createStencilPaint(int width, int height, Color baseColor, Color lineColor) {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) image.getGraphics();
    g2.setColor(baseColor);
    g2.fillRect(0, 0, width, height);
    g2.setColor(lineColor);
    g2.drawLine(0, height, width, 0);
    g2.fillRect(0, 0, 1, 1);
    g2.dispose();
    return new TexturePaint(image, new Rectangle(0, 0, width, height));
  }

  static Area subtractFeatureArea(Area area, Shape featureAreaToSubtract) {
    if (featureAreaToSubtract != null) {
      area.subtract(new Area(featureAreaToSubtract));
    }
    return area;
  }
}
