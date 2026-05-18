package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSimpleAppearance} — default field values, opacity math,
 * boolean state queries (isActuallyShowing, isAlphaBlended, isAllAlphaBlended,
 * isEthereal), and the updateOpacityRelatedBooleans logic via reflection.
 */
public class GlrSimpleAppearanceStateTest {

  // ── Default field values ──────────────────────────────────────────

  @Test
  public void isShaded_defaultFalse() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(getBoolField(appearance, "isShaded"));
  }

  @Test
  public void isAmbientLinkedToDiffuse_defaultFalse() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(getBoolField(appearance, "isAmbientLinkedToDiffuse"));
  }

  @Test
  public void isMaterialActuallyShowing_defaultFalse() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(getBoolField(appearance, "isMaterialActuallyShowing"));
  }

  @Test
  public void isMaterialAlphaBlended_defaultFalse() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(getBoolField(appearance, "isMaterialAlphaBlended"));
  }

  @Test
  public void isEthereal_defaultFalse() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(getBoolField(appearance, "isEthereal"));
  }

  @Test
  public void opacity_defaultNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    Field f = GlrSimpleAppearance.class.getDeclaredField("opacity");
    f.setAccessible(true);
    assertTrue(Float.isNaN(f.getFloat(appearance)));
  }

  @Test
  public void shininess_defaultNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    Field f = GlrSimpleAppearance.class.getDeclaredField("shininess");
    f.setAccessible(true);
    assertTrue(Float.isNaN(f.getFloat(appearance)));
  }

  @Test
  public void polygonMode_defaultZero() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    Field f = GlrSimpleAppearance.class.getDeclaredField("polygonMode");
    f.setAccessible(true);
    assertEquals(0, f.getInt(appearance));
  }

  // ── Color arrays are initialized to NaN ───────────────────────────

  @Test
  public void ambient_initiallyNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    float[] ambient = getFloatArray(appearance, "ambient");
    for (float v : ambient) {
      assertTrue("ambient should be NaN", Float.isNaN(v));
    }
  }

  @Test
  public void diffuse_initiallyNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    float[] diffuse = getFloatArray(appearance, "diffuse");
    for (float v : diffuse) {
      assertTrue("diffuse should be NaN", Float.isNaN(v));
    }
  }

  @Test
  public void specular_initiallyNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    float[] specular = getFloatArray(appearance, "specular");
    for (float v : specular) {
      assertTrue("specular should be NaN", Float.isNaN(v));
    }
  }

  @Test
  public void emissive_initiallyNaN() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    float[] emissive = getFloatArray(appearance, "emissive");
    for (float v : emissive) {
      assertTrue("emissive should be NaN", Float.isNaN(v));
    }
  }

  @Test
  public void colorArrays_haveLength4() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertEquals(4, getFloatArray(appearance, "ambient").length);
    assertEquals(4, getFloatArray(appearance, "diffuse").length);
    assertEquals(4, getFloatArray(appearance, "specular").length);
    assertEquals(4, getFloatArray(appearance, "emissive").length);
  }

  // ── isActuallyShowing ─────────────────────────────────────────────

  @Test
  public void isActuallyShowing_default_false() {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(appearance.isActuallyShowing());
  }

  @Test
  public void isActuallyShowing_materialShowing_true() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setBoolField(appearance, "isMaterialActuallyShowing", true);
    assertTrue(appearance.isActuallyShowing());
  }

  // ── isAlphaBlended ────────────────────────────────────────────────

  @Test
  public void isAlphaBlended_default_false() {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(appearance.isAlphaBlended());
  }

  @Test
  public void isAlphaBlended_materialAlpha_true() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setBoolField(appearance, "isMaterialAlphaBlended", true);
    assertTrue(appearance.isAlphaBlended());
  }

  // ── isAllAlphaBlended ─────────────────────────────────────────────

  @Test
  public void isAllAlphaBlended_default_false() {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(appearance.isAllAlphaBlended());
  }

  @Test
  public void isAllAlphaBlended_materialAlpha_true() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setBoolField(appearance, "isMaterialAlphaBlended", true);
    assertTrue(appearance.isAllAlphaBlended());
  }

  // ── isEthereal ────────────────────────────────────────────────────

  @Test
  public void isEthereal_default_false() {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    assertFalse(appearance.isEthereal());
  }

  @Test
  public void isEthereal_setTrue_returnsTrue() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setBoolField(appearance, "isEthereal", true);
    assertTrue(appearance.isEthereal());
  }

  // ── updateOpacityRelatedBooleans via reflection ────────────────────

  @Test
  public void updateOpacity_opaqueColor_showingAndNotBlended() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setFloatField(appearance, "opacity", 1.0f);
    setFloatArrayValues(appearance, "diffuse", 1.0f, 0.0f, 0.0f, 1.0f);
    invokeUpdateOpacity(appearance);
    assertTrue(getBoolField(appearance, "isMaterialActuallyShowing"));
    assertFalse(getBoolField(appearance, "isMaterialAlphaBlended"));
  }

  @Test
  public void updateOpacity_halfOpacity_showingAndBlended() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setFloatField(appearance, "opacity", 0.5f);
    setFloatArrayValues(appearance, "diffuse", 1.0f, 0.0f, 0.0f, 1.0f);
    invokeUpdateOpacity(appearance);
    assertTrue(getBoolField(appearance, "isMaterialActuallyShowing"));
    assertTrue(getBoolField(appearance, "isMaterialAlphaBlended"));
  }

  @Test
  public void updateOpacity_zeroOpacity_notShowing() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setFloatField(appearance, "opacity", 0.0f);
    setFloatArrayValues(appearance, "diffuse", 1.0f, 0.0f, 0.0f, 1.0f);
    invokeUpdateOpacity(appearance);
    assertFalse(getBoolField(appearance, "isMaterialActuallyShowing"));
  }

  @Test
  public void updateOpacity_transparentDiffuseAlpha_blended() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setFloatField(appearance, "opacity", 1.0f);
    setFloatArrayValues(appearance, "diffuse", 1.0f, 0.0f, 0.0f, 0.5f);
    invokeUpdateOpacity(appearance);
    assertTrue(getBoolField(appearance, "isMaterialActuallyShowing"));
    assertTrue(getBoolField(appearance, "isMaterialAlphaBlended"));
  }

  @Test
  public void updateOpacity_zeroDiffuseAlpha_notShowing() throws Exception {
    GlrSimpleAppearance<?> appearance = new GlrSimpleAppearance<>();
    setFloatField(appearance, "opacity", 1.0f);
    setFloatArrayValues(appearance, "diffuse", 1.0f, 0.0f, 0.0f, 0.0f);
    invokeUpdateOpacity(appearance);
    assertFalse(getBoolField(appearance, "isMaterialActuallyShowing"));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static boolean getBoolField(Object obj, String name) throws Exception {
    Field f = findField(obj.getClass(), name);
    f.setAccessible(true);
    return f.getBoolean(obj);
  }

  private static void setBoolField(Object obj, String name, boolean value) throws Exception {
    Field f = findField(obj.getClass(), name);
    f.setAccessible(true);
    f.setBoolean(obj, value);
  }

  private static void setFloatField(Object obj, String name, float value) throws Exception {
    Field f = findField(obj.getClass(), name);
    f.setAccessible(true);
    f.setFloat(obj, value);
  }

  private static float[] getFloatArray(Object obj, String name) throws Exception {
    Field f = findField(obj.getClass(), name);
    f.setAccessible(true);
    return (float[]) f.get(obj);
  }

  private static void setFloatArrayValues(Object obj, String name, float... values) throws Exception {
    float[] arr = getFloatArray(obj, name);
    System.arraycopy(values, 0, arr, 0, values.length);
  }

  private static void invokeUpdateOpacity(Object obj) throws Exception {
    java.lang.reflect.Method m = GlrSimpleAppearance.class.getDeclaredMethod("updateOpacityRelatedBooleans");
    m.setAccessible(true);
    m.invoke(obj);
  }

  private static Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
    while (clazz != null) {
      try {
        return clazz.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        clazz = clazz.getSuperclass();
      }
    }
    throw new NoSuchFieldException(name);
  }
}
