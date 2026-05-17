package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Vector3Test {

  static final double EPSILON = 1e-10;
  static final Vector3 A = new Vector3(1, 2, 3);
  static final Vector3 B = new Vector3(4, 5, 6);

  @Test
  void plus_addsComponents() {
    Vector3 r = A.plus(B);
    assertEquals(5.0, r.x(), EPSILON);
    assertEquals(7.0, r.y(), EPSILON);
    assertEquals(9.0, r.z(), EPSILON);
  }

  @Test
  void minus_subtractsComponents() {
    Vector3 r = A.minus(B);
    assertEquals(-3.0, r.x(), EPSILON);
    assertEquals(-3.0, r.y(), EPSILON);
    assertEquals(-3.0, r.z(), EPSILON);
  }

  @Test
  void times_multipliesByScalar() {
    Vector3 r = A.times(3.0);
    assertEquals(3.0, r.x(), EPSILON);
    assertEquals(6.0, r.y(), EPSILON);
    assertEquals(9.0, r.z(), EPSILON);
  }

  @Test
  void dividedBy_dividesByScalar() {
    Vector3 r = new Vector3(10, 20, 30).dividedBy(10.0);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void negate_negatesComponents() {
    Vector3 r = A.negate();
    assertEquals(-1.0, r.x(), EPSILON);
    assertEquals(-2.0, r.y(), EPSILON);
    assertEquals(-3.0, r.z(), EPSILON);
  }

  @Test
  void dotProduct_computes() {
    double d = A.dotProduct(B);
    assertEquals(32.0, d, EPSILON); // 1*4 + 2*5 + 3*6
  }

  @Test
  void crossProduct_computesPerpendicular() {
    Vector3 cross = Vector3.POSITIVE_X_AXIS.crossProduct(Vector3.POSITIVE_Y_AXIS);
    assertEquals(0.0, cross.x(), EPSILON);
    assertEquals(0.0, cross.y(), EPSILON);
    assertEquals(1.0, cross.z(), EPSILON);
  }

  @Test
  void crossProduct_general() {
    Vector3 cross = A.crossProduct(B);
    // (2*6 - 3*5, 4*3 - 6*1, 1*5 - 2*4)
    assertEquals(-3.0, cross.x(), EPSILON);
    assertEquals(6.0, cross.y(), EPSILON);
    assertEquals(-3.0, cross.z(), EPSILON);
  }

  @Test
  void interpolate_atZero_returnsA() {
    Vector3 r = A.interpolate(B, 0.0);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atOne_returnsB() {
    Vector3 r = A.interpolate(B, 1.0);
    assertEquals(B.x(), r.x(), EPSILON);
    assertEquals(B.y(), r.y(), EPSILON);
    assertEquals(B.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atHalf_returnsMidpoint() {
    Vector3 r = A.interpolate(B, 0.5);
    assertEquals(2.5, r.x(), EPSILON);
    assertEquals(3.5, r.y(), EPSILON);
    assertEquals(4.5, r.z(), EPSILON);
  }

  @Test
  void magnitudeSquared_computes() {
    assertEquals(14.0, A.magnitudeSquared(), EPSILON);
  }

  @Test
  void magnitude_computes() {
    assertEquals(Math.sqrt(14.0), A.magnitude(), EPSILON);
  }

  @Test
  void magnitude_unitVector() {
    assertEquals(1.0, Vector3.POSITIVE_X_AXIS.magnitude(), EPSILON);
  }

  @Test
  void magnitudeSquared_static() {
    assertEquals(14.0, Vector3.magnitudeSquared(1, 2, 3), EPSILON);
  }

  @Test
  void magnitude_static_unitVector() {
    assertEquals(1.0, Vector3.magnitude(1, 0, 0), EPSILON);
  }

  @Test
  void magnitude_static_general() {
    assertEquals(Math.sqrt(14.0), Vector3.magnitude(1, 2, 3), EPSILON);
  }

  @Test
  void normalized_unitVector_returnsSame() {
    assertSame(Vector3.POSITIVE_X_AXIS, Vector3.POSITIVE_X_AXIS.normalized());
  }

  @Test
  void normalized_general_hasUnitLength() {
    Vector3 n = A.normalized();
    assertEquals(1.0, n.magnitude(), EPSILON);
  }

  @Test
  void createNormalized_zeroVector_returnsNaN() {
    Vector3 r = Vector3.createNormalized(0, 0, 0);
    assertTrue(r.isNaN());
  }

  @Test
  void createNormalized_unitVector_exact() {
    Vector3 r = Vector3.createNormalized(1, 0, 0);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(0.0, r.y(), EPSILON);
  }

  @Test
  void createNormalized_general() {
    Vector3 r = Vector3.createNormalized(3, 4, 0);
    assertEquals(0.6, r.x(), EPSILON);
    assertEquals(0.8, r.y(), EPSILON);
    assertEquals(0.0, r.z(), EPSILON);
  }

  @Test
  void isNormalized_unitVector() {
    assertTrue(Vector3.POSITIVE_X_AXIS.isNormalized());
    assertTrue(Vector3.POSITIVE_Y_AXIS.isNormalized());
    assertTrue(Vector3.POSITIVE_Z_AXIS.isNormalized());
  }

  @Test
  void isNormalized_nonUnit() {
    assertFalse(A.isNormalized());
  }

  @Test
  void isOrthogonalTo_perpendicularVectors() {
    assertTrue(Vector3.POSITIVE_X_AXIS.isOrthogonalTo(Vector3.POSITIVE_Y_AXIS));
    assertTrue(Vector3.POSITIVE_X_AXIS.isOrthogonalTo(Vector3.POSITIVE_Z_AXIS));
    assertTrue(Vector3.POSITIVE_Y_AXIS.isOrthogonalTo(Vector3.POSITIVE_Z_AXIS));
  }

  @Test
  void isOrthogonalTo_parallelVectors() {
    assertFalse(Vector3.POSITIVE_X_AXIS.isOrthogonalTo(Vector3.POSITIVE_X_AXIS));
  }

  @Test
  void asScaleMatrix() {
    OrthogonalMatrix3x3 m = A.asScaleMatrix();
    assertEquals(1.0, m.right().x(), EPSILON);
    assertEquals(2.0, m.up().y(), EPSILON);
    assertEquals(3.0, m.backward().z(), EPSILON);
    assertEquals(0.0, m.right().y(), EPSILON);
  }

  @Test
  void asPoint_convertsCorrectly() {
    Point3 p = A.asPoint();
    assertEquals(1.0, p.x(), EPSILON);
    assertEquals(2.0, p.y(), EPSILON);
    assertEquals(3.0, p.z(), EPSILON);
  }

  @Test
  void withX_changesOnlyX() {
    Vector3 r = A.withX(10);
    assertEquals(10.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void withY_changesOnlyY() {
    Vector3 r = A.withY(10);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(10.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void withZ_changesOnlyZ() {
    Vector3 r = A.withZ(10);
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(10.0, r.z(), EPSILON);
  }

  @Test
  void projectedOnto_parallel() {
    Vector3 proj = A.projectedOnto(Vector3.POSITIVE_X_AXIS);
    assertEquals(1.0, proj.x(), EPSILON);
    assertEquals(0.0, proj.y(), EPSILON);
    assertEquals(0.0, proj.z(), EPSILON);
  }

  @Test
  void projectedOnto_general() {
    Vector3 proj = new Vector3(3, 4, 0).projectedOnto(Vector3.POSITIVE_X_AXIS);
    assertEquals(3.0, proj.x(), EPSILON);
    assertEquals(0.0, proj.y(), EPSILON);
  }

  @Test
  void angleWith_sameDirection_zero() {
    Angle a = Vector3.POSITIVE_X_AXIS.angleWith(Vector3.POSITIVE_X_AXIS);
    assertEquals(0.0, a.getAsRadians(), EPSILON);
  }

  @Test
  void angleWith_perpendicular() {
    Angle a = Vector3.POSITIVE_X_AXIS.angleWith(Vector3.POSITIVE_Y_AXIS);
    assertEquals(Math.PI / 2, a.getAsRadians(), EPSILON);
  }

  @Test
  void angleWith_opposite_pi() {
    Angle a = Vector3.POSITIVE_X_AXIS.angleWith(Vector3.NEGATIVE_X_AXIS);
    assertEquals(Math.PI, a.getAsRadians(), EPSILON);
  }

  @Test
  void angleWith_dotBeyondOne_clamped() {
    // When vectors are very close, dot product can slightly exceed 1.0 due to FP errors
    Angle a = Vector3.POSITIVE_X_AXIS.angleWith(new Vector3(1, 0, 0));
    assertEquals(0.0, a.getAsRadians(), EPSILON);
  }

  @Test
  void constants_haveCorrectValues() {
    assertEquals(new Vector3(0, 0, 0), Vector3.ZERO);
    assertTrue(Vector3.NaN.isNaN());
    assertEquals(new Vector3(1, 0, 0), Vector3.POSITIVE_X_AXIS);
    assertEquals(new Vector3(0, 1, 0), Vector3.POSITIVE_Y_AXIS);
    assertEquals(new Vector3(0, 0, 1), Vector3.POSITIVE_Z_AXIS);
    assertEquals(new Vector3(-1, 0, 0), Vector3.NEGATIVE_X_AXIS);
    assertEquals(new Vector3(0, -1, 0), Vector3.NEGATIVE_Y_AXIS);
    assertEquals(new Vector3(0, 0, -1), Vector3.NEGATIVE_Z_AXIS);
  }
}
