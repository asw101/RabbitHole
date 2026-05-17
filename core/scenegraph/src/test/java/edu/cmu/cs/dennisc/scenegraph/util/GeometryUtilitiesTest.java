package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GeometryUtilitiesTest {
  private static final double EPSILON = 0.000001;

  private IndexedTriangleArray makeTriangleArray(Vertex[] vertices, int[] polygonData) {
    IndexedTriangleArray ita = new IndexedTriangleArray();
    ita.vertices.setValue(vertices);
    ita.polygonData.setValue(polygonData);
    return ita;
  }

  @Test(expected = AssertionError.class)
  public void constructorThrowsAssertionError() {
    new GeometryUtilities();
  }

  @Test
  public void cleanReturnsNonNull() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2}, new int[]{0, 1, 2});
    IndexedTriangleArray result = GeometryUtilities.clean(ita);
    assertNotNull(result);
  }

  @Test
  public void cleanPreservesSingleTriangle() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2}, new int[]{0, 1, 2});

    GeometryUtilities.clean(ita);
    assertEquals(3, ita.vertices.getValue().length);
    assertEquals(3, ita.polygonData.getValueAsArray().length);
  }

  @Test
  public void cleanMergesDuplicateVertices() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    // Duplicate of v0
    Vertex v3 = Vertex.createXYZ(0, 0, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2, v3}, new int[]{0, 1, 2, 3, 1, 2});

    GeometryUtilities.clean(ita);
    // v0 and v3 are equal, so they should be merged
    assertTrue("Should have fewer vertices after merging duplicates",
        ita.vertices.getValue().length <= 3);
  }

  @Test
  public void cleanRemovesDegenerateTriangles() {
    // Triangle where two indices are the same (degenerate - a line)
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2},
        new int[]{0, 1, 2, 0, 0, 1}); // second triangle is degenerate

    GeometryUtilities.clean(ita);
    // The degenerate triangle should be removed
    int[] resultPolygons = ita.polygonData.getValueAsArray();
    assertEquals("Degenerate triangle should be removed", 3, resultPolygons.length);
  }

  @Test
  public void cleanRemovesDuplicateTriangles() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    // Two identical triangles
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2}, new int[]{0, 1, 2, 0, 1, 2});

    GeometryUtilities.clean(ita);
    int[] resultPolygons = ita.polygonData.getValueAsArray();
    assertEquals("Duplicate triangle should be removed", 3, resultPolygons.length);
  }

  @Test
  public void cleanRemovesUnreferencedVertices() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    Vertex v3 = Vertex.createXYZ(99, 99, 99); // unreferenced
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2, v3}, new int[]{0, 1, 2});

    GeometryUtilities.clean(ita);
    assertEquals("Unreferenced vertex should be removed",
        3, ita.vertices.getValue().length);
  }

  @Test
  public void cleanHandlesMultipleTrianglesCorrectly() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    Vertex v3 = Vertex.createXYZ(1, 1, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2, v3}, new int[]{0, 1, 2, 1, 3, 2});

    GeometryUtilities.clean(ita);
    assertEquals(4, ita.vertices.getValue().length);
    assertEquals(6, ita.polygonData.getValueAsArray().length);
  }

  @Test
  public void cleanWithNoDuplicateVerticesPreservesAll() {
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2}, new int[]{0, 1, 2});

    GeometryUtilities.clean(ita);
    assertEquals(3, ita.vertices.getValue().length);
  }

  @Test
  public void cleanCombinesAllThreeOperations() {
    // Setup: duplicate vertex (v0 == v4), degenerate triangle, duplicate triangle, unreferenced vertex
    Vertex v0 = Vertex.createXYZ(0, 0, 0);
    Vertex v1 = Vertex.createXYZ(1, 0, 0);
    Vertex v2 = Vertex.createXYZ(0, 1, 0);
    Vertex v3 = Vertex.createXYZ(5, 5, 5); // will become unreferenced after degenerate removal
    Vertex v4 = Vertex.createXYZ(0, 0, 0); // duplicate of v0
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2, v3, v4},
        new int[]{0, 1, 2, 4, 1, 2, 3, 3, 1}); // second is dup (after merge), third is degenerate

    GeometryUtilities.clean(ita);
    assertNotNull(ita.vertices.getValue());
    assertNotNull(ita.polygonData.getValueAsArray());
  }

  @Test
  public void cleanPreservesVertexPositionData() {
    Vertex v0 = Vertex.createXYZ(10, 20, 30);
    Vertex v1 = Vertex.createXYZ(40, 50, 60);
    Vertex v2 = Vertex.createXYZ(70, 80, 90);
    IndexedTriangleArray ita = makeTriangleArray(
        new Vertex[]{v0, v1, v2}, new int[]{0, 1, 2});

    GeometryUtilities.clean(ita);
    Vertex[] result = ita.vertices.getValue();
    assertEquals(10.0, result[0].position.x(), EPSILON);
    assertEquals(20.0, result[0].position.y(), EPSILON);
    assertEquals(30.0, result[0].position.z(), EPSILON);
  }
}
