package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link WeightedMeshControl} — preProcess reset via reflection,
 * initialization flag, and field defaults.
 *
 * Uses reflection to set up weightedJointMatrices[] and weights[] arrays
 * directly, then calls preProcess() to verify reset behavior.
 */
public class WeightedMeshControlTest {

  private WeightedMeshControl control;

  @Before
  public void setUp() {
    control = new WeightedMeshControl();
  }

  // ── needsInitialization flag ──

  @Test
  public void needsInitialization_truByDefault() throws Exception {
    boolean needs = getNeedsInit();
    assertTrue("New WeightedMeshControl should need initialization", needs);
  }

  // ── Default null fields ──

  @Test
  public void weightedMesh_nullByDefault() {
    assertNull(control.weightedMesh);
  }

  @Test
  public void vertexBuffer_nullByDefault() {
    assertNull(control.vertexBuffer);
  }

  @Test
  public void normalBuffer_nullByDefault() {
    assertNull(control.normalBuffer);
  }

  @Test
  public void textCoordBuffer_nullByDefault() {
    assertNull(control.textCoordBuffer);
  }

  @Test
  public void indexBuffer_nullByDefault() {
    assertNull(control.indexBuffer);
  }

  // ── preProcess resets arrays ──

  @Test
  public void preProcess_resetsJointMatricesToNaN() throws Exception {
    int count = 3;
    setupArrays(count);

    // Populate with non-NaN values
    AffineMatrix4x4[] matrices = getJointMatrices();
    for (int i = 0; i < count; i++) {
      matrices[i] = AffineMatrix4x4.IDENTITY;
    }

    control.preProcess();

    matrices = getJointMatrices();
    for (int i = 0; i < count; i++) {
      assertTrue("Matrix at index " + i + " should be NaN after preProcess",
          matrices[i].isNaN());
    }
  }

  @Test
  public void preProcess_resetsWeightsToZero() throws Exception {
    int count = 3;
    setupArrays(count);

    // Populate with non-zero weights
    float[] weights = getWeights();
    for (int i = 0; i < count; i++) {
      weights[i] = 1.0f;
    }

    control.preProcess();

    weights = getWeights();
    for (int i = 0; i < count; i++) {
      assertEquals("Weight at index " + i + " should be 0 after preProcess",
          0.0f, weights[i], 0.0001f);
    }
  }

  @Test
  public void preProcess_preservesArrayLength() throws Exception {
    int count = 5;
    setupArrays(count);

    control.preProcess();

    assertEquals(count, getJointMatrices().length);
    assertEquals(count, getWeights().length);
  }

  @Test
  public void preProcess_singleElement() throws Exception {
    setupArrays(1);
    getJointMatrices()[0] = AffineMatrix4x4.IDENTITY;
    getWeights()[0] = 0.75f;

    control.preProcess();

    assertTrue(getJointMatrices()[0].isNaN());
    assertEquals(0.0f, getWeights()[0], 0.0001f);
  }

  @Test
  public void preProcess_largeArray() throws Exception {
    int count = 100;
    setupArrays(count);

    // Fill with non-default values
    AffineMatrix4x4[] matrices = getJointMatrices();
    float[] weights = getWeights();
    AffineMatrix4x4 nonIdentity = new AffineMatrix4x4(
        new OrthogonalMatrix3x3(
            new Vector3(0, 1, 0),
            new Vector3(-1, 0, 0),
            new Vector3(0, 0, 1)),
        new Point3(1, 2, 3));
    for (int i = 0; i < count; i++) {
      matrices[i] = nonIdentity;
      weights[i] = 0.5f;
    }

    control.preProcess();

    matrices = getJointMatrices();
    weights = getWeights();
    for (int i = 0; i < count; i++) {
      assertTrue("Matrix " + i + " should be NaN", matrices[i].isNaN());
      assertEquals("Weight " + i + " should be 0", 0.0f, weights[i], 0.0001f);
    }
  }

  @Test
  public void preProcess_calledTwice_stillResets() throws Exception {
    int count = 2;
    setupArrays(count);

    control.preProcess();
    // Modify after first preProcess
    getJointMatrices()[0] = AffineMatrix4x4.IDENTITY;
    getWeights()[0] = 0.99f;

    control.preProcess();
    assertTrue(getJointMatrices()[0].isNaN());
    assertEquals(0.0f, getWeights()[0], 0.0001f);
  }

  // ── Initialize with null mesh ──

  @Test
  public void initialize_withNull_doesNotCrash() {
    // initialize(null) should not NPE — internalInitialize checks for null
    control.initialize(null);
    // weightedMesh is set to null, nothing else initialized
    assertNull(control.weightedMesh);
  }

  @Test
  public void initialize_withNull_arraysRemainNull() throws Exception {
    control.initialize(null);
    assertNull(getJointMatricesOrNull());
    assertNull(getWeightsOrNull());
  }

  // ── Helpers ──

  private void setupArrays(int count) throws Exception {
    AffineMatrix4x4[] matrices = new AffineMatrix4x4[count];
    float[] weights = new float[count];
    for (int i = 0; i < count; i++) {
      matrices[i] = AffineMatrix4x4.NaN;
      weights[i] = 0f;
    }
    Field mf = WeightedMeshControl.class.getDeclaredField("weightedJointMatrices");
    mf.setAccessible(true);
    mf.set(control, matrices);

    Field wf = WeightedMeshControl.class.getDeclaredField("weights");
    wf.setAccessible(true);
    wf.set(control, weights);
  }

  private AffineMatrix4x4[] getJointMatrices() throws Exception {
    Field f = WeightedMeshControl.class.getDeclaredField("weightedJointMatrices");
    f.setAccessible(true);
    return (AffineMatrix4x4[]) f.get(control);
  }

  private AffineMatrix4x4[] getJointMatricesOrNull() throws Exception {
    Field f = WeightedMeshControl.class.getDeclaredField("weightedJointMatrices");
    f.setAccessible(true);
    return (AffineMatrix4x4[]) f.get(control);
  }

  private float[] getWeights() throws Exception {
    Field f = WeightedMeshControl.class.getDeclaredField("weights");
    f.setAccessible(true);
    return (float[]) f.get(control);
  }

  private float[] getWeightsOrNull() throws Exception {
    Field f = WeightedMeshControl.class.getDeclaredField("weights");
    f.setAccessible(true);
    return (float[]) f.get(control);
  }

  private boolean getNeedsInit() throws Exception {
    Field f = WeightedMeshControl.class.getDeclaredField("needsInitialization");
    f.setAccessible(true);
    return f.getBoolean(control);
  }
}
