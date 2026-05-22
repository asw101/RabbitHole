package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrVisual} — state flags (isShowing, isScaleIdentity),
 * appearance nullability checks, geometry adapter array,
 * and RenderType enum values.
 */
public class GlrVisualStateTest {

  // ── RenderType enum ──────────────────────────────────────────────

  @Test
  public void renderType_OPAQUE_exists() {
    assertEquals("OPAQUE", GlrVisual.RenderType.OPAQUE.name());
  }

  @Test
  public void renderType_ALPHA_BLENDED_exists() {
    assertEquals("ALPHA_BLENDED", GlrVisual.RenderType.ALPHA_BLENDED.name());
  }

  @Test
  public void renderType_GHOST_exists() {
    assertEquals("GHOST", GlrVisual.RenderType.GHOST.name());
  }

  @Test
  public void renderType_SILHOUETTE_exists() {
    assertEquals("SILHOUETTE", GlrVisual.RenderType.SILHOUETTE.name());
  }

  @Test
  public void renderType_ALL_exists() {
    assertEquals("ALL", GlrVisual.RenderType.ALL.name());
  }

  @Test
  public void renderType_values_hasFiveEntries() {
    assertEquals(5, GlrVisual.RenderType.values().length);
  }

  @Test
  public void renderType_valueOf_OPAQUE() {
    assertEquals(GlrVisual.RenderType.OPAQUE, GlrVisual.RenderType.valueOf("OPAQUE"));
  }

  @Test
  public void renderType_valueOf_ALL() {
    assertEquals(GlrVisual.RenderType.ALL, GlrVisual.RenderType.valueOf("ALL"));
  }

  // ── Default state of GlrVisual fields ─────────────────────────────

