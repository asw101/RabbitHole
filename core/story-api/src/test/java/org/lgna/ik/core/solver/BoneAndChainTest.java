package org.lgna.ik.core.solver;

import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Bone.Axis} inner class and {@link Bone.Direction} enum.
 *
 * <p>Bone and Chain constructors require live JointImp instances which are
 * tightly coupled to the scenegraph. Without a mocking library the full
 * constructor path cannot be exercised directly, but Bone.Axis is a
 * public static inner class whose constructor only stores its arguments.
 * Passing {@code null} for the bone reference lets us exercise every
 * vector-math path inside Axis without touching JointImp.
 */
public class BoneAndChainTest {

  private static final double TOL = 1e-12;

  // ── Bone.Direction enum ───────────────────────────────────────────────

  @Test
  public void directionEnumContainsTwoValues() {
    Bone.Direction[] values = Bone.Direction.values();
    assertEquals(2, values.length);
    assertEquals(Bone.Direction.DOWNSTREAM, Bone.Direction.valueOf("DOWNSTREAM"));
    assertEquals(Bone.Direction.UPSTREAM, Bone.Direction.valueOf("UPSTREAM"));
  }

  // ── Axis constructor ──────────────────────────────────────────────────

  @Test
  public void axisConstructorStoresIndexAndInitializesToZero() {
    Bone.Axis axis = new Bone.Axis(null, 1);
    assertEquals(1, axis.getOriginalIndexInJoint());
    assertNull(axis.getBone());
    assertEquals(Vector3.ZERO, axis.getCurrentValue());
  }

