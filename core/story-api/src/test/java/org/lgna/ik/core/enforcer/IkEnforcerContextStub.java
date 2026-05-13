package org.lgna.ik.core.enforcer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of IkEnforcerContext for testing extracted classes
 * without requiring a real JointedModelImp. Creates JacobianAxis instances
 * with null JointImp references — safe because tests exercise pure math,
 * not joint-model integration.
 */
class IkEnforcerContextStub implements IkEnforcerContext {
  private final List<JacobianAxis> indexToAxis;
  private final Map<JacobianAxis, Integer> axisToIndex;

  IkEnforcerContextStub(int numAxes) {
    indexToAxis = new ArrayList<>();
    axisToIndex = new HashMap<>();
    for (int i = 0; i < numAxes; i++) {
      JacobianAxis axis = new JacobianAxis(null, i % 3);
      indexToAxis.add(axis);
      axisToIndex.put(axis, i);
    }
  }

  IkEnforcerContextStub(List<JacobianAxis> axes) {
    indexToAxis = new ArrayList<>(axes);
    axisToIndex = new HashMap<>();
    for (int i = 0; i < axes.size(); i++) {
      axisToIndex.put(axes.get(i), i);
    }
  }

  @Override
  public List<JacobianAxis> getIndexToAxis() {
    return indexToAxis;
  }

  @Override
  public Map<JacobianAxis, Integer> getAxisToIndex() {
    return axisToIndex;
  }

  @Override
  public int getGlobalIndexForAxis(JacobianAxis axis) {
    Integer idx = axisToIndex.get(axis);
    assert idx != null : "Unknown axis in stub context";
    return idx;
  }
}
