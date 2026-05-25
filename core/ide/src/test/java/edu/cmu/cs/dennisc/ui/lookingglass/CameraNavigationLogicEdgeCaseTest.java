package edu.cmu.cs.dennisc.ui.lookingglass;

import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CameraNavigationLogicEdgeCaseTest {
  @Test
  public void clampDistanceHonorsBothBoundsAndPreservesInRangeValues() {
    assertEquals(1.0, CameraNavigationFunctionLogic.clampDistance(-5.0, 1.0, 10.0), 0.00001);
    assertEquals(10.0, CameraNavigationFunctionLogic.clampDistance(25.0, 1.0, 10.0), 0.00001);
    assertEquals(4.5, CameraNavigationFunctionLogic.clampDistance(4.5, 1.0, 10.0), 0.00001);
  }

  @Test
  public void requestDirectionTreatsZeroAsNonPositiveAndCoversRemainingNegativeBranch() {
    assertEquals(8.0, CameraNavigationFunctionLogic.requestDirection(0.0, -1.0, 4.0, -8.0), 0.00001);
    assertEquals(-4.0, CameraNavigationFunctionLogic.requestDirection(-2.0, -1.0, 4.0, -8.0), 0.00001);
  }

  @Test
  public void updateTranslationClampsNegativeHeightAndCancelsOpposingDirections() {
    Point3 updated = CameraNavigationFunctionLogic.updateTranslation(new Point3(2.0, -3.0, 5.0), true, true, true, true, 1.5);

    assertEquals(2.0, updated.x(), 0.00001);
    assertEquals(0.0, updated.y(), 0.00001);
    assertEquals(5.0, updated.z(), 0.00001);
  }

  @Test
  public void heightAndPitchHandleZeroAndPositiveDistances() {
    assertEquals(0.0, CameraNavigationFunctionLogic.getHeight(0.0), 0.00001);
    assertEquals(4.0, CameraNavigationFunctionLogic.getHeight(20.0), 0.00001);
    assertEquals(Math.PI / 4.0, CameraNavigationFunctionLogic.getPitchMinimum(1.0, 1.0), 0.00001);
  }

  @Test
  public void calculateArrowThetaHandlesOtherQuadrantsAndOrigin() {
    assertEquals(Math.PI, CameraNavigationDragAdapterLogic.calculateArrowTheta(-1, 0), 0.00001);
    assertEquals(-Math.PI / 2.0, CameraNavigationDragAdapterLogic.calculateArrowTheta(0, -5), 0.00001);
    assertEquals(0.0, CameraNavigationDragAdapterLogic.calculateArrowTheta(0, 0), 0.00001);
  }
}
