package edu.cmu.cs.dennisc.ui.lookingglass;

import org.junit.Test;

import static org.junit.Assert.*;

public class CameraNavigationDragAdapterLogicTest {
  @Test
  public void resolveNavigationModeMatchesModifierKeys() {
    assertEquals(CameraNavigationMode.TRANSLATE_XZ, CameraNavigationDragAdapterLogic.resolveNavigationMode(false, false));
    assertEquals(CameraNavigationMode.TRANSLATE_Y, CameraNavigationDragAdapterLogic.resolveNavigationMode(false, true));
    assertEquals(CameraNavigationMode.ORBIT, CameraNavigationDragAdapterLogic.resolveNavigationMode(true, false));
    assertNull(CameraNavigationDragAdapterLogic.resolveNavigationMode(true, true));
  }

  @Test
  public void calculateArrowThetaUsesAtan2() {
    assertEquals(Math.PI / 2.0, CameraNavigationDragAdapterLogic.calculateArrowTheta(0, 5), 0.00001);
  }

  @Test
  public void createMouseDragPlanBuildsOrbitAndTranslationRequests() {
    CameraNavigationDragAdapterLogic.MouseDragPlan orbitPlan =
        CameraNavigationDragAdapterLogic.createMouseDragPlan(CameraNavigationMode.ORBIT, 10, 20, 12, 24, 18, 30);
    assertTrue(orbitPlan.orbit);
    assertEquals(0.12, orbitPlan.orbitYaw, 0.00001);
    assertEquals(-0.012, orbitPlan.orbitPitch, 0.00001);

    CameraNavigationDragAdapterLogic.MouseDragPlan translatePlan =
        CameraNavigationDragAdapterLogic.createMouseDragPlan(CameraNavigationMode.TRANSLATE_Y, 10, 20, 10, 20, 10, 30);
    assertFalse(translatePlan.orbit);
    assertEquals(-0.5, translatePlan.velocityY, 0.00001);
  }
}
