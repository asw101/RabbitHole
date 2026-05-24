package org.alice.stageide.sceneeditor.interact.manipulators;

import java.awt.Point;

final class ResizeDragManipulatorLogic {
  private ResizeDragManipulatorLogic() {
    throw new AssertionError();
  }

  static double computeScaleAmount(Point currentPoint, Point initialPoint, double resizeScale) {
    int xDifference = currentPoint.x - initialPoint.x;
    int yDifference = -(currentPoint.y - initialPoint.y);
    return (xDifference + yDifference) * resizeScale;
  }

  static double computeAccumulatedScale(double initialScale, double scaleAmount, double minScale) {
    return Math.max(initialScale + scaleAmount, minScale);
  }
}
