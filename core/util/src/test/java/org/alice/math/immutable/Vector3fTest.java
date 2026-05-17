package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector3fTest {

  static final float EPSILON = 1e-6f;
  static final Vector3f A = new Vector3f(1f, 2f, 3f);
  static final Vector3f B = new Vector3f(4f, 5f, 6f);

  // --- Static Constants ---

  @Test
  void zero_hasAllZeroComponents() {
    assertEquals(0f, Vector3f.ZERO.x());
    assertEquals(0f, Vector3f.ZERO.y());
    assertEquals(0f, Vector3f.ZERO.z());
  }

  @Test
  void nan_hasAllNaNComponents() {
    assertTrue(Float.isNaN(Vector3f.NaN.x()));
    assertTrue(Float.isNaN(Vector3f.NaN.y()));
    assertTrue(Float.isNaN(Vector3f.NaN.z()));
  }

  // --- isNaN ---

  @Test
  void isNaN_trueForNaNConstant() {
    assertTrue(Vector3f.NaN.isNaN());
  }

  @Test
  void isNaN_falseForNormalVector() {
    assertFalse(A.isNaN());
  }

  @Test
  void isNaN_trueWhenOneComponentNaN() {
    assertTrue(new Vector3f(Float.NaN, 1f, 2f).isNaN());
    assertTrue(new Vector3f(1f, Float.NaN, 2f).isNaN());
    assertTrue(new Vector3f(1f, 2f, Float.NaN).isNaN());
  }

  // --- isWithinReasonableEpsilonOf ---

  @Test
  void isWithinReasonableEpsilonOf_identicalVectors() {
    assertTrue(A.isWithinReasonableEpsilonOf(A));
  }

  @Test
  void isWithinReasonableEpsilonOf_slightlyDifferent() {
    Vector3f close = new Vector3f(1.0001f, 2.0001f, 3.0001f);
    assertTrue(A.isWithinReasonableEpsilonOf(close));
  }

  @Test
  void isWithinReasonableEpsilonOf_veryDifferent() {
    assertFalse(A.isWithinReasonableEpsilonOf(B));
  }

  // --- plus ---

  @Test
  void plus_addsComponents() {
    Vector3f r = A.plus(B);
    assertEquals(5f, r.x(), EPSILON);
    assertEquals(7f, r.y(), EPSILON);
    assertEquals(9f, r.z(), EPSILON);
  }

  @Test
  void plus_withZeroIsIdentity() {
    Vector3f r = A.plus(Vector3f.ZERO);
    assertEquals(A, r);
  }

  // --- minus ---

  @Test
  void minus_subtractsComponents() {
    Vector3f r = A.minus(B);
    assertEquals(-3f, r.x(), EPSILON);
    assertEquals(-3f, r.y(), EPSILON);
    assertEquals(-3f, r.z(), EPSILON);
  }

  @Test
  void minus_selfIsZero() {
    Vector3f r = A.minus(A);
    assertEquals(0f, r.x(), EPSILON);
    assertEquals(0f, r.y(), EPSILON);
    assertEquals(0f, r.z(), EPSILON);
  }

  // --- times (vector) ---

  @Test
  void times_vector_multipliesComponents() {
    Vector3f r = A.times(B);
    assertEquals(4f, r.x(), EPSILON);
    assertEquals(10f, r.y(), EPSILON);
    assertEquals(18f, r.z(), EPSILON);
  }

  // --- times (scalar) ---

  @Test
  void times_scalar_multipliesAllComponents() {
    Vector3f r = A.times(3f);
    assertEquals(3f, r.x(), EPSILON);
    assertEquals(6f, r.y(), EPSILON);
    assertEquals(9f, r.z(), EPSILON);
  }

  @Test
  void times_zeroScalarGivesZeroVector() {
    Vector3f r = A.times(0f);
    assertEquals(0f, r.x(), EPSILON);
    assertEquals(0f, r.y(), EPSILON);
    assertEquals(0f, r.z(), EPSILON);
  }

  // --- dividedBy ---

  @Test
  void dividedBy_dividesAllComponents() {
    Vector3f r = new Vector3f(10f, 20f, 30f).dividedBy(10f);
    assertEquals(1f, r.x(), EPSILON);
    assertEquals(2f, r.y(), EPSILON);
    assertEquals(3f, r.z(), EPSILON);
  }

  @Test
  void dividedBy_oneIsIdentity() {
    Vector3f r = A.dividedBy(1f);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  // --- negate ---

  @Test
  void negate_flipsSign() {
    Vector3f r = A.negate();
    assertEquals(-1f, r.x(), EPSILON);
    assertEquals(-2f, r.y(), EPSILON);
    assertEquals(-3f, r.z(), EPSILON);
  }

  @Test
  void negate_doubleNegateRestoresOriginal() {
    Vector3f r = A.negate().negate();
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  // --- dotProduct ---

  @Test
  void dotProduct_orthogonalVectorsIsZero() {
    Vector3f i = new Vector3f(1f, 0f, 0f);
    Vector3f j = new Vector3f(0f, 1f, 0f);
    assertEquals(0.0, i.dotProduct(j), EPSILON);
  }

  @Test
  void dotProduct_knownResult() {
    // 1*4 + 2*5 + 3*6 = 32
    assertEquals(32.0, A.dotProduct(B), EPSILON);
  }

  // --- magnitude ---

  @Test
  void magnitude_unitVector() {
    Vector3f unit = new Vector3f(1f, 0f, 0f);
    assertEquals(1.0, unit.magnitude(), EPSILON);
  }

  @Test
  void magnitude_knownVector() {
    // sqrt(1+4+9) = sqrt(14)
    assertEquals(Math.sqrt(14), A.magnitude(), EPSILON);
  }

  @Test
  void magnitudeSquared_knownVector() {
    assertEquals(14.0, A.magnitudeSquared(), EPSILON);
  }

  // --- normalized ---

  @Test
  void normalized_hasMagnitudeOne() {
    Vector3f n = A.normalized();
    assertEquals(1.0, n.magnitude(), 1e-5);
  }

  @Test
  void normalized_unitVectorReturnsSelf() {
    Vector3f unit = new Vector3f(1f, 0f, 0f);
    assertSame(unit, unit.normalized());
  }

  // --- createNormalized ---

  @Test
  void createNormalized_normalCase() {
    Vector3f n = Vector3f.createNormalized(3f, 0f, 0f);
    assertEquals(1f, n.x(), EPSILON);
    assertEquals(0f, n.y(), EPSILON);
    assertEquals(0f, n.z(), EPSILON);
  }

  @Test
  void createNormalized_zeroVectorReturnsNaN() {
    Vector3f n = Vector3f.createNormalized(0f, 0f, 0f);
    assertTrue(n.isNaN());
  }

  @Test
  void createNormalized_alreadyNormalized() {
    Vector3f n = Vector3f.createNormalized(1f, 0f, 0f);
    assertEquals(1f, n.x(), EPSILON);
    assertEquals(0f, n.y(), EPSILON);
  }

  // --- crossProduct ---

  @Test
  void crossProduct_unitAxes() {
    Vector3f i = new Vector3f(1f, 0f, 0f);
    Vector3f j = new Vector3f(0f, 1f, 0f);
    Vector3f k = i.crossProduct(j);
    assertEquals(0f, k.x(), EPSILON);
    assertEquals(0f, k.y(), EPSILON);
    assertEquals(1f, k.z(), EPSILON);
  }

  @Test
  void crossProduct_anticommutative() {
    Vector3f ab = A.crossProduct(B);
    Vector3f ba = B.crossProduct(A);
    assertEquals(-ab.x(), ba.x(), EPSILON);
    assertEquals(-ab.y(), ba.y(), EPSILON);
    assertEquals(-ab.z(), ba.z(), EPSILON);
  }

  @Test
  void crossProduct_selfIsZero() {
    Vector3f r = A.crossProduct(A);
    assertEquals(0f, r.x(), EPSILON);
    assertEquals(0f, r.y(), EPSILON);
    assertEquals(0f, r.z(), EPSILON);
  }

  // --- interpolate ---

  @Test
  void interpolate_atZeroReturnsStart() {
    Vector3f r = A.interpolate(B, 0f);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atOneReturnsEnd() {
    Vector3f r = A.interpolate(B, 1f);
    assertEquals(B.x(), r.x(), EPSILON);
    assertEquals(B.y(), r.y(), EPSILON);
    assertEquals(B.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atHalfReturnsMidpoint() {
    Vector3f r = A.interpolate(B, 0.5f);
    assertEquals(2.5f, r.x(), EPSILON);
    assertEquals(3.5f, r.y(), EPSILON);
    assertEquals(4.5f, r.z(), EPSILON);
  }

  // --- isNormalized ---

  @Test
  void isNormalized_trueForUnitVector() {
    assertTrue(new Vector3f(1f, 0f, 0f).isNormalized());
  }

  @Test
  void isNormalized_falseForNonUnitVector() {
    assertFalse(A.isNormalized());
  }

  // --- isZero ---

  @Test
  void isZero_trueForZeroVector() {
    assertTrue(Vector3f.ZERO.isZero());
  }

  @Test
  void isZero_falseForNonZero() {
    assertFalse(A.isZero());
  }
}
