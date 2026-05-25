package org.alice.stageide.sceneeditor.viewmanager;

import org.alice.stageide.sceneeditor.CameraOption;
import org.lgna.story.Color;

final class MarkerUtilitiesHelper {
  private MarkerUtilitiesHelper() {
    throw new AssertionError();
  }

  static int getColorIndex(Color[] colors, Color color) {
    for (int i = 0; i < colors.length; i++) {
      if (colors[i].equals(color)) {
        return i;
      }
    }
    return -1;
  }

  static String getColorFileName(String[] colorNameKeys, Color[] colors, Color color) {
    int index = getColorIndex(colors, color);
    if (index != -1) {
      String colorName = colorNameKeys[index];
      return colorName.substring(0, 1).toUpperCase() + colorName.substring(1);
    }
    return "White";
  }

  static String getCameraViewKey(CameraOption cameraOption) {
    return switch (cameraOption) {
      case STARTING_CAMERA_VIEW -> "sceneCameraView";
      case LAYOUT_SCENE_VIEW -> "layoutPerspectiveView";
      case TOP -> "topOrthographicView";
      case SIDE -> "sideOrthographicView";
      case FRONT -> "frontOrthographicView";
    };
  }
}
