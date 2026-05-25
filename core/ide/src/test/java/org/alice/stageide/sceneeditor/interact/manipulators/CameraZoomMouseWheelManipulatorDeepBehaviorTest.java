package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

public class CameraZoomMouseWheelManipulatorDeepBehaviorTest {
  @Test
  public void computeHeightForXReturnsLowHeightPastUpCurveLimit() {
    assertEquals(1.5,
        CameraZoomMouseWheelManipulatorLogic.computeHeightForX(-5.0, true, 2.0, 1.0, 3.0, 1.5, 0.5, 2.0),
        0.00001);
  }

  @Test
  public void interpolateNormalizedVectorHonorsEndpoints() {
    Vector3 start = new Vector3(2, 0, 0);
    Vector3 end = new Vector3(0, 0, 5);

    Vector3 atStart = CameraZoomMouseWheelManipulatorLogic.interpolateNormalizedVector(start, end, 0.0);
    Vector3 atEnd = CameraZoomMouseWheelManipulatorLogic.interpolateNormalizedVector(start, end, 1.0);

    assertEquals(1.0, atStart.x(), 0.00001);
    assertEquals(0.0, atStart.z(), 0.00001);
    assertEquals(0.0, atEnd.x(), 0.00001);
    assertEquals(1.0, atEnd.z(), 0.00001);
  }
}
