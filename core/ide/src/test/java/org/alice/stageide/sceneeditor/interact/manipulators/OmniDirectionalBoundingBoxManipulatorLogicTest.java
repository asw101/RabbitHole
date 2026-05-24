package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Point3;
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
}
