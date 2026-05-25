package org.alice.stageide.sceneeditor.interact;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CameraNavigatorWidgetHelperTest {
  @Test
  public void createVisibilityStateForCollapsedPerspectiveKeepsOnlyDriverVisible() {
    CameraNavigatorWidgetHelper.VisibilityState state = CameraNavigatorWidgetHelper.createVisibilityState(CameraNavigatorWidget.CameraMode.PERSPECTIVE, false);

    assertFalse(state.isCameraControlUpDownVisible());
    assertFalse(state.isCameraControlStrafeVisible());
    assertTrue(state.isCameraDriverVisible());
    assertFalse(state.isOrthographicStrafeVisible());
    assertFalse(state.isOrthographicZoomVisible());
  }

  @Test
  public void createVisibilityStateForOrthographicIgnoresExpansion() {
    CameraNavigatorWidgetHelper.VisibilityState state = CameraNavigatorWidgetHelper.createVisibilityState(CameraNavigatorWidget.CameraMode.ORTHOGRAPHIC, true);

    assertFalse(state.isCameraControlUpDownVisible());
    assertFalse(state.isCameraControlStrafeVisible());
    assertFalse(state.isCameraDriverVisible());
    assertTrue(state.isOrthographicStrafeVisible());
    assertTrue(state.isOrthographicZoomVisible());
  }

  @Test
  public void getControlSlotsKeepsExistingComponentOrder() {
    assertEquals(List.of(
            CameraNavigatorWidgetHelper.ControlSlot.CAMERA_STRAFE,
            CameraNavigatorWidgetHelper.ControlSlot.CAMERA_DRIVER,
            CameraNavigatorWidgetHelper.ControlSlot.CAMERA_UP_DOWN),
        CameraNavigatorWidgetHelper.getControlSlots(CameraNavigatorWidget.CameraMode.PERSPECTIVE));
    assertEquals(List.of(
            CameraNavigatorWidgetHelper.ControlSlot.ORTHOGRAPHIC_STRAFE,
            CameraNavigatorWidgetHelper.ControlSlot.ORTHOGRAPHIC_ZOOM),
        CameraNavigatorWidgetHelper.getControlSlots(CameraNavigatorWidget.CameraMode.ORTHOGRAPHIC));
  }
}
