package edu.cmu.cs.dennisc.math.polynomial;

import org.alice.math.immutable.Vector4;
import org.junit.Test;

import static org.junit.Assert.*;

public class BasisMatrixCubicDeepTest {
  private static final double EPSILON = 1e-10;

  @Test
  public void evaluate_interpolatesEndpoints() {
    HermiteCubic cubic = new HermiteCubic(2.5, -1.25, 4.0, -3.0);

    assertEquals(2.5, cubic.evaluate(0.0), EPSILON);
    assertEquals(-1.25, cubic.evaluate(1.0), EPSILON);
  }

  @Test
  public void evaluateDerivative_matchesEndpointTangents() {
    HermiteCubic cubic = new HermiteCubic(2.5, -1.25, 4.0, -3.0);

    assertEquals(4.0, cubic.evaluateDerivative(0.0), EPSILON);
    assertEquals(-3.0, cubic.evaluateDerivative(1.0), EPSILON);
  }

  @Test
  public void evaluate_matchesExpandedHermitePolynomial() {
    double p0 = 1.5;
    double p1 = -2.0;
    double m0 = 0.75;
    double m1 = 3.25;
    HermiteCubic cubic = new HermiteCubic(p0, p1, m0, m1);

    assertEquals(expectedValue(0.25, p0, p1, m0, m1), cubic.evaluate(0.25), EPSILON);
    assertEquals(expectedValue(0.50, p0, p1, m0, m1), cubic.evaluate(0.50), EPSILON);
    assertEquals(expectedValue(0.75, p0, p1, m0, m1), cubic.evaluate(0.75), EPSILON);
  }

  @Test
  public void evaluateDerivative_matchesExpandedHermiteDerivative() {
    double p0 = -3.0;
    double p1 = 2.0;
    double m0 = 5.0;
    double m1 = -1.0;
    HermiteCubic cubic = new HermiteCubic(p0, p1, m0, m1);

    assertEquals(expectedDerivative(0.25, p0, p1, m0, m1), cubic.evaluateDerivative(0.25), EPSILON);
    assertEquals(expectedDerivative(0.50, p0, p1, m0, m1), cubic.evaluateDerivative(0.50), EPSILON);
    assertEquals(expectedDerivative(0.75, p0, p1, m0, m1), cubic.evaluateDerivative(0.75), EPSILON);
  }

  @Test
  public void scalarAndVectorConstructors_areEquivalent() {
    Vector4 geometry = new Vector4(-1.0, 4.0, 2.5, -0.5);
    HermiteCubic fromVector = new HermiteCubic(geometry);
    HermiteCubic fromScalars = new HermiteCubic(-1.0, 4.0, 2.5, -0.5);

    double[] samples = {0.0, 0.2, 0.5, 0.8, 1.0};
    for (double t : samples) {
      assertEquals(fromScalars.evaluate(t), fromVector.evaluate(t), EPSILON);
      assertEquals(fromScalars.evaluateDerivative(t), fromVector.evaluateDerivative(t), EPSILON);
    }
  }

  @Test
  public void isNaN_reflectsGeometryState() {
    assertFalse(new HermiteCubic(1.0, 2.0, 3.0, 4.0).isNaN());
    assertTrue(new HermiteCubic(new Vector4(1.0, Double.NaN, 3.0, 4.0)).isNaN());
  }

  @Test
  public void evaluate_linearConfigurationProducesLinearCurve() {
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 1.0, 1.0);

    assertEquals(0.25, cubic.evaluate(0.25), EPSILON);
    assertEquals(0.50, cubic.evaluate(0.50), EPSILON);
    assertEquals(0.75, cubic.evaluate(0.75), EPSILON);
    assertEquals(1.0, cubic.evaluateDerivative(0.25), EPSILON);
    assertEquals(1.0, cubic.evaluateDerivative(0.50), EPSILON);
    assertEquals(1.0, cubic.evaluateDerivative(0.75), EPSILON);
  }

  private static double expectedValue(double t, double p0, double p1, double m0, double m1) {
    double t2 = t * t;
    double t3 = t2 * t;
    return ((2.0 * t3) - (3.0 * t2) + 1.0) * p0
        + ((-2.0 * t3) + (3.0 * t2)) * p1
        + (t3 - (2.0 * t2) + t) * m0
        + (t3 - t2) * m1;
  }

  private static double expectedDerivative(double t, double p0, double p1, double m0, double m1) {
    double t2 = t * t;
    return ((6.0 * t2) - (6.0 * t)) * p0
        + ((-6.0 * t2) + (6.0 * t)) * p1
        + ((3.0 * t2) - (4.0 * t) + 1.0) * m0
        + ((3.0 * t2) - (2.0 * t)) * m1;
  }
}
