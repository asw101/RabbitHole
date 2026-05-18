package edu.cmu.cs.dennisc.render.gl.imp;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector4;
import org.alice.math.immutable.FullMatrix4x4;
import org.junit.Test;

import java.nio.IntBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link SelectionBufferInfo} z-buffer math, point-in-source
 * computation, and field access when nameCount is zero (no visual adapter).
 */
public class SelectionBufferInfoZBufferTest {

  private static SelectionBufferInfo create(int zFrontInt, int zBackInt) {
    IntBuffer buf = IntBuffer.allocate(3);
    buf.put(0);           // nameCount = 0
    buf.put(zFrontInt);
    buf.put(zBackInt);
    buf.flip();
    PickContext pc = new PickContext(true);
    return new SelectionBufferInfo(pc, buf, 0);
  }

  // ── Z-front / Z-back ──────────────────────────────────────────────

  @Test
  public void zFront_zero_returnsZero() {
    SelectionBufferInfo sbi = create(0, 0);
    assertEquals(0f, sbi.getZFront(), 0.0001f);
  }

  @Test
  public void zBack_zero_returnsZero() {
    SelectionBufferInfo sbi = create(0, 0);
    assertEquals(0f, sbi.getZBack(), 0.0001f);
  }

  @Test
  public void zFront_maxUnsigned_returnsOne() {
    // 0xFFFFFFFF as signed int is -1
    SelectionBufferInfo sbi = create(-1, 0);
    assertEquals(1.0f, sbi.getZFront(), 0.0001f);
  }

  @Test
  public void zBack_maxUnsigned_returnsOne() {
    SelectionBufferInfo sbi = create(0, -1);
    assertEquals(1.0f, sbi.getZBack(), 0.0001f);
  }

  @Test
  public void zFront_halfMax_returnsHalf() {
    int halfMax = (int) (PickContext.MAX_UNSIGNED_INTEGER / 2);
    SelectionBufferInfo sbi = create(halfMax, 0);
    assertEquals(0.5f, sbi.getZFront(), 0.01f);
  }

  @Test
  public void zFrontAndBack_bothSet() {
    SelectionBufferInfo sbi = create(-1, -1);
    assertEquals(1.0f, sbi.getZFront(), 0.0001f);
    assertEquals(1.0f, sbi.getZBack(), 0.0001f);
  }

  @Test
  public void zFront_lessThanZBack() {
    int front = (int) (PickContext.MAX_UNSIGNED_INTEGER / 4);
    int back = (int) (PickContext.MAX_UNSIGNED_INTEGER * 3 / 4);
    SelectionBufferInfo sbi = create(front, back);
    assertTrue(sbi.getZFront() < sbi.getZBack());
  }

  // ── Visual adapter null when nameCount=0 ──────────────────────────

  @Test
  public void getSgVisual_noAdapter_returnsNull() {
    SelectionBufferInfo sbi = create(0, 0);
    assertNull(sbi.getSgVisual());
  }

  @Test
  public void isFrontFacing_noAdapter_returnsFalse() {
    SelectionBufferInfo sbi = create(0, 0);
    assertFalse(sbi.isFrontFacing());
  }

  @Test
  public void getGeometryIndex_noAdapter_returnsNegativeOne() {
    SelectionBufferInfo sbi = create(0, 0);
    assertEquals(-1, sbi.getGeometryIndex());
  }

  @Test
  public void getSubElement_noAdapter_returnsNegativeOne() {
    SelectionBufferInfo sbi = create(0, 0);
    assertEquals(-1, sbi.getSubElement());
  }

  @Test
  public void getSGGeometry_noAdapter_returnsNull() {
    SelectionBufferInfo sbi = create(0, 0);
    assertNull(sbi.getSGGeometry());
  }

  // ── pointInSource ─────────────────────────────────────────────────

  @Test
  public void getPointInSource_initiallyNaN() {
    SelectionBufferInfo sbi = create(0, 0);
    assertTrue(sbi.getPointInSource().isNaN());
  }

  @Test
  public void updatePointInSource_identityMatrix_zFrontZero() {
    SelectionBufferInfo sbi = create(0, 0);
    // z = 2*zFront - 1 = -1
    // v = M * (0, 0, -1, 1) with identity M => (0, 0, -1, 1)
    // point = (0/1, 0/1, -1/1) = (0, 0, -1)
    sbi.updatePointInSource(Matrix4x4.IDENTITY);
    Point3 p = sbi.getPointInSource();
    assertFalse(p.isNaN());
    assertEquals(0.0, p.x(), 0.0001);
    assertEquals(0.0, p.y(), 0.0001);
    assertEquals(-1.0, p.z(), 0.0001);
  }

  @Test
  public void updatePointInSource_identityMatrix_zFrontHalf() {
    int halfMax = (int) (PickContext.MAX_UNSIGNED_INTEGER / 2);
    SelectionBufferInfo sbi = create(halfMax, 0);
    // z = 2*0.5 - 1 = 0
    // v = identity * (0, 0, 0, 1) => (0, 0, 0, 1)
    sbi.updatePointInSource(Matrix4x4.IDENTITY);
    Point3 p = sbi.getPointInSource();
    assertEquals(0.0, p.x(), 0.01);
    assertEquals(0.0, p.y(), 0.01);
    assertEquals(0.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_identityMatrix_zFrontMax() {
    SelectionBufferInfo sbi = create(-1, -1);
    // z = 2*1 - 1 = 1
    // v = identity * (0, 0, 1, 1) => (0, 0, 1, 1)
    sbi.updatePointInSource(Matrix4x4.IDENTITY);
    Point3 p = sbi.getPointInSource();
    assertEquals(0.0, p.x(), 0.0001);
    assertEquals(0.0, p.y(), 0.0001);
    assertEquals(1.0, p.z(), 0.0001);
  }

  @Test
  public void updatePointInSource_translationMatrix() {
    SelectionBufferInfo sbi = create(0, 0);
    // z = -1, v = M*(0,0,-1,1)
    // With translation (10, 20, 30), result = (10, 20, 29, 1)
    Matrix4x4 m = AffineMatrix4x4.createTranslation(10, 20, 30);
    sbi.updatePointInSource(m);
    Point3 p = sbi.getPointInSource();
    assertEquals(10.0, p.x(), 0.01);
    assertEquals(20.0, p.y(), 0.01);
    assertEquals(29.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_calledTwice_overwritesPrevious() {
    SelectionBufferInfo sbi = create(0, 0);
    sbi.updatePointInSource(Matrix4x4.IDENTITY);
    Point3 first = sbi.getPointInSource();
    sbi.updatePointInSource(AffineMatrix4x4.createTranslation(5, 5, 5));
    Point3 second = sbi.getPointInSource();
    assertNotEquals(first, second);
  }
}
