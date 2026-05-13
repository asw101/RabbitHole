package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.lgna.ik.core.solver.Bone;
import org.lgna.ik.core.solver.Bone.Axis;
import org.lgna.ik.core.solver.Chain;

import java.util.Map;

public class PositionConstraint extends Constraint {
  private Point3 eeDesiredPosition;

  public PositionConstraint(Chain chain, Point3 eeDesiredPosition, IkEnforcerContext context) {
    super(chain, context);
    this.eeDesiredPosition = eeDesiredPosition;
  }

  @Override
  public boolean isMet() {
    Vector3 desiredVector = computeDesiredVector();
    return desiredVector.magnitudeSquared() < MIN_DISTANCE_SQUARED_BEFORE_CONSTRAINT_IS_MET;
  }

  public void setEeDesiredPosition(Point3 position) {
    this.eeDesiredPosition = position;
  }

  @Override
  public Displacement computeDesiredDisplacement() {
    Vector3 dispPoint = computeDesiredVector();
    return new Displacement(new double[] {dispPoint.x(), dispPoint.y(), dispPoint.z()});
  }

  private Vector3 computeDesiredVector() {
    return eeDesiredPosition.minus(chain.getEndEffectorPosition());
  }

  @Override
  public Jacobian computeJacobian() {
    chain.updateStateFromJoints();

    Map<Bone, Map<Axis, Vector3>> velocityContributions = chain.computeLinearVelocityContributions();

    updateJacobianUsingVelocityContributions(velocityContributions);

    return jacobian;
  }
}
