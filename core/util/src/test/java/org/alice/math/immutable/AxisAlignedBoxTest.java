package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AxisAlignedBoxTest {

  static final AxisAlignedBox BOX = AxisAlignedBox.createAxisAlignedBox(-1, -2, -3, 4, 5, 6);
  static final double EPSILON = 1e-10;

  @Test
  void createAxisAlignedBox_storesMinMax() {
    assertEquals(-1.0, BOX.getXMinimum(), EPSILON);
    assertEquals(-2.0, BOX.getYMinimum(), EPSILON);
    assertEquals(-3.0, BOX.getZMinimum(), EPSILON);
    assertEquals(4.0, BOX.getXMaximum(), EPSILON);
    assertEquals(5.0, BOX.getYMaximum(), EPSILON);
    assertEquals(6.0, BOX.getZMaximum(), EPSILON);
  }

  @Test
  void isNaN_falseForValid() {
    assertFalse(BOX.isNaN());
  }

  @Test
  void isNaN_trueForNaN() {
    assertTrue(AxisAlignedBox.NaN.isNaN());
  }

  @Test
  void getCenter_computesMidpoint() {
    Point3 center = BOX.getCenter();
    assertEquals(1.5, center.x(), EPSILON);
    assertEquals(1.5, center.y(), EPSILON);
    assertEquals(1.5, center.z(), EPSILON);
  }

  @Test
  void getCenterOfFrontFace() {
    Point3 p = BOX.getCenterOfFrontFace();
    assertEquals(1.5, p.x(), EPSILON);
    assertEquals(1.5, p.y(), EPSILON);
    assertEquals(-3.0, p.z(), EPSILON);
  }

  @Test
  void getCenterOfBackFace() {
    Point3 p = BOX.getCenterOfBackFace();
    assertEquals(1.5, p.x(), EPSILON);
    assertEquals(1.5, p.y(), EPSILON);
    assertEquals(6.0, p.z(), EPSILON);
  }

  @Test
  void getCenterOfLeftFace() {
    Point3 p = BOX.getCenterOfLeftFace();
    assertEquals(-1.0, p.x(), EPSILON);
    assertEquals(1.5, p.y(), EPSILON);
    assertEquals(1.5, p.z(), EPSILON);
  }

  @Test
  void getCenterOfRightFace() {
    Point3 p = BOX.getCenterOfRightFace();
    assertEquals(4.0, p.x(), EPSILON);
    assertEquals(1.5, p.y(), EPSILON);
    assertEquals(1.5, p.z(), EPSILON);
  }

  @Test
  void getCenterOfTopFace() {
    Point3 p = BOX.getCenterOfTopFace();
    assertEquals(1.5, p.x(), EPSILON);
    assertEquals(5.0, p.y(), EPSILON);
    assertEquals(1.5, p.z(), EPSILON);
  }

  @Test
  void getCenterOfBottomFace() {
    Point3 p = BOX.getCenterOfBottomFace();
    assertEquals(1.5, p.x(), EPSILON);
    assertEquals(-2.0, p.y(), EPSILON);
    assertEquals(1.5, p.z(), EPSILON);
  }

  @Test
  void getWidth() {
    assertEquals(5.0, BOX.getWidth(), EPSILON);
  }

  @Test
  void getHeight() {
    assertEquals(7.0, BOX.getHeight(), EPSILON);
  }

  @Test
  void getDepth() {
    assertEquals(9.0, BOX.getDepth(), EPSILON);
  }

  @Test
  void getSize() {
    Dimension3 size = BOX.getSize();
    assertEquals(5.0, size.x(), EPSILON);
    assertEquals(7.0, size.y(), EPSILON);
    assertEquals(9.0, size.z(), EPSILON);
  }

  @Test
  void getVolume() {
    assertEquals(5.0 * 7.0 * 9.0, BOX.getVolume(), EPSILON);
  }

  @Test
  void getDiagonal() {
    double expected = Math.sqrt(25 + 49 + 81);
    assertEquals(expected, BOX.getDiagonal(), EPSILON);
  }

  @Test
  void getDiagonal_nan() {
    assertTrue(Double.isNaN(AxisAlignedBox.NaN.getDiagonal()));
  }

  @Test
  void contains_insidePoint() {
    assertTrue(BOX.contains(new Point3(0, 0, 0)));
  }

  @Test
  void contains_outsidePoint() {
    assertFalse(BOX.contains(new Point3(100, 0, 0)));
  }

  @Test
  void contains_cornerPoint() {
    assertTrue(BOX.contains(new Point3(-1, -2, -3)));
  }

  @Test
  void contains_nan_returnsFalse() {
    assertFalse(AxisAlignedBox.NaN.contains(new Point3(0, 0, 0)));
  }

  @Test
  void unionPoint_extendsBox() {
    AxisAlignedBox result = BOX.union(new Point3(10, 10, 10));
    assertEquals(10.0, result.getXMaximum(), EPSILON);
    assertEquals(10.0, result.getYMaximum(), EPSILON);
    assertEquals(10.0, result.getZMaximum(), EPSILON);
    assertEquals(-1.0, result.getXMinimum(), EPSILON);
  }

  @Test
  void unionPoint_containedDoesNotChange() {
    AxisAlignedBox result = BOX.union(new Point3(0, 0, 0));
    assertSame(BOX, result);
  }

  @Test
  void unionPoint_nanPoint_ignored() {
    AxisAlignedBox result = BOX.union(Point3.NaN);
    assertSame(BOX, result);
  }

  @Test
  void unionPoint_fromNanBox() {
    Point3 p = new Point3(1, 2, 3);
    AxisAlignedBox result = AxisAlignedBox.NaN.union(p);
    assertEquals(p, result.minimum());
    assertEquals(p, result.maximum());
  }

  @Test
  void unionBox_extendsBox() {
    AxisAlignedBox other = AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 10, 10, 10);
    AxisAlignedBox result = BOX.union(other);
    assertEquals(-1.0, result.getXMinimum(), EPSILON);
    assertEquals(10.0, result.getXMaximum(), EPSILON);
  }

  @Test
  void unionBox_null_returnsSame() {
    AxisAlignedBox result = BOX.union((AxisAlignedBox) null);
    assertSame(BOX, result);
  }

  @Test
  void unionBox_nanBox_returnsSame() {
    AxisAlignedBox result = BOX.union(AxisAlignedBox.NaN);
    assertSame(BOX, result);
  }

  @Test
  void unionBox_fromNan_returnsOther() {
    AxisAlignedBox other = AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1);
    AxisAlignedBox result = AxisAlignedBox.NaN.union(other);
    assertSame(other, result);
  }

  @Test
  void getPoints_returns8Corners() {
    Point3[] points = BOX.getPoints();
    assertEquals(8, points.length);
    assertEquals(new Point3(-1, -2, -3), points[0]);
    assertEquals(new Point3(4, 5, 6), points[7]);
  }

  @Test
  void getVectors_returns8Corners() {
    Vector4[] vectors = BOX.getVectors();
    assertEquals(8, vectors.length);
    assertEquals(-1.0, vectors[0].x(), EPSILON);
    assertEquals(1.0, vectors[0].w(), EPSILON);
  }

  @Test
  void translate_movesBox() {
    Vector3 offset = new Vector3(10, 20, 30);
    AxisAlignedBox result = BOX.translate(offset);
    assertEquals(9.0, result.getXMinimum(), EPSILON);
    assertEquals(18.0, result.getYMinimum(), EPSILON);
    assertEquals(27.0, result.getZMinimum(), EPSILON);
    assertEquals(14.0, result.getXMaximum(), EPSILON);
  }

  @Test
  void scale_double_scalesBox() {
    AxisAlignedBox result = BOX.scale(2.0);
    assertEquals(-2.0, result.getXMinimum(), EPSILON);
    assertEquals(8.0, result.getXMaximum(), EPSILON);
  }

  @Test
  void scale_matrix_scalesBox() {
    Matrix3x3 scale = Matrix3x3.create(2, 0, 0, 0, 3, 0, 0, 0, 1);
    AxisAlignedBox result = BOX.scale(scale);
    assertEquals(-2.0, result.getXMinimum(), EPSILON);
    assertEquals(8.0, result.getXMaximum(), EPSILON);
    assertEquals(-6.0, result.getYMinimum(), EPSILON);
    assertEquals(15.0, result.getYMaximum(), EPSILON);
  }

  @Test
  void empty_hasZeroDimensions() {
    assertEquals(0.0, AxisAlignedBox.Empty.getWidth(), EPSILON);
    assertEquals(0.0, AxisAlignedBox.Empty.getHeight(), EPSILON);
    assertEquals(0.0, AxisAlignedBox.Empty.getDepth(), EPSILON);
    assertEquals(0.0, AxisAlignedBox.Empty.getVolume(), EPSILON);
  }
}
