package org.lgna.ik.core.enforcer;

import Jama.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Characterization tests for PriorityLevel — groups constraints at the same
 * priority and computes augmented Jacobians and displacements.
 *
 * Uses a minimal TestConstraint stub since Constraint is abstract and its
 * concrete subclasses (PositionConstraint, OrientationConstraint) depend on
 * Chain and JointImp. The stub provides known Jacobians and displacements.
 */
public class PriorityLevelTest {

  private static final double DELTA = 1e-6;

  private IkEnforcerContextStub context;
  private List<JacobianAxis> axes;

  @Before
  public void setUp() {
    context = new IkEnforcerContextStub(3);
    axes = context.getIndexToAxis();
  }

  /** Minimal Constraint stub for testing PriorityLevel aggregation. */
  private static class TestConstraint extends Constraint {
    private final Jacobian fixedJacobian;
    private final Displacement fixedDisplacement;
    private final boolean met;

    TestConstraint(Jacobian jacobian, Displacement displacement, boolean met) {
      super(null, null); // no Chain or context needed for this test
      this.fixedJacobian = jacobian;
      this.fixedDisplacement = displacement;
      this.met = met;
    }

    @Override
    public boolean isMet() {
      return met;
    }

    @Override
    public Displacement computeDesiredDisplacement() {
      return fixedDisplacement;
    }

    @Override
    public Jacobian computeJacobian() {
      return fixedJacobian;
    }
  }

  @Test
  public void addConstraintIncreasesListSize() {
    PriorityLevel pl = new PriorityLevel(context);
    assertEquals(0, pl.constraints.size());

    Jacobian j = new Jacobian(Matrix.identity(3, 3),
        new JacobianAxis[]{axes.get(0), axes.get(1), axes.get(2)}, context);
    Displacement d = new Displacement(new double[]{1, 2, 3});
    pl.addConstraint(new TestConstraint(j, d, false));

    assertEquals(1, pl.constraints.size());
  }

  @Test
  public void computeAugmentedJacobianWithSingleConstraint() {
    Matrix m = new Matrix(new double[][]{{1, 2}, {3, 4}, {5, 6}});
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);
    Displacement d = new Displacement(new double[]{0, 0, 0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j, d, false));

    Jacobian augmented = pl.computeAugmentedJacobian();
    assertNotNull(augmented);
    assertEquals(3, augmented.getRowCount());
  }

  @Test
  public void computeAugmentedDisplacementConcatenates() {
    Jacobian j1 = new Jacobian(Matrix.identity(2, 2),
        new JacobianAxis[]{axes.get(0), axes.get(1)}, context);
    Jacobian j2 = new Jacobian(new Matrix(3, 1),
        new JacobianAxis[]{axes.get(2)}, context);

    Displacement d1 = new Displacement(new double[]{1.0, 2.0});
    Displacement d2 = new Displacement(new double[]{3.0, 4.0, 5.0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j1, d1, false));
    pl.addConstraint(new TestConstraint(j2, d2, false));

    Displacement augmented = pl.computeAugmentedDesiredDisplacement();
    assertEquals(5, augmented.size());
    assertEquals(1.0, augmented.storage[0], DELTA);
    assertEquals(2.0, augmented.storage[1], DELTA);
    assertEquals(3.0, augmented.storage[2], DELTA);
    assertEquals(4.0, augmented.storage[3], DELTA);
    assertEquals(5.0, augmented.storage[4], DELTA);
  }

  @Test
  public void areConstraintsMetReturnsTrueWhenAllMet() {
    Jacobian j = new Jacobian(Matrix.identity(1, 1),
        new JacobianAxis[]{axes.get(0)}, context);
    Displacement d = new Displacement(new double[]{0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j, d, true));
    pl.addConstraint(new TestConstraint(j, d, true));

    assertTrue(pl.areConstraintsMet());
  }

  @Test
  public void areConstraintsMetReturnsFalseWhenAnyUnmet() {
    Jacobian j = new Jacobian(Matrix.identity(1, 1),
        new JacobianAxis[]{axes.get(0)}, context);
    Displacement d = new Displacement(new double[]{0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j, d, true));
    pl.addConstraint(new TestConstraint(j, d, false));

    assertFalse(pl.areConstraintsMet());
  }

  @Test
  public void emptyPriorityLevelConstraintsAreMet() {
    PriorityLevel pl = new PriorityLevel(context);
    assertTrue(pl.areConstraintsMet());
  }

  @Test
  public void getCurrentJacobianReturnsLastComputed() {
    Matrix m = Matrix.identity(2, 2);
    JacobianAxis[] cols = {axes.get(0), axes.get(1)};
    Jacobian j = new Jacobian(m, cols, context);
    Displacement d = new Displacement(new double[]{0, 0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j, d, false));

    assertNull(pl.getCurrentJacobian());
    pl.computeAugmentedJacobian();
    assertNotNull(pl.getCurrentJacobian());
  }

  @Test
  public void getCurrentDisplacementReturnsLastComputed() {
    Jacobian j = new Jacobian(Matrix.identity(2, 2),
        new JacobianAxis[]{axes.get(0), axes.get(1)}, context);
    Displacement d = new Displacement(new double[]{7.0, 8.0});

    PriorityLevel pl = new PriorityLevel(context);
    pl.addConstraint(new TestConstraint(j, d, false));

    assertNull(pl.getCurrentAugmentedDesiredDisplacement());
    pl.computeAugmentedDesiredDisplacement();
    Displacement result = pl.getCurrentAugmentedDesiredDisplacement();
    assertNotNull(result);
    assertEquals(7.0, result.storage[0], DELTA);
  }
}
