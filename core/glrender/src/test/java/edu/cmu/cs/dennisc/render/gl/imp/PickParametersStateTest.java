package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.Assert.*;

/**
 * Tests for {@link PickParameters} — constructor, getters, pick result
 * accumulation, and front-most result logic.
 */
public class PickParametersStateTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_noException() {
    PickParameters pp = new PickParameters(null, null, new Point(10, 20), true, null);
    assertNotNull(pp);
  }

  // ── getRenderTarget ───────────────────────────────────────────────

  @Test
  public void getRenderTarget_null_returnsNull() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertNull(pp.getRenderTarget());
  }

  // ── getSGCamera ───────────────────────────────────────────────────

  @Test
  public void getSGCamera_null_returnsNull() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertNull(pp.getSGCamera());
  }

  // ── getX ──────────────────────────────────────────────────────────

  @Test
  public void getX_returnsMouseX() {
    PickParameters pp = new PickParameters(null, null, new Point(42, 99), false, null);
    assertEquals(42, pp.getX());
  }

  @Test
  public void getX_zeroPoint() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertEquals(0, pp.getX());
  }

  @Test
  public void getX_negativePoint() {
    PickParameters pp = new PickParameters(null, null, new Point(-5, 0), false, null);
    assertEquals(-5, pp.getX());
  }

  // ── getFlippedY ───────────────────────────────────────────────────

  @Test
  public void getFlippedY_atOrigin() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    assertEquals(600, pp.getFlippedY(vp));
  }

  @Test
  public void getFlippedY_atBottom() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 600), false, null);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    assertEquals(0, pp.getFlippedY(vp));
  }

  @Test
  public void getFlippedY_midScreen() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 300), false, null);
    Rectangle vp = new Rectangle(0, 0, 800, 600);
    assertEquals(300, pp.getFlippedY(vp));
  }

  @Test
  public void getFlippedY_nonZeroViewport() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 50), false, null);
    Rectangle vp = new Rectangle(10, 20, 400, 300);
    assertEquals(250, pp.getFlippedY(vp));
  }

  // ── isSubElementRequired ──────────────────────────────────────────

  @Test
  public void isSubElementRequired_true() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), true, null);
    assertTrue(pp.isSubElementRequired());
  }

  @Test
  public void isSubElementRequired_false() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertFalse(pp.isSubElementRequired());
  }

  // ── getPickObserver ───────────────────────────────────────────────

  @Test
  public void getPickObserver_null() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertNull(pp.getPickObserver());
  }

  // ── accessAllPickResults ──────────────────────────────────────────

  @Test
  public void pickResults_initiallyEmpty() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertTrue(pp.accessAllPickResults().isEmpty());
  }

  @Test
  public void addPickResult_increasesSize() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    pp.addPickResult(null, null, true, null, 0, org.alice.math.immutable.Point3.ORIGIN);
    assertEquals(1, pp.accessAllPickResults().size());
  }

  @Test
  public void addMultiplePickResults_allTracked() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    for (int i = 0; i < 5; i++) {
      pp.addPickResult(null, null, true, null, i, org.alice.math.immutable.Point3.ORIGIN);
    }
    assertEquals(5, pp.accessAllPickResults().size());
  }

  // ── accessFrontMostPickResult ─────────────────────────────────────

  @Test
  public void frontMostPickResult_emptyList_returnsCameraResult() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    assertNotNull(pp.accessFrontMostPickResult());
  }

  @Test
  public void frontMostPickResult_withResults_returnsFirst() {
    PickParameters pp = new PickParameters(null, null, new Point(0, 0), false, null);
    pp.addPickResult(null, null, true, null, 42, org.alice.math.immutable.Point3.ORIGIN);
    pp.addPickResult(null, null, false, null, 99, org.alice.math.immutable.Point3.ORIGIN);
    assertNotNull(pp.accessFrontMostPickResult());
  }
}
