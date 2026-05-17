package edu.cmu.cs.dennisc.scenegraph;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class IndicesTest {

  @Test
  public void defaultIndicesAreEmpty() {
    Indices indices = new Indices();
    assertEquals(0, indices.getIndexCount());
    assertEquals(0, indices.getTriangleCount());
    assertArrayEquals(new int[0], indices.getIndices());
  }

  @Test
  public void setIndicesWithIntArrayStoresValues() {
    Indices indices = new Indices();
    // 9 ints = 1 triangle (tex, norm, vert per vertex, 3 vertices per tri)
    int[] data = {0, 0, 0, 1, 1, 1, 2, 2, 2};
    indices.setIndices(data, false);

    assertEquals(9, indices.getIndexCount());
    assertEquals(1, indices.getTriangleCount());
  }

  @Test
  public void setIndicesWithShortArrayConvertsToInt() {
    Indices indices = new Indices();
    short[] data = {0, 0, 0, 1, 1, 1, 2, 2, 2};
    indices.setIndices(data, false);

    assertEquals(9, indices.getIndexCount());
    assertEquals(1, indices.getTriangleCount());
  }

  @Test
  public void adjustIndicesMultipliesCorrectly() {
    Indices indices = new Indices();
    // When needsAdjustment=true, indices get adjusted:
    // texCoord *= 2, normal *= 3, vertex *= 3
    int[] data = {1, 1, 1, 2, 2, 2, 3, 3, 3};
    indices.setIndices(data, true);

    int[] adjusted = indices.getIndices();
    // First triplet: tex=1*2=2, norm=1*3=3, vert=1*3=3
    assertEquals(2, adjusted[0]);
    assertEquals(3, adjusted[1]);
    assertEquals(3, adjusted[2]);
    // Second triplet: tex=2*2=4, norm=2*3=6, vert=2*3=6
    assertEquals(4, adjusted[3]);
    assertEquals(6, adjusted[4]);
    assertEquals(6, adjusted[5]);
  }

  @Test
  public void adjustIndicesIsIdempotent() {
    Indices indices = new Indices();
    int[] data = {1, 1, 1, 2, 2, 2, 3, 3, 3};
    indices.setIndices(data, true);

    int[] firstResult = indices.getIndices().clone();
    // Calling again should not re-adjust
    indices.adjustIndicesIfNecessary();
    assertArrayEquals(firstResult, indices.getIndices());
  }

  @Test
  public void getTextureCoordinateIndexWhenNotAdjusted() {
    Indices indices = new Indices();
    // Pre-adjusted indices: tex is at offset 0,3,6 — multiply by 2
    int[] data = {10, 30, 30, 20, 60, 60, 30, 90, 90};
    indices.setIndices(data, false);

    // After setIndices with needsAdjustment=false, no adjustment happens
    // getTextureCoordinateIndex divides by 2 (since !isInNeedOfIndexAdjustment)
    assertEquals(5, indices.getTextureCoordinateIndex(0, 0)); // 10/2
    assertEquals(10, indices.getTextureCoordinateIndex(0, 1)); // 20/2
    assertEquals(15, indices.getTextureCoordinateIndex(0, 2)); // 30/2
  }

  @Test
  public void getNormalIndexWhenNotAdjusted() {
    Indices indices = new Indices();
    int[] data = {0, 30, 0, 0, 60, 0, 0, 90, 0};
    indices.setIndices(data, false);

    // getNormalIndex divides by 3
    assertEquals(10, indices.getNormalIndex(0, 0)); // 30/3
    assertEquals(20, indices.getNormalIndex(0, 1)); // 60/3
    assertEquals(30, indices.getNormalIndex(0, 2)); // 90/3
  }

  @Test
  public void getVertexIndexWhenNotAdjusted() {
    Indices indices = new Indices();
    int[] data = {0, 0, 30, 0, 0, 60, 0, 0, 90};
    indices.setIndices(data, false);

    // getVertexIndex divides by 3
    assertEquals(10, indices.getVertexIndex(0, 0)); // 30/3
    assertEquals(20, indices.getVertexIndex(0, 1)); // 60/3
    assertEquals(30, indices.getVertexIndex(0, 2)); // 90/3
  }

  @Test
  public void multipleTriangles() {
    Indices indices = new Indices();
    // 18 ints = 2 triangles
    int[] data = new int[18];
    for (int i = 0; i < 18; i++) {
      data[i] = i;
    }
    indices.setIndices(data, false);

    assertEquals(18, indices.getIndexCount());
    assertEquals(2, indices.getTriangleCount());
  }

  @Test
  public void shortArrayAdjustmentWorks() {
    Indices indices = new Indices();
    short[] data = {1, 1, 1, 2, 2, 2, 3, 3, 3};
    indices.setIndices(data, true);

    int[] adjusted = indices.getIndices();
    assertEquals(2, adjusted[0]);  // 1*2
    assertEquals(3, adjusted[1]);  // 1*3
    assertEquals(3, adjusted[2]);  // 1*3
  }
}
