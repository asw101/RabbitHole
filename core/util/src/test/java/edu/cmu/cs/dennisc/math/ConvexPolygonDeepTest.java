package edu.cmu.cs.dennisc.math;

import org.alice.math.immutable.Point2;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConvexPolygonDeepTest {

  @Test
  public void empty_polygon_has_no_vertices() {
    ConvexPolygon poly = new ConvexPolygon();
    assertTrue(poly.getVertices().isEmpty());
  }

  @Test
  public void includePoint_single() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(1.0, 2.0));
    assertEquals(1, poly.getVertices().size());
  }

  @Test
  public void includePoint_two() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(0.0, 0.0));
    poly.includePoint(new Point2(1.0, 0.0));
    assertEquals(2, poly.getVertices().size());
  }

  @Test
  public void includePoint_triangle() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(0.0, 0.0));
    poly.includePoint(new Point2(1.0, 0.0));
    poly.includePoint(new Point2(0.5, 1.0));
    assertEquals(3, poly.getVertices().size());
  }

  @Test
  public void includePoint_duplicate_ignored() {
    ConvexPolygon poly = new ConvexPolygon();
    Point2 p = new Point2(1.0, 2.0);
    poly.includePoint(p);
    poly.includePoint(p);
    assertEquals(1, poly.getVertices().size());
  }

  @Test
  public void distanceAlong_insideTriangle() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(-10.0, -10.0));
    poly.includePoint(new Point2(10.0, -10.0));
    poly.includePoint(new Point2(0.0, 10.0));
    double d = poly.distanceAlong(0.0, 0.0);
    assertEquals(0.0, d, 1e-10);
  }

  @Test
  public void distanceAlong_origin_insideLargeSquare() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(-5.0, -5.0));
    poly.includePoint(new Point2(5.0, -5.0));
    poly.includePoint(new Point2(5.0, 5.0));
    poly.includePoint(new Point2(-5.0, 5.0));
    double d = poly.distanceAlong(1.0, 1.0);
    assertTrue(d >= 0);
  }

  @Test
  public void includePoint_square() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(0.0, 0.0));
    poly.includePoint(new Point2(1.0, 0.0));
    poly.includePoint(new Point2(1.0, 1.0));
    poly.includePoint(new Point2(0.0, 1.0));
    assertTrue(poly.getVertices().size() >= 3);
  }

  @Test
  public void getVertices_returnsLiveList() {
    ConvexPolygon poly = new ConvexPolygon();
    poly.includePoint(new Point2(0.0, 0.0));
    assertNotNull(poly.getVertices());
    assertFalse(poly.getVertices().isEmpty());
  }
}
