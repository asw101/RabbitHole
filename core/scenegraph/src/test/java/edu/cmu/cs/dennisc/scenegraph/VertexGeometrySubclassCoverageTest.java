package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertBoxEquals;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class VertexGeometrySubclassCoverageTest {
  @Test
  public void simpleVertexGeometrySubclassesComputeBoundsAndPlanesFromVertices() {
    AxisAlignedBox expected = new AxisAlignedBox(new Point3(-1, 0, 0), new Point3(3, 4, 0));
    Point3 expectedPlaneTranslation = new Point3(1, 2, 0);

    for (VertexGeometry geometry : geometries()) {
      geometry.vertices.setValue(createVertices());

      assertBoxEquals(expected, geometry.getAxisAlignedMinimumBoundingBox());
      assertPointEquals(geometry.getClass().getSimpleName(), expectedPlaneTranslation, geometry.getPlane().translation());
    }
  }

  @Test
  public void simpleVertexGeometrySubclassesTransformVerticesAndRefreshBounds() {
    AxisAlignedBox expected = new AxisAlignedBox(new Point3(4, -1, 2), new Point3(8, 3, 2));

    for (VertexGeometry geometry : geometries()) {
      geometry.vertices.setValue(createVertices());
      geometry.transform(Matrix4x4.fromTranslation(new Point3(5, -1, 2)));

      Vertex[] vertices = geometry.vertices.getValue();
      assertPointEquals(geometry.getClass().getSimpleName() + " first vertex", new Point3(6, 1, 2), vertices[0].position);
      assertPointEquals(geometry.getClass().getSimpleName() + " last vertex", new Point3(8, 3, 2), vertices[2].position);
      assertBoxEquals(expected, geometry.getAxisAlignedMinimumBoundingBox());
      assertPointEquals(geometry.getClass().getSimpleName() + " plane", new Point3(6, 1, 2), geometry.getPlane().translation());
    }
  }

  @Test
  public void simpleVertexGeometrySubclassesDefensivelyCopyVertices() {
    for (VertexGeometry geometry : geometries()) {
      geometry.vertices.setValue(createVertices());

      Vertex[] copy = geometry.vertices.getCopy();
      copy[0].position = new Point3(99, 99, 99);

      assertNotSame(copy[0], geometry.vertices.getValue()[0]);
      assertEquals(new Point3(1, 2, 0), geometry.vertices.getValue()[0].position);
    }
  }

  private static List<VertexGeometry> geometries() {
    return Arrays.asList(
        new PointArray(),
        new LineArray(),
        new LineLoop(),
        new LineStrip(),
        new QuadArray(),
        new QuadStrip(),
        new TriangleFan(),
        new TriangleStrip()
    );
  }

  private static Vertex[] createVertices() {
    return new Vertex[]{
        Vertex.createXYZIJK(1, 2, 0, 0, 0, 1),
        Vertex.createXYZIJK(-1, 0, 0, 0, 0, 1),
        Vertex.createXYZIJK(3, 4, 0, 0, 0, 1),
    };
  }
}
