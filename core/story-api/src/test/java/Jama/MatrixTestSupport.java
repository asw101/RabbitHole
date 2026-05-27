package Jama;

import static org.junit.Assert.assertEquals;

final class MatrixTestSupport {
  static final double TOLERANCE = 1.0e-8;

  private MatrixTestSupport() {
  }

  static void assertMatrixEquals(Matrix expected, Matrix actual) {
    assertMatrixEquals(expected, actual, TOLERANCE);
  }

  static void assertMatrixEquals(Matrix expected, Matrix actual, double tolerance) {
    assertEquals(expected.getRowDimension(), actual.getRowDimension());
    assertEquals(expected.getColumnDimension(), actual.getColumnDimension());
    for (int i = 0; i < expected.getRowDimension(); i++) {
      for (int j = 0; j < expected.getColumnDimension(); j++) {
        assertEquals(
            "matrix entry [" + i + "," + j + "]",
            expected.get(i, j),
            actual.get(i, j),
            tolerance);
      }
    }
  }
}
