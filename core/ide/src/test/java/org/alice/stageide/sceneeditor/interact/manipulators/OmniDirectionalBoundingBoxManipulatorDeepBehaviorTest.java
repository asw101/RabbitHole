package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

public class OmniDirectionalBoundingBoxManipulatorDeepBehaviorTest {
  @Test
  public void getHorizonPixelLocationRejectsNonVerticalCameraAlignment() {
    assertEquals(-1, OmniDirectionalBoundingBoxManipulatorLogic.getHorizonPixelLocation(0.5, 0.0, 200.0, 4.0, 2.0));
  }

  @Test
  public void resolveOrthographicPickPointLeavesPointAloneWhenHorizonHidden() {
    Point3 pickPoint = new Point3(1, 2, 3);

    assertEquals(pickPoint, OmniDirectionalBoundingBoxManipulatorLogic.resolveOrthographicPickPoint(pickPoint, false));
  }
}
