package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector3;

final class Camera2DDragManipulatorLogic {
  private Camera2DDragManipulatorLogic() {
    throw new AssertionError();
  }

  static double clampTime(double time, double minTime, double maxTime) {
    if (time < minTime) {
      return minTime;
    }
    if (time > maxTime) {
      return maxTime;
    }
    return time;
  }

  static Vector3 computeTotalMovementAmount(Vector3 relativeMovementAmount, Vector3 initialMoveFactor, double worldDistancePerPixelSeconds, double time) {
    return relativeMovementAmount.plus(initialMoveFactor.times(worldDistancePerPixelSeconds * time));
  }

  static Vector3 computeTotalRotationAmount(Vector3 relativeRotationAmount, Vector3 initialRotateFactor, double radiansPerPixelSeconds, double time) {
    return relativeRotationAmount.plus(initialRotateFactor.times(radiansPerPixelSeconds * time));
  }
}
