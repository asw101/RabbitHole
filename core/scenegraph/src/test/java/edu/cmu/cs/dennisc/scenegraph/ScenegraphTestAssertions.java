package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;

import static org.junit.Assert.assertEquals;

/**
 * Shared assertion helpers for scenegraph tests, eliminating duplicate
 * assertPointEquals / assertBoxEquals / EPSILON across 12+ test classes.
 */
public final class ScenegraphTestAssertions {
  public static final double EPSILON = 0.000001;

  private ScenegraphTestAssertions() {
  }

  public static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals("x", expected.x(), actual.x(), EPSILON);
    assertEquals("y", expected.y(), actual.y(), EPSILON);
    assertEquals("z", expected.z(), actual.z(), EPSILON);
  }

  public static void assertPointEquals(String msg, Point3 expected, Point3 actual) {
    assertEquals(msg + " x", expected.x(), actual.x(), EPSILON);
    assertEquals(msg + " y", expected.y(), actual.y(), EPSILON);
    assertEquals(msg + " z", expected.z(), actual.z(), EPSILON);
  }

  public static void assertBoxEquals(AxisAlignedBox expected, AxisAlignedBox actual) {
    assertPointEquals("min", expected.minimum(), actual.minimum());
    assertPointEquals("max", expected.maximum(), actual.maximum());
  }
}
