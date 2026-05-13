package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.alice.math.immutable.Vector3;
import org.lgna.ik.core.solver.Bone;
import org.lgna.ik.core.solver.Bone.Axis;
import org.lgna.ik.core.solver.Chain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Constraint {
  Chain chain;
  Jacobian jacobian;
  Axis[] axes;
  protected final IkEnforcerContext context;

  static final double MIN_ANGLE_IN_RADIANS_BEFORE_CONSTRAINT_IS_MET = Math.PI * .0001;
  static final double MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET = .0001;
  static final double MIN_DISTANCE_SQUARED_BEFORE_CONSTRAINT_IS_MET = MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET * MIN_DISTANCE_BEFORE_CONSTRAINT_IS_MET;

  protected Constraint(Chain chain, IkEnforcerContext context) {
    this.chain = chain;
    this.context = context;
  }

  public abstract boolean isMet();

  public abstract Displacement computeDesiredDisplacement();

  public abstract Jacobian computeJacobian();

  protected void updateJacobianUsingVelocityContributions(Map<Bone, Map<Axis, Vector3>> velocityContributions) {
    boolean isJacobianInitialized = jacobian != null;

    List<JacobianAxis> indexToAxis = context.getIndexToAxis();

    if (!isJacobianInitialized) {
      List<JacobianAxis> jacobianAxisList = new ArrayList<JacobianAxis>();
      List<Vector3> contributionsList = new ArrayList<Vector3>();
      List<Axis> axisList = new ArrayList<Axis>();

      for (int i = 0; i < indexToAxis.size(); ++i) {
        JacobianAxis jacobianAxis = indexToAxis.get(i);

        for (Entry<Bone, Map<Axis, Vector3>> e : velocityContributions.entrySet()) {
          Bone bone = e.getKey();
          Map<Axis, Vector3> axisMap = e.getValue();

          if (bone.getA() == jacobianAxis.jointImp) {
            for (Entry<Axis, Vector3> ea : axisMap.entrySet()) {
              Axis axis = ea.getKey();
              Vector3 contribution = ea.getValue();

              if (axis.getOriginalIndexInJoint() == jacobianAxis.axisInBoneIndex) {
                assert !jacobianAxisList.contains(jacobianAxis);

                jacobianAxisList.add(jacobianAxis);
                contributionsList.add(contribution);
                axisList.add(axis);
              }
            }
          }
        }
      }

      JacobianAxis[] jacobianAxes = jacobianAxisList.toArray(new JacobianAxis[jacobianAxisList.size()]);

      int ji = 0;
      Matrix mj = new Matrix(3, jacobianAxes.length);

      for (Vector3 contribution : contributionsList) {
        mj.set(0, ji, contribution.x());
        mj.set(1, ji, contribution.y());
        mj.set(2, ji, contribution.z());

        ++ji;
      }

      jacobian = new Jacobian(mj, jacobianAxes, context);

      axes = axisList.toArray(new Axis[jacobianAxes.length]);
    } else {
      assert axes.length == jacobian.columnIndexToJacobianColumn.length;

      for (int ji = 0; ji < jacobian.columnIndexToJacobianColumn.length; ++ji) {
        JacobianAxis jacobianAxis = jacobian.columnIndexToJacobianColumn[ji];

        Axis axis = axes[ji];

        Vector3 contribution = velocityContributions.get(axis.getBone()).get(axis);

        jacobian.matrix.set(0, ji, contribution.x());
        jacobian.matrix.set(1, ji, contribution.y());
        jacobian.matrix.set(2, ji, contribution.z());
      }

      jacobian.matrixWasUpdated();
    }
  }
}
