package org.alice.stageide.sceneeditor.interact.manipulators;

import org.junit.Test;

import java.awt.Point;

import static org.junit.Assert.assertEquals;

public class ResizeDragManipulatorLogicTest {
  @Test
  public void computeScaleAmountCombinesHorizontalAndInvertedVerticalDrag() {
    double scaleAmount = ResizeDragManipulatorLogic.computeScaleAmount(new Point(130, 80), new Point(100, 100), 0.005d);

    assertEquals(0.25d, scaleAmount, 0.00001d);
  }

  @Test
  public void computeAccumulatedScaleClampsToMinimumScale() {
    assertEquals(0.1d, ResizeDragManipulatorLogic.computeAccumulatedScale(0.5d, -1.0d, 0.1d), 0.00001d);
    assertEquals(0.8d, ResizeDragManipulatorLogic.computeAccumulatedScale(0.5d, 0.3d, 0.1d), 0.00001d);
  }
}
