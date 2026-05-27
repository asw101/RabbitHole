package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QRDecompositionDeepTest {
  @Test
  public void decompositionExposesHouseholderUpperTriangularAndOrthogonalFactors() {
    Matrix matrix = new Matrix(new double[][]{
        {12.0, -51.0, 4.0},
        {6.0, 167.0, -68.0},
        {-4.0, 24.0, -41.0}
    });
    QRDecomposition qr = new QRDecomposition(matrix);

    assertTrue(qr.isFullRank());
    assertMatrixEquals(matrix, qr.getQ().times(qr.getR()), 1.0e-7);
    assertMatrixEquals(Matrix.identity(3, 3), qr.getQ().transpose().times(qr.getQ()), 1.0e-7);
    assertEquals(0.0, qr.getR().get(1, 0), 0.0);
    assertEquals(0.0, qr.getR().get(2, 0), 0.0);
    assertEquals(0.0, qr.getR().get(2, 1), 0.0);
    assertEquals(0.0, qr.getH().get(0, 1), 0.0);
    assertEquals(0.0, qr.getH().get(0, 2), 0.0);

    Matrix expectedSolution = new Matrix(new double[][]{{1.0}, {2.0}, {3.0}});
    Matrix rhs = matrix.times(expectedSolution);
    assertMatrixEquals(expectedSolution, qr.solve(rhs), 1.0e-7);
  }

  @Test
  public void rankDeficiencyAndRowMismatchesFailFast() {
    QRDecomposition rankDeficient = new QRDecomposition(
        new Matrix(new double[][]{{1.0, 0.0}, {2.0, 0.0}, {3.0, 0.0}}));
    assertFalse(rankDeficient.isFullRank());

    expectRuntime(
        () -> rankDeficient.solve(new Matrix(new double[][]{{1.0}, {2.0}, {3.0}})),
        "Matrix is rank deficient.");
    expectIllegalArgument(
        () -> rankDeficient.solve(new Matrix(new double[][]{{1.0}, {2.0}})),
        "Matrix row dimensions must agree.");
  }

  private static void expectIllegalArgument(Runnable runnable, String message) {
    try {
      runnable.run();
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals(message, expected.getMessage());
    }
  }

  private static void expectRuntime(Runnable runnable, String message) {
    try {
      runnable.run();
      fail("expected RuntimeException");
    } catch (RuntimeException expected) {
      assertEquals(message, expected.getMessage());
    }
  }
}
