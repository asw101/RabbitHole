package org.alice.stageide.sceneeditor.viewmanager;

import org.alice.stageide.sceneeditor.CameraOption;
import org.junit.Test;
import org.lgna.story.Color;

import static org.junit.Assert.assertEquals;

public class MarkerUtilitiesHelperTest {
  private static final String[] COLOR_NAMES = {"red", "green"};
  private static final Color[] COLORS = {Color.RED, Color.GREEN};

  @Test
  public void getColorFileNameReturnsCapitalizedKnownColor() {
    assertEquals("Green", MarkerUtilitiesHelper.getColorFileName(COLOR_NAMES, COLORS, Color.GREEN));
  }

  @Test
  public void getColorFileNameFallsBackToWhiteForUnknownColor() {
    assertEquals("White", MarkerUtilitiesHelper.getColorFileName(COLOR_NAMES, COLORS, Color.BLUE));
  }

  @Test
  public void getCameraViewKeyMatchesResourceBundleKeys() {
    assertEquals("sceneCameraView", MarkerUtilitiesHelper.getCameraViewKey(CameraOption.STARTING_CAMERA_VIEW));
    assertEquals("frontOrthographicView", MarkerUtilitiesHelper.getCameraViewKey(CameraOption.FRONT));
  }
}
