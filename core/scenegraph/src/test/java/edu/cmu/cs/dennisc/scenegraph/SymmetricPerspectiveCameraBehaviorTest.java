package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AngleInRadians;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SymmetricPerspectiveCameraBehaviorTest {
  @Test
  public void defaultsExposeConfiguredViewingAngles() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();

    assertEquals(0.5, camera.verticalViewingAngle.getValue().getAsRadians(), 0.0);
    assertTrue(Double.isNaN(camera.horizontalViewingAngle.getValue().getAsRadians()));
  }

  @Test
  public void effectiveAnglesCanBeStoredIndependentlyFromBoundProperties() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    AngleInRadians horizontal = new AngleInRadians(1.25);
    AngleInRadians vertical = new AngleInRadians(0.75);

    camera.setEffectiveHorizontalViewingAngle(horizontal);
    camera.setEffectiveVerticalViewingAngle(vertical);

    assertSame(horizontal, camera.getEffectiveHorizontalViewingAngle());
    assertSame(vertical, camera.getEffectiveVerticalViewingAngle());
  }
}
