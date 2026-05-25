package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.awt.geom.AffineTransform;

import static org.junit.Assert.*;

/**
 * Additional tests for Graphics2D AffineTransform→4x4 matrix conversion —
 * tests numeric stability, chained operations, and matrix properties.
 */
public class Graphics2DMatrixPropertyTest {


  // ── Identity properties ───────────────────────────────────────────

  @Test
  public void identity_allDiagonalOnes() {
    double[] gl = convert(new AffineTransform());
    assertEquals(1.0, gl[0], 1e-10);
    assertEquals(1.0, gl[5], 1e-10);
    assertEquals(1.0, gl[10], 1e-10);
    assertEquals(1.0, gl[15], 1e-10);
  }

  @Test
  public void identity_allOffDiagonalZero() {
    double[] gl = convert(new AffineTransform());
    for (int i = 0; i < 16; i++) {
      int col = i / 4;
      int row = i % 4;
      if (row != col) {
        assertEquals("gl[" + i + "] should be 0", 0.0, gl[i], 1e-10);
      }
    }
  }

  // ── Translation preserves identity sub-matrix ─────────────────────

  @Test
  public void translation_preserves2x2Identity() {
    double[] gl = convert(AffineTransform.getTranslateInstance(42, 99));
    assertEquals(1.0, gl[0], 1e-10);
    assertEquals(0.0, gl[1], 1e-10);
    assertEquals(0.0, gl[4], 1e-10);
    assertEquals(1.0, gl[5], 1e-10);
  }

  // ── Rotation properties ───────────────────────────────────────────

  @Test
  public void rotation360_isIdentity() {
    double[] gl = convert(AffineTransform.getRotateInstance(2 * Math.PI));
    assertEquals(1.0, gl[0], 1e-6);
    assertEquals(0.0, gl[1], 1e-6);
    assertEquals(0.0, gl[4], 1e-6);
    assertEquals(1.0, gl[5], 1e-6);
  }

  @Test
  public void rotation_preservesDeterminant() {
    for (double theta : new double[]{0, Math.PI/6, Math.PI/4, Math.PI/3, Math.PI/2, Math.PI}) {
      AffineTransform at = AffineTransform.getRotateInstance(theta);
      double[] gl = convert(at);
      double det = gl[0] * gl[5] - gl[4] * gl[1];
      assertEquals("Rotation determinant should be 1 for theta=" + theta, 1.0, det, 1e-6);
    }
  }

  @Test
  public void rotation_orthogonality() {
    double theta = Math.PI / 3;
    double[] gl = convert(AffineTransform.getRotateInstance(theta));
    // For rotation matrix: column vectors should be orthogonal
    // col0 = (gl[0], gl[1]), col1 = (gl[4], gl[5])
    double dot = gl[0] * gl[4] + gl[1] * gl[5];
    assertEquals(0.0, dot, 1e-6);
  }

  // ── Scale properties ──────────────────────────────────────────────

  @Test
  public void scale_determinantIsProduct() {
    double sx = 2.5, sy = 3.7;
    double[] gl = convert(AffineTransform.getScaleInstance(sx, sy));
    double det = gl[0] * gl[5] - gl[4] * gl[1];
    assertEquals(sx * sy, det, 1e-6);
  }

  @Test
  public void uniformScale_preservesRotation() {
    double s = 3.0;
    AffineTransform at = new AffineTransform();
    at.scale(s, s);
    at.rotate(Math.PI / 4);
    double[] gl = convert(at);
    double det = gl[0] * gl[5] - gl[4] * gl[1];
    assertEquals(s * s, det, 1e-6);
  }

  // ── Composite transforms ──────────────────────────────────────────

  @Test
  public void translateRotateScale_translationPreserved() {
    AffineTransform at = new AffineTransform();
    at.translate(100, 200);
    at.rotate(Math.PI / 4);
    at.scale(2, 3);
    double[] gl = convert(at);
    assertEquals(100.0, gl[12], 1e-6);
    assertEquals(200.0, gl[13], 1e-6);
  }

