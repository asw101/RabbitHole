package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GroundMeshDataTest {

  @Test
  public void verticesArrayHasExpectedSize() {
    assertEquals(593, GroundMeshData.VERTICES.length);
  }

  @Test
  public void firstVertexMatchesExpectedPositionAndTextureCoordinates() {
    Vertex first = GroundMeshData.VERTICES[0];
    assertEquals(500.0, first.position.x(), 1e-4);
    assertEquals(0.0, first.position.y(), 1e-6);
    assertEquals(-500.0, first.position.z(), 1e-4);
    assertEquals(-61.8721f, first.textureCoordinate0.u, 1e-4f);
    assertEquals(80.0448f, first.textureCoordinate0.v, 1e-4f);
  }

  @Test
  public void allVerticesLieOnGroundPlane() {
    for (Vertex vertex : GroundMeshData.VERTICES) {
      assertEquals(0.0, vertex.position.y(), 1e-6);
    }
  }

  @Test
  public void allNormalsPointUpward() {
    for (Vertex vertex : GroundMeshData.VERTICES) {
      assertNotNull(vertex.normal);
      assertEquals(0.0f, vertex.normal.x(), 1e-6f);
      assertEquals(1.0f, vertex.normal.y(), 1e-6f);
      assertEquals(0.0f, vertex.normal.z(), 1e-6f);
    }
  }

  @Test
  public void everyVertexHasTextureCoordinates() {
    for (Vertex vertex : GroundMeshData.VERTICES) {
      assertNotNull(vertex.textureCoordinate0);
      assertFalse(Float.isNaN(vertex.textureCoordinate0.u));
      assertFalse(Float.isNaN(vertex.textureCoordinate0.v));
    }
  }

  @Test
  public void meshExtendsAcrossPositiveAndNegativeX() {
    double minX = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      minX = Math.min(minX, vertex.position.x());
      maxX = Math.max(maxX, vertex.position.x());
    }
    assertEquals(-500.0, minX, 1e-3);
    assertEquals(500.0, maxX, 1e-3);
  }

  @Test
  public void meshExtendsAcrossPositiveAndNegativeZ() {
    double minZ = Double.POSITIVE_INFINITY;
    double maxZ = Double.NEGATIVE_INFINITY;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      minZ = Math.min(minZ, vertex.position.z());
      maxZ = Math.max(maxZ, vertex.position.z());
    }
    assertEquals(-500.0, minZ, 1e-3);
    assertEquals(500.0, maxZ, 1e-3);
  }

  @Test
  public void meshContainsVerticesNearOrigin() {
    boolean found = false;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      if (Math.abs(vertex.position.x()) < 0.01 && Math.abs(vertex.position.z()) < 0.01) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void meshContainsFarCornerVertices() {
    boolean positiveCorner = false;
    boolean negativeCorner = false;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      if (Math.abs(vertex.position.x() - 500.0) < 1e-3 && Math.abs(vertex.position.z() - 500.0) < 1e-3) {
        positiveCorner = true;
      }
      if (Math.abs(vertex.position.x() + 500.0) < 1e-3 && Math.abs(vertex.position.z() + 500.0) < 1e-3) {
        negativeCorner = true;
      }
    }
    assertTrue(positiveCorner);
    assertTrue(negativeCorner);
  }

  @Test
  public void textureCoordinatesSpanPositiveAndNegativeU() {
    float minU = Float.POSITIVE_INFINITY;
    float maxU = Float.NEGATIVE_INFINITY;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      minU = Math.min(minU, vertex.textureCoordinate0.u);
      maxU = Math.max(maxU, vertex.textureCoordinate0.u);
    }
    assertTrue(minU < 0.0f);
    assertTrue(maxU > 0.0f);
  }

  @Test
  public void textureCoordinatesSpanPositiveAndNegativeV() {
    float minV = Float.POSITIVE_INFINITY;
    float maxV = Float.NEGATIVE_INFINITY;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      minV = Math.min(minV, vertex.textureCoordinate0.v);
      maxV = Math.max(maxV, vertex.textureCoordinate0.v);
    }
    assertTrue(minV < 0.0f);
    assertTrue(maxV > 0.0f);
  }

  @Test
  public void meshHasManyDistinctPositions() {
    Set<String> positions = new HashSet<>();
    for (Vertex vertex : GroundMeshData.VERTICES) {
      positions.add(vertex.position.toString());
    }
    assertTrue(positions.size() > 300);
  }

  @Test
  public void meshHasManyDistinctTextureCoordinates() {
    Set<String> textureCoordinates = new HashSet<>();
    for (Vertex vertex : GroundMeshData.VERTICES) {
      textureCoordinates.add(vertex.textureCoordinate0.toString());
    }
    assertTrue(textureCoordinates.size() > 300);
  }

  @Test
  public void centerStripContainsExpectedSampleVertex() {
    boolean found = false;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      if (Math.abs(vertex.position.x() - 88.1586) < 1e-4 && Math.abs(vertex.position.z() + 88.7805) < 1e-4) {
        found = true;
        assertEquals(-10.4969f, vertex.textureCoordinate0.u, 1e-4f);
        assertEquals(14.6232f, vertex.textureCoordinate0.v, 1e-4f);
      }
    }
    assertTrue(found);
  }

  @Test
  public void mirroredVerticesExistOnLeftAndRightOfCenter() {
    boolean left = false;
    boolean right = false;
    for (Vertex vertex : GroundMeshData.VERTICES) {
      if (Math.abs(vertex.position.x() - 124.3240) < 1e-4 && Math.abs(vertex.position.z() - 375.0) < 1e-4) {
        right = true;
      }
      if (Math.abs(vertex.position.x() + 124.3192) < 1e-4 && Math.abs(vertex.position.z() - 375.0) < 1e-4) {
        left = true;
      }
    }
    assertTrue(left);
    assertTrue(right);
  }

  @Test
  public void verticesArrayIsSafeToCopy() {
    Vertex[] copy = GroundMeshData.VERTICES.clone();
    assertEquals(GroundMeshData.VERTICES.length, copy.length);
    assertEquals(GroundMeshData.VERTICES[10], copy[10]);
  }
}
