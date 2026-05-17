package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArrowTest {

  @Test
  public void arrowConstructsWithPositiveYAxis() {
    SimpleAppearance appearance = new SimpleAppearance();
    Arrow arrow = new Arrow(2.0, 0.1, 0.5, 0.2,
        Cylinder.BottomToTopAxis.POSITIVE_Y, appearance, true);
    assertNotNull(arrow);
    assertEquals(2, arrow.getComponentCount());
  }

  @Test
  public void arrowConstructsWithSeparateAppearances() {
    SimpleAppearance cylinderApp = new SimpleAppearance();
    SimpleAppearance coneApp = new SimpleAppearance();
    Arrow arrow = new Arrow(2.0, 0.1, 0.5, 0.2,
        Cylinder.BottomToTopAxis.POSITIVE_X, cylinderApp, coneApp, false);
    assertNotNull(arrow);
  }

  @Test
  public void getVisualsReturnsTwoVisuals() {
    SimpleAppearance appearance = new SimpleAppearance();
    Arrow arrow = new Arrow(1.0, 0.05, 0.3, 0.1,
        Cylinder.BottomToTopAxis.POSITIVE_Z, appearance, true);
    Visual[] visuals = arrow.getVisuals();
    assertEquals(2, visuals.length);
    assertNotNull(visuals[0]);
    assertNotNull(visuals[1]);
  }

  @Test
  public void resizeUpdatesGeometry() {
    SimpleAppearance appearance = new SimpleAppearance();
    Arrow arrow = new Arrow(1.0, 0.05, 0.3, 0.1,
        Cylinder.BottomToTopAxis.POSITIVE_Y, appearance, true);

    // Should not throw
    arrow.resize(3.0, 0.2, 0.8, 0.4);
    assertNotNull(arrow.getVisuals());
  }

  @Test
  public void setVisualShowingHidesAndShows() {
    SimpleAppearance appearance = new SimpleAppearance();
    Arrow arrow = new Arrow(1.0, 0.05, 0.3, 0.1,
        Cylinder.BottomToTopAxis.POSITIVE_Y, appearance, true);

    arrow.setVisualShowing(false);
    for (Visual v : arrow.getVisuals()) {
      assertFalse(v.isShowing.getValue());
    }

    arrow.setVisualShowing(true);
    for (Visual v : arrow.getVisuals()) {
      assertTrue(v.isShowing.getValue());
    }
  }

  @Test
  public void setFrontFacingAppearanceUpdatesVisuals() {
    SimpleAppearance original = new SimpleAppearance();
    Arrow arrow = new Arrow(1.0, 0.05, 0.3, 0.1,
        Cylinder.BottomToTopAxis.POSITIVE_Y, original, true);

    TexturedAppearance replacement = new TexturedAppearance();
    arrow.setFrontFacingAppearance(replacement);

    for (Visual v : arrow.getVisuals()) {
      assertNotNull(v.frontFacingAppearance.getValue());
    }
  }

  @Test
  public void arrowWithNegativeAxis() {
    SimpleAppearance appearance = new SimpleAppearance();
    Arrow arrow = new Arrow(2.0, 0.1, 0.5, 0.2,
        Cylinder.BottomToTopAxis.NEGATIVE_Y, appearance, true);
    assertNotNull(arrow);
    assertEquals(2, arrow.getComponentCount());
  }
}
