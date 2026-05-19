package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.SCone;
import org.lgna.story.SCylinder;
import org.lgna.story.SDisc;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConeImp} (via {@link SCone}), {@link CylinderImp} (via
 * {@link SCylinder}), and {@link DiscImp} (via {@link SDisc}).
 *
 * <p>Covers the AbstractCylinderImp hierarchy (Cone, Cylinder) and the
 * ShapeImp hierarchy (Disc), including radius/length properties,
 * resize operations, geometry, and collision hulls.
 */
public class CylinderHierarchyImpTest {

  // ══════════════════════════════════════════════════════════════════════
  //  SCone / ConeImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void cone_constructsWithoutError() {
    SCone cone = new SCone();
    assertNotNull(cone.getImplementation());
  }

  @Test
  public void cone_abstractionRoundTrips() {
    SCone cone = new SCone();
    assertSame(cone, cone.getImplementation().getAbstraction());
  }

  @Test
  public void cone_baseRadiusProperty_notNull() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    assertNotNull(imp.baseRadius);
  }

  @Test
  public void cone_baseRadius_defaultIsPositive() {
    SCone cone = new SCone();
    assertTrue(cone.getImplementation().baseRadius.getValue() > 0);
  }

  @Test
  public void cone_baseRadius_setValue_roundTrips() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    imp.baseRadius.setValue(2.5);
    assertEquals(2.5, imp.baseRadius.getValue(), 1e-9);
  }

  @Test
  public void cone_getCollisionHull_notNull() {
    SCone cone = new SCone();
    assertNotNull(cone.getImplementation().getCollisionHull());
  }

  @Test
  public void cone_hasResizers() {
    SCone cone = new SCone();
    Resizer[] resizers = cone.getImplementation().getResizers();
    assertNotNull(resizers);
    assertTrue(resizers.length > 0);
  }

  @Test
  public void cone_setValueForResizer_uniform() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 3.0);
    assertEquals(3.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void cone_setValueForResizer_xzPlane() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    imp.setValueForResizer(Resizer.XZ_PLANE, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.XZ_PLANE), 1e-6);
  }

  @Test
  public void cone_setValueForResizer_yAxis() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 4.0);
    assertEquals(4.0, imp.getValueForResizer(Resizer.Y_AXIS), 1e-6);
  }

  @Test
  public void cone_hasOneVisual() {
    SCone cone = new SCone();
    assertEquals(1, cone.getImplementation().getSgVisuals().length);
  }

  @Test
  public void cone_visualHasGeometry() {
    SCone cone = new SCone();
    Visual v = cone.getImplementation().getSgVisuals()[0];
    Geometry geom = v.getGeometry();
    assertNotNull(geom);
  }

  @Test
  public void cone_extendsAbstractCylinderImp() {
    assertTrue(AbstractCylinderImp.class.isAssignableFrom(ConeImp.class));
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SCylinder / CylinderImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void cylinder_constructsWithoutError() {
    SCylinder cyl = new SCylinder();
    assertNotNull(cyl.getImplementation());
  }

  @Test
  public void cylinder_abstractionRoundTrips() {
    SCylinder cyl = new SCylinder();
    assertSame(cyl, cyl.getImplementation().getAbstraction());
  }

  @Test
  public void cylinder_radiusProperty_notNull() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    assertNotNull(imp.radius);
  }

  @Test
  public void cylinder_radius_defaultIsPositive() {
    SCylinder cyl = new SCylinder();
    assertTrue(cyl.getImplementation().radius.getValue() > 0);
  }

  @Test
  public void cylinder_radius_setValue_roundTrips() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.radius.setValue(3.0);
    assertEquals(3.0, imp.radius.getValue(), 1e-9);
  }

  @Test
  public void cylinder_getCollisionHull_notNull() {
    SCylinder cyl = new SCylinder();
    assertNotNull(cyl.getImplementation().getCollisionHull());
  }

  @Test
  public void cylinder_hasResizers() {
    SCylinder cyl = new SCylinder();
    assertTrue(cyl.getImplementation().getResizers().length > 0);
  }

  @Test
  public void cylinder_setValueForResizer_uniform() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void cylinder_setValueForResizer_xzPlane() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.setValueForResizer(Resizer.XZ_PLANE, 2.5);
    assertEquals(2.5, imp.getValueForResizer(Resizer.XZ_PLANE), 1e-6);
  }

  @Test
  public void cylinder_setValueForResizer_yAxis() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 5.0);
    assertEquals(5.0, imp.getValueForResizer(Resizer.Y_AXIS), 1e-6);
  }

  @Test
  public void cylinder_setSize_doesNotThrow() {
    SCylinder cyl = new SCylinder();
    cyl.getImplementation().setSize(new Dimension3(2.0, 3.0, 2.0));
  }

  @Test
  public void cylinder_extendsAbstractCylinderImp() {
    assertTrue(AbstractCylinderImp.class.isAssignableFrom(CylinderImp.class));
  }

  @Test
  public void cylinder_hasOneVisual() {
    SCylinder cyl = new SCylinder();
    assertEquals(1, cyl.getImplementation().getSgVisuals().length);
  }

  // ══════════════════════════════════════════════════════════════════════
  //  SDisc / DiscImp
  // ══════════════════════════════════════════════════════════════════════

  @Test
  public void disc_constructsWithoutError() {
    SDisc disc = new SDisc();
    assertNotNull(disc.getImplementation());
  }

  @Test
  public void disc_abstractionRoundTrips() {
    SDisc disc = new SDisc();
    assertSame(disc, disc.getImplementation().getAbstraction());
  }

  @Test
  public void disc_outerRadiusProperty_notNull() {
    SDisc disc = new SDisc();
    DiscImp imp = disc.getImplementation();
    assertNotNull(imp.outerRadius);
  }

  @Test
  public void disc_outerRadius_defaultIsPositive() {
    SDisc disc = new SDisc();
    assertTrue(disc.getImplementation().outerRadius.getValue() > 0);
  }

  @Test
  public void disc_outerRadius_setValue_roundTrips() {
    SDisc disc = new SDisc();
    DiscImp imp = disc.getImplementation();
    imp.outerRadius.setValue(4.0);
    assertEquals(4.0, imp.outerRadius.getValue(), 1e-9);
  }

  @Test
  public void disc_hasResizers() {
    SDisc disc = new SDisc();
    assertTrue(disc.getImplementation().getResizers().length > 0);
  }

  @Test
  public void disc_setValueForResizer_xzPlane() {
    SDisc disc = new SDisc();
    DiscImp imp = disc.getImplementation();
    imp.setValueForResizer(Resizer.XZ_PLANE, 3.0);
    assertEquals(3.0, imp.getValueForResizer(Resizer.XZ_PLANE), 1e-6);
  }

  @Test
  public void disc_setSize_doesNotThrow() {
    SDisc disc = new SDisc();
    disc.getImplementation().setSize(new Dimension3(4.0, 0.1, 4.0));
  }

  @Test
  public void disc_hasOneVisual() {
    SDisc disc = new SDisc();
    assertEquals(1, disc.getImplementation().getSgVisuals().length);
  }

  @Test
  public void disc_geometryNotNull() {
    SDisc disc = new SDisc();
    assertNotNull(disc.getImplementation().getGeometry());
  }

  @Test
  public void disc_getCollisionHull_notNull() {
    SDisc disc = new SDisc();
    assertNotNull(disc.getImplementation().getCollisionHull());
  }

  @Test
  public void disc_extendsShapeImp() {
    assertTrue(ShapeImp.class.isAssignableFrom(DiscImp.class));
  }

  @Test
  public void disc_scalePropertiesNotNull() {
    SDisc disc = new SDisc();
    assertNotNull(disc.getImplementation().getScaleProperties());
  }
}
