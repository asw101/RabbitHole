package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LUDecompositionDeepTest {
  @Test
  public void factorsPivotVectorsAndSolveMatchPivotedMatrix() {
    Matrix matrix = new Matrix(new double[][]{{0.0, 1.0}, {2.0, 3.0}});
    LUDecomposition lu = new LUDecomposition(matrix);

    assertTrue(lu.isNonsingular());
    assertMatrixEquals(matrix.getMatrix(lu.getPivot(), 0, 1), lu.getL().times(lu.getU()));
    assertArrayEquals(new int[]{1, 0}, lu.getPivot());
    assertArrayEquals(new double[]{1.0, 0.0}, lu.getDoublePivot(), TOLERANCE);
    assertEquals(-2.0, lu.det(), TOLERANCE);

    Matrix rhs = new Matrix(new double[][]{{1.0}, {8.0}});
    Matrix solution = lu.solve(rhs);
    assertMatrixEquals(rhs, matrix.times(solution));
  }

  @Test
  public void singularAndInvalidInputsAreRejected() {
    LUDecomposition singular = new LUDecomposition(new Matrix(new double[][]{{1.0, 2.0}, {2.0, 4.0}}));
    assertFalse(singular.isNonsingular());

    expectRuntime(() -> singular.solve(new Matrix(new double[][]{{1.0}, {2.0}})), "Matrix is singular.");
    expectIllegalArgument(() -> singular.solve(new Matrix(new double[][]{{1.0}})), "Matrix row dimensions must agree.");
    expectIllegalArgument(
        () -> new LUDecomposition(new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}})).det(),
        "Matrix must be square.");
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
