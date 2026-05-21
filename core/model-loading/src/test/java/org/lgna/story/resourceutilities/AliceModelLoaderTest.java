package org.lgna.story.resourceutilities;

import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.WeightInfo;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Assert;
import org.junit.Test;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;

public class AliceModelLoaderTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void translateSkeletonVisualMovesSkeletonVerticesAndBoundingBox() {
    SkeletonVisual visual = new SkeletonVisual();
    Joint root = new Joint();
    root.jointID.setValue("ROOT");
    visual.skeleton.setValue(root);

    Mesh mesh = new Mesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}));
    mesh.normalBuffer.setValue(FloatBuffer.wrap(new float[] {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f}));
    mesh.indexBuffer.setValue(IntBuffer.wrap(new int[] {0, 1, 0}));
    visual.geometries.setValue(new Geometry[] {mesh});
    visual.baseBoundingBox.setValue(new AxisAlignedBox(new Point3(-1.0, -2.0, -3.0), new Point3(4.0, 5.0, 6.0)));

    AliceModelLoader.translateSkeletonVisual(visual, new Vector3(1.0, 2.0, 3.0));

    Assert.assertEquals(1.0, root.localTransformation.getValue().translation().x(), EPSILON);
    Assert.assertEquals(2.0, root.localTransformation.getValue().translation().y(), EPSILON);
    Assert.assertEquals(3.0, root.localTransformation.getValue().translation().z(), EPSILON);
    Assert.assertArrayEquals(new double[] {1.0, 3.0, 5.0, 4.0, 6.0, 8.0}, read(mesh.vertexBuffer.getValue()), EPSILON);
    Assert.assertEquals(0.0, visual.baseBoundingBox.getValue().minimum().x(), EPSILON);
    Assert.assertEquals(0.0, visual.baseBoundingBox.getValue().minimum().y(), EPSILON);
    Assert.assertEquals(0.0, visual.baseBoundingBox.getValue().minimum().z(), EPSILON);
    Assert.assertEquals(5.0, visual.baseBoundingBox.getValue().maximum().x(), EPSILON);
    Assert.assertEquals(7.0, visual.baseBoundingBox.getValue().maximum().y(), EPSILON);
    Assert.assertEquals(9.0, visual.baseBoundingBox.getValue().maximum().z(), EPSILON);
  }

  @Test
  public void jointIdDiscoveryAndMissingJointInsertionFollowResourceHierarchy() throws Throwable {
    List<Field> jointFields = AliceModelLoader.getJointIdFields(TestResource.class);
    Assert.assertEquals(3, jointFields.size());
    Assert.assertTrue(jointFields.stream().anyMatch(field -> field.getName().equals("ROOT")));
    Assert.assertTrue(jointFields.stream().anyMatch(field -> field.getName().equals("ARM")));
    Assert.assertTrue(jointFields.stream().anyMatch(field -> field.getName().equals("HAND")));

    SkeletonVisual visual = new SkeletonVisual();
    Joint root = new Joint();
    root.jointID.setValue("ROOT");
    visual.skeleton.setValue(root);
    visual.setName("TestSkeleton");

    AliceModelLoader.addMissingJoints(visual, TestResource.class);

    Joint arm = visual.skeleton.getValue().getJoint("ARM");
    Joint hand = visual.skeleton.getValue().getJoint("HAND");
    Assert.assertNotNull(arm);
    Assert.assertNotNull(hand);
    Assert.assertSame(root, arm.getParent());
    Assert.assertSame(arm, hand.getParent());
  }

  @Test
  public void addMissingJointsRejectsSkeletonFreeResourcesThatRequireJoints() {
    SkeletonVisual visual = new SkeletonVisual();
    visual.setName("NoSkeleton");

    PipelineException error = org.junit.Assert.assertThrows(
        PipelineException.class,
        () -> AliceModelLoader.addMissingJoints(visual, TestResource.class));

    Assert.assertTrue(error.getMessage().contains("No skeleton found on NoSkeleton"));
  }

  @Test
  public void renameJointsRenamesSkeletonAndWeightInfoReferences() {
    SkeletonVisual visual = new SkeletonVisual();
    Joint pelvis = new Joint();
    pelvis.jointID.setValue("pelvis");
    Joint arm = new Joint();
    arm.jointID.setValue("arm_l");
    arm.setParent(pelvis);
    visual.skeleton.setValue(pelvis);

    WeightedMesh weightedMesh = new WeightedMesh();
    weightedMesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {0.0, 0.0, 0.0, 1.0, 1.0, 1.0}));
    weightedMesh.normalBuffer.setValue(FloatBuffer.wrap(new float[] {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f}));
    weightedMesh.indexBuffer.setValue(IntBuffer.wrap(new int[] {0, 1, 0}));
    WeightInfo weightInfo = new WeightInfo();
    weightInfo.addReference("pelvis", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {1.0f, 0.0f}, AffineMatrix4x4.IDENTITY));
    weightInfo.addReference("arm_l", InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(
        new float[] {0.0f, 1.0f}, AffineMatrix4x4.IDENTITY));
    weightedMesh.weightInfo.setValue(weightInfo);
    visual.weightedMeshes.setValue(new WeightedMesh[] {weightedMesh});

    AliceModelLoader.renameJoints(visual);

    Assert.assertEquals("PELVIS_LOWER_BODY", visual.skeleton.getValue().jointID.getValue());
    Assert.assertEquals("LEFT_ARM", ((Joint) visual.skeleton.getValue().getComponentAt(0)).jointID.getValue());
    Map<String, InverseAbsoluteTransformationWeightsPair> renamedWeights = weightedMesh.weightInfo.getValue().getMap();
    Assert.assertTrue(renamedWeights.containsKey("PELVIS_LOWER_BODY"));
    Assert.assertTrue(renamedWeights.containsKey("LEFT_ARM"));
    Assert.assertFalse(renamedWeights.containsKey("pelvis"));
    Assert.assertFalse(renamedWeights.containsKey("arm_l"));
  }

  @Test
  public void textureNameHelpersNormalizeDiffuseSuffixSpacingAndAlicePaths() {
    Assert.assertEquals("My_Texture", AliceModelLoader.getAliceTextureName("  My  Texture_diffuse.png "));

    String resourceName = AliceModelLoader.getAliceResourceNameForImageFile("PenguinResource", "Body Diffuse_DIFFUSE.png");
    Assert.assertEquals("penguinresource_BODY_DIFFUSE.a3t", resourceName);
  }

  private static double[] read(DoubleBuffer buffer) {
    DoubleBuffer duplicate = buffer.duplicate();
    duplicate.position(0);
    double[] values = new double[duplicate.remaining()];
    duplicate.get(values);
    return values;
  }

  public static final class TestResource implements JointedModelResource {
    public static final JointId ROOT = new JointId(null, TestResource.class);
    public static final JointId ARM = new JointId(ROOT, TestResource.class);
    public static final JointId HAND = new JointId(ARM, TestResource.class);
    public static final String NOT_A_JOINT = "ignore";

    @Override
    public JointedModelImp.JointImplementationAndVisualDataFactory<JointedModelResource> getImplementationAndVisualFactory() {
      return null;
    }
  }
}
