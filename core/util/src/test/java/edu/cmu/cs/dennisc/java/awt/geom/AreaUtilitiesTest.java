package edu.cmu.cs.dennisc.java.awt.geom;

import org.junit.Test;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AreaUtilitiesTest {
  @Test
  public void createAreaReturnsEmptyAreaForNullShape() {
    Area area = AreaUtilities.createArea(null);

    assertTrue(area.isEmpty());
  }

  @Test
  public void createIntersectionReturnsOverlappingRegion() {
    Area intersection = AreaUtilities.createIntersection(
        new Rectangle2D.Double(0, 0, 4, 4),
        new Rectangle2D.Double(2, 1, 4, 4)
    );

    Rectangle2D bounds = intersection.getBounds2D();
    assertEquals(2.0, bounds.getX(), 0.0);
    assertEquals(1.0, bounds.getY(), 0.0);
    assertEquals(2.0, bounds.getWidth(), 0.0);
    assertEquals(3.0, bounds.getHeight(), 0.0);
    assertTrue(intersection.contains(3.0, 2.0));
    assertFalse(intersection.contains(1.0, 1.0));
  }

  @Test
  public void createUnionAndSubtractionHandleNullAndOverlap() {
    Area union = AreaUtilities.createUnion(null, new Rectangle2D.Double(5, 6, 2, 3));
    Rectangle2D unionBounds = union.getBounds2D();
    assertEquals(5.0, unionBounds.getX(), 0.0);
    assertEquals(6.0, unionBounds.getY(), 0.0);
    assertEquals(2.0, unionBounds.getWidth(), 0.0);
    assertEquals(3.0, unionBounds.getHeight(), 0.0);

    Area subtraction = AreaUtilities.createSubtraction(
        new Rectangle2D.Double(0, 0, 5, 5),
        new Rectangle2D.Double(1, 1, 2, 2)
    );
    assertFalse(subtraction.contains(1.5, 1.5));
    assertTrue(subtraction.contains(4.5, 4.5));
  }
}
