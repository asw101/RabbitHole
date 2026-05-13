package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.lgna.ik.core.solver.Chain;
import org.lgna.story.implementation.AsSeenBy;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.resources.JointId;

import java.util.*;
import java.util.Map.Entry;

public class TightPositionalIkEnforcer extends IkEnforcer implements IkEnforcerContext {

  // Ordered by priority
  private List<PriorityLevel> priorityLevels = new ArrayList<PriorityLevel>();
  private List<PositionConstraint> activePositionConstraints = new ArrayList<PositionConstraint>();
  private List<OrientationConstraint> activeOrientationConstraints = new ArrayList<OrientationConstraint>();

  // All joints in the model, indexed for Jacobian construction
  List<JacobianAxis> indexToAxis;
  Map<JointImp, List<JacobianAxis>> axesByIndexInJoint;
  Map<JacobianAxis, Integer> axisToIndex;

  NullspaceProjector nullspaceProjector;

  //this is the cumulative angledeltas. just like nullspaceprojector.
  AngleDeltas angleDeltas;

  public TightPositionalIkEnforcer(JointedModelImp<?, ?> jointedModelImp) {
    super(jointedModelImp);
    initializeListOfAxes();
  }

  // --- IkEnforcerContext implementation ---

  @Override
  public List<JacobianAxis> getIndexToAxis() {
    return indexToAxis;
  }

  @Override
  public Map<JacobianAxis, Integer> getAxisToIndex() {
    return axisToIndex;
  }

  @Override
  public int getGlobalIndexForAxis(JacobianAxis jacobianAxis) {
    Integer globalIndex = axisToIndex.get(jacobianAxis);
    assert globalIndex != null;
    return globalIndex;
  }

  // --- Initialization ---

  private void initializeListOfAxes() {
    indexToAxis = new ArrayList<JacobianAxis>();
    axesByIndexInJoint = new HashMap<JointImp, List<JacobianAxis>>();
    axisToIndex = new HashMap<JacobianAxis, Integer>();

    Iterable<JointImp> jointImps = jointedModelImp.getJoints();

    int globalIndex = 0;

    for (JointImp jointImp : jointImps) {
      List<JacobianAxis> axesInJoint = new ArrayList<JacobianAxis>();
      axesByIndexInJoint.put(jointImp, axesInJoint);

      for (int indexInJoint = 0; indexInJoint < 3; ++indexInJoint) {
        JacobianAxis jacobianAxis = new JacobianAxis(jointImp, indexInJoint);
        indexToAxis.add(jacobianAxis);
        axesInJoint.add(jacobianAxis);
        axisToIndex.put(jacobianAxis, globalIndex);
        ++globalIndex;
      }
    }

    nullspaceProjector = new NullspaceProjector(indexToAxis.size(), this);
  }

  // --- Public API ---

  public void enforceConstraints() {
    //convergence loop (constraints or max iter)
    //clamping loop (joint limits, clamping)
    //priority loop (apply constraints in current null space)
    convergenceLoop();
  }

  public PositionConstraint createPositionConstraint(int level, JointId anchorId, JointId endId) {
    Chain chain = Chain.createInstance(jointedModelImp, anchorId, endId);

    Point3 endPosition = jointedModelImp.getJointImplementation(endId).getTransformation(AsSeenBy.SCENE).translation().plus(Vector3.POSITIVE_Y_AXIS);

    PositionConstraint positionConstraint = new PositionConstraint(chain, endPosition, this);

    // call setEeDesiredPosition

    activePositionConstraints.add(positionConstraint);

    while ((priorityLevels.size() - 1) < level) {
      priorityLevels.add(new PriorityLevel(this));
    }
    priorityLevels.get(level).addConstraint(positionConstraint);

    return positionConstraint;
  }

  // --- Convergence loop ---

  private void convergenceLoop() {
    int numIterations = 0;
    int maxNumIterations = 10;

    angleDeltas = new AngleDeltas(indexToAxis.size(), this);

    while ((numIterations < maxNumIterations) && !areConstraintsMet()) {
      // compute current jacobians for all priority levels.
      for (PriorityLevel priorityLevel : priorityLevels) {
        priorityLevel.computeAugmentedJacobian();
        priorityLevel.computeAugmentedDesiredDisplacement();
      }

      nullspaceProjector.initializeToIdentity();

      // if I would have to do anything about locking state and joint limits, I would initialize them here.
      clampingLoop();
      ++numIterations;
    }
  }

  private boolean areConstraintsMet() {
    for (PriorityLevel priorityLevel : priorityLevels) {
      if (!priorityLevel.areConstraintsMet()) {
        return false;
      }
    }

    return true;
  }

