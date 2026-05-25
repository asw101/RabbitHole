package org.alice.ide.capture.views;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;

final class ImageCaptureRectangleStencilLogic {
  private static final int WINDOW_OFFSET = 32;

  private ImageCaptureRectangleStencilLogic() {
    throw new AssertionError();
  }

  static Rectangle createHoleOnPress(int x, int y) {
    return new Rectangle(x, y, 0, 0);
  }

  static Rectangle createHoleOnDrag(int xPressed, int yPressed, int x, int y) {
    return new Rectangle(
        Math.min(xPressed, x),
        Math.min(yPressed, y),
        Math.abs(x - xPressed),
        Math.abs(y - yPressed));
  }

  static Rectangle createInvalidHole() {
    return new Rectangle(-1, -1, -1, -1);
  }

  static boolean isHoleValid(Rectangle hole) {
    return hole.width > -1;
  }

  static boolean shouldCapture(Rectangle hole) {
    return isHoleValid(hole) && (hole.width > 0) && (hole.height > 0);
  }

  static Point getWindowLocation(int xScreen, int yScreen) {
    return new Point(xScreen + WINDOW_OFFSET, yScreen + WINDOW_OFFSET);
  }

  static Shape createStencilShape(Shape clip, Rectangle hole) {
    if (hole.width > 0) {
      Area area = new Area(clip);
      area.subtract(new Area(hole));
      return area;
    }
    return clip;
  }

  static Rectangle createHoleOutline(Rectangle hole) {
    return new Rectangle(hole.x - 1, hole.y - 1, hole.width + 1, hole.height + 1);
  }
}
