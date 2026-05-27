package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MatrixArithmeticAndNormTest {
  @Test
  public void arithmeticOperationsAndNormsProduceExpectedResults() {
    Matrix left = new Matrix(new double[][]{{1.0, -2.0}, {3.0, 4.0}});
    Matrix right = new Matrix(new double[][]{{5.0, 6.0}, {7.0, 8.0}});

    assertMatrixEquals(new Matrix(new double[][]{{1.0, 3.0}, {-2.0, 4.0}}), left.transpose());
    assertEquals(6.0, left.norm1(), TOLERANCE);
    assertEquals(7.0, left.normInf(), TOLERANCE);
    assertEquals(Math.sqrt(30.0), left.normF(), TOLERANCE);
    assertEquals(left.svd().norm2(), left.norm2(), TOLERANCE);

    assertMatrixEquals(new Matrix(new double[][]{{-1.0, 2.0}, {-3.0, -4.0}}), left.uminus());
    assertMatrixEquals(new Matrix(new double[][]{{6.0, 4.0}, {10.0, 12.0}}), left.plus(right));
    assertMatrixEquals(new Matrix(new double[][]{{-4.0, -8.0}, {-4.0, -4.0}}), left.minus(right));
    assertMatrixEquals(new Matrix(new double[][]{{5.0, -12.0}, {21.0, 32.0}}), left.arrayTimes(right));
    assertMatrixEquals(new Matrix(new double[][]{{0.2, -0.3333333333333333}, {0.42857142857142855, 0.5}}), left.arrayRightDivide(right), 1.0e-12);
    assertMatrixEquals(new Matrix(new double[][]{{5.0, -3.0}, {2.3333333333333335, 2.0}}), left.arrayLeftDivide(right), 1.0e-12);
    assertMatrixEquals(new Matrix(new double[][]{{2.5, -5.0}, {7.5, 10.0}}), left.times(2.5));
    assertMatrixEquals(new Matrix(new double[][]{{-9.0, -10.0}, {43.0, 50.0}}), left.times(right));

    Matrix plusEquals = left.copy();
    plusEquals.plusEquals(right);
    assertMatrixEquals(new Matrix(new double[][]{{6.0, 4.0}, {10.0, 12.0}}), plusEquals);

    Matrix minusEquals = left.copy();
    minusEquals.minusEquals(right);
    assertMatrixEquals(new Matrix(new double[][]{{-4.0, -8.0}, {-4.0, -4.0}}), minusEquals);

    Matrix arrayTimesEquals = left.copy();
    arrayTimesEquals.arrayTimesEquals(right);
    assertMatrixEquals(new Matrix(new double[][]{{5.0, -12.0}, {21.0, 32.0}}), arrayTimesEquals);

    Matrix arrayRightDivideEquals = left.copy();
    arrayRightDivideEquals.arrayRightDivideEquals(right);
    assertMatrixEquals(
        new Matrix(new double[][]{{0.2, -0.3333333333333333}, {0.42857142857142855, 0.5}}),
        arrayRightDivideEquals,
        1.0e-12);

    Matrix arrayLeftDivideEquals = left.copy();
    arrayLeftDivideEquals.arrayLeftDivideEquals(right);
    assertMatrixEquals(
        new Matrix(new double[][]{{5.0, -3.0}, {2.3333333333333335, 2.0}}),
        arrayLeftDivideEquals,
        1.0e-12);

    Matrix timesEquals = left.copy();
    timesEquals.timesEquals(-2.0);
    assertMatrixEquals(new Matrix(new double[][]{{-2.0, 4.0}, {-6.0, -8.0}}), timesEquals);
  }

  @Test
  public void dimensionMismatchesThrowHelpfulExceptions() {
    Matrix matrix = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});

    expectIllegalArgument(() -> matrix.plus(new Matrix(1, 2)), "Matrix dimensions must agree.");
    expectIllegalArgument(() -> matrix.minus(new Matrix(1, 2)), "Matrix dimensions must agree.");
    expectIllegalArgument(() -> matrix.arrayTimes(new Matrix(1, 2)), "Matrix dimensions must agree.");
    expectIllegalArgument(() -> matrix.arrayRightDivide(new Matrix(1, 2)), "Matrix dimensions must agree.");
    expectIllegalArgument(() -> matrix.arrayLeftDivide(new Matrix(1, 2)), "Matrix dimensions must agree.");
    expectIllegalArgument(() -> matrix.times(new Matrix(3, 1)), "Matrix inner dimensions must agree.");
  }

  private static void expectIllegalArgument(Runnable runnable, String message) {
    try {
      runnable.run();
      fail("expected an IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertEquals(message, expected.getMessage());
    }
  }
}