  @Test
  public void scaleTranslate_order_matters() {
    // Scale-then-translate: translate is NOT affected by scale in AffineTransform.translate
    AffineTransform at1 = new AffineTransform();
    at1.scale(2, 3);
    at1.translate(10, 20);

    AffineTransform at2 = new AffineTransform();
    at2.translate(10, 20);
    at2.scale(2, 3);

    double[] gl1 = convert(at1);
    double[] gl2 = convert(at2);

    // They should differ in translation
    assertNotEquals(gl1[12], gl2[12], 1e-6);
  }

  // ── Z components always fixed ─────────────────────────────────────

  @Test
  public void anyTransform_zComponentsFixed() {
    AffineTransform[] transforms = {
        new AffineTransform(),
        AffineTransform.getTranslateInstance(10, 20),
        AffineTransform.getScaleInstance(2, 3),
        AffineTransform.getRotateInstance(1.0),
        AffineTransform.getShearInstance(0.5, 0.3),
        new AffineTransform(1.5, 0.3, 0.7, 2.1, 50, 60)
    };
    for (AffineTransform at : transforms) {
      double[] gl = convert(at);
      assertEquals(0.0, gl[2], 1e-10);
      assertEquals(0.0, gl[6], 1e-10);
      assertEquals(1.0, gl[10], 1e-10);
      assertEquals(0.0, gl[14], 1e-10);
      assertEquals(0.0, gl[8], 1e-10);
      assertEquals(0.0, gl[9], 1e-10);
      assertEquals(0.0, gl[3], 1e-10);
      assertEquals(0.0, gl[7], 1e-10);
      assertEquals(0.0, gl[11], 1e-10);
      assertEquals(1.0, gl[15], 1e-10);
    }
  }

  // ── Numeric stability ─────────────────────────────────────────────

  @Test
  public void verySmallScale_noZero() {
    double[] gl = convert(AffineTransform.getScaleInstance(1e-8, 1e-8));
    assertEquals(1e-8, gl[0], 1e-15);
    assertEquals(1e-8, gl[5], 1e-15);
  }

  @Test
  public void veryLargeTranslation_preserved() {
    double[] gl = convert(AffineTransform.getTranslateInstance(1e12, -1e12));
    assertEquals(1e12, gl[12], 1.0);
    assertEquals(-1e12, gl[13], 1.0);
  }

  // ── Shear properties ──────────────────────────────────────────────

  @Test
  public void shear_determinantPreserved() {
    AffineTransform at = AffineTransform.getShearInstance(0.5, 0.25);
    double[] gl = convert(at);
    double det = gl[0] * gl[5] - gl[4] * gl[1];
    // AffineTransform shear determinant = 1 - shx*shy = 1 - 0.5*0.25 = 0.875
    assertEquals(at.getDeterminant(), det, 1e-6);
  }

  @Test
  public void shear_translation_isZero() {
    double[] gl = convert(AffineTransform.getShearInstance(0.5, 0.25));
    assertEquals(0.0, gl[12], 1e-10);
    assertEquals(0.0, gl[13], 1e-10);
  }

  // ── Helper ────────────────────────────────────────────────────────

  private static double[] convert(AffineTransform at) {
    double[] s = new double[6];
    at.getMatrix(s);
    double[] gl = new double[16];
    gl[0] = s[0]; gl[4] = s[2]; gl[8] = 0; gl[12] = s[4];
    gl[1] = s[1]; gl[5] = s[3]; gl[9] = 0; gl[13] = s[5];
    gl[2] = 0;    gl[6] = 0;    gl[10] = 1; gl[14] = 0;
    gl[3] = 0;    gl[7] = 0;    gl[11] = 0; gl[15] = 1;
    return gl;
  }
}
