package org.alice.stageide.sceneeditor.interact;

import java.util.List;

final class CameraNavigatorWidgetHelper {
  enum ControlSlot {
    CAMERA_STRAFE,
    CAMERA_DRIVER,
    CAMERA_UP_DOWN,
    ORTHOGRAPHIC_STRAFE,
    ORTHOGRAPHIC_ZOOM
  }

  static final class VisibilityState {
    private final boolean cameraControlUpDownVisible;
    private final boolean cameraControlStrafeVisible;
    private final boolean cameraDriverVisible;
    private final boolean orthographicStrafeVisible;
    private final boolean orthographicZoomVisible;

    VisibilityState(boolean cameraControlUpDownVisible, boolean cameraControlStrafeVisible, boolean cameraDriverVisible, boolean orthographicStrafeVisible, boolean orthographicZoomVisible) {
      this.cameraControlUpDownVisible = cameraControlUpDownVisible;
      this.cameraControlStrafeVisible = cameraControlStrafeVisible;
      this.cameraDriverVisible = cameraDriverVisible;
      this.orthographicStrafeVisible = orthographicStrafeVisible;
      this.orthographicZoomVisible = orthographicZoomVisible;
    }

    boolean isCameraControlUpDownVisible() {
      return this.cameraControlUpDownVisible;
    }

    boolean isCameraControlStrafeVisible() {
      return this.cameraControlStrafeVisible;
    }

    boolean isCameraDriverVisible() {
      return this.cameraDriverVisible;
    }

    boolean isOrthographicStrafeVisible() {
      return this.orthographicStrafeVisible;
    }

    boolean isOrthographicZoomVisible() {
      return this.orthographicZoomVisible;
    }
  }

  private CameraNavigatorWidgetHelper() {
    throw new AssertionError();
  }

  static VisibilityState createVisibilityState(CameraNavigatorWidget.CameraMode mode, boolean isExpanded) {
    return switch (mode) {
      case PERSPECTIVE -> new VisibilityState(isExpanded, isExpanded, true, false, false);
      case ORTHOGRAPHIC -> new VisibilityState(false, false, false, true, true);
    };
  }

  static List<ControlSlot> getControlSlots(CameraNavigatorWidget.CameraMode mode) {
    return switch (mode) {
      case PERSPECTIVE -> List.of(ControlSlot.CAMERA_STRAFE, ControlSlot.CAMERA_DRIVER, ControlSlot.CAMERA_UP_DOWN);
      case ORTHOGRAPHIC -> List.of(ControlSlot.ORTHOGRAPHIC_STRAFE, ControlSlot.ORTHOGRAPHIC_ZOOM);
    };
  }
}
