package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertArrayEquals;

public class PlentifulInverseAbsoluteTransformationWeightsPairBehaviorTest {
  @Test
  public void pairUsesIdentityIndicesAndCopySnapshotsWeights() {
    float[] weights = {0.1f, 0.2f, 0.3f};
    PlentifulInverseAbsoluteTransformationWeightsPair pair = new PlentifulInverseAbsoluteTransformationWeightsPair();
    pair.setInverseAbsoluteTransformation(AffineMatrix4x4.createTranslation(-3, -2, -1));
    pair.setWeights(weights);

    PlentifulInverseAbsoluteTransformationWeightsPair copy = (PlentifulInverseAbsoluteTransformationWeightsPair) pair.createCopy();
    weights[0] = 0.9f;

    assertArrayEquals(new float[] {0.9f, 0.2f, 0.3f}, expand(pair), 0.000001f);
    assertArrayEquals(new float[] {0.1f, 0.2f, 0.3f}, expand(copy), 0.000001f);
    assertPointEquals(new Point3(-3, -2, -1), copy.getInverseAbsoluteTransformation().translation());
  }

  private static float[] expand(PlentifulInverseAbsoluteTransformationWeightsPair pair) {
    float[] values = new float[3];
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      int index = iterator.getIndex();
      values[index] = iterator.next();
    }
    return values;
  }
}
