package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class WeightedMeshTest {
  @Test
  public void normalizeWeightsBalancesEachVertexAcrossAllJoints() {
    WeightedMesh mesh = weightedMeshWithVertices(0, 0, 0, 1, 0, 0, 2, 0, 0);
    WeightInfo weights = new WeightInfo();
    weights.addReference("left", weights(new float[] {2.0f, 4.0f, 0.0f}));
    weights.addReference("right", weights(new float[] {2.0f, 0.0f, 6.0f}));
    mesh.weightInfo.setValue(weights);

    mesh.normalizeWeights();

    assertArrayEquals(new float[] {0.5f, 1.0f, 0.0f}, expandWeights(weights.getMap().get("left"), 3), 0.000001f);
    assertArrayEquals(new float[] {0.5f, 0.0f, 1.0f}, expandWeights(weights.getMap().get("right"), 3), 0.000001f);
  }

  @Test
  public void scaleUpdatesVerticesAndInverseBindTranslations() {
    WeightedMesh mesh = weightedMeshWithVertices(1, 2, 3, -1, -2, -3);
    WeightInfo weights = new WeightInfo();
    weights.addReference("root", weightsWithTransform(new float[] {1.0f, 1.0f}, AffineMatrix4x4.createTranslation(1, 2, 3).invert()));
    mesh.weightInfo.setValue(weights);

    mesh.scale(2.0);

    assertArrayEquals(new double[] {2, 4, 6, -2, -4, -6}, mesh.vertexBuffer.getValue().array(), EPSILON);
    assertPointEquals(new Point3(-2, -4, -6), weights.getMap().get("root").getInverseAbsoluteTransformation().translation());
  }

  @Test
  public void createCopyClonesWeightInfoIndependently() {
    WeightedMesh mesh = weightedMeshWithVertices(0, 0, 0, 1, 1, 1);
    mesh.skeleton.setValue(new Joint());
    WeightInfo weights = new WeightInfo();
    weights.addReference("joint", weights(new float[] {1.0f, 0.5f}));
    mesh.weightInfo.setValue(weights);

    WeightedMesh copy = mesh.createCopy();
    weights.addReference("later", weights(new float[] {0.25f, 0.75f}));

    assertEquals(1, copy.weightInfo.getValue().getMap().size());
    assertNotSame(mesh.weightInfo.getValue(), copy.weightInfo.getValue());
    assertEquals(mesh.skeleton.getValue(), copy.skeleton.getValue());
  }

  private static WeightedMesh weightedMeshWithVertices(double... vertices) {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(vertices));
    mesh.weightInfo.setValue(new WeightInfo());
    return mesh;
  }

  private static InverseAbsoluteTransformationWeightsPair weights(float[] values) {
    return weightsWithTransform(values, AffineMatrix4x4.IDENTITY);
  }

  private static InverseAbsoluteTransformationWeightsPair weightsWithTransform(float[] values, AffineMatrix4x4 transform) {
    return InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(values, transform);
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
