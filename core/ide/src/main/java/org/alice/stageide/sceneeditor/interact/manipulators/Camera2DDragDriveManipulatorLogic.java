package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.alice.math.immutable.Vector3;

final class Camera2DDragDriveManipulatorLogic {
  private Camera2DDragDriveManipulatorLogic() {
    throw new AssertionError();
  }

  static Vector3 computeRelativeMovementAmount(Vector2 mousePos, Vector2 initialMousePosition, boolean rotationHandle, double minPixelMoveAmount, double worldDistancePerPixelSeconds, double time) {
    double movement = mousePos.minus(initialMousePosition).y();
    if (rotationHandle) {
      if (Math.abs(movement) < minPixelMoveAmount) {
        movement = 0.0d;
      } else if (movement < 0.0d) {
        movement += minPixelMoveAmount;
      }
    }
    return new Vector3(0.0d, 0.0d, movement * worldDistancePerPixelSeconds * time);
  }

  static Vector3 computeRelativeRotationAmount(Vector2 mousePos, Vector2 initialMousePosition, boolean translationHandle, double minPixelMoveAmount, double radiansPerPixelSeconds, double time) {
    double rotation = mousePos.minus(initialMousePosition).x();
    if (translationHandle) {
      if (Math.abs(rotation) < minPixelMoveAmount) {
        rotation = 0.0d;
      } else {
        rotation += rotation < 0.0d ? minPixelMoveAmount : -minPixelMoveAmount;
      }
    }
    return new Vector3(0.0d, -rotation * radiansPerPixelSeconds * time, 0.0d);
  }
}
