package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Characterization tests for Jacobian — the core matrix class in the IK solver
 * representing velocity contributions of joint axes.
 *
 * Context-dependent: uses IkEnforcerContext for getGlobalIndexForAxis (in
 * multiplyWithAngleDeltas) and indexToAxis.size() (in
 * multiplyDisplacementWithInverseForMoving).
 *
 * Tests use a 3-axis context with known matrices to verify:
 * - Basic construction
 * - Augmenting constructor (merging multiple Jacobians)
 * - J × Δθ multiplication
 * - J⁻¹ × Δx inverse multiplication
 * - SVD-based inverse creation
 */
public class JacobianTest {

  private static final double DELTA = 1e-6;

  private IkEnforcerContextStub context;
  private List<JacobianAxis> axes;

  @Before
  public void setUp() {
    context = new IkEnforcerContextStub(3);
    axes = context.getIndexToAxis();
  }

  @Test
  public void basicConstructorSetsMatrixDimensions() {
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(3, cols, context);

    assertEquals(3, j.getRowCount());
    assertEquals(2, j.matrix.getColumnDimension());
  }

  @Test
  public void matrixConstructorStoresMatrixDirectly() {
    Matrix m = new Matrix(2, 3);
    m.set(0, 0, 1.0);
    m.set(1, 2, 2.0);
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};

    Jacobian j = new Jacobian(m, cols, context);

    assertEquals(1.0, j.matrix.get(0, 0), DELTA);
    assertEquals(2.0, j.matrix.get(1, 2), DELTA);
  }

  @Test
  public void multiplyWithAngleDeltasProducesCorrectDisplacement() {
    // 2×3 Jacobian (2 displacement rows, 3 axes)
    Matrix m = new Matrix(new double[][]{
        {1.0, 0.0, 2.0},
        {0.0, 3.0, 0.0}
    });
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian j = new Jacobian(m, cols, context);

    AngleDeltas ad = new AngleDeltas(3, context);
    ad.storage[0] = 1.0; // axis 0
    ad.storage[1] = 2.0; // axis 1
    ad.storage[2] = 3.0; // axis 2

    // Row 0: 1*1 + 0*2 + 2*3 = 7
    // Row 1: 0*1 + 3*2 + 0*3 = 6
    Displacement result = j.multiplyWithAngleDeltas(ad);

    assertEquals(2, result.size());
    assertEquals(7.0, result.storage[0], DELTA);
    assertEquals(6.0, result.storage[1], DELTA);
  }

  @Test
  public void multiplyWithZeroAngleDeltasProducesZeroDisplacement() {
    Matrix m = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);

    AngleDeltas ad = new AngleDeltas(3, context);
    // all zeros

    Displacement result = j.multiplyWithAngleDeltas(ad);
    assertEquals(0.0, result.storage[0], DELTA);
    assertEquals(0.0, result.storage[1], DELTA);
  }

  @Test
  public void multiplyDisplacementWithInverseProducesAngleDeltas() {
    // Identity Jacobian: 3×3
    Matrix m = Matrix.identity(3, 3);
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian j = new Jacobian(m, cols, context);

    Displacement disp = new Displacement(new double[]{1.0, 2.0, 3.0});
    AngleDeltas result = j.multiplyDisplacementWithInverseForMoving(disp);

    assertEquals(3, result.storage.length);
    // For identity Jacobian, damped inverse ≈ 0.990099 * I
    double dampedFactor = 1.0 / (1.0 + 0.01);
    assertEquals(1.0 * dampedFactor, result.storage[0], DELTA);
    assertEquals(2.0 * dampedFactor, result.storage[1], DELTA);
    assertEquals(3.0 * dampedFactor, result.storage[2], DELTA);
  }

  @Test
  public void inverseResultHasCorrectGlobalIndexMapping() {
    // 2-column Jacobian using axes 0 and 2 (skipping 1)
    Matrix m = Matrix.identity(2, 2);
    JacobianAxis[] cols = {axes.get(0), axes.get(2)};
    Jacobian j = new Jacobian(m, cols, context);

    Displacement disp = new Displacement(new double[]{5.0, 7.0});
    AngleDeltas result = j.multiplyDisplacementWithInverseForMoving(disp);

    assertEquals(3, result.storage.length);
    // Index 1 should be zero (axis 1 not in this Jacobian)
    assertEquals(0.0, result.storage[1], DELTA);
    // Indices 0 and 2 should have values
    assertTrue(Math.abs(result.storage[0]) > DELTA);
    assertTrue(Math.abs(result.storage[2]) > DELTA);
  }

  @Test
  public void createInverseForNullProjectionReturnsInvertedJacobian() {
    Matrix m = Matrix.identity(3, 3);
    JacobianAxis[] cols = {axes.get(0), axes.get(1), axes.get(2)};
    Jacobian j = new Jacobian(m, cols, context);

    InvertedJacobian inv = j.createInverseForNullProjection();

    assertNotNull(inv);
    assertNotNull(inv.matrix);
    assertSame("InvertedJacobian should reference source Jacobian", j, inv.straight);
  }

  @Test
  public void svdInfoIsCachedAndInvalidated() {
    Matrix m = Matrix.identity(2, 2);
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);

    // First call creates SVD
    j.createInverseForNullProjection();
    assertNotNull(j.svdInfo);

    // matrixWasUpdated clears cache
    j.matrixWasUpdated();
    assertNull(j.svdInfo);
  }

  @Test
  public void augmentingConstructorMergesJacobians() {
    // J1: 2 rows, axes [0, 1]
    Matrix m1 = new Matrix(new double[][]{{1, 2}, {3, 4}});
    JacobianAxis[] cols1 = {axes.get(0), axes.get(1)};
    Jacobian j1 = new Jacobian(m1, cols1, context);

    // J2: 1 row, axes [1, 2]
    Matrix m2 = new Matrix(new double[][]{{5, 6}});
    JacobianAxis[] cols2 = {axes.get(1), axes.get(2)};
    Jacobian j2 = new Jacobian(m2, cols2, context);

    Jacobian augmented = new Jacobian(new Jacobian[]{j1, j2}, context);

    // Augmented should have 3 rows (2+1) and 3 columns (axes 0,1,2)
    assertEquals(3, augmented.getRowCount());
    assertEquals(3, augmented.matrix.getColumnDimension());
  }

  @Test
  public void getRowCountMatchesMatrixRows() {
    Matrix m = new Matrix(5, 2);
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);
    assertEquals(5, j.getRowCount());
  }
}
