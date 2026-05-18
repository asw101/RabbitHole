package org.lgna.ik.core.enforcer;

import org.junit.Test;
import org.lgna.story.resources.JointId;

import static org.junit.Assert.assertEquals;

public class WeightsTest {
  private static final double EPS = 1.0e-9;

  @Test
  public void defaultJointWeightStartsAtOne() {
    Weights weights = new Weights();
    assertEquals(1.0, weights.defaultJointWeight, EPS);
  }

  @Test
  public void getEffectiveJointWeightUsesDefaultForUnknownJoint() {
    Weights weights = new Weights();
    assertEquals(1.0, weights.getEffectiveJointWeight(new JointId(null, null)), EPS);
  }

  @Test
  public void setDefaultJointWeightUpdatesFallbackValue() {
    Weights weights = new Weights();
    weights.setDefaultJointWeight(2.5);
    assertEquals(2.5, weights.getEffectiveJointWeight(new JointId(null, null)), EPS);
  }

  @Test
  public void setJointWeightOverridesSpecificJoint() {
    Weights weights = new Weights();
    JointId jointId = new JointId(null, null);
    weights.setJointWeight(jointId, 4.5);
    assertEquals(4.5, weights.getEffectiveJointWeight(jointId), EPS);
  }

  @Test
  public void changingDefaultDoesNotOverwriteExistingSpecificWeight() {
    Weights weights = new Weights();
    JointId jointId = new JointId(null, null);
    weights.setJointWeight(jointId, 3.0);
    weights.setDefaultJointWeight(9.0);
    assertEquals(3.0, weights.getEffectiveJointWeight(jointId), EPS);
  }

  @Test
  public void multipleSpecificWeightsCanCoexist() {
    Weights weights = new Weights();
    JointId first = new JointId(null, null);
    JointId second = new JointId(null, null);
    weights.setJointWeight(first, 2.0);
    weights.setJointWeight(second, 5.0);

    assertEquals(2.0, weights.getEffectiveJointWeight(first), EPS);
    assertEquals(5.0, weights.getEffectiveJointWeight(second), EPS);
  }

  @Test
  public void settingWeightTwiceUsesLatestValue() {
    Weights weights = new Weights();
    JointId jointId = new JointId(null, null);
    weights.setJointWeight(jointId, 1.5);
    weights.setJointWeight(jointId, 6.5);

    assertEquals(6.5, weights.getEffectiveJointWeight(jointId), EPS);
  }

  @Test
  public void nullJointIdCanBeStoredAndRetrieved() {
    Weights weights = new Weights();
    weights.setJointWeight(null, 7.0);

    assertEquals(7.0, weights.getEffectiveJointWeight(null), EPS);
  }

  @Test
  public void jointWeightsMapTracksAssignedEntries() {
    Weights weights = new Weights();
    weights.setJointWeight(new JointId(null, null), 2.0);
    weights.setJointWeight(new JointId(null, null), 3.0);

    assertEquals(2, weights.jointWeights.size());
  }
}