  @Test
  public void axisConstructorIndex0() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertEquals(0, axis.getOriginalIndexInJoint());
    assertEquals(Vector3.ZERO, axis.getCurrentValue());
  }

  @Test
  public void axisConstructorIndex2() {
    Bone.Axis axis = new Bone.Axis(null, 2);
    assertEquals(2, axis.getOriginalIndexInJoint());
    assertEquals(Vector3.ZERO, axis.getCurrentValue());
  }

  // ── getCurrentValue / setCurrentValue ─────────────────────────────────

  @Test
  public void setAndGetCurrentValue() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    Vector3 v = new Vector3(1.0, 2.0, 3.0);
    axis.setCurrentValue(v);
    assertSame(v, axis.getCurrentValue());
  }

  // ── invertDirection ───────────────────────────────────────────────────

  @Test
  public void invertDirectionNegatesAxis() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(new Vector3(1.0, -2.0, 3.0));
    axis.invertDirection();
    Vector3 negated = axis.getCurrentValue();
    assertEquals(-1.0, negated.x(), TOL);
    assertEquals(2.0, negated.y(), TOL);
    assertEquals(-3.0, negated.z(), TOL);
  }

  @Test
  public void doubleInvertRestoresOriginal() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    Vector3 original = new Vector3(4.0, 5.0, 6.0);
    axis.setCurrentValue(original);
    axis.invertDirection();
    axis.invertDirection();
    Vector3 restored = axis.getCurrentValue();
    assertEquals(original.x(), restored.x(), TOL);
    assertEquals(original.y(), restored.y(), TOL);
    assertEquals(original.z(), restored.z(), TOL);
  }

  // ── updateLinearContributions ─────────────────────────────────────────

  @Test
  public void linearContributionInitiallyZero() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertEquals(Vector3.ZERO, axis.getLinearContribution());
  }

  @Test
  public void updateLinearContributionsCrossesAxisWithVector() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(Vector3.POSITIVE_X_AXIS);
    Vector3 jointEeVec = Vector3.POSITIVE_Y_AXIS;
    axis.updateLinearContributions(jointEeVec);

    // (1,0,0) x (0,1,0) = (0,0,1)
    Vector3 result = axis.getLinearContribution();
    assertEquals(0.0, result.x(), TOL);
    assertEquals(0.0, result.y(), TOL);
    assertEquals(1.0, result.z(), TOL);
  }

  @Test
  public void updateLinearContributionsParallelVectorsGiveZero() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(new Vector3(3.0, 0.0, 0.0));
    axis.updateLinearContributions(new Vector3(5.0, 0.0, 0.0));
    Vector3 result = axis.getLinearContribution();
    assertEquals(0.0, result.x(), TOL);
    assertEquals(0.0, result.y(), TOL);
    assertEquals(0.0, result.z(), TOL);
  }

  @Test
  public void updateLinearContributionsGeneralCase() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    Vector3 a = new Vector3(1.0, 2.0, 3.0);
    Vector3 b = new Vector3(4.0, 5.0, 6.0);
    axis.setCurrentValue(a);
    axis.updateLinearContributions(b);
    // cross product: (2*6-3*5, 3*4-1*6, 1*5-2*4) = (-3, 6, -3)
    Vector3 result = axis.getLinearContribution();
    assertEquals(-3.0, result.x(), TOL);
    assertEquals(6.0, result.y(), TOL);
    assertEquals(-3.0, result.z(), TOL);
  }

  // ── updateAngularContributions ────────────────────────────────────────

  @Test
  public void angularContributionInitiallyZero() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertEquals(Vector3.ZERO, axis.getAngularContribution());
  }

  @Test
  public void updateAngularContributionsSetsAxisValue() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    Vector3 v = new Vector3(7.0, 8.0, 9.0);
    axis.setCurrentValue(v);
    axis.updateAngularContributions();
    assertSame(v, axis.getAngularContribution());
  }

  // ── getLocalAxis ──────────────────────────────────────────────────────

  @Test
  public void getLocalAxisIndex0ReturnsPositiveX() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertEquals(Vector3.POSITIVE_X_AXIS, axis.getLocalAxis());
  }

  @Test
  public void getLocalAxisIndex1ReturnsPositiveY() {
    Bone.Axis axis = new Bone.Axis(null, 1);
    assertEquals(Vector3.POSITIVE_Y_AXIS, axis.getLocalAxis());
  }

  @Test
  public void getLocalAxisIndex2ReturnsPositiveZ() {
    Bone.Axis axis = new Bone.Axis(null, 2);
    assertEquals(Vector3.POSITIVE_Z_AXIS, axis.getLocalAxis());
  }

  @Test(expected = RuntimeException.class)
  public void getLocalAxisInvalidIndexThrows() {
    Bone.Axis axis = new Bone.Axis(null, 3);
    axis.getLocalAxis();
  }

  // ── equals / hashCode (same null bone, same index) ────────────────────

  @Test
  public void axisSelfEqualityReturnsTrue() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertTrue(axis.equals(axis));
  }

  @Test
  public void axisNotEqualToNull() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertFalse(axis.equals(null));
  }

  @Test
  public void axisNotEqualToNonAxisObject() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    assertFalse(axis.equals("a string"));
  }

  // ── getBone / getOriginalIndexInJoint ─────────────────────────────────

  @Test
  public void getBoneReturnsConstructorArg() {
    Bone.Axis axis = new Bone.Axis(null, 2);
    assertNull(axis.getBone());
  }

  @Test
  public void getOriginalIndexInJointReturnsConstructorArg() {
    Bone.Axis axis = new Bone.Axis(null, 1);
    assertEquals(1, axis.getOriginalIndexInJoint());
  }

  // ── Repeated mutation round-trips ─────────────────────────────────────

  @Test
  public void linearContributionUpdatesOverwrite() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(Vector3.POSITIVE_Z_AXIS);
    axis.updateLinearContributions(Vector3.POSITIVE_X_AXIS);
    // (0,0,1) x (1,0,0) = (0,1,0)
    assertEquals(0.0, axis.getLinearContribution().x(), TOL);
    assertEquals(1.0, axis.getLinearContribution().y(), TOL);
    assertEquals(0.0, axis.getLinearContribution().z(), TOL);

    axis.setCurrentValue(Vector3.POSITIVE_Y_AXIS);
    axis.updateLinearContributions(Vector3.POSITIVE_Z_AXIS);
    // (0,1,0) x (0,0,1) = (1,0,0)
    assertEquals(1.0, axis.getLinearContribution().x(), TOL);
    assertEquals(0.0, axis.getLinearContribution().y(), TOL);
    assertEquals(0.0, axis.getLinearContribution().z(), TOL);
  }

  @Test
  public void angularContributionReflectsLatestAxis() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(new Vector3(1, 0, 0));
    axis.updateAngularContributions();
    assertEquals(1.0, axis.getAngularContribution().x(), TOL);

    axis.setCurrentValue(new Vector3(0, 1, 0));
    axis.updateAngularContributions();
    assertEquals(0.0, axis.getAngularContribution().x(), TOL);
    assertEquals(1.0, axis.getAngularContribution().y(), TOL);
  }

  @Test
  public void invertThenUpdateLinearContributions() {
    Bone.Axis axis = new Bone.Axis(null, 0);
    axis.setCurrentValue(Vector3.POSITIVE_X_AXIS);
    axis.invertDirection();
    // now axis is (-1,0,0)
    axis.updateLinearContributions(Vector3.POSITIVE_Y_AXIS);
    // (-1,0,0) x (0,1,0) = (0,0,-1)
    assertEquals(0.0, axis.getLinearContribution().x(), TOL);
    assertEquals(0.0, axis.getLinearContribution().y(), TOL);
    assertEquals(-1.0, axis.getLinearContribution().z(), TOL);
  }
}
