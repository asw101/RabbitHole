package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the static visual-construction methods on PerspectiveCameraMarkerImp.
 * These methods build scenegraph geometry from primitives (Box, Cylinder, QuadArray)
 * and are fully headless-safe.
 *
 * <p>Note: Instantiating PerspectiveCameraMarker itself requires DynamicResource
 * loading so that path is NOT tested here.
 */
public class PerspectiveCameraMarkerImpTest {

  // ── createCameraVisuals ──

  @Test
  public void createCameraVisualsReturnsNonNullArray() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertNotNull(visuals);
  }

  @Test
  public void createCameraVisualsReturnsFourVisuals() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertEquals("Should return 4 visuals: lens, cylinder1, cylinder2, box", 4, visuals.length);
  }

  @Test
  public void createCameraVisualsEachElementIsNotNull() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    for (int i = 0; i < visuals.length; i++) {
      assertNotNull("Visual at index " + i + " should not be null", visuals[i]);
    }
  }

  @Test
  public void createCameraVisualsHaveGeometry() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    for (Visual v : visuals) {
      assertNotNull("Each visual should have geometry", v.geometries.getValue());
      assertTrue("Each visual should have at least one geometry",
          v.geometries.getValue().length >= 1);
    }
  }

  @Test
  public void createCameraVisualsShareAppearance() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    for (Visual v : visuals) {
      assertSame("All visuals should share the paint appearance",
          paint, v.frontFacingAppearance.getValue());
    }
  }

  @Test
  public void createCameraVisualsFirstVisualIsLensHood() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertEquals("Camera Lens Hood Visual", visuals[0].getName());
  }

  @Test
  public void createCameraVisualsLensHoodParentIsSuppliedParent() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertSame("Lens hood visual should be parented", parent, visuals[0].getParent());
  }

  @Test
  public void createCameraVisualsBoxVisualNameIsCorrect() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertEquals("Camera Box Visual", visuals[3].getName());
  }

  @Test
  public void createCameraVisualsBoxParentIsSuppliedParent() {
    SimpleAppearance paint = new SimpleAppearance();
    Transformable parent = new Transformable();
    Visual[] visuals = PerspectiveCameraMarkerImp.createCameraVisuals(paint, parent);
    assertSame(parent, visuals[3].getParent());
  }

  // ── MarkerImp properties via static scenegraph checks ──

  @Test
  public void simpleAppearanceDefaultOpacityIsOne() {
    SimpleAppearance sa = new SimpleAppearance();
    float opacity = sa.opacity.getValue();
    assertEquals("Default SimpleAppearance opacity should be 1.0", 1.0f, opacity, 1e-6f);
  }

  @Test
  public void simpleAppearanceDiffuseColorIsNotNull() {
    SimpleAppearance sa = new SimpleAppearance();
    assertNotNull(sa.diffuseColor.getValue());
  }
}
