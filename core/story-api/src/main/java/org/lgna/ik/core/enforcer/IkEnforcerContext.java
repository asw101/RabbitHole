package org.lgna.ik.core.enforcer;

import java.util.List;
import java.util.Map;

/**
 * Abstraction providing axis-index mappings needed by extracted IK classes
 * (Jacobian, AngleDeltas, NullspaceProjector, Constraint, PriorityLevel).
 * Implemented by TightPositionalIkEnforcer; stubbed in tests.
 */
public interface IkEnforcerContext {
  List<JacobianAxis> getIndexToAxis();
  Map<JacobianAxis, Integer> getAxisToIndex();
  int getGlobalIndexForAxis(JacobianAxis axis);
}
