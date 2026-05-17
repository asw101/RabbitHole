package Jama;

import org.junit.Test;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.Assert.*;

public class MatrixOperationsTest {

   private static final double TOL = 1e-9;

   // ---- Constructors ----

   @Test
   public void testConstructorMN() {
      Matrix A = new Matrix(3, 4);
      assertEquals(3, A.getRowDimension());
      assertEquals(4, A.getColumnDimension());
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 4; j++)
            assertEquals(0.0, A.get(i, j), TOL);
   }

   @Test
   public void testConstructorMNS() {
      Matrix A = new Matrix(3, 3, 5.0);
      assertEquals(3, A.getRowDimension());
      assertEquals(3, A.getColumnDimension());
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            assertEquals(5.0, A.get(i, j), TOL);
   }

   @Test
   public void testConstructorFromPackedArray() {
      // vals = {1,2,3,4,5,6}, m=2 => 2 rows, 3 cols column-packed
      // Column-packed: col0={1,2}, col1={3,4}, col2={5,6}
      Matrix A = new Matrix(new double[]{1, 2, 3, 4, 5, 6}, 2);
      assertEquals(2, A.getRowDimension());
      assertEquals(3, A.getColumnDimension());
      assertEquals(1.0, A.get(0, 0), TOL);
      assertEquals(2.0, A.get(1, 0), TOL);
      assertEquals(3.0, A.get(0, 1), TOL);
      assertEquals(4.0, A.get(1, 1), TOL);
      assertEquals(5.0, A.get(0, 2), TOL);
      assertEquals(6.0, A.get(1, 2), TOL);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testConstructorFromPackedArrayBadLength() {
      new Matrix(new double[]{1, 2, 3, 4, 5}, 2);
   }

   @Test
   public void testConstructWithCopy() {
      double[][] data = {{1, 2}, {3, 4}};
      Matrix A = Matrix.constructWithCopy(data);
      assertEquals(1.0, A.get(0, 0), TOL);
      // mutating source should not affect matrix
      data[0][0] = 99.0;
      assertEquals(1.0, A.get(0, 0), TOL);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testConstructWithCopyJagged() {
      double[][] data = {{1, 2}, {3}};
      Matrix.constructWithCopy(data);
   }

   // ---- Copy / Clone ----

   @Test
   public void testCopy() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix B = A.copy();
      assertEquals(A.get(0, 0), B.get(0, 0), TOL);
      B.set(0, 0, 99.0);
      assertEquals(1.0, A.get(0, 0), TOL); // original unchanged
   }

   @Test
   public void testClone() {
      Matrix A = new Matrix(new double[][]{{5, 6}, {7, 8}});
      Object obj = A.clone();
      assertTrue(obj instanceof Matrix);
      Matrix B = (Matrix) obj;
      assertEquals(5.0, B.get(0, 0), TOL);
      B.set(0, 0, 0.0);
      assertEquals(5.0, A.get(0, 0), TOL);
   }

   // ---- Array copies ----

   @Test
   public void testGetArrayCopy() {
      double[][] data = {{1, 2}, {3, 4}};
      Matrix A = new Matrix(data);
      double[][] copy = A.getArrayCopy();
      assertEquals(1.0, copy[0][0], TOL);
      copy[0][0] = 99.0;
      assertEquals(1.0, A.get(0, 0), TOL);
   }

   @Test
   public void testGetColumnPackedCopy() {
      Matrix A = new Matrix(new double[][]{{1, 3, 5}, {2, 4, 6}});
      double[] packed = A.getColumnPackedCopy();
      // column-packed: col0={1,2}, col1={3,4}, col2={5,6}
      assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6}, packed, TOL);
   }

   @Test
   public void testGetRowPackedCopy() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
      double[] packed = A.getRowPackedCopy();
      assertArrayEquals(new double[]{1, 2, 3, 4, 5, 6}, packed, TOL);
   }

   // ---- Transpose ----

   @Test
   public void testTranspose() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
      Matrix At = A.transpose();
      assertEquals(3, At.getRowDimension());
      assertEquals(2, At.getColumnDimension());
      assertEquals(1.0, At.get(0, 0), TOL);
      assertEquals(4.0, At.get(0, 1), TOL);
      assertEquals(2.0, At.get(1, 0), TOL);
      assertEquals(5.0, At.get(1, 1), TOL);
      assertEquals(3.0, At.get(2, 0), TOL);
      assertEquals(6.0, At.get(2, 1), TOL);
   }

   @Test
   public void testTransposeDoubleIsOriginal() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix Att = A.transpose().transpose();
      for (int i = 0; i < 2; i++)
         for (int j = 0; j < 2; j++)
            assertEquals(A.get(i, j), Att.get(i, j), TOL);
   }

   // ---- Norms ----

   @Test
   public void testNorm1() {
      // norm1 = max column sum of absolute values
      Matrix A = new Matrix(new double[][]{{1, -3}, {-2, 4}});
      // col0: |1|+|-2|=3, col1: |-3|+|4|=7
      assertEquals(7.0, A.norm1(), TOL);
   }

   @Test
   public void testNormF() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      // sqrt(1+4+9+16) = sqrt(30)
      assertEquals(Math.sqrt(30.0), A.normF(), TOL);
   }

   @Test
   public void testNormInf() {
      // normInf = max row sum of absolute values
      Matrix A = new Matrix(new double[][]{{1, -3}, {-2, 4}});
      // row0: |1|+|-3|=4, row1: |-2|+|4|=6
      assertEquals(6.0, A.normInf(), TOL);
   }

   @Test
   public void testNorm2() {
      // norm2 = largest singular value
      // For a diagonal matrix, it's the largest absolute diagonal entry
      Matrix A = new Matrix(new double[][]{{3, 0}, {0, -5}});
      assertEquals(5.0, A.norm2(), TOL);
   }

   // ---- Uminus ----

   @Test
   public void testUminus() {
      Matrix A = new Matrix(new double[][]{{1, -2}, {3, -4}});
      Matrix neg = A.uminus();
      assertEquals(-1.0, neg.get(0, 0), TOL);
      assertEquals(2.0, neg.get(0, 1), TOL);
      assertEquals(-3.0, neg.get(1, 0), TOL);
      assertEquals(4.0, neg.get(1, 1), TOL);
      // original unchanged
      assertEquals(1.0, A.get(0, 0), TOL);
   }

   // ---- Element-wise arithmetic ----

   @Test
   public void testPlus() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix C = A.plus(B);
      assertEquals(11.0, C.get(0, 0), TOL);
      assertEquals(22.0, C.get(0, 1), TOL);
      assertEquals(33.0, C.get(1, 0), TOL);
      assertEquals(44.0, C.get(1, 1), TOL);
   }

   @Test
   public void testPlusEquals() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix result = A.plusEquals(B);
      assertSame(A, result);
      assertEquals(11.0, A.get(0, 0), TOL);
      assertEquals(44.0, A.get(1, 1), TOL);
   }

   @Test
   public void testMinus() {
      Matrix A = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix B = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix C = A.minus(B);
      assertEquals(9.0, C.get(0, 0), TOL);
      assertEquals(36.0, C.get(1, 1), TOL);
   }

   @Test
   public void testMinusEquals() {
      Matrix A = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix B = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix result = A.minusEquals(B);
      assertSame(A, result);
      assertEquals(9.0, A.get(0, 0), TOL);
   }

   @Test
   public void testArrayTimes() {
      Matrix A = new Matrix(new double[][]{{2, 3}, {4, 5}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix C = A.arrayTimes(B);
      assertEquals(20.0, C.get(0, 0), TOL);
      assertEquals(60.0, C.get(0, 1), TOL);
      assertEquals(120.0, C.get(1, 0), TOL);
      assertEquals(200.0, C.get(1, 1), TOL);
   }

   @Test
   public void testArrayTimesEquals() {
      Matrix A = new Matrix(new double[][]{{2, 3}, {4, 5}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix result = A.arrayTimesEquals(B);
      assertSame(A, result);
      assertEquals(20.0, A.get(0, 0), TOL);
   }

   @Test
   public void testArrayRightDivide() {
      Matrix A = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix B = new Matrix(new double[][]{{2, 5}, {10, 8}});
      Matrix C = A.arrayRightDivide(B);
      assertEquals(5.0, C.get(0, 0), TOL);
      assertEquals(4.0, C.get(0, 1), TOL);
      assertEquals(3.0, C.get(1, 0), TOL);
      assertEquals(5.0, C.get(1, 1), TOL);
   }

   @Test
   public void testArrayRightDivideEquals() {
      Matrix A = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix B = new Matrix(new double[][]{{2, 5}, {10, 8}});
      Matrix result = A.arrayRightDivideEquals(B);
      assertSame(A, result);
      assertEquals(5.0, A.get(0, 0), TOL);
   }

   @Test
   public void testArrayLeftDivide() {
      // arrayLeftDivide: B(i,j) / A(i,j)  — "A left-divides B"
      Matrix A = new Matrix(new double[][]{{2, 5}, {10, 8}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix C = A.arrayLeftDivide(B);
      assertEquals(5.0, C.get(0, 0), TOL);
      assertEquals(4.0, C.get(0, 1), TOL);
      assertEquals(3.0, C.get(1, 0), TOL);
      assertEquals(5.0, C.get(1, 1), TOL);
   }

   @Test
   public void testArrayLeftDivideEquals() {
      Matrix A = new Matrix(new double[][]{{2, 5}, {10, 8}});
      Matrix B = new Matrix(new double[][]{{10, 20}, {30, 40}});
      Matrix result = A.arrayLeftDivideEquals(B);
      assertSame(A, result);
      assertEquals(5.0, A.get(0, 0), TOL);
   }

   // ---- Scalar operations ----

   @Test
   public void testTimesScalar() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix B = A.times(3.0);
      assertEquals(3.0, B.get(0, 0), TOL);
      assertEquals(6.0, B.get(0, 1), TOL);
      assertEquals(12.0, B.get(1, 1), TOL);
      // original unchanged
      assertEquals(1.0, A.get(0, 0), TOL);
   }

   @Test
   public void testTimesEqualsScalar() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix result = A.timesEquals(2.0);
      assertSame(A, result);
      assertEquals(2.0, A.get(0, 0), TOL);
      assertEquals(4.0, A.get(0, 1), TOL);
      assertEquals(8.0, A.get(1, 1), TOL);
   }

   // ---- Matrix multiplication ----

   @Test
   public void testTimesMatrix() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix B = new Matrix(new double[][]{{5, 6}, {7, 8}});
      Matrix C = A.times(B);
      // [1*5+2*7, 1*6+2*8] = [19, 22]
      // [3*5+4*7, 3*6+4*8] = [43, 50]
      assertEquals(19.0, C.get(0, 0), TOL);
      assertEquals(22.0, C.get(0, 1), TOL);
      assertEquals(43.0, C.get(1, 0), TOL);
      assertEquals(50.0, C.get(1, 1), TOL);
   }

   @Test
   public void testTimesMatrixRectangular() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}}); // 2x3
      Matrix B = new Matrix(new double[][]{{7, 8}, {9, 10}, {11, 12}}); // 3x2
      Matrix C = A.times(B); // 2x2
      assertEquals(2, C.getRowDimension());
      assertEquals(2, C.getColumnDimension());
      assertEquals(58.0, C.get(0, 0), TOL);  // 1*7+2*9+3*11
      assertEquals(64.0, C.get(0, 1), TOL);  // 1*8+2*10+3*12
      assertEquals(139.0, C.get(1, 0), TOL); // 4*7+5*9+6*11
      assertEquals(154.0, C.get(1, 1), TOL); // 4*8+5*10+6*12
   }

   // ---- Decomposition accessors ----

   @Test
   public void testLU() {
      Matrix A = new Matrix(new double[][]{{2, 1}, {1, 3}});
      assertNotNull(A.lu());
   }

   @Test
   public void testQR() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}, {5, 6}});
      assertNotNull(A.qr());
   }

   @Test
   public void testChol() {
      // positive definite symmetric matrix
      Matrix A = new Matrix(new double[][]{{4, 2}, {2, 3}});
      assertNotNull(A.chol());
   }

   @Test
   public void testSVD() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      assertNotNull(A.svd());
   }

   @Test
   public void testEig() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      assertNotNull(A.eig());
   }

   // ---- Inverse ----

   @Test
   public void testInverse() {
      Matrix A = new Matrix(new double[][]{{4, 7}, {2, 6}});
      Matrix Ainv = A.inverse();
      Matrix I = A.times(Ainv);
      assertEquals(2, I.getRowDimension());
      for (int i = 0; i < 2; i++)
         for (int j = 0; j < 2; j++)
            assertEquals(i == j ? 1.0 : 0.0, I.get(i, j), 1e-7);
   }

   @Test
   public void testInverse3x3() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {0, 1, 4}, {5, 6, 0}});
      Matrix Ainv = A.inverse();
      Matrix I = A.times(Ainv);
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            assertEquals(i == j ? 1.0 : 0.0, I.get(i, j), 1e-7);
   }

   // ---- solveTranspose ----

   @Test
   public void testSolveTranspose() {
      Matrix A = new Matrix(new double[][]{{4, 7}, {2, 6}});
      Matrix B = new Matrix(new double[][]{{1, 2}, {3, 4}});
      // solveTranspose(B) returns A'.solve(B') which solves A'*X = B'
      Matrix X = A.solveTranspose(B);
      assertNotNull(X);
      // verify A'*X ≈ B'
      Matrix check = A.transpose().times(X);
      Matrix Bt = B.transpose();
      for (int i = 0; i < Bt.getRowDimension(); i++)
         for (int j = 0; j < Bt.getColumnDimension(); j++)
            assertEquals(Bt.get(i, j), check.get(i, j), 1e-7);
   }

   // ---- det, rank, cond, trace ----

   @Test
   public void testDet() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      assertEquals(-2.0, A.det(), 1e-7);
   }

   @Test
   public void testDet3x3() {
      Matrix A = new Matrix(new double[][]{{6, 1, 1}, {4, -2, 5}, {2, 8, 7}});
      assertEquals(-306.0, A.det(), 1e-7);
   }

   @Test
   public void testRank() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
      assertEquals(2, A.rank());

      Matrix B = Matrix.identity(3, 3);
      assertEquals(3, B.rank());
   }

   @Test
   public void testCond() {
      Matrix I = Matrix.identity(3, 3);
      assertEquals(1.0, I.cond(), 1e-7);
   }

   @Test
   public void testTrace() {
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
      assertEquals(15.0, A.trace(), TOL);
   }

   // ---- Static factories ----

   @Test
   public void testIdentity() {
      Matrix I = Matrix.identity(3, 3);
      for (int i = 0; i < 3; i++)
         for (int j = 0; j < 3; j++)
            assertEquals(i == j ? 1.0 : 0.0, I.get(i, j), TOL);
   }

   @Test
   public void testIdentityRectangular() {
      Matrix I = Matrix.identity(2, 4);
      assertEquals(2, I.getRowDimension());
      assertEquals(4, I.getColumnDimension());
      assertEquals(1.0, I.get(0, 0), TOL);
      assertEquals(1.0, I.get(1, 1), TOL);
      assertEquals(0.0, I.get(0, 2), TOL);
   }

   @Test
   public void testRandom() {
      Matrix R = Matrix.random(4, 5);
      assertEquals(4, R.getRowDimension());
      assertEquals(5, R.getColumnDimension());
      for (int i = 0; i < 4; i++)
         for (int j = 0; j < 5; j++) {
            assertTrue(R.get(i, j) >= 0.0);
            assertTrue(R.get(i, j) <= 1.0);
         }
   }

   // ---- Print ----

   @Test
   public void testPrintWidthDecimals() {
      Matrix A = new Matrix(new double[][]{{1.5, 2.5}, {3.5, 4.5}});
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      A.print(pw, 10, 4);
      pw.flush();
      String output = sw.toString();
      assertTrue(output.length() > 0);
      assertTrue(output.contains("1.5"));
   }

   @Test
   public void testPrintNumberFormat() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.00");
      A.print(pw, fmt, 8);
      pw.flush();
      String output = sw.toString();
      assertTrue(output.length() > 0);
   }

   @Test
   public void testPrintToStdout() {
      Matrix A = new Matrix(new double[][]{{1}});
      // print(int w, int d) prints to stdout — just ensure no exception
      A.print(10, 2);
   }

   @Test
   public void testPrintNumberFormatToStdout() {
      Matrix A = new Matrix(new double[][]{{1}});
      java.text.DecimalFormat fmt = new java.text.DecimalFormat("0.0");
      A.print(fmt, 8);
   }

   // ---- Submatrix getMatrix / setMatrix ----

   @Test
   public void testGetMatrixRange() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12}
      });
      Matrix sub = A.getMatrix(0, 1, 1, 2); // rows 0-1, cols 1-2
      assertEquals(2, sub.getRowDimension());
      assertEquals(2, sub.getColumnDimension());
      assertEquals(2.0, sub.get(0, 0), TOL);
      assertEquals(3.0, sub.get(0, 1), TOL);
      assertEquals(6.0, sub.get(1, 0), TOL);
      assertEquals(7.0, sub.get(1, 1), TOL);
   }

   @Test
   public void testGetMatrixIndexed() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
      });
      int[] rows = {0, 2};
      int[] cols = {1, 2};
      Matrix sub = A.getMatrix(rows, cols);
      assertEquals(2, sub.getRowDimension());
      assertEquals(2, sub.getColumnDimension());
      assertEquals(2.0, sub.get(0, 0), TOL);
      assertEquals(3.0, sub.get(0, 1), TOL);
      assertEquals(8.0, sub.get(1, 0), TOL);
      assertEquals(9.0, sub.get(1, 1), TOL);
   }

   @Test
   public void testGetMatrixRowIndicesColRange() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
      });
      int[] rows = {0, 2};
      Matrix sub = A.getMatrix(rows, 0, 1);
      assertEquals(2, sub.getRowDimension());
      assertEquals(2, sub.getColumnDimension());
      assertEquals(1.0, sub.get(0, 0), TOL);
      assertEquals(2.0, sub.get(0, 1), TOL);
      assertEquals(7.0, sub.get(1, 0), TOL);
      assertEquals(8.0, sub.get(1, 1), TOL);
   }

   @Test
   public void testGetMatrixRowRangeColIndices() {
      Matrix A = new Matrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
      });
      int[] cols = {0, 2};
      Matrix sub = A.getMatrix(0, 1, cols);
      assertEquals(2, sub.getRowDimension());
      assertEquals(2, sub.getColumnDimension());
      assertEquals(1.0, sub.get(0, 0), TOL);
      assertEquals(3.0, sub.get(0, 1), TOL);
      assertEquals(4.0, sub.get(1, 0), TOL);
      assertEquals(6.0, sub.get(1, 1), TOL);
   }

   @Test
   public void testSetMatrixRange() {
      Matrix A = new Matrix(3, 3, 0.0);
      Matrix sub = new Matrix(new double[][]{{7, 8}, {9, 10}});
      A.setMatrix(1, 2, 1, 2, sub);
      assertEquals(7.0, A.get(1, 1), TOL);
      assertEquals(8.0, A.get(1, 2), TOL);
      assertEquals(9.0, A.get(2, 1), TOL);
      assertEquals(10.0, A.get(2, 2), TOL);
      assertEquals(0.0, A.get(0, 0), TOL);
   }

   @Test
   public void testSetMatrixIndexed() {
      Matrix A = new Matrix(3, 3, 0.0);
      Matrix sub = new Matrix(new double[][]{{5, 6}, {7, 8}});
      int[] rows = {0, 2};
      int[] cols = {0, 2};
      A.setMatrix(rows, cols, sub);
      assertEquals(5.0, A.get(0, 0), TOL);
      assertEquals(6.0, A.get(0, 2), TOL);
      assertEquals(7.0, A.get(2, 0), TOL);
      assertEquals(8.0, A.get(2, 2), TOL);
      assertEquals(0.0, A.get(1, 1), TOL);
   }

   @Test
   public void testSetMatrixRowIndicesColRange() {
      Matrix A = new Matrix(3, 3, 0.0);
      Matrix sub = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
      int[] rows = {0, 2};
      A.setMatrix(rows, 0, 2, sub);
      assertEquals(1.0, A.get(0, 0), TOL);
      assertEquals(2.0, A.get(0, 1), TOL);
      assertEquals(3.0, A.get(0, 2), TOL);
      assertEquals(4.0, A.get(2, 0), TOL);
      assertEquals(5.0, A.get(2, 1), TOL);
      assertEquals(6.0, A.get(2, 2), TOL);
   }

   @Test
   public void testSetMatrixRowRangeColIndices() {
      Matrix A = new Matrix(3, 3, 0.0);
      Matrix sub = new Matrix(new double[][]{{11, 22}, {33, 44}});
      int[] cols = {0, 2};
      A.setMatrix(0, 1, cols, sub);
      assertEquals(11.0, A.get(0, 0), TOL);
      assertEquals(22.0, A.get(0, 2), TOL);
      assertEquals(33.0, A.get(1, 0), TOL);
      assertEquals(44.0, A.get(1, 2), TOL);
   }

   // ---- Additional coverage for Matrix operations ----

   @Test
   public void testTimesMatrixIdentity() {
      Matrix A = new Matrix(new double[][]{{1, 2}, {3, 4}});
      Matrix I = Matrix.identity(2, 2);
      Matrix C = A.times(I);
      for (int i = 0; i < 2; i++)
         for (int j = 0; j < 2; j++)
            assertEquals(A.get(i, j), C.get(i, j), TOL);
   }

   @Test
   public void testTraceRectangular() {
      // trace sums min(m,n) diagonal entries
      Matrix A = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
      assertEquals(6.0, A.trace(), TOL); // 1+5
   }

   @Test
   public void testNormFIdentity() {
      Matrix I = Matrix.identity(3, 3);
      assertEquals(Math.sqrt(3.0), I.normF(), TOL);
   }

   @Test
   public void testNorm1Identity() {
      Matrix I = Matrix.identity(3, 3);
      assertEquals(1.0, I.norm1(), TOL);
   }

   @Test
   public void testGetArray() {
      double[][] data = {{1, 2}, {3, 4}};
      Matrix A = new Matrix(data);
      double[][] arr = A.getArray();
      // getArray returns internal reference
      arr[0][0] = 99.0;
      assertEquals(99.0, A.get(0, 0), TOL);
   }

   @Test
   public void testConstructor2DArrayWithDims() {
      double[][] data = {{1, 2, 3}, {4, 5, 6}};
      Matrix A = new Matrix(data, 2, 3);
      assertEquals(2, A.getRowDimension());
      assertEquals(3, A.getColumnDimension());
      assertEquals(5.0, A.get(1, 1), TOL);
   }

   @Test
   public void testSolveSquare() {
      // A*x = b
      Matrix A = new Matrix(new double[][]{{2, 1}, {5, 3}});
      Matrix b = new Matrix(new double[][]{{4}, {7}});
      Matrix x = A.solve(b);
      // verify A*x = b
      Matrix check = A.times(x);
      for (int i = 0; i < 2; i++)
         assertEquals(b.get(i, 0), check.get(i, 0), 1e-7);
   }

   @Test
   public void testSolveOverdetermined() {
      // least squares: 3x2 system
      Matrix A = new Matrix(new double[][]{{1, 0}, {0, 1}, {1, 1}});
      Matrix b = new Matrix(new double[][]{{1}, {2}, {4}});
      Matrix x = A.solve(b);
      assertNotNull(x);
      assertEquals(2, x.getRowDimension());
      assertEquals(1, x.getColumnDimension());
   }

   @Test
   public void testDetIdentity() {
      Matrix I = Matrix.identity(4, 4);
      assertEquals(1.0, I.det(), 1e-7);
   }
}