  private void clampingLoop() {
    boolean isComputedWithClamping = true;

    while (isComputedWithClamping) {
      isComputedWithClamping = false; //make it true when you clamp

      priorityLoop();

      makeCloserToNaturalPose();

      isComputedWithClamping = applyAngleChangesAndClampingIfNecessary_NoClampingForNow();
    }
  }

  // --- Priority loop ---

  private void priorityLoop() {
    //loop through the priority levels and compute the angle delta vector incrementally with each
    int lastPriorityLevel = 0;
    boolean first = true;
    for (PriorityLevel priorityLevel : priorityLevels) {
      // verify the order (temporarily)
      if (!first) {
        if (lastPriorityLevel > priorityLevel.levelIndex) {
          throw new RuntimeException("priorities are not ordered " + lastPriorityLevel + " " + priorityLevel.levelIndex);
        }
      } else {
        first = false;
      }
      lastPriorityLevel = priorityLevel.levelIndex;
      // /verify the order (temporarily)

      priorityLoopIteration(priorityLevel);
    }
  }

  private void priorityLoopIteration(PriorityLevel priorityLevel) {
    Displacement desiredDisplacement = priorityLevel.getCurrentAugmentedDesiredDisplacement();

    Jacobian currentJacobian = priorityLevel.getCurrentJacobian();
    Displacement remainingDesiredDisplacement = createRemainingDesiredDisplacement(desiredDisplacement, currentJacobian, angleDeltas);

    // project jacobian on the nullspace
    Jacobian projectedJacobian = nullspaceProjector.createProjected(currentJacobian);

    // add to angle delta, compute nullspace projectors, etc
    addRequiredMotionToCurrentAngleDeltas(angleDeltas, projectedJacobian, remainingDesiredDisplacement);

    updateNullspaceProjector(nullspaceProjector, projectedJacobian);
  }

  private Displacement createRemainingDesiredDisplacement(Displacement desiredDisplacement, Jacobian currentJacobian, AngleDeltas currentAngleDeltas) {
    Displacement alreadyMoved = currentJacobian.multiplyWithAngleDeltas(currentAngleDeltas);
    return desiredDisplacement.createThisMinusOther(alreadyMoved);
  }

  private void addRequiredMotionToCurrentAngleDeltas(AngleDeltas currentAngleDeltas, Jacobian projectedJacobian, Displacement remainingDesiredDisplacement) {
    AngleDeltas angleDeltasToMove = projectedJacobian.multiplyDisplacementWithInverseForMoving(remainingDesiredDisplacement);
    //update currentAngleDeltas
    currentAngleDeltas.add(angleDeltasToMove);
  }

  private void updateNullspaceProjector(NullspaceProjector currentNullspaceProjector, Jacobian jacobian) {
    InvertedJacobian inverse = jacobian.createInverseForNullProjection();

    currentNullspaceProjector.subtractInverseTimesJacobian(inverse, jacobian);
  }

  private void makeCloserToNaturalPose() {
    // No-op: natural pose attraction via nullspace projector not yet implemented.
  }

  // --- Angle application ---

  //this is the version that never clamps
  private boolean applyAngleChangesAndClampingIfNecessary_NoClampingForNow() {
    boolean clamped = false;

    //basically, just rotate the joints and that's it.

    //I have axes for each joint
    for (Entry<JointImp, List<JacobianAxis>> e : axesByIndexInJoint.entrySet()) {
      JointImp jointImp = e.getKey();
      List<JacobianAxis> axesForJoint = e.getValue();

      if (axesForJoint.size() == 3) {
        // Accumulate rotation as primitive doubles to avoid intermediate Vector3 allocations
        double rx = 0, ry = 0, rz = 0;
        int axisIndexInJoint = 0;

        for (JacobianAxis axis : axesForJoint) {
          assert axis.isFree();
          double delta = angleDeltas.getForAxis(axis);

          switch (axisIndexInJoint) {
            case 0 -> rx = delta;
            case 1 -> ry = delta;
            case 2 -> rz = delta;
            default -> {
              assert false;
            }
          }
          ++axisIndexInJoint;
        }

        double angleInRadians = Math.sqrt(rx * rx + ry * ry + rz * rz);

        if (angleInRadians > 0) {
          Vector3 rotAxis = new Vector3(rx / angleInRadians, ry / angleInRadians, rz / angleInRadians);
          jointImp.applyRotationInRadians(rotAxis, angleInRadians);
        }
      } else {
        assert false : "Joint with other than three angles";
      }
    }

    return clamped;
  }
}
