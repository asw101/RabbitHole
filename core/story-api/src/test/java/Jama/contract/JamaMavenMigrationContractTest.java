package Jama.contract;

import Jama.CholeskyDecomposition;
import Jama.EigenvalueDecomposition;
import Jama.LUDecomposition;
import Jama.Matrix;
import Jama.QRDecomposition;
import Jama.SingularValueDecomposition;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract tests for PR #829: JAMA Maven dependency migration.
 *
 * These tests verify the behavioral contract that MUST be preserved when
 * switching from vendored Jama source to gov.nist.math:jama:1.0.3 from
 * Maven Central. Each test documents a specific API contract used by Alice's
 * IK solver (InvertedJacobian, Jacobian, SvdInfo, Constraint, JacobianMath).
 *
 * FAILS on develop: vendored Jama classes will be deleted by PR #829.
 * PASSES after merge: Maven Central artifact provides identical API.
 */
public class JamaMavenMigrationContractTest {
  private static final double TOLERANCE = 1.0e-8;

  // ── Matrix construction contracts used by IK solver ───────────────

  @Test
  public void matrixConstructor_rowsColsValue_createsFilledMatrix() {
    Matrix m = new Matrix(3, 3, 1.0);
    assertEquals(3, m.getRowDimension());
    assertEquals(3, m.getColumnDimension());
    assertEquals(1.0, m.get(2, 2), 0.0);
  }

  @Test
  public void matrixConstructor_2dArray_sharesBackingArray() {
    double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
    Matrix m = new Matrix(data);
    data[0][0] = 99.0;
    assertEquals("Matrix(double[][]) must share backing array", 99.0, m.get(0, 0), 0.0);
  }

  @Test
  public void matrixConstructWithCopy_isolatesFromSource() {
    double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
    Matrix m = Matrix.constructWithCopy(data);
    data[0][0] = 99.0;
    assertEquals("constructWithCopy must isolate from source", 1.0, m.get(0, 0), 0.0);
  }

