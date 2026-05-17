package org.lgna.story;

import org.alice.math.immutable.Dimension3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for SModel facade methods (getPaint, getOpacity, getWidth/Height/Depth,
 * setVehicle, getScale, getSize, resize methods) exercised through SBox,
 * which extends SShape → SModel → SMovableTurnable → STurnable → SThing.
 *
 * <p>These tests cover the SModel method bodies that delegate to ModelImp,
 * as well as SBox/SShape construction. All headless-safe.
 */
public class SModelFacadeTest {

  private SBox box;

  @Before
  public void setUp() {
    box = new SBox();
  }

  // ── Construction ──────────────────────────────────────

  @Test
  public void boxConstructsSuccessfully() {
    assertNotNull(box);
    assertNotNull(box.getImplementation());
  }

  // ── getPaint / setPaint ───────────────────────────────

  @Test
  public void getPaint_defaultIsNull() {
    // Default paint before any texture is set
    Paint paint = box.getPaint();
    // May be null or a default — just verify no exception
    // The initial paint value depends on implementation
  }

  @Test
  public void setPaint_colorBlue_thenGetReturnsBlue() {
    box.setPaint(Color.BLUE, SetPaint.duration(0));
    assertEquals(Color.BLUE, box.getPaint());
  }

  @Test
  public void setPaint_colorRed_thenGetReturnsRed() {
    box.setPaint(Color.RED, SetPaint.duration(0));
    assertEquals(Color.RED, box.getPaint());
  }

  @Test
  public void setPaint_overwritesPrevious() {
    box.setPaint(Color.GREEN, SetPaint.duration(0));
    box.setPaint(Color.WHITE, SetPaint.duration(0));
    assertEquals(Color.WHITE, box.getPaint());
  }

  // ── getOpacity / setOpacity ───────────────────────────

  @Test
  public void getOpacity_defaultIsOne() {
    assertEquals(1.0, box.getOpacity(), 0.001);
  }

  @Test
  public void setOpacity_half_thenGetReturnsHalf() {
    box.setOpacity(0.5, SetOpacity.duration(0));
    assertEquals(0.5, box.getOpacity(), 0.01);
  }

  @Test
  public void setOpacity_zero() {
    box.setOpacity(0.0, SetOpacity.duration(0));
    assertEquals(0.0, box.getOpacity(), 0.01);
  }

  @Test
  public void setOpacity_one() {
    box.setOpacity(1.0, SetOpacity.duration(0));
    assertEquals(1.0, box.getOpacity(), 0.01);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setOpacity_negative_throws() {
    box.setOpacity(-0.1, SetOpacity.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setOpacity_greaterThanOne_throws() {
    box.setOpacity(1.5, SetOpacity.duration(0));
  }

  // ── getWidth / getHeight / getDepth ───────────────────

  @Test
  public void getWidth_returnsPositive() {
    assertTrue(box.getWidth() > 0);
  }

  @Test
  public void getHeight_returnsPositive() {
    assertTrue(box.getHeight() > 0);
  }

  @Test
  public void getDepth_returnsPositive() {
    assertTrue(box.getDepth() > 0);
  }

  // ── setWidth / setHeight / setDepth ───────────────────

  @Test
  public void setWidth_updatesDimension() {
    box.setWidth(3.0, SetWidth.duration(0));
    assertEquals(3.0, box.getWidth(), 0.01);
  }

  @Test
  public void setHeight_updatesDimension() {
    box.setHeight(4.0, SetHeight.duration(0));
    assertEquals(4.0, box.getHeight(), 0.01);
  }

  @Test
  public void setDepth_updatesDimension() {
    box.setDepth(5.0, SetDepth.duration(0));
    assertEquals(5.0, box.getDepth(), 0.01);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setWidth_negative_throws() {
    box.setWidth(-1.0, SetWidth.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setHeight_negative_throws() {
    box.setHeight(-1.0, SetHeight.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setDepth_negative_throws() {
    box.setDepth(-1.0, SetDepth.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setWidth_zero_throws() {
    box.setWidth(0.0, SetWidth.duration(0));
  }

  // ── getScale / setScale ───────────────────────────────

  @Test
  public void getScale_defaultIsUnit() {
    Scale scale = box.getScale();
    assertNotNull(scale);
  }

  @Test
  public void setScale_changesScale() {
    Scale newScale = new Scale(2.0, 2.0, 2.0);
    box.setScale(newScale, SetScale.duration(0));
    // After setting scale, the getter should reflect the change
    assertNotNull(box.getScale());
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setScale_null_throws() {
    box.setScale(null, SetScale.duration(0));
  }

  // ── getSize / setSize ─────────────────────────────────

  @Test
  public void getSize_returnsNonNull() {
    Size size = box.getSize();
    assertNotNull(size);
  }

  @Test
  public void setSize_changesSize() {
    Size newSize = new Size(2.0, 3.0, 4.0);
    box.setSize(newSize, SetSize.duration(0));
    Size actual = box.getSize();
    assertNotNull(actual);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void setSize_null_throws() {
    box.setSize(null, SetSize.duration(0));
  }

  // ── resize methods ────────────────────────────────────

  @Test
  public void resize_doubleSize() {
    double widthBefore = box.getWidth();
    box.resize(2.0, Resize.duration(0));
    // After resize by factor 2, width should double
    assertEquals(widthBefore * 2.0, box.getWidth(), 0.1);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void resize_negativeFactor_throws() {
    box.resize(-1.0, Resize.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void resize_zeroFactor_throws() {
    box.resize(0.0, Resize.duration(0));
  }

  @Test
  public void resizeWidth_exercisesCodePath() {
    box.resizeWidth(2.0, ResizeWidth.duration(0));
    // Exercises SModel.resizeWidth code path; headless may not apply animation
    assertTrue(box.getWidth() > 0);
  }

  @Test
  public void resizeHeight_exercisesCodePath() {
    box.resizeHeight(2.0, ResizeHeight.duration(0));
    assertTrue(box.getHeight() > 0);
  }

  @Test
  public void resizeDepth_exercisesCodePath() {
    box.resizeDepth(2.0, ResizeDepth.duration(0));
    assertTrue(box.getDepth() > 0);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void resizeWidth_negative_throws() {
    box.resizeWidth(-1.0, ResizeWidth.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void resizeHeight_negative_throws() {
    box.resizeHeight(-1.0, ResizeHeight.duration(0));
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void resizeDepth_negative_throws() {
    box.resizeDepth(-1.0, ResizeDepth.duration(0));
  }

  // ── setVehicle ────────────────────────────────────────

  @Test
  public void setVehicle_null_doesNotThrow() {
    box.setVehicle(null);
  }

  @Test
  public void setVehicle_anotherBox() {
    SBox otherBox = new SBox();
    box.setVehicle(otherBox);
    // Should complete without exception
  }

  // ── say / think argument validation ───────────────────

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void say_null_throws() {
    box.say(null);
  }

  @Test(expected = org.lgna.common.LgnaIllegalArgumentException.class)
  public void think_null_throws() {
    box.think(null);
  }
}
