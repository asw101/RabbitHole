package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Camera2DDragManipulatorLogicTest {
  @Test
  public void clampTimeRestrictsValuesToManipulatorWindow() {
    assertEquals(0.001d, Camera2DDragManipulatorLogic.clampTime(0.0d, 0.001d, 0.1d), 0.00001d);
    assertEquals(0.05d, Camera2DDragManipulatorLogic.clampTime(0.05d, 0.001d, 0.1d), 0.00001d);
    assertEquals(0.1d, Camera2DDragManipulatorLogic.clampTime(0.2d, 0.001d, 0.1d), 0.00001d);
  }

  @Test
  public void computeTotalMovementAmountAddsInitialImpulseToMouseMovement() {
    Vector3 total = Camera2DDragManipulatorLogic.computeTotalMovementAmount(new Vector3(1.0d, -2.0d, 3.0d), new Vector3(4.0d, 5.0d, 6.0d), 0.5d, 2.0d);

    assertEquals(5.0d, total.x(), 0.00001d);
    assertEquals(3.0d, total.y(), 0.00001d);
    assertEquals(9.0d, total.z(), 0.00001d);
  }

  @Test
  public void computeTotalRotationAmountAddsInitialRotationImpulse() {
    Vector3 total = Camera2DDragManipulatorLogic.computeTotalRotationAmount(new Vector3(0.2d, 0.3d, -0.4d), new Vector3(1.0d, -2.0d, 3.0d), 0.25d, 2.0d);

    assertEquals(0.7d, total.x(), 0.00001d);
    assertEquals(-0.7d, total.y(), 0.00001d);
    assertEquals(1.1d, total.z(), 0.00001d);
  }
}
