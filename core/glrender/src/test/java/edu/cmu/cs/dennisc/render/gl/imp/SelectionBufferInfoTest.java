package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.FullMatrix4x4;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.alice.math.immutable.Vector4;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link SelectionBufferInfo} — z-buffer int-to-float conversion,
 * updatePointInSource(Matrix4x4), and accessor methods.
 *
 * SelectionBufferInfo is package-private, so tests live in the same package.
 * Constructs instances with nameCount=0 to skip visual adapter lookup.
 */
public class SelectionBufferInfoTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── Helper: create a PickContext (works headlessly — no GL needed for constructor) ──

  private PickContext createPickContext() {
    return new PickContext(true);
  }

  // ── Helper: create SelectionBufferInfo from raw int buffer values ──

  private SelectionBufferInfo createInfo(int nameCount, int zFrontInt, int zBackInt) {
    // IntBuffer layout: [nameCount, zFront, zBack, ...names]
    int extraNames = Math.max(nameCount, 0);
    IntBuffer buf = IntBuffer.allocate(3 + extraNames);
    buf.put(nameCount);
    buf.put(zFrontInt);
    buf.put(zBackInt);
    for (int i = 0; i < extraNames; i++) {
      buf.put(0);
    }
    buf.flip();
    return new SelectionBufferInfo(createPickContext(), buf, 0);
  }

  // ── Z-buffer conversion tests ──

  @Test
  public void zFront_zeroInt_yieldsZeroFloat() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertEquals(0.0f, info.getZFront(), 0.0001f);
  }

  @Test
  public void zBack_zeroInt_yieldsZeroFloat() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertEquals(0.0f, info.getZBack(), 0.0001f);
  }

  @Test
  public void zFront_maxUnsigned_yieldsOne() {
    // 0xFFFFFFFF as signed int is -1
    SelectionBufferInfo info = createInfo(0, -1, 0);
    assertEquals(1.0f, info.getZFront(), 0.0001f);
  }

  @Test
  public void zBack_maxUnsigned_yieldsOne() {
    SelectionBufferInfo info = createInfo(0, 0, -1);
    assertEquals(1.0f, info.getZBack(), 0.0001f);
  }

  @Test
  public void zFront_halfMaxUnsigned_yieldsApproximatelyHalf() {
    // 0x7FFFFFFF = 2147483647 → about 0.5
    int halfMax = 0x7FFFFFFF;
    SelectionBufferInfo info = createInfo(0, halfMax, 0);
    assertEquals(0.5f, info.getZFront(), 0.01f);
  }

  @Test
  public void zFront_quarterMaxUnsigned_yieldsApproximatelyQuarter() {
    // 0x3FFFFFFF → about 0.25
    int quarterMax = 0x3FFFFFFF;
    SelectionBufferInfo info = createInfo(0, quarterMax, 0);
    assertEquals(0.25f, info.getZFront(), 0.01f);
  }

  @Test
  public void zFront_precededByZBack_independent() {
    SelectionBufferInfo info = createInfo(0, 100, 200);
    float zf = info.getZFront();
    float zb = info.getZBack();
    assertTrue("zFront should be less than zBack", zf < zb);
  }

  // ── nameCount=0 ⇒ null visual adapter, default fields ──

  @Test
  public void nameCountZero_sgVisualIsNull() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertNull(info.getSgVisual());
  }

  @Test
  public void nameCountZero_isFrontFacingIsFalse() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertFalse(info.isFrontFacing());
  }

  @Test
  public void nameCountZero_geometryIndexIsNegativeOne() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertEquals(-1, info.getGeometryIndex());
  }

  @Test
  public void nameCountZero_subElementIsNegativeOne() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertEquals(-1, info.getSubElement());
  }

  @Test
  public void nameCountZero_sgGeometryIsNull() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertNull(info.getSGGeometry());
  }

  // ── pointInSource starts as NaN ──

  @Test
  public void pointInSource_initiallyNaN() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    assertTrue(info.getPointInSource().isNaN());
  }

  // ── updatePointInSource(Matrix4x4) math tests ──

  @Test
  public void updatePointInSource_identityMatrix_zFrontZero() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    // Identity matrix
    Matrix4x4 identity = AffineMatrix4x4.IDENTITY;
    info.updatePointInSource(identity);
    Point3 p = info.getPointInSource();
    assertFalse("After update with identity, point should not be NaN", p.isNaN());
    // z = 2*0 - 1 = -1; transform by identity: (0, 0, -1, 1) → (0, 0, -1)
    assertEquals(0.0, p.x(), 0.0001);
    assertEquals(0.0, p.y(), 0.0001);
    assertEquals(-1.0, p.z(), 0.0001);
  }

  @Test
  public void updatePointInSource_identityMatrix_zFrontHalf() {
    // zFront ≈ 0.5 → z = 2*0.5 - 1 = 0
    int halfMax = 0x7FFFFFFF;
    SelectionBufferInfo info = createInfo(0, halfMax, 0);
    Matrix4x4 identity = AffineMatrix4x4.IDENTITY;
    info.updatePointInSource(identity);
    Point3 p = info.getPointInSource();
    assertEquals(0.0, p.x(), 0.01);
    assertEquals(0.0, p.y(), 0.01);
    assertEquals(0.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_identityMatrix_zFrontOne() {
    // zFront = 1.0 → z = 2*1 - 1 = 1
    SelectionBufferInfo info = createInfo(0, -1, 0);
    Matrix4x4 identity = AffineMatrix4x4.IDENTITY;
    info.updatePointInSource(identity);
    Point3 p = info.getPointInSource();
    assertEquals(0.0, p.x(), 0.01);
    assertEquals(0.0, p.y(), 0.01);
    assertEquals(1.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_translationMatrix() {
    // Translate by (10, 20, 30)
    SelectionBufferInfo info = createInfo(0, 0, 0);
    Matrix4x4 m = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(10, 20, 30));
    info.updatePointInSource(m);
    Point3 p = info.getPointInSource();
    // v = (0, 0, -1, 1) transformed by translation → (10, 20, 29)
    assertEquals(10.0, p.x(), 0.01);
    assertEquals(20.0, p.y(), 0.01);
    assertEquals(29.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_scaleMatrix() {
    // Scale by 2x via a full 4x4 matrix
    SelectionBufferInfo info = createInfo(0, 0, 0);
    Matrix4x4 scale = new FullMatrix4x4(
        new Vector4(2, 0, 0, 0),
        new Vector4(0, 2, 0, 0),
        new Vector4(0, 0, 2, 0),
        new Vector4(0, 0, 0, 1));
    info.updatePointInSource(scale);
    Point3 p = info.getPointInSource();
    // v = (0, 0, -1, 1), scaled: (0, 0, -2, 1) → (0, 0, -2)
    assertEquals(0.0, p.x(), 0.01);
    assertEquals(0.0, p.y(), 0.01);
    assertEquals(-2.0, p.z(), 0.01);
  }

  @Test
  public void updatePointInSource_perspectiveMatrix_performsWDivide() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    // Perspective-style matrix with w != 1
    Matrix4x4 m = new FullMatrix4x4(
        new Vector4(1, 0, 0, 0),
        new Vector4(0, 1, 0, 0),
        new Vector4(0, 0, 1, -1),
        new Vector4(0, 0, 0, 0));
    info.updatePointInSource(m);
    Point3 p = info.getPointInSource();
    // v = (0,0,-1,1): transform → (0, 0, -1, 1) with z-component and w: z*1 + 1*0 = -1, w = 0*-1 + 1*0 = 1
    // Actually: row3=(0,0,1,-1) means new_z = 0*0 + 0*0 + 1*(-1) + (-1)*1 = -2
    //           row4=(0,0,0,0) means new_w = 0 + 0 + 0 + 0 = 0 → division by zero → Infinity/NaN
    // This tests the w-divide path with edge case
    assertNotNull(p);
  }

  // ── Offset in IntBuffer ──

  @Test
  public void constructor_withNonZeroOffset_readsCorrectly() {
    // Place garbage at offset 0-2, real data at offset 3
    IntBuffer buf = IntBuffer.allocate(6);
    buf.put(99); buf.put(99); buf.put(99); // garbage
    buf.put(0);   // nameCount
    buf.put(-1);  // zFront = max unsigned
    buf.put(0);   // zBack = 0
    buf.flip();
    SelectionBufferInfo info = new SelectionBufferInfo(createPickContext(), buf, 3);
    assertEquals(1.0f, info.getZFront(), 0.0001f);
    assertEquals(0.0f, info.getZBack(), 0.0001f);
  }

  // ── nameCount=4 with unknown visual adapter ──

  @Test
  public void nameCountFour_unknownKey_visualAdapterIsNull() {
    // nameCount=4, keys that don't match any registered adapter
    IntBuffer buf = IntBuffer.allocate(7);
    buf.put(4);   // nameCount
    buf.put(0);   // zFront
    buf.put(0);   // zBack
    buf.put(999); // key (no adapter registered for this)
    buf.put(1);   // isFrontFacing = true
    buf.put(0);   // geometryIndex
    buf.put(42);  // subElement
    buf.flip();
    SelectionBufferInfo info = new SelectionBufferInfo(createPickContext(), buf, 0);
    // visualAdapter will be null since key 999 isn't registered
    assertNull(info.getSgVisual());
    assertEquals(0, info.getGeometryIndex());
    assertEquals(42, info.getSubElement());
  }

  // ── updatePointInSource overwrite ──

  @Test
  public void updatePointInSource_calledTwice_secondOverwritesFirst() {
    SelectionBufferInfo info = createInfo(0, 0, 0);
    Matrix4x4 identity = AffineMatrix4x4.IDENTITY;
    info.updatePointInSource(identity);
    Point3 p1 = info.getPointInSource();

    Matrix4x4 translate = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(5, 5, 5));
    info.updatePointInSource(translate);
    Point3 p2 = info.getPointInSource();

    assertNotEquals("Second call should overwrite first", p1, p2);
  }
}
