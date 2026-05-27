package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CholeskyDecompositionDeepTest {
  @Test
  public void lowerTriangularFactorAndSolveRecoverSpdInputs() {
    Matrix matrix = new Matrix(new double[][]{
        {25.0, 15.0, -5.0},
        {15.0, 18.0, 0.0},
        {-5.0, 0.0, 11.0}
    });
    CholeskyDecomposition decomposition = new CholeskyDecomposition(matrix);

    assertTrue(decomposition.isSPD());
    assertMatrixEquals(
        new Matrix(new double[][]{{5.0, 0.0, 0.0}, {3.0, 3.0, 0.0}, {-1.0, 1.0, 3.0}}),
        decomposition.getL(),
        TOLERANCE);
    assertMatrixEquals(matrix, decomposition.getL().times(decomposition.getL().transpose()), 1.0e-7);

    Matrix expectedSolution = new Matrix(new double[][]{{1.0}, {2.0}, {3.0}});
    Matrix rhs = matrix.times(expectedSolution);
    assertMatrixEquals(expectedSolution, decomposition.solve(rhs), 1.0e-7);
  }

  @Test
  public void nonSpdMatricesAndBadDimensionsAreRejected() {
    CholeskyDecomposition nonSymmetric = new CholeskyDecomposition(
        new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}}));
    assertFalse(nonSymmetric.isSPD());

    CholeskyDecomposition nonPositive = new CholeskyDecomposition(
        new Matrix(new double[][]{{1.0, 2.0}, {2.0, 1.0}}));
    assertFalse(nonPositive.isSPD());

    expectRuntime(
        () -> nonPositive.solve(new Matrix(new double[][]{{1.0}, {2.0}})),
        "Matrix is not symmetric positive definite.");
    expectIllegalArgument(
        () -> nonPositive.solve(new Matrix(new double[][]{{1.0}})),
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
