package org.alice.stageide.sceneeditor.side;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for scene editor side panel classes.
 */
public class SideCompositeStructureTest {

  // ---- SideComposite ----

  @Test
  public void sideComposite_classIsAccessible() {
    assertNotNull(SideComposite.class);
  }

  @Test
  public void sideComposite_isPublic() {
    assertTrue(Modifier.isPublic(SideComposite.class.getModifiers()));
  }

  @Test
  public void sideComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SideComposite.class.getModifiers()));
  }

  // ---- ObjectPropertiesToolPalette ----

  @Test
  public void objectPropertiesToolPalette_classIsAccessible() {
    assertNotNull(ObjectPropertiesToolPalette.class);
  }

  @Test
  public void objectPropertiesToolPalette_isPublic() {
    assertTrue(Modifier.isPublic(ObjectPropertiesToolPalette.class.getModifiers()));
  }

  // ---- ObjectMarkerFieldData ----

  @Test
  public void objectMarkerFieldData_classIsAccessible() {
    assertNotNull(ObjectMarkerFieldData.class);
  }

  @Test
  public void objectMarkerFieldData_isPublic() {
    assertTrue(Modifier.isPublic(ObjectMarkerFieldData.class.getModifiers()));
  }

  @Test
  public void objectMarkerFieldData_extendsMarkerFieldData() {
    assertTrue(MarkerFieldData.class.isAssignableFrom(ObjectMarkerFieldData.class));
  }

  // ---- CameraMarkerFieldData ----

  @Test
  public void cameraMarkerFieldData_classIsAccessible() {
    assertNotNull(CameraMarkerFieldData.class);
  }

  @Test
  public void cameraMarkerFieldData_isPublic() {
    assertTrue(Modifier.isPublic(CameraMarkerFieldData.class.getModifiers()));
  }

  @Test
  public void cameraMarkerFieldData_extendsMarkerFieldData() {
    assertTrue(MarkerFieldData.class.isAssignableFrom(CameraMarkerFieldData.class));
  }

  // ---- MarkerFieldData ----

  @Test
  public void markerFieldData_classIsAccessible() {
    assertNotNull(MarkerFieldData.class);
  }

  @Test
  public void markerFieldData_isPublic() {
    assertTrue(Modifier.isPublic(MarkerFieldData.class.getModifiers()));
  }

  @Test
  public void markerFieldData_isAbstract() {
    assertTrue(Modifier.isAbstract(MarkerFieldData.class.getModifiers()));
  }

  // ---- CameraMarkersToolPalette ----

  @Test
  public void cameraMarkersToolPalette_classIsAccessible() {
    assertNotNull(CameraMarkersToolPalette.class);
  }

  @Test
  public void cameraMarkersToolPalette_isPublic() {
    assertTrue(Modifier.isPublic(CameraMarkersToolPalette.class.getModifiers()));
  }

  // ---- AddCameraMarkerFieldComposite ----

  @Test
  public void addCameraMarkerFieldComposite_classIsAccessible() {
    assertNotNull(AddCameraMarkerFieldComposite.class);
  }

  @Test
  public void addCameraMarkerFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddCameraMarkerFieldComposite.class.getModifiers()));
  }

  // ---- SnapDetailsToolPaletteCoreComposite ----

  @Test
  public void snapDetailsToolPaletteCoreComposite_classIsAccessible() {
    assertNotNull(SnapDetailsToolPaletteCoreComposite.class);
  }

  @Test
  public void snapDetailsToolPaletteCoreComposite_isPublic() {
    assertTrue(Modifier.isPublic(SnapDetailsToolPaletteCoreComposite.class.getModifiers()));
  }

  // ---- AddMarkerFieldComposite ----

  @Test
  public void addMarkerFieldComposite_classIsAccessible() {
    assertNotNull(AddMarkerFieldComposite.class);
  }

  @Test
  public void addMarkerFieldComposite_isPublic() {
    assertTrue(Modifier.isPublic(AddMarkerFieldComposite.class.getModifiers()));
  }

  @Test
  public void addMarkerFieldComposite_isAbstract() {
    assertTrue(Modifier.isAbstract(AddMarkerFieldComposite.class.getModifiers()));
  }

  // ---- MarkerColorIdCascade ----

  @Test
  public void markerColorIdCascade_classIsAccessible() {
    assertNotNull(MarkerColorIdCascade.class);
  }

  @Test
  public void markerColorIdCascade_isPublic() {
    assertTrue(Modifier.isPublic(MarkerColorIdCascade.class.getModifiers()));
  }

  // ---- cross-cutting: all field data classes share base ----

  @Test
  public void allFieldDataClasses_shareBaseClass() {
    assertTrue(MarkerFieldData.class.isAssignableFrom(ObjectMarkerFieldData.class));
    assertTrue(MarkerFieldData.class.isAssignableFrom(CameraMarkerFieldData.class));
  }

  @Test
  public void fieldDataClasses_areNotAbstract() {
    assertFalse(Modifier.isAbstract(ObjectMarkerFieldData.class.getModifiers()));
    assertFalse(Modifier.isAbstract(CameraMarkerFieldData.class.getModifiers()));
  }
}
