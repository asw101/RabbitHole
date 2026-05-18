package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class TightPositionalIkEnforcerTest {
  private static final double EPS = 1.0e-6;

  private static Unsafe unsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static void setPrivateField(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static Object invokePrivate(TightPositionalIkEnforcer enforcer, String methodName, Class<?>[] parameterTypes, Object... args) {
    try {
      Method method = TightPositionalIkEnforcer.class.getDeclaredMethod(methodName, parameterTypes);
      method.setAccessible(true);
      return method.invoke(enforcer, args);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static TightPositionalIkEnforcer newEnforcer(List<JacobianAxis> axes) {
    try {
      TightPositionalIkEnforcer enforcer = (TightPositionalIkEnforcer) unsafe().allocateInstance(TightPositionalIkEnforcer.class);
      enforcer.indexToAxis = axes;
      enforcer.axesByIndexInJoint = new HashMap<>();
      enforcer.axisToIndex = new HashMap<>();
      for (int i = 0; i < axes.size(); i++) {
        JacobianAxis axis = axes.get(i);
        enforcer.axisToIndex.put(axis, i);
        enforcer.axesByIndexInJoint.computeIfAbsent(axis.jointImp, key -> new ArrayList<JacobianAxis>()).add(axis);
      }
      enforcer.nullspaceProjector = new NullspaceProjector(axes.size(), enforcer);
      setPrivateField(enforcer, "priorityLevels", new ArrayList<PriorityLevel>());
      setPrivateField(enforcer, "activePositionConstraints", new ArrayList<PositionConstraint>());
      setPrivateField(enforcer, "activeOrientationConstraints", new ArrayList<OrientationConstraint>());
      return enforcer;
    } catch (InstantiationException e) {
      throw new AssertionError(e);
    }
  }

  @SuppressWarnings("unchecked")
  private static List<PriorityLevel> getPriorityLevels(TightPositionalIkEnforcer enforcer) {
    try {
      Field field = TightPositionalIkEnforcer.class.getDeclaredField("priorityLevels");
      field.setAccessible(true);
      return (List<PriorityLevel>) field.get(enforcer);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static AngleDeltas getAngleDeltas(TightPositionalIkEnforcer enforcer) {
    try {
      Field field = TightPositionalIkEnforcer.class.getDeclaredField("angleDeltas");
      field.setAccessible(true);
      return (AngleDeltas) field.get(enforcer);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static TightPositionalIkEnforcer createTwoAxisEnforcer() {
    List<JacobianAxis> axes = new ArrayList<JacobianAxis>();
    axes.add(new JacobianAxis(null, 0));
    axes.add(new JacobianAxis(null, 1));
    return newEnforcer(axes);
  }

  @Test
  public void getIndexToAxisReturnsConfiguredAxes() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    assertEquals(2, enforcer.getIndexToAxis().size());
  }

  @Test
  public void getAxisToIndexReturnsConfiguredMapping() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    assertEquals(Integer.valueOf(0), enforcer.getAxisToIndex().get(enforcer.getIndexToAxis().get(0)));
    assertEquals(Integer.valueOf(1), enforcer.getAxisToIndex().get(enforcer.getIndexToAxis().get(1)));
  }

  @Test
  public void getGlobalIndexForAxisLooksUpConfiguredIndex() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    assertEquals(1, enforcer.getGlobalIndexForAxis(enforcer.getIndexToAxis().get(1)));
  }

  @Test
  public void enforceConstraintsWithNoPriorityLevelsInitializesAngleDeltas() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    enforcer.enforceConstraints();
    assertNotNull(getAngleDeltas(enforcer));
    assertEquals(2, getAngleDeltas(enforcer).storage.length);
  }

  @Test
  public void emptyPriorityLevelsAreAlreadyMet() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    assertSame(Boolean.TRUE, invokePrivate(enforcer, "areConstraintsMet", new Class<?>[0]));
  }

  @Test
  public void createRemainingDesiredDisplacementSubtractsAlreadyMovedAmount() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    Jacobian jacobian = new Jacobian(new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}}),
        enforcer.getIndexToAxis().toArray(new JacobianAxis[0]), enforcer);
    AngleDeltas angleDeltas = new AngleDeltas(2, enforcer);
    angleDeltas.storage[0] = 1.0;
    angleDeltas.storage[1] = 2.0;
    Displacement desired = new Displacement(new double[]{5.0, 6.0});

    Displacement remaining = (Displacement) invokePrivate(enforcer,
        "createRemainingDesiredDisplacement",
        new Class<?>[]{Displacement.class, Jacobian.class, AngleDeltas.class},
        desired, jacobian, angleDeltas);

    assertEquals(4.0, remaining.storage[0], EPS);
    assertEquals(4.0, remaining.storage[1], EPS);
  }

  @Test
  public void createRemainingDesiredDisplacementReturnsOriginalWhenNoAnglesApplied() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    Jacobian jacobian = new Jacobian(new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}}),
        enforcer.getIndexToAxis().toArray(new JacobianAxis[0]), enforcer);
    Displacement desired = new Displacement(new double[]{3.0, 7.0});

    Displacement remaining = (Displacement) invokePrivate(enforcer,
        "createRemainingDesiredDisplacement",
        new Class<?>[]{Displacement.class, Jacobian.class, AngleDeltas.class},
        desired, jacobian, new AngleDeltas(2, enforcer));

    assertEquals(3.0, remaining.storage[0], EPS);
    assertEquals(7.0, remaining.storage[1], EPS);
  }

  @Test
  public void addRequiredMotionToCurrentAngleDeltasUsesJacobianInverse() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    Jacobian jacobian = new Jacobian(new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}}),
        enforcer.getIndexToAxis().toArray(new JacobianAxis[0]), enforcer);
    AngleDeltas current = new AngleDeltas(2, enforcer);
    Displacement desired = new Displacement(new double[]{3.0, 4.0});

    invokePrivate(enforcer,
        "addRequiredMotionToCurrentAngleDeltas",
        new Class<?>[]{AngleDeltas.class, Jacobian.class, Displacement.class},
        current, jacobian, desired);

    double dampedFactor = 1.0 / (1.0 + 0.01);
    assertEquals(3.0 * dampedFactor, current.storage[0], EPS);
    assertEquals(4.0 * dampedFactor, current.storage[1], EPS);
  }

  @Test
  public void updateNullspaceProjectorSubtractsInverseTimesJacobian() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    Jacobian jacobian = new Jacobian(new Matrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}}),
        enforcer.getIndexToAxis().toArray(new JacobianAxis[0]), enforcer);
    NullspaceProjector projector = new NullspaceProjector(2, enforcer);
    projector.initializeToIdentity();

    invokePrivate(enforcer,
        "updateNullspaceProjector",
        new Class<?>[]{NullspaceProjector.class, Jacobian.class},
        projector, jacobian);

    assertEquals(0.0, projector.matrix[0][0], EPS);
    assertEquals(0.0, projector.matrix[1][1], EPS);
  }

  @Test
  public void priorityLevelsCanBeMutatedAfterUnsafeConstruction() {
    TightPositionalIkEnforcer enforcer = createTwoAxisEnforcer();
    getPriorityLevels(enforcer).add(new PriorityLevel(enforcer));
    assertEquals(1, getPriorityLevels(enforcer).size());
  }
}
