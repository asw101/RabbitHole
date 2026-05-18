package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Background;
import edu.cmu.cs.dennisc.scenegraph.ExponentialSquaredFog;
import edu.cmu.cs.dennisc.scenegraph.LinearFog;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Sphere;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorldAndLookAtTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void worldConstructorCreatesExpectedChildrenAndDefaults() {
    World world = new World();

    assertSame(world.getSGBackground(), world.background.getValue());
    assertEquals(new Color4f(0.5f, 0.5f, 1.0f, 1.0f), world.getSGBackground().color.getValue());
    assertEquals(new Color4f(0.2f, 0.2f, 0.2f, 1.0f), world.getSGAmbientLight().color.getValue());
    assertEquals(3, world.getComponentCount());
    assertSame(world, world.getSGAmbientLight().getParent());
    assertSame(world.getSGSunVehicle(), world.getSGSunLight().getParent());
    assertSame(world.getSGCameraVehicle(), world.getSGCamera().getParent());
    assertEquals(32.0, world.getSGCameraVehicle().getLocalTransformation().translation().z(), EPSILON);
    assertEquals(1000.0, world.getSGCamera().farClippingPlaneDistance.getValue(), EPSILON);
  }

  @Test
  public void worldSetNamePropagatesToInternalScenegraphObjects() {
    World world = new World();

    world.setName("demoWorld");

    assertEquals("demoWorld.sgBackground", world.getSGBackground().getName());
    assertEquals("demoWorld.sgAmbientLight", world.getSGAmbientLight().getName());
    assertEquals("demoWorld.sgSunVehicle", world.getSGSunVehicle().getName());
    assertEquals("demoWorld.sgSunLight", world.getSGSunLight().getName());
    assertEquals("demoWorld.sgCameraVehicle", world.getSGCameraVehicle().getName());
    assertEquals("demoWorld.sgCamera", world.getSGCamera().getName());
  }

  @Test
  public void goodLookAtDistanceReturnsNaNForTooWideBoxAndFiniteValueForVisual() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();

    double tooWide = GoodLookAtUtils.calculateGoodLookAtDistance(
        new AxisAlignedBox(new Point3(-100, -1, -1), new Point3(101, 1, 1)),
        AffineMatrix4x4.IDENTITY,
        new AngleInRadians(Math.PI / 2.0),
        1.0,
        camera);
    assertTrue(Double.isNaN(tooWide));

    Sphere sphere = new Sphere();
    sphere.radius.setValue(1.0);
    Visual visual = new Visual();
    visual.setGeometry(sphere);
    double finite = GoodLookAtUtils.calculateGoodLookAtDistance(visual, new AngleInRadians(Math.PI / 2.0), 1.0, camera);
    assertTrue(Double.isFinite(finite));
    assertTrue(finite > 0.0);
  }

  @Test
  public void symmetricPerspectiveAndAbstractCameraAccessorsWorkThroughCameraHierarchy() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    Transformable parent = new Transformable();
    parent.setParent(new Scene());
    camera.setParent(parent);

    camera.setEffectiveHorizontalViewingAngle(new AngleInRadians(1.25));
    camera.setEffectiveVerticalViewingAngle(new AngleInRadians(0.75));

    assertSame(parent, camera.getMovableParent());
    assertEquals(SymmetricPerspectiveCamera.DEFAULT_VERTICAL_VIEW_ANGLE, camera.verticalViewingAngle.getValue());
    assertEquals(SymmetricPerspectiveCamera.DEFAULT_WIDTH_TO_HEIGHT_RATIO, SymmetricPerspectiveCamera.DEFAULT_WIDTH_TO_HEIGHT_RATIO, 0.0);
    assertEquals(1.25, camera.getEffectiveHorizontalViewingAngle().getAsRadians(), EPSILON);
    assertEquals(0.75, camera.getEffectiveVerticalViewingAngle().getAsRadians(), EPSILON);
    assertEquals(0.125, camera.nearClippingPlaneDistance.getValue(), EPSILON);
    assertEquals(256.0, camera.farClippingPlaneDistance.getValue(), EPSILON);
  }

  @Test
  public void cameraMovableParentSkipsVrHeadsetComposite() {
    Transformable vrUser = new Transformable();
    Transformable headset = new Transformable();
    headset.setName("VRHeadset.sgComposite");
    headset.setParent(vrUser);

    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.setParent(headset);

    assertSame(vrUser, camera.getMovableParent());
  }

  @Test
  public void orthographicCameraBackgroundAndFogDefaultsRemainStable() {
    OrthographicCamera camera = new OrthographicCamera();
    Background background = new Background();
    LinearFog linearFog = new LinearFog();
    ExponentialSquaredFog exponentialSquaredFog = new ExponentialSquaredFog();

    camera.background.setValue(background);

    assertSame(background, camera.background.getValue());
    assertEquals(Color4f.WHITE, background.color.getValue());
    assertEquals(1.0, linearFog.nearDistance.getValue(), EPSILON);
    assertEquals(256.0, linearFog.farDistance.getValue(), EPSILON);
    assertEquals(1.0, exponentialSquaredFog.density.getValue(), EPSILON);
    assertEquals(0, camera.postRenderLayers.getValue().length);
  }
}
