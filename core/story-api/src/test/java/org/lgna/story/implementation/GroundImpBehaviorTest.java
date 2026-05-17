package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.SGround;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for GroundImp via {@link SGround}. The SGround constructor creates
 * a GroundImp internally, exercising the full chain:
 * GroundImp → SimpleModelImp → SingleVisualModelImp → VisualScaleModelImp →
 * ModelImp → TransformableImp → AbstractTransformableImp → EntityImp.
 *
 * <p>All tests are headless-safe — no AWT or rendering required.
 */
public class GroundImpBehaviorTest {

  private SGround ground;
  private GroundImp groundImp;

  @Before
  public void setUp() {
    ground = new SGround();
    groundImp = ground.getImplementation();
  }

  // --- Construction ---

  @Test
  public void constructorCreatesNonNullGroundImp() {
    assertNotNull("SGround should create a GroundImp internally", groundImp);
  }

  @Test
  public void abstractionReturnsOwningSGround() {
    assertSame("getAbstraction() should return the owning SGround", ground, groundImp.getAbstraction());
  }

  @Test
  public void sgCompositeIsTransformable() {
    assertTrue("getSgComposite() should return a Transformable",
        groundImp.getSgComposite() instanceof Transformable);
    assertNotNull(groundImp.getSgComposite());
  }

  // --- Scenegraph visual ---

  @Test
  public void sgVisualsHasOneEntry() {
    Visual[] visuals = groundImp.getSgVisuals();
    assertNotNull("getSgVisuals() should not be null", visuals);
    assertEquals("Ground should have exactly one visual", 1, visuals.length);
  }

  @Test
  public void sgVisualHasGeometry() {
    Visual sgVisual = groundImp.getSgVisuals()[0];
    Geometry geom = sgVisual.getGeometry();
    assertNotNull("Ground visual should have geometry after construction", geom);
  }

  @Test
  public void sgVisualIsParentedToComposite() {
    Visual sgVisual = groundImp.getSgVisuals()[0];
    assertSame("Visual should be parented to sgComposite",
        groundImp.getSgComposite(), sgVisual.getParent());
  }

  @Test
  public void sgVisualHasBackFacingAppearance() {
    Visual sgVisual = groundImp.getSgVisuals()[0];
    assertNotNull("Visual should have a back-facing appearance",
        sgVisual.backFacingAppearance.getValue());
  }

  // --- GroundMeshData ---

  @Test
  public void verticesArrayHas593Entries() {
    assertEquals("VERTICES array should have 593 entries", 593, GroundMeshData.VERTICES.length);
  }

  @Test
  public void polygonDataIsNonEmpty() {
    assertTrue("POLYGON_DATA should be non-empty", GroundMeshData.POLYGON_DATA.length > 0);
  }

  // --- Paint property ---

  @Test
  public void defaultPaintIsNotNull() {
    assertNotNull("Default paint should not be null", groundImp.paint.getValue());
  }

  @Test
  public void setPaintRoundTrips() {
    groundImp.paint.setValue(Color.RED);
    assertEquals("Paint should round-trip RED", Color.RED, groundImp.paint.getValue());
  }

  @Test
  public void setPaintViaFacade() {
    groundImp.paint.setValue(Color.BLUE);
    assertEquals("SGround.getPaint() should reflect the set paint", Color.BLUE, ground.getPaint());
  }

  // --- Opacity property ---

  @Test
  public void defaultOpacityIsOne() {
    float opacity = groundImp.opacity.getValue();
    assertEquals("Default opacity should be 1.0", 1.0f, opacity, 1e-6f);
  }

  @Test
  public void setOpacityRoundTrips() {
    groundImp.opacity.setValue(0.5f);
    assertEquals("Opacity should round-trip 0.5", 0.5f, groundImp.opacity.getValue(), 1e-6f);
  }

  @Test
  public void setOpacityViaFacade() {
    groundImp.opacity.setValue(0.75f);
    double opacity = ground.getOpacity();
    assertEquals(0.75, opacity, 1e-2);
  }

  // --- Resizers ---

  @Test
  public void getResizersReturnsEmptyArray() {
    Resizer[] resizers = groundImp.getResizers();
    assertNotNull("getResizers() should not return null", resizers);
    assertEquals("GroundImp should not support resizing", 0, resizers.length);
  }

  // --- Vehicle ---

  @Test
  public void defaultVehicleIsNull() {
    assertNull("Default vehicle should be null for unparented ground", groundImp.getVehicle());
  }

  @Test
  public void setVehicleWithStandInWorks() {
    StandInImp vehicle = new StandInImp();
    groundImp.setVehicle(vehicle);
    assertSame("getVehicle() should return the set StandIn", vehicle, groundImp.getVehicle());
  }

  // --- Instance registry ---

  @Test
  public void instanceRegistryReturnsGroundImpForSgComposite() {
    EntityImp imp = EntityImp.getInstance(groundImp.getSgComposite());
    assertSame("Instance registry should map sgComposite back to GroundImp", groundImp, imp);
  }

  @Test
  public void instanceRegistryReturnsGroundImpForSgVisual() {
    EntityImp imp = EntityImp.getInstance(groundImp.getSgVisuals()[0]);
    assertSame("Instance registry should map sgVisual back to GroundImp", groundImp, imp);
  }

  // --- Transform operations (inherited) ---

  @Test
  public void localTransformationDefaultIsIdentity() {
    AffineMatrix4x4 m = groundImp.getLocalTransformation();
    assertNotNull(m);
    assertTrue("Default local transformation should be identity", m.isIdentity());
  }

  @Test
  public void setLocalTransformationRoundTrips() {
    AffineMatrix4x4 translation = AffineMatrix4x4.createTranslation(1, 2, 3);
    groundImp.setLocalTransformation(translation);
    Point3 pos = groundImp.getLocalPosition();
    assertEquals(1.0, pos.x(), 1e-9);
    assertEquals(2.0, pos.y(), 1e-9);
    assertEquals(3.0, pos.z(), 1e-9);
  }

  // --- Naming (inherited) ---

  @Test
  public void setNameRoundTrips() {
    groundImp.setName("myGround");
    assertEquals("myGround", groundImp.getName());
  }

  // --- Scene query ---

  @Test
  public void getSceneReturnsNullWhenUnattached() {
    assertNull("getScene() should return null for unparented ground", groundImp.getScene());
  }

  // --- Bounding box ---

  @Test
  public void axisBoundingBoxIsNotNull() {
    assertNotNull("Bounding box should not be null", groundImp.getAxisAlignedMinimumBoundingBox());
  }
}
