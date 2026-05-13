package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Characterization tests for SvdInfo — SVD decomposition cache providing
 * damped and regular pseudo-inverses of a Jacobian matrix.
 * Uses known matrices where the pseudo-inverse can be verified analytically.
 */
public class SvdInfoTest {

  private static final double DELTA = 1e-6;

  @Test
  public void tallMatrixNotTransposed() {
    // 3x2 matrix (m >= n), should NOT be transposed
    Matrix m = new Matrix(new double[][]{
        {1, 0},
        {0, 1},
        {0, 0}
    });
    SvdInfo info = new SvdInfo(m);
    assertFalse(info.isTransposed);
  }

  @Test
  public void wideMatrixIsTransposed() {
    // 2x3 matrix (m < n), should be transposed
    Matrix m = new Matrix(new double[][]{
        {1, 0, 0},
        {0, 1, 0}
    });
    SvdInfo info = new SvdInfo(m);
    assertTrue(info.isTransposed);
  }

  @Test
  public void squareMatrixNotTransposed() {
    Matrix m = Matrix.identity(3, 3);
    SvdInfo info = new SvdInfo(m);
    assertFalse(info.isTransposed);
  }

  @Test
  public void dampedInverseIsCached() {
    Matrix m = Matrix.identity(2, 2);
    SvdInfo info = new SvdInfo(m);

    Matrix first = info.createDampedInverse();
    Matrix second = info.createDampedInverse();
    assertSame("Damped inverse should be cached", first, second);
    assertTrue(info.isDampedReady);
  }

  @Test
  public void regularInverseIsCached() {
    Matrix m = Matrix.identity(2, 2);
    SvdInfo info = new SvdInfo(m);

    Matrix first = info.createRegularInverse();
    Matrix second = info.createRegularInverse();
    assertSame("Regular inverse should be cached", first, second);
    assertTrue(info.isRegularReady);
  }

  @Test
  public void identityDampedInverseApproximatesIdentity() {
    Matrix m = Matrix.identity(3, 3);
    SvdInfo info = new SvdInfo(m);
    Matrix inv = info.createDampedInverse();

    // For identity, damped inverse is d/(d^2 + lambda^2) on diag
    // With d=1 and lambda=0.1: 1/(1+0.01) = 0.990099...
    assertEquals(3, inv.getRowDimension());
    assertEquals(3, inv.getColumnDimension());
    for (int i = 0; i < 3; i++) {
      double expected = 1.0 / (1.0 + 0.01); // SVD_DAMPING_CONSTANT=0.1
      assertEquals(expected, inv.get(i, i), DELTA);
    }
  }

  @Test
  public void identityRegularInverseIsIdentity() {
    Matrix m = Matrix.identity(3, 3);
    SvdInfo info = new SvdInfo(m);
    Matrix inv = info.createRegularInverse();

    assertEquals(3, inv.getRowDimension());
    assertEquals(3, inv.getColumnDimension());
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        double expected = (i == j) ? 1.0 : 0.0;
        assertEquals(expected, inv.get(i, j), DELTA);
      }
    }
  }

  @Test
  public void dampedAndRegularInversesDiffer() {
    Matrix m = Matrix.identity(2, 2);
    SvdInfo info = new SvdInfo(m);

    Matrix damped = info.createDampedInverse();
    // Create new SvdInfo since sForDamped/sForRegular share storage concern
    SvdInfo info2 = new SvdInfo(Matrix.identity(2, 2));
    Matrix regular = info2.createRegularInverse();

    // Damped should attenuate singular values, so diagonal should differ
    assertNotEquals(damped.get(0, 0), regular.get(0, 0), 1e-10);
  }

  @Test
  public void wideMatrixInverseHasCorrectDimensions() {
    // 2x4 → inverse should be 4x2
    Matrix m = new Matrix(new double[][]{
        {1, 0, 0, 0},
        {0, 1, 0, 0}
    });
    SvdInfo info = new SvdInfo(m);
    Matrix inv = info.createDampedInverse();

    assertEquals(4, inv.getRowDimension());
    assertEquals(2, inv.getColumnDimension());
  }

  @Test
  public void tallMatrixInverseHasCorrectDimensions() {
    // 4x2 → inverse should be 2x4
    Matrix m = new Matrix(new double[][]{
        {1, 0},
        {0, 1},
        {0, 0},
        {0, 0}
    });
    SvdInfo info = new SvdInfo(m);
    Matrix inv = info.createDampedInverse();

    assertEquals(2, inv.getRowDimension());
    assertEquals(4, inv.getColumnDimension());
  }
}
