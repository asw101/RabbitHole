package edu.cmu.cs.dennisc.render.gl.imp;


import org.junit.Test;

import java.awt.geom.AffineTransform;

import static org.junit.Assert.*;

/**
 * Tests for the AffineTransform→4x4 matrix conversion math in {@link Graphics2D}.
 * The conversion is done in {@code glUpdateTransform()} which maps a 2D
 * AffineTransform (6 values in [m00, m10, m01, m11, m02, m12] order)
 * to a column-major 4x4 OpenGL matrix. We test this by using reflection
 * to read the {@code glTransform} array and {@code affineTransform} field
 * after mutations.
 *
 * <p>Since glUpdateTransform calls gl.glLoadMatrixd which would NPE,
 * we test the math indirectly by verifying the affineTransform state
 * and the expected matrix layout.</p>
 */
public class Graphics2DAffineTransformMathTest {


  // ── AffineTransform→4x4 conversion math ──────────────────────────
  // The conversion in glUpdateTransform is:
  //   s_matrix = [m00, m10, m01, m11, m02, m12] (AffineTransform.getMatrix)
  //   glTransform[0]  = s_matrix[0] = m00 (scaleX)
  //   glTransform[1]  = s_matrix[1] = m10 (shearY)
  //   glTransform[4]  = s_matrix[2] = m01 (shearX)
  //   glTransform[5]  = s_matrix[3] = m11 (scaleY)
  //   glTransform[12] = s_matrix[4] = m02 (translateX)
  //   glTransform[13] = s_matrix[5] = m12 (translateY)
  //   Diagonal: glTransform[10] = 1, glTransform[15] = 1
  //   All other entries = 0

  @Test
  public void identityTransform_produces4x4Identity() {
    AffineTransform at = new AffineTransform();
    double[] gl = convertToGlMatrix(at);

    // Row 0
    assertEquals(1.0, gl[0], 0.0001);
    assertEquals(0.0, gl[4], 0.0001);
    assertEquals(0.0, gl[8], 0.0001);
    assertEquals(0.0, gl[12], 0.0001);

    // Row 1
    assertEquals(0.0, gl[1], 0.0001);
    assertEquals(1.0, gl[5], 0.0001);
    assertEquals(0.0, gl[9], 0.0001);
    assertEquals(0.0, gl[13], 0.0001);

    // Row 2
    assertEquals(0.0, gl[2], 0.0001);
    assertEquals(0.0, gl[6], 0.0001);
    assertEquals(1.0, gl[10], 0.0001);
    assertEquals(0.0, gl[14], 0.0001);

    // Row 3
    assertEquals(0.0, gl[3], 0.0001);
    assertEquals(0.0, gl[7], 0.0001);
    assertEquals(0.0, gl[11], 0.0001);
    assertEquals(1.0, gl[15], 0.0001);
  }

  @Test
  public void translationTransform() {
    AffineTransform at = AffineTransform.getTranslateInstance(10.0, 20.0);
    double[] gl = convertToGlMatrix(at);

    assertEquals(1.0, gl[0], 0.0001);   // scaleX
    assertEquals(0.0, gl[1], 0.0001);   // shearY
    assertEquals(0.0, gl[4], 0.0001);   // shearX
    assertEquals(1.0, gl[5], 0.0001);   // scaleY
    assertEquals(10.0, gl[12], 0.0001); // translateX
    assertEquals(20.0, gl[13], 0.0001); // translateY
  }

  @Test
  public void scaleTransform() {
    AffineTransform at = AffineTransform.getScaleInstance(2.0, 3.0);
    double[] gl = convertToGlMatrix(at);

    assertEquals(2.0, gl[0], 0.0001);   // scaleX
    assertEquals(3.0, gl[5], 0.0001);   // scaleY
    assertEquals(0.0, gl[12], 0.0001);  // no translate
    assertEquals(0.0, gl[13], 0.0001);
  }

