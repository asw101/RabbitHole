package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class InverseAbsoluteTransformationWeightsPairTest {
  @Test
  public void factoryChoosesSparseOrPlentifulBasedOnWeightDensity() {
    InverseAbsoluteTransformationWeightsPair sparse =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            new float[] {0.0f, 0.5f, 0.0f, 0.25f},
            AffineMatrix4x4.IDENTITY);
    InverseAbsoluteTransformationWeightsPair plentiful =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            new float[] {0.1f, 0.2f, 0.3f, 0.4f},
            AffineMatrix4x4.IDENTITY);

    assertTrue(sparse instanceof SparseInverseAbsoluteTransformationWeightsPair);
    assertTrue(plentiful instanceof PlentifulInverseAbsoluteTransformationWeightsPair);
  }

  @Test
  public void weightIteratorReportsSparseIndicesAndValues() {
    InverseAbsoluteTransformationWeightsPair pair =
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            new float[] {0.0f, 0.25f, 0.0f, 0.75f},
            AffineMatrix4x4.IDENTITY);

    List<Integer> indices = new ArrayList<>();
    List<Float> values = new ArrayList<>();
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      indices.add(iterator.getIndex());
      values.add(iterator.next());
    }

    assertArrayEquals(new Integer[] {1, 3}, indices.toArray(new Integer[0]));
    assertArrayEquals(new Float[] {0.25f, 0.75f}, values.toArray(new Float[0]));
  }

  @Test
  public void encodeDecodeRoundTripPreservesSparseWeightsAndTransform() {
    SparseInverseAbsoluteTransformationWeightsPair original = new SparseInverseAbsoluteTransformationWeightsPair();
    AffineMatrix4x4 inverseTransform = AffineMatrix4x4.createTranslation(-1, -2, -3);
    original.setInverseAbsoluteTransformation(inverseTransform);
    original.setWeights(new float[] {0.0f, 0.25f, 0.0f, 0.75f});

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    original.encode(encoder);
    BinaryDecoder decoder = encoder.createDecoder();
    SparseInverseAbsoluteTransformationWeightsPair decoded = new SparseInverseAbsoluteTransformationWeightsPair();
    decoded.decode(decoder);

    assertPointEquals(new Point3(-1, -2, -3), decoded.getInverseAbsoluteTransformation().translation());
    assertArrayEquals(new float[] {0.0f, 0.25f, 0.0f, 0.75f}, expandWeights(decoded, 4), 0.000001f);
  }

  private static float[] expandWeights(InverseAbsoluteTransformationWeightsPair pair, int vertexCount) {
    float[] expanded = new float[vertexCount];
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      int index = iterator.getIndex();
      expanded[index] = iterator.next();
    }
    return expanded;
  }
}
