package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector4Test {

  static final double EPSILON = 1e-10;
  static final Vector4 A = new Vector4(1, 2, 3, 4);
  static final Vector4 B = new Vector4(5, 6, 7, 8);

  // --- Static Constants ---

  @Test
  void zero_hasAllZeroComponents() {
    assertEquals(0, Vector4.ZERO.x());
    assertEquals(0, Vector4.ZERO.y());
    assertEquals(0, Vector4.ZERO.z());
    assertEquals(0, Vector4.ZERO.w());
  }

  @Test
  void unitX_correctValues() {
    assertEquals(1.0, Vector4.UNIT_X.x());
    assertEquals(0.0, Vector4.UNIT_X.y());
    assertEquals(0.0, Vector4.UNIT_X.z());
    assertEquals(0.0, Vector4.UNIT_X.w());
  }

  @Test
  void unitY_correctValues() {
    assertEquals(0.0, Vector4.UNIT_Y.x());
    assertEquals(1.0, Vector4.UNIT_Y.y());
    assertEquals(0.0, Vector4.UNIT_Y.z());
    assertEquals(0.0, Vector4.UNIT_Y.w());
  }

  @Test
  void unitZ_correctValues() {
    assertEquals(0.0, Vector4.UNIT_Z.x());
    assertEquals(0.0, Vector4.UNIT_Z.y());
    assertEquals(1.0, Vector4.UNIT_Z.z());
    assertEquals(0.0, Vector4.UNIT_Z.w());
  }

  @Test
  void unitW_correctValues() {
    assertEquals(0.0, Vector4.UNIT_W.x());
    assertEquals(0.0, Vector4.UNIT_W.y());
    assertEquals(0.0, Vector4.UNIT_W.z());
    assertEquals(1.0, Vector4.UNIT_W.w());
  }

  @Test
  void nan_allComponentsNaN() {
    assertTrue(Double.isNaN(Vector4.NaN.x()));
    assertTrue(Double.isNaN(Vector4.NaN.y()));
    assertTrue(Double.isNaN(Vector4.NaN.z()));
    assertTrue(Double.isNaN(Vector4.NaN.w()));
  }

  // --- isNaN ---

  @Test
  void isNaN_trueForNaNConstant() {
    assertTrue(Vector4.NaN.isNaN());
  }

  @Test
  void isNaN_falseForNormalVector() {
    assertFalse(A.isNaN());
  }

  @Test
  void isNaN_trueWhenSingleComponentNaN() {
    assertTrue(new Vector4(Double.NaN, 0, 0, 0).isNaN());
    assertTrue(new Vector4(0, Double.NaN, 0, 0).isNaN());
    assertTrue(new Vector4(0, 0, Double.NaN, 0).isNaN());
    assertTrue(new Vector4(0, 0, 0, Double.NaN).isNaN());
  }

  // --- isZero ---

  @Test
  void isZero_trueForZeroVector() {
    assertTrue(Vector4.ZERO.isZero());
  }

  @Test
  void isZero_falseForNonZero() {
    assertFalse(A.isZero());
  }

  // --- dotProduct ---

  @Test
  void dotProduct_knownResult() {
    // 1*5 + 2*6 + 3*7 + 4*8 = 5+12+21+32 = 70
    assertEquals(70.0, A.dotProduct(B), EPSILON);
  }

  @Test
  void dotProduct_orthogonalVectorsIsZero() {
    assertEquals(0.0, Vector4.UNIT_X.dotProduct(Vector4.UNIT_Y), EPSILON);
  }

  @Test
  void dotProduct_selfIsMagnitudeSquared() {
    assertEquals(A.magnitudeSquared(), A.dotProduct(A), EPSILON);
  }

  // --- times ---

  @Test
  void times_multipliesAllComponents() {
    Vector4 r = A.times(3.0);
    assertEquals(3.0, r.x(), EPSILON);
    assertEquals(6.0, r.y(), EPSILON);
    assertEquals(9.0, r.z(), EPSILON);
    assertEquals(12.0, r.w(), EPSILON);
  }

  @Test
  void times_zeroGivesZero() {
    Vector4 r = A.times(0.0);
    assertTrue(r.isZero());
  }

  // --- plus ---

  @Test
  void plus_addsComponents() {
    Vector4 r = A.plus(B);
    assertEquals(6.0, r.x(), EPSILON);
    assertEquals(8.0, r.y(), EPSILON);
    assertEquals(10.0, r.z(), EPSILON);
    assertEquals(12.0, r.w(), EPSILON);
  }

  @Test
  void plus_withZeroIsIdentity() {
    assertEquals(A, A.plus(Vector4.ZERO));
  }

  // --- minus ---

  @Test
  void minus_subtractsComponents() {
    Vector4 r = A.minus(B);
    assertEquals(-4.0, r.x(), EPSILON);
    assertEquals(-4.0, r.y(), EPSILON);
    assertEquals(-4.0, r.z(), EPSILON);
    assertEquals(-4.0, r.w(), EPSILON);
  }

  @Test
  void minus_selfIsZero() {
    Vector4 r = A.minus(A);
    assertTrue(r.isZero());
  }

  // --- negate ---

  @Test
  void negate_flipsSign() {
    Vector4 r = A.negate();
    assertEquals(-1.0, r.x(), EPSILON);
    assertEquals(-2.0, r.y(), EPSILON);
    assertEquals(-3.0, r.z(), EPSILON);
    assertEquals(-4.0, r.w(), EPSILON);
  }

  @Test
  void negate_doubleNegateRestoresOriginal() {
    assertEquals(A, A.negate().negate());
  }

  // --- dividedBy ---

  @Test
  void dividedBy_dividesAllComponents() {
    Vector4 r = new Vector4(10, 20, 30, 40).dividedBy(10.0);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
    assertEquals(4.0, r.w(), EPSILON);
  }

  // --- isWithinEpsilonOf ---

  @Test
  void isWithinEpsilonOf_identicalVectors() {
    assertTrue(A.isWithinEpsilonOf(A, 0.0001));
  }

  @Test
  void isWithinEpsilonOf_closeVectors() {
    Vector4 close = new Vector4(1.0001, 2.0001, 3.0001, 4.0001);
    assertTrue(A.isWithinEpsilonOf(close, 0.001));
  }

  @Test
  void isWithinEpsilonOf_farVectors() {
    assertFalse(A.isWithinEpsilonOf(B, 0.001));
  }

  // --- isWithinReasonableEpsilonOf ---

  @Test
  void isWithinReasonableEpsilonOf_identical() {
    assertTrue(A.isWithinReasonableEpsilonOf(A));
  }

  @Test
  void isWithinReasonableEpsilonOf_far() {
    assertFalse(A.isWithinReasonableEpsilonOf(B));
  }

  // --- magnitude ---

  @Test
  void magnitude_unitVector() {
    assertEquals(1.0, Vector4.UNIT_X.magnitude(), EPSILON);
  }

  @Test
  void magnitude_knownVector() {
    // sqrt(1+4+9+16) = sqrt(30)
    assertEquals(Math.sqrt(30), A.magnitude(), EPSILON);
  }

  @Test
  void magnitudeSquared_knownVector() {
    assertEquals(30.0, A.magnitudeSquared(), EPSILON);
  }

  // --- normalized ---

  @Test
  void normalized_hasMagnitudeOne() {
    Vector4 n = A.normalized();
    assertEquals(1.0, n.magnitude(), 1e-9);
  }

  @Test
  void normalized_unitVectorReturnsSelf() {
    assertSame(Vector4.UNIT_X, Vector4.UNIT_X.normalized());
  }

  // --- interpolate ---

  @Test
  void interpolate_atZeroReturnsStart() {
    Vector4 r = A.interpolate(B, 0.0);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atOneReturnsEnd() {
    Vector4 r = A.interpolate(B, 1.0);
    assertEquals(B.x(), r.x(), EPSILON);
    assertEquals(B.y(), r.y(), EPSILON);
    assertEquals(B.z(), r.z(), EPSILON);
  }
}
