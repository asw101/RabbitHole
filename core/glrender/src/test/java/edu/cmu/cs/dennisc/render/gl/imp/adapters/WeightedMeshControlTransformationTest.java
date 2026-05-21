package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class WeightedMeshControlTransformationTest {
  @Test
  public void initializeCopiesMutableBuffersAndPreservesSharedIndexAndTexcoordBuffers() {
    WeightedMesh mesh = createMesh(new float[]{1.0f, 1.0f});
    WeightedMeshControl control = new WeightedMeshControl();

    control.initialize(mesh);

    assertNotSame(mesh.vertexBuffer.getValue(), control.vertexBuffer);
    assertNotSame(mesh.normalBuffer.getValue(), control.normalBuffer);
    assertSame(mesh.textCoordBuffer.getValue(), control.textCoordBuffer);
    assertSame(mesh.indexBuffer.getValue(), control.indexBuffer);
  }

  @Test
  public void processAndPostProcessTransformVerticesAndNormals() {
    WeightedMeshControl control = new WeightedMeshControl();
    control.initialize(createMesh(new float[]{1.0f, 1.0f}));

    Joint joint = new Joint();
    joint.jointID.setValue("jointA");
    control.preProcess();
    control.process(joint, AffineMatrix4x4.createTranslation(2.0, 3.0, 4.0));
    control.postProcess();

    assertArrayEquals(new double[]{3.0, 5.0, 7.0, 1.0, 3.0, 5.0}, toArray(control.vertexBuffer), 0.00001);
    assertArrayEquals(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f}, toArray(control.normalBuffer), 0.00001f);
  }

  @Test
  public void postProcessNormalizesAccumulatedWeightsBeforeApplyingTransform() {
    WeightedMeshControl control = new WeightedMeshControl();
    control.initialize(createMesh(new float[]{2.0f, 2.0f}));

    Joint joint = new Joint();
    joint.jointID.setValue("jointA");
    control.preProcess();
    control.process(joint, AffineMatrix4x4.createTranslation(5.0, 0.0, 0.0));
    control.postProcess();

    assertArrayEquals(new double[]{6.0, 2.0, 3.0, 4.0, 0.0, 1.0}, toArray(control.vertexBuffer), 0.00001);
  }

  @Test
  public void processWithUnknownJointLeavesWeightsAtZero() throws Exception {
    WeightedMeshControl control = new WeightedMeshControl();
    control.initialize(createMesh(new float[]{1.0f, 1.0f}));

    Joint joint = new Joint();
    joint.jointID.setValue("missing");
    control.preProcess();
    control.process(joint, AffineMatrix4x4.createTranslation(1.0, 1.0, 1.0));

    java.lang.reflect.Field weightsField = WeightedMeshControl.class.getDeclaredField("weights");
    weightsField.setAccessible(true);
    assertArrayEquals(new float[]{0.0f, 0.0f}, (float[]) weightsField.get(control), 0.00001f);
  }

  private static WeightedMesh createMesh(float[] weights) {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{1.0, 2.0, 3.0, -1.0, 0.0, 1.0}));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f}));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 1.0f}));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 0}));

    WeightInfo weightInfo = new WeightInfo();
    InverseAbsoluteTransformationWeightsPair pair = InverseAbsoluteTransformationWeightsPair
        .createInverseAbsoluteTransformationWeightsPair(weights, AffineMatrix4x4.IDENTITY);
    weightInfo.addReference("jointA", pair);
    mesh.weightInfo.setValue(weightInfo);
    return mesh;
  }

  private static double[] toArray(DoubleBuffer buffer) {
    DoubleBuffer duplicate = buffer.duplicate();
    duplicate.position(0);
    double[] values = new double[duplicate.remaining()];
    duplicate.get(values);
    return values;
  }

  private static float[] toArray(FloatBuffer buffer) {
    FloatBuffer duplicate = buffer.duplicate();
    duplicate.position(0);
    float[] values = new float[duplicate.remaining()];
    duplicate.get(values);
    return values;
  }
}
