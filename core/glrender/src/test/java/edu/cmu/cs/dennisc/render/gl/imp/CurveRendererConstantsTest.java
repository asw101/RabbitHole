package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link CurveRenderer} static constants and constructor.
 * All rendering methods require GL context; only constants are testable headlessly.
 */
public class CurveRendererConstantsTest {

  @Test
  public void constructor_noException() {
    CurveRenderer cr = new CurveRenderer();
    assertNotNull(cr);
  }

  @Test
  public void stackCount_isFifty() throws Exception {
    assertEquals(50, getStaticInt("STACK_COUNT"));
  }

  @Test
  public void sliceCount_isFifty() throws Exception {
    assertEquals(50, getStaticInt("SLICE_COUNT"));
  }

  @Test
  public void sideCount_isFifty() throws Exception {
    assertEquals(50, getStaticInt("SIDE_COUNT"));
  }

  @Test
  public void ringCount_isFifty() throws Exception {
    assertEquals(50, getStaticInt("RING_COUNT"));
  }

  @Test
  public void tau_isTwoPi() throws Exception {
    float tau = getStaticFloat("TAU");
    assertEquals(2f * (float) Math.PI, tau, 0.001f);
  }

  @Test
  public void tau_isPositive() throws Exception {
    assertTrue(getStaticFloat("TAU") > 0);
  }

  @Test
  public void twoInstances_independent() {
    CurveRenderer a = new CurveRenderer();
    CurveRenderer b = new CurveRenderer();
    assertNotSame(a, b);
  }

  // ── Reflection helpers ──────────────────────────────────────────────

  private static int getStaticInt(String name) throws Exception {
    Field f = CurveRenderer.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.getInt(null);
  }

  private static float getStaticFloat(String name) throws Exception {
    Field f = CurveRenderer.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.getFloat(null);
  }
}
