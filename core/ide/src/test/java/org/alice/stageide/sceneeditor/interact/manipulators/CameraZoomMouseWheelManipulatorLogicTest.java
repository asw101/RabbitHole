package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

public class CameraZoomMouseWheelManipulatorLogicTest {
  @Test
  public void computeIdealBackwardYUsesLinearRampForPositiveX() {
    double y = CameraZoomMouseWheelManipulatorLogic.computeIdealBackwardY(2.0, false, 4.0, 1.5, 0.5, 1.0, -0.2);

    assertEquals(2.0, y, 0.00001);
  }

  @Test
  public void computeIdealBackwardYClampsToLowDownAmount() {
    double y = CameraZoomMouseWheelManipulatorLogic.computeIdealBackwardY(-0.1, true, 2.0, 1.0, 0.5, 1.0, -0.3);

    assertTrue(y >= -0.3);
  }

  @Test
  public void computeHeightForXUsesCosineCurveBeforeInflection() {
    double height = CameraZoomMouseWheelManipulatorLogic.computeHeightForX(-1.0, true, 2.0, 1.0, 3.0, 1.5, 0.5, 2.0);

    assertEquals(4.0, height, 0.00001);
  }

  @Test
  public void interpolateNormalizedVectorReturnsUnitVector() {
    Vector3 result = CameraZoomMouseWheelManipulatorLogic.interpolateNormalizedVector(new Vector3(1, 0, 0), new Vector3(0, 1, 0), 0.5);

    assertEquals(1.0, result.magnitude(), 0.00001);
    assertTrue(result.x() > 0.0);
    assertTrue(result.y() > 0.0);
  }
}
