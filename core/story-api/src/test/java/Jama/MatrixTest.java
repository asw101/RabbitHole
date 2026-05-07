package Jama;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MatrixTest {
  private static final double TOLERANCE = 1.0e-9;

  @Test
  public void squareSolveProducesExpectedSolutionAndSmallResidual() {
    Matrix coefficients = new Matrix(new double[][] {
        {3.0, 2.0},
        {1.0, 2.0}
    });
    Matrix rightHandSide = new Matrix(new double[][] {
        {18.0},
        {14.0}
    });

    Matrix solution = coefficients.solve(rightHandSide);

    assertMatrixEquals(new double[][] {
        {2.0},
        {6.0}
    }, solution);
    assertEquals(4.0, coefficients.det(), TOLERANCE);
    assertEquals(0.0, coefficients.times(solution).minus(rightHandSide).normInf(), TOLERANCE);
  }

  @Test
  public void rectangularSolveUsesLeastSquaresPath() {
    Matrix design = new Matrix(new double[][] {
        {1.0, 1.0},
        {1.0, 2.0},
        {1.0, 3.0}
    });
    Matrix observations = new Matrix(new double[][] {
        {1.0},
        {2.0},
        {3.0}
    });

    Matrix fitted = design.solve(observations);

    assertMatrixEquals(new double[][] {
        {0.0},
        {1.0}
    }, fitted);
    assertEquals(0.0, design.times(fitted).minus(observations).normInf(), TOLERANCE);
  }

  @Test
  public void submatrixSelectionAndReplacementPreserveDocumentedIndexOrder() {
    Matrix matrix = new Matrix(new double[][] {
        {1.0, 2.0, 3.0},
        {4.0, 5.0, 6.0},
        {7.0, 8.0, 9.0}
    });

    Matrix selected = matrix.getMatrix(0, 1, new int[] {2, 0});
    matrix.setMatrix(new int[] {0, 2}, 1, 2, new Matrix(new double[][] {
        {20.0, 30.0},
        {80.0, 90.0}
    }));

    assertMatrixEquals(new double[][] {
        {3.0, 1.0},
        {6.0, 4.0}
    }, selected);
    assertMatrixEquals(new double[][] {
        {1.0, 20.0, 30.0},
        {4.0, 5.0, 6.0},
        {7.0, 80.0, 90.0}
    }, matrix);
  }

  @Test
  public void readAcceptsLeadingBlankLinesAndScientificNotation() throws Exception {
    Matrix matrix = Matrix.read(new BufferedReader(new StringReader("""

         1.0 2.5E1 -3.0
         4.25 5.0 6.75

        """)));

    assertMatrixEquals(new double[][] {
        {1.0, 25.0, -3.0},
        {4.25, 5.0, 6.75}
    }, matrix);
  }

  @Test
  public void readRejectsRowsThatDoNotMatchFirstRowWidth() throws Exception {
    try {
      Matrix.read(new BufferedReader(new StringReader("""
          1.0 2.0 3.0
          4.0 5.0

          """)));
      fail("Expected short row to fail");
    } catch (IOException e) {
      assertEquals("Row 2 is too short.", e.getMessage());
    }

    try {
      Matrix.read(new BufferedReader(new StringReader("""
          1.0 2.0
          3.0 4.0 5.0

          """)));
      fail("Expected long row to fail");
    } catch (IOException e) {
      assertEquals("Row 2 is too long.", e.getMessage());
    }
  }

  @Test
  public void dimensionMismatchExceptionsKeepCurrentMessages() {
    try {
      new Matrix(1, 2).plus(new Matrix(2, 1));
      fail("Expected mismatched addition to fail");
    } catch (IllegalArgumentException e) {
      assertEquals("Matrix dimensions must agree.", e.getMessage());
    }

    try {
      new Matrix(1, 2).times(new Matrix(3, 1));
      fail("Expected mismatched multiplication to fail");
    } catch (IllegalArgumentException e) {
      assertEquals("Matrix inner dimensions must agree.", e.getMessage());
    }
  }

  private static void assertMatrixEquals(double[][] expected, Matrix actual) {
    assertEquals("row dimension", expected.length, actual.getRowDimension());
    assertEquals("column dimension", expected[0].length, actual.getColumnDimension());
    for (int row = 0; row < expected.length; row++) {
      for (int column = 0; column < expected[row].length; column++) {
        assertEquals(
            "entry [" + row + "][" + column + "]",
            expected[row][column],
            actual.get(row, column),
            TOLERANCE);
      }
    }
  }
}
