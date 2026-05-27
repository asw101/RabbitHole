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
import static org.junit.Assert.assertTrue;

public class MarkerVertexGeometryCoverageTest {
  @Test
  public void markerVertexGeometriesComputeBoundingBoxesAndPlanes() {
    for (VertexGeometry geometry : geometries()) {
      assertTrue(geometry.getClass().getSimpleName(), geometry instanceof Geometry);
      assertBoxEquals(
          new AxisAlignedBox(new Point3(-1, 0, 0), new Point3(3, 4, 0)),
          geometry.getAxisAlignedMinimumBoundingBox()
      );
      assertPointEquals(
          geometry.getClass().getSimpleName(),
          new Point3(1, 2, 0),
          geometry.getPlane().translation()
      );
    }
  }

  @Test
  public void markerVertexGeometriesTransformVerticesInPlace() {
    for (VertexGeometry geometry : geometries()) {
      geometry.transform(Matrix4x4.fromTranslation(new Point3(5, -1, 2)));

      Vertex[] vertices = geometry.vertices.getValue();
      assertPointEquals(geometry.getClass().getSimpleName(), new Point3(6, 1, 2), vertices[0].position);
      assertPointEquals(geometry.getClass().getSimpleName(), new Point3(8, 3, 2), vertices[2].position);
    }
  }

  @Test
  public void markerVertexGeometriesExposeDefensiveVertexCopies() {
    for (VertexGeometry geometry : geometries()) {
      Vertex[] copy = geometry.vertices.getCopy();
      copy[0].position = new Point3(99, 99, 99);

      assertNotSame(copy[0], geometry.vertices.getValue()[0]);
      assertEquals(new Point3(1, 2, 0), geometry.vertices.getValue()[0].position);
    }
  }

  private static List<VertexGeometry> geometries() {
    return Arrays.asList(
        populate(new LineArray()),
        populate(new LineLoop()),
        populate(new LineStrip()),
        populate(new PointArray()),
        populate(new QuadArray()),
        populate(new QuadStrip()),
        populate(new TriangleFan()),
        populate(new TriangleStrip())
    );
  }

  private static <T extends VertexGeometry> T populate(T geometry) {
    geometry.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJK(1, 2, 0, 0, 0, 1),
        Vertex.createXYZIJK(-1, 0, 0, 0, 0, 1),
        Vertex.createXYZIJK(3, 4, 0, 0, 0, 1),
    });
    return geometry;
  }
}
