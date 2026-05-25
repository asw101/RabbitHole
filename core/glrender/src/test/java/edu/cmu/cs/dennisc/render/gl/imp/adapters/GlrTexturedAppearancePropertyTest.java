package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrTexturedAppearance} — propertyChanged coverage for texture
 * properties, alpha blend logic, and delegation to GlrSimpleAppearance.
 */
public class GlrTexturedAppearancePropertyTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private GlrTexturedAppearance adapter;
  private TexturedAppearance sg;

  @Before
  public void setUp() {
    sg = new TexturedAppearance();
    adapter = (GlrTexturedAppearance) AdapterFactory.getAdapterFor(sg);
  }

  // ── propertyChanged — isDiffuseColorTextureAlphaBlended ───────────

  @Test
  public void propertyChanged_isDiffuseColorTextureAlphaBlended_true() throws Exception {
    sg.isDiffuseColorTextureAlphaBlended.setValue(true);
    adapter.propertyChanged(sg.isDiffuseColorTextureAlphaBlended);
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureAlphaBlended");
    f.setAccessible(true);
    assertTrue(f.getBoolean(adapter));
  }

  @Test
  public void propertyChanged_isDiffuseColorTextureAlphaBlended_false() throws Exception {
    sg.isDiffuseColorTextureAlphaBlended.setValue(false);
    adapter.propertyChanged(sg.isDiffuseColorTextureAlphaBlended);
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureAlphaBlended");
    f.setAccessible(true);
    assertFalse(f.getBoolean(adapter));
  }

  // ── propertyChanged — isDiffuseColorTextureClamped ────────────────

  @Test
  public void propertyChanged_isDiffuseColorTextureClamped_true() throws Exception {
    sg.isDiffuseColorTextureClamped.setValue(true);
    adapter.propertyChanged(sg.isDiffuseColorTextureClamped);
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureClamped");
    f.setAccessible(true);
    assertTrue(f.getBoolean(adapter));
  }

  @Test
  public void propertyChanged_isDiffuseColorTextureClamped_false() throws Exception {
    sg.isDiffuseColorTextureClamped.setValue(false);
    adapter.propertyChanged(sg.isDiffuseColorTextureClamped);
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureClamped");
    f.setAccessible(true);
    assertFalse(f.getBoolean(adapter));
  }

  // ── propertyChanged — diffuseColorTexture (null→null) ─────────────

  @Test
  public void propertyChanged_diffuseColorTexture_nullToNull() throws Exception {
    sg.diffuseColorTexture.setValue(null);
    adapter.propertyChanged(sg.diffuseColorTexture);
    Field f = GlrTexturedAppearance.class.getDeclaredField("diffuseColorTextureAdapter");
    f.setAccessible(true);
    assertNull(f.get(adapter));
  }

  // ── propertyChanged — bumpTexture (null→null) ─────────────────────

  @Test
  public void propertyChanged_bumpTexture_nullToNull() throws Exception {
    sg.bumpTexture.setValue(null);
    adapter.propertyChanged(sg.bumpTexture);
    Field f = GlrTexturedAppearance.class.getDeclaredField("bumpTextureAdapter");
    f.setAccessible(true);
    assertNull(f.get(adapter));
  }

  // ── propertyChanged — textureId (todo log) ────────────────────────

  @Test
  public void propertyChanged_textureId_doesNotThrow() {
    sg.textureId.setValue(42);
    adapter.propertyChanged(sg.textureId);
  }

  // ── isAlphaBlended — combines texture + material ──────────────────

  @Test
  public void isAlphaBlended_falseWhenBothFalse() {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(1.0f);
    adapter.propertyChanged(sg.opacity);
    sg.isDiffuseColorTextureAlphaBlended.setValue(false);
    adapter.propertyChanged(sg.isDiffuseColorTextureAlphaBlended);
    assertFalse(adapter.isAlphaBlended());
  }

  @Test
  public void isAlphaBlended_trueWhenTextureAlphaBlended() {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(1.0f);
    adapter.propertyChanged(sg.opacity);
    sg.isDiffuseColorTextureAlphaBlended.setValue(true);
    adapter.propertyChanged(sg.isDiffuseColorTextureAlphaBlended);
    assertTrue(adapter.isAlphaBlended());
  }

  @Test
  public void isAlphaBlended_trueWhenMaterialAlphaBlended() {
    sg.diffuseColor.setValue(Color4f.WHITE);
    adapter.propertyChanged(sg.diffuseColor);
    sg.opacity.setValue(0.5f);
    adapter.propertyChanged(sg.opacity);
    sg.isDiffuseColorTextureAlphaBlended.setValue(false);
    adapter.propertyChanged(sg.isDiffuseColorTextureAlphaBlended);
    assertTrue(adapter.isAlphaBlended());
  }

  // ── Delegation to SimpleAppearance ────────────────────────────────

  @Test
  public void propertyChanged_diffuseColor_delegatesToSuper() throws Exception {
    sg.diffuseColor.setValue(Color4f.BLUE);
    adapter.propertyChanged(sg.diffuseColor);
    Field f = GlrSimpleAppearance.class.getDeclaredField("diffuse");
    f.setAccessible(true);
    float[] diffuse = (float[]) f.get(adapter);
    assertEquals(0.0f, diffuse[0], 0.01f); // RED component of BLUE
    assertEquals(0.0f, diffuse[1], 0.01f);
    assertEquals(1.0f, diffuse[2], 0.01f);
  }

  @Test
  public void propertyChanged_opacity_delegatesToSuper() throws Exception {
    sg.opacity.setValue(0.3f);
    adapter.propertyChanged(sg.opacity);
    Field f = GlrSimpleAppearance.class.getDeclaredField("opacity");
    f.setAccessible(true);
    assertEquals(0.3f, f.getFloat(adapter), 0.01f);
  }

  // ── Structure ─────────────────────────────────────────────────────

  @Test
  public void extendsGlrSimpleAppearance() {
    assertTrue(GlrSimpleAppearance.class.isAssignableFrom(GlrTexturedAppearance.class));
  }

  @Test
  public void ownerIsSet() {
    assertSame(sg, adapter.getOwner());
  }

  // ── Initial state ─────────────────────────────────────────────────

  @Test
  public void initialDiffuseColorTextureAdapter_isNull() throws Exception {
    Field f = GlrTexturedAppearance.class.getDeclaredField("diffuseColorTextureAdapter");
    f.setAccessible(true);
    assertNull(f.get(adapter));
  }

  @Test
  public void initialBumpTextureAdapter_isNull() throws Exception {
    Field f = GlrTexturedAppearance.class.getDeclaredField("bumpTextureAdapter");
    f.setAccessible(true);
    assertNull(f.get(adapter));
  }

  @Test
  public void initialIsDiffuseColorTextureAlphaBlended_isFalse() throws Exception {
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureAlphaBlended");
    f.setAccessible(true);
    assertFalse(f.getBoolean(adapter));
  }

  @Test
  public void initialIsDiffuseColorTextureClamped_isFalse() throws Exception {
    Field f = GlrTexturedAppearance.class.getDeclaredField("isDiffuseColorTextureClamped");
    f.setAccessible(true);
    assertFalse(f.getBoolean(adapter));
  }
}