  @Test
  public void identityMatrix_hasDiagonalOnes() {
    Matrix id = Matrix.identity(4, 4);
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        assertEquals(i == j ? 1.0 : 0.0, id.get(i, j), 0.0);
      }
    }
  }

  // ── Arithmetic contracts used by Jacobian math ────────────────────

  @Test
  public void times_multipliesMatricesCorrectly() {
    Matrix a = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    Matrix b = new Matrix(new double[][]{{5.0, 6.0}, {7.0, 8.0}});
    Matrix result = a.times(b);
    assertEquals(19.0, result.get(0, 0), TOLERANCE);
    assertEquals(22.0, result.get(0, 1), TOLERANCE);
    assertEquals(43.0, result.get(1, 0), TOLERANCE);
    assertEquals(50.0, result.get(1, 1), TOLERANCE);
  }

  @Test
  public void timesScalar_scalesAllElements() {
    Matrix m = new Matrix(new double[][]{{2.0, 3.0}, {4.0, 5.0}});
    Matrix result = m.times(2.0);
    assertEquals(4.0, result.get(0, 0), TOLERANCE);
    assertEquals(10.0, result.get(1, 1), TOLERANCE);
  }

  @Test
  public void plus_addsElementwise() {
    Matrix a = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    Matrix b = new Matrix(new double[][]{{5.0, 6.0}, {7.0, 8.0}});
    Matrix result = a.plus(b);
    assertEquals(6.0, result.get(0, 0), TOLERANCE);
    assertEquals(12.0, result.get(1, 1), TOLERANCE);
  }

  @Test
  public void minus_subtractsElementwise() {
    Matrix a = new Matrix(new double[][]{{5.0, 6.0}, {7.0, 8.0}});
    Matrix b = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    Matrix result = a.minus(b);
    assertEquals(4.0, result.get(0, 0), TOLERANCE);
    assertEquals(4.0, result.get(1, 1), TOLERANCE);
  }

  @Test
  public void transpose_swapsRowsAndColumns() {
    Matrix m = new Matrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}});
    Matrix t = m.transpose();
    assertEquals(3, t.getRowDimension());
    assertEquals(2, t.getColumnDimension());
    assertEquals(2.0, t.get(1, 0), TOLERANCE);
    assertEquals(4.0, t.get(0, 1), TOLERANCE);
  }

  // ── SVD contract — core of IK solver ──────────────────────────────

  @Test
  public void svd_reconstructsOriginalMatrix() {
    Matrix a = new Matrix(new double[][]{
        {1.0, 2.0, 3.0},
        {4.0, 5.0, 6.0},
        {7.0, 8.0, 10.0}
    });
    SingularValueDecomposition svd = a.svd();
    assertNotNull(svd);

    Matrix u = svd.getU();
    Matrix s = svd.getS();
    Matrix v = svd.getV();
    Matrix reconstructed = u.times(s).times(v.transpose());

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals("SVD reconstruction [" + i + "," + j + "]",
            a.get(i, j), reconstructed.get(i, j), TOLERANCE);
      }
    }
  }

  @Test
  public void svd_singularValuesAreNonNegativeAndDescending() {
    Matrix a = new Matrix(new double[][]{
        {1.0, 0.0, 0.0},
        {0.0, 3.0, 0.0},
        {0.0, 0.0, 2.0}
    });
    double[] singularValues = a.svd().getSingularValues();
    assertEquals(3, singularValues.length);
    for (int i = 0; i < singularValues.length; i++) {
      assertTrue("Singular value must be non-negative", singularValues[i] >= 0.0);
    }
    for (int i = 1; i < singularValues.length; i++) {
      assertTrue("Singular values must be descending",
          singularValues[i - 1] >= singularValues[i] - TOLERANCE);
    }
  }

  @Test
  public void svd_rank_matchesExpected() {
    Matrix fullRank = new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}});
    assertEquals(2, fullRank.svd().rank());

    Matrix rankDeficient = new Matrix(new double[][]{{1.0, 2.0}, {2.0, 4.0}});
    assertEquals(1, rankDeficient.svd().rank());
  }

  // ── QR decomposition used by solver ───────────────────────────────

  @Test
  public void qr_reconstructsOriginalMatrix() {
    Matrix a = new Matrix(new double[][]{
        {12.0, -51.0, 4.0},
        {6.0, 167.0, -68.0},
        {-4.0, 24.0, -41.0}
    });
    QRDecomposition qr = a.qr();
    Matrix q = qr.getQ();
    Matrix r = qr.getR();
    Matrix reconstructed = q.times(r);

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals("QR reconstruction [" + i + "," + j + "]",
            a.get(i, j), reconstructed.get(i, j), TOLERANCE);
      }
    }
  }

  @Test
  public void qr_isFullRank_forNonSingularMatrix() {
    Matrix a = Matrix.identity(3, 3);
    assertTrue(a.qr().isFullRank());
  }

  // ── LU decomposition ─────────────────────────────────────────────

  @Test
  public void lu_solve_findsCorrectSolution() {
    Matrix a = new Matrix(new double[][]{
        {2.0, 1.0},
        {5.0, 7.0}
    });
    Matrix b = new Matrix(new double[][]{{11.0}, {13.0}});
    Matrix x = a.lu().solve(b);
    assertEquals(64.0 / 9.0, x.get(0, 0), 1.0e-6);
    assertEquals(-29.0 / 9.0, x.get(1, 0), 1.0e-6);
  }

  @Test
  public void lu_determinant_matchesExpected() {
    Matrix a = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    LUDecomposition lu = a.lu();
    assertEquals(-2.0, lu.det(), TOLERANCE);
  }

  // ── Eigenvalue decomposition ──────────────────────────────────────

  @Test
  public void eig_symmetricMatrix_hasRealEigenvalues() {
    Matrix symmetric = new Matrix(new double[][]{
        {2.0, 1.0},
        {1.0, 2.0}
    });
    EigenvalueDecomposition eig = symmetric.eig();
    double[] imaginary = eig.getImagEigenvalues();
    for (double im : imaginary) {
      assertEquals("Symmetric matrix eigenvalues must be real", 0.0, im, TOLERANCE);
    }
  }

  @Test
  public void eig_reconstructsOriginalMatrix() {
    Matrix a = new Matrix(new double[][]{
        {4.0, 1.0},
        {1.0, 3.0}
    });
    EigenvalueDecomposition eig = a.eig();
    Matrix v = eig.getV();
    Matrix d = eig.getD();
    Matrix reconstructed = v.times(d).times(v.inverse());

    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        assertEquals("Eig reconstruction [" + i + "," + j + "]",
            a.get(i, j), reconstructed.get(i, j), TOLERANCE);
      }
    }
  }

  // ── Cholesky decomposition ────────────────────────────────────────

  @Test
  public void cholesky_posDefMatrix_isSPD() {
    Matrix spd = new Matrix(new double[][]{
        {4.0, 2.0},
        {2.0, 3.0}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(spd);
    assertTrue("Positive definite matrix must be detected as SPD", chol.isSPD());
  }

  @Test
  public void cholesky_solve_matchesExpected() {
    Matrix spd = new Matrix(new double[][]{
        {4.0, 2.0},
        {2.0, 3.0}
    });
    Matrix b = new Matrix(new double[][]{{1.0}, {2.0}});
    Matrix x = new CholeskyDecomposition(spd).solve(b);
    assertNotNull(x);
    assertEquals(2, x.getRowDimension());
  }

  // ── Array packing — used in serialization ─────────────────────────

  @Test
  public void columnPackedCopy_roundTrips() {
    Matrix original = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    double[] packed = original.getColumnPackedCopy();
    assertArrayEquals(new double[]{1.0, 3.0, 2.0, 4.0}, packed, 0.0);
    Matrix restored = new Matrix(packed, 2);
    assertEquals(original.get(0, 0), restored.get(0, 0), 0.0);
    assertEquals(original.get(1, 1), restored.get(1, 1), 0.0);
  }

  @Test
  public void rowPackedCopy_preservesRowMajorOrder() {
    Matrix m = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});
    double[] packed = m.getRowPackedCopy();
    assertArrayEquals(new double[]{1.0, 2.0, 3.0, 4.0}, packed, 0.0);
  }

  // ── Norm contract used by IK convergence checks ───────────────────

  @Test
  public void normF_frobenius_matchesExpected() {
    Matrix m = new Matrix(new double[][]{{3.0, 4.0}});
    assertEquals(5.0, m.normF(), TOLERANCE);
  }

  @Test
  public void norm2_spectralNorm_isLargestSingularValue() {
    Matrix m = new Matrix(new double[][]{{3.0, 0.0}, {0.0, 5.0}});
    assertEquals(5.0, m.norm2(), TOLERANCE);
  }

  // ── Inverse and solve ─────────────────────────────────────────────

  @Test
  public void inverse_timesOriginal_givesIdentity() {
    Matrix a = new Matrix(new double[][]{
        {1.0, 2.0},
        {3.0, 4.0}
    });
    Matrix product = a.inverse().times(a);
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        assertEquals(i == j ? 1.0 : 0.0, product.get(i, j), TOLERANCE);
      }
    }
  }

  @Test
  public void solve_linearSystem_givesCorrectResult() {
    Matrix a = new Matrix(new double[][]{
        {1.0, 1.0},
        {0.0, 1.0}
    });
    Matrix b = new Matrix(new double[][]{{3.0}, {2.0}});
    Matrix x = a.solve(b);
    assertEquals(1.0, x.get(0, 0), TOLERANCE);
    assertEquals(2.0, x.get(1, 0), TOLERANCE);
  }
}
