package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the Context abstract base class — scaled count tracking,
 * push/pop scaled count stack, glu accessor, and setGL.
 * Uses {@link PickContext} as a concrete implementation since it
 * provides no-op normalize methods.
 */
public class ContextScaledCountTest {

  // ── isScaled ──────────────────────────────────────────────────────

  @Test
  public void isScaled_initiallyFalse() {
    PickContext ctx = new PickContext(true);
    assertFalse(ctx.isScaled());
  }

  // ── incrementScaledCount / decrementScaledCount ───────────────────

  @Test
  public void increment_once_isScaled() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    assertTrue(ctx.isScaled());
  }

  @Test
  public void increment_twice_stillScaled() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    assertTrue(ctx.isScaled());
  }

  @Test
  public void decrement_toZero_notScaled() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.decrementScaledCount();
    assertFalse(ctx.isScaled());
  }

  @Test
  public void increment_decrement_increment_isScaled() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.decrementScaledCount();
    ctx.incrementScaledCount();
    assertTrue(ctx.isScaled());
  }

  @Test
  public void multipleIncrements_partialDecrement_stillScaled() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    ctx.decrementScaledCount();
    assertTrue(ctx.isScaled());
  }

  // ── pushScaledCountAndSetToZero / popAndRestoreScaledCount ────────

  @Test
  public void pushAndPop_restoresScaledCount() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());
    ctx.popAndRestoreScaledCount();
    assertTrue(ctx.isScaled());
  }

  @Test
  public void push_setsToZero() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());
  }

  @Test
  public void nestedPush_restoresCorrectly() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());
    ctx.popAndRestoreScaledCount();
    assertTrue(ctx.isScaled());
    ctx.popAndRestoreScaledCount();
    assertTrue(ctx.isScaled());
  }

  @Test
  public void pushFromZero_popRestoresZero() {
    PickContext ctx = new PickContext(true);
    ctx.pushScaledCountAndSetToZero();
    assertFalse(ctx.isScaled());
    ctx.popAndRestoreScaledCount();
    assertFalse(ctx.isScaled());
  }

  // ── initialize ────────────────────────────────────────────────────

  @Test
  public void initialize_resetsScaledCount() {
    PickContext ctx = new PickContext(true);
    ctx.incrementScaledCount();
    ctx.incrementScaledCount();
    assertTrue(ctx.isScaled());
    ctx.initialize();
    assertFalse(ctx.isScaled());
  }

  // ── glu ───────────────────────────────────────────────────────────

  @Test
  public void glu_isNotNull() {
    PickContext ctx = new PickContext(true);
    assertNotNull(ctx.glu);
  }

  // ── setGL ─────────────────────────────────────────────────────────

  @Test
  public void setGL_null_noException() {
    PickContext ctx = new PickContext(true);
    ctx.setGL(null);
    assertNull(ctx.gl);
  }

  @Test
  public void gl_initiallyNull() {
    PickContext ctx = new PickContext(true);
    assertNull(ctx.gl);
  }
}
