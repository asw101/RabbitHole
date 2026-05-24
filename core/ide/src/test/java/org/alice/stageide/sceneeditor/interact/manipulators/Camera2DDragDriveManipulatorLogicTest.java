package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Camera2DDragDriveManipulatorLogicTest {
  @Test
  public void computeRelativeMovementAmountIgnoresSmallMovementOnRotationHandles() {
    Vector3 amount = Camera2DDragDriveManipulatorLogic.computeRelativeMovementAmount(new Vector2(0.0d, 12.0d), new Vector2(0.0d, 10.0d), true, 5.0d, 0.08d, 1.0d);

    assertEquals(Vector3.ZERO, amount);
  }

  @Test
  public void computeRelativeMovementAmountOffsetsNegativeMovementOnRotationHandles() {
    Vector3 amount = Camera2DDragDriveManipulatorLogic.computeRelativeMovementAmount(new Vector2(0.0d, 0.0d), new Vector2(0.0d, 10.0d), true, 5.0d, 0.08d, 2.0d);

    assertEquals(0.0d, amount.x(), 0.00001d);
    assertEquals(0.0d, amount.y(), 0.00001d);
    assertEquals(-0.8d, amount.z(), 0.00001d);
  }

  @Test
  public void computeRelativeRotationAmountQuantizesTranslationHandleMotion() {
    Vector3 amount = Camera2DDragDriveManipulatorLogic.computeRelativeRotationAmount(new Vector2(30.0d, 0.0d), new Vector2(10.0d, 0.0d), true, 5.0d, 0.02d, 2.0d);

    assertEquals(0.0d, amount.x(), 0.00001d);
    assertEquals(-0.6d, amount.y(), 0.00001d);
    assertEquals(0.0d, amount.z(), 0.00001d);
  }
}
