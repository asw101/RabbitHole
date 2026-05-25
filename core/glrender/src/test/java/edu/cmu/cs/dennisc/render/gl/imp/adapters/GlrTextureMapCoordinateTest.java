package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrTexture} — texture coordinate mapping, reference counting,
 * dirty-state tracking, and structural validation. Avoids GL calls entirely by
 * using a concrete subclass that stubs out the abstract method.
 */
public class GlrTextureMapCoordinateTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  // ── mapU / mapV identity mapping ───────────────────────────────────

  @Test
  public void mapU_returnsIdentity_zero() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.0f, tex.mapU(0.0f), 0.0001f);
  }

  @Test
  public void mapU_returnsIdentity_one() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(1.0f, tex.mapU(1.0f), 0.0001f);
  }

  @Test
  public void mapU_returnsIdentity_half() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.5f, tex.mapU(0.5f), 0.0001f);
  }

  @Test
  public void mapU_returnsIdentity_negative() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(-0.5f, tex.mapU(-0.5f), 0.0001f);
  }

  @Test
  public void mapU_returnsIdentity_greaterThanOne() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(2.0f, tex.mapU(2.0f), 0.0001f);
  }

  @Test
  public void mapV_returnsIdentity_zero() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.0f, tex.mapV(0.0f), 0.0001f);
  }

  @Test
  public void mapV_returnsIdentity_one() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(1.0f, tex.mapV(1.0f), 0.0001f);
  }

  @Test
  public void mapV_returnsIdentity_half() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.5f, tex.mapV(0.5f), 0.0001f);
  }

  @Test
  public void mapV_returnsIdentity_negative() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(-1.0f, tex.mapV(-1.0f), 0.0001f);
  }

  @Test
  public void mapV_returnsIdentity_greaterThanOne() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(3.5f, tex.mapV(3.5f), 0.0001f);
  }

  // ── UV symmetry ────────────────────────────────────────────────────

  @Test
  public void mapU_and_mapV_sameInput_sameOutput() {
    TestableGlrTexture tex = new TestableGlrTexture();
    float val = 0.75f;
    assertEquals(tex.mapU(val), tex.mapV(val), 0.0001f);
  }

  // ── reference counting ─────────────────────────────────────────────

  @Test
  public void newTexture_isNotReferenced() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void addReference_makesReferenced() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.addReference();
    assertTrue(tex.isReferenced());
  }

  @Test
  public void addReference_removeReference_notReferenced() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.addReference();
    tex.removeReference();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void multipleAddReference_allMustBeRemoved() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.addReference();
    tex.addReference();
    tex.addReference();
    assertTrue(tex.isReferenced());
    tex.removeReference();
    assertTrue(tex.isReferenced());
    tex.removeReference();
    assertTrue(tex.isReferenced());
    tex.removeReference();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void removeReference_whenZero_doesNotGoNegative() {
    TestableGlrTexture tex = new TestableGlrTexture();
    // Should log severe but not crash
    tex.removeReference();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void addOneRemoveOne_cycle() {
    TestableGlrTexture tex = new TestableGlrTexture();
    for (int i = 0; i < 5; i++) {
      tex.addReference();
      assertTrue(tex.isReferenced());
      tex.removeReference();
      assertFalse(tex.isReferenced());
    }
  }

  // ── dirty state (via reflection since isDirty is protected) ────────

  @Test
  public void newTexture_isDirtyByDefault() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertTrue(invokeDirtyCheck(tex));
  }

  @Test
  public void setDirtyFalse_clearsFlag() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    invokeSetDirty(tex, false);
    assertFalse(invokeDirtyCheck(tex));
  }

  @Test
  public void setDirtyTrue_setsFlag() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    invokeSetDirty(tex, false);
    invokeSetDirty(tex, true);
    assertTrue(invokeDirtyCheck(tex));
  }

  @Test
  public void setDirty_toggle_multiple() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    for (int i = 0; i < 3; i++) {
      invokeSetDirty(tex, false);
      assertFalse(invokeDirtyCheck(tex));
      invokeSetDirty(tex, true);
      assertTrue(invokeDirtyCheck(tex));
    }
  }

  // ── isValid with null owner ────────────────────────────────────────

  @Test
  public void isValid_nullOwner_returnsFalse() {
    TestableGlrTexture tex = new TestableGlrTexture();
    // owner is null by default since initialize() not called
    assertFalse(tex.isValid());
  }

  // ── structural checks ─────────────────────────────────────────────

  @Test
  public void glrTexture_isAbstract_andExtendsGlrObject() {
    assertTrue(Modifier.isAbstract(GlrTexture.class.getModifiers()));
    assertEquals(GlrObject.class, GlrTexture.class.getSuperclass());
  }

  @Test
  public void publicApiMethods_allExist() throws Exception {
    String[] methods = {"mapU", "mapV", "addReference", "removeReference",
        "isReferenced", "isPotentiallyAlphaBlended", "isValid"};
    for (String name : methods) {
      try {
        // Try no-arg first, then float-arg
        Method m;
        if (name.equals("mapU") || name.equals("mapV")) {
          m = GlrTexture.class.getMethod(name, float.class);
        } else {
          m = GlrTexture.class.getMethod(name);
        }
        assertTrue(name + " should be public", Modifier.isPublic(m.getModifiers()));
      } catch (NoSuchMethodException e) {
        fail("Expected public method not found: " + name);
      }
    }
  }

  @Test
  public void privateFields_refCountAndDirtyFlag_exist() throws Exception {
    for (String fieldName : new String[]{"refCount", "isTextureDataDirty"}) {
      Field f = GlrTexture.class.getDeclaredField(fieldName);
      assertTrue(fieldName + " should be private", Modifier.isPrivate(f.getModifiers()));
    }
  }

  // ── edge values for mapU/mapV ──────────────────────────────────────

  @Test
  public void mapU_verySmallPositive() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(Float.MIN_VALUE, tex.mapU(Float.MIN_VALUE), 0.0f);
  }

  @Test
  public void mapV_maxValue() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(Float.MAX_VALUE, tex.mapV(Float.MAX_VALUE), 0.0f);
  }

  @Test
  public void mapU_nan() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertTrue(Float.isNaN(tex.mapU(Float.NaN)));
  }

  @Test
  public void mapV_positiveInfinity() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(Float.POSITIVE_INFINITY, tex.mapV(Float.POSITIVE_INFINITY), 0.0f);
  }

  @Test
  public void mapU_negativeInfinity() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(Float.NEGATIVE_INFINITY, tex.mapU(Float.NEGATIVE_INFINITY), 0.0f);
  }

  // ── helpers ────────────────────────────────────────────────────────

  private boolean invokeDirtyCheck(TestableGlrTexture tex) throws Exception {
    Method m = GlrTexture.class.getDeclaredMethod("isDirty");
    m.setAccessible(true);
    return (boolean) m.invoke(tex);
  }

  private void invokeSetDirty(TestableGlrTexture tex, boolean dirty) throws Exception {
    Method m = GlrTexture.class.getDeclaredMethod("setDirty", boolean.class);
    m.setAccessible(true);
    m.invoke(tex, dirty);
  }

  /**
   * Concrete subclass to test non-GL methods. The abstract newTextureData
   * is never called in these tests.
   */
  private static class TestableGlrTexture extends GlrTexture {
    @Override
    protected com.jogamp.opengl.util.texture.TextureData newTextureData(
        com.jogamp.opengl.GL gl,
        com.jogamp.opengl.util.texture.TextureData currentTexture) {
      throw new UnsupportedOperationException("test stub");
    }
  }
}
