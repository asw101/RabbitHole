package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilityWeightedMeshControlBoundingBoxTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void absoluteBoundingBoxUsesReferencedIndices() {
    UtilityWeightedMeshControl control = new UtilityWeightedMeshControl();
    control.initialize(weightedMesh());

    AxisAlignedBox box = control.getAbsoluteBoundingBox();

    assertBoxEquals(new AxisAlignedBox(new Point3(0, 0, 0), new Point3(2, 0, 0)), box);
  }

  @Test
  public void jointBoundingBoxIgnoresWeightsAtOrBelowThreshold() {
    UtilityWeightedMeshControl control = new UtilityWeightedMeshControl();
    Joint joint = new Joint();
    joint.jointID.setValue("ROOT");
    joint.localTransformation.setValue(AffineMatrix4x4.IDENTITY);
    control.initialize(weightedMesh());

    AxisAlignedBox box = control.getBoundingBoxForJoint(joint);
    AxisAlignedBox missing = control.getBoundingBoxForJoint(new Joint());

    assertBoxEquals(new AxisAlignedBox(new Point3(1, 0, 0), new Point3(2, 0, 0)), box);
    assertTrue(missing.isNaN());
  }

  private static void assertBoxEquals(AxisAlignedBox expected, AxisAlignedBox actual) {
    assertEquals(expected.minimum().x(), actual.minimum().x(), EPSILON);
    assertEquals(expected.minimum().y(), actual.minimum().y(), EPSILON);
    assertEquals(expected.minimum().z(), actual.minimum().z(), EPSILON);
    assertEquals(expected.maximum().x(), actual.maximum().x(), EPSILON);
    assertEquals(expected.maximum().y(), actual.maximum().y(), EPSILON);
    assertEquals(expected.maximum().z(), actual.maximum().z(), EPSILON);
  }

  private static WeightedMesh weightedMesh() {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        1.0, 0.0, 0.0,
        2.0, 0.0, 0.0,
    }));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
    }));
    mesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f}));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{2, 0}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference(
        "ROOT",
        InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
            new float[]{0.1f, 0.25f, 0.8f},
            AffineMatrix4x4.IDENTITY
        )
    );
    mesh.weightInfo.setValue(weightInfo);
    return mesh;
  }
}
