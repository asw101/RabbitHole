package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Camera2DDragUpDownRotateManipulatorLogicTest {
  @Test
  public void computeRelativeRotationAmountUsesVerticalMouseDelta() {
    Vector3 amount = Camera2DDragUpDownRotateManipulatorLogic.computeRelativeRotationAmount(new Vector2(20.0d, 35.0d), new Vector2(5.0d, 15.0d), 0.02d, 0.5d);

    assertEquals(0.2d, amount.x(), 0.00001d);
    assertEquals(0.0d, amount.y(), 0.00001d);
    assertEquals(0.0d, amount.z(), 0.00001d);
  }
}
