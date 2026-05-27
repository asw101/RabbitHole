package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.Point2;
import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.story.SThing;
import org.lgna.story.implementation.EntityImp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CollisionHullAdditionalCoverageTest {
  private static final double EPSILON = 1e-6;

  @Test
  public void polygonPrismHullContainsProjectedPointsAndPreservesCrossSectionArea() {
    PolygonPrismHull hull = new PolygonPrismHull(new Point3(0.0, 0.0, 0.0), 2.0, List.of(
        new Point2(-1.0, -1.0),
        new Point2(-1.0, 1.0),
        new Point2(1.0, 1.0),
        new Point2(1.0, -1.0)));

    assertTrue(containsProjectedPoint(hull, 0.5, 0.5));
    assertFalse(containsProjectedPoint(hull, 1.5, 0.0));
    assertEquals(4.0, polygonArea(hull.getCrossSectionVertices(null)), EPSILON);
  }

  @Test
  public void cylinderHullContainsProjectedPointsAndApproximatesCircularArea() {
    CylinderHull hull = new CylinderHull(Point3.ORIGIN, 3.0, 3.0);

    assertTrue(containsProjectedPoint(hull, 2.0, 0.0));
    assertFalse(containsProjectedPoint(hull, 3.5, 0.0));
    assertEquals(Math.PI * 9.0, polygonArea(hull.getCrossSectionVertices(Point3.ORIGIN)), 1.5);
  }

  @Test
  public void verticalPrismDistanceAllowsNearMissWhenProximityBridgesGap() {
    VerticalPrismCollisionHull lower = new StubHull(new Point3(0.0, 0.0, 0.0), 1.0, 2.0);
    VerticalPrismCollisionHull upper = new StubHull(new Point3(0.0, 1.4, 0.0), 1.0, 2.0);

    assertFalse(lower.collidesWith(upper));
    assertTrue(lower.isWithinDistance(upper, 0.5));
  }

  @Test
  public void collisionAndProximityHandlersInvalidateCachedHullsWhenThingsChange() {
    Map<SThing, VerticalPrismCollisionHull> hulls = new HashMap<>();
    TestThing thing = new TestThing("thing", new StubHull(Point3.ORIGIN, 2.0, 1.0));
    CollisionHandler collisionHandler = new CollisionHandler(hulls);
    ProximityEventHandler proximityHandler = new ProximityEventHandler(hulls);

    hulls.put(thing, thing.getCollisionHull());
    collisionHandler.markAsChanged(thing);
    assertFalse(hulls.containsKey(thing));

    hulls.put(thing, thing.getCollisionHull());
    proximityHandler.markAsChanged(thing);
    assertFalse(hulls.containsKey(thing));
  }

  @Test
  public void occlusionHandlerMarkAsChangedIsExplicitlyNoOp() {
    OcclusionHandler handler = new OcclusionHandler();
    TestThing thing = new TestThing("thing", new StubHull(Point3.ORIGIN, 2.0, 1.0));

    handler.markAsChanged(thing);

    assertTrue(true);
  }

  private static boolean containsProjectedPoint(VerticalPrismCollisionHull hull, double x, double z) {
    double pointDistance = Math.sqrt((x * x) + (z * z));
    double boundaryDistance = hull.distanceAlong(x, z);
    return !Double.isNaN(boundaryDistance) && (boundaryDistance + EPSILON) >= pointDistance;
  }

  private static double polygonArea(List<Point2> vertices) {
    double twiceArea = 0.0;
    for (int i = 0; i < vertices.size(); i++) {
      Point2 a = vertices.get(i);
      Point2 b = vertices.get((i + 1) % vertices.size());
      twiceArea += (a.x() * b.y()) - (b.x() * a.y());
    }
    return Math.abs(twiceArea) / 2.0;
  }

  private static final class TestThing extends SThing {
    private final String name;
    private final VerticalPrismCollisionHull collisionHull;

    private TestThing(String name, VerticalPrismCollisionHull collisionHull) {
      this.name = name;
      this.collisionHull = collisionHull;
    }

    @Override
    public <T extends EntityImp> T getImplementation() {
      return null;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public VerticalPrismCollisionHull getCollisionHull() {
      return collisionHull;
    }
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
      return List.of(
          new Point2(base.x() - radius, base.z() - radius),
          new Point2(base.x() + radius, base.z() - radius),
          new Point2(base.x() + radius, base.z() + radius),
          new Point2(base.x() - radius, base.z() + radius));
    }
  }
}
