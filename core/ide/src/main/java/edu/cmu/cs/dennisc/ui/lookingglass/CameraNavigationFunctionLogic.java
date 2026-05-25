package edu.cmu.cs.dennisc.ui.lookingglass;

import org.alice.math.immutable.Point3;

final class CameraNavigationFunctionLogic {
  private CameraNavigationFunctionLogic() {
    throw new AssertionError();
  }

  static double clampDistance(double distance, double minimum, double maximum) {
    return Math.max(Math.min(distance, maximum), minimum);
  }

  static double requestDirection(double requested, double current, double accelerationForce, double decelerationForce) {
    if (requested > 0) {
      return requested > current ? accelerationForce : decelerationForce;
    } else {
      return requested > current ? -decelerationForce : -accelerationForce;
    }
  }

  static Point3 updateTranslation(Point3 translation, boolean isForwardKeyPressed, boolean isBackwardKeyPressed,
                                  boolean isLeftKeyPressed, boolean isRightKeyPressed, double delta) {
    double y = Math.max(translation.y(), 0);
    double z = translation.z();
    if (isForwardKeyPressed) {
      z -= delta;
    }
    if (isBackwardKeyPressed) {
      z += delta;
    }
    double x = translation.x();
    if (isLeftKeyPressed) {
      x -= delta;
    }
    if (isRightKeyPressed) {
      x += delta;
    }
    return new Point3(x, y, z);
  }

  static double getHeight(double distance) {
    double d = distance * 0.1;
    return d * d;
  }

  static double getPitchMinimum(double height, double distance) {
    return Math.atan2(height, distance);
  }
}
