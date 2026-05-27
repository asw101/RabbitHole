package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MatrixSubmatrixMutationTest {
  @Test
  public void getMatrixAndSetMatrixOverloadsExtractAndReplaceExpectedRegions() {
    Matrix matrix = new Matrix(new double[][]{
        {1.0, 2.0, 3.0},
        {4.0, 5.0, 6.0},
        {7.0, 8.0, 9.0}
    });

    assertMatrixEquals(
        new Matrix(new double[][]{{2.0, 3.0}, {5.0, 6.0}}),
        matrix.getMatrix(0, 1, 1, 2));
    assertMatrixEquals(
        new Matrix(new double[][]{{7.0, 9.0}, {1.0, 3.0}}),
        matrix.getMatrix(new int[]{2, 0}, new int[]{0, 2}));
    assertMatrixEquals(
        new Matrix(new double[][]{{4.0, 6.0}, {7.0, 9.0}}),
        matrix.getMatrix(1, 2, new int[]{0, 2}));
    assertMatrixEquals(
        new Matrix(new double[][]{{2.0, 3.0}, {8.0, 9.0}}),
        matrix.getMatrix(new int[]{0, 2}, 1, 2));

    matrix.set(0, 0, -1.0);
    assertEquals(-1.0, matrix.get(0, 0), 0.0);

    matrix.setMatrix(0, 1, 1, 2, new Matrix(new double[][]{{10.0, 11.0}, {12.0, 13.0}}));
    matrix.setMatrix(new int[]{0, 2}, new int[]{0, 2}, new Matrix(new double[][]{{14.0, 15.0}, {16.0, 17.0}}));
    matrix.setMatrix(new int[]{1, 2}, 0, 1, new Matrix(new double[][]{{18.0, 19.0}, {20.0, 21.0}}));
    matrix.setMatrix(0, 1, new int[]{1, 2}, new Matrix(new double[][]{{22.0, 23.0}, {24.0, 25.0}}));

    assertMatrixEquals(
        new Matrix(new double[][]{
            {14.0, 22.0, 23.0},
            {18.0, 24.0, 25.0},
            {20.0, 21.0, 17.0}
        }),
        matrix);
  }

  @Test
  public void invalidSubmatrixRequestsUseDocumentedMessages() {
    Matrix matrix = new Matrix(new double[][]{{1.0, 2.0}, {3.0, 4.0}});

    expectOutOfBounds(() -> matrix.getMatrix(0, 2, 0, 1));
    expectOutOfBounds(() -> matrix.getMatrix(new int[]{0, 3}, new int[]{0}));
    expectOutOfBounds(() -> matrix.getMatrix(0, 1, new int[]{0, 2}));
    expectOutOfBounds(() -> matrix.getMatrix(new int[]{0, 2}, 0, 1));
    expectOutOfBounds(() -> matrix.setMatrix(0, 2, 0, 0, new Matrix(3, 1)));
    expectOutOfBounds(() -> matrix.setMatrix(new int[]{0, 2}, new int[]{0}, new Matrix(2, 1)));
    expectOutOfBounds(() -> matrix.setMatrix(new int[]{0, 2}, 0, 0, new Matrix(2, 1)));
    expectOutOfBounds(() -> matrix.setMatrix(0, 1, new int[]{0, 2}, new Matrix(2, 2)));
  }

  private static void expectOutOfBounds(Runnable runnable) {
    try {
      runnable.run();
      fail("expected an ArrayIndexOutOfBoundsException");
    } catch (ArrayIndexOutOfBoundsException expected) {
      assertEquals("Submatrix indices", expected.getMessage());
    }
  }
}
