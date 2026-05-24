package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.alice.math.immutable.Vector3;

final class Camera2DDragUpDownRotateManipulatorLogic {
  private Camera2DDragUpDownRotateManipulatorLogic() {
    throw new AssertionError();
  }

  static Vector3 computeRelativeRotationAmount(Vector2 mousePos, Vector2 initialMousePosition, double radiansPerPixelSeconds, double time) {
    double amountToRotateX = mousePos.minus(initialMousePosition).y() * radiansPerPixelSeconds * time;
    return new Vector3(amountToRotateX, 0.0d, 0.0d);
  }
}
