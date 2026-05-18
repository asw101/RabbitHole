package org.lgna.ik.core.solver;

import Jama.Matrix;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;
import org.lgna.ik.core.enforcer.Weights;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SolverTest {
  private static final double EPS = 1.0e-9;

  @SuppressWarnings("unchecked")
  private static List<Chain> getChains(Solver solver) {
    try {
      Field field = Solver.class.getDeclaredField("chains");
      field.setAccessible(true);
      return (List<Chain>) field.get(solver);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static double speedFor(Map<Bone.Axis, Double> speeds, int originalIndex) {
    for (Map.Entry<Bone.Axis, Double> entry : speeds.entrySet()) {
      if (entry.getKey().getOriginalIndexInJoint() == originalIndex) {
        return entry.getValue();
      }
    }
    throw new AssertionError("Missing speed for axis " + originalIndex);
  }

  private static Chain createSimpleChain() {
    BoneTest.TestJointImp joint = new BoneTest.TestJointImp(
        "joint",
        new BoneTest.NamedJointId("joint"),
        AffineMatrix4x4.IDENTITY,
        true,
        true,
        true);
    Chain chain = BoneTest.createChain(joint);
    chain.setEndEffectorLocalPosition(new Point3(0.0, 1.0, 0.0));
    return chain;
  }

  @Test
  public void addChainStoresChainInternally() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();

    solver.addChain(chain);

    assertEquals(1, getChains(solver).size());
    assertSame(chain, getChains(solver).get(0));
  }

  @Test
  public void removeChainRemovesStoredChain() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();
    solver.addChain(chain);

    solver.removeChain(chain);

    assertTrue(getChains(solver).isEmpty());
  }

  @Test
  public void prepareAndCalculateJacobianAndInverseReturnsNullWithoutDesiredConstraints() {
    Solver solver = new Solver();
    solver.setJointWeights(new Weights());
    solver.addChain(createSimpleChain());

    assertNull(solver.prepareAndCalculateJacobianAndInverse());
  }

  @Test
  public void desiredVelocityMapsStoreAssignedVelocities() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();

    solver.setDesiredEndEffectorLinearVelocity(chain, Vector3.POSITIVE_X_AXIS);
    solver.setDesiredEndEffectorAngularVelocity(chain, Vector3.POSITIVE_Z_AXIS);

    assertSame(Vector3.POSITIVE_X_AXIS, solver.desiredLinearVelocities.get(chain));
    assertSame(Vector3.POSITIVE_Z_AXIS, solver.desiredAngularVelocities.get(chain));
  }

  @Test
  public void solveLinearVelocityProducesNonZeroAxisSpeed() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();
    solver.setJointWeights(new Weights());
    solver.addChain(chain);
    solver.setDesiredEndEffectorLinearVelocity(chain, Vector3.POSITIVE_X_AXIS);

    Map<Bone, Map<Bone.Axis, Double>> result = solver.solve();

    assertNotNull(result);
    Map<Bone.Axis, Double> speeds = result.get(chain.getBones()[0]);
    assertEquals(3, speeds.size());
    assertTrue(Math.abs(speedFor(speeds, 2)) > EPS);
  }

  @Test
  public void solveAngularVelocityProducesMatchingAxisSpeed() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();
    solver.setJointWeights(new Weights());
    solver.addChain(chain);
    solver.setDesiredEndEffectorAngularVelocity(chain, Vector3.POSITIVE_Z_AXIS);

    Map<Bone, Map<Bone.Axis, Double>> result = solver.solve();

    assertNotNull(result);
    Map<Bone.Axis, Double> speeds = result.get(chain.getBones()[0]);
    assertEquals(3, speeds.size());
    assertTrue(Math.abs(speedFor(speeds, 2)) > EPS);
  }

  @Test
  public void prepareAndCalculateJacobianAndInverseClearsDesiredVelocityMaps() {
    Solver solver = new Solver();
    Chain chain = createSimpleChain();
    solver.setJointWeights(new Weights());
    solver.addChain(chain);
    solver.setDesiredEndEffectorLinearVelocity(chain, Vector3.POSITIVE_X_AXIS);

    Solver.JacobianAndInverse jacobianAndInverse = solver.prepareAndCalculateJacobianAndInverse();

    assertNotNull(jacobianAndInverse);
    assertTrue(solver.desiredLinearVelocities.isEmpty());
    assertTrue(solver.desiredAngularVelocities.isEmpty());
  }

  @Test
  public void calculatePseudoInverseErrorForTimeIsZeroForExactInverse() {
    Solver solver = new Solver();
    Solver.JacobianAndInverse pair = solver.new JacobianAndInverse(Matrix.identity(3, 3), Matrix.identity(3, 3), Matrix.identity(3, 3));

    assertEquals(0.0, solver.calculatePseudoInverseErrorForTime(pair, 0.25), EPS);
  }

  @Test
  public void calculatePseudoInverseErrorForTimeUsesDeterminantAndTimePower() {
    Solver solver = new Solver();
    Matrix jacobian = Matrix.identity(3, 3).times(2.0);
    Solver.JacobianAndInverse pair = solver.new JacobianAndInverse(jacobian, Matrix.identity(3, 3), Matrix.identity(3, 3));

    assertEquals(-0.125, solver.calculatePseudoInverseErrorForTime(pair, 0.5), EPS);
  }
}
