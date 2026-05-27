package Jama;

import org.junit.Test;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SingularValueDecompositionDeepTest {
  @Test
  public void decompositionReconstructsTallMatricesAndReportsSpectralMetrics() {
    Matrix matrix = new Matrix(new double[][]{
        {1.0, 0.0, 0.0},
        {0.0, 2.0, 0.0},
        {0.0, 0.0, 3.0},
        {0.0, 0.0, 0.0}
    });
    SingularValueDecomposition decomposition = new SingularValueDecomposition(matrix);

    assertMatrixEquals(matrix, decomposition.getU().times(decomposition.getS()).times(decomposition.getV().transpose()), 1.0e-7);
    assertArrayEquals(new double[]{3.0, 2.0, 1.0}, decomposition.getSingularValues(), TOLERANCE);
    assertEquals(3.0, decomposition.norm2(), TOLERANCE);
    assertEquals(3.0, decomposition.cond(), TOLERANCE);
    assertEquals(3, decomposition.rank());
  }

  @Test
  public void wideAndRankDeficientMatricesStillExposeConsistentFactors() {
    Matrix matrix = new Matrix(new double[][]{{1.0, 2.0, 3.0}, {2.0, 4.0, 6.0}});
    SingularValueDecomposition decomposition = new SingularValueDecomposition(matrix);

    assertEquals(2, decomposition.getU().getRowDimension());
    assertEquals(3, decomposition.getU().getColumnDimension());
    assertEquals(3, decomposition.getV().getRowDimension());
    assertEquals(3, decomposition.getS().getColumnDimension());
    assertEquals(1, decomposition.rank());
    assertTrue(Double.isInfinite(decomposition.cond()) || (decomposition.cond() > 1.0e12));
    assertTrue(decomposition.getSingularValues()[0] >= decomposition.getSingularValues()[1]);
  }
}
