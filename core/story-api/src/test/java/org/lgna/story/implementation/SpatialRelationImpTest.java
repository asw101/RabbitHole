package org.lgna.story.implementation;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link SpatialRelationImp} enum — the six spatial relations
 * and their {@code getPlaceLocation} computations.
 */
public class SpatialRelationImpTest {

  private static final AxisAlignedBox UNIT_BOX =
      new AxisAlignedBox(new Point3(-0.5, -0.5, -0.5), new Point3(0.5, 0.5, 0.5));

  private static final AxisAlignedBox LARGE_BOX =
      new AxisAlignedBox(new Point3(-1, -1, -1), new Point3(1, 1, 1));

  // ══════════════════════════════════════════════════════════════════════════
  //  Enum values
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void allSixValuesExist() {
    SpatialRelationImp[] values = SpatialRelationImp.values();
    assertEquals(6, values.length);
  }

  @Test
  public void valueOfLeftOf() {
    assertEquals(SpatialRelationImp.LEFT_OF, SpatialRelationImp.valueOf("LEFT_OF"));
  }

  @Test
  public void valueOfRightOf() {
    assertEquals(SpatialRelationImp.RIGHT_OF, SpatialRelationImp.valueOf("RIGHT_OF"));
  }

  @Test
  public void valueOfAbove() {
    assertEquals(SpatialRelationImp.ABOVE, SpatialRelationImp.valueOf("ABOVE"));
  }

  @Test
  public void valueOfBelow() {
    assertEquals(SpatialRelationImp.BELOW, SpatialRelationImp.valueOf("BELOW"));
  }

  @Test
  public void valueOfInFrontOf() {
    assertEquals(SpatialRelationImp.IN_FRONT_OF, SpatialRelationImp.valueOf("IN_FRONT_OF"));
  }

  @Test
  public void valueOfBehind() {
    assertEquals(SpatialRelationImp.BEHIND, SpatialRelationImp.valueOf("BEHIND"));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  getPlaceLocation — basic direction checks
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void rightOfPlacesPositiveX() {
    Point3 loc = SpatialRelationImp.RIGHT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("RIGHT_OF should place at positive X", loc.x() > 0);
  }

  @Test
  public void leftOfPlacesNegativeX() {
    Point3 loc = SpatialRelationImp.LEFT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("LEFT_OF should place at negative X", loc.x() < 0);
  }

  @Test
  public void abovePlacesPositiveY() {
    Point3 loc = SpatialRelationImp.ABOVE.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("ABOVE should place at positive Y", loc.y() > 0);
  }

  @Test
  public void belowPlacesNegativeY() {
    Point3 loc = SpatialRelationImp.BELOW.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("BELOW should place at negative Y", loc.y() < 0);
  }

  @Test
  public void inFrontOfPlacesNegativeZ() {
    Point3 loc = SpatialRelationImp.IN_FRONT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("IN_FRONT_OF should place at negative Z", loc.z() < 0);
  }

  @Test
  public void behindPlacesPositiveZ() {
    Point3 loc = SpatialRelationImp.BEHIND.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertNotNull(loc);
    assertTrue("BEHIND should place at positive Z", loc.z() > 0);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  getPlaceLocation — offset and mixed box sizes
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void rightOfWithOffsetIncreasesX() {
    Point3 noOffset = SpatialRelationImp.RIGHT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    Point3 withOffset = SpatialRelationImp.RIGHT_OF.getPlaceLocation(2.0, UNIT_BOX, UNIT_BOX);
    assertTrue(withOffset.x() > noOffset.x());
  }

  @Test
  public void aboveWithLargerObjectBoxGivesLargerY() {
    Point3 locSmall = SpatialRelationImp.ABOVE.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    Point3 locLarge = SpatialRelationImp.ABOVE.getPlaceLocation(0.0, UNIT_BOX, LARGE_BOX);
    assertTrue("Larger object box should produce larger Y placement", locLarge.y() > locSmall.y());
  }

  @Test
  public void rightOfZeroOffsetYandZAreZero() {
    Point3 loc = SpatialRelationImp.RIGHT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(0.0, loc.y(), 1e-9);
    assertEquals(0.0, loc.z(), 1e-9);
  }

  @Test
  public void aboveZeroOffsetXandZAreZero() {
    Point3 loc = SpatialRelationImp.ABOVE.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(0.0, loc.x(), 1e-9);
    assertEquals(0.0, loc.z(), 1e-9);
  }

  @Test
  public void behindZeroOffsetXandYAreZero() {
    Point3 loc = SpatialRelationImp.BEHIND.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(0.0, loc.x(), 1e-9);
    assertEquals(0.0, loc.y(), 1e-9);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  getPlaceLocation — exact values for unit boxes
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void rightOfUnitBoxesZeroOffsetExactValue() {
    // RIGHT_OF: x = 0 * 1 + objectBox.max.x (0.5) - subjectBox.min.x (-0.5) = 1.0
    Point3 loc = SpatialRelationImp.RIGHT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(1.0, loc.x(), 1e-9);
  }

  @Test
  public void leftOfUnitBoxesZeroOffsetExactValue() {
    // LEFT_OF: x = 0 * (-1) + objectBox.min.x (-0.5) - subjectBox.max.x (0.5) = -1.0
    Point3 loc = SpatialRelationImp.LEFT_OF.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(-1.0, loc.x(), 1e-9);
  }

  @Test
  public void aboveUnitBoxesZeroOffsetExactValue() {
    Point3 loc = SpatialRelationImp.ABOVE.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(1.0, loc.y(), 1e-9);
  }

  @Test
  public void belowUnitBoxesZeroOffsetExactValue() {
    Point3 loc = SpatialRelationImp.BELOW.getPlaceLocation(0.0, UNIT_BOX, UNIT_BOX);
    assertEquals(-1.0, loc.y(), 1e-9);
  }
}
