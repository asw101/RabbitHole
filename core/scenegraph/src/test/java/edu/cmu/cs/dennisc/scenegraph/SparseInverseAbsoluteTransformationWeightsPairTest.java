package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SparseInverseAbsoluteTransformationWeightsPairTest {
  private interface EncoderAction {
    void encode(OutputStreamBinaryEncoder encoder) throws Exception;
  }

  private InputStreamBinaryDecoder decoderFor(EncoderAction action) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    action.encode(encoder);
    encoder.flush();
    return new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
  }

  private static List<Float> weightsOf(InverseAbsoluteTransformationWeightsPair pair) {
    List<Float> weights = new ArrayList<>();
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      weights.add(iterator.next());
    }
    return weights;
  }

  private static List<Integer> indicesOf(InverseAbsoluteTransformationWeightsPair pair) {
    List<Integer> indices = new ArrayList<>();
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      indices.add(iterator.getIndex());
      iterator.next();
    }
    return indices;
  }

  @Test
  public void setWeightsRetainsOnlyNonZeroEntries() {
    SparseInverseAbsoluteTransformationWeightsPair pair = new SparseInverseAbsoluteTransformationWeightsPair();

    pair.setWeights(new float[] {0.0f, 0.25f, 0.0f, 0.75f, 0.0f});

    assertEquals(List.of(0.25f, 0.75f), weightsOf(pair));
    assertEquals(List.of(1, 3), indicesOf(pair));
    assertTrue(pair.getIterator().hasNext());
  }

  @Test
  public void createCopyPreservesSparseEntriesAfterOriginalChanges() {
    SparseInverseAbsoluteTransformationWeightsPair original = new SparseInverseAbsoluteTransformationWeightsPair();
    original.setWeights(new float[] {0.0f, 0.25f, 0.0f, 0.75f});
    original.setInverseAbsoluteTransformation(AffineMatrix4x4.createTranslation(1.0, 2.0, 3.0));

    SparseInverseAbsoluteTransformationWeightsPair copy = (SparseInverseAbsoluteTransformationWeightsPair) original.createCopy();
    original.setWeights(new float[] {1.0f, 0.0f, 0.0f, 0.0f});

    assertEquals(List.of(0.25f, 0.75f), weightsOf(copy));
    assertEquals(List.of(1, 3), indicesOf(copy));
    assertEquals(1.0, copy.getInverseAbsoluteTransformation().translation().x(), 0.0);
    assertEquals(2.0, copy.getInverseAbsoluteTransformation().translation().y(), 0.0);
    assertEquals(3.0, copy.getInverseAbsoluteTransformation().translation().z(), 0.0);
    assertEquals(List.of(1.0f), weightsOf(original));
  }

  @Test
  public void encodeAndDecodeRoundTripPreservesIndicesWeightsAndTransform() throws Exception {
    SparseInverseAbsoluteTransformationWeightsPair original = new SparseInverseAbsoluteTransformationWeightsPair();
    original.setWeights(new float[] {0.0f, 0.4f, 0.0f, 0.0f, 0.6f});
    original.setInverseAbsoluteTransformation(AffineMatrix4x4.createTranslation(-2.0, 5.0, 7.5));

    InputStreamBinaryDecoder decoder = decoderFor(original::encode);
    SparseInverseAbsoluteTransformationWeightsPair decoded = new SparseInverseAbsoluteTransformationWeightsPair();
    decoded.decode(decoder);

    assertEquals(List.of(0.4f, 0.6f), weightsOf(decoded));
    assertEquals(List.of(1, 4), indicesOf(decoded));
    assertEquals(-2.0, decoded.getInverseAbsoluteTransformation().translation().x(), 0.0);
    assertEquals(5.0, decoded.getInverseAbsoluteTransformation().translation().y(), 0.0);
    assertEquals(7.5, decoded.getInverseAbsoluteTransformation().translation().z(), 0.0);
  }
}
