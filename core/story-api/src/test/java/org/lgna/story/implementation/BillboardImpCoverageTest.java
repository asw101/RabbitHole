package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.SBillboard;

import static org.junit.Assert.*;

/**
 * Tests for {@link BillboardImp} via {@link SBillboard}.
 *
 * <p>Covers front paint, back paint property, paint appearances,
 * opacity appearances, resize operations, and visual structure.
 * Extends coverage beyond {@link MoreImplBehaviorTest} which covers
 * basic construction and resizer set/get.
 */
public class BillboardImpCoverageTest {

  @Test
  public void billboard_constructsWithoutError() {
    SBillboard bb = new SBillboard();
    assertNotNull(bb);
    assertNotNull(bb.getImplementation());
  }

  @Test
  public void billboard_abstractionRoundTrips() {
    SBillboard bb = new SBillboard();
    assertSame(bb, bb.getImplementation().getAbstraction());
  }

  @Test
  public void billboard_hasTwoVisuals_frontAndBack() {
    SBillboard bb = new SBillboard();
    Visual[] visuals = bb.getImplementation().getSgVisuals();
    assertNotNull(visuals);
    assertEquals(2, visuals.length);
  }

  @Test
  public void billboard_sgPaintAppearances_hasFrontOnly() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    TexturedAppearance[] appearances = imp.getSgPaintAppearances();
    assertNotNull(appearances);
    assertEquals(1, appearances.length);
  }

  @Test
  public void billboard_sgOpacityAppearances_hasBothFaces() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    SimpleAppearance[] appearances = imp.getSgOpacityAppearances();
    assertNotNull(appearances);
    assertEquals(2, appearances.length);
  }

  @Test
  public void billboard_backPaintProperty_notNull() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    assertNotNull(imp.backPaint);
  }

  @Test
  public void billboard_backPaint_setValue_doesNotThrow() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.backPaint.setValue(Color.RED);
  }

  @Test
  public void billboard_frontPaint_setValue_roundTrips() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.paint.setValue(Color.BLUE);
    assertEquals(Color.BLUE, imp.paint.getValue());
  }

  @Test
  public void billboard_resizers_hasThree() {
    SBillboard bb = new SBillboard();
    Resizer[] resizers = bb.getImplementation().getResizers();
    assertEquals(3, resizers.length);
  }

  @Test
  public void billboard_resizers_containsXY_X_Y() {
    SBillboard bb = new SBillboard();
    Resizer[] resizers = bb.getImplementation().getResizers();
    assertEquals(Resizer.XY_PLANE, resizers[0]);
    assertEquals(Resizer.X_AXIS, resizers[1]);
    assertEquals(Resizer.Y_AXIS, resizers[2]);
  }

  @Test
  public void billboard_setValueForResizer_xAxis_roundTrips() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setValueForResizer(Resizer.X_AXIS, 2.5);
    assertEquals(2.5, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_yAxis_roundTrips() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 3.5);
    assertEquals(3.5, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_xyPlane_scalesProportionally() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setValueForResizer(Resizer.X_AXIS, 1.0);
    imp.setValueForResizer(Resizer.Y_AXIS, 1.0);
    imp.setValueForResizer(Resizer.XY_PLANE, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.XY_PLANE), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_zero_isIgnored() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    double before = imp.getValueForResizer(Resizer.X_AXIS);
    imp.setValueForResizer(Resizer.X_AXIS, 0.0);
    assertEquals(before, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_negative_isIgnored() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    double before = imp.getValueForResizer(Resizer.Y_AXIS);
    imp.setValueForResizer(Resizer.Y_AXIS, -1.0);
    assertEquals(before, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
  }

  @Test
  public void billboard_setSize_doesNotThrow() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setSize(new Dimension3(2.0, 3.0, 0.01));
  }

  @Test
  public void billboard_opacity_defaultIsOne() {
    SBillboard bb = new SBillboard();
    assertEquals(1.0f, bb.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void billboard_opacity_roundTrips() {
    SBillboard bb = new SBillboard();
    bb.getImplementation().opacity.setValue(0.5f);
    assertEquals(0.5f, bb.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void billboard_sgCompositeNotNull() {
    SBillboard bb = new SBillboard();
    assertNotNull(bb.getImplementation().getSgComposite());
  }

  @Test
  public void billboard_initialScaleIsUnit() {
    SBillboard bb = new SBillboard();
    Dimension3 scale = bb.getImplementation().getScale();
    assertEquals(1.0, scale.x(), 1e-6);
    assertEquals(1.0, scale.y(), 1e-6);
  }

  @Test
  public void billboard_extendsVisualScaleModelImp() {
    assertTrue(VisualScaleModelImp.class.isAssignableFrom(BillboardImp.class));
  }

  @Test
  public void billboard_instanceRegistryWorks() {
    SBillboard bb = new SBillboard();
    Visual[] visuals = bb.getImplementation().getSgVisuals();
    EntityImp found = EntityImp.getInstance(visuals[0]);
    assertSame(bb.getImplementation(), found);
  }

  @Test
  public void billboard_getValueForResizer_allResizers_positive() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    for (Resizer r : imp.getResizers()) {
      assertTrue("Resizer " + r + " should return positive value",
          imp.getValueForResizer(r) > 0);
    }
  }

  @Test
  public void billboard_xAxis_independentOfYAxis() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setValueForResizer(Resizer.X_AXIS, 5.0);
    double y = imp.getValueForResizer(Resizer.Y_AXIS);
    imp.setValueForResizer(Resizer.X_AXIS, 10.0);
    assertEquals(y, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
  }

  @Test
  public void billboard_yAxis_independentOfXAxis() {
    SBillboard bb = new SBillboard();
    BillboardImp imp = bb.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 5.0);
    double x = imp.getValueForResizer(Resizer.X_AXIS);
    imp.setValueForResizer(Resizer.Y_AXIS, 10.0);
    assertEquals(x, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
  }
}
