package org.lgna.ik.core.solver;

import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.bound.CumulativeBound;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisRotation;
import org.alice.math.immutable.Dimension3;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.alice.math.immutable.Vector3;
import org.junit.Test;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.resources.JointId;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BoneTest {
  private static final double EPS = 1.0e-9;

  public static final class NamedJointId extends JointId {
    private final String name;

    public NamedJointId(String name) {
      this(name, null);
    }

    public NamedJointId(String name, JointId parent) {
      super(parent, null);
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  public static final class TestJointImp extends JointImp {
    private final JointId jointId;
    private final Joint joint = new Joint();

    public TestJointImp(String name, JointId jointId, AffineMatrix4x4 transform,
                        boolean freeX, boolean freeY, boolean freeZ) {
      super(null);
      this.jointId = jointId;
      putInstance(this.joint);
      this.joint.jointID.setValue(name);
      this.joint.isFreeInX.setValue(freeX);
      this.joint.isFreeInY.setValue(freeY);
      this.joint.isFreeInZ.setValue(freeZ);
      this.joint.setLocalTransformation(transform);
    }

    public void setTransform(AffineMatrix4x4 transform) {
      this.joint.setLocalTransformation(transform);
    }

    @Override
    public JointId getJointId() {
      return jointId;
    }

    @Override
    public Joint getSgComposite() {
      return joint;
    }

    @Override
    public boolean isFreeInX() {
      return joint.isFreeInX.getValue();
    }

    @Override
    public boolean isFreeInY() {
      return joint.isFreeInY.getValue();
    }

    @Override
    public boolean isFreeInZ() {
      return joint.isFreeInZ.getValue();
    }

    @Override
    public UnitQuaternion getOriginalOrientation() {
      return UnitQuaternion.IDENTITY;
    }

    @Override
    public AffineMatrix4x4 getScaledOriginalTransformation() {
      return getLocalTransformation();
    }

    @Override
    public void setScale(Dimension3 scale) {
    }

    @Override
    public boolean isReoriented() {
      return false;
    }

    @Override
    public boolean isRelocated() {
      return false;
    }

    @Override
    protected void copyOnto(JointImp newJoint) {
    }

    @Override
    protected void updateCumulativeBound(CumulativeBound rv, AffineMatrix4x4 trans) {
    }
  }

  public static Chain createChain(TestJointImp... joints) {
    List<Bone.Direction> directions = new ArrayList<Bone.Direction>();
    for (int i = 0; i < joints.length; i++) {
      directions.add(Bone.Direction.DOWNSTREAM);
    }
    return createChain(Arrays.asList(joints), directions);
  }

  public static Chain createChain(List<TestJointImp> joints, List<Bone.Direction> directions) {
    try {
      Constructor<Chain> ctor = Chain.class.getDeclaredConstructor(List.class, List.class);
      ctor.setAccessible(true);
      return ctor.newInstance(new ArrayList<JointImp>(joints), new ArrayList<Bone.Direction>(directions));
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  public static Bone.Axis axisFor(Bone bone, int originalIndexInJoint) {
    for (Bone.Axis axis : bone.getAxes()) {
      if (axis.getOriginalIndexInJoint() == originalIndexInJoint) {
        return axis;
      }
    }
    throw new AssertionError("Missing axis " + originalIndexInJoint);
  }

  private static void assertVectorEquals(Vector3 expected, Vector3 actual) {
    assertEquals(expected.x(), actual.x(), EPS);
    assertEquals(expected.y(), actual.y(), EPS);
    assertEquals(expected.z(), actual.z(), EPS);
  }

  @Test
  public void axisConstructorStartsWithZeroState() {
    TestJointImp joint = new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Bone bone = createChain(joint).getBones()[0];
    Bone.Axis axis = new Bone.Axis(bone, 0);

    assertSame(bone, axis.getBone());
    assertEquals(0, axis.getOriginalIndexInJoint());
    assertVectorEquals(Vector3.ZERO, axis.getCurrentValue());
    assertVectorEquals(Vector3.ZERO, axis.getLinearContribution());
    assertVectorEquals(Vector3.ZERO, axis.getAngularContribution());
  }

  @Test
  public void getLocalAxisReturnsPositiveXAxisForIndexZero() {
    Bone.Axis axis = new Bone.Axis(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false)).getBones()[0], 0);
    assertVectorEquals(Vector3.POSITIVE_X_AXIS, axis.getLocalAxis());
  }

  @Test
  public void getLocalAxisReturnsPositiveYAxisForIndexOne() {
    Bone.Axis axis = new Bone.Axis(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, false, true, false)).getBones()[0], 1);
    assertVectorEquals(Vector3.POSITIVE_Y_AXIS, axis.getLocalAxis());
  }

  @Test
  public void getLocalAxisReturnsPositiveZAxisForIndexTwo() {
    Bone.Axis axis = new Bone.Axis(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, false, false, true)).getBones()[0], 2);
    assertVectorEquals(Vector3.POSITIVE_Z_AXIS, axis.getLocalAxis());
  }

  @Test(expected = RuntimeException.class)
  public void getLocalAxisRejectsInvalidIndex() {
    Bone.Axis axis = new Bone.Axis(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true)).getBones()[0], 3);
    axis.getLocalAxis();
  }

  @Test
  public void updateLinearContributionsUsesCrossProduct() {
    Bone.Axis axis = axisFor(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false)).getBones()[0], 0);
    axis.setCurrentValue(new Vector3(1.0, 2.0, 3.0));

    axis.updateLinearContributions(new Vector3(4.0, 5.0, 6.0));

    assertVectorEquals(new Vector3(-3.0, 6.0, -3.0), axis.getLinearContribution());
  }

  @Test
  public void updateAngularContributionsCopiesCurrentAxis() {
    Bone.Axis axis = axisFor(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false)).getBones()[0], 0);
    axis.setCurrentValue(new Vector3(7.0, 8.0, 9.0));

    axis.updateAngularContributions();

    assertVectorEquals(new Vector3(7.0, 8.0, 9.0), axis.getAngularContribution());
  }

  @Test
  public void invertDirectionNegatesCurrentAxis() {
    Bone.Axis axis = axisFor(createChain(new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false)).getBones()[0], 0);
    axis.setCurrentValue(new Vector3(2.0, -4.0, 6.0));

    axis.invertDirection();

    assertVectorEquals(new Vector3(-2.0, 4.0, -6.0), axis.getCurrentValue());
  }

  @Test
  public void equalsAndHashCodeMatchForSameJointAndIndex() {
    TestJointImp joint = new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Bone bone = createChain(joint).getBones()[0];
    Bone.Axis a = new Bone.Axis(bone, 0);
    Bone.Axis b = new Bone.Axis(bone, 0);

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void equalsDiffersForDifferentIndexOrBone() {
    Bone firstBone = createChain(new TestJointImp("jointA", new NamedJointId("jointA"), AffineMatrix4x4.IDENTITY, true, true, false)).getBones()[0];
    Bone secondBone = createChain(new TestJointImp("jointB", new NamedJointId("jointB"), AffineMatrix4x4.IDENTITY, true, false, false)).getBones()[0];

    assertNotEquals(new Bone.Axis(firstBone, 0), new Bone.Axis(firstBone, 1));
    assertNotEquals(new Bone.Axis(firstBone, 0), new Bone.Axis(secondBone, 0));
    assertFalse(new Bone.Axis(firstBone, 0).equals("not an axis"));
  }

  @Test
  public void updateStateFromJointReadsJointOrientationAndAnchor() {
    OrthogonalMatrix3x3 orientation = new AxisRotation(Vector3.POSITIVE_Z_AXIS, new AngleInRadians(Math.PI / 2.0)).asMatrix3x3();
    TestJointImp joint = new TestJointImp(
        "joint",
        new NamedJointId("joint"),
        new AffineMatrix4x4(orientation, new Point3(1.0, 2.0, 3.0)),
        true,
        true,
        true);
    Bone bone = createChain(joint).getBones()[0];

    bone.updateStateFromJoint();

    assertEquals(1.0, bone.getAnchorPosition().x(), EPS);
    assertEquals(2.0, bone.getAnchorPosition().y(), EPS);
    assertEquals(3.0, bone.getAnchorPosition().z(), EPS);
    assertVectorEquals(orientation.getRight(), axisFor(bone, 0).getCurrentValue());
    assertVectorEquals(orientation.getUp(), axisFor(bone, 1).getCurrentValue());
    assertVectorEquals(orientation.getBackward(), axisFor(bone, 2).getCurrentValue());
  }

  @Test
  public void updateStateFromJointInvertsAxesForUpstreamDirection() {
    TestJointImp joint = new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = createChain(Arrays.asList(joint), Arrays.asList(Bone.Direction.UPSTREAM));
    Bone bone = chain.getBones()[0];

    bone.updateStateFromJoint();

    assertVectorEquals(Vector3.NEGATIVE_X_AXIS, axisFor(bone, 0).getCurrentValue());
  }

  @Test
  public void applyRotationRotatesJointAroundOriginalLocalAxis() {
    TestJointImp joint = new TestJointImp("joint", new NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Bone bone = createChain(joint).getBones()[0];
    Bone.Axis axis = axisFor(bone, 0);

    axis.applyRotation(Math.PI / 2.0);

    assertFalse(joint.getLocalOrientation().isWithinReasonableEpsilonOf(OrthogonalMatrix3x3.IDENTITY));
  }
}
