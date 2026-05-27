package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.*;

public class WeightedMeshBehaviorTest {
  @Test
  public void normalizeWeightsBalancesEveryVertexAcrossAllJointPairs() {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {0, 0, 0, 1, 1, 1}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("root", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {1.0f, 1.0f},
        AffineMatrix4x4.IDENTITY
    ));
    weightInfo.addReference("arm", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {1.0f, 0.0f},
        AffineMatrix4x4.IDENTITY
    ));
    mesh.weightInfo.setValue(weightInfo);

    mesh.normalizeWeights();

    assertArrayEquals(new float[] {0.5f, 1.0f}, expand(weightInfo.getMap().get("root"), 2), 0.000001f);
    assertArrayEquals(new float[] {0.5f, 0.0f}, expand(weightInfo.getMap().get("arm"), 2), 0.000001f);
  }

  @Test
  public void scaleAlsoScalesWeightInfoInverseTransformations() {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {1, 2, 3}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("joint", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {1.0f},
        AffineMatrix4x4.createTranslation(-1, -2, -3)
    ));
    mesh.weightInfo.setValue(weightInfo);

    mesh.scale(2.0);

    assertArrayEquals(new double[] {2.0, 4.0, 6.0}, mesh.vertexBuffer.getValue().array(), 0.0);
    assertPointEquals(new Point3(-2, -4, -6), weightInfo.getMap().get("joint").getInverseAbsoluteTransformation().translation());
  }

  @Test
  public void createCopyDuplicatesMeshBuffersAndWeightInfo() {
    WeightedMesh original = new WeightedMesh();
    original.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {1, 2, 3}));
    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("joint", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {1.0f},
        AffineMatrix4x4.createTranslation(-1, 0, 0)
    ));
    original.weightInfo.setValue(weightInfo);

    WeightedMesh copy = original.createCopy();
    original.vertexBuffer.getValue().put(0, 9.0);
    original.weightInfo.getValue().getMap().get("joint").setWeights(new float[] {0.25f});

    assertNotSame(original.vertexBuffer.getValue(), copy.vertexBuffer.getValue());
    assertArrayEquals(new double[] {1.0, 2.0, 3.0}, toDoubleArray(copy.vertexBuffer.getValue()), 0.0);
    assertArrayEquals(new float[] {1.0f}, expand(copy.weightInfo.getValue().getMap().get("joint"), 1), 0.000001f);
  }

  private static double[] toDoubleArray(DoubleBuffer buffer) {
    DoubleBuffer copy = buffer.duplicate();
    copy.rewind();
    double[] values = new double[copy.remaining()];
    copy.get(values);
    return values;
  }

  private static float[] expand(InverseAbsoluteTransformationWeightsPair pair, int vertexCount) {
    float[] values = new float[vertexCount];
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      int index = iterator.getIndex();
      values[index] = iterator.next();
    }
    return values;
  }
}
