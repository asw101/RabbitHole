package org.lgna.ik.core.enforcer;

import java.util.ArrayList;
import java.util.List;

public class PriorityLevel {
  public int levelIndex;
  public List<Constraint> constraints = new ArrayList<Constraint>();
  private final IkEnforcerContext context;

  private Jacobian currentJacobian;

  public PriorityLevel(IkEnforcerContext context) {
    this.context = context;
  }

  public Jacobian computeAugmentedJacobian() {
    Jacobian[] jacobians = new Jacobian[constraints.size()];
    int i = 0;
    for (Constraint constraint : constraints) {
      Jacobian jacobian = constraint.computeJacobian();
      jacobians[i] = jacobian;
      ++i;
    }

    currentJacobian = augmentJacobians(jacobians);
    return currentJacobian;
  }

  public Jacobian getCurrentJacobian() {
    return currentJacobian;
  }

  private Jacobian augmentJacobians(Jacobian[] jacobians) {
    return new Jacobian(jacobians, context);
  }

  private Displacement currentAugmentedDesiredDisplacement;

  public Displacement computeAugmentedDesiredDisplacement() {
    Displacement[] displacements = new Displacement[constraints.size()];
    int size = 0;
    int i = 0;
    for (Constraint constraint : constraints) {
      Displacement displacement = constraint.computeDesiredDisplacement();
      displacements[i] = displacement;
      ++i;
      size += displacement.storage.length;
    }

    currentAugmentedDesiredDisplacement = augmentDisplacements(displacements, size);
    return currentAugmentedDesiredDisplacement;
  }

  public Displacement getCurrentAugmentedDesiredDisplacement() {
    return currentAugmentedDesiredDisplacement;
  }

  private Displacement augmentDisplacements(Displacement[] displacements, int size) {
    double[] d = new double[size];
    int largeIndex = 0;
    for (Displacement displacement : displacements) {
      for (int i = 0; i < displacement.size(); ++i) {
        d[largeIndex + i] = displacement.storage[i];
      }
      largeIndex += displacement.size();
    }
    return new Displacement(d);
  }

  public boolean areConstraintsMet() {
    for (Constraint constraint : constraints) {
      if (!constraint.isMet()) {
        return false;
      }
    }
    return true;
  }

  public void addConstraint(Constraint constraint) {
    constraints.add(constraint);
  }
}
