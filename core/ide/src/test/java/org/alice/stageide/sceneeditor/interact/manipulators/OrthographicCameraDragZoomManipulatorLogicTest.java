package org.alice.stageide.sceneeditor.interact.manipulators;

import org.alice.math.immutable.Vector2;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.assertEquals;

public class OrthographicCameraDragZoomManipulatorLogicTest {
  @Test
  public void getZoomValueForColorUsesDirectionColors() {
    assertEquals(-0.1d, OrthographicCameraDragZoomManipulatorLogic.getZoomValueForColor(Color.RED, Color.RED, Color.GREEN, 0.1d), 0.00001d);
    assertEquals(0.1d, OrthographicCameraDragZoomManipulatorLogic.getZoomValueForColor(Color.GREEN, Color.RED, Color.GREEN, 0.1d), 0.00001d);
    assertEquals(0.0d, OrthographicCameraDragZoomManipulatorLogic.getZoomValueForColor(Color.BLUE, Color.RED, Color.GREEN, 0.1d), 0.00001d);
  }

  @Test
  public void computeTotalZoomAmountCombinesMouseTravelAndInitialImpulse() {
    double total = OrthographicCameraDragZoomManipulatorLogic.computeTotalZoomAmount(new Vector2(10.0d, 25.0d), new Vector2(4.0d, 5.0d), -0.1d, 0.1d, 2.0d);

    assertEquals(3.98d, total, 0.00001d);
  }

  @Test
  public void computeNextZoomPreservesCurrentZoomWhenContinuingPastLimit() {
    assertEquals(74.0d, OrthographicCameraDragZoomManipulatorLogic.computeNextZoom(74.0d, 2.0d, 0.01d, 75.0d), 0.00001d);
    assertEquals(0.5d, OrthographicCameraDragZoomManipulatorLogic.computeNextZoom(0.5d, -1.0d, 0.01d, 75.0d), 0.00001d);
  }

  @Test
  public void computeNextZoomAppliesZoomWithinBounds() {
    assertEquals(10.5d, OrthographicCameraDragZoomManipulatorLogic.computeNextZoom(10.0d, 0.5d, 0.01d, 75.0d), 0.00001d);
  }
}
