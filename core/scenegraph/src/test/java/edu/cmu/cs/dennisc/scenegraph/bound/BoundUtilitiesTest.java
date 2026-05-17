package edu.cmu.cs.dennisc.scenegraph.bound;

import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BoundUtilitiesTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void getBoundingBoxFromVertexArray() {
    Vertex[] vertices = new Vertex[]{
        Vertex.createXYZ(0, 0, 0),
        Vertex.createXYZ(1, 2, 3),
        Vertex.createXYZ(-1, -2, -3)
    };
    AxisAlignedBox box = BoundUtilities.getBoundingBox(vertices);
    assertNotNull(box);
    assertEquals(-1.0, box.minimum().x(), EPSILON);
    assertEquals(-2.0, box.minimum().y(), EPSILON);
    assertEquals(-3.0, box.minimum().z(), EPSILON);
    assertEquals(1.0, box.maximum().x(), EPSILON);
    assertEquals(2.0, box.maximum().y(), EPSILON);
    assertEquals(3.0, box.maximum().z(), EPSILON);
  }

  @Test
  public void getBoundingBoxFromSingleVertex() {
    Vertex[] vertices = new Vertex[]{Vertex.createXYZ(5, 6, 7)};
    AxisAlignedBox box = BoundUtilities.getBoundingBox(vertices);
    assertEquals(5.0, box.minimum().x(), EPSILON);
    assertEquals(5.0, box.maximum().x(), EPSILON);
  }

  @Test
  public void getBoundingBoxFromPointIterable() {
    List<Point3> points = Arrays.asList(
        new Point3(0, 0, 0),
        new Point3(10, 20, 30),
        new Point3(-5, -10, -15)
    );
    AxisAlignedBox box = BoundUtilities.getBoundingBox(points);
    assertNotNull(box);
    assertEquals(-5.0, box.minimum().x(), EPSILON);
    assertEquals(10.0, box.maximum().x(), EPSILON);
    assertEquals(-10.0, box.minimum().y(), EPSILON);
    assertEquals(20.0, box.maximum().y(), EPSILON);
  }

  @Test
  public void getBoundingBoxFromEmptyIterableReturnsNaN() {
    List<Point3> empty = Collections.emptyList();
    AxisAlignedBox box = BoundUtilities.getBoundingBox(empty);
    assertTrue("Empty point list should return NaN box", box.isNaN());
  }

  @Test
  public void getBoundingBoxFromDoubleArray() {
    double[] xyzs = new double[]{0, 0, 0, 1, 2, 3, -1, -2, -3};
    AxisAlignedBox box = BoundUtilities.getBoundingBox(xyzs);
    assertNotNull(box);
    assertEquals(-1.0, box.minimum().x(), EPSILON);
    assertEquals(1.0, box.maximum().x(), EPSILON);
  }

  @Test
  public void getBoundingBoxFromDoubleBuffer() {
    DoubleBuffer buf = DoubleBuffer.wrap(new double[]{0, 0, 0, 5, 10, 15});
    AxisAlignedBox box = BoundUtilities.getBoundingBox(buf);
    assertNotNull(box);
    assertEquals(0.0, box.minimum().x(), EPSILON);
    assertEquals(5.0, box.maximum().x(), EPSILON);
    assertEquals(0.0, box.minimum().y(), EPSILON);
    assertEquals(10.0, box.maximum().y(), EPSILON);
    assertEquals(0.0, box.minimum().z(), EPSILON);
    assertEquals(15.0, box.maximum().z(), EPSILON);
  }
}
