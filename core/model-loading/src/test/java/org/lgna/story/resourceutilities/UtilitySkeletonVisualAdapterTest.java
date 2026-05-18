package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class UtilitySkeletonVisualAdapterTest {
  @Test
  public void utilityWeightedMeshControlComputesBoundingBoxes() {
    Joint root = joint("ROOT", AffineMatrix4x4.IDENTITY);
    Joint child = joint("CHILD", new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(0, 1, 0)));
    child.setParent(root);

    WeightedMesh weightedMesh = weightedMesh(root, child);
    UtilityWeightedMeshControl control = new UtilityWeightedMeshControl();
    control.initialize(weightedMesh);

    AxisAlignedBox absoluteBox = control.getAbsoluteBoundingBox();
    assertBox(absoluteBox, 0.0, 0.0, 0.0, 2.0, 2.0, 0.0);

    AxisAlignedBox rootBox = control.getBoundingBoxForJoint(root);
    assertBox(rootBox, 0.0, 0.0, 0.0, 2.0, 0.0, 0.0);

    AxisAlignedBox childBox = control.getBoundingBoxForJoint(child);
    assertBox(childBox, 0.0, 2.0, 0.0, 0.0, 2.0, 0.0);

    Joint missing = joint("MISSING", AffineMatrix4x4.IDENTITY);
    assertTrue(control.getBoundingBoxForJoint(missing).isNaN());
  }

  @Test
  public void adapterBuildsMeshControlsAndInitializesJointBoundingBoxes() {
    Joint root = joint("ROOT", AffineMatrix4x4.IDENTITY);
    Joint child = joint("CHILD", new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(0, 1, 0)));
    child.setParent(root);

    SkeletonVisual visual = new SkeletonVisual();
    visual.skeleton.setValue(root);
    visual.weightedMeshes.setValue(new WeightedMesh[]{weightedMesh(root, child)});
    TexturedAppearance appearance = new TexturedAppearance();
    appearance.textureId.setValue(7);
    visual.textures.setValue(new TexturedAppearance[]{appearance});

    UtilitySkeletonVisualAdapter adapter = new UtilitySkeletonVisualAdapter();
    adapter.initialize(visual);

    assertEquals(1, adapter.getUtilityWeightedMeshControls().size());
    assertBox(adapter.getAbsoluteBoundingBox(), 0.0, 0.0, 0.0, 2.0, 2.0, 0.0);

    adapter.initializeJointBoundingBoxes();
    assertFalse(root.boundingBox.getValue().isNaN());
    assertFalse(child.boundingBox.getValue().isNaN());
  }

  @Test
  public void adapterHandlesNullSkeleton() {
    SkeletonVisual visual = new SkeletonVisual();
    visual.skeleton.setValue(null);
    visual.weightedMeshes.setValue(new WeightedMesh[0]);
    visual.textures.setValue(new TexturedAppearance[0]);

    UtilitySkeletonVisualAdapter adapter = new UtilitySkeletonVisualAdapter();
    adapter.initialize(visual);
    adapter.initializeJointBoundingBoxes();

    assertTrue(adapter.getAbsoluteBoundingBox().isNaN());
  }

  private static Joint joint(String id, AffineMatrix4x4 localTransformation) {
    Joint joint = new Joint();
    joint.jointID.setValue(id);
    joint.setName(id);
    joint.localTransformation.setValue(localTransformation);
    return joint;
  }

  private static WeightedMesh weightedMesh(Joint root, Joint child) {
    WeightedMesh weightedMesh = new WeightedMesh();
    weightedMesh.setName("weightedMesh");
    weightedMesh.textureId.setValue(7);
    weightedMesh.skeleton.setValue(root);
    weightedMesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[]{
        0.0, 0.0, 0.0,
        2.0, 0.0, 0.0,
        0.0, 2.0, 0.0
    }));
    weightedMesh.normalBuffer.setValue(FloatBuffer.wrap(new float[]{
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f
    }));
    weightedMesh.textCoordBuffer.setValue(FloatBuffer.wrap(new float[]{
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f
    }));
    weightedMesh.indexBuffer.setValue(IntBuffer.wrap(new int[]{0, 1, 2}));

    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("ROOT", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[]{1.0f, 1.0f, 0.1f}, AffineMatrix4x4.IDENTITY));
    weightInfo.addReference("CHILD", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[]{0.0f, 0.0f, 1.0f}, AffineMatrix4x4.IDENTITY));
    weightedMesh.weightInfo.setValue(weightInfo);
    return weightedMesh;
  }

  private static void assertBox(AxisAlignedBox box, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    assertEquals(minX, box.minimum().x(), 0.000001);
    assertEquals(minY, box.minimum().y(), 0.000001);
    assertEquals(minZ, box.minimum().z(), 0.000001);
    assertEquals(maxX, box.maximum().x(), 0.000001);
    assertEquals(maxY, box.maximum().y(), 0.000001);
    assertEquals(maxZ, box.maximum().z(), 0.000001);
  }
}
