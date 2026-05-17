package Jama;

import org.junit.Test;
import static org.junit.Assert.*;

public class EigenvalueDecompositionTest {

   private static final double TOL = 1e-9;

   // ---- helpers ----

   private static Matrix sym(double[][] a) {
      return new Matrix(a);
   }

   /** A*V should approximately equal V*D for any eigendecomposition */
   private static void assertAVeqVD(Matrix A, EigenvalueDecomposition eig) {
      Matrix V = eig.getV();
      Matrix D = eig.getD();
      Matrix AV = A.times(V);
      Matrix VD = V.times(D);
      int m = AV.getRowDimension();
      int n = AV.getColumnDimension();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            assertEquals("AV≈VD [" + i + "," + j + "]",
                  AV.get(i, j), VD.get(i, j), 1e-7);
         }
      }
   }

   /** For symmetric matrices: V*D*V' ≈ A */
   private static void assertReconstruction(Matrix A, EigenvalueDecomposition eig) {
      Matrix V = eig.getV();
      Matrix D = eig.getD();
      Matrix R = V.times(D).times(V.transpose());
      int m = A.getRowDimension();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < m; j++) {
            assertEquals("Reconstruction [" + i + "," + j + "]",
                  A.get(i, j), R.get(i, j), 1e-7);
         }
      }
   }

   /** V'*V ≈ I  (orthogonal eigenvector matrix) */
   private static void assertOrthogonal(Matrix V) {
      Matrix VtV = V.transpose().times(V);
      int n = VtV.getRowDimension();
      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            double expected = (i == j) ? 1.0 : 0.0;
            assertEquals("V'V [" + i + "," + j + "]",
                  expected, VtV.get(i, j), 1e-7);
         }
      }
   }

   // ---- Symmetric path (tred2 + tql2) ----

   @Test
   public void testSymmetric3x3() {
      Matrix A = sym(new double[][]{
            {2, 1, 0},
            {1, 3, 1},
            {0, 1, 2}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] im = eig.getImagEigenvalues();

      assertEquals(3, re.length);
      assertEquals(3, im.length);
      for (double v : im) assertEquals(0.0, v, TOL);

      // eigenvalues of {{2,1,0},{1,3,1},{0,1,2}} are 1, 2, 4
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertArrayEquals(new double[]{1.0, 2.0, 4.0}, sorted, 1e-7);

      assertReconstruction(A, eig);
      assertOrthogonal(eig.getV());
   }

   @Test
   public void testSymmetric4x4Identity() {
      Matrix A = Matrix.identity(4, 4);
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      for (double v : re) assertEquals(1.0, v, TOL);
      assertOrthogonal(eig.getV());
      assertReconstruction(A, eig);
   }

   @Test
   public void testSymmetric2x2() {
      Matrix A = sym(new double[][]{{5, 2}, {2, 5}});
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertEquals(3.0, sorted[0], TOL);
      assertEquals(7.0, sorted[1], TOL);
      assertReconstruction(A, eig);
      assertOrthogonal(eig.getV());
   }

   @Test
   public void testSymmetricDiagonal() {
      Matrix A = sym(new double[][]{
            {3, 0, 0},
            {0, 1, 0},
            {0, 0, 7}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertEquals(1.0, sorted[0], TOL);
      assertEquals(3.0, sorted[1], TOL);
      assertEquals(7.0, sorted[2], TOL);
      assertReconstruction(A, eig);
   }

   @Test
   public void testSymmetric1x1() {
      Matrix A = sym(new double[][]{{42.0}});
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      assertEquals(42.0, eig.getRealEigenvalues()[0], TOL);
      assertEquals(0.0, eig.getImagEigenvalues()[0], TOL);
      assertEquals(1.0, eig.getV().get(0, 0), TOL);
   }

   @Test
   public void testSymmetricLarge5x5() {
      // build a symmetric matrix S = A + A'
      double[][] raw = {
            {2, 1, 0, 3, 1},
            {1, 5, 2, 1, 0},
            {0, 2, 3, 1, 2},
            {3, 1, 1, 4, 1},
            {1, 0, 2, 1, 6}
      };
      Matrix A = new Matrix(raw);
      // already symmetric by construction
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      assertReconstruction(A, eig);
      assertOrthogonal(eig.getV());

      double[] im = eig.getImagEigenvalues();
      for (double v : im) assertEquals(0.0, v, TOL);
   }

   @Test
   public void testSymmetricLarge6x6() {
      // construct B'*B which is symmetric positive semi-definite
      double[][] b = {
            {1, 2, 0, 1, 0, 3},
            {0, 1, 3, 0, 2, 1},
            {2, 0, 1, 1, 1, 0},
            {1, 1, 1, 2, 0, 1},
            {0, 3, 0, 1, 2, 2},
            {1, 0, 2, 0, 1, 1}
      };
      Matrix B = new Matrix(b);
      Matrix A = B.transpose().times(B); // symmetric PSD
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      assertReconstruction(A, eig);
      assertOrthogonal(eig.getV());
      // all eigenvalues should be >= 0 for PSD
      for (double v : eig.getRealEigenvalues()) {
         assertTrue("Eigenvalue should be non-negative", v >= -1e-7);
      }
   }

   @Test
   public void testSymmetricGetD_Diagonal() {
      Matrix A = sym(new double[][]{{4, 1}, {1, 4}});
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      Matrix D = eig.getD();
      assertEquals(2, D.getRowDimension());
      assertEquals(2, D.getColumnDimension());
      // D should be diagonal for symmetric case
      assertEquals(0.0, D.get(0, 1), TOL);
      assertEquals(0.0, D.get(1, 0), TOL);
      // diagonal entries should be the eigenvalues
      double[] re = eig.getRealEigenvalues();
      assertEquals(re[0], D.get(0, 0), TOL);
      assertEquals(re[1], D.get(1, 1), TOL);
   }

   @Test
   public void testSymmetricNegativeEigenvalues() {
      // indefinite symmetric matrix
      Matrix A = sym(new double[][]{
            {1, 3},
            {3, 1}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      // eigenvalues: 1-3=-2, 1+3=4
      assertEquals(-2.0, sorted[0], TOL);
      assertEquals(4.0, sorted[1], TOL);
      assertReconstruction(A, eig);
   }

   @Test
   public void testSymmetricRepeatedEigenvalues() {
      // 3I has triple eigenvalue 3
      Matrix A = sym(new double[][]{
            {3, 0, 0},
            {0, 3, 0},
            {0, 0, 3}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      for (double v : eig.getRealEigenvalues()) {
         assertEquals(3.0, v, TOL);
      }
      assertOrthogonal(eig.getV());
   }

   // ---- Non-symmetric path (orthes + hqr2) ----

   @Test
   public void testNonSymmetricUpperTriangular() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3},
            {0, 4, 5},
            {0, 0, 6}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] im = eig.getImagEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      // eigenvalues of upper triangular = diagonal entries
      assertEquals(1.0, sorted[0], 1e-7);
      assertEquals(4.0, sorted[1], 1e-7);
      assertEquals(6.0, sorted[2], 1e-7);
      for (double v : im) assertEquals(0.0, v, 1e-7);
      assertAVeqVD(A, eig);
   }

   @Test
   public void testNonSymmetric3x3Block() {
      // {{5,2,0},{-2,5,0},{0,0,3}} has eigenvalue 3 and 5±2i
      Matrix A = new Matrix(new double[][]{
            {5, 2, 0},
            {-2, 5, 0},
            {0, 0, 3}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] im = eig.getImagEigenvalues();

      // One eigenvalue should be real=3, imag=0
      // The other pair should have real=5, imag=±2
      boolean found3 = false;
      boolean foundPlus2i = false;
      boolean foundMinus2i = false;
      for (int i = 0; i < 3; i++) {
         if (Math.abs(re[i] - 3.0) < 1e-7 && Math.abs(im[i]) < 1e-7) found3 = true;
         if (Math.abs(re[i] - 5.0) < 1e-7 && Math.abs(im[i] - 2.0) < 1e-7) foundPlus2i = true;
         if (Math.abs(re[i] - 5.0) < 1e-7 && Math.abs(im[i] + 2.0) < 1e-7) foundMinus2i = true;
      }
      assertTrue("Should find eigenvalue 3", found3);
      assertTrue("Should find eigenvalue 5+2i", foundPlus2i);
      assertTrue("Should find eigenvalue 5-2i", foundMinus2i);
   }

   @Test
   public void testNonSymmetricComplexEigenvalues4x4() {
      // Rotation-block matrix with complex eigenvalue pairs
      // Block 1: rotation by 45° scaled => eigenvalues a ± bi
      // Block 2: rotation by 60° scaled => eigenvalues c ± di
      double cos45 = Math.cos(Math.PI / 4);
      double sin45 = Math.sin(Math.PI / 4);
      double cos60 = Math.cos(Math.PI / 3);
      double sin60 = Math.sin(Math.PI / 3);
      Matrix A = new Matrix(new double[][]{
            {cos45, -sin45, 0, 0},
            {sin45, cos45, 0, 0},
            {0, 0, 2 * cos60, -2 * sin60},
            {0, 0, 2 * sin60, 2 * cos60}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] im = eig.getImagEigenvalues();
      // Should have non-zero imaginary parts
      boolean hasComplex = false;
      for (double v : im) {
         if (Math.abs(v) > 1e-7) {
            hasComplex = true;
            break;
         }
      }
      assertTrue("Should have complex eigenvalues", hasComplex);
   }

   @Test
   public void testNonSymmetricGetD_BlockDiagonal() {
      // Matrix with complex eigenvalue pair => D has 2x2 blocks
      Matrix A = new Matrix(new double[][]{
            {0, -1},
            {1, 0}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      Matrix D = eig.getD();
      double[] im = eig.getImagEigenvalues();
      // eigenvalues are ±i, D should have off-diagonal entries
      boolean hasOffDiag = Math.abs(D.get(0, 1)) > 1e-7 || Math.abs(D.get(1, 0)) > 1e-7;
      assertTrue("D should have 2x2 block for complex eigenvalues", hasOffDiag);
   }

   @Test
   public void testNonSymmetric4x4General() {
      Matrix A = new Matrix(new double[][]{
            {4, 1, 0, 0},
            {0, 3, 1, 0},
            {0, 0, 2, 1},
            {0, 0, 0, 1}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertEquals(1.0, sorted[0], 1e-7);
      assertEquals(2.0, sorted[1], 1e-7);
      assertEquals(3.0, sorted[2], 1e-7);
      assertEquals(4.0, sorted[3], 1e-7);
      assertAVeqVD(A, eig);
   }

   @Test
   public void testNonSymmetricLowerTriangular() {
      Matrix A = new Matrix(new double[][]{
            {2, 0, 0},
            {3, 5, 0},
            {1, 4, 8}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertEquals(2.0, sorted[0], 1e-7);
      assertEquals(5.0, sorted[1], 1e-7);
      assertEquals(8.0, sorted[2], 1e-7);
      assertAVeqVD(A, eig);
   }

   @Test
   public void testNonSymmetric5x5() {
      Matrix A = new Matrix(new double[][]{
            {5, 1, 0, 0, 0},
            {0, 4, 1, 0, 0},
            {0, 0, 3, 1, 0},
            {0, 0, 0, 2, 1},
            {0, 0, 0, 0, 1}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] re = eig.getRealEigenvalues();
      assertEquals(5, re.length);
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      for (int i = 0; i < 5; i++) {
         assertEquals(i + 1.0, sorted[i], 1e-6);
      }
   }

   @Test
   public void testNonSymmetricViaMatrixEig() {
      // Test through Matrix.eig() accessor
      Matrix A = new Matrix(new double[][]{
            {2, 1},
            {0, 3}
      });
      EigenvalueDecomposition eig = A.eig();
      assertNotNull(eig);
      double[] re = eig.getRealEigenvalues();
      double[] sorted = re.clone();
      java.util.Arrays.sort(sorted);
      assertEquals(2.0, sorted[0], TOL);
      assertEquals(3.0, sorted[1], TOL);
   }

   @Test
   public void testNonSymmetricComplex3x3() {
      // Companion matrix of x^3 + 1 = 0 => roots are -1, e^{i*pi/3}, e^{-i*pi/3}
      Matrix A = new Matrix(new double[][]{
            {0, 0, -1},
            {1, 0, 0},
            {0, 1, 0}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      double[] im = eig.getImagEigenvalues();
      boolean hasComplex = false;
      for (double v : im) {
         if (Math.abs(v) > 1e-7) {
            hasComplex = true;
            break;
         }
      }
      assertTrue("Companion matrix should yield complex eigenvalues", hasComplex);
   }

   @Test
   public void testGetVDimensions() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
      });
      EigenvalueDecomposition eig = new EigenvalueDecomposition(A);
      Matrix V = eig.getV();
      assertEquals(3, V.getRowDimension());
      assertEquals(3, V.getColumnDimension());
      Matrix D = eig.getD();
      assertEquals(3, D.getRowDimension());
      assertEquals(3, D.getColumnDimension());
   }
}
