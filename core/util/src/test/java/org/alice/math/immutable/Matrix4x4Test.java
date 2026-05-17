package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Matrix4x4Test {

  static final Matrix4x4 M1 = Matrix4x4.create(
      1, 2, 3, 7,
      6, 5, 4, 2,
      0, 8, 9, 5,
      0, 5, 8, 1
  );
  static final double M1_DET = -781.0;

  static final AffineMatrix4x4 A1 = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(4, 6, 2));

  static final Matrix4x4 LOCAL_IDENTITY = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.ORIGIN);

  @Test
  void createShouldMakeMatrix() {
    Matrix4x4 m = new FullMatrix4x4(Vector4.UNIT_X, Vector4.UNIT_Y, Vector4.UNIT_Z, Vector4.UNIT_W);
    assertNotNull(m, "Matrix should not be null");
  }

  @Test
  void createIdentityShouldEqualIdentityMatrix() {
    assertEquals(Matrix4x4.IDENTITY, LOCAL_IDENTITY, "Matrix should be identity");
  }

  @Test
  void createIdentityShouldReplyTrueOnIsIdentity() {
    assertTrue(LOCAL_IDENTITY.isIdentity(), "Matrix should be identity");
  }

  @Test
  void createIdentityShouldReplyFalseOnIsNaN() {
    assertFalse(LOCAL_IDENTITY.isNaN(), "Matrix should not be NaN");
  }

  @Test
  void isNaNShouldRecognizeNaN() {
    assertTrue(Matrix4x4.NaN.isNaN(), "Matrix should be NaN");
  }

  @Test
  void isNaNShouldRejectValidMatrix() {
    assertFalse(M1.isNaN(), "Matrix should not be NaN");
  }

  @Test
  void isIdentityShouldRecognizeIdentity() {
    assertTrue(Matrix4x4.IDENTITY.isIdentity(), "Matrix should be identity");
  }

  @Test
  void isIdentityShouldRejectNotIdentity() {
    assertFalse(M1.isIdentity(), "Matrix should not be identity");
  }

  @Test
  void matrixHasDeterminant() {
    double d = M1.determinant();
    assertEquals(M1_DET, d);
  }

  @Test
  void invertShouldExist() {
    Matrix4x4 inverted = M1.invert();
    assertNotNull(inverted);
  }

  @Test
  void invertShouldHaveValues() {
    Matrix4x4 inverted = M1.invert();
    assertFalse(inverted.isNaN(), "Matrix should not be NaN");
  }

  @Test
  void doubleInvertShouldBeIdempotent() {
    Matrix4x4 inverted = M1.invert();
    Matrix4x4 twiceInverted = inverted.invert();
    assertTrue(M1.isWithinReasonableEpsilonOf(twiceInverted), "Matrix should be within reasonable epsilon");
  }

  @Test
  void multiplyingIdentityShouldReturnIdentityMatrix() {
    Matrix4x4 i1 = Matrix4x4.IDENTITY;
    Matrix4x4 i2 = Matrix4x4.IDENTITY;
    Matrix4x4 product = i1.times(i2);
    assertTrue(product.isIdentity(), "Matrix should be identity");
  }

  // --- Additional tests for default methods ---

  @Test
  void transform_point3_nonIdentity() {
    Point3 p = new Point3(1, 0, 0);
    Point3 result = A1.transform(p);
    assertEquals(5.0, result.x(), 1e-10);
    assertEquals(6.0, result.y(), 1e-10);
    assertEquals(2.0, result.z(), 1e-10);
  }

  @Test
  void transform_vector3_identity() {
    Vector3 v = new Vector3(1, 2, 3);
    Vector3 result = Matrix4x4.IDENTITY.transform(v);
    assertSame(v, result);
  }

  @Test
  void transform_vector3_nonIdentity() {
    Vector3 v = new Vector3(1, 0, 0);
    Vector3 result = A1.transform(v);
    assertEquals(1.0, result.x(), 1e-10);
    assertEquals(0.0, result.y(), 1e-10);
    assertEquals(0.0, result.z(), 1e-10);
  }

  @Test
  void transform_vector3f_identity() {
    Vector3f v = new Vector3f(1, 2, 3);
    Vector3f result = Matrix4x4.IDENTITY.transform(v);
    assertSame(v, result);
  }

  @Test
  void transform_vector3f_nonIdentity() {
    Vector3f v = new Vector3f(1, 0, 0);
    Vector3f result = A1.transform(v);
    assertEquals(1.0f, result.x(), 1e-5f);
  }

  @Test
  void transform_vector4_identity() {
    Vector4 v = new Vector4(1, 2, 3, 1);
    Vector4 result = Matrix4x4.IDENTITY.transform(v);
    assertEquals(v, result);
  }

  @Test
  void transform_vector4_nonIdentity() {
    Vector4 v = new Vector4(1, 0, 0, 1);
    Vector4 result = M1.transform(v);
    assertNotNull(result);
  }

  @Test
  void transform_ray_identity() {
    Ray r = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    Ray result = Matrix4x4.IDENTITY.transform(r);
    assertSame(r, result);
  }

  @Test
  void transform_ray_translated() {
    Ray r = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    Ray result = A1.transform(r);
    assertEquals(4.0, result.origin().x(), 1e-10);
    assertEquals(6.0, result.origin().y(), 1e-10);
    assertEquals(2.0, result.origin().z(), 1e-10);
  }

  @Test
  void asColumnMajorArray16_identity() {
    double[] arr = Matrix4x4.IDENTITY.asColumnMajorArray16();
    assertEquals(16, arr.length);
    assertEquals(1.0, arr[0], 1e-10);
    assertEquals(0.0, arr[1], 1e-10);
    assertEquals(1.0, arr[5], 1e-10);
    assertEquals(1.0, arr[10], 1e-10);
    assertEquals(1.0, arr[15], 1e-10);
  }

  @Test
  void writeColumnMajorArray16_double() {
    double[] dest = new double[16];
    M1.writeColumnMajorArray16(dest);
    assertEquals(M1.e11(), dest[0], 1e-10);
    assertEquals(M1.e21(), dest[1], 1e-10);
    assertEquals(M1.e31(), dest[2], 1e-10);
    assertEquals(M1.e41(), dest[3], 1e-10);
  }

  @Test
  void writeColumnMajorArray16_float() {
    float[] dest = new float[16];
    M1.writeColumnMajorArray16(dest);
    assertEquals((float) M1.e11(), dest[0], 1e-5f);
    assertEquals((float) M1.e21(), dest[1], 1e-5f);
  }

  @Test
  void asRowMajorArray16() {
    double[] arr = M1.asRowMajorArray16();
    assertEquals(16, arr.length);
    assertEquals(M1.e11(), arr[0], 1e-10);
    assertEquals(M1.e12(), arr[1], 1e-10);
    assertEquals(M1.e13(), arr[2], 1e-10);
    assertEquals(M1.e14(), arr[3], 1e-10);
  }

  @Test
  void isWithinReasonableEpsilonOf() {
    assertTrue(M1.isWithinReasonableEpsilonOf(M1));
    assertFalse(M1.isWithinReasonableEpsilonOf(Matrix4x4.IDENTITY));
  }

  @Test
  void fromTranslation_identity() {
    Matrix4x4 m = Matrix4x4.fromTranslation(Point3.ORIGIN);
    assertTrue(m.isIdentity());
  }

  @Test
  void fromScale() {
    Matrix4x4 m = Matrix4x4.fromScale(2, 3, 4);
    assertEquals(2.0, m.e11(), 1e-10);
    assertEquals(3.0, m.e22(), 1e-10);
    assertEquals(4.0, m.e33(), 1e-10);
  }

  @Test
  void times_identityLeft_returnsSame() {
    Matrix4x4 product = Matrix4x4.IDENTITY.times(M1);
    assertTrue(M1.isWithinReasonableEpsilonOf(product));
  }

  @Test
  void times_identityRight_returnsSame() {
    Matrix4x4 product = M1.times(Matrix4x4.IDENTITY);
    assertTrue(M1.isWithinReasonableEpsilonOf(product));
  }

  @Test
  void invert_zeroDetReturnsIdentity() {
    // A matrix with zero determinant should return identity
    Matrix4x4 singular = Matrix4x4.create(
        1, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 0,
        0, 0, 0, 1);
    Matrix4x4 inv = singular.invert();
    assertTrue(inv.isIdentity());
  }
}