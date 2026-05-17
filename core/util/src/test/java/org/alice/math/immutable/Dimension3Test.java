package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Dimension3Test {

  static final double EPSILON = 1e-10;
  static final Dimension3 A = new Dimension3(2, 3, 4);
  static final Dimension3 B = new Dimension3(5, 6, 7);

  // --- Static Constants ---

  @Test
  void unitSize_allOnes() {
    assertEquals(1.0, Dimension3.UNIT_SIZE.x());
    assertEquals(1.0, Dimension3.UNIT_SIZE.y());
    assertEquals(1.0, Dimension3.UNIT_SIZE.z());
  }

  @Test
  void nan_allComponentsNaN() {
    assertTrue(Double.isNaN(Dimension3.NaN.x()));
    assertTrue(Double.isNaN(Dimension3.NaN.y()));
    assertTrue(Double.isNaN(Dimension3.NaN.z()));
  }

  // --- isNaN ---

  @Test
  void isNaN_trueForNaNConstant() {
    assertTrue(Dimension3.NaN.isNaN());
  }

  @Test
  void isNaN_falseForNormalDimension() {
    assertFalse(A.isNaN());
  }

  @Test
  void isNaN_trueWhenOneComponentNaN() {
    assertTrue(new Dimension3(Double.NaN, 1, 2).isNaN());
    assertTrue(new Dimension3(1, Double.NaN, 2).isNaN());
    assertTrue(new Dimension3(1, 2, Double.NaN).isNaN());
  }

  // --- uniformScale ---

  @Test
  void uniformScale_allComponentsEqual() {
    Dimension3 d = Dimension3.uniformScale(5.0);
    assertEquals(5.0, d.x(), EPSILON);
    assertEquals(5.0, d.y(), EPSILON);
    assertEquals(5.0, d.z(), EPSILON);
  }

  // --- times (scalar) ---

  @Test
  void times_scalar_multipliesAllComponents() {
    Dimension3 r = A.times(3.0);
    assertEquals(6.0, r.x(), EPSILON);
    assertEquals(9.0, r.y(), EPSILON);
    assertEquals(12.0, r.z(), EPSILON);
  }

  // --- times (Dimension3) ---

  @Test
  void times_dimension_multipliesComponents() {
    Dimension3 r = A.times(B);
    assertEquals(10.0, r.x(), EPSILON);
    assertEquals(18.0, r.y(), EPSILON);
    assertEquals(28.0, r.z(), EPSILON);
  }

  @Test
  void times_unitSizeIsIdentity() {
    Dimension3 r = A.times(Dimension3.UNIT_SIZE);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  // --- dividedBy ---

  @Test
  void dividedBy_dividesComponents() {
    Dimension3 r = new Dimension3(10, 20, 30).dividedBy(new Dimension3(2, 4, 5));
    assertEquals(5.0, r.x(), EPSILON);
    assertEquals(5.0, r.y(), EPSILON);
    assertEquals(6.0, r.z(), EPSILON);
  }

  // --- interpolate ---

  @Test
  void interpolate_atZeroReturnsStart() {
    Dimension3 r = A.interpolate(B, 0.0);
    assertEquals(A.x(), r.x(), EPSILON);
    assertEquals(A.y(), r.y(), EPSILON);
    assertEquals(A.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atOneReturnsEnd() {
    Dimension3 r = A.interpolate(B, 1.0);
    assertEquals(B.x(), r.x(), EPSILON);
    assertEquals(B.y(), r.y(), EPSILON);
    assertEquals(B.z(), r.z(), EPSILON);
  }

  @Test
  void interpolate_atHalfReturnsMidpoint() {
    Dimension3 r = A.interpolate(B, 0.5);
    assertEquals(3.5, r.x(), EPSILON);
    assertEquals(4.5, r.y(), EPSILON);
    assertEquals(5.5, r.z(), EPSILON);
  }

  // --- asScaleMatrix ---

  @Test
  void asScaleMatrix_diagonalMatchesComponents() {
    OrthogonalMatrix3x3 m = A.asScaleMatrix();
    assertEquals(A.x(), m.right().x(), EPSILON);
    assertEquals(0.0, m.right().y(), EPSILON);
    assertEquals(0.0, m.right().z(), EPSILON);
    assertEquals(0.0, m.up().x(), EPSILON);
    assertEquals(A.y(), m.up().y(), EPSILON);
    assertEquals(0.0, m.up().z(), EPSILON);
    assertEquals(0.0, m.backward().x(), EPSILON);
    assertEquals(0.0, m.backward().y(), EPSILON);
    assertEquals(A.z(), m.backward().z(), EPSILON);
  }

  // --- withSafeNumbers ---

  @Test
  void withSafeNumbers_safeInputReturnsSelf() {
    assertSame(A, A.withSafeNumbers());
  }

  @Test
  void withSafeNumbers_replacesNaNWithOne() {
    Dimension3 r = new Dimension3(Double.NaN, 2, 3).withSafeNumbers();
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  @Test
  void withSafeNumbers_replacesInfinityWithOne() {
    Dimension3 r = new Dimension3(1, Double.POSITIVE_INFINITY, 3).withSafeNumbers();
    assertEquals(1.0, r.x(), EPSILON);
    assertEquals(1.0, r.y(), EPSILON);
    assertEquals(3.0, r.z(), EPSILON);
  }

  // --- hasNegativeComponents ---

  @Test
  void hasNegativeComponents_falseForPositive() {
    assertFalse(A.hasNegativeComponents());
  }

  @Test
  void hasNegativeComponents_trueWhenNegative() {
    assertTrue(new Dimension3(-1, 2, 3).hasNegativeComponents());
    assertTrue(new Dimension3(1, -2, 3).hasNegativeComponents());
    assertTrue(new Dimension3(1, 2, -3).hasNegativeComponents());
  }

  // --- applyScale / removeScale ---

  @Test
  void applyScale_scalesPointComponents() {
    Point3 p = new Point3(1, 2, 3);
    Point3 r = A.applyScale(p);
    assertEquals(2.0, r.x(), EPSILON);
    assertEquals(6.0, r.y(), EPSILON);
    assertEquals(12.0, r.z(), EPSILON);
  }

  @Test
  void removeScale_invertsApplyScale() {
    Point3 p = new Point3(4, 6, 8);
    Point3 r = A.removeScale(p);
    assertEquals(2.0, r.x(), EPSILON);
    assertEquals(2.0, r.y(), EPSILON);
    assertEquals(2.0, r.z(), EPSILON);
  }

  // --- isZero ---

  @Test
  void isZero_trueForZeroDimension() {
    assertTrue(new Dimension3(0, 0, 0).isZero());
  }

  @Test
  void isZero_falseForNonZero() {
    assertFalse(A.isZero());
  }
}
