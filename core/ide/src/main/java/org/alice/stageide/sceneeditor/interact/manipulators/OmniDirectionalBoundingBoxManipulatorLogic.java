package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Point3;

final class OmniDirectionalBoundingBoxManipulatorLogic {
  private OmniDirectionalBoundingBoxManipulatorLogic() {
    throw new AssertionError();
  }

  static int getHorizonPixelLocation(double dotProductWithWorldUp, double cameraY, double surfaceHeight, double planeHeight, double planeYMaximum) {
    if ((dotProductWithWorldUp == 1) || (dotProductWithWorldUp == -1)) {
      double yRatio = surfaceHeight / planeHeight;
      double horizonInCameraSpace = -cameraY;
      double distanceFromMaxY = planeYMaximum - horizonInCameraSpace;
      return (int) (yRatio * distanceFromMaxY);
    }
    return -1;
  }

  static boolean isHorizonInView(int horizonLinePixelVal, double surfaceHeight) {
    return (horizonLinePixelVal >= 0) && (horizonLinePixelVal <= surfaceHeight);
  }

  static Point3 resolveOrthographicPickPoint(Point3 pickPoint, boolean horizonInView) {
    if (pickPoint == null) {
      return Point3.ORIGIN;
    }
    return horizonInView ? pickPoint.withY(0) : pickPoint;
  }
}
