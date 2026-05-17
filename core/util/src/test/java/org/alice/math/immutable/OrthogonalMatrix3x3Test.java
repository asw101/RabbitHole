package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrthogonalMatrix3x3Test {

  static final double EPSILON = 1e-10;
  static final OrthogonalMatrix3x3 IDENTITY = OrthogonalMatrix3x3.IDENTITY;

  @Test
  void identity_hasCorrectVectors() {
    assertEquals(Vector3.POSITIVE_X_AXIS, IDENTITY.right());
    assertEquals(Vector3.POSITIVE_Y_AXIS, IDENTITY.up());
    assertEquals(Vector3.POSITIVE_Z_AXIS, IDENTITY.backward());
  }

  @Test
  void getRight_getUp_getBackward() {
    assertEquals(IDENTITY.right(), IDENTITY.getRight());
    assertEquals(IDENTITY.up(), IDENTITY.getUp());
    assertEquals(IDENTITY.backward(), IDENTITY.getBackward());
  }

  @Test
  void isNaN_identity() {
    assertFalse(IDENTITY.isNaN());
  }

  @Test
  void isNaN_nan() {
    assertTrue(OrthogonalMatrix3x3.NaN.isNaN());
  }

  @Test
  void isIdentity_identity() {
    assertTrue(IDENTITY.isIdentity());
  }

  @Test
  void isIdentity_notIdentity() {
    OrthogonalMatrix3x3 m = IDENTITY.applyRotationAboutArbitraryAxis(Vector3.POSITIVE_X_AXIS, new AngleInRadians(1.0));
    assertFalse(m.isIdentity());
  }

  @Test
  void deviationFromNormal_identity() {
    assertEquals(0.0, IDENTITY.deviationFromNormal(), EPSILON);
  }

  @Test
  void isAlignedWith_same() {
    assertTrue(IDENTITY.isAlignedWith(IDENTITY));
  }

  @Test
  void isAlignedWith_rotated() {
    OrthogonalMatrix3x3 rotated = IDENTITY.applyRotationAboutArbitraryAxis(Vector3.POSITIVE_Y_AXIS, new AngleInRadians(0.5));
    assertFalse(IDENTITY.isAlignedWith(rotated));
  }

  @Test
  void applyRotationAboutArbitraryAxis_xAxis() {
    OrthogonalMatrix3x3 rotated = IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_X_AXIS, new AngleInRadians(Math.PI / 2));
    assertFalse(rotated.isIdentity());
    // After 90° around X: Y→Z, Z→-Y
    assertEquals(0.0, rotated.up().y(), 0.01);
  }

  @Test
  void applyRotationAboutArbitraryAxis_yAxis() {
    OrthogonalMatrix3x3 rotated = IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_Y_AXIS, new AngleInRadians(Math.PI / 2));
    assertFalse(rotated.isIdentity());
  }

  @Test
  void applyRotationAboutArbitraryAxis_zAxis() {
    OrthogonalMatrix3x3 rotated = IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_Z_AXIS, new AngleInRadians(Math.PI / 2));
    assertFalse(rotated.isIdentity());
  }

  @Test
  void applyRotation_fullCircle_returnsIdentity() {
    OrthogonalMatrix3x3 rotated = IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_X_AXIS, new AngleInRadians(2 * Math.PI));
    assertTrue(rotated.isIdentity());
  }

  @Test
  void plus_addsComponents() {
    OrthogonalMatrix3x3 result = IDENTITY.plus(IDENTITY);
    assertEquals(2.0, result.right().x(), EPSILON);
    assertEquals(2.0, result.up().y(), EPSILON);
    assertEquals(2.0, result.backward().z(), EPSILON);
  }

  @Test
  void times_double() {
    OrthogonalMatrix3x3 result = IDENTITY.times(3.0);
    assertEquals(3.0, result.right().x(), EPSILON);
    assertEquals(3.0, result.up().y(), EPSILON);
    assertEquals(3.0, result.backward().z(), EPSILON);
  }

  @Test
  void asMatrix3x3_returnsSelf() {
    assertSame(IDENTITY, IDENTITY.asMatrix3x3());
  }

  @Test
  void asUnitQuaternion_identity() {
    UnitQuaternion q = IDENTITY.asUnitQuaternion();
    assertTrue(q.isAlignedWith(UnitQuaternion.IDENTITY));
  }

  @Test
  void asAxisRotation_identity() {
    AxisRotation ar = IDENTITY.asAxisRotation();
    assertEquals(AxisRotation.IDENTITY, ar);
  }

  @Test
  void asAxisRotation_nan() {
    AxisRotation ar = OrthogonalMatrix3x3.NaN.asAxisRotation();
    assertEquals(AxisRotation.NaN, ar);
  }

  @Test
  void asAxisRotation_180degrees() {
    // 180° about Y axis: right → -right, backward → -backward
    OrthogonalMatrix3x3 rot180 = new OrthogonalMatrix3x3(
        Vector3.NEGATIVE_X_AXIS, Vector3.POSITIVE_Y_AXIS, Vector3.NEGATIVE_Z_AXIS);
    AxisRotation ar = rot180.asAxisRotation();
    assertNotNull(ar);
    assertEquals(Math.PI, ar.angle().getAsRadians(), 0.01);
  }

  @Test
  void asEulerAngles_identity() {
    EulerAngles ea = IDENTITY.asEulerAngles();
    assertEquals(0.0, ea.pitch().getAsRadians(), 0.01);
    assertEquals(0.0, ea.yaw().getAsRadians(), 0.01);
    assertEquals(0.0, ea.roll().getAsRadians(), 0.01);
  }

  @Test
  void asForwardAndUpGuide() {
    ForwardAndUpGuide fug = IDENTITY.asForwardAndUpGuide();
    assertEquals(-1.0, fug.forward().z(), EPSILON);
    assertEquals(1.0, fug.upGuide().y(), EPSILON);
  }

  @Test
  void normalized_alreadyNormal() {
    assertSame(IDENTITY, IDENTITY.normalized());
  }

  @Test
  void normalized_scaledVectors() {
    OrthogonalMatrix3x3 scaled = IDENTITY.times(2.0);
    OrthogonalMatrix3x3 n = scaled.normalized();
    assertTrue(n.isNormalized());
  }

  @Test
  void asStandUp_alreadyUpright() {
    OrthogonalMatrix3x3 result = IDENTITY.asStandUp();
    assertSame(IDENTITY, result);
  }

  @Test
  void asStandUp_tilted() {
    OrthogonalMatrix3x3 tilted = IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_X_AXIS, new AngleInRadians(0.5));
    OrthogonalMatrix3x3 stood = tilted.asStandUp();
    assertEquals(1.0, stood.getUp().y(), 0.01);
  }

  @Test
  void asStandUp_backwardAlongY() {
    // backward is along Y axis (looking straight up/down)
    OrthogonalMatrix3x3 m = new OrthogonalMatrix3x3(
        Vector3.POSITIVE_X_AXIS,
        Vector3.POSITIVE_Z_AXIS,
        Vector3.NEGATIVE_Y_AXIS);
    OrthogonalMatrix3x3 stood = m.asStandUp();
    assertNotNull(stood);
  }

  // --- Matrix3x3 interface default methods ---

  @Test
  void getForward_negatesBackward() {
    Vector3 fwd = IDENTITY.getForward();
    assertEquals(0.0, fwd.x(), EPSILON);
    assertEquals(0.0, fwd.y(), EPSILON);
    assertEquals(-1.0, fwd.z(), EPSILON);
  }

  @Test
  void matrix3x3Create_fromArray() {
    double[] vals = {1, 0, 0, 0, 1, 0, 0, 0, 1};
    Matrix3x3 m = Matrix3x3.create(vals);
    assertTrue(m.isIdentity());
  }

  @Test
  void matrix3x3Create_fromValues() {
    Matrix3x3 m = Matrix3x3.create(1, 0, 0, 0, 1, 0, 0, 0, 1);
    assertTrue(m.isIdentity());
  }

  @Test
  void isZero() {
    assertTrue(Matrix3x3.ZERO.isZero());
    assertFalse(IDENTITY.isZero());
  }

  @Test
  void isNormalized() {
    assertTrue(IDENTITY.isNormalized());
  }

  @Test
  void determinant_identity() {
    assertEquals(1.0, IDENTITY.determinant(), EPSILON);
  }

  @Test
  void invert_identity() {
    Matrix3x3 inv = IDENTITY.invert();
    assertTrue(inv.isIdentity());
  }

  @Test
  void invert_roundTrip() {
    Matrix3x3 m = Matrix3x3.create(2, 1, 0, 0, 3, 1, 1, 0, 2);
    Matrix3x3 inv = m.invert();
    Matrix3x3 product = m.times(inv);
    assertTrue(product.isWithinReasonableEpsilonOf(Matrix3x3.IDENTITY));
  }

  @Test
  void scale_identity_returnsSame() {
    assertSame(IDENTITY, IDENTITY.scale(1.0));
  }

  @Test
  void scale_doubles() {
    Matrix3x3 result = IDENTITY.scale(2.0);
    assertEquals(2.0, result.getRight().x(), EPSILON);
    assertEquals(2.0, result.getUp().y(), EPSILON);
  }

  @Test
  void times_matrix_identity() {
    Matrix3x3 m = Matrix3x3.create(2, 1, 0, 0, 3, 1, 1, 0, 2);
    Matrix3x3 result = m.times(IDENTITY);
    assertTrue(m.isWithinReasonableEpsilonOf(result));
  }

  @Test
  void transform_vector() {
    Vector3 v = new Vector3(1, 0, 0);
    Vector3 result = IDENTITY.transform(v);
    assertEquals(1.0, result.x(), EPSILON);
    assertEquals(0.0, result.y(), EPSILON);
    assertEquals(0.0, result.z(), EPSILON);
  }

  @Test
  void transform_point() {
    Point3 p = new Point3(1, 2, 3);
    Point3 result = IDENTITY.transform(p);
    assertEquals(1.0, result.x(), EPSILON);
    assertEquals(2.0, result.y(), EPSILON);
    assertEquals(3.0, result.z(), EPSILON);
  }

  @Test
  void transformVector_doubleArrays() {
    double[] src = {1, 0, 0};
    double[] dest = new double[3];
    IDENTITY.transformVector(dest, 0, src, 0);
    assertEquals(1.0, dest[0], EPSILON);
    assertEquals(0.0, dest[1], EPSILON);
    assertEquals(0.0, dest[2], EPSILON);
  }

  @Test
  void transformVector_floatSrcDoublesDest() {
    float[] src = {1, 0, 0};
    double[] dest = new double[3];
    IDENTITY.transformVector(dest, 0, src, 0);
    assertEquals(1.0, dest[0], EPSILON);
  }

  @Test
  void transformVector_floatArrays() {
    float[] src = {1, 2, 3};
    float[] dest = new float[3];
    IDENTITY.transformVector(dest, 0, src, 0);
    assertEquals(1.0f, dest[0], 1e-6f);
    assertEquals(2.0f, dest[1], 1e-6f);
    assertEquals(3.0f, dest[2], 1e-6f);
  }

  @Test
  void writeColumnMajorArray16() {
    double[] dest = new double[16];
    IDENTITY.writeColumnMajorArray16(dest);
    assertEquals(1.0, dest[0], EPSILON);
    assertEquals(0.0, dest[1], EPSILON);
    assertEquals(0.0, dest[3], EPSILON);
    assertEquals(1.0, dest[15], EPSILON);
  }

  @Test
  void isWithinReasonableEpsilonOf() {
    assertTrue(IDENTITY.isWithinReasonableEpsilonOf(IDENTITY));
    Matrix3x3 m = Matrix3x3.create(2, 0, 0, 0, 1, 0, 0, 0, 1);
    assertFalse(IDENTITY.isWithinReasonableEpsilonOf(m));
  }

  @Test
  void isWithinEpsilonOf() {
    assertTrue(IDENTITY.isWithinEpsilonOf(IDENTITY, 0.001));
  }
}
