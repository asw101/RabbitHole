package org.alice.stageide.sceneeditor;

import org.junit.Test;

import java.util.Arrays;

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

  @Test
  public void perspectiveViews_precedeOrthographicViews() {
    assertTrue(CameraOption.STARTING_CAMERA_VIEW.compareTo(CameraOption.TOP) < 0);
    assertTrue(CameraOption.LAYOUT_SCENE_VIEW.compareTo(CameraOption.TOP) < 0);
  }

  @Test
  public void orthographicViews_areContiguousAtEnd() {
    CameraOption[] values = CameraOption.values();
    assertArrayEquals(new CameraOption[]{CameraOption.TOP, CameraOption.SIDE, CameraOption.FRONT},
        Arrays.copyOfRange(values, 2, values.length));
  }
}
