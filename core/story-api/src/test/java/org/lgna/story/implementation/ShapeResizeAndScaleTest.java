package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.SBox;
import org.lgna.story.SSphere;
import org.lgna.story.STorus;

import static org.junit.Assert.*;

/**
 * Tests for {@link BoxImp}, {@link SphereImp}, and {@link TorusImp}
 * focusing on per-axis setValueForResizer, setSize round-trip,
 * geometry type assertions, and uniform scale.
 *
 * <p>Extends coverage beyond {@link ShapeImpBehaviorTest} which covers
 * basic construction, paint, opacity, and simple resize.
 */
public class ShapeResizeAndScaleTest {

  // ══════════════════════════════════════════════════════════════════════
  //  BoxImp — per-axis resize
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void boxImp_setValueForResizer_xAxis_updatesOnlyX() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    double originalY = imp.getValueForResizer(Resizer.Y_AXIS);
    double originalZ = imp.getValueForResizer(Resizer.Z_AXIS);
    imp.setValueForResizer(Resizer.X_AXIS, 5.0);
    assertEquals(5.0, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
    assertEquals(originalY, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
    assertEquals(originalZ, imp.getValueForResizer(Resizer.Z_AXIS), 1e-9);
  }

  @Test
  public void boxImp_setValueForResizer_yAxis_updatesOnlyY() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    double originalX = imp.getValueForResizer(Resizer.X_AXIS);
    double originalZ = imp.getValueForResizer(Resizer.Z_AXIS);
    imp.setValueForResizer(Resizer.Y_AXIS, 7.0);
    assertEquals(7.0, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
    assertEquals(originalX, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
    assertEquals(originalZ, imp.getValueForResizer(Resizer.Z_AXIS), 1e-9);
  }

  @Test
  public void boxImp_setValueForResizer_zAxis_updatesOnlyZ() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    double originalX = imp.getValueForResizer(Resizer.X_AXIS);
    double originalY = imp.getValueForResizer(Resizer.Y_AXIS);
    imp.setValueForResizer(Resizer.Z_AXIS, 9.0);
    assertEquals(9.0, imp.getValueForResizer(Resizer.Z_AXIS), 1e-9);
    assertEquals(originalX, imp.getValueForResizer(Resizer.X_AXIS), 1e-9);
    assertEquals(originalY, imp.getValueForResizer(Resizer.Y_AXIS), 1e-9);
  }

  @Test
  public void boxImp_setSize_roundTrip() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setSize(new Dimension3(3.0, 4.0, 5.0));
    Dimension3 size = imp.getSize();
    assertEquals(3.0, size.x(), 1e-6);
    assertEquals(4.0, size.y(), 1e-6);
    assertEquals(5.0, size.z(), 1e-6);
  }

  @Test
  public void boxImp_geometryType() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    assertNotNull(imp.getGeometry());
    assertTrue(imp.getGeometry() instanceof edu.cmu.cs.dennisc.scenegraph.Box);
  }

  @Test
  public void boxImp_hasFourResizers() {
    SBox box = new SBox();
    assertEquals(4, box.getImplementation().getResizers().length);
  }

  @Test
  public void boxImp_uniformResizer_scalesAllAxes() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 3.0);
    assertEquals(3.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void boxImp_scaleProperties_notEmpty() {
    SBox box = new SBox();
    assertTrue(box.getImplementation().getScaleProperties().length > 0);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SphereImp — radius property and resize
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void sphereImp_radiusProperty_setValue() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.radius.setValue(3.0);
    assertEquals(3.0, imp.radius.getValue(), 1e-9);
  }

  @Test
  public void sphereImp_radiusProperty_defaultPositive() {
    SSphere sphere = new SSphere();
    assertTrue(sphere.getImplementation().radius.getValue() > 0);
  }

  @Test
  public void sphereImp_getCollisionHull_notNull() {
    SSphere sphere = new SSphere();
    assertNotNull(sphere.getImplementation().getCollisionHull());
  }

  @Test
  public void sphereImp_geometryType() {
    SSphere sphere = new SSphere();
    assertNotNull(sphere.getImplementation().getGeometry());
    assertTrue(sphere.getImplementation().getGeometry() instanceof edu.cmu.cs.dennisc.scenegraph.Sphere);
  }

  @Test
  public void sphereImp_setSize_roundTrip() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.setSize(new Dimension3(4.0, 4.0, 4.0));
    Dimension3 size = imp.getSize();
    assertNotNull(size);
  }

  @Test
  public void sphereImp_uniformResizer() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 5.0);
    assertEquals(5.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void sphereImp_scaleProperties_notEmpty() {
    SSphere sphere = new SSphere();
    assertTrue(sphere.getImplementation().getScaleProperties().length > 0);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  TorusImp — inner/outer radius properties
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void torusImp_innerRadius_setValue() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.innerRadius.setValue(0.5);
    assertEquals(0.5, imp.innerRadius.getValue(), 1e-9);
  }

  @Test
  public void torusImp_outerRadius_setValue() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.outerRadius.setValue(2.0);
    assertEquals(2.0, imp.outerRadius.getValue(), 1e-9);
  }

  @Test
  public void torusImp_innerRadius_doesNotAffectOuterRadius_viaProperty() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    double outerBefore = imp.outerRadius.getValue();
    imp.innerRadius.setValue(0.3);
    assertEquals(outerBefore, imp.outerRadius.getValue(), 1e-9);
  }

  @Test
  public void torusImp_outerRadius_doesNotAffectInnerRadius() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    double innerBefore = imp.innerRadius.getValue();
    imp.outerRadius.setValue(3.0);
    assertEquals(3.0, imp.outerRadius.getValue(), 1e-9);
    assertEquals(innerBefore, imp.innerRadius.getValue(), 1e-9);
  }

  @Test
  public void torusImp_getCollisionHull_notNull() {
    STorus torus = new STorus();
    assertNotNull(torus.getImplementation().getCollisionHull());
  }

  @Test
  public void torusImp_geometryType() {
    STorus torus = new STorus();
    assertNotNull(torus.getImplementation().getGeometry());
    assertTrue(torus.getImplementation().getGeometry() instanceof edu.cmu.cs.dennisc.scenegraph.Torus);
  }

  @Test
  public void torusImp_setSize_doesNotThrow() {
    STorus torus = new STorus();
    torus.getImplementation().setSize(new Dimension3(3.0, 1.0, 3.0));
  }

  @Test
  public void torusImp_setValueForResizer_xzPlane() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.setValueForResizer(Resizer.XZ_PLANE, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.XZ_PLANE), 1e-6);
  }

  @Test
  public void torusImp_setValueForResizer_yAxis() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 1.5);
    assertEquals(1.5, imp.getValueForResizer(Resizer.Y_AXIS), 1e-6);
  }

  @Test
  public void torusImp_setValueForResizer_uniform() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.5);
    assertEquals(2.5, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void torusImp_scaleProperties_notEmpty() {
    STorus torus = new STorus();
    assertTrue(torus.getImplementation().getScaleProperties().length > 0);
  }
}
