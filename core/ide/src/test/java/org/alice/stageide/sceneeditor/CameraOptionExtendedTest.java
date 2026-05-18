package org.alice.stageide.sceneeditor;

import org.junit.Test;

import static org.junit.Assert.*;

public class CameraOptionExtendedTest {

  @Test
  public void values_orderMatchesDeclaration() {
    assertArrayEquals(new CameraOption[]{
            CameraOption.STARTING_CAMERA_VIEW,
            CameraOption.LAYOUT_SCENE_VIEW,
            CameraOption.TOP,
            CameraOption.SIDE,
            CameraOption.FRONT
        },
        CameraOption.values());
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalid_throws() {
    CameraOption.valueOf("INVALID");
  }
}
