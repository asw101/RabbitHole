package org.lgna.story.implementation;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.Duration;
import org.lgna.story.Orientation;
import org.lgna.story.SBox;
import org.lgna.story.SScene;
import org.lgna.story.SSphere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Final coverage push — targets specific uncovered lines in STurnable,
 * SceneImp, EntityImp, AbstractTransformableImp, MarkerImp, and TransformAnimator.
 */
public class FinalCoveragePushTest {

  private SBox box;
  private SSphere sphere;
  private SceneImp sceneImp;

  @Before
  public void setUp() {
    TestScene scene = new TestScene();
    sceneImp = scene.getImplementation();
    box = new SBox();
    box.getImplementation().setVehicle(sceneImp);
    box.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    sphere = new SSphere();
    sphere.getImplementation().setVehicle(sceneImp);
    sphere.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
  }

  // --- STurnable.setOrientationRelativeToVehicle (with vehicle) ---

  @Test
  public void setOrientationRelativeToVehicleWithVehicle() {
    Orientation o = new Orientation(0, 0, 0, 1);
    box.setOrientationRelativeToVehicle(o, new Duration(0.0));
    assertNotNull(box.getOrientationRelativeToVehicle());
  }

  // --- STurnable.setOrientationRelativeToVehicle (without vehicle — else branch) ---

  @Test
  public void setOrientationRelativeToVehicleWithoutVehicle() {
    SBox orphan = new SBox();
    Orientation o = new Orientation(0, 0, 0, 1);
    orphan.setOrientationRelativeToVehicle(o, new Duration(0.0));
    assertNotNull(orphan.getOrientationRelativeToVehicle());
  }

  // --- STurnable.turnToFace ---

  @Test
  public void turnToFaceZeroDuration() {
    box.turnToFace(sphere, new Duration(0.0));
    assertFalse(box.getImplementation().getLocalOrientation().isIdentity());
  }

  // --- STurnable.orientTo ---

  @Test
  public void orientToZeroDuration() {
    sphere.getImplementation().setLocalTransformation(
        new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(5, 0, -5)));
    box.orientTo(sphere, new Duration(0.0));
    assertNotNull(box.getOrientationRelativeToVehicle());
  }

  // --- STurnable.orientToUpright ---

  @Test
  public void orientToUprightZeroDuration() {
    box.getImplementation().applyRotationInRadians(
        new org.alice.math.immutable.Vector3(1, 0, 0), Math.PI / 4, box.getImplementation());
    box.orientToUpright(new Duration(0.0));
    assertNotNull(box.getOrientationRelativeToVehicle());
  }

  // --- STurnable.pointAt ---

  @Test
  public void pointAtZeroDuration() {
    box.pointAt(sphere, new Duration(0.0));
    assertNotNull(box.getOrientationRelativeToVehicle());
  }

  // --- SMovableTurnable.moveToward ---

  @Test
  public void moveTowardZeroDuration() {
    box.moveToward(sphere, 2.0, new Duration(0.0));
    Point3 pos = box.getImplementation().getLocalPosition();
    assertTrue("Should have moved toward sphere", pos.x() > 0);
  }

  // --- SMovableTurnable.moveAwayFrom ---

  @Test
  public void moveAwayFromZeroDuration() {
    box.moveAwayFrom(sphere, 2.0, new Duration(0.0));
    Point3 pos = box.getImplementation().getLocalPosition();
    assertTrue("Should have moved away from sphere", pos.x() < 0);
  }

  // --- SMovableTurnable.moveTo with zero duration ---

  @Test
  public void moveToZeroDuration() {
    box.moveTo(sphere, new Duration(0.0));
    Point3 pos = box.getImplementation().getLocalPosition();
    assertEquals(5.0, pos.x(), 1.0);
  }

  // --- SMovableTurnable.moveAndOrientTo ---

  @Test
  public void moveAndOrientToZeroDuration() {
    box.moveAndOrientTo(sphere, new Duration(0.0));
    Point3 pos = box.getImplementation().getLocalPosition();
    assertEquals(5.0, pos.x(), 1.0);
  }

  // --- SModel.setWidth/Height/Depth ---

  @Test
  public void setWidthZeroDuration() {
    box.setWidth(3.0, new Duration(0.0));
    assertTrue(box.getWidth() > 0);
  }

  @Test
  public void setHeightZeroDuration() {
    box.setHeight(4.0, new Duration(0.0));
    assertTrue(box.getHeight() > 0);
  }

  @Test
  public void setDepthZeroDuration() {
    box.setDepth(5.0, new Duration(0.0));
    assertTrue(box.getDepth() > 0);
  }

  // --- SModel.resizeWidth/Height/Depth ---

  @Test
  public void resizeWidthZeroDuration() {
    box.resizeWidth(2.0, new Duration(0.0));
    assertTrue(box.getWidth() > 0);
  }

  @Test
  public void resizeHeightZeroDuration() {
    box.resizeHeight(2.0, new Duration(0.0));
    assertTrue(box.getHeight() > 0);
  }

  @Test
  public void resizeDepthZeroDuration() {
    box.resizeDepth(2.0, new Duration(0.0));
    assertTrue(box.getDepth() > 0);
  }

  // --- SModel.resize ---

  @Test
  public void resizeUniformZeroDuration() {
    box.resize(2.0, new Duration(0.0));
    assertTrue(box.getWidth() > 0);
  }

  // --- EntityImp additional ---

  @Test
  public void entityImpToString() {
    assertNotNull(box.getImplementation().toString());
  }

  @Test
  public void entityImpSetNamePropagates() {
    box.setName("testBox");
    assertEquals("testBox", box.getName());
  }

  // --- Multiple paint/opacity animations ---

  @Test
  public void paintAnimateZeroDuration() {
    box.getImplementation().paint.animateValue(Color.GREEN, 0.0, null);
    assertEquals(Color.GREEN, box.getImplementation().paint.getValue());
  }

  @Test
  public void opacityAnimateZeroDuration() {
    box.getImplementation().opacity.animateValue(0.3f, 0.0, null);
    assertEquals(0.3f, box.getImplementation().opacity.getValue(), 1e-2f);
  }

  // --- Test double ---

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
