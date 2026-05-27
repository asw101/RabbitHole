package Jama;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SingularValueDecompositionTest {
  private static final double TOLERANCE = 1.0e-9;

  @Test
  public void reconstructsOriginalTallMatrixFromUSVTranspose() {
    Matrix source = new Matrix(new double[][]{{1, 2}, {3, 4}, {5, 6}});

    SingularValueDecomposition decomposition = new SingularValueDecomposition(source);
    Matrix reconstructed = decomposition.getU().times(decomposition.getS()).times(decomposition.getV().transpose());

    assertEquals(source.getRowDimension(), reconstructed.getRowDimension());
    assertEquals(source.getColumnDimension(), reconstructed.getColumnDimension());
    for (int row = 0; row < source.getRowDimension(); row++) {
      for (int column = 0; column < source.getColumnDimension(); column++) {
        assertEquals(source.get(row, column), reconstructed.get(row, column), TOLERANCE);
      }
    }
  }

  @Test
  public void reportsSortedSingularValuesAndDiagonalMatrix() {
    Matrix diagonal = new Matrix(new double[][]{{3, 0}, {0, -5}});

    SingularValueDecomposition decomposition = new SingularValueDecomposition(diagonal);
    double[] singularValues = decomposition.getSingularValues();
    Matrix s = decomposition.getS();

    assertArrayEquals(new double[]{5.0, 3.0}, singularValues, TOLERANCE);
    assertEquals(5.0, decomposition.norm2(), TOLERANCE);
    assertEquals(5.0, s.get(0, 0), TOLERANCE);
    assertEquals(3.0, s.get(1, 1), TOLERANCE);
    assertEquals(0.0, s.get(0, 1), TOLERANCE);
    assertEquals(0.0, s.get(1, 0), TOLERANCE);
  }

  @Test
  public void rankAndConditionReflectRankDeficientInput() {
    Matrix rankDeficient = new Matrix(new double[][]{{2, 4}, {1, 2}, {0, 0}});

    SingularValueDecomposition decomposition = new SingularValueDecomposition(rankDeficient);

    assertEquals(1, decomposition.rank());
    assertTrue(Double.isInfinite(decomposition.cond()) || decomposition.cond() > 1.0e12);
  }
}
