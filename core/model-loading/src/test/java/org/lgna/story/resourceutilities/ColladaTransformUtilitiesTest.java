package org.lgna.story.resourceutilities;

import org.junit.Test;
import static org.junit.Assert.*;

public class ColladaTransformUtilitiesTest {

  @Test
  public void flipCoordinateSpaceConstantIsTrue() {
    assertTrue(ColladaTransformUtilities.FLIP_COORDINATE_SPACE);
  }

  @Test
  public void createFlippedRowMajorTransform_identity_staysIdentity() {
    double[] identity = {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    double[] result = ColladaTransformUtilities.createFlippedRowMajorTransform(identity);
    assertArrayEquals(identity, result, 1e-12);
  }

  @Test
  public void createFlippedRowMajorTransform_doesNotMutateInput() {
    double[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    double[] copy = input.clone();
    ColladaTransformUtilities.createFlippedRowMajorTransform(input);
    assertArrayEquals(copy, input, 1e-12);
  }

  @Test
  public void createFlippedRowMajorTransform_negatesCorrectIndices() {
    double[] input = new double[16];
    java.util.Arrays.fill(input, 1.0);
    double[] result = ColladaTransformUtilities.createFlippedRowMajorTransform(input);
    // Indices 1, 3, 4, 6, 9, 11 are negated
    int[] negatedIndices = {1, 3, 4, 6, 9, 11};
    int[] keptIndices = {0, 2, 5, 7, 8, 10, 12, 13, 14, 15};
    for (int idx : negatedIndices) {
      assertEquals("Index " + idx + " should be negated", -1.0, result[idx], 1e-12);
    }
    for (int idx : keptIndices) {
      assertEquals("Index " + idx + " should be kept", 1.0, result[idx], 1e-12);
    }
  }

  @Test
  public void createFlippedRowMajorTransform_translationOnly() {
    double[] t = {1, 0, 0, 5, 0, 1, 0, 0, 0, 0, 1, 7, 0, 0, 0, 1};
    double[] result = ColladaTransformUtilities.createFlippedRowMajorTransform(t);
    assertEquals(-5.0, result[3], 1e-12);  // tx negated
    assertEquals(-7.0, result[11], 1e-12); // tz negated
  }

  @Test
  public void createFlippedPoint3DoubleArray_unitX_negatesXKeepsYZ() {
    double[] unitX = {1.0, 0.0, 0.0};
    double[] result = ColladaTransformUtilities.createFlippedPoint3DoubleArray(unitX);
    assertEquals(-1.0, result[0], 1e-12);
    assertEquals(0.0, result[1], 1e-12);
    assertEquals(0.0, result[2], 1e-12);
  }

  @Test
  public void createFlippedPoint3DoubleArray_unitY_keepsAll() {
    double[] unitY = {0.0, 1.0, 0.0};
    double[] result = ColladaTransformUtilities.createFlippedPoint3DoubleArray(unitY);
    assertEquals(0.0, result[0], 1e-12);
    assertEquals(1.0, result[1], 1e-12);
    assertEquals(0.0, result[2], 1e-12);
  }

  @Test
  public void createFlippedPoint3DoubleArray_unitZ_negatesZ() {
    double[] unitZ = {0.0, 0.0, 1.0};
    double[] result = ColladaTransformUtilities.createFlippedPoint3DoubleArray(unitZ);
    assertEquals(0.0, result[0], 1e-12);
    assertEquals(0.0, result[1], 1e-12);
    assertEquals(-1.0, result[2], 1e-12);
  }

  @Test
  public void createFlippedPoint3DoubleArray_origin() {
    double[] origin = {0.0, 0.0, 0.0};
    double[] result = ColladaTransformUtilities.createFlippedPoint3DoubleArray(origin);
    assertArrayEquals(origin, result, 1e-12);
  }

  @Test
  public void createFlippedPoint3DoubleArray_multipleTriples() {
    double[] pts = {1, 2, 3, 4, 5, 6};
    double[] result = ColladaTransformUtilities.createFlippedPoint3DoubleArray(pts);
    assertEquals(-1.0, result[0], 1e-12);
    assertEquals(2.0, result[1], 1e-12);
    assertEquals(-3.0, result[2], 1e-12);
    assertEquals(-4.0, result[3], 1e-12);
    assertEquals(5.0, result[4], 1e-12);
    assertEquals(-6.0, result[5], 1e-12);
  }

  @Test
  public void createFlippedPoint3FloatArray_unitX_negatesX() {
    float[] unitX = {1.0f, 0.0f, 0.0f};
    float[] result = ColladaTransformUtilities.createFlippedPoint3FloatArray(unitX);
    assertEquals(-1.0f, result[0], 1e-6f);
    assertEquals(0.0f, result[1], 1e-6f);
    assertEquals(0.0f, result[2], 1e-6f);
  }

  @Test
  public void createFlippedPoint3FloatArray_unitZ_negatesZ() {
    float[] unitZ = {0.0f, 0.0f, 1.0f};
    float[] result = ColladaTransformUtilities.createFlippedPoint3FloatArray(unitZ);
    assertEquals(0.0f, result[0], 1e-6f);
    assertEquals(0.0f, result[1], 1e-6f);
    assertEquals(-1.0f, result[2], 1e-6f);
  }

  @Test
  public void createFlippedPoint3FloatArray_origin() {
    float[] origin = {0.0f, 0.0f, 0.0f};
    float[] result = ColladaTransformUtilities.createFlippedPoint3FloatArray(origin);
    assertArrayEquals(origin, result, 1e-6f);
  }

  @Test
  public void createFlippedPoint3FloatArray_multipleTriples() {
    float[] pts = {1f, 2f, 3f, 4f, 5f, 6f};
    float[] result = ColladaTransformUtilities.createFlippedPoint3FloatArray(pts);
    assertEquals(-1.0f, result[0], 1e-6f);
    assertEquals(2.0f, result[1], 1e-6f);
    assertEquals(-3.0f, result[2], 1e-6f);
    assertEquals(-4.0f, result[3], 1e-6f);
    assertEquals(5.0f, result[4], 1e-6f);
    assertEquals(-6.0f, result[5], 1e-6f);
  }
}
