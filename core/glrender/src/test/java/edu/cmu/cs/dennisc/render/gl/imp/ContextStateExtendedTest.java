package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link Context} abstract base class — extended coverage of
 * GL field, glu field, scaled count invariants, and edge cases.
 */
public class ContextStateExtendedTest {

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void pickContext_synchronous_constructsCleanly() {
    PickContext ctx = new PickContext(true);
    assertNotNull(ctx);
  }

  @Test
  public void pickContext_asynchronous_constructsCleanly() {
    PickContext ctx = new PickContext(false);
    assertNotNull(ctx);
  }

  // ── gl field defaults ─────────────────────────────────────────────

  @Test
  public void gl_field_isPublic() throws Exception {
    Field f = Context.class.getDeclaredField("gl");
    assertTrue(java.lang.reflect.Modifier.isPublic(f.getModifiers()));
  }

  @Test
  public void glu_field_isPublic() throws Exception {
    Field f = Context.class.getDeclaredField("glu");
    assertTrue(java.lang.reflect.Modifier.isPublic(f.getModifiers()));
  }

  // ── setGL(null) ───────────────────────────────────────────────────

  @Test
  public void setGL_null_setsGlNull() {
    PickContext ctx = new PickContext(true);
    ctx.setGL(null);
    assertNull(ctx.gl);
  }

  @Test
  public void setGL_null_multiple_noException() {
    PickContext ctx = new PickContext(true);
    ctx.setGL(null);
    ctx.setGL(null);
    ctx.setGL(null);
    assertNull(ctx.gl);
  }

  // ── Scaled count edge cases ───────────────────────────────────────

  @Test
  public void scaledCount_manyIncrements() {
    PickContext ctx = new PickContext(true);
    for (int i = 0; i < 100; i++) {
      ctx.incrementScaledCount();
    }
    assertTrue(ctx.isScaled());
    for (int i = 0; i < 100; i++) {
      ctx.decrementScaledCount();
    }
    assertFalse(ctx.isScaled());
  }

  @Test
  public void pushPop_alternating() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());

    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());

    ctx.popAndRestoreScaledCount();
    assertTrue(ctx.isScaled());

    ctx.popAndRestoreScaledCount();
    assertTrue(ctx.isScaled());
  }

  // ── isTextureEnabled / isLightingEnabled abstract in Context ──────

  @Test
  public void pickContext_textureDisabled() {
    PickContext ctx = new PickContext(true);
    assertFalse(ctx.isTextureEnabled());
  }

  @Test
  public void pickContext_lightingDisabled() {
    PickContext ctx = new PickContext(true);
    assertFalse(ctx.isLightingEnabled());
  }

  @Test
  public void renderContext_textureDisabled() {
    RenderContext rc = new RenderContext();
    assertFalse(rc.isTextureEnabled());
  }

  @Test
  public void renderContext_lightingEnabled() {
    RenderContext rc = new RenderContext();
    assertTrue(rc.isLightingEnabled());
  }

  // ── initialize on RenderContext ───────────────────────────────────

  @Test
  public void renderContext_globalOpacity_afterConstruct() throws Exception {
    RenderContext rc = new RenderContext();
    Field f = RenderContext.class.getDeclaredField("globalOpacity");
    f.setAccessible(true);
    assertEquals(1.0f, f.getFloat(rc), 0.0001f);
  }

  @Test
  public void renderContext_clearRect_afterConstruct() throws Exception {
    RenderContext rc = new RenderContext();
    Field f = RenderContext.class.getDeclaredField("clearRect");
    f.setAccessible(true);
    java.awt.Rectangle rect = (java.awt.Rectangle) f.get(rc);
    assertEquals(0, rect.x);
    assertEquals(0, rect.y);
    assertEquals(0, rect.width);
    assertEquals(0, rect.height);
  }

  // ── glu is always present ─────────────────────────────────────────

  @Test
  public void pickContext_glu_notNull() {
    PickContext ctx = new PickContext(true);
    assertNotNull(ctx.glu);
  }

  @Test
  public void renderContext_glu_notNull() {
    RenderContext rc = new RenderContext();
    assertNotNull(rc.glu);
  }

  // ── Context.scaledCountStack internal via reflection ───────────────

  @Test
  public void scaledCountStack_exists() throws Exception {
    Field f = Context.class.getDeclaredField("scaledCountStack");
    f.setAccessible(true);
    PickContext ctx = new PickContext(true);
    assertNotNull(f.get(ctx));
  }

  @Test
  public void scaledCount_field_initiallyZero() throws Exception {
    Field f = Context.class.getDeclaredField("scaledCount");
    f.setAccessible(true);
    PickContext ctx = new PickContext(true);
    assertEquals(0, f.getInt(ctx));
  }
}
