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
}
