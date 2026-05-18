package org.lgna.ik.core.solver;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisRotation;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ChainTest {
  private static final double EPS = 1.0e-9;

  private static void assertVectorEquals(Vector3 expected, Vector3 actual) {
    assertEquals(expected.x(), actual.x(), EPS);
    assertEquals(expected.y(), actual.y(), EPS);
    assertEquals(expected.z(), actual.z(), EPS);
  }

  private static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals(expected.x(), actual.x(), EPS);
    assertEquals(expected.y(), actual.y(), EPS);
    assertEquals(expected.z(), actual.z(), EPS);
  }

  @Test
  public void createChainBuildsOneBonePerJoint() {
    Chain chain = BoneTest.createChain(
        new BoneTest.TestJointImp("a", new BoneTest.NamedJointId("a"), AffineMatrix4x4.IDENTITY, true, false, false),
        new BoneTest.TestJointImp("b", new BoneTest.NamedJointId("b"), AffineMatrix4x4.IDENTITY, false, true, false));

    assertEquals(2, chain.getBones().length);
  }

  @Test
  public void getJointImpAtAndDirectionAtReflectConstructorInputs() {
    BoneTest.TestJointImp first = new BoneTest.TestJointImp("a", new BoneTest.NamedJointId("a"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = BoneTest.createChain(Collections.singletonList(first), Collections.singletonList(Bone.Direction.UPSTREAM));

    assertSame(first, chain.getJointImpAt(0));
    assertEquals(Bone.Direction.UPSTREAM, chain.getDirectionAt(0));
  }

  @Test
  public void setEndEffectorLocalPositionChangesReportedWorldPosition() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp(
        "joint", new BoneTest.NamedJointId("joint"),
        new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(2.0, 0.0, 0.0)),
        true, true, true);
    Chain chain = BoneTest.createChain(joint);

    chain.setEndEffectorLocalPosition(new Point3(0.0, 3.0, 0.0));

    assertPointEquals(new Point3(2.0, 3.0, 0.0), chain.getEndEffectorPosition());
  }

  @Test
  public void setEndEffectorPositionConvertsWorldPointToLocalCoordinates() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp(
        "joint", new BoneTest.NamedJointId("joint"),
        new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(5.0, 1.0, 0.0)),
        true, true, true);
    Chain chain = BoneTest.createChain(joint);

    chain.setEndEffectorPosition(new Point3(7.0, 4.0, 0.0));

    assertPointEquals(new Point3(7.0, 4.0, 0.0), chain.getEndEffectorPosition());
  }

  @Test
  public void getEndEffectorOrientationUsesLastJointOrientation() {
    OrthogonalMatrix3x3 orientation = new AxisRotation(Vector3.POSITIVE_X_AXIS, new AngleInRadians(Math.PI / 3.0)).asMatrix3x3();
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp(
        "joint", new BoneTest.NamedJointId("joint"),
        new AffineMatrix4x4(orientation, Point3.ORIGIN),
        true, true, true);
    Chain chain = BoneTest.createChain(joint);

    assertTrue(chain.getEndEffectorOrientation().isWithinReasonableEpsilonOf(orientation));
  }

  @Test
  public void computeLinearVelocityContributionsUsesEachAxisContribution() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    chain.setEndEffectorLocalPosition(new Point3(0.0, 1.0, 0.0));

    assertVectorEquals(Vector3.POSITIVE_Z_AXIS,
        chain.computeLinearVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 0)));
    assertVectorEquals(Vector3.ZERO,
        chain.computeLinearVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 1)));
    assertVectorEquals(Vector3.NEGATIVE_X_AXIS,
        chain.computeLinearVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 2)));
  }

  @Test
  public void computeAngularVelocityContributionsCopiesAxisValues() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];

    assertVectorEquals(Vector3.POSITIVE_X_AXIS,
        chain.computeAngularVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 0)));
    assertVectorEquals(Vector3.POSITIVE_Y_AXIS,
        chain.computeAngularVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 1)));
    assertVectorEquals(Vector3.POSITIVE_Z_AXIS,
        chain.computeAngularVelocityContributions().get(bone).get(BoneTest.axisFor(bone, 2)));
  }

  @Test
  public void updateStateFromJointsRefreshesAnchorAfterJointMove() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = BoneTest.createChain(joint);

    joint.setTransform(new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(9.0, 8.0, 7.0)));
    chain.updateStateFromJoints();

    assertPointEquals(new Point3(9.0, 8.0, 7.0), chain.getAnchorPosition());
  }

  @Test
  public void getLastJointImpReturnsLastJoint() {
    BoneTest.TestJointImp first = new BoneTest.TestJointImp("a", new BoneTest.NamedJointId("a"), AffineMatrix4x4.IDENTITY, true, false, false);
    BoneTest.TestJointImp last = new BoneTest.TestJointImp("b", new BoneTest.NamedJointId("b"), AffineMatrix4x4.IDENTITY, false, true, false);
    Chain chain = BoneTest.createChain(first, last);

    assertSame(last, chain.getLastJointImp());
  }

  @Test
  public void emptyChainReportsEmpty() {
    Chain empty = BoneTest.createChain(new ArrayList<BoneTest.TestJointImp>(), new ArrayList<Bone.Direction>());

    assertTrue(empty.isEmpty());
    assertEquals(0, empty.getBones().length);
  }
}
