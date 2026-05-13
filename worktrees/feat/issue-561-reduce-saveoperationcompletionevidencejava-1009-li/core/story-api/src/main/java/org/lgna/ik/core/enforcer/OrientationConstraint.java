package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.AxisRotation;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Vector3;
import org.lgna.ik.core.solver.Bone;
import org.lgna.ik.core.solver.Bone.Axis;
import org.lgna.ik.core.solver.Chain;

import java.util.Map;

public class OrientationConstraint extends Constraint {

  public OrientationConstraint(Chain chain, IkEnforcerContext context) {
    super(chain, context);
  }

  private OrthogonalMatrix3x3 eeDesiredOrientation;

  @Override
  public boolean isMet() {
    AxisRotation axisRotation = computeDesiredAxisRotation();

    return axisRotation.angle().getAsRadians() < MIN_ANGLE_IN_RADIANS_BEFORE_CONSTRAINT_IS_MET;
  }

  public void setEeDesiredOrientation(OrthogonalMatrix3x3 orientation) {
    this.eeDesiredOrientation = orientation;
  }

  @Override
  public Displacement computeDesiredDisplacement() {
    AxisRotation axisRotation = computeDesiredAxisRotation();

    Vector3 displacementVector = axisRotation.axis().times(axisRotation.angle().getAsRadians());

    return new Displacement(new double[] {displacementVector.x(), displacementVector.y(), displacementVector.z()});
  }

  private AxisRotation computeDesiredAxisRotation() {
    OrthogonalMatrix3x3 endEffectorOrientation = chain.getEndEffectorOrientation();

    OrthogonalMatrix3x3 inv = (OrthogonalMatrix3x3) endEffectorOrientation.invert();

    OrthogonalMatrix3x3 desiredRotation = (OrthogonalMatrix3x3) eeDesiredOrientation.times(inv);

    AxisRotation axisRotation = desiredRotation.asAxisRotation();
    return axisRotation;
  }

  @Override
  public Jacobian computeJacobian() {
    chain.updateStateFromJoints();

    Map<Bone, Map<Axis, Vector3>> velocityContributions = chain.computeAngularVelocityContributions();

    updateJacobianUsingVelocityContributions(velocityContributions);

    return jacobian;
  }
}