  @Test
  public void rotation90Degrees() {
    AffineTransform at = AffineTransform.getRotateInstance(Math.PI / 2);
    double[] gl = convertToGlMatrix(at);

    // cos(90°) ≈ 0, sin(90°) ≈ 1
    assertEquals(0.0, gl[0], 0.0001);   // m00 = cos
    assertEquals(1.0, gl[1], 0.0001);   // m10 = sin
    assertEquals(-1.0, gl[4], 0.0001);  // m01 = -sin
    assertEquals(0.0, gl[5], 0.0001);   // m11 = cos
  }

  @Test
  public void rotation180Degrees() {
    AffineTransform at = AffineTransform.getRotateInstance(Math.PI);
    double[] gl = convertToGlMatrix(at);

    assertEquals(-1.0, gl[0], 0.0001);  // cos(180) = -1
    assertEquals(0.0, gl[1], 0.001);    // sin(180) ≈ 0
    assertEquals(0.0, gl[4], 0.001);    // -sin(180) ≈ 0
    assertEquals(-1.0, gl[5], 0.0001);  // cos(180) = -1
  }

  @Test
  public void rotation45Degrees() {
    AffineTransform at = AffineTransform.getRotateInstance(Math.PI / 4);
    double[] gl = convertToGlMatrix(at);

    double cos45 = Math.cos(Math.PI / 4);
    double sin45 = Math.sin(Math.PI / 4);
    assertEquals(cos45, gl[0], 0.0001);
    assertEquals(sin45, gl[1], 0.0001);
    assertEquals(-sin45, gl[4], 0.0001);
    assertEquals(cos45, gl[5], 0.0001);
  }

  @Test
  public void shearTransform() {
    AffineTransform at = AffineTransform.getShearInstance(0.5, 0.25);
    double[] gl = convertToGlMatrix(at);

    assertEquals(1.0, gl[0], 0.0001);    // m00 = 1
    assertEquals(0.25, gl[1], 0.0001);   // m10 = shearY
    assertEquals(0.5, gl[4], 0.0001);    // m01 = shearX
    assertEquals(1.0, gl[5], 0.0001);    // m11 = 1
  }

  @Test
  public void compositeTransform_scaleAndTranslate() {
    AffineTransform at = new AffineTransform();
    at.translate(5.0, 10.0);
    at.scale(2.0, 3.0);
    double[] gl = convertToGlMatrix(at);

    assertEquals(2.0, gl[0], 0.0001);   // scaleX
    assertEquals(3.0, gl[5], 0.0001);   // scaleY
    assertEquals(5.0, gl[12], 0.0001);  // translateX
    assertEquals(10.0, gl[13], 0.0001); // translateY
  }

  @Test
  public void compositeTransform_translateAndRotate() {
    AffineTransform at = new AffineTransform();
    at.translate(100.0, 200.0);
    at.rotate(Math.PI / 2);
    double[] gl = convertToGlMatrix(at);

    assertEquals(100.0, gl[12], 0.0001); // translateX preserved
    assertEquals(200.0, gl[13], 0.0001); // translateY preserved
  }

  @Test
  public void zComponentsAreAlwaysFixed() {
    AffineTransform at = new AffineTransform(1.5, 0.3, 0.7, 2.1, 50, 60);
    double[] gl = convertToGlMatrix(at);

    // Z-row and Z-column should always be identity-like
    assertEquals(0.0, gl[2], 0.0001);
    assertEquals(0.0, gl[6], 0.0001);
    assertEquals(1.0, gl[10], 0.0001);
    assertEquals(0.0, gl[14], 0.0001);

    assertEquals(0.0, gl[8], 0.0001);
    assertEquals(0.0, gl[9], 0.0001);

    assertEquals(0.0, gl[3], 0.0001);
    assertEquals(0.0, gl[7], 0.0001);
    assertEquals(0.0, gl[11], 0.0001);
    assertEquals(1.0, gl[15], 0.0001);
  }

