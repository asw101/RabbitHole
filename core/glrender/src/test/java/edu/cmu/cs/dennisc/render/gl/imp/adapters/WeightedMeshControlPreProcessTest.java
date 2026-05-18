package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link WeightedMeshControl} preProcess() reset logic
 * and construction state. Uses reflection to set internal arrays
 * since full initialization requires a WeightedMesh.
 */
public class WeightedMeshControlPreProcessTest {

  @Test
  public void constructor_noException() {
    WeightedMeshControl wmc = new WeightedMeshControl();
    assertNotNull(wmc);
  }

  @Test
  public void constructor_weightedMeshInitiallyNull() {
    WeightedMeshControl wmc = new WeightedMeshControl();
    assertNull(wmc.weightedMesh);
  }

  @Test
  public void preProcess_resetsAllJointMatricesToNaN() throws Exception {
    WeightedMeshControl wmc = new WeightedMeshControl();
    int vertexCount = 5;
    AffineMatrix4x4[] matrices = new AffineMatrix4x4[vertexCount];
    float[] weights = new float[vertexCount];

    // Set non-NaN values to verify reset
    AffineMatrix4x4 nonNaN = AffineMatrix4x4.createTranslation(1, 2, 3);
    for (int i = 0; i < vertexCount; i++) {
      matrices[i] = nonNaN;
      weights[i] = 0.5f;
    }

    setField(wmc, "weightedJointMatrices", matrices);
    setField(wmc, "weights", weights);

    wmc.preProcess();

    AffineMatrix4x4[] result = (AffineMatrix4x4[]) getField(wmc, "weightedJointMatrices");
    float[] resultWeights = (float[]) getField(wmc, "weights");

    for (int i = 0; i < vertexCount; i++) {
      assertTrue("Joint matrix [" + i + "] should be NaN after preProcess",
          result[i].equals(AffineMatrix4x4.NaN));
      assertEquals("Weight [" + i + "] should be 0 after preProcess",
          0f, resultWeights[i], 0.0001f);
    }
  }

  @Test
  public void preProcess_singleVertex_resetsCorrectly() throws Exception {
    WeightedMeshControl wmc = new WeightedMeshControl();
    AffineMatrix4x4[] matrices = { AffineMatrix4x4.createTranslation(5, 5, 5) };
    float[] weights = { 1.0f };

    setField(wmc, "weightedJointMatrices", matrices);
    setField(wmc, "weights", weights);

    wmc.preProcess();

    AffineMatrix4x4[] result = (AffineMatrix4x4[]) getField(wmc, "weightedJointMatrices");
    float[] resultWeights = (float[]) getField(wmc, "weights");
    assertTrue(result[0].equals(AffineMatrix4x4.NaN));
    assertEquals(0f, resultWeights[0], 0.0001f);
  }

  @Test
  public void preProcess_emptyArrays_noException() throws Exception {
    WeightedMeshControl wmc = new WeightedMeshControl();
    setField(wmc, "weightedJointMatrices", new AffineMatrix4x4[0]);
    setField(wmc, "weights", new float[0]);
    wmc.preProcess();
  }

  @Test
  public void preProcess_largeArray_allReset() throws Exception {
    WeightedMeshControl wmc = new WeightedMeshControl();
    int count = 100;
    AffineMatrix4x4[] matrices = new AffineMatrix4x4[count];
    float[] weights = new float[count];
    for (int i = 0; i < count; i++) {
      matrices[i] = AffineMatrix4x4.createTranslation(i, i, i);
      weights[i] = (float) i / count;
    }

    setField(wmc, "weightedJointMatrices", matrices);
    setField(wmc, "weights", weights);

    wmc.preProcess();

    AffineMatrix4x4[] result = (AffineMatrix4x4[]) getField(wmc, "weightedJointMatrices");
    float[] resultWeights = (float[]) getField(wmc, "weights");
    for (int i = 0; i < count; i++) {
      assertTrue(result[i].equals(AffineMatrix4x4.NaN));
      assertEquals(0f, resultWeights[i], 0.0001f);
    }
  }

  @Test
  public void preProcess_calledTwice_idempotent() throws Exception {
    WeightedMeshControl wmc = new WeightedMeshControl();
    AffineMatrix4x4[] matrices = { AffineMatrix4x4.createTranslation(1, 1, 1) };
    float[] weights = { 0.7f };

    setField(wmc, "weightedJointMatrices", matrices);
    setField(wmc, "weights", weights);

    wmc.preProcess();
    wmc.preProcess();

    AffineMatrix4x4[] result = (AffineMatrix4x4[]) getField(wmc, "weightedJointMatrices");
    float[] resultWeights = (float[]) getField(wmc, "weights");
    assertTrue(result[0].equals(AffineMatrix4x4.NaN));
    assertEquals(0f, resultWeights[0], 0.0001f);
  }

  // ── Reflection helpers ────────────────────────────────────────────

  private static void setField(Object obj, String name, Object value) throws Exception {
    Field f = obj.getClass().getDeclaredField(name);
    f.setAccessible(true);
    f.set(obj, value);
  }

  private static Object getField(Object obj, String name) throws Exception {
    Field f = obj.getClass().getDeclaredField(name);
    f.setAccessible(true);
    return f.get(obj);
  }
}
