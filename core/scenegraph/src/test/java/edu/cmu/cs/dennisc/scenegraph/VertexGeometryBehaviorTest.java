package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertBoxEquals;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class VertexGeometryBehaviorTest {
  @Test
  public void boundingBoxUsesVertexPositions() {
    TriangleArray geometry = geometry();

    AxisAlignedBox box = geometry.getAxisAlignedMinimumBoundingBox();

    assertBoxEquals(
        new AxisAlignedBox(new Point3(-1, 0, 0), new Point3(3, 4, 0)),
        box
    );
  }

  @Test
  public void getPlaneUsesFirstVertexPositionAsTranslation() {
    TriangleArray geometry = geometry();

    assertPointEquals(new Point3(1, 2, 0), geometry.getPlane().translation());
  }

  @Test
  public void transformUpdatesVertexPositionsInPlace() {
    TriangleArray geometry = geometry();

    geometry.transform(Matrix4x4.fromTranslation(new Point3(5, -1, 2)));

    Vertex[] vertices = geometry.vertices.getValue();
    assertPointEquals(new Point3(6, 1, 2), vertices[0].position);
    assertPointEquals(new Point3(8, 3, 2), vertices[2].position);
    assertPointEquals(new Point3(6, 1, 2), geometry.getPlane().translation());
  }

  @Test
  public void verticesPropertyCreatesDefensiveCopies() {
    TriangleArray geometry = geometry();

    Vertex[] copy = geometry.vertices.getCopy();
    copy[0].position = new Point3(99, 99, 99);

    assertNotSame(copy[0], geometry.vertices.getValue()[0]);
    assertEquals(new Point3(1, 2, 0), geometry.vertices.getValue()[0].position);
  }

  private static TriangleArray geometry() {
    TriangleArray geometry = new TriangleArray();
    geometry.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJK(1, 2, 0, 0, 0, 1),
        Vertex.createXYZIJK(-1, 0, 0, 0, 0, 1),
        Vertex.createXYZIJK(3, 4, 0, 0, 0, 1),
    });
    return geometry;
  }
}
