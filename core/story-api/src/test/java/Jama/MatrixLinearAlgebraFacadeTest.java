package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatrixLinearAlgebraFacadeTest {
  @Test
  public void decompositionFactoriesAndSolversExposeConsistentResults() {
    Matrix square = new Matrix(new double[][]{{4.0, 3.0}, {6.0, 3.0}});
    Matrix rhs = new Matrix(new double[][]{{10.0}, {12.0}});

    LUDecomposition lu = square.lu();
    QRDecomposition qr = square.qr();
    CholeskyDecomposition chol = new Matrix(new double[][]{{25.0, 15.0}, {15.0, 18.0}}).chol();
    SingularValueDecomposition svd = square.svd();
    EigenvalueDecomposition eig = square.eig();

    assertTrue(lu.isNonsingular());
    assertTrue(qr.isFullRank());
    assertTrue(chol.isSPD());
    assertEquals(square.svd().norm2(), svd.norm2(), TOLERANCE);
    assertEquals(square.det(), lu.det(), TOLERANCE);
    assertEquals(square.rank(), svd.rank());
    assertEquals(square.cond(), svd.cond(), TOLERANCE);
    assertEquals(square.trace(), eig.getD().trace(), TOLERANCE);

    Matrix solution = square.solve(rhs);
    assertMatrixEquals(rhs, square.times(solution));

    Matrix solveTranspose = square.solveTranspose(new Matrix(new double[][]{{10.0, 12.0}}));
    assertMatrixEquals(new Matrix(new double[][]{{10.0}, {12.0}}), square.transpose().times(solveTranspose));

    Matrix inverse = square.inverse();
    assertMatrixEquals(Matrix.identity(2, 2), square.times(inverse), 1.0e-7);

    Matrix rectangular = new Matrix(new double[][]{{1.0, 1.0}, {1.0, 2.0}, {1.0, 3.0}});
    Matrix leastSquaresRhs = new Matrix(new double[][]{{1.0}, {2.0}, {2.0}});
    Matrix leastSquaresSolution = rectangular.solve(leastSquaresRhs);
    assertMatrixEquals(new Matrix(new double[][]{{2.0 / 3.0}, {0.5}}), leastSquaresSolution, 1.0e-7);
  }

  @Test
  public void scalarMatrixPropertiesAndFactoriesBehaveAsExpected() {
    Matrix matrix = new Matrix(new double[][]{{2.0, 0.0}, {0.0, 1.0}});

    assertEquals(2.0, matrix.det(), TOLERANCE);
    assertEquals(2, matrix.rank());
    assertEquals(2.0, matrix.cond(), TOLERANCE);
    assertEquals(3.0, matrix.trace(), TOLERANCE);

    Matrix identity = Matrix.identity(3, 2);
    assertMatrixEquals(new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}, {0.0, 0.0}}), identity);

    Matrix random = Matrix.random(4, 3);
    assertEquals(4, random.getRowDimension());
    assertEquals(3, random.getColumnDimension());
    for (int i = 0; i < random.getRowDimension(); i++) {
      for (int j = 0; j < random.getColumnDimension(); j++) {
        assertTrue(random.get(i, j) >= 0.0);
        assertTrue(random.get(i, j) < 1.0);
      }
    }
  }
}
