package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertEquals;

public class ManipulatorLogicEdgeCaseTest {
  @Test
  public void computeRelativeRotationAmountIgnoresHorizontalDeltaAndSupportsNegativeMovement() {
    Vector3 amount = Camera2DDragUpDownRotateManipulatorLogic.computeRelativeRotationAmount(
        new Vector2(100.0, 10.0), new Vector2(0.0, 20.0), 0.5, 2.0);

    assertEquals(-10.0, amount.x(), 0.00001);
    assertEquals(0.0, amount.y(), 0.00001);
    assertEquals(0.0, amount.z(), 0.00001);
  }

  @Test
  public void computeRelativeRotationAmountReturnsZeroWhenTimeIsZero() {
    Vector3 amount = Camera2DDragUpDownRotateManipulatorLogic.computeRelativeRotationAmount(
        new Vector2(20.0, 35.0), new Vector2(5.0, 15.0), 0.02, 0.0);

    assertEquals(0.0, amount.x(), 0.00001);
    assertEquals(0.0, amount.y(), 0.00001);
    assertEquals(0.0, amount.z(), 0.00001);
  }

  @Test
  public void computeScaleAmountSupportsShrinkingAndNoMovement() {
    assertEquals(-0.2, ResizeDragManipulatorLogic.computeScaleAmount(new Point(80, 120), new Point(100, 100), 0.005), 0.00001);
    assertEquals(0.0, ResizeDragManipulatorLogic.computeScaleAmount(new Point(100, 100), new Point(100, 100), 0.005), 0.00001);
  }

  @Test
  public void computeAccumulatedScalePreservesExactMinimumAndLargerValues() {
    assertEquals(0.3, ResizeDragManipulatorLogic.computeAccumulatedScale(0.5, -0.2, 0.3), 0.00001);
    assertEquals(1.2, ResizeDragManipulatorLogic.computeAccumulatedScale(1.0, 0.2, 0.3), 0.00001);
  }
}
