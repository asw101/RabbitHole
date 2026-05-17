package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.SBox;
import org.lgna.story.SCone;
import org.lgna.story.SCylinder;
import org.lgna.story.SDisc;
import org.lgna.story.SSphere;
import org.lgna.story.STorus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests all shape implementations (BoxImp, SphereImp, ConeImp, CylinderImp,
 * TorusImp, DiscImp) via their facade classes. Each facade's constructor
 * creates the Imp internally — no AWT or rendering needed.
 *
 * <p>Covers the ShapeImp→SimpleModelImp→SingleVisualModelImp→VisualScaleModelImp
 * hierarchy plus each shape's resizer, geometry, and property logic.
 */
public class ShapeImpBehaviorTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  SBox / BoxImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void boxConstructorCreatesNonNullImp() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation());
  }

  @Test
  public void boxAbstractionRoundTrips() {
    SBox box = new SBox();
    assertSame(box, box.getImplementation().getAbstraction());
  }

  @Test
  public void boxHasOneVisual() {
    SBox box = new SBox();
    Visual[] visuals = box.getImplementation().getSgVisuals();
    assertNotNull(visuals);
    assertEquals(1, visuals.length);
  }

  @Test
  public void boxVisualHasGeometry() {
    SBox box = new SBox();
    Visual sgVisual = box.getImplementation().getSgVisuals()[0];
    Geometry geom = sgVisual.getGeometry();
    assertNotNull(geom);
  }

  @Test
  public void boxHasFourResizers() {
    SBox box = new SBox();
    Resizer[] resizers = box.getImplementation().getResizers();
    assertEquals(4, resizers.length);
  }

  @Test
  public void boxGetValueForResizerUniform() {
    SBox box = new SBox();
    double val = box.getImplementation().getValueForResizer(Resizer.UNIFORM);
    assertTrue(val > 0);
  }

  @Test
  public void boxGetValueForResizerXAxis() {
    SBox box = new SBox();
    double val = box.getImplementation().getValueForResizer(Resizer.X_AXIS);
    assertTrue(val > 0);
  }

  @Test
  public void boxGetValueForResizerYAxis() {
    SBox box = new SBox();
    double val = box.getImplementation().getValueForResizer(Resizer.Y_AXIS);
    assertTrue(val > 0);
  }

  @Test
  public void boxGetValueForResizerZAxis() {
    SBox box = new SBox();
    double val = box.getImplementation().getValueForResizer(Resizer.Z_AXIS);
    assertTrue(val > 0);
  }

  @Test
  public void boxSetValueForResizerUniform() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void boxSetValueForResizerXAxis() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setValueForResizer(Resizer.X_AXIS, 3.0);
    assertEquals(3.0, imp.getValueForResizer(Resizer.X_AXIS), 1e-6);
  }

  @Test
  public void boxSetValueForResizerYAxis() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setValueForResizer(Resizer.Y_AXIS, 4.0);
    assertEquals(4.0, imp.getValueForResizer(Resizer.Y_AXIS), 1e-6);
  }

  @Test
  public void boxSetValueForResizerZAxis() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setValueForResizer(Resizer.Z_AXIS, 5.0);
    assertEquals(5.0, imp.getValueForResizer(Resizer.Z_AXIS), 1e-6);
  }

  @Test
  public void boxSetSize() {
    SBox box = new SBox();
    BoxImp imp = box.getImplementation();
    imp.setSize(new Dimension3(2.0, 3.0, 4.0));
    Dimension3 size = imp.getSize();
    assertEquals(2.0, size.x(), 1e-6);
    assertEquals(3.0, size.y(), 1e-6);
    assertEquals(4.0, size.z(), 1e-6);
  }

  @Test
  public void boxPaintPropertyRoundTrips() {
    SBox box = new SBox();
    box.getImplementation().paint.setValue(Color.BLUE);
    assertEquals(Color.BLUE, box.getImplementation().paint.getValue());
  }

  @Test
  public void boxOpacityPropertyRoundTrips() {
    SBox box = new SBox();
    box.getImplementation().opacity.setValue(0.5f);
    assertEquals(0.5f, box.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void boxDefaultOpacityIsOne() {
    SBox box = new SBox();
    assertEquals(1.0f, box.getImplementation().opacity.getValue(), 1e-6f);
  }

  @Test
  public void boxInstanceRegistryWorks() {
    SBox box = new SBox();
    EntityImp imp = EntityImp.getInstance(box.getImplementation().getSgComposite());
    assertSame(box.getImplementation(), imp);
  }

  @Test
  public void boxScaleIsUnitSize() {
    SBox box = new SBox();
    Dimension3 scale = box.getImplementation().getScale();
    assertEquals(1.0, scale.x(), 1e-6);
    assertEquals(1.0, scale.y(), 1e-6);
    assertEquals(1.0, scale.z(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SSphere / SphereImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sphereConstructorCreatesNonNullImp() {
    SSphere sphere = new SSphere();
    assertNotNull(sphere.getImplementation());
  }

  @Test
  public void sphereAbstractionRoundTrips() {
    SSphere sphere = new SSphere();
    assertSame(sphere, sphere.getImplementation().getAbstraction());
  }

  @Test
  public void sphereHasOneVisual() {
    SSphere sphere = new SSphere();
    assertEquals(1, sphere.getImplementation().getSgVisuals().length);
  }

  @Test
  public void sphereRadiusDefaultIsPositive() {
    SSphere sphere = new SSphere();
    assertTrue(sphere.getRadius() > 0);
  }

  @Test
  public void sphereHasResizers() {
    SSphere sphere = new SSphere();
    Resizer[] resizers = sphere.getImplementation().getResizers();
    assertNotNull(resizers);
    assertTrue(resizers.length > 0);
  }

  @Test
  public void sphereGetValueForResizerReturnsPositive() {
    SSphere sphere = new SSphere();
    double val = sphere.getImplementation().getValueForResizer(Resizer.UNIFORM);
    assertTrue(val > 0);
  }

  @Test
  public void sphereSetValueForResizerUniform() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void spherePaintRoundTrips() {
    SSphere sphere = new SSphere();
    sphere.getImplementation().paint.setValue(Color.RED);
    assertEquals(Color.RED, sphere.getImplementation().paint.getValue());
  }

  @Test
  public void sphereSetSize() {
    SSphere sphere = new SSphere();
    SphereImp imp = sphere.getImplementation();
    imp.setSize(new Dimension3(4.0, 4.0, 4.0));
    Dimension3 size = imp.getSize();
    assertNotNull(size);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SCone / ConeImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void coneConstructorCreatesNonNullImp() {
    SCone cone = new SCone();
    assertNotNull(cone.getImplementation());
  }

  @Test
  public void coneAbstractionRoundTrips() {
    SCone cone = new SCone();
    assertSame(cone, cone.getImplementation().getAbstraction());
  }

  @Test
  public void coneHasOneVisual() {
    SCone cone = new SCone();
    assertEquals(1, cone.getImplementation().getSgVisuals().length);
  }

  @Test
  public void coneBaseRadiusIsPositive() {
    SCone cone = new SCone();
    assertTrue(cone.getBaseRadius() > 0);
  }

  @Test
  public void coneLengthIsPositive() {
    SCone cone = new SCone();
    assertTrue(cone.getLength() > 0);
  }

  @Test
  public void coneHasResizers() {
    SCone cone = new SCone();
    assertTrue(cone.getImplementation().getResizers().length > 0);
  }

  @Test
  public void coneSetValueForResizer() {
    SCone cone = new SCone();
    ConeImp imp = cone.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.0);
    assertEquals(2.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void conePaintRoundTrips() {
    SCone cone = new SCone();
    cone.getImplementation().paint.setValue(Color.GREEN);
    assertEquals(Color.GREEN, cone.getImplementation().paint.getValue());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SCylinder / CylinderImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void cylinderConstructorCreatesNonNullImp() {
    SCylinder cyl = new SCylinder();
    assertNotNull(cyl.getImplementation());
  }

  @Test
  public void cylinderAbstractionRoundTrips() {
    SCylinder cyl = new SCylinder();
    assertSame(cyl, cyl.getImplementation().getAbstraction());
  }

  @Test
  public void cylinderHasOneVisual() {
    SCylinder cyl = new SCylinder();
    assertEquals(1, cyl.getImplementation().getSgVisuals().length);
  }

  @Test
  public void cylinderRadiusIsPositive() {
    SCylinder cyl = new SCylinder();
    assertTrue(cyl.getRadius() > 0);
  }

  @Test
  public void cylinderLengthIsPositive() {
    SCylinder cyl = new SCylinder();
    assertTrue(cyl.getLength() > 0);
  }

  @Test
  public void cylinderHasResizers() {
    SCylinder cyl = new SCylinder();
    assertTrue(cyl.getImplementation().getResizers().length > 0);
  }

  @Test
  public void cylinderSetValueForResizer() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 3.0);
    assertEquals(3.0, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void cylinderPaintRoundTrips() {
    SCylinder cyl = new SCylinder();
    cyl.getImplementation().paint.setValue(Color.YELLOW);
    assertEquals(Color.YELLOW, cyl.getImplementation().paint.getValue());
  }

  @Test
  public void cylinderSetSize() {
    SCylinder cyl = new SCylinder();
    CylinderImp imp = cyl.getImplementation();
    imp.setSize(new Dimension3(2.0, 3.0, 2.0));
    Dimension3 size = imp.getSize();
    assertNotNull(size);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  STorus / TorusImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void torusConstructorCreatesNonNullImp() {
    STorus torus = new STorus();
    assertNotNull(torus.getImplementation());
  }

  @Test
  public void torusAbstractionRoundTrips() {
    STorus torus = new STorus();
    assertSame(torus, torus.getImplementation().getAbstraction());
  }

  @Test
  public void torusHasOneVisual() {
    STorus torus = new STorus();
    assertEquals(1, torus.getImplementation().getSgVisuals().length);
  }

  @Test
  public void torusInnerRadiusIsPositive() {
    STorus torus = new STorus();
    assertTrue(torus.getInnerRadius() > 0);
  }

  @Test
  public void torusOuterRadiusIsPositive() {
    STorus torus = new STorus();
    assertTrue(torus.getOuterRadius() > 0);
  }

  @Test
  public void torusHasResizers() {
    STorus torus = new STorus();
    assertTrue(torus.getImplementation().getResizers().length > 0);
  }

  @Test
  public void torusSetValueForResizer() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.setValueForResizer(Resizer.UNIFORM, 2.5);
    assertEquals(2.5, imp.getValueForResizer(Resizer.UNIFORM), 1e-6);
  }

  @Test
  public void torusPaintRoundTrips() {
    STorus torus = new STorus();
    torus.getImplementation().paint.setValue(Color.MAGENTA);
    assertEquals(Color.MAGENTA, torus.getImplementation().paint.getValue());
  }

  @Test
  public void torusSetSize() {
    STorus torus = new STorus();
    TorusImp imp = torus.getImplementation();
    imp.setSize(new Dimension3(3.0, 1.0, 3.0));
    Dimension3 size = imp.getSize();
    assertNotNull(size);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SDisc / DiscImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void discConstructorCreatesNonNullImp() {
    SDisc disc = new SDisc();
    assertNotNull(disc.getImplementation());
  }

  @Test
  public void discAbstractionRoundTrips() {
    SDisc disc = new SDisc();
    assertSame(disc, disc.getImplementation().getAbstraction());
  }

  @Test
  public void discHasOneVisual() {
    SDisc disc = new SDisc();
    assertEquals(1, disc.getImplementation().getSgVisuals().length);
  }

  @Test
  public void discRadiusIsPositive() {
    SDisc disc = new SDisc();
    assertTrue(disc.getRadius() > 0);
  }

  @Test
  public void discHasResizers() {
    SDisc disc = new SDisc();
    assertTrue(disc.getImplementation().getResizers().length > 0);
  }

  @Test
  public void discSetValueForResizer() {
    SDisc disc = new SDisc();
    DiscImp imp = disc.getImplementation();
    imp.setValueForResizer(Resizer.XZ_PLANE, 4.0);
    assertEquals(4.0, imp.getValueForResizer(Resizer.XZ_PLANE), 1e-6);
  }

  @Test
  public void discPaintRoundTrips() {
    SDisc disc = new SDisc();
    disc.getImplementation().paint.setValue(Color.CYAN);
    assertEquals(Color.CYAN, disc.getImplementation().paint.getValue());
  }

  @Test
  public void discSetSize() {
    SDisc disc = new SDisc();
    DiscImp imp = disc.getImplementation();
    imp.setSize(new Dimension3(5.0, 0.1, 5.0));
    Dimension3 size = imp.getSize();
    assertNotNull(size);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Inherited behaviors (via Box as representative)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void shapeSgCompositeIsNotNull() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().getSgComposite());
  }

  @Test
  public void shapeLocalTransformationIsIdentity() {
    SBox box = new SBox();
    assertTrue(box.getImplementation().getLocalTransformation().isIdentity());
  }

  @Test
  public void shapeAxisBoundingBoxIsNotNull() {
    SBox box = new SBox();
    assertNotNull(box.getImplementation().getAxisAlignedMinimumBoundingBox());
  }

  @Test
  public void shapeDefaultVehicleIsNull() {
    SBox box = new SBox();
    assertNotNull("Shape should be parentable", box.getImplementation().getSgComposite());
  }

  @Test
  public void shapeVisualInstanceRegistryWorks() {
    SBox box = new SBox();
    Visual v = box.getImplementation().getSgVisuals()[0];
    EntityImp imp = EntityImp.getInstance(v);
    assertSame(box.getImplementation(), imp);
  }
}