  @Test
  public void isShowing_defaultFalse() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("isShowing");
    f.setAccessible(true);
    assertFalse(f.getBoolean(visual));
  }

  @Test
  public void isScaleIdentity_defaultTrue() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("isScaleIdentity");
    f.setAccessible(true);
    assertTrue(f.getBoolean(visual));
  }

  @Test
  public void glrGeometries_initiallyNull() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("glrGeometries");
    f.setAccessible(true);
    assertNull(f.get(visual));
  }

  @Test
  public void glrFrontFacingAppearance_initiallyNull() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("glrFrontFacingAppearance");
    f.setAccessible(true);
    assertNull(f.get(visual));
  }

  @Test
  public void glrBackFacingAppearance_initiallyNull() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("glrBackFacingAppearance");
    f.setAccessible(true);
    assertNull(f.get(visual));
  }

  // ── isActuallyShowing (protected) ─────────────────────────────────

  @Test
  public void isActuallyShowing_default_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isActuallyShowing");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  @Test
  public void isActuallyShowing_showingButNoGeometry_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    setField(visual, "isShowing", true);
    Method m = GlrVisual.class.getDeclaredMethod("isActuallyShowing");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  @Test
  public void isActuallyShowing_showingButNoAppearance_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    setField(visual, "isShowing", true);
    setField(visual, "glrGeometries", new GlrGeometry[]{});
    Method m = GlrVisual.class.getDeclaredMethod("isActuallyShowing");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── isAnyFaceActuallyShowing ──────────────────────────────────────

  @Test
  public void isAnyFaceActuallyShowing_bothNull_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isAnyFaceActuallyShowing");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── isAnyFaceAlphaBlended ─────────────────────────────────────────

  @Test
  public void isAnyFaceAlphaBlended_bothNull_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isAnyFaceAlphaBlended");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── isEthereal (private) ──────────────────────────────────────────

  @Test
  public void isEthereal_bothNull_true() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isEthereal");
    m.setAccessible(true);
    assertTrue((boolean) m.invoke(visual));
  }

  // ── hasOpaque ─────────────────────────────────────────────────────

  @Test
  public void hasOpaque_nullGeometries_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("hasOpaque");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  @Test
  public void hasOpaque_emptyGeometries_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    setField(visual, "glrGeometries", new GlrGeometry[]{});
    Method m = GlrVisual.class.getDeclaredMethod("hasOpaque");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── isAlphaBlended ────────────────────────────────────────────────

  @Test
  public void isAlphaBlended_nullGeometries_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isAlphaBlended");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  @Test
  public void isAlphaBlended_emptyGeometries_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    setField(visual, "glrGeometries", new GlrGeometry[]{});
    Method m = GlrVisual.class.getDeclaredMethod("isAlphaBlended");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── isAllAlpha ────────────────────────────────────────────────────

  @Test
  public void isAllAlpha_bothNull_false() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("isAllAlpha");
    m.setAccessible(true);
    assertFalse((boolean) m.invoke(visual));
  }

  // ── scale buffer ──────────────────────────────────────────────────

  @Test
  public void scaleBuffer_initialSize() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("scale");
    f.setAccessible(true);
    double[] scale = (double[]) f.get(visual);
    assertEquals(16, scale.length);
  }

  @Test
  public void scaleBuffer_initiallyAllZero() throws Exception {
    GlrVisual<?> visual = createVisual();
    Field f = GlrVisual.class.getDeclaredField("scale");
    f.setAccessible(true);
    double[] scale = (double[]) f.get(visual);
    for (double v : scale) {
      assertEquals(0.0, v, 0.0001);
    }
  }

  // ── updateScale via reflection ────────────────────────────────────

  @Test
  public void updateScale_identityMatrix_setsIdentityTrue() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("updateScale", org.alice.math.immutable.Matrix3x3.class);
    m.setAccessible(true);
    m.invoke(visual, org.alice.math.immutable.Matrix3x3.IDENTITY);
    Field f = GlrVisual.class.getDeclaredField("isScaleIdentity");
    f.setAccessible(true);
    assertTrue(f.getBoolean(visual));
  }

  @Test
  public void updateScale_nonIdentityMatrix_setsIdentityFalse() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("updateScale", org.alice.math.immutable.Matrix3x3.class);
    m.setAccessible(true);
    org.alice.math.immutable.Matrix3x3 scaled = org.alice.math.immutable.Matrix3x3.create(
        2, 0, 0,
        0, 2, 0,
        0, 0, 2
    );
    m.invoke(visual, scaled);
    Field f = GlrVisual.class.getDeclaredField("isScaleIdentity");
    f.setAccessible(true);
    assertFalse(f.getBoolean(visual));
  }

  @Test
  public void updateScale_writesColumnMajorArray() throws Exception {
    GlrVisual<?> visual = createVisual();
    Method m = GlrVisual.class.getDeclaredMethod("updateScale", org.alice.math.immutable.Matrix3x3.class);
    m.setAccessible(true);
    m.invoke(visual, org.alice.math.immutable.Matrix3x3.IDENTITY);
    Field f = GlrVisual.class.getDeclaredField("scale");
    f.setAccessible(true);
    double[] scale = (double[]) f.get(visual);
    // Column-major 4x4 for identity 3x3: [0]=1, [5]=1, [10]=1, [15]=1
    assertEquals(1.0, scale[0], 0.0001);
    assertEquals(1.0, scale[5], 0.0001);
    assertEquals(1.0, scale[10], 0.0001);
    assertEquals(1.0, scale[15], 0.0001);
  }

  // ── getFrontFacingAppearanceAdapter ────────────────────────────────

  @Test
  public void getFrontFacingAppearanceAdapter_initiallyNull() {
    GlrVisual<?> visual = createVisual();
    assertNull(visual.getFrontFacingAppearanceAdapter());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  private static GlrVisual<?> createVisual() {
    return new GlrVisual<>();
  }

  private static void setField(Object obj, String fieldName, Object value) throws Exception {
    Class<?> clazz = obj.getClass();
    while (clazz != null) {
      try {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
        return;
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new NoSuchFieldException(fieldName);
  }
}
