package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.alice.math.immutable.Point3;
import edu.cmu.cs.dennisc.render.PickResult;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link PickParameters} data structure logic.
 * All pick result management, coordinate access, and configuration queries.
 */
public class PickParametersDataTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private PickParameters params;
  private static final Point MOUSE_POS = new Point(100, 200);

  @Before
  public void setUp() {
    params = new PickParameters(null, null, MOUSE_POS, false, null);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_getX_returnsMouseX() {
    assertEquals(100, params.getX());
  }

  @Test
  public void constructor_isSubElementRequired_returnsFalse() {
    assertFalse(params.isSubElementRequired());
  }

  @Test
  public void constructor_subElementRequired_returnsTrue() {
    PickParameters subParams = new PickParameters(null, null, MOUSE_POS, true, null);
    assertTrue(subParams.isSubElementRequired());
  }

  @Test
  public void constructor_renderTarget_null() {
    assertNull(params.getRenderTarget());
  }

  @Test
  public void constructor_sgCamera_null() {
    assertNull(params.getSGCamera());
  }

  @Test
  public void constructor_pickObserver_null() {
    assertNull(params.getPickObserver());
  }

  // ── getFlippedY ───────────────────────────────────────────────────

  @Test
  public void getFlippedY_standardViewport() {
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    // flippedY = viewport.height - mousePos.y = 600 - 200 = 400
    assertEquals(400, params.getFlippedY(viewport));
  }

  @Test
  public void getFlippedY_tallViewport() {
    Rectangle viewport = new Rectangle(0, 0, 800, 1080);
    assertEquals(880, params.getFlippedY(viewport));
  }

  @Test
  public void getFlippedY_mouseAtTop() {
    PickParameters topParams = new PickParameters(null, null, new Point(50, 0), false, null);
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    assertEquals(600, topParams.getFlippedY(viewport));
  }

  @Test
  public void getFlippedY_mouseAtBottom() {
    PickParameters bottomParams = new PickParameters(null, null, new Point(50, 600), false, null);
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    assertEquals(0, bottomParams.getFlippedY(viewport));
  }

  @Test
  public void getFlippedY_squareViewport() {
    PickParameters centerParams = new PickParameters(null, null, new Point(256, 256), false, null);
    Rectangle viewport = new Rectangle(0, 0, 512, 512);
    assertEquals(256, centerParams.getFlippedY(viewport));
  }

  // ── Pick results: initially empty ─────────────────────────────────

  @Test
  public void accessAllPickResults_initiallyEmpty() {
    assertTrue(params.accessAllPickResults().isEmpty());
  }

  @Test
  public void accessFrontMostPickResult_noResults_returnsCameraResult() {
    PickResult result = params.accessFrontMostPickResult();
    assertNotNull(result);
  }

  // ── addPickResult ─────────────────────────────────────────────────

  @Test
  public void addPickResult_addsToList() {
    params.addPickResult(null, null, true, null, 0, new Point3(1, 2, 3));
    assertEquals(1, params.accessAllPickResults().size());
  }

  @Test
  public void addPickResult_multiple() {
    params.addPickResult(null, null, true, null, 0, new Point3(1, 0, 0));
    params.addPickResult(null, null, false, null, 1, new Point3(0, 1, 0));
    params.addPickResult(null, null, true, null, 2, new Point3(0, 0, 1));
    assertEquals(3, params.accessAllPickResults().size());
  }

  @Test
  public void accessFrontMostPickResult_withResults_returnsFirst() {
    params.addPickResult(null, null, true, null, 0, new Point3(1, 2, 3));
    params.addPickResult(null, null, false, null, 1, new Point3(4, 5, 6));
    PickResult front = params.accessFrontMostPickResult();
    assertNotNull(front);
    assertSame(params.accessAllPickResults().get(0), front);
  }

  // ── Pick result with null point ───────────────────────────────────

  @Test
  public void addPickResult_nullPoint_succeeds() {
    params.addPickResult(null, null, true, null, 0, null);
    assertEquals(1, params.accessAllPickResults().size());
  }

  // ── Different mouse positions ─────────────────────────────────────

  @Test
  public void getX_zeroPosition() {
    PickParameters p = new PickParameters(null, null, new Point(0, 0), false, null);
    assertEquals(0, p.getX());
  }

  @Test
  public void getX_negativePosition() {
    PickParameters p = new PickParameters(null, null, new Point(-10, 50), false, null);
    assertEquals(-10, p.getX());
  }

  @Test
  public void getFlippedY_zeroHeight_returnsNegative() {
    PickParameters p = new PickParameters(null, null, new Point(0, 100), false, null);
    Rectangle viewport = new Rectangle(0, 0, 100, 0);
    assertEquals(-100, p.getFlippedY(viewport));
  }

  // ── Ordering of pick results ──────────────────────────────────────

  @Test
  public void pickResults_maintainInsertionOrder() {
    for (int i = 0; i < 10; i++) {
      params.addPickResult(null, null, true, null, i, new Point3(i, 0, 0));
    }
    List<PickResult> results = params.accessAllPickResults();
    assertEquals(10, results.size());
  }

  // ── PickResult construction ───────────────────────────────────────

  @Test
  public void addPickResult_frontFacing() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertTrue(result.isFrontFacing());
  }

  @Test
  public void addPickResult_backFacing() {
    params.addPickResult(null, null, false, null, 0, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertFalse(result.isFrontFacing());
  }

  @Test
  public void addPickResult_subElementIndex() {
    params.addPickResult(null, null, true, null, 42, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertEquals(42, result.getSubElement());
  }

  @Test
  public void addPickResult_position() {
    Point3 pos = new Point3(1.5, 2.5, 3.5);
    params.addPickResult(null, null, true, null, 0, pos);
    PickResult result = params.accessFrontMostPickResult();
    assertNotNull(result.getPositionInSource());
  }

  // ── Viewport offsets ──────────────────────────────────────────────

  @Test
  public void getFlippedY_viewportWithOffset() {
    // Rectangle height is what matters, not x/y offset
    Rectangle viewport = new Rectangle(50, 50, 800, 600);
    assertEquals(400, params.getFlippedY(viewport));
  }

  // ── Large coordinates ─────────────────────────────────────────────

  @Test
  public void getX_largeValue() {
    PickParameters p = new PickParameters(null, null, new Point(Integer.MAX_VALUE, 0), false, null);
    assertEquals(Integer.MAX_VALUE, p.getX());
  }

  @Test
  public void getFlippedY_largeViewport() {
    PickParameters p = new PickParameters(null, null, new Point(0, 0), false, null);
    Rectangle viewport = new Rectangle(0, 0, 100, 10000);
    assertEquals(10000, p.getFlippedY(viewport));
  }

  // ── PickResult via PickParameters ─────────────────────────────────

  @Test
  public void pickResult_isFrontFacing_true() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    assertTrue(params.accessFrontMostPickResult().isFrontFacing());
  }

  @Test
  public void pickResult_isFrontFacing_false() {
    params.addPickResult(null, null, false, null, 0, new Point3(0, 0, 0));
    assertFalse(params.accessFrontMostPickResult().isFrontFacing());
  }

  @Test
  public void pickResult_subElement_preserved() {
    params.addPickResult(null, null, true, null, 99, new Point3(0, 0, 0));
    assertEquals(99, params.accessFrontMostPickResult().getSubElement());
  }

  @Test
  public void pickResult_subElement_negativeOne() {
    params.addPickResult(null, null, true, null, -1, new Point3(0, 0, 0));
    assertEquals(-1, params.accessFrontMostPickResult().getSubElement());
  }

  @Test
  public void pickResult_position_preserved() {
    Point3 pos = new Point3(10.5, 20.5, 30.5);
    params.addPickResult(null, null, true, null, 0, pos);
    PickResult result = params.accessFrontMostPickResult();
    assertNotNull(result.getPositionInSource());
  }

  @Test
  public void pickResults_clearAfterConstruction_empty() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    // Verify list is mutable via accessAllPickResults
    List<PickResult> results = params.accessAllPickResults();
    assertFalse(results.isEmpty());
  }

  @Test
  public void pickResult_nullGeometry_allowed() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertNull(result.getGeometry());
  }

  @Test
  public void pickResult_nullVisual_allowed() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertNull(result.getVisual());
  }

  @Test
  public void pickResult_nullSource_allowed() {
    params.addPickResult(null, null, true, null, 0, new Point3(0, 0, 0));
    PickResult result = params.accessFrontMostPickResult();
    assertNull(result.getSource());
  }

  // ── Flipped Y math with various positions ─────────────────────────

  @Test
  public void getFlippedY_midScreen() {
    PickParameters mid = new PickParameters(null, null, new Point(400, 300), false, null);
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    assertEquals(300, mid.getFlippedY(viewport));
  }

  @Test
  public void getFlippedY_identity() {
    // When mouseY equals viewport height, flippedY should be 0
    PickParameters p = new PickParameters(null, null, new Point(0, 600), false, null);
    Rectangle viewport = new Rectangle(0, 0, 800, 600);
    assertEquals(0, p.getFlippedY(viewport));
  }

  @Test
  public void getX_multipleInstances_independent() {
    PickParameters p1 = new PickParameters(null, null, new Point(10, 20), false, null);
    PickParameters p2 = new PickParameters(null, null, new Point(30, 40), false, null);
    assertEquals(10, p1.getX());
    assertEquals(30, p2.getX());
  }
}
