package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Vector3;
import org.junit.Test;
import org.lgna.ik.core.solver.Bone;
import org.lgna.ik.core.solver.BoneTest;
import org.lgna.ik.core.solver.Chain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ConstraintTest {
  private static final double EPS = 1.0e-9;

  private static final class TestConstraint extends Constraint {
    private Map<Bone, Map<Bone.Axis, Vector3>> contributions = Collections.emptyMap();

    private TestConstraint(Chain chain, IkEnforcerContext context) {
      super(chain, context);
    }

    public void setContributions(Map<Bone, Map<Bone.Axis, Vector3>> contributions) {
      this.contributions = contributions;
    }

    @Override
    public boolean isMet() {
      return false;
    }

    @Override
    public Displacement computeDesiredDisplacement() {
      return new Displacement(new double[]{0.0, 0.0, 0.0});
    }

    @Override
    public Jacobian computeJacobian() {
      updateJacobianUsingVelocityContributions(contributions);
      return jacobian;
    }
  }

  private static Map<Bone, Map<Bone.Axis, Vector3>> contributionsFor(Bone bone, Vector3 x, Vector3 y, Vector3 z) {
    Map<Bone.Axis, Vector3> axisMap = new HashMap<Bone.Axis, Vector3>();
    if (x != null) {
      axisMap.put(BoneTest.axisFor(bone, 0), x);
    }
    if (y != null) {
      for (Bone.Axis axis : bone.getAxes()) {
        if (axis.getOriginalIndexInJoint() == 1) {
          axisMap.put(axis, y);
        }
      }
    }
    if (z != null) {
      for (Bone.Axis axis : bone.getAxes()) {
        if (axis.getOriginalIndexInJoint() == 2) {
          axisMap.put(axis, z);
        }
      }
    }
    Map<Bone, Map<Bone.Axis, Vector3>> result = new HashMap<Bone, Map<Bone.Axis, Vector3>>();
    result.put(bone, axisMap);
    return result;
  }

  @Test
  public void minimumConstraintThresholdsArePositive() {
    assertTrue(Constraint.MIN_ANGLE_IN_RADIANS_BEFORE_CONSTRAINT_IS_MET > 0.0);
    assertTrue(Constraint.MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET > 0.0);
    assertEquals(
        Constraint.MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET * Constraint.MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET,
        Constraint.MIN_DISTANCE_SQUARED_BEFORE_CONSTRAINT_IS_MET,
        EPS);
  }

  @Test
  public void updateJacobianUsingVelocityContributionsInitializesJacobianMatrix() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(
        new JacobianAxis(joint, 0),
        new JacobianAxis(joint, 1),
        new JacobianAxis(joint, 2)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone,
        new Vector3(1.0, 2.0, 3.0),
        new Vector3(4.0, 5.0, 6.0),
        new Vector3(7.0, 8.0, 9.0)));

    Jacobian jacobian = constraint.computeJacobian();

    assertNotNull(jacobian);
    assertEquals(3, jacobian.getRowCount());
    assertEquals(3, jacobian.matrix.getColumnDimension());
    assertEquals(1.0, jacobian.matrix.get(0, 0), EPS);
    assertEquals(5.0, jacobian.matrix.get(1, 1), EPS);
    assertEquals(9.0, jacobian.matrix.get(2, 2), EPS);
  }

  @Test
  public void updateJacobianUsingVelocityContributionsStoresMatchingAxesArray() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(
        new JacobianAxis(joint, 0),
        new JacobianAxis(joint, 1),
        new JacobianAxis(joint, 2)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone, Vector3.ZERO, Vector3.ZERO, Vector3.ZERO));

    constraint.computeJacobian();

    assertEquals(3, constraint.axes.length);
    assertEquals(0, constraint.axes[0].getOriginalIndexInJoint());
    assertEquals(1, constraint.axes[1].getOriginalIndexInJoint());
    assertEquals(2, constraint.axes[2].getOriginalIndexInJoint());
  }

  @Test
  public void recomputingJacobianReusesSameJacobianObject() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(
        new JacobianAxis(joint, 0),
        new JacobianAxis(joint, 1),
        new JacobianAxis(joint, 2)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone, Vector3.POSITIVE_X_AXIS, Vector3.POSITIVE_Y_AXIS, Vector3.POSITIVE_Z_AXIS));
    Jacobian first = constraint.computeJacobian();

    constraint.setContributions(contributionsFor(bone,
        new Vector3(2.0, 0.0, 0.0),
        new Vector3(0.0, 2.0, 0.0),
        new Vector3(0.0, 0.0, 2.0)));
    Jacobian second = constraint.computeJacobian();

    assertSame(first, second);
    assertEquals(2.0, second.matrix.get(0, 0), EPS);
    assertEquals(2.0, second.matrix.get(1, 1), EPS);
    assertEquals(2.0, second.matrix.get(2, 2), EPS);
  }

  @Test
  public void recomputingJacobianInvalidatesCachedSvdInfo() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(new JacobianAxis(joint, 0)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone, Vector3.POSITIVE_X_AXIS, null, null));
    Jacobian jacobian = constraint.computeJacobian();
    jacobian.createInverseForNullProjection();

    constraint.setContributions(contributionsFor(bone, new Vector3(2.0, 0.0, 0.0), null, null));
    constraint.computeJacobian();

    assertTrue(jacobian.svdInfo == null);
  }

  @Test
  public void contextFiltersColumnsByJointAxisIndex() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, true, true);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(
        new JacobianAxis(joint, 0),
        new JacobianAxis(joint, 2)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone,
        new Vector3(1.0, 0.0, 0.0),
        new Vector3(0.0, 1.0, 0.0),
        new Vector3(0.0, 0.0, 1.0)));

    Jacobian jacobian = constraint.computeJacobian();

    assertEquals(2, jacobian.matrix.getColumnDimension());
  }

  @Test
  public void singleAxisJointProducesSingleJacobianColumn() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(new JacobianAxis(joint, 0)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone, new Vector3(3.0, 4.0, 5.0), null, null));

    Jacobian jacobian = constraint.computeJacobian();

    assertEquals(1, jacobian.matrix.getColumnDimension());
    assertEquals(3.0, jacobian.matrix.get(0, 0), EPS);
    assertEquals(4.0, jacobian.matrix.get(1, 0), EPS);
    assertEquals(5.0, jacobian.matrix.get(2, 0), EPS);
  }

  @Test
  public void computeJacobianReturnsSameReferenceAsStoredJacobianField() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false);
    Chain chain = BoneTest.createChain(joint);
    Bone bone = chain.getBones()[0];
    IkEnforcerContextStub context = new IkEnforcerContextStub(Arrays.asList(new JacobianAxis(joint, 0)));
    TestConstraint constraint = new TestConstraint(chain, context);
    constraint.setContributions(contributionsFor(bone, Vector3.POSITIVE_X_AXIS, null, null));

    Jacobian jacobian = constraint.computeJacobian();

    assertSame(jacobian, constraint.jacobian);
  }
}
