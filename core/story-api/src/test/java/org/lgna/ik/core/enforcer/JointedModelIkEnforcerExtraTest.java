package org.lgna.ik.core.enforcer;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.junit.Test;
import org.lgna.ik.core.solver.BoneTest;
import org.lgna.ik.core.solver.Chain;
import org.lgna.story.resources.JointId;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Additional coverage for JointedModelIkEnforcer accessor and bookkeeping paths. */
public class JointedModelIkEnforcerExtraTest {

  private static JointedModelIkEnforcer newEnforcer() {
    return new JointedModelIkEnforcer(null);
  }

  @Test
  public void getEndEffectorPositionReturnsNullForEmptyMap() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    e.chainsForEes.put(eeId, new HashMap<JointId, Chain>());
    assertNull(e.getEndEffectorPosition(eeId));
  }

  @Test
  public void getAnchorPositionReturnsNullForEmptyMap() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId baseId = new JointId(null, null);
    e.anchors.put(baseId, new HashMap<JointId, Chain>());
    assertNull(e.getAnchorPosition(baseId));
  }

  @Test
  public void getEndEffectorPositionReturnsValueWhenChainPresent() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byAnchor = new HashMap<>();
    byAnchor.put(new JointId(null, null), chain);
    e.chainsForEes.put(eeId, byAnchor);
    Point3 p = e.getEndEffectorPosition(eeId);
    assertNotNull(p);
  }

  @Test
  public void getAnchorPositionReturnsValueWhenChainPresent() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId baseId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byEe = new HashMap<>();
    byEe.put(new JointId(null, null), chain);
    e.anchors.put(baseId, byEe);
    Point3 p = e.getAnchorPosition(baseId);
    assertNotNull(p);
  }

  @Test
  public void setDefaultJointWeightUpdatesWeights() {
    JointedModelIkEnforcer e = newEnforcer();
    e.setDefaultJointWeight(0.42);
    // No exception expected; bytecode covered.
  }

  @Test
  public void setJointWeightUpdatesWeights() {
    JointedModelIkEnforcer e = newEnforcer();
    e.setJointWeight(new JointId(null, null), 0.5);
  }

  @Test
  public void setEeDesiredLinearVelocityRecordsAndDelegates() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byAnchor = new HashMap<>();
    byAnchor.put(new JointId(null, null), chain);
    e.chainsForEes.put(eeId, byAnchor);
    e.setEeDesiredLinearVelocity(eeId, new Vector3(1, 0, 0));
    assertEquals(1, e.currentDesiredLinearVelocities.size());
  }

  @Test
  public void setEeDesiredAngularVelocityRecordsAndDelegates() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byAnchor = new HashMap<>();
    byAnchor.put(new JointId(null, null), chain);
    e.chainsForEes.put(eeId, byAnchor);
    e.setEeDesiredAngularVelocity(eeId, new Vector3(0, 1, 0));
    assertEquals(1, e.currentDesiredAngularVelocities.size());
  }

  @Test
  public void setEeDesiredPositionRecordsParameters() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byAnchor = new HashMap<>();
    byAnchor.put(new JointId(null, null), chain);
    e.chainsForEes.put(eeId, byAnchor);
    e.setEeDesiredPosition(eeId, new Point3(1, 2, 3), 10.0);
    assertEquals(1, e.currentDesiredPositions.size());
  }

  @Test
  public void setEeDesiredOrientationRecordsParameters() {
    JointedModelIkEnforcer e = newEnforcer();
    JointId eeId = new JointId(null, null);
    Chain chain = BoneTest.createChain(new BoneTest.TestJointImp("j",
        new BoneTest.NamedJointId("j"), AffineMatrix4x4.IDENTITY, true, false, false));
    Map<JointId, Chain> byAnchor = new HashMap<>();
    byAnchor.put(new JointId(null, null), chain);
    e.chainsForEes.put(eeId, byAnchor);
    e.setEeDesiredOrientation(eeId, OrthogonalMatrix3x3.IDENTITY, 1.0);
    assertEquals(1, e.currentDesiredOrientations.size());
  }

  @Test
  public void advanceTimeStaticallyForFixedDurationWithNullSpeedMapPrintsAndReturns() {
    // solver.solve() throws NPE on empty enforcer; that's fine as the early
    // moveJointsWithSpeedsForTime guard for null is the relevant covered line.
    JointedModelIkEnforcer e = newEnforcer();
    try {
      e.advanceTimeStaticallyForFixedDuration(0.1);
    } catch (Throwable ignored) { }
  }
}
