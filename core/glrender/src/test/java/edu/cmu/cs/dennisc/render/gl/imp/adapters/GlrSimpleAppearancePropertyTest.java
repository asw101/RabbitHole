package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.FillingStyle;
import edu.cmu.cs.dennisc.scenegraph.ShadingStyle;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrSimpleAppearance} — propertyChanged coverage for all 9 property
 * branches, opacity-related booleans, and state queries.
 */
public class GlrSimpleAppearancePropertyTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  private GlrSimpleAppearance<?> adapter;
  private SimpleAppearance sg;

  @Before
  public void setUp() {
    sg = new SimpleAppearance();
    adapter = (GlrSimpleAppearance<?>) AdapterFactory.getAdapterFor(sg);
  }

  // ── propertyChanged — diffuseColor ────────────────────────────────

  @Test
  public void propertyChanged_diffuseColor_syncsArray() throws Exception {
    sg.diffuseColor.setValue(Color4f.RED);
    adapter.propertyChanged(sg.diffuseColor);
    float[] diffuse = getFloatArray("diffuse");
    assertEquals(1.0f, diffuse[0], 0.01f); // red
    assertEquals(0.0f, diffuse[1], 0.01f); // green
    assertEquals(0.0f, diffuse[2], 0.01f); // blue
  }

  @Test
  public void propertyChanged_diffuseColor_updatesOpacityBooleans() throws Exception {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    adapter.propertyChanged(sg.opacity);
    // With full opacity and white diffuse, material should be showing and not alpha blended
    assertTrue(adapter.isActuallyShowing());
    assertFalse(adapter.isAlphaBlended());
  }

  // ── propertyChanged — opacity ─────────────────────────────────────

  @Test
  public void propertyChanged_opacity_syncsFloat() throws Exception {
    sg.opacity.setValue(0.5f);
    adapter.propertyChanged(sg.opacity);
    Field f = GlrSimpleAppearance.class.getDeclaredField("opacity");
    f.setAccessible(true);
    assertEquals(0.5f, f.getFloat(adapter), 0.01f);
  }

  @Test
  public void propertyChanged_opacity_halfTransparent_isAlphaBlended() throws Exception {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(0.5f);
    adapter.propertyChanged(sg.opacity);
    assertTrue("Half opacity should be alpha blended", adapter.isAlphaBlended());
    assertTrue("Half opacity should be showing", adapter.isActuallyShowing());
  }

  @Test
  public void propertyChanged_opacity_zero_notShowing() throws Exception {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(0.0f);
    adapter.propertyChanged(sg.opacity);
    assertFalse("Zero opacity should not be showing", adapter.isActuallyShowing());
  }

  // ── propertyChanged — ambientColor ────────────────────────────────

  @Test
  public void propertyChanged_ambientColor_syncsArray() throws Exception {
    sg.ambientColor.setValue(Color4f.BLUE);
    adapter.propertyChanged(sg.ambientColor);
    float[] ambient = getFloatArray("ambient");
    assertEquals(0.0f, ambient[0], 0.01f); // red
    assertEquals(0.0f, ambient[1], 0.01f); // green
    assertEquals(1.0f, ambient[2], 0.01f); // blue
  }

  @Test
  public void propertyChanged_ambientColor_nanLinksToSpecular() throws Exception {
    // Default ambient is NaN — linked to diffuse
    adapter.propertyChanged(sg.ambientColor);
    Field f = GlrSimpleAppearance.class.getDeclaredField("isAmbientLinkedToDiffuse");
    f.setAccessible(true);
    assertTrue("NaN ambient should link to diffuse", f.getBoolean(adapter));
  }

  @Test
  public void propertyChanged_ambientColor_explicitValue_unlinks() throws Exception {
    sg.ambientColor.setValue(Color4f.RED);
    adapter.propertyChanged(sg.ambientColor);
    Field f = GlrSimpleAppearance.class.getDeclaredField("isAmbientLinkedToDiffuse");
    f.setAccessible(true);
    assertFalse("Explicit ambient should not link to diffuse", f.getBoolean(adapter));
  }

  // ── propertyChanged — specularHighlightColor ──────────────────────

  @Test
  public void propertyChanged_specularHighlightColor_syncsArray() throws Exception {
    sg.specularHighlightColor.setValue(Color4f.GREEN);
    adapter.propertyChanged(sg.specularHighlightColor);
    float[] specular = getFloatArray("specular");
    assertEquals(0.0f, specular[0], 0.01f);
    assertEquals(1.0f, specular[1], 0.01f);
    assertEquals(0.0f, specular[2], 0.01f);
  }

  // ── propertyChanged — specularHighlightExponent ───────────────────

  @Test
  public void propertyChanged_specularHighlightExponent_syncsFloat() throws Exception {
    sg.specularHighlightExponent.setValue(64.0f);
    adapter.propertyChanged(sg.specularHighlightExponent);
    Field f = GlrSimpleAppearance.class.getDeclaredField("shininess");
    f.setAccessible(true);
    assertEquals(64.0f, f.getFloat(adapter), 0.01f);
  }

  // ── propertyChanged — emissiveColor ───────────────────────────────

  @Test
  public void propertyChanged_emissiveColor_syncsArray() throws Exception {
    sg.emissiveColor.setValue(Color4f.YELLOW);
    adapter.propertyChanged(sg.emissiveColor);
    float[] emissive = getFloatArray("emissive");
    assertEquals(1.0f, emissive[0], 0.01f);
    assertEquals(1.0f, emissive[1], 0.01f);
    assertEquals(0.0f, emissive[2], 0.01f);
  }

  // ── propertyChanged — fillingStyle ────────────────────────────────

  @Test
  public void propertyChanged_fillingStyle_solid() throws Exception {
    sg.fillingStyle.setValue(FillingStyle.SOLID);
    adapter.propertyChanged(sg.fillingStyle);
    int mode = getPolygonMode();
    // GL_FILL = 0x1B02 = 6914
    assertEquals(6914, mode);
  }

  @Test
  public void propertyChanged_fillingStyle_wireframe() throws Exception {
    sg.fillingStyle.setValue(FillingStyle.WIREFRAME);
    adapter.propertyChanged(sg.fillingStyle);
    int mode = getPolygonMode();
    // GL_LINE = 0x1B01 = 6913
    assertEquals(6913, mode);
  }

  @Test
  public void propertyChanged_fillingStyle_points() throws Exception {
    sg.fillingStyle.setValue(FillingStyle.POINTS);
    adapter.propertyChanged(sg.fillingStyle);
    int mode = getPolygonMode();
    // GL_POINT = 0x1B00 = 6912
    assertEquals(6912, mode);
  }

  // ── propertyChanged — shadingStyle ────────────────────────────────

  @Test
  public void propertyChanged_shadingStyle_smooth_isShaded() throws Exception {
    sg.shadingStyle.setValue(ShadingStyle.SMOOTH);
    adapter.propertyChanged(sg.shadingStyle);
    assertTrue("SMOOTH should be shaded", getBoolean("isShaded"));
  }

  @Test
  public void propertyChanged_shadingStyle_flat_isShaded() throws Exception {
    sg.shadingStyle.setValue(ShadingStyle.FLAT);
    adapter.propertyChanged(sg.shadingStyle);
    assertTrue("FLAT should be shaded", getBoolean("isShaded"));
  }

  @Test
  public void propertyChanged_shadingStyle_none_notShaded() throws Exception {
    sg.shadingStyle.setValue(ShadingStyle.NONE);
    adapter.propertyChanged(sg.shadingStyle);
    assertFalse("NONE should not be shaded", getBoolean("isShaded"));
  }

  @Test
  public void propertyChanged_shadingStyle_none_afterSmooth_notShaded() throws Exception {
    sg.shadingStyle.setValue(ShadingStyle.SMOOTH);
    adapter.propertyChanged(sg.shadingStyle);
    assertTrue(getBoolean("isShaded"));
    sg.shadingStyle.setValue(ShadingStyle.NONE);
    adapter.propertyChanged(sg.shadingStyle);
    assertFalse("After toggle to NONE should not be shaded", getBoolean("isShaded"));
  }

  // ── propertyChanged — isEthereal ──────────────────────────────────

  @Test
  public void propertyChanged_isEthereal_true() throws Exception {
    sg.isEthereal.setValue(true);
    adapter.propertyChanged(sg.isEthereal);
    assertTrue(adapter.isEthereal());
  }

  @Test
  public void propertyChanged_isEthereal_false() throws Exception {
    sg.isEthereal.setValue(false);
    adapter.propertyChanged(sg.isEthereal);
    assertFalse(adapter.isEthereal());
  }

  // ── isAllAlphaBlended ─────────────────────────────────────────────

  @Test
  public void isAllAlphaBlended_matchesIsAlphaBlended() throws Exception {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(0.5f);
    adapter.propertyChanged(sg.opacity);
    assertEquals(adapter.isAlphaBlended(), adapter.isAllAlphaBlended());
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrAppearance() {
    assertTrue(GlrAppearance.class.isAssignableFrom(GlrSimpleAppearance.class));
  }

  @Test
  public void ownerIsSet() {
    assertSame(sg, adapter.getOwner());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private float[] getFloatArray(String name) throws Exception {
    Field f = GlrSimpleAppearance.class.getDeclaredField(name);
    f.setAccessible(true);
    return (float[]) f.get(adapter);
  }

  private int getPolygonMode() throws Exception {
    Field f = GlrSimpleAppearance.class.getDeclaredField("polygonMode");
    f.setAccessible(true);
    return f.getInt(adapter);
  }

  private boolean getBoolean(String name) throws Exception {
    Field f = GlrSimpleAppearance.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.getBoolean(adapter);
  }
}
