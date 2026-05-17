package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AffineMatrix4x4Test {

  static final AffineMatrix4x4 IDENTITY_AFFINE = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, Point3.ORIGIN);
  static final AffineMatrix4x4 TRANSLATED = AffineMatrix4x4.createTranslation(3, 5, 7);
  static final double EPSILON = 1e-10;

  // --- Factory methods ---

  @Test
  void createTranslation_producesCorrectTranslation() {
    AffineMatrix4x4 m = AffineMatrix4x4.createTranslation(1, 2, 3);
    assertEquals(new Point3(1, 2, 3), m.translation());
    assertTrue(m.orientation().isIdentity());
  }

  @Test
  void createOrientation_producesCorrectOrientation() {
    AxisRotation rot = AxisRotation.createXAxisRotation(new AngleInRadians(Math.PI / 4));
    AffineMatrix4x4 m = AffineMatrix4x4.createOrientation(rot);
    assertEquals(Point3.ORIGIN, m.translation());
    assertTrue(m.orientation().isAlignedWith(rot));
  }

  @Test
  void createWithDiagonal_producesScaleMatrix() {
    Dimension3 diag = new Dimension3(2, 3, 4);
    AffineMatrix4x4 m = AffineMatrix4x4.createWithDiagonal(diag);
    assertEquals(2.0, m.e11(), EPSILON);
    assertEquals(3.0, m.e22(), EPSILON);
    assertEquals(4.0, m.e33(), EPSILON);
    assertEquals(Point3.ORIGIN, m.translation());
  }

  @Test
  void createFromColumnMajorArray12_roundTrips() {
    double[] arr = {1, 0, 0, 0, 1, 0, 0, 0, 1, 4, 5, 6};
    AffineMatrix4x4 m = AffineMatrix4x4.createFromColumnMajorArray12(arr);
    assertEquals(4.0, m.translation().x(), EPSILON);
    assertEquals(5.0, m.translation().y(), EPSILON);
    assertEquals(6.0, m.translation().z(), EPSILON);
    double[] back = m.asColumnMajorArray12();
    assertArrayEquals(arr, back, EPSILON);
  }

  @Test
  void createFromRowMajorArray_12elements() {
    double[] arr = {1, 0, 0, 10, 0, 1, 0, 20, 0, 0, 1, 30};
    AffineMatrix4x4 m = AffineMatrix4x4.createFromRowMajorArray(arr);
    assertEquals(10.0, m.translation().x(), EPSILON);
    assertEquals(20.0, m.translation().y(), EPSILON);
    assertEquals(30.0, m.translation().z(), EPSILON);
  }

  @Test
  void createFromRowMajorArray_16elements() {
    double[] arr = {1, 0, 0, 10, 0, 1, 0, 20, 0, 0, 1, 30, 0, 0, 0, 1};
    AffineMatrix4x4 m = AffineMatrix4x4.createFromRowMajorArray(arr);
    assertEquals(10.0, m.translation().x(), EPSILON);
  }

  // --- Condition checks ---

  @Test
  void isAffine_alwaysTrue() {
    assertTrue(IDENTITY_AFFINE.isAffine());
    assertTrue(TRANSLATED.isAffine());
  }

  @Test
  void isNaN_falseForValid() {
    assertFalse(TRANSLATED.isNaN());
  }

  @Test
  void isNaN_trueForNaN() {
    assertTrue(Matrix4x4.NaN.isNaN());
  }

  @Test
  void isIdentity_trueForIdentity() {
    assertTrue(IDENTITY_AFFINE.isIdentity());
  }

  @Test
  void isIdentity_falseForTranslated() {
    assertFalse(TRANSLATED.isIdentity());
  }

  @Test
  void isWithinEpsilonOf_sameMatrix() {
    assertTrue(TRANSLATED.isWithinEpsilonOf(TRANSLATED, EPSILON));
  }

  @Test
  void isWithinEpsilonOf_slightlyDifferent() {
    AffineMatrix4x4 other = AffineMatrix4x4.createTranslation(3 + 1e-15, 5, 7);
    assertTrue(TRANSLATED.isWithinReasonableEpsilonOf(other));
  }

  // --- Element accessors ---

  @Test
  void elementAccessors_identity() {
    assertEquals(1.0, IDENTITY_AFFINE.e11());
    assertEquals(0.0, IDENTITY_AFFINE.e12());
    assertEquals(0.0, IDENTITY_AFFINE.e13());
    assertEquals(0.0, IDENTITY_AFFINE.e14());
    assertEquals(0.0, IDENTITY_AFFINE.e21());
    assertEquals(1.0, IDENTITY_AFFINE.e22());
    assertEquals(0.0, IDENTITY_AFFINE.e23());
    assertEquals(0.0, IDENTITY_AFFINE.e24());
    assertEquals(0.0, IDENTITY_AFFINE.e31());
    assertEquals(0.0, IDENTITY_AFFINE.e32());
    assertEquals(1.0, IDENTITY_AFFINE.e33());
    assertEquals(0.0, IDENTITY_AFFINE.e34());
    assertEquals(0.0, IDENTITY_AFFINE.e41());
    assertEquals(0.0, IDENTITY_AFFINE.e42());
    assertEquals(0.0, IDENTITY_AFFINE.e43());
    assertEquals(1.0, IDENTITY_AFFINE.e44());
  }

  @Test
  void elementAccessors_translated() {
    assertEquals(3.0, TRANSLATED.e14());
    assertEquals(5.0, TRANSLATED.e24());
    assertEquals(7.0, TRANSLATED.e34());
  }

  // --- Row/Column accessors ---

  @Test
  void rowsAndColumns_identity() {
    assertEquals(Vector4.UNIT_X, IDENTITY_AFFINE.rowX());
    assertEquals(Vector4.UNIT_Y, IDENTITY_AFFINE.rowY());
    assertEquals(Vector4.UNIT_Z, IDENTITY_AFFINE.rowZ());
    assertEquals(Vector4.UNIT_W, IDENTITY_AFFINE.rowW());
  }

  @Test
  void columns_translated() {
    Vector4 colTrans = TRANSLATED.columnTranslation();
    assertEquals(3.0, colTrans.x(), EPSILON);
    assertEquals(5.0, colTrans.y(), EPSILON);
    assertEquals(7.0, colTrans.z(), EPSILON);
    assertEquals(1.0, colTrans.w(), EPSILON);
  }

  @Test
  void columnRight_identity() {
    assertEquals(new Vector4(1, 0, 0, 0), IDENTITY_AFFINE.columnRight());
  }

  @Test
  void columnUp_identity() {
    assertEquals(new Vector4(0, 1, 0, 0), IDENTITY_AFFINE.columnUp());
  }

  @Test
  void columnBackward_identity() {
    assertEquals(new Vector4(0, 0, 1, 0), IDENTITY_AFFINE.columnBackward());
  }

  // --- Arithmetic operations ---

  @Test
  void invert_identity() {
    AffineMatrix4x4 inv = IDENTITY_AFFINE.invert();
    assertTrue(inv.isIdentity());
  }

  @Test
  void invert_translation() {
    AffineMatrix4x4 inv = TRANSLATED.invert();
    assertEquals(-3.0, inv.translation().x(), EPSILON);
    assertEquals(-5.0, inv.translation().y(), EPSILON);
    assertEquals(-7.0, inv.translation().z(), EPSILON);
  }

  @Test
  void invert_roundTrip() {
    AffineMatrix4x4 rotated = AffineMatrix4x4.createOrientation(
        AxisRotation.createYAxisRotation(new AngleInRadians(Math.PI / 6)));
    AffineMatrix4x4 m = rotated.times(TRANSLATED);
    AffineMatrix4x4 inv = m.invert();
    AffineMatrix4x4 product = m.times(inv);
    assertTrue(product.isWithinReasonableEpsilonOf(Matrix4x4.IDENTITY));
  }

  @Test
  void times_affine() {
    AffineMatrix4x4 a = AffineMatrix4x4.createTranslation(1, 0, 0);
    AffineMatrix4x4 b = AffineMatrix4x4.createTranslation(0, 2, 0);
    AffineMatrix4x4 product = a.times(b);
    assertEquals(1.0, product.translation().x(), EPSILON);
    assertEquals(2.0, product.translation().y(), EPSILON);
    assertEquals(0.0, product.translation().z(), EPSILON);
  }

  @Test
  void times_fullMatrix() {
    FullMatrix4x4 full = new FullMatrix4x4(Vector4.UNIT_X, Vector4.UNIT_Y, Vector4.UNIT_Z, Vector4.UNIT_W);
    Matrix4x4 product = IDENTITY_AFFINE.times(full);
    assertTrue(product.isIdentity());
  }

  @Test
  void times_scale1_returnsSame() {
    assertSame(TRANSLATED, TRANSLATED.times(1.0));
  }

  @Test
  void times_scale2_doublesValues() {
    AffineMatrix4x4 scaled = TRANSLATED.times(2.0);
    assertEquals(6.0, scaled.translation().x(), EPSILON);
    assertEquals(10.0, scaled.translation().y(), EPSILON);
    assertEquals(14.0, scaled.translation().z(), EPSILON);
  }

  @Test
  void plusPreservingAffine_addsTranslations() {
    AffineMatrix4x4 a = AffineMatrix4x4.createTranslation(1, 2, 3);
    AffineMatrix4x4 b = AffineMatrix4x4.createTranslation(4, 5, 6);
    AffineMatrix4x4 result = a.plusPreservingAffine(b);
    assertEquals(5.0, result.translation().x(), EPSILON);
    assertEquals(7.0, result.translation().y(), EPSILON);
    assertEquals(9.0, result.translation().z(), EPSILON);
  }

  @Test
  void plusPreservingAffine_nanFallsBackToOther() {
    AffineMatrix4x4 result = Matrix4x4.NaN.plusPreservingAffine(TRANSLATED);
    assertEquals(TRANSLATED, result);
  }

  @Test
  void scaleTranslation_double_scale1_returnsSame() {
    assertSame(TRANSLATED, TRANSLATED.scaleTranslation(1.0));
  }

  @Test
  void scaleTranslation_double_scales() {
    AffineMatrix4x4 result = TRANSLATED.scaleTranslation(0.5);
    assertEquals(1.5, result.translation().x(), EPSILON);
    assertEquals(2.5, result.translation().y(), EPSILON);
    assertEquals(3.5, result.translation().z(), EPSILON);
  }

  @Test
  void scaleTranslation_matrix_identity_returnsSame() {
    Matrix4x4 result = TRANSLATED.scaleTranslation(Matrix3x3.IDENTITY);
    assertSame(TRANSLATED, result);
  }

  @Test
  void scaleTranslation_matrix_scales() {
    Matrix3x3 scale = Matrix3x3.create(2, 0, 0, 0, 3, 0, 0, 0, 4);
    Matrix4x4 result = TRANSLATED.scaleTranslation(scale);
    if (result instanceof AffineMatrix4x4 affine) {
      assertEquals(6.0, affine.translation().x(), EPSILON);
      assertEquals(15.0, affine.translation().y(), EPSILON);
      assertEquals(28.0, affine.translation().z(), EPSILON);
    } else {
      fail("Expected AffineMatrix4x4");
    }
  }

  // --- Transform operations ---

  @Test
  void transformPoint3_translatesPoint() {
    double[] src = {1.0, 2.0, 3.0};
    double[] dest = new double[3];
    TRANSLATED.transformPoint3(dest, 0, src, 0);
    assertEquals(4.0, dest[0], EPSILON);
    assertEquals(7.0, dest[1], EPSILON);
    assertEquals(10.0, dest[2], EPSILON);
  }

  @Test
  void transformPoint3_nullDest_noOp() {
    double[] src = {1.0, 2.0, 3.0};
    TRANSLATED.transformPoint3(null, 0, src, 0);
    // Should not throw
  }

  @Test
  void transformVector3_identityIsPassThrough() {
    float[] src = {1.0f, 2.0f, 3.0f};
    float[] dest = new float[3];
    IDENTITY_AFFINE.transformVector3(dest, 0, src, 0);
    assertEquals(1.0f, dest[0], 1e-6f);
    assertEquals(2.0f, dest[1], 1e-6f);
    assertEquals(3.0f, dest[2], 1e-6f);
  }

  @Test
  void transformVector3_nullDest_noOp() {
    float[] src = {1.0f, 2.0f, 3.0f};
    TRANSLATED.transformVector3(null, 0, src, 0);
  }

  @Test
  void transform_vector4() {
    Vector4 v = new Vector4(1, 0, 0, 1);
    Vector4 result = TRANSLATED.transform(v);
    assertEquals(4.0, result.x(), EPSILON);
    assertEquals(5.0, result.y(), EPSILON);
    assertEquals(7.0, result.z(), EPSILON);
    assertEquals(1.0, result.w(), EPSILON);
  }

  // --- Array round-trips ---

  @Test
  void asColumnMajorArray12_roundTrip() {
    AffineMatrix4x4 m = AffineMatrix4x4.createTranslation(10, 20, 30);
    double[] arr = m.asColumnMajorArray12();
    assertEquals(12, arr.length);
    AffineMatrix4x4 back = AffineMatrix4x4.createFromColumnMajorArray12(arr);
    assertTrue(m.isWithinReasonableEpsilonOf(back));
  }

  @Test
  void asColumnMajorArray16_roundTrip() {
    double[] arr = TRANSLATED.asColumnMajorArray16();
    assertEquals(16, arr.length);
    assertEquals(3.0, arr[12], EPSILON); // translation x
    assertEquals(5.0, arr[13], EPSILON); // translation y
    assertEquals(7.0, arr[14], EPSILON); // translation z
    assertEquals(1.0, arr[15], EPSILON); // bottom-right
  }

  @Test
  void asRowMajorArray16_roundTrip() {
    double[] arr = TRANSLATED.asRowMajorArray16();
    assertEquals(16, arr.length);
    assertEquals(3.0, arr[3], EPSILON);  // e14
    assertEquals(5.0, arr[7], EPSILON);  // e24
    assertEquals(7.0, arr[11], EPSILON); // e34
  }

  @Test
  void writeColumnMajorArray16_doubleArray() {
    double[] dest = new double[16];
    TRANSLATED.writeColumnMajorArray16(dest);
    assertEquals(1.0, dest[0], EPSILON);
    assertEquals(3.0, dest[12], EPSILON);
  }

  @Test
  void writeColumnMajorArray16_floatArray() {
    float[] dest = new float[16];
    TRANSLATED.writeColumnMajorArray16(dest);
    assertEquals(1.0f, dest[0], 1e-6f);
    assertEquals(3.0f, dest[12], 1e-6f);
  }

  // --- Normalize ---

  @Test
  void normalizeOnlyOrientation_alreadyNormalized() {
    assertSame(IDENTITY_AFFINE, IDENTITY_AFFINE.normalizeOnlyOrientation());
  }

  @Test
  void normalizeOrientation_alreadyNormalized() {
    assertSame(IDENTITY_AFFINE, IDENTITY_AFFINE.normalizeOrientation());
  }

  @Test
  void normalizeOrientation_scaledOrientation() {
    OrthogonalMatrix3x3 scaled = OrthogonalMatrix3x3.IDENTITY.times(2.0);
    AffineMatrix4x4 m = new AffineMatrix4x4(scaled, new Point3(10, 20, 30));
    AffineMatrix4x4 normalized = m.normalizeOrientation();
    assertTrue(normalized.orientation().isNormalized());
  }

  // --- With methods ---

  @Test
  void withTranslation_changesTranslation() {
    AffineMatrix4x4 result = TRANSLATED.withTranslation(new Point3(100, 200, 300));
    assertEquals(new Point3(100, 200, 300), result.translation());
    assertEquals(TRANSLATED.orientation(), result.orientation());
  }

  @Test
  void withOrientation_changesOrientation() {
    OrthogonalMatrix3x3 newOr = OrthogonalMatrix3x3.IDENTITY.applyRotationAboutArbitraryAxis(
        Vector3.POSITIVE_Y_AXIS, new AngleInRadians(1.0));
    AffineMatrix4x4 result = TRANSLATED.withOrientation(newOr);
    assertEquals(TRANSLATED.translation(), result.translation());
    assertTrue(newOr.isAlignedWith(result.orientation()));
  }

  // --- Rotation ---

  @Test
  void rotateAboutXAxis_fromIdentity() {
    AffineMatrix4x4 rotated = IDENTITY_AFFINE.rotateAboutXAxis(new AngleInRadians(Math.PI / 2));
    assertFalse(rotated.isIdentity());
    assertTrue(rotated.isAffine());
  }

  @Test
  void rotateAboutYAxis_fromIdentity() {
    AffineMatrix4x4 rotated = IDENTITY_AFFINE.rotateAboutYAxis(new AngleInRadians(Math.PI / 2));
    assertFalse(rotated.isIdentity());
    assertTrue(rotated.isAffine());
  }

  // --- Matrix4x4 interface default methods via AffineMatrix4x4 ---

  @Test
  void transform_point3_identity() {
    Point3 p = new Point3(1, 2, 3);
    Point3 result = IDENTITY_AFFINE.transform(p);
    assertSame(p, result);
  }

  @Test
  void transform_point3_translated() {
    Point3 p = new Point3(1, 2, 3);
    Point3 result = TRANSLATED.transform(p);
    assertEquals(4.0, result.x(), EPSILON);
    assertEquals(7.0, result.y(), EPSILON);
    assertEquals(10.0, result.z(), EPSILON);
  }

  @Test
  void transform_vector3_identity() {
    Vector3 v = new Vector3(1, 2, 3);
    Vector3 result = IDENTITY_AFFINE.transform(v);
    assertSame(v, result);
  }

  @Test
  void transform_vector3_translated() {
    // Translation should not affect direction vectors
    Vector3 v = new Vector3(1, 0, 0);
    Vector3 result = TRANSLATED.transform(v);
    assertEquals(1.0, result.x(), EPSILON);
    assertEquals(0.0, result.y(), EPSILON);
    assertEquals(0.0, result.z(), EPSILON);
  }

  @Test
  void transform_vector3f_identity() {
    Vector3f v = new Vector3f(1, 2, 3);
    Vector3f result = IDENTITY_AFFINE.transform(v);
    assertSame(v, result);
  }

  @Test
  void transform_ray() {
    Ray ray = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    Ray result = IDENTITY_AFFINE.transform(ray);
    assertSame(ray, result);
  }

  @Test
  void transform_ray_translated() {
    Ray ray = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    Ray result = TRANSLATED.transform(ray);
    assertEquals(3.0, result.origin().x(), EPSILON);
    assertEquals(5.0, result.origin().y(), EPSILON);
    assertEquals(7.0, result.origin().z(), EPSILON);
    assertEquals(1.0, result.direction().x(), EPSILON);
  }

  @Test
  void determinant_identity() {
    assertEquals(1.0, IDENTITY_AFFINE.determinant(), EPSILON);
  }

  @Test
  void determinant_translated() {
    assertEquals(1.0, TRANSLATED.determinant(), EPSILON);
  }

  @Test
  void times_matrix4x4_identityLeft() {
    Matrix4x4 product = IDENTITY_AFFINE.times((Matrix4x4) TRANSLATED);
    assertTrue(product.isWithinReasonableEpsilonOf(TRANSLATED));
  }

  @Test
  void times_matrix4x4_identityRight() {
    Matrix4x4 product = TRANSLATED.times((Matrix4x4) IDENTITY_AFFINE);
    assertTrue(product.isWithinReasonableEpsilonOf(TRANSLATED));
  }

  // --- Matrix4x4.create factory ---

  @Test
  void create_affineValues_producesAffine() {
    Matrix4x4 m = Matrix4x4.create(
        1, 0, 0, 5,
        0, 1, 0, 6,
        0, 0, 1, 7,
        0, 0, 0, 1);
    assertInstanceOf(AffineMatrix4x4.class, m);
    assertEquals(5.0, ((AffineMatrix4x4) m).translation().x(), EPSILON);
  }

  @Test
  void create_nonAffineValues_producesFullMatrix() {
    Matrix4x4 m = Matrix4x4.create(
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        1, 2, 3, 4);
    assertInstanceOf(FullMatrix4x4.class, m);
  }

  @Test
  void fromTranslation_producesAffine() {
    Matrix4x4 m = Matrix4x4.fromTranslation(new Point3(1, 2, 3));
    assertInstanceOf(AffineMatrix4x4.class, m);
    assertTrue(m.isAffine());
  }

  @Test
  void fromScale_producesAffine() {
    Matrix4x4 m = Matrix4x4.fromScale(2, 3, 4);
    assertInstanceOf(AffineMatrix4x4.class, m);
    assertEquals(2.0, m.e11(), EPSILON);
    assertEquals(3.0, m.e22(), EPSILON);
    assertEquals(4.0, m.e33(), EPSILON);
  }
}
