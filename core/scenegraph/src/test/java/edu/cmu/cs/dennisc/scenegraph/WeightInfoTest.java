package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WeightInfoTest {

  @Test
  public void defaultConstructorCreatesEmptyMap() {
    WeightInfo wi = new WeightInfo();
    assertNotNull(wi.getMap());
    assertTrue(wi.getMap().isEmpty());
  }

  @Test
  public void addReferenceStoresEntry() {
    WeightInfo wi = new WeightInfo();
    float[] weights = new float[]{0.5f, 0.3f, 0.2f};
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    wi.addReference("joint1", pair);

    Map<String, InverseAbsoluteTransformationWeightsPair> map = wi.getMap();
    assertEquals(1, map.size());
    assertTrue(map.containsKey("joint1"));
  }

  @Test
  public void getWeightInfoForJointReturnsCorrectPair() {
    WeightInfo wi = new WeightInfo();
    float[] weights = new float[]{1.0f, 0.0f, 0.0f};
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    wi.addReference("testJointId", pair);

    Joint joint = new Joint();
    joint.jointID.setValue("testJointId");
    InverseAbsoluteTransformationWeightsPair result = wi.getWeightInfoForJoint(joint);
    assertNotNull(result);
  }

  @Test
  public void getWeightInfoForUnknownJointReturnsNull() {
    WeightInfo wi = new WeightInfo();
    Joint joint = new Joint();
    joint.jointID.setValue("nonexistent");
    assertNull(wi.getWeightInfoForJoint(joint));
  }

  @Test
  public void createCopyIsIndependent() {
    WeightInfo wi = new WeightInfo();
    float[] weights = new float[]{0.8f, 0.1f, 0.1f};
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    wi.addReference("j1", pair);

    WeightInfo copy = wi.createCopy();
    assertNotNull(copy);
    assertEquals(1, copy.getMap().size());
    assertTrue(copy.getMap().containsKey("j1"));

    // Modifying original shouldn't affect copy
    wi.addReference("j2", pair);
    assertEquals(1, copy.getMap().size());
  }

  @Test
  public void scaleAdjustsTransformations() {
    WeightInfo wi = new WeightInfo();
    float[] weights = new float[]{1.0f, 0.0f};
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    wi.addReference("j1", pair);

    // Should not throw
    wi.scale(2.0);
    assertNotNull(wi.getMap().get("j1").getInverseAbsoluteTransformation());
  }

  @Test
  public void createPairReturnsNullForAllZeroWeights() {
    float[] weights = new float[]{0, 0, 0};
    InverseAbsoluteTransformationWeightsPair result =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    assertNull(result);
  }

  @Test
  public void createPairReturnsSparseForFewNonZero() {
    float[] weights = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0.5f};
    InverseAbsoluteTransformationWeightsPair result =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    assertNotNull(result);
    assertTrue(result instanceof SparseInverseAbsoluteTransformationWeightsPair);
  }

  @Test
  public void createPairReturnsPlentifulForMostNonZero() {
    float[] weights = new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
    InverseAbsoluteTransformationWeightsPair result =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    assertNotNull(result);
    assertTrue(result instanceof PlentifulInverseAbsoluteTransformationWeightsPair);
  }

  @Test
  public void weightIteratorIteratesThroughWeights() {
    float[] weights = new float[]{0.5f, 0.3f, 0.2f};
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            weights, AffineMatrix4x4.IDENTITY);
    assertNotNull(pair);

    InverseAbsoluteTransformationWeightsPair.WeightIterator it = pair.getIterator();
    int count = 0;
    while (it.hasNext()) {
      it.next();
      count++;
    }
    assertTrue("Should iterate through weights", count > 0);
  }
}
