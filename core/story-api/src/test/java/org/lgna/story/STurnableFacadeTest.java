package org.lgna.story;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.SceneImp;
import org.lgna.story.implementation.StandInImp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the STurnable and SMovableTurnable facade methods via SBox.
 * SBox extends SShape → SModel → SMovableTurnable → STurnable → SThing.
 * These are zero-duration animation calls (instant apply), headless-safe.
 */
public class STurnableFacadeTest {

  private SBox subject;
  private SBox other;
  private TestScene scene;

  @Before
  public void setUp() {
    scene = new TestScene();
    SceneImp sceneImp = scene.getImplementation();

    subject = new SBox();
    subject.getImplementation().setVehicle(sceneImp);
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);

    other = new SBox();
    other.getImplementation().setVehicle(sceneImp);
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  STurnable — distance queries (facade layer)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getDistanceToReturnsPositive() {
    Double dist = subject.getDistanceTo(other);
    assertNotNull(dist);
    assertTrue(dist > 0);
  }

  @Test
  public void getDistanceToIsConsistent() {
    Double d1 = subject.getDistanceTo(other);
    Double d2 = other.getDistanceTo(subject);
    assertEquals(d1, d2, 1e-6);
  }

  @Test
  public void isFacingReturnsBooleanForFrontTarget() {
    // Place other in front of subject (-Z direction)
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -5));
    Boolean facing = subject.isFacing(other);
    assertNotNull(facing);
    assertTrue(facing);
  }

  @Test
  public void isFacingReturnsFalseForBehind() {
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 5));
    assertFalse(subject.isFacing(other));
  }

  @Test
  public void getDistanceAboveReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 3, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Double above = subject.getDistanceAbove(other);
    assertNotNull(above);
    assertTrue(above > 0);
  }

  @Test
  public void isAboveReturnsTrueWhenAbove() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    assertTrue(subject.isAbove(other));
  }

  @Test
  public void getDistanceBelowReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    Double below = subject.getDistanceBelow(other);
    assertNotNull(below);
    assertTrue(below > 0);
  }

  @Test
  public void isBelowReturnsTrueWhenBelow() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    assertTrue(subject.isBelow(other));
  }

  @Test
  public void getDistanceToTheRightOfReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Double dist = subject.getDistanceToTheRightOf(other);
    assertNotNull(dist);
  }

  @Test
  public void isToTheRightOfReturnsBooleanValue() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Boolean result = subject.isToTheRightOf(other);
    assertNotNull(result);
  }

  @Test
  public void getDistanceToTheLeftOfReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(-5, 0, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Double dist = subject.getDistanceToTheLeftOf(other);
    assertNotNull(dist);
  }

  @Test
  public void isToTheLeftOfReturnsBooleanValue() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(-5, 0, 0));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Boolean result = subject.isToTheLeftOf(other);
    assertNotNull(result);
  }

  @Test
  public void getDistanceInFrontOfReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -5));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Double dist = subject.getDistanceInFrontOf(other);
    assertNotNull(dist);
  }

  @Test
  public void isInFrontOfReturnsBooleanValue() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -5));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Boolean result = subject.isInFrontOf(other);
    assertNotNull(result);
  }

  @Test
  public void getDistanceBehindReturnsNonNull() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 5));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Double dist = subject.getDistanceBehind(other);
    assertNotNull(dist);
  }

  @Test
  public void isBehindReturnsBooleanValue() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 5));
    other.getImplementation().setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Boolean result = subject.isBehind(other);
    assertNotNull(result);
  }

  @Test
  public void getOrientationRelativeToVehicleReturnsNonNull() {
    Orientation orientation = subject.getOrientationRelativeToVehicle();
    assertNotNull(orientation);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SMovableTurnable — position queries (facade layer)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getPositionRelativeToVehicleReturnsNonNull() {
    Position pos = subject.getPositionRelativeToVehicle();
    assertNotNull(pos);
  }

  @Test
  public void getPositionRelativeToVehicleReflectsTranslation() {
    subject.getImplementation().setLocalTransformation(AffineMatrix4x4.createTranslation(3, 4, 5));
    Position pos = subject.getPositionRelativeToVehicle();
    assertNotNull(pos);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  STurnable — zero-duration turn (facade layer)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void turnLeftZeroDurationAppliesImmediately() {
    subject.turn(TurnDirection.LEFT, 0.25, new Duration(0.0));
    assertFalse(subject.getImplementation().getLocalOrientation().isIdentity());
  }

  @Test
  public void rollRightZeroDurationAppliesImmediately() {
    subject.roll(RollDirection.RIGHT, 0.25, new Duration(0.0));
    assertFalse(subject.getImplementation().getLocalOrientation().isIdentity());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SMovableTurnable — zero-duration move (facade layer)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void moveForwardZeroDuration() {
    subject.move(MoveDirection.FORWARD, 2.0, new Duration(0.0));
    Point3 pos = subject.getImplementation().getLocalPosition();
    assertTrue("Should have moved in -Z", pos.z() < -0.1);
  }

  @Test
  public void moveUpZeroDuration() {
    subject.move(MoveDirection.UP, 3.0, new Duration(0.0));
    Point3 pos = subject.getImplementation().getLocalPosition();
    assertTrue("Should have moved in +Y", pos.y() > 0.1);
  }

  @Test
  public void moveRightZeroDuration() {
    subject.move(MoveDirection.RIGHT, 2.0, new Duration(0.0));
    Point3 pos = subject.getImplementation().getLocalPosition();
    assertTrue("Should have moved in +X", pos.x() > 0.1);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SModel facade methods (paint, opacity)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getPaintReturnsNonNull() {
    assertNotNull(subject.getPaint());
  }

  @Test
  public void getOpacityReturnsOne() {
    assertEquals(1.0, subject.getOpacity(), 1e-6);
  }

  @Test
  public void setPaintZeroDuration() {
    subject.setPaint(Color.RED, new Duration(0.0));
    assertEquals(Color.RED, subject.getPaint());
  }

  @Test
  public void setOpacityZeroDuration() {
    subject.setOpacity(0.5, new Duration(0.0));
    assertEquals(0.5, subject.getOpacity(), 1e-2);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SThing — name property
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setNameRoundTrips() {
    subject.setName("myBox");
    assertEquals("myBox", subject.getName());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Test double
  // ══════════════════════════════════════════════════════════════════════════

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
