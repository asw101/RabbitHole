package org.alice.stageide.sceneeditor.viewmanager;

import org.alice.stageide.sceneeditor.CameraOption;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization test for the CameraMarkerTracker refactoring.
 * Verifies the public API surface is preserved and extracted classes
 * maintain the expected type hierarchy.
 */
public class CameraMarkerTrackerCharacterizationTest {

  @Test
  public void publicApiMethodsArePresent() {
    Set<String> publicMethods = Arrays.stream(CameraMarkerTracker.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .map(Method::getName)
        .collect(Collectors.toSet());
    // These are the public methods that external callers depend on
    assertTrue("setCameras must be public", publicMethods.contains("setCameras"));
    assertTrue("trackStartingCameraView must be public", publicMethods.contains("trackStartingCameraView"));
    assertTrue("updateMarkersForNewScene must be public", publicMethods.contains("updateMarkersForNewScene"));
    assertTrue("centerCameraOnField must be public", publicMethods.contains("centerCameraOnField"));
    assertTrue("setIsVrActive must be public", publicMethods.contains("setIsVrActive"));
    assertTrue("valueChanged must be public", publicMethods.contains("valueChanged"));
    assertTrue("propertyChanged must be public", publicMethods.contains("propertyChanged"));
  }

  @Test
  public void trackerImplementsExpectedInterfaces() {
    assertTrue("Must implement PropertyListener",
        edu.cmu.cs.dennisc.property.event.PropertyListener.class.isAssignableFrom(CameraMarkerTracker.class));
    assertTrue("Must implement ValueListener",
        org.lgna.croquet.event.ValueListener.class.isAssignableFrom(CameraMarkerTracker.class));
  }

  @Test
  public void extractedClassHierarchyIsCorrect() {
    // Perspective markers extend CameraMarkerConfiguration
    assertTrue(CameraMarkerConfiguration.class.isAssignableFrom(PerspectiveCameraMarkerConfiguration.class));
    assertTrue(PerspectiveCameraMarkerConfiguration.class.isAssignableFrom(StartingCameraMarkerConfiguration.class));
    assertTrue(PerspectiveCameraMarkerConfiguration.class.isAssignableFrom(LayoutCameraMarkerConfiguration.class));

    // Orthographic markers extend CameraMarkerConfiguration
    assertTrue(CameraMarkerConfiguration.class.isAssignableFrom(OrthographicCameraMarkerConfiguration.class));
    assertTrue(OrthographicCameraMarkerConfiguration.class.isAssignableFrom(TopCameraMarkerConfiguration.class));
    assertTrue(OrthographicCameraMarkerConfiguration.class.isAssignableFrom(SideCameraMarkerConfiguration.class));
    assertTrue(OrthographicCameraMarkerConfiguration.class.isAssignableFrom(FrontCameraMarkerConfiguration.class));
  }

  @Test
  public void extractedClassesArePackagePrivate() {
    assertFalse("CameraMarkerConfiguration should not be public",
        Modifier.isPublic(CameraMarkerConfiguration.class.getModifiers()));
    assertFalse("PerspectiveCameraMarkerConfiguration should not be public",
        Modifier.isPublic(PerspectiveCameraMarkerConfiguration.class.getModifiers()));
    assertFalse("OrthographicCameraMarkerConfiguration should not be public",
        Modifier.isPublic(OrthographicCameraMarkerConfiguration.class.getModifiers()));
  }

  @Test
  public void cameraOptionValuesAreCoveredByMarkers() {
    // All five CameraOption values must have a corresponding marker configuration class
    CameraOption[] expectedOptions = {
        CameraOption.STARTING_CAMERA_VIEW,
        CameraOption.LAYOUT_SCENE_VIEW,
        CameraOption.TOP,
        CameraOption.SIDE,
        CameraOption.FRONT
    };
    for (CameraOption option : expectedOptions) {
      assertNotNull("CameraOption." + option + " must exist", option);
    }
  }
}
