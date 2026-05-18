package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.ik.core.solver.BoneTest;
import org.lgna.ik.core.solver.Chain;
import org.lgna.ik.core.solver.Solver;
import org.lgna.story.resources.JointId;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JointedModelIkEnforcerTest {
  private static final double EPS = 1.0e-9;

  @SuppressWarnings("unchecked")
  private static List<Chain> getSolverChains(Solver solver) {
    try {
      Field field = Solver.class.getDeclaredField("chains");
      field.setAccessible(true);
      return (List<Chain>) field.get(solver);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static Weights getSolverWeights(Solver solver) {
    try {
      Field field = Solver.class.getDeclaredField("weights");
      field.setAccessible(true);
      return (Weights) field.get(solver);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals(expected.x(), actual.x(), EPS);
    assertEquals(expected.y(), actual.y(), EPS);
    assertEquals(expected.z(), actual.z(), EPS);
  }

  @Test
  public void constructorLinksSolverToWeightsInstance() {
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    assertSame(enforcer.weights, getSolverWeights(enforcer.solver));
  }

  @Test
  public void hasActiveChainIsFalseInitially() {
    assertFalse(new JointedModelIkEnforcer(null).hasActiveChain());
  }

  @Test
  public void hasActiveChainBecomesTrueWhenMapContainsEntry() {
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    enforcer.chainsForEes.put(new JointId(null, null), new HashMap<JointId, Chain>());
    assertTrue(enforcer.hasActiveChain());
  }

  @Test
  public void setDefaultJointWeightUpdatesWeightsObject() {
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    enforcer.setDefaultJointWeight(4.0);
    assertEquals(4.0, enforcer.weights.defaultJointWeight, EPS);
  }

  @Test
  public void setJointWeightDelegatesToWeights() {
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    JointId jointId = new JointId(null, null);
    enforcer.setJointWeight(jointId, 2.5);
    assertEquals(2.5, enforcer.weights.getEffectiveJointWeight(jointId), EPS);
  }

  @Test
  public void getChainForPrintingReturnsRegisteredChain() {
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    JointId anchorId = new JointId(null, null);
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byEe = new HashMap<JointId, Chain>();
    byEe.put(eeId, chain);
    enforcer.anchors.put(anchorId, byEe);
    assertSame(chain, enforcer.getChainForPrinting(anchorId, eeId));
  }

  @Test
  public void setEeLocalPositionUpdatesAllChainsForEndEffector() {
    JointId eeId = new JointId(null, null);
    Chain first = BoneTest.createChain(new BoneTest.TestJointImp("a", new BoneTest.NamedJointId("a"), AffineMatrix4x4.IDENTITY, true, false, false));
    Chain second = BoneTest.createChain(new BoneTest.TestJointImp("b", new BoneTest.NamedJointId("b"), new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(1.0, 0.0, 0.0)), true, false, false));
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    Map<JointId, Chain> chains = new HashMap<JointId, Chain>();
    chains.put(new JointId(null, null), first);
    chains.put(new JointId(null, null), second);
    enforcer.chainsForEes.put(eeId, chains);

    enforcer.setEeLocalPosition(eeId, new Point3(0.0, 2.0, 0.0));

    assertPointEquals(new Point3(0.0, 2.0, 0.0), first.getEndEffectorPosition());
    assertPointEquals(new Point3(1.0, 2.0, 0.0), second.getEndEffectorPosition());
  }

  @Test
  public void setEePositionUpdatesAllChainsForEndEffector() {
    JointId eeId = new JointId(null, null);
    Chain first = BoneTest.createChain(new BoneTest.TestJointImp("a", new BoneTest.NamedJointId("a"), AffineMatrix4x4.IDENTITY, true, false, false));
    Chain second = BoneTest.createChain(new BoneTest.TestJointImp("b", new BoneTest.NamedJointId("b"), new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(1.0, 0.0, 0.0)), true, false, false));
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    Map<JointId, Chain> chains = new HashMap<JointId, Chain>();
    chains.put(new JointId(null, null), first);
    chains.put(new JointId(null, null), second);
    enforcer.chainsForEes.put(eeId, chains);

    enforcer.setEePosition(eeId, new Point3(4.0, 5.0, 0.0));

    assertPointEquals(new Point3(4.0, 5.0, 0.0), first.getEndEffectorPosition());
    assertPointEquals(new Point3(4.0, 5.0, 0.0), second.getEndEffectorPosition());
  }

  @Test
  public void getEndEffectorPositionReturnsPositionFromRegisteredChain() {
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false));
    chain.setEndEffectorLocalPosition(new Point3(0.0, 3.0, 0.0));
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    Map<JointId, Chain> byAnchor = new HashMap<JointId, Chain>();
    byAnchor.put(new JointId(null, null), chain);
    enforcer.chainsForEes.put(eeId, byAnchor);
    assertPointEquals(new Point3(0.0, 3.0, 0.0), enforcer.getEndEffectorPosition(eeId));
  }

  @Test
  public void getAnchorPositionReturnsPositionFromRegisteredChain() {
    JointId anchorId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(3.0, 4.0, 0.0)), true, false, false));
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    Map<JointId, Chain> byEe = new HashMap<JointId, Chain>();
    byEe.put(new JointId(null, null), chain);
    enforcer.anchors.put(anchorId, byEe);
    assertPointEquals(new Point3(3.0, 4.0, 0.0), enforcer.getAnchorPosition(anchorId));
  }

  @Test
  public void clearChainBetweenRemovesMapsAndSolverRegistration() {
    JointId anchorId = new JointId(null, null);
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("joint", new BoneTest.NamedJointId("joint"), AffineMatrix4x4.IDENTITY, true, false, false));
    JointedModelIkEnforcer enforcer = new JointedModelIkEnforcer(null);
    Map<JointId, Chain> byEe = new HashMap<JointId, Chain>();
    byEe.put(eeId, chain);
    Map<JointId, Chain> byAnchor = new HashMap<JointId, Chain>();
    byAnchor.put(anchorId, chain);
    enforcer.anchors.put(anchorId, byEe);
    enforcer.chainsForEes.put(eeId, byAnchor);
    enforcer.solver.addChain(chain);
    assertEquals(1, getSolverChains(enforcer.solver).size());

    enforcer.clearChainBetween(anchorId, eeId);

    assertTrue(enforcer.anchors.isEmpty());
    assertTrue(enforcer.chainsForEes.isEmpty());
    assertTrue(getSolverChains(enforcer.solver).isEmpty());
  }
}
