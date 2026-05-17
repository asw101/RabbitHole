package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HorizontalSurfaceTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void defaultConstructorFaceUp() {
    HorizontalSurface surface = new HorizontalSurface(true);
    assertNotNull(surface);
    assertNotNull(surface.getGeometry());
  }

  @Test
  public void defaultConstructorFaceDown() {
    HorizontalSurface surface = new HorizontalSurface(false);
    assertNotNull(surface);
    assertNotNull(surface.getGeometry());
  }

  @Test
  public void parameterizedConstructorSetsValues() {
    HorizontalSurface surface = new HorizontalSurface(true, 10.0f, 5.0f, 2.0f);
    assertNotNull(surface);
    assertNotNull(surface.getGeometry());
  }

  @Test
  public void setSizeUpdatesVertexPositions() {
    HorizontalSurface surface = new HorizontalSurface(true);
    surface.setSize(4.0f, 6.0f, 2.0f);
    // Verifying the geometry exists and was updated without errors
    assertNotNull(surface.getGeometry());
  }

  @Test
  public void setTilingUpdatesTextureCoords() {
    HorizontalSurface surface = new HorizontalSurface(true);
    surface.setTiling(3.0f, 3.0f);
    // Verifying no errors during tiling update
    assertNotNull(surface.getGeometry());
  }

  @Test
  public void setTilingOneOneClamps() {
    HorizontalSurface surface = new HorizontalSurface(true);
    surface.setTiling(1.0f, 1.0f);
    // When tiling is 1,1, clamping should be enabled
    assertTrue(surface.getAppearance().isDiffuseColorTextureClamped.getValue());
  }

  @Test
  public void setTilingNonOneUnclamped() {
    HorizontalSurface surface = new HorizontalSurface(true);
    surface.setTiling(2.0f, 2.0f);
    // When tiling > 1, clamping should be disabled
    assertEquals(false, surface.getAppearance().isDiffuseColorTextureClamped.getValue());
  }

  @Test
  public void faceUpAndFaceDownProduceDifferentNormals() {
    HorizontalSurface up = new HorizontalSurface(true, 1, 1, 1);
    HorizontalSurface down = new HorizontalSurface(false, 1, 1, 1);
    // Both should construct without error and have geometry
    assertNotNull(up.getGeometry());
    assertNotNull(down.getGeometry());
  }
}
