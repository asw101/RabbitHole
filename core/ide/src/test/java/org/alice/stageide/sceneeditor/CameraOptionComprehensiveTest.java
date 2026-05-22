package org.alice.stageide.sceneeditor;

import org.junit.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CameraOptionComprehensiveTest {

  @Test
  public void enumContainsFiveValues() {
    assertEquals(5, CameraOption.values().length);
  }

  @Test
  public void valuesAppearInDeclaredOrder() {
    assertArrayEquals(new CameraOption[]{
        CameraOption.STARTING_CAMERA_VIEW,
        CameraOption.LAYOUT_SCENE_VIEW,
        CameraOption.TOP,
        CameraOption.SIDE,
        CameraOption.FRONT
    }, CameraOption.values());
  }

  @Test
  public void enumNamesMatchExpectedConstants() {
    List<String> names = Arrays.stream(CameraOption.values()).map(Enum::name).collect(Collectors.toList());
    assertEquals(Arrays.asList("STARTING_CAMERA_VIEW", "LAYOUT_SCENE_VIEW", "TOP", "SIDE", "FRONT"), names);
  }

  @Test
  public void valueOfReturnsStartingCameraView() {
    assertSame(CameraOption.STARTING_CAMERA_VIEW, CameraOption.valueOf("STARTING_CAMERA_VIEW"));
  }

  @Test
  public void valueOfReturnsLayoutSceneView() {
    assertSame(CameraOption.LAYOUT_SCENE_VIEW, CameraOption.valueOf("LAYOUT_SCENE_VIEW"));
  }

  @Test
  public void valueOfReturnsTop() {
    assertSame(CameraOption.TOP, CameraOption.valueOf("TOP"));
  }

  @Test
  public void valueOfReturnsSide() {
    assertSame(CameraOption.SIDE, CameraOption.valueOf("SIDE"));
  }

  @Test
  public void valueOfReturnsFront() {
    assertSame(CameraOption.FRONT, CameraOption.valueOf("FRONT"));
  }

  @Test
  public void ordinalsMatchDeclarationOrder() {
    assertEquals(0, CameraOption.STARTING_CAMERA_VIEW.ordinal());
    assertEquals(1, CameraOption.LAYOUT_SCENE_VIEW.ordinal());
    assertEquals(2, CameraOption.TOP.ordinal());
    assertEquals(3, CameraOption.SIDE.ordinal());
    assertEquals(4, CameraOption.FRONT.ordinal());
  }

  @Test
  public void startingCameraComesBeforeLayoutCamera() {
    assertTrue(CameraOption.STARTING_CAMERA_VIEW.compareTo(CameraOption.LAYOUT_SCENE_VIEW) < 0);
  }

  @Test
  public void perspectiveOptionsPrecedeOrthographicOptions() {
    assertTrue(CameraOption.STARTING_CAMERA_VIEW.compareTo(CameraOption.TOP) < 0);
    assertTrue(CameraOption.LAYOUT_SCENE_VIEW.compareTo(CameraOption.FRONT) < 0);
  }

  @Test
  public void orthographicOptionsAreLastThreeValues() {
    CameraOption[] values = CameraOption.values();
    assertArrayEquals(new CameraOption[]{CameraOption.TOP, CameraOption.SIDE, CameraOption.FRONT}, Arrays.copyOfRange(values, 2, values.length));
  }

  @Test
  public void topComesBeforeSideAndFront() {
    assertTrue(CameraOption.TOP.compareTo(CameraOption.SIDE) < 0);
    assertTrue(CameraOption.TOP.compareTo(CameraOption.FRONT) < 0);
  }

  @Test
  public void sideComesBeforeFront() {
    assertTrue(CameraOption.SIDE.compareTo(CameraOption.FRONT) < 0);
  }

  @Test
  public void enumSetContainsAllOptions() {
    EnumSet<CameraOption> set = EnumSet.allOf(CameraOption.class);
    assertEquals(5, set.size());
    assertTrue(set.contains(CameraOption.STARTING_CAMERA_VIEW));
    assertTrue(set.contains(CameraOption.LAYOUT_SCENE_VIEW));
    assertTrue(set.contains(CameraOption.TOP));
    assertTrue(set.contains(CameraOption.SIDE));
    assertTrue(set.contains(CameraOption.FRONT));
  }

  @Test
  public void enumMapStoresValuesByCameraOption() {
    EnumMap<CameraOption, String> map = new EnumMap<CameraOption, String>(CameraOption.class);
    map.put(CameraOption.STARTING_CAMERA_VIEW, "start");
    map.put(CameraOption.FRONT, "front");
    assertEquals("start", map.get(CameraOption.STARTING_CAMERA_VIEW));
    assertEquals("front", map.get(CameraOption.FRONT));
  }

  @Test
  public void enumMapCanStoreAllValues() {
    EnumMap<CameraOption, Integer> map = new EnumMap<CameraOption, Integer>(CameraOption.class);
    for (CameraOption option : CameraOption.values()) {
      map.put(option, option.ordinal());
    }
    assertEquals(5, map.size());
  }

  @Test
  public void declaringClassIsCameraOption() {
    assertSame(CameraOption.class, CameraOption.FRONT.getDeclaringClass());
  }

  @Test
  public void toStringMatchesName() {
    for (CameraOption option : CameraOption.values()) {
      assertEquals(option.name(), option.toString());
    }
  }

  @Test
  public void valuesReturnsDefensiveCopy() {
    CameraOption[] first = CameraOption.values();
    CameraOption[] second = CameraOption.values();
    assertNotSame(first, second);
  }

  @Test
  public void enumTypeDirectlyExtendsJavaLangEnum() {
    assertEquals(Enum.class, CameraOption.class.getSuperclass());
  }
}
