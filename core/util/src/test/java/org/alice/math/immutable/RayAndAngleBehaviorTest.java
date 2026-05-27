package org.alice.math.immutable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RayAndAngleBehaviorTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void rayProjectsPointsAndNormalizesDirections() {
    Ray ray = Ray.fromAtoB(Point3.ORIGIN, new Point3(0, 0, 2));

    assertEquals(new Vector3(0, 0, 1), ray.direction());
    assertEquals(new Point3(0, 0, 5), ray.getPointAlong(5));
    assertEquals(4.0, ray.getProjectedPointT(new Point3(1, 0, 4)), EPSILON);
    assertEquals(new Point3(0, 0, 4), ray.getProjectedPoint(new Point3(1, 0, 4)));
    assertTrue(Ray.NaN.isNaN());

    Ray normalized = new Ray(Point3.ORIGIN, new Vector3(0, 0, 3)).normalized();
    assertEquals(new Vector3(0, 0, 1), normalized.direction());
    assertSame(ray, ray.normalized());
  }

  @Test
  public void anglesConvertNegateScaleAndInterpolateAcrossUnits() {
    AngleInDegrees degrees = new AngleInDegrees(180);
    AngleInRadians radians = new AngleInRadians(Math.PI / 2);
    AngleInRevolutions revolutions = new AngleInRevolutions(0.25);

    assertEquals(Math.PI, degrees.getAsRadians(), EPSILON);
    assertEquals(90.0, radians.getAsDegrees(), EPSILON);
    assertEquals(Math.PI / 2, revolutions.getAsRadians(), EPSILON);

    assertEquals(-180.0, degrees.negated().getAsDegrees(), EPSILON);
    assertSame(degrees, degrees.minus(Angle.ZERO));
    assertEquals(Math.PI, radians.times(2).getAsRadians(), EPSILON);
    assertEquals(0.5, revolutions.interpolateToward(new AngleInRevolutions(0.75), 0.5).getAsRevolutions(), EPSILON);
    assertEquals(Math.PI, new AngleInRadians(2.7).toNearestPi().getAsRadians(), EPSILON);
    assertTrue(Angle.NaN.isNaN());
  }
}
