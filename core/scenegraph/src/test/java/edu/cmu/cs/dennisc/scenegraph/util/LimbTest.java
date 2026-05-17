package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.scenegraph.Component;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LimbTest {

  @Test
  public void constructsWithThreeLengths() {
    Limb limb = new Limb(1.0, 2.0, 0.5);
    assertNotNull(limb);
  }

  @Test
  public void hasExpectedChildCount() {
    Limb limb = new Limb(1.0, 2.0, 0.5);
    // Limb has: visual (from ModelTransformable), sgVisualAB, m_sgB child
    // m_sgB has: visual, sgVisualBC, m_sgC child
    assertTrue("Limb should have children", limb.getComponentCount() > 0);
  }

  @Test
  public void limbHasVisual() {
    Limb limb = new Limb(1.0, 2.0, 0.5);
    assertNotNull(limb.getSGVisual());
  }

  @Test
  public void limbSGVisualHasGeometry() {
    Limb limb = new Limb(1.0, 2.0, 0.5);
    assertNotNull(limb.getSGVisual().geometries.getValue());
    assertTrue("Visual should have geometry", limb.getSGVisual().geometries.getValue().length > 0);
  }

  @Test
  public void constructsWithEqualLengths() {
    Limb limb = new Limb(1.0, 1.0, 1.0);
    assertNotNull(limb);
  }

  @Test
  public void constructsWithVerySmallLengths() {
    Limb limb = new Limb(0.01, 0.02, 0.005);
    assertNotNull(limb);
  }

  @Test
  public void constructsWithLargeLengths() {
    Limb limb = new Limb(100.0, 200.0, 50.0);
    assertNotNull(limb);
  }
}
