package edu.cmu.cs.dennisc.math.polynomial;

import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Vector4;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BasisMatrixCubicTest {
  private static final double TOLERANCE = 1.0e-9;

  private static final class TestBasisMatrixCubic extends BasisMatrixCubic {
    private TestBasisMatrixCubic(Matrix4x4 h, Vector4 g) {
      super(h, g);
    }
  }

  @Test
  public void evaluateAndDerivativeUseBasisColumnsAsPolynomialCoefficients() {
    TestBasisMatrixCubic cubic = new TestBasisMatrixCubic(Matrix4x4.IDENTITY, new Vector4(2.0, 3.0, 5.0, 7.0));

    assertEquals(17.0, cubic.evaluate(1.0), TOLERANCE);
    assertEquals(10.5, cubic.evaluate(0.5), TOLERANCE);
    assertEquals(17.0, cubic.evaluateDerivative(1.0), TOLERANCE);
    assertEquals(9.5, cubic.evaluateDerivative(0.5), TOLERANCE);
    assertFalse(cubic.isNaN());
  }

  @Test
  public void isNaNReportsInvalidBasisOrControlVector() {
    assertTrue(new TestBasisMatrixCubic(Matrix4x4.NaN, new Vector4(1, 2, 3, 4)).isNaN());
    assertTrue(new TestBasisMatrixCubic(Matrix4x4.IDENTITY, Vector4.NaN).isNaN());
  }
}