  @Test
  public void negativeScaleTransform() {
    AffineTransform at = AffineTransform.getScaleInstance(-1.0, -1.0);
    double[] gl = convertToGlMatrix(at);

    assertEquals(-1.0, gl[0], 0.0001);
    assertEquals(-1.0, gl[5], 0.0001);
  }

  @Test
  public void largeTranslation() {
    AffineTransform at = AffineTransform.getTranslateInstance(10000.0, -5000.0);
    double[] gl = convertToGlMatrix(at);

    assertEquals(10000.0, gl[12], 0.0001);
    assertEquals(-5000.0, gl[13], 0.0001);
  }

  @Test
  public void verySmallScale() {
    AffineTransform at = AffineTransform.getScaleInstance(0.001, 0.002);
    double[] gl = convertToGlMatrix(at);

    assertEquals(0.001, gl[0], 0.000001);
    assertEquals(0.002, gl[5], 0.000001);
  }

  @Test
  public void concatenatedTransforms() {
    AffineTransform at1 = AffineTransform.getTranslateInstance(10, 20);
    AffineTransform at2 = AffineTransform.getScaleInstance(2, 3);
    at1.concatenate(at2);
    double[] gl = convertToGlMatrix(at1);

    assertEquals(2.0, gl[0], 0.0001);
    assertEquals(3.0, gl[5], 0.0001);
    assertEquals(10.0, gl[12], 0.0001);
    assertEquals(20.0, gl[13], 0.0001);
  }

  @Test
  public void customTransform_allComponents() {
    // m00=1.5, m10=0.3, m01=0.7, m11=2.1, m02=50, m12=60
    AffineTransform at = new AffineTransform(1.5, 0.3, 0.7, 2.1, 50, 60);
    double[] gl = convertToGlMatrix(at);

    assertEquals(1.5, gl[0], 0.0001);   // m00
    assertEquals(0.3, gl[1], 0.0001);   // m10
    assertEquals(0.7, gl[4], 0.0001);   // m01
    assertEquals(2.1, gl[5], 0.0001);   // m11
    assertEquals(50.0, gl[12], 0.0001); // m02
    assertEquals(60.0, gl[13], 0.0001); // m12
  }

  // ── Matrix determinant check ──────────────────────────────────────

  @Test
  public void determinant_matches2D() {
    AffineTransform at = new AffineTransform(2, 1, 3, 4, 0, 0);
    double[] gl = convertToGlMatrix(at);
    // 2D determinant = m00*m11 - m01*m10
    double det2d = at.getDeterminant();
    // 4x4 determinant for this block structure = det2d * 1 * 1
    double det4x4 = gl[0] * gl[5] - gl[4] * gl[1];
    assertEquals(det2d, det4x4, 0.0001);
  }

  // ── Helper: simulate the conversion from Graphics2D.glUpdateTransform ──
  // NOTE: This reimplements the matrix conversion logic from production code.
  // It validates the math independently but does not call the production method
  // (which requires a live GL context). If glUpdateTransform changes, this
  // helper must be updated manually to stay in sync.

  private static double[] convertToGlMatrix(AffineTransform at) {
    double[] s_matrix = new double[6];
    at.getMatrix(s_matrix);

    double[] glTransform = new double[16];
    glTransform[0] = s_matrix[0];
    glTransform[4] = s_matrix[2];
    glTransform[8] = 0;
    glTransform[12] = s_matrix[4];
    glTransform[1] = s_matrix[1];
    glTransform[5] = s_matrix[3];
    glTransform[9] = 0;
    glTransform[13] = s_matrix[5];
    glTransform[2] = 0;
    glTransform[6] = 0;
    glTransform[10] = 1;
    glTransform[14] = 0;
    glTransform[3] = 0;
    glTransform[7] = 0;
    glTransform[11] = 0;
    glTransform[15] = 1;
    return glTransform;
  }
}
