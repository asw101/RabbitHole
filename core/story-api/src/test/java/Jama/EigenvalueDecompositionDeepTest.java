package Jama;

import org.junit.Test;

import java.util.Arrays;

import static Jama.MatrixTestSupport.TOLERANCE;
import static Jama.MatrixTestSupport.assertMatrixEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EigenvalueDecompositionDeepTest {
  @Test
  public void symmetricMatricesProduceOrthogonalEigenvectorsAndDiagonalReconstruction() {
    Matrix symmetric = new Matrix(new double[][]{{2.0, 1.0}, {1.0, 2.0}});
    EigenvalueDecomposition decomposition = new EigenvalueDecomposition(symmetric);

    Matrix vectors = decomposition.getV();
    Matrix diagonal = decomposition.getD();
    assertMatrixEquals(symmetric, vectors.times(diagonal).times(vectors.transpose()), 1.0e-7);
    assertMatrixEquals(Matrix.identity(2, 2), vectors.transpose().times(vectors), 1.0e-7);

    double[] real = decomposition.getRealEigenvalues().clone();
    Arrays.sort(real);
    assertArrayEquals(new double[]{1.0, 3.0}, real, TOLERANCE);
    assertArrayEquals(new double[]{0.0, 0.0}, decomposition.getImagEigenvalues(), TOLERANCE);
    assertEquals(4.0, diagonal.trace(), TOLERANCE);
  }

  @Test
  public void nonsymmetricMatricesExposeComplexPairsInBlockDiagonalForm() {
    Matrix rotation = new Matrix(new double[][]{{0.0, -1.0}, {1.0, 0.0}});
    EigenvalueDecomposition decomposition = new EigenvalueDecomposition(rotation);

    Matrix vectors = decomposition.getV();
    Matrix diagonal = decomposition.getD();
    assertMatrixEquals(rotation.times(vectors), vectors.times(diagonal), 1.0e-6);

    double[] real = decomposition.getRealEigenvalues().clone();
    double[] imaginary = decomposition.getImagEigenvalues().clone();
    Arrays.sort(real);
    Arrays.sort(imaginary);
    assertArrayEquals(new double[]{0.0, 0.0}, real, TOLERANCE);
    assertArrayEquals(new double[]{-1.0, 1.0}, imaginary, TOLERANCE);
    assertEquals(2.0, Math.abs(diagonal.get(0, 1)) + Math.abs(diagonal.get(1, 0)), TOLERANCE);
  }
}
