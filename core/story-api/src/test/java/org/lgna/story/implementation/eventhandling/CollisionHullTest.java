package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point2;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CollisionHullTest {

  @Test
  public void polygonPrismHullStoresBaseAndHeight() {
    PolygonPrismHull hull = squareHull(0.0, 1.0, 0.0, 1.0, 2.5);
    assertEquals(new Point3(0.0, 1.0, 0.0), hull.centerBase);
    assertEquals(2.5, hull.height, 1e-6);
  }

  @Test
  public void polygonPrismHullConstructsFromAxisAlignedBox() {
    AxisAlignedBox box = AxisAlignedBox.createAxisAlignedBox(-1.0, 0.0, -1.0, 1.0, 2.0, 1.0);
    PolygonPrismHull hull = new PolygonPrismHull(new Point3(0.0, 0.0, 0.0), 2.0, AffineMatrix4x4.IDENTITY, box);
    assertTrue(hull.distanceAlong(1.0, 0.0) > 0.0);
    assertEquals(4, hull.getCrossSectionVertices(null).size());
  }

  @Test
  public void polygonPrismHullDistanceAlongReturnsPositiveForContainedDirection() {
    PolygonPrismHull hull = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    assertTrue(hull.distanceAlong(1.0, 0.0) > 0.0);
    assertTrue(hull.distanceAlong(0.0, 1.0) > 0.0);
  }

  @Test
  public void polygonPrismHullCrossSectionVerticesShiftToNewCenter() {
    PolygonPrismHull hull = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    List<Point2> shifted = hull.getCrossSectionVertices(new Point3(3.0, 0.0, -2.0));
    assertTrue(shifted.contains(new Point2(2.0, -3.0)));
    assertTrue(shifted.contains(new Point2(4.0, -1.0)));
  }

  @Test
  public void combinationHullReturnsSecondWhenFirstIsNull() {
    VerticalPrismCollisionHull other = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    assertSame(other, PolygonPrismHull.combinationHull(null, other));
  }

  @Test
  public void combinationHullReturnsFirstWhenSecondIsNull() {
    VerticalPrismCollisionHull hull = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    assertSame(hull, PolygonPrismHull.combinationHull(hull, null));
  }

  @Test
  public void combinationHullExtendsHeightAcrossBothHulls() {
    VerticalPrismCollisionHull a = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    VerticalPrismCollisionHull b = squareHull(0.0, 1.0, 0.0, 1.0, 4.0);
    VerticalPrismCollisionHull combined = PolygonPrismHull.combinationHull(a, b);
    assertEquals(0.0, combined.centerBase.y(), 1e-6);
    assertEquals(5.0, combined.height, 1e-6);
  }

  @Test
  public void cylinderHullStoresBaseAndHeight() {
    CylinderHull hull = new CylinderHull(new Point3(1.0, 2.0, 3.0), 4.0, 5.0);
    assertEquals(new Point3(1.0, 2.0, 3.0), hull.centerBase);
    assertEquals(4.0, hull.height, 1e-6);
  }

  @Test
  public void cylinderHullDistanceAlongReturnsRadius() {
    CylinderHull hull = new CylinderHull(Point3.ORIGIN, 2.0, 3.5);
    assertEquals(3.5, hull.distanceAlong(10.0, -7.0), 1e-6);
  }

  @Test
  public void cylinderHullApproximatesCrossSectionWithTwelveVertices() {
    CylinderHull hull = new CylinderHull(Point3.ORIGIN, 2.0, 2.0);
    assertEquals(12, hull.getCrossSectionVertices(Point3.ORIGIN).size());
  }

  @Test
  public void cylinderHullCrossSectionVerticesShiftToNewCenter() {
    CylinderHull hull = new CylinderHull(Point3.ORIGIN, 2.0, 2.0);
    List<Point2> vertices = hull.getCrossSectionVertices(new Point3(5.0, 0.0, -3.0));
    assertEquals(12, vertices.size());
    Point2 first = vertices.get(0);
    assertEquals(5.0, first.x(), 1e-6);
    assertEquals(-1.0, first.y(), 1e-6);
  }

  @Test
  public void collidesWithReturnsTrueForOverlappingHulls() {
    VerticalPrismCollisionHull a = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    VerticalPrismCollisionHull b = squareHull(0.5, 0.0, 0.5, 1.0, 2.0);
    assertTrue(a.collidesWith(b));
  }

  @Test
  public void collidesWithReturnsFalseForSeparatedHulls() {
    VerticalPrismCollisionHull a = squareHull(0.0, 0.0, 0.0, 1.0, 2.0);
    VerticalPrismCollisionHull b = squareHull(5.0, 0.0, 5.0, 1.0, 2.0);
    assertFalse(a.collidesWith(b));
  }

  @Test
  public void collidesWithReturnsFalseForExactEdgeTouch() {
    VerticalPrismCollisionHull a = new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0);
    VerticalPrismCollisionHull b = new StubHull(new Point3(2.0, 0.0, 0.0), 2.0, 1.0);
    assertFalse(a.collidesWith(b));
  }

  @Test
  public void isWithinDistanceUsesProximityPadding() {
    VerticalPrismCollisionHull a = new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0);
    VerticalPrismCollisionHull b = new StubHull(new Point3(2.5, 0.0, 0.0), 2.0, 1.0);
    assertTrue(a.isWithinDistance(b, 1.0));
  }

  @Test
  public void isWithinDistanceReturnsFalseWhenOtherIsNull() {
    VerticalPrismCollisionHull a = new StubHull(Point3.ORIGIN, 2.0, 1.0);
    assertFalse(a.isWithinDistance(null, 1.0));
  }

  @Test
  public void isWithinDistanceReturnsFalseWhenHeightDoesNotOverlap() {
    VerticalPrismCollisionHull a = new StubHull(new Point3(0.0, 0.0, 0.0), 1.0, 2.0);
    VerticalPrismCollisionHull b = new StubHull(new Point3(0.0, 3.0, 0.0), 1.0, 2.0);
    assertFalse(a.isWithinDistance(b, 0.5));
  }

  @Test
  public void isWithinDistanceReturnsTrueWhenHeightOverlapAndRadiusOverlap() {
    VerticalPrismCollisionHull a = new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 2.0);
    VerticalPrismCollisionHull b = new StubHull(new Point3(1.0, 1.0, 1.0), 2.0, 2.0);
    assertTrue(a.isWithinDistance(b, 0.0));
  }

  private static PolygonPrismHull squareHull(double x, double y, double z, double halfExtent, double height) {
    return new PolygonPrismHull(new Point3(x, y, z), height, Arrays.asList(
        new Point2(-halfExtent, -halfExtent),
        new Point2(-halfExtent, halfExtent),
        new Point2(halfExtent, halfExtent),
        new Point2(halfExtent, -halfExtent)));
  }

  private static final class StubHull extends VerticalPrismCollisionHull {
    private final double radius;

    private StubHull(Point3 centerBase, double height, double radius) {
      super(centerBase, height);
      this.radius = radius;
    }

    @Override
    public double distanceAlong(double xDistance, double zDistance) {
      return radius;
    }

    @Override
    protected List<Point2> getCrossSectionVertices(Point3 newCenter) {
      Point3 base = newCenter == null ? centerBase : newCenter;
      return Arrays.asList(
          new Point2(base.x() - radius, base.z() - radius),
          new Point2(base.x() + radius, base.z() + radius));
    }
  }
}
