package edu.cmu.cs.dennisc.ui.lookingglass;

final class CameraNavigationDragAdapterLogic {
  private CameraNavigationDragAdapterLogic() {
    throw new AssertionError();
  }

  static double calculateArrowTheta(int xPixelDelta, int yPixelDelta) {
    return Math.atan2(yPixelDelta, xPixelDelta);
  }

  static CameraNavigationMode resolveNavigationMode(boolean isControlDown, boolean isShiftDown) {
    if (isControlDown) {
      return isShiftDown ? null : CameraNavigationMode.ORBIT;
    }
    return isShiftDown ? CameraNavigationMode.TRANSLATE_Y : CameraNavigationMode.TRANSLATE_XZ;
  }
}
