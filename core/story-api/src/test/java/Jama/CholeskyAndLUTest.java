package Jama;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link CholeskyDecomposition}, {@link LUDecomposition}, and
 * {@link QRDecomposition}. All pure math — no AWT or rendering.
 */
public class CholeskyAndLUTest {

  private static final double TOL = 1e-9;

  // ═══════════════════════════════════════════════════════════════════════
  //  CholeskyDecomposition
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void cholesky_isSPD_trueForPositiveDefiniteMatrix() {
    // Symmetric positive-definite 3×3
    Matrix A = new Matrix(new double[][] {
        {4, 12, -16},
        {12, 37, -43},
        {-16, -43, 98}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    assertTrue("Known SPD matrix must report isSPD=true", chol.isSPD());
  }

  @Test
  public void cholesky_getL_returnsLowerTriangular() {
    Matrix A = new Matrix(new double[][] {
        {4, 12, -16},
        {12, 37, -43},
        {-16, -43, 98}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    Matrix L = chol.getL();
    assertNotNull(L);
    int n = L.getRowDimension();
    assertEquals(3, n);
    // Upper triangle must be zero
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        assertEquals("L[" + i + "][" + j + "] should be 0",
            0.0, L.get(i, j), TOL);
      }
    }
  }

  @Test
  public void cholesky_LtimesLtranspose_reconstructsA() {
    Matrix A = new Matrix(new double[][] {
        {4, 12, -16},
        {12, 37, -43},
        {-16, -43, 98}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    Matrix L = chol.getL();
    Matrix LLt = L.times(L.transpose());
    assertMatrixEquals(A, LLt);
  }

  @Test
  public void cholesky_solve_producesCorrectSolution() {
    Matrix A = new Matrix(new double[][] {
        {25, 15, -5},
        {15, 18, 0},
        {-5, 0, 11}
    });
    Matrix b = new Matrix(new double[][] {
        {40},
        {33},
        {6}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    assertTrue(chol.isSPD());
    Matrix x = chol.solve(b);
    Matrix residual = A.times(x).minus(b);
    assertEquals("Residual norm should be ~0", 0.0, residual.normF(), TOL);
  }

  @Test
  public void cholesky_isSPD_falseForNonSPD() {
    // Not positive-definite (eigenvalue < 0)
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {2, 1}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    assertFalse(chol.isSPD());
  }

  @Test
  public void cholesky_isSPD_falseForNonSymmetric() {
    Matrix A = new Matrix(new double[][] {
        {4, 1},
        {2, 4}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    assertFalse(chol.isSPD());
  }

  @Test(expected = RuntimeException.class)
  public void cholesky_solve_throwsIfNotSPD() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {2, 1}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    Matrix b = new Matrix(new double[][] {{1}, {1}});
    chol.solve(b);
  }

  @Test(expected = IllegalArgumentException.class)
  public void cholesky_solve_throwsOnDimensionMismatch() {
    Matrix A = new Matrix(new double[][] {
        {4, 12, -16},
        {12, 37, -43},
        {-16, -43, 98}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    Matrix b = new Matrix(new double[][] {{1}, {2}});  // 2 rows != 3
    chol.solve(b);
  }

  @Test
  public void cholesky_2x2_identity() {
    Matrix I = Matrix.identity(2, 2);
    CholeskyDecomposition chol = new CholeskyDecomposition(I);
    assertTrue(chol.isSPD());
    assertMatrixEquals(I, chol.getL());
  }

  @Test
  public void cholesky_solve_multipleRHS() {
    Matrix A = new Matrix(new double[][] {
        {2, 1},
        {1, 2}
    });
    Matrix B = new Matrix(new double[][] {
        {3, 5},
        {3, 5}
    });
    CholeskyDecomposition chol = new CholeskyDecomposition(A);
    assertTrue(chol.isSPD());
    Matrix X = chol.solve(B);
    Matrix residual = A.times(X).minus(B);
    assertEquals(0.0, residual.normF(), TOL);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  LUDecomposition
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void lu_isNonsingular_trueForInvertibleMatrix() {
    Matrix A = new Matrix(new double[][] {
        {2, 3},
        {5, 7}
    });
    LUDecomposition lu = new LUDecomposition(A);
    assertTrue(lu.isNonsingular());
  }

  @Test
  public void lu_isNonsingular_falseForSingularMatrix() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {2, 4}
    });
    LUDecomposition lu = new LUDecomposition(A);
    assertFalse(lu.isNonsingular());
  }

  @Test
  public void lu_LtimesU_equalsPtimesA() {
    Matrix A = new Matrix(new double[][] {
        {2, 5, 3},
        {4, 2, 1},
        {6, 7, 9}
    });
    LUDecomposition lu = new LUDecomposition(A);
    Matrix L = lu.getL();
    Matrix U = lu.getU();
    int[] piv = lu.getPivot();

    // Reconstruct P*A
    int m = A.getRowDimension();
    int n = A.getColumnDimension();
    Matrix PA = new Matrix(m, n);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        PA.set(i, j, A.get(piv[i], j));
      }
    }
    Matrix LU = L.times(U);
    assertMatrixEquals(PA, LU);
  }

  @Test
  public void lu_det_returnsCorrectDeterminant() {
    Matrix A = new Matrix(new double[][] {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    });
    LUDecomposition lu = new LUDecomposition(A);
    // det = 1(5*0-6*8) - 2(4*0-6*7) + 3(4*8-5*7) = -48 + 84 - 9 = 27
    assertEquals(27.0, lu.det(), TOL);
  }

  @Test
  public void lu_det_identityIsOne() {
    Matrix I = Matrix.identity(4, 4);
    LUDecomposition lu = new LUDecomposition(I);
    assertEquals(1.0, lu.det(), TOL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lu_det_throwsForNonSquare() {
    // Use m > n so the LU constructor succeeds (m < n causes AIOOBE)
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4},
        {5, 6}
    });
    new LUDecomposition(A).det();
  }

  @Test
  public void lu_solve_producesCorrectSolution() {
    Matrix A = new Matrix(new double[][] {
        {3, 2},
        {1, 2}
    });
    Matrix b = new Matrix(new double[][] {{18}, {14}});
    LUDecomposition lu = new LUDecomposition(A);
    Matrix x = lu.solve(b);
    // x = [2, 6]
    assertEquals(2.0, x.get(0, 0), TOL);
    assertEquals(6.0, x.get(1, 0), TOL);
  }

  @Test(expected = RuntimeException.class)
  public void lu_solve_throwsIfSingular() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {2, 4}
    });
    Matrix b = new Matrix(new double[][] {{1}, {2}});
    new LUDecomposition(A).solve(b);
  }

  @Test(expected = IllegalArgumentException.class)
  public void lu_solve_throwsOnDimensionMismatch() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4}
    });
    Matrix b = new Matrix(new double[][] {{1}, {2}, {3}});
    new LUDecomposition(A).solve(b);
  }

  @Test
  public void lu_getL_isLowerTriangular() {
    Matrix A = new Matrix(new double[][] {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    });
    LUDecomposition lu = new LUDecomposition(A);
    Matrix L = lu.getL();
    int m = L.getRowDimension();
    int n = L.getColumnDimension();
    for (int i = 0; i < m; i++) {
      for (int j = i + 1; j < n; j++) {
        assertEquals(0.0, L.get(i, j), TOL);
      }
      // Diagonal must be 1.0
      if (i < n) {
        assertEquals(1.0, L.get(i, i), TOL);
      }
    }
  }

  @Test
  public void lu_getU_isUpperTriangular() {
    Matrix A = new Matrix(new double[][] {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    });
    LUDecomposition lu = new LUDecomposition(A);
    Matrix U = lu.getU();
    int n = U.getRowDimension();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        assertEquals(0.0, U.get(i, j), TOL);
      }
    }
  }

  @Test
  public void lu_getPivot_hasCorrectLength() {
    Matrix A = new Matrix(new double[][] {
        {2, 1},
        {5, 3}
    });
    LUDecomposition lu = new LUDecomposition(A);
    assertEquals(2, lu.getPivot().length);
  }

  @Test
  public void lu_getDoublePivot_matchesPivot() {
    Matrix A = new Matrix(new double[][] {
        {0, 1},
        {1, 0}
    });
    LUDecomposition lu = new LUDecomposition(A);
    int[] piv = lu.getPivot();
    double[] dpiv = lu.getDoublePivot();
    assertEquals(piv.length, dpiv.length);
    for (int i = 0; i < piv.length; i++) {
      assertEquals((double) piv[i], dpiv[i], TOL);
    }
  }

  @Test
  public void lu_solve_multipleRHS() {
    Matrix A = new Matrix(new double[][] {
        {1, 0, 0},
        {0, 2, 0},
        {0, 0, 3}
    });
    Matrix B = new Matrix(new double[][] {
        {1, 4},
        {2, 6},
        {3, 9}
    });
    Matrix X = new LUDecomposition(A).solve(B);
    assertEquals(1.0, X.get(0, 0), TOL);
    assertEquals(4.0, X.get(0, 1), TOL);
    assertEquals(1.0, X.get(1, 0), TOL);
    assertEquals(3.0, X.get(1, 1), TOL);
    assertEquals(1.0, X.get(2, 0), TOL);
    assertEquals(3.0, X.get(2, 1), TOL);
  }

  @Test
  public void lu_4x4_detAndSolve() {
    Matrix A = new Matrix(new double[][] {
        {2, 1, 1, 0},
        {4, 3, 3, 1},
        {8, 7, 9, 5},
        {6, 7, 9, 8}
    });
    LUDecomposition lu = new LUDecomposition(A);
    assertTrue(lu.isNonsingular());
    // Verify through L*U = P*A reconstruction instead of hard-coded det
    Matrix L = lu.getL();
    Matrix U = lu.getU();
    int[] piv = lu.getPivot();
    int m = A.getRowDimension();
    int n = A.getColumnDimension();
    Matrix PA = new Matrix(m, n);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        PA.set(i, j, A.get(piv[i], j));
      }
    }
    assertMatrixEquals(PA, L.times(U));

    Matrix b = new Matrix(new double[][] {{1}, {1}, {1}, {1}});
    Matrix x = lu.solve(b);
    Matrix residual = A.times(x).minus(b);
    assertEquals(0.0, residual.normF(), TOL);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  QRDecomposition
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void qr_isFullRank_trueForFullRankMatrix() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4},
        {5, 6}
    });
    QRDecomposition qr = new QRDecomposition(A);
    assertTrue(qr.isFullRank());
  }

  @Test
  public void qr_isFullRank_falseForRankDeficient() {
    // Use zero column to guarantee rank deficiency
    Matrix A = new Matrix(new double[][] {
        {1, 0},
        {2, 0},
        {3, 0}
    });
    QRDecomposition qr = new QRDecomposition(A);
    assertFalse(qr.isFullRank());
  }

  @Test
  public void qr_QtimesR_reconstructsA() {
    Matrix A = new Matrix(new double[][] {
        {12, -51, 4},
        {6, 167, -68},
        {-4, 24, -41}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix Q = qr.getQ();
    Matrix R = qr.getR();
    Matrix QR = Q.times(R);
    assertMatrixEquals(A, QR);
  }

  @Test
  public void qr_Q_isOrthogonal() {
    Matrix A = new Matrix(new double[][] {
        {12, -51, 4},
        {6, 167, -68},
        {-4, 24, -41}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix Q = qr.getQ();
    // Q^T * Q should be identity
    Matrix QtQ = Q.transpose().times(Q);
    Matrix I = Matrix.identity(Q.getColumnDimension(), Q.getColumnDimension());
    assertMatrixEquals(I, QtQ);
  }

  @Test
  public void qr_R_isUpperTriangular() {
    Matrix A = new Matrix(new double[][] {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix R = qr.getR();
    int n = R.getRowDimension();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        assertEquals("R[" + i + "][" + j + "] should be 0",
            0.0, R.get(i, j), TOL);
      }
    }
  }

  @Test
  public void qr_getH_returnsLowerTrapezoidal() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4},
        {5, 6}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix H = qr.getH();
    assertNotNull(H);
    assertEquals(3, H.getRowDimension());
    assertEquals(2, H.getColumnDimension());
    // Upper triangle (i<j) should be zero
    for (int i = 0; i < H.getRowDimension(); i++) {
      for (int j = i + 1; j < H.getColumnDimension(); j++) {
        assertEquals(0.0, H.get(i, j), TOL);
      }
    }
  }

  @Test
  public void qr_solve_leastSquares() {
    // Overdetermined system: 3×2
    Matrix A = new Matrix(new double[][] {
        {1, 1},
        {1, 2},
        {1, 3}
    });
    Matrix b = new Matrix(new double[][] {
        {1},
        {2},
        {2}
    });
    QRDecomposition qr = new QRDecomposition(A);
    assertTrue(qr.isFullRank());
    Matrix x = qr.solve(b);
    assertNotNull(x);
    assertEquals(2, x.getRowDimension());
    assertEquals(1, x.getColumnDimension());
  }

  @Test(expected = IllegalArgumentException.class)
  public void qr_solve_throwsOnDimensionMismatch() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4},
        {5, 6}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix b = new Matrix(new double[][] {{1}, {2}}); // 2 rows != 3
    qr.solve(b);
  }

  @Test(expected = RuntimeException.class)
  public void qr_solve_throwsIfRankDeficient() {
    Matrix A = new Matrix(new double[][] {
        {1, 0},
        {2, 0},
        {3, 0}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix b = new Matrix(new double[][] {{1}, {2}, {3}});
    qr.solve(b);
  }

  @Test
  public void qr_squareMatrix_reconstructsA() {
    Matrix A = new Matrix(new double[][] {
        {1, 2},
        {3, 4}
    });
    QRDecomposition qr = new QRDecomposition(A);
    Matrix Q = qr.getQ();
    Matrix R = qr.getR();
    assertMatrixEquals(A, Q.times(R));
  }

  @Test
  public void qr_identity_decomposesCleanly() {
    Matrix I = Matrix.identity(3, 3);
    QRDecomposition qr = new QRDecomposition(I);
    assertTrue(qr.isFullRank());
    // Q*R should reconstruct to I
    Matrix QR = qr.getQ().times(qr.getR());
    assertMatrixEquals(I, QR);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Helper
  // ═══════════════════════════════════════════════════════════════════════

  private static void assertMatrixEquals(Matrix expected, Matrix actual) {
    assertEquals("Row mismatch", expected.getRowDimension(), actual.getRowDimension());
    assertEquals("Col mismatch", expected.getColumnDimension(), actual.getColumnDimension());
    for (int i = 0; i < expected.getRowDimension(); i++) {
      for (int j = 0; j < expected.getColumnDimension(); j++) {
        assertEquals("Element [" + i + "][" + j + "]",
            expected.get(i, j), actual.get(i, j), TOL);
      }
    }
  }
}
