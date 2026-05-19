package edu.cmu.cs.dennisc.math.polynomial;

import org.junit.Test;

import static org.junit.Assert.*;

public class HermiteCubicTest {

  @Test
  public void evaluate_atZero_returnsG0() {
    // Hermite: h(0) = g0 (first geometry point)
    HermiteCubic cubic = new HermiteCubic(1.0, 0.0, 0.0, 0.0);
    assertEquals(1.0, cubic.evaluate(0.0), 1e-10);
  }

  @Test
  public void evaluate_atOne_returnsG1() {
    // Hermite: h(1) = g1 (second geometry point)
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 0.0, 0.0);
    assertEquals(1.0, cubic.evaluate(1.0), 1e-10);
  }

  @Test
  public void evaluate_endpointInterpolation() {
    HermiteCubic cubic = new HermiteCubic(2.0, 5.0, 0.0, 0.0);
    assertEquals(2.0, cubic.evaluate(0.0), 1e-10);
    assertEquals(5.0, cubic.evaluate(1.0), 1e-10);
  }

  @Test
  public void evaluate_withTangents() {
    // g0=0, g1=1, tangent0=1, tangent1=1 gives a smooth curve
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 1.0, 1.0);
    assertEquals(0.0, cubic.evaluate(0.0), 1e-10);
    assertEquals(1.0, cubic.evaluate(1.0), 1e-10);
    // Midpoint should be 0.5 for symmetric tangents
    assertEquals(0.5, cubic.evaluate(0.5), 1e-10);
  }

  @Test
  public void evaluate_linearCase() {
    // g0=0, g1=1, tangent0=1, tangent1=1 is linear (f(t)=t)
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 1.0, 1.0);
    assertEquals(0.25, cubic.evaluate(0.25), 1e-10);
    assertEquals(0.75, cubic.evaluate(0.75), 1e-10);
  }

  @Test
  public void evaluateDerivative_linearCase() {
    // For f(t) = t, derivative should be 1 everywhere
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 1.0, 1.0);
    assertEquals(1.0, cubic.evaluateDerivative(0.0), 1e-10);
    assertEquals(1.0, cubic.evaluateDerivative(0.5), 1e-10);
    assertEquals(1.0, cubic.evaluateDerivative(1.0), 1e-10);
  }

  @Test
  public void evaluateDerivative_atEndpoints() {
    // g0=0, g1=0, tangent0=2, tangent1=3
    HermiteCubic cubic = new HermiteCubic(0.0, 0.0, 2.0, 3.0);
    assertEquals(2.0, cubic.evaluateDerivative(0.0), 1e-10);
    assertEquals(3.0, cubic.evaluateDerivative(1.0), 1e-10);
  }

  @Test
  public void isNaN_normalValues_returnsFalse() {
    HermiteCubic cubic = new HermiteCubic(1.0, 2.0, 3.0, 4.0);
    assertFalse(cubic.isNaN());
  }

  @Test
  public void isNaN_nanInGeometry_returnsTrue() {
    HermiteCubic cubic = new HermiteCubic(Double.NaN, 2.0, 3.0, 4.0);
    assertTrue(cubic.isNaN());
  }

  @Test
  public void evaluate_constantFunction() {
    // g0=5, g1=5, tangent0=0, tangent1=0 → constant 5
    HermiteCubic cubic = new HermiteCubic(5.0, 5.0, 0.0, 0.0);
    assertEquals(5.0, cubic.evaluate(0.0), 1e-10);
    assertEquals(5.0, cubic.evaluate(0.5), 1e-10);
    assertEquals(5.0, cubic.evaluate(1.0), 1e-10);
  }

  @Test
  public void evaluateDerivative_constantFunction_isZero() {
    HermiteCubic cubic = new HermiteCubic(5.0, 5.0, 0.0, 0.0);
    assertEquals(0.0, cubic.evaluateDerivative(0.0), 1e-10);
    assertEquals(0.0, cubic.evaluateDerivative(0.5), 1e-10);
    assertEquals(0.0, cubic.evaluateDerivative(1.0), 1e-10);
  }

  @Test
  public void evaluate_negativeValues() {
    HermiteCubic cubic = new HermiteCubic(-3.0, -1.0, 0.0, 0.0);
    assertEquals(-3.0, cubic.evaluate(0.0), 1e-10);
    assertEquals(-1.0, cubic.evaluate(1.0), 1e-10);
  }

  @Test
  public void evaluate_outsideUnitInterval() {
    HermiteCubic cubic = new HermiteCubic(0.0, 1.0, 1.0, 1.0);
    // Extrapolation beyond [0,1] should still produce a result
    double atTwo = cubic.evaluate(2.0);
    assertFalse(Double.isNaN(atTwo));
  }

  @Test
  public void implementsCubicInterface() {
    HermiteCubic cubic = new HermiteCubic(0, 1, 0, 0);
    assertTrue(cubic instanceof Cubic);
    assertTrue(cubic instanceof Polynomial);
  }
}
