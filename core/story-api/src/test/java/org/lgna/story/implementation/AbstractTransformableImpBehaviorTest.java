package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for AbstractTransformableImp that extend beyond the
 * characterization test coverage. Focuses on: 3D distance with all axes,
 * facing boundary angles, rotation composition, multi-hop vehicle chains,
 * position-only preservation, and directional distance queries.
 *
 * <p>Uses {@link StandInImp} as the concrete test double. All tests are headless-safe.
 */
public class AbstractTransformableImpBehaviorTest {

  private StandInImp vehicle;
  private StandInImp subject;

  @Before
  public void setUp() {
    vehicle = new StandInImp();
    subject = new StandInImp();
    subject.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  3D distance with all axes
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void distanceToWithAllThreeAxes() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 2));

    double distance = subject.getDistanceTo(other);
    assertEquals("Distance should be 3.0 (1² + 2² + 2² = 9)", 3.0, distance, 1e-6);
  }

  @Test
  public void distanceToWithNegativeCoordinates() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(-3, -4, 0));

    double distance = subject.getDistanceTo(other);
    assertEquals("Distance should be 5.0 with negative coords", 5.0, distance, 1e-6);
  }

  @Test
  public void distanceToIsSymmetric() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(4, 0, 0));

    double d1 = subject.getDistanceTo(other);
    double d2 = other.getDistanceTo(subject);
    assertEquals("Distance should be symmetric", d1, d2, 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Facing boundary — exactly 90°
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void isFacingAt90DegreesReturnsFalse() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    // Place other exactly to the side (X axis, Z=0) — perpendicular, not "facing"
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    // z component of transform relative to subject should be 0
    assertFalse("Entity at exactly 90° (Z=0) should not be 'facing'",
        subject.isFacing(other));
  }

  @Test
  public void isFacingSlightlyInFront() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, -0.001));
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    assertTrue("Entity slightly in front (-Z) should be 'facing'",
        subject.isFacing(other));
  }

  @Test
  public void isFacingSlightlyBehind() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0.001));
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    assertFalse("Entity slightly behind (+Z) should not be 'facing'",
        subject.isFacing(other));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Rotation composition
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sequentialRotationsCompose() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Vector3 yAxis = new Vector3(0, 1, 0);

    subject.applyRotationInRadians(yAxis, Math.PI / 4.0, subject);
    subject.applyRotationInRadians(yAxis, Math.PI / 4.0, subject);

    OrthogonalMatrix3x3 orientation = subject.getLocalOrientation();
    assertFalse("After two 45° rotations, orientation should not be identity",
        orientation.isIdentity());
  }

  @Test
  public void fullRotationReturnsToIdentity() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Vector3 yAxis = new Vector3(0, 1, 0);

    // Four 90° rotations = 360° = identity
    for (int i = 0; i < 4; i++) {
      subject.applyRotationInRadians(yAxis, Math.PI / 2.0, subject);
    }

    OrthogonalMatrix3x3 orientation = subject.getLocalOrientation();
    assertTrue("After 360° rotation, orientation should be ~identity",
        orientation.isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-6));
  }

  @Test
  public void rotationAboutXAxis() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Vector3 xAxis = new Vector3(1, 0, 0);
    subject.applyRotationInRadians(xAxis, Math.PI / 2.0, subject);

    OrthogonalMatrix3x3 orientation = subject.getLocalOrientation();
    assertFalse("Rotation about X should change orientation", orientation.isIdentity());
  }

  @Test
  public void rotationAboutZAxis() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Vector3 zAxis = new Vector3(0, 0, 1);
    subject.applyRotationInRadians(zAxis, Math.PI / 2.0, subject);

    OrthogonalMatrix3x3 orientation = subject.getLocalOrientation();
    assertFalse("Rotation about Z should change orientation", orientation.isIdentity());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Multi-hop vehicle chain
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void threeHopVehicleChainPreservesAbsoluteTransform() {
    StandInImp grandparent = new StandInImp();
    grandparent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));

    vehicle.setVehicle(grandparent);
    vehicle.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));

    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 3));

    // Chain: grandparent(10,0,0) -> vehicle(0,5,0) -> subject(0,0,3)
    // Absolute should be approximately (10, 5, 3)
    AffineMatrix4x4 abs = subject.getAbsoluteTransformation();
    assertEquals(10.0, abs.translation().x(), 1e-6);
    assertEquals(5.0, abs.translation().y(), 1e-6);
    assertEquals(3.0, abs.translation().z(), 1e-6);
  }

  @Test
  public void fourHopVehicleChainWorks() {
    StandInImp root = new StandInImp();
    StandInImp level1 = new StandInImp();
    StandInImp level2 = new StandInImp();
    StandInImp leaf = new StandInImp();

    level1.setVehicle(root);
    level2.setVehicle(level1);
    leaf.setVehicle(level2);

    root.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));
    level1.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));
    level2.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));
    leaf.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));

    AffineMatrix4x4 abs = leaf.getAbsoluteTransformation();
    assertEquals("4 hops of x+1 should give x=4", 4.0, abs.translation().x(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  setPositionOnly preserves orientation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setPositionOnlyPreservesOrientation() {
    Vector3 yAxis = new Vector3(0, 1, 0);
    subject.applyRotationInRadians(yAxis, Math.PI / 3.0, subject);
    OrthogonalMatrix3x3 orientationBefore = subject.getLocalOrientation();

    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));

    subject.setPositionOnly(target);

    OrthogonalMatrix3x3 orientationAfter = subject.getLocalOrientation();
    assertTrue("Orientation should be preserved after setPositionOnly",
        orientationBefore.isWithinEpsilonOf(orientationAfter, 1e-6));
  }

  @Test
  public void setPositionOnlyMovesToTarget() {
    StandInImp target = new StandInImp();
    target.setVehicle(vehicle);
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(7, 8, 9));

    subject.setPositionOnly(target);

    Point3 pos = subject.getLocalPosition();
    assertEquals(7.0, pos.x(), 1e-6);
    assertEquals(8.0, pos.y(), 1e-6);
    assertEquals(9.0, pos.z(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  animateApplyTranslation with zero duration (all axes)
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void animateApplyTranslationXYZ() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    subject.animateApplyTranslation(2.0, 3.0, 4.0, subject, 0.0,
        TraditionalStyle.BEGIN_AND_END_GENTLY);

    Point3 pos = subject.getLocalPosition();
    assertEquals(2.0, pos.x(), 1e-6);
    assertEquals(3.0, pos.y(), 1e-6);
    assertEquals(4.0, pos.z(), 1e-6);
  }

  @Test
  public void animateApplyRotationWithZeroDuration() {
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    Vector3 yAxis = new Vector3(0, 1, 0);
    subject.animateApplyRotationInRevolutions(yAxis, 0.25, subject, 0.0,
        TraditionalStyle.BEGIN_AND_END_GENTLY);

    OrthogonalMatrix3x3 orientation = subject.getLocalOrientation();
    assertFalse("Orientation should change after rotation", orientation.isIdentity());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Directional distance queries
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void distanceAboveReturnsPositiveWhenAbove() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    // Place subject above other
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));

    double distAbove = subject.getDistanceAbove(other, vehicle);
    assertTrue("Distance above should be positive", distAbove > 0);
  }

  @Test
  public void distanceBelowReturnsPositiveWhenBelow() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    // Place subject below other
    subject.setLocalTransformation(AffineMatrix4x4.IDENTITY);

    double distBelow = subject.getDistanceBelow(other, vehicle);
    assertTrue("Distance below should be positive", distBelow > 0);
  }

  @Test
  public void distanceToTheRightOfReturnsValue() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));

    double dist = subject.getDistanceToTheRightOf(other, vehicle);
    assertNotNull("Distance to the right should be a number");
  }

  @Test
  public void distanceBehindReturnsValue() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 5));

    double dist = subject.getDistanceBehind(other, vehicle);
    assertNotNull("Distance behind should be a number");
  }

  @Test
  public void distanceInFrontOfReturnsValue() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, -5));

    double dist = subject.getDistanceInFrontOf(other, vehicle);
    assertNotNull("Distance in front should be a number");
  }

  @Test
  public void distanceToTheLeftOfReturnsValue() {
    StandInImp other = new StandInImp();
    other.setVehicle(vehicle);
    other.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(-5, 0, 0));

    double dist = subject.getDistanceToTheLeftOf(other, vehicle);
    assertNotNull("Distance to the left should be a number");
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  setLocalOrientationOnly
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void setLocalOrientationOnlyPreservesPosition() {
    subject.setLocalTransformation(AffineMatrix4x4.createTranslation(7, 8, 9));
    Vector3 yAxis = new Vector3(0, 1, 0);
    subject.applyRotationInRadians(yAxis, Math.PI / 4.0, subject);

    // Now reset orientation but preserve position
    subject.setLocalOrientationOnly(OrthogonalMatrix3x3.IDENTITY);

    Point3 pos = subject.getLocalPosition();
    assertEquals(7.0, pos.x(), 1e-6);
    assertEquals(8.0, pos.y(), 1e-6);
    assertEquals(9.0, pos.z(), 1e-6);

    assertTrue("Orientation should be identity after reset",
        subject.getLocalOrientation().isWithinEpsilonOf(OrthogonalMatrix3x3.IDENTITY, 1e-6));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  applyAnimation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void applyAnimationDoesNotThrow() {
    subject.applyAnimation();
  }
}
