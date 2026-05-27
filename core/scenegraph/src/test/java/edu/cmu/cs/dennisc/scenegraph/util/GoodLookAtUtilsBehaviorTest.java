package edu.cmu.cs.dennisc.scenegraph.util;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GoodLookAtUtilsBehaviorTest {
  @Test
  public void createLookAtMatrixFacingNegativeZProducesIdentityOrientationAndEyeTranslation() throws Exception {
    AffineMatrix4x4 matrix = invokeCreateLookAtMatrix(1.0, 2.0, 3.0, 1.0, 2.0, 2.0, 0.0, 1.0, 0.0);
    OrthogonalMatrix3x3 orientation = matrix.orientation();

    assertEquals(1.0, orientation.right().x(), 1.0e-9);
    assertEquals(1.0, orientation.up().y(), 1.0e-9);
    assertEquals(1.0, orientation.backward().z(), 1.0e-9);
    assertEquals(-1.0, matrix.translation().x(), 1.0e-9);
    assertEquals(-2.0, matrix.translation().y(), 1.0e-9);
    assertEquals(-3.0, matrix.translation().z(), 1.0e-9);
  }

  @Test
  public void createLookAtMatrixHandlesLookingStraightUpWithAlternateUpVector() throws Exception {
    AffineMatrix4x4 matrix = invokeCreateLookAtMatrix(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0);
    OrthogonalMatrix3x3 orientation = matrix.orientation();

    assertTrue(Double.isFinite(orientation.right().x()));
    assertTrue(Double.isFinite(orientation.up().z()));
    assertEquals(1.0, orientation.right().x(), 1.0e-9);
    assertEquals(1.0, orientation.up().z(), 1.0e-9);
    assertEquals(-1.0, orientation.backward().y(), 1.0e-9);
  }

  @Test
  public void createLookAtMatrixHandlesLookingStraightDownWithAlternateUpVector() throws Exception {
    AffineMatrix4x4 matrix = invokeCreateLookAtMatrix(0.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 1.0);
    OrthogonalMatrix3x3 orientation = matrix.orientation();

    assertTrue(Double.isFinite(orientation.right().x()));
    assertTrue(Double.isFinite(orientation.up().z()));
    assertEquals(-1.0, orientation.right().x(), 1.0e-9);
    assertEquals(1.0, orientation.up().z(), 1.0e-9);
    assertEquals(1.0, orientation.backward().y(), 1.0e-9);
  }

  private static AffineMatrix4x4 invokeCreateLookAtMatrix(
      double eyeX,
      double eyeY,
      double eyeZ,
      double centerX,
      double centerY,
      double centerZ,
      double upX,
      double upY,
      double upZ) throws Exception {
    Method method = GoodLookAtUtils.class.getDeclaredMethod(
        "createLookAtMatrix",
        double.class,
        double.class,
        double.class,
        double.class,
        double.class,
        double.class,
        double.class,
        double.class,
        double.class);
    method.setAccessible(true);
    return (AffineMatrix4x4) method.invoke(null, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
  }
}
