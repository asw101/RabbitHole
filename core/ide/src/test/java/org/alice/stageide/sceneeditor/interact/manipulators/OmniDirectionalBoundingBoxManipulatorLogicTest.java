package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

public class OmniDirectionalBoundingBoxManipulatorLogicTest {
  @Test
  public void getHorizonPixelLocationUsesCameraAndPlaneMetrics() {
    assertEquals(100, OmniDirectionalBoundingBoxManipulatorLogic.getHorizonPixelLocation(1.0, 0.0, 200.0, 4.0, 2.0));
  }

  @Test
  public void isHorizonInViewChecksBounds() {
    assertTrue(OmniDirectionalBoundingBoxManipulatorLogic.isHorizonInView(10, 20.0));
    assertFalse(OmniDirectionalBoundingBoxManipulatorLogic.isHorizonInView(25, 20.0));
  }

  @Test
  public void resolveOrthographicPickPointClampsToGroundWhenVisible() {
    assertEquals(new Point3(1, 0, 3), OmniDirectionalBoundingBoxManipulatorLogic.resolveOrthographicPickPoint(new Point3(1, 2, 3), true));
    assertEquals(Point3.ORIGIN, OmniDirectionalBoundingBoxManipulatorLogic.resolveOrthographicPickPoint(null, false));
  }

  @Test
  public void computeOrthographicMovementVectorAppliesGroundClampAndOffset() {
    Vector3 movement = OmniDirectionalBoundingBoxManipulatorLogic.computeOrthographicMovementVector(new Point3(2, 3, 4), true, new Vector3(0.5, 1.0, -1.0), new Point3(1.5, 0.0, 1.0));

    assertEquals(1.0d, movement.x(), 0.00001d);
    assertEquals(1.0d, movement.y(), 0.00001d);
    assertEquals(2.0d, movement.z(), 0.00001d);
  }
}
