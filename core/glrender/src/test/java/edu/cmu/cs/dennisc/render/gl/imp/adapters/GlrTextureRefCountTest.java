package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrTexture} — reference counting, render context list management,
 * dirty flag, mapU/mapV identity mapping, and isValid/isPotentiallyAlphaBlended
 * when owner is null.
 *
 * Uses a minimal concrete subclass to test the abstract class.
 */
public class GlrTextureRefCountTest {

  // ── Reference counting ────────────────────────────────────────────

  @Test
  public void refCount_initiallyZero() throws Exception {
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
  public void addThenRemoveReference_notReferenced() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.addReference();
    tex.removeReference();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void multipleAddReference_allCounted() {
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
  public void removeReference_atZero_logsButNoException() {
    TestableGlrTexture tex = new TestableGlrTexture();
    // Should log a severe message but not throw
    tex.removeReference();
    assertFalse(tex.isReferenced());
  }

  @Test
  public void refCount_value_afterMultipleAdds() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.addReference();
    tex.addReference();
    tex.addReference();
    Field f = GlrTexture.class.getDeclaredField("refCount");
    f.setAccessible(true);
    assertEquals(3, f.getInt(tex));
  }

  // ── Render context list ───────────────────────────────────────────

  @Test
  public void addRenderContext_increases() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    RenderContext rc = new RenderContext();
    tex.addRenderContext(rc);
    List<?> list = getRenderContexts(tex);
    assertEquals(1, list.size());
  }

  @Test
  public void removeRenderContext_decreases() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    RenderContext rc = new RenderContext();
    tex.addRenderContext(rc);
    tex.removeRenderContext(rc);
    List<?> list = getRenderContexts(tex);
    assertTrue(list.isEmpty());
  }

  @Test
  public void multipleRenderContexts_tracked() throws Exception {
    TestableGlrTexture tex = new TestableGlrTexture();
    RenderContext rc1 = new RenderContext();
    RenderContext rc2 = new RenderContext();
    tex.addRenderContext(rc1);
    tex.addRenderContext(rc2);
    List<?> list = getRenderContexts(tex);
    assertEquals(2, list.size());
  }

  // ── Dirty flag ────────────────────────────────────────────────────

  @Test
  public void isDirty_initiallyTrue() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertTrue(tex.callIsDirty());
  }

  @Test
  public void setDirty_false_clearsDirty() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.callSetDirty(false);
    assertFalse(tex.callIsDirty());
  }

  @Test
  public void setDirty_true_setsDirty() {
    TestableGlrTexture tex = new TestableGlrTexture();
    tex.callSetDirty(false);
    tex.callSetDirty(true);
    assertTrue(tex.callIsDirty());
  }

  // ── mapU / mapV identity ──────────────────────────────────────────

  @Test
  public void mapU_identity() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.0f, tex.mapU(0.0f), 0.0001f);
    assertEquals(0.5f, tex.mapU(0.5f), 0.0001f);
    assertEquals(1.0f, tex.mapU(1.0f), 0.0001f);
  }

  @Test
  public void mapV_identity() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(0.0f, tex.mapV(0.0f), 0.0001f);
    assertEquals(0.5f, tex.mapV(0.5f), 0.0001f);
    assertEquals(1.0f, tex.mapV(1.0f), 0.0001f);
  }

  @Test
  public void mapU_negativeValue_passesThrough() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(-0.5f, tex.mapU(-0.5f), 0.0001f);
  }

  @Test
  public void mapV_greaterThanOne_passesThrough() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertEquals(2.5f, tex.mapV(2.5f), 0.0001f);
  }

  // ── isValid with null owner ───────────────────────────────────────

  @Test
  public void isValid_nullOwner_returnsFalse() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertFalse(tex.isValid());
  }

  // ── getOwner ─────────────────────────────────────────────────────

  @Test
  public void getOwner_initiallyNull() {
    TestableGlrTexture tex = new TestableGlrTexture();
    assertNull(tex.getOwner());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  @SuppressWarnings("unchecked")
  private static List<?> getRenderContexts(GlrTexture<?> tex) throws Exception {
    Field f = GlrTexture.class.getDeclaredField("renderContexts");
    f.setAccessible(true);
    return (List<?>) f.get(tex);
  }

  /**
   * Minimal concrete subclass for testing abstract GlrTexture.
   */
  private static class TestableGlrTexture extends GlrTexture {
    @Override
    protected com.jogamp.opengl.util.texture.TextureData newTextureData(
        com.jogamp.opengl.GL gl, com.jogamp.opengl.util.texture.TextureData currentTexture) {
      return null;
    }

    boolean callIsDirty() {
      return isDirty();
    }

    void callSetDirty(boolean dirty) {
      setDirty(dirty);
    }
  }
}
