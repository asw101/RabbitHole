package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.Matrix3x3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExtravagantAxesTest {

  @Test
  public void constructsWithThreeArgs() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    assertNotNull(axes);
  }

  @Test
  public void constructsWithTwoArgs() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0);
    assertNotNull(axes);
  }

  @Test
  public void constructsWithOneArg() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0);
    assertNotNull(axes);
  }

  @Test
  public void hasFourArrowChildren() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    // Should have 4 arrows: X, Y, Z, Forward
    assertEquals(4, axes.getComponentCount());
  }

  @Test
  public void getSgVisualsReturnsEightVisuals() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    Visual[] visuals = axes.getSgVisuals();
    // Each arrow has 2 visuals (cylinder + cone), 4 arrows = 8 visuals
    assertEquals(8, visuals.length);
  }

  @Test
  public void getSgOpacityAppearancesReturnsFour() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    SimpleAppearance[] apps = axes.getSgOpacityAppearances();
    assertEquals(4, apps.length);
    for (SimpleAppearance a : apps) {
      assertNotNull(a);
    }
  }

  @Test
  public void defaultScaleIsIdentity() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0);
    assertEquals(Matrix3x3.IDENTITY, axes.getScale());
  }

  @Test
  public void getScalePropertyIsNotNull() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0);
    assertNotNull(axes.getScaleProperty());
  }

  @Test
  public void setScaleUpdatesScale() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    Matrix3x3 scaled = Matrix3x3.IDENTITY.scale(2.0);
    axes.setScale(scaled);
    assertEquals(scaled, axes.getScale());
  }

  @Test
  public void resizeDoesNotThrow() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    axes.resize(2.0, 3.0, 1.5);
    assertNotNull(axes);
  }

  @Test
  public void setOpacityChangesAllAppearances() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    axes.setOpacity(0.5f);
    assertEquals(0.5f, axes.getOpacity(), 0.001);
  }

  @Test
  public void defaultOpacityIsOne() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    assertEquals(1.0f, axes.getOpacity(), 0.001);
  }

  @Test
  public void setIsShowingFalseHidesVisuals() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    axes.setIsShowing(false);
    for (Visual v : axes.getSgVisuals()) {
      assertFalse(v.isShowing.getValue());
    }
  }

  @Test
  public void setIsShowingTrueShowsVisuals() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    axes.setIsShowing(false);
    axes.setIsShowing(true);
    for (Visual v : axes.getSgVisuals()) {
      assertTrue(v.isShowing.getValue());
    }
  }

  @Test
  public void childrenAreArrows() {
    ExtravagantAxes axes = new ExtravagantAxes(1.0, 2.0, 1.0);
    for (Component child : axes.getComponents()) {
      assertTrue("Child should be an Arrow", child instanceof Arrow);
    }
  }
}
