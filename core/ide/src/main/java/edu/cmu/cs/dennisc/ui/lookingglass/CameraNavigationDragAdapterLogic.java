package edu.cmu.cs.dennisc.ui.lookingglass;

final class CameraNavigationDragAdapterLogic {
  static final class MouseDragPlan {
    final boolean orbit;
    final double orbitYaw;
    final double orbitPitch;
    final double velocityX;
    final double velocityY;
    final double velocityZ;

    private MouseDragPlan(boolean orbit, double orbitYaw, double orbitPitch,
                          double velocityX, double velocityY, double velocityZ) {
      this.orbit = orbit;
      this.orbitYaw = orbitYaw;
      this.orbitPitch = orbitPitch;
      this.velocityX = velocityX;
      this.velocityY = velocityY;
      this.velocityZ = velocityZ;
    }
  }

  private CameraNavigationDragAdapterLogic() {
    throw new AssertionError();
  }

  static double calculateArrowTheta(int xPixelDelta, int yPixelDelta) {
    return Math.atan2(yPixelDelta, xPixelDelta);
  }

  static MouseDragPlan createMouseDragPlan(CameraNavigationMode mode, int xPixel0, int yPixel0,
                                           int xPixelPrev, int yPixelPrev, int xPixel, int yPixel) {
    final double translationXzFactor = 0.05;
    final double translationYFactor = 0.05;
    final double orbitYawFactor = 0.02;
    final double orbitPitchFactor = 0.002;
    if (mode == CameraNavigationMode.ORBIT) {
      int xPixelDelta = xPixel - xPixelPrev;
      int yPixelDelta = yPixel - yPixelPrev;
      return new MouseDragPlan(true, xPixelDelta * orbitYawFactor, -yPixelDelta * orbitPitchFactor, 0.0, 0.0, 0.0);
    }
    if (mode == CameraNavigationMode.TRANSLATE_XZ) {
      return new MouseDragPlan(false, 0.0, 0.0,
          (xPixel - xPixel0) * translationXzFactor,
          0.0,
          (yPixel - yPixel0) * translationXzFactor);
    }
    if (mode == CameraNavigationMode.TRANSLATE_Y) {
      return new MouseDragPlan(false, 0.0, 0.0,
          0.0,
          -((yPixel - yPixel0) * translationYFactor),
          0.0);
    }
    return new MouseDragPlan(false, 0.0, 0.0, 0.0, 0.0, 0.0);
  }

  static CameraNavigationMode resolveNavigationMode(boolean isControlDown, boolean isShiftDown) {
    if (isControlDown) {
      return isShiftDown ? null : CameraNavigationMode.ORBIT;
    }
    return isShiftDown ? CameraNavigationMode.TRANSLATE_Y : CameraNavigationMode.TRANSLATE_XZ;
  }
}
