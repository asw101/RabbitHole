package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentAddedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentRemovedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.ComponentsListener;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertBoxEquals;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ScenegraphModelTest {

  @Test
  public void meshVertexBufferInvalidatesCachedBoundsAndNotifiesListeners() {
    Mesh mesh = meshWithVertices(0, 0, 0, 1, 2, 3);
    AxisAlignedBox initialBounds = mesh.getAxisAlignedMinimumBoundingBox();
    AtomicInteger boundChanges = new AtomicInteger();
    mesh.addBoundListener((BoundEvent event) -> {
      assertSame(mesh, event.getSource());
      boundChanges.incrementAndGet();
    });

    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(new double[] {-2, -3, -4, 5, 6, 7}));

    assertEquals(1, boundChanges.get());
    AxisAlignedBox updatedBounds = mesh.getAxisAlignedMinimumBoundingBox();
    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(-2, -3, -4, 5, 6, 7), updatedBounds);
    assertTrue("bounds should be recomputed after vertexBuffer changes", initialBounds != updatedBounds);
  }

  @Test
  public void meshTextureIdArrayOverridesScalarTextureAndReportsUniqueReferences() {
    Mesh mesh = new Mesh();
    mesh.textureId.setValue(7);

    assertEquals(Arrays.asList(7), mesh.getReferencedTextureIds());
    assertEquals(Integer.valueOf(7), mesh.getTextureId(0));

    mesh.textureIdArray.addAll(Arrays.asList(3, 3, 3, 4, 4, 4, 5, 5, 5));

    assertEquals(Arrays.asList(3, 4, 5), mesh.getReferencedTextureIds());
    assertEquals(Integer.valueOf(3), mesh.getTextureId(0));
    assertEquals(Integer.valueOf(4), mesh.getTextureId(3));
    assertEquals(Integer.valueOf(5), mesh.getTextureId(6));
  }

  @Test
  public void weightedMeshNormalizesWeightsPerVertexAcrossJoints() {
    WeightedMesh mesh = weightedMeshWithVertices(0, 0, 0, 1, 0, 0, 2, 0, 0);
    WeightInfo weights = new WeightInfo();
    weights.addReference("left", weights(2.0f, 4.0f, 0.0f));
    weights.addReference("right", weights(2.0f, 0.0f, 6.0f));
    mesh.weightInfo.setValue(weights);

    mesh.normalizeWeights();

    assertArrayEquals(new float[] {0.5f, 1.0f, 0.0f}, expandedWeights(weights.getMap().get("left"), 3), 0.000001f);
    assertArrayEquals(new float[] {0.5f, 0.0f, 1.0f}, expandedWeights(weights.getMap().get("right"), 3), 0.000001f);
  }

  @Test
  public void weightedMeshScaleUpdatesVerticesAndInverseBindTranslations() {
    WeightedMesh mesh = weightedMeshWithVertices(1, 2, 3, -1, -2, -3);
    WeightInfo weights = new WeightInfo();
    weights.addReference("root", weightsWithTransform(new float[] {1.0f, 1.0f}, AffineMatrix4x4.createTranslation(1, 2, 3).invert()));
    mesh.weightInfo.setValue(weights);

    mesh.scale(2.0);

    assertArrayEquals(new double[] {2, 4, 6, -2, -4, -6}, mesh.vertexBuffer.getValue().array(), EPSILON);
    Point3 inverseTranslation = weights.getMap().get("root").getInverseAbsoluteTransformation().translation();
    assertPointEquals(new Point3(-2, -4, -6), inverseTranslation);
  }

  @Test
  public void skeletonVisualUsesDefaultPoseMeshesForBoundsWhenPresent() {
    SkeletonVisual visual = new SkeletonVisual();
    WeightedMesh bindPose = weightedMeshWithVertices(0, 0, 0, 1, 1, 1);
    WeightedMesh defaultPose = weightedMeshWithVertices(-4, -5, -6, 4, 5, 6);
    visual.weightedMeshes.setValue(new WeightedMesh[] {bindPose});
    visual.defaultPoseWeightedMeshes.setValue(new WeightedMesh[] {defaultPose});
    visual.hasDefaultPoseWeightedMeshes.setValue(true);

    AxisAlignedBox bounds = visual.getAxisAlignedMinimumBoundingBox(true);

    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(-4, -5, -6, 4, 5, 6), bounds);
  }

  @Test
  public void skeletonVisualNormalizesWeightedAndDefaultPoseMeshes() {
    WeightedMesh weighted = weightedMeshWithUnnormalizedWeights("weightedLeft", "weightedRight");
    WeightedMesh defaultPose = weightedMeshWithUnnormalizedWeights("defaultLeft", "defaultRight");
    SkeletonVisual visual = new SkeletonVisual();
    visual.weightedMeshes.setValue(new WeightedMesh[] {weighted});
    visual.defaultPoseWeightedMeshes.setValue(new WeightedMesh[] {defaultPose});
    visual.hasDefaultPoseWeightedMeshes.setValue(true);

    visual.normalizeWeightedMeshes();

    assertNormalizedPair(weighted.weightInfo.getValue(), "weightedLeft", "weightedRight");
    assertNormalizedPair(defaultPose.weightInfo.getValue(), "defaultLeft", "defaultRight");
  }

  @Test
  public void skeletonVisualScaleDelegatesToSkeletonAndWeightedMeshesWithoutRendering() {
    Joint root = joint("root", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    Joint child = joint("child", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    root.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 2, 0));
    root.addComponent(child);

    WeightedMesh mesh = weightedMeshWithVertices(1, 1, 1, 2, 2, 2);
    mesh.weightInfo.setValue(new WeightInfo());
    SkeletonVisual visual = new SkeletonVisual();
    visual.skeleton.setValue(root);
    visual.weightedMeshes.setValue(new WeightedMesh[] {mesh});

    visual.scale(3.0);

    assertPointEquals(new Point3(3, 0, 0), root.getLocalTransformation().translation());
    assertPointEquals(new Point3(0, 6, 0), child.getLocalTransformation().translation());
    assertArrayEquals(new double[] {3, 3, 3, 6, 6, 6}, mesh.vertexBuffer.getValue().array(), EPSILON);
  }

  @Test
  public void jointLookupFindsExactAndPrefixMatchesInSyntheticHierarchy() {
    Joint root = joint("root", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    Joint leftArm = joint("leftArm", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    Joint leftHand = joint("leftHand", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    Joint rightArm = joint("rightArm", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    root.addComponent(leftArm);
    leftArm.addComponent(leftHand);
    root.addComponent(rightArm);

    assertSame(leftHand, root.getJoint("leftHand"));
    assertNull(root.getJoint("missing"));

    List<Joint> leftJoints = Arrays.asList(root.getJoints("left"));
    assertEquals(Arrays.asList(leftArm, leftHand), leftJoints);
  }

  @Test
  public void jointScaleUpdatesLocalTranslationsAndLocalBounds() {
    Joint root = joint("root", AxisAlignedBox.createAxisAlignedBox(-1, -2, -3, 1, 2, 3));
    Joint child = joint("child", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 2, 2, 2));
    root.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(4, 5, 6));
    root.addComponent(child);

    root.scale(2.0);

    assertPointEquals(new Point3(2, 4, 6), root.getLocalTransformation().translation());
    assertPointEquals(new Point3(8, 10, 12), child.getLocalTransformation().translation());
    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(-2, -4, -6, 2, 4, 6), root.boundingBox.getValue());
    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 4, 4, 4), child.boundingBox.getValue());
  }

  @Test
  public void jointCumulativeBoundsUseEachChildLocalBoundingBox() {
    Joint root = joint("root", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1));
    Joint child = joint("child", AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 2, 3, 4));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    root.addComponent(child);

    AxisAlignedBox localOnly = root.getBoundingBox(false);
    AxisAlignedBox cumulative = root.getBoundingBox(true);

    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 1, 1, 1), localOnly);
    assertBoxEquals(AxisAlignedBox.createAxisAlignedBox(0, 0, 0, 12, 23, 34), cumulative);
  }

  @Test
  public void compositeAddAndRemoveComponentUpdatesParentOrderAndEvents() {
    Scene parent = new Scene();
    Transformable first = new Transformable();
    Transformable second = new Transformable();
    AtomicReference<ComponentAddedEvent> added = new AtomicReference<>();
    AtomicReference<ComponentRemovedEvent> removed = new AtomicReference<>();
    parent.addChildrenListener(new ComponentsListener() {
      @Override
      public void componentAdded(ComponentAddedEvent event) {
        added.set(event);
      }

      @Override
      public void componentRemoved(ComponentRemovedEvent event) {
        removed.set(event);
      }
    });

    parent.addComponent(first);
    parent.addComponent(second);

    assertSame(parent, first.getParent());
    assertSame(parent, first.getRoot());
    assertTrue(parent.isAncestorOf(first));
    assertTrue(first.isDescendantOf(parent));
    assertEquals(2, parent.getComponentCount());
    assertEquals(1, parent.getIndexOfComponent(second));
    assertSame(second, parent.getComponentAt(1));
    assertArrayEquals(new Component[] {first, second}, parent.getComponentsAsArray());
    assertSame(parent, added.get().getTypedSource());
    assertSame(second, added.get().getChild());

    parent.removeComponent(first);

    assertNull(first.getParent());
    assertEquals(1, parent.getComponentCount());
    assertSame(parent, removed.get().getTypedSource());
    assertSame(first, removed.get().getChild());
  }

  @Test
  public void parentChangesPropagateHierarchyAndAbsoluteEventsThroughSubtree() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();
    parent.addComponent(child);
    AtomicInteger parentHierarchyChanges = new AtomicInteger();
    AtomicInteger childHierarchyChanges = new AtomicInteger();
    AtomicInteger childAbsoluteChanges = new AtomicInteger();
    parent.addHierarchyListener(event -> {
      assertSame(parent, event.getTypedSource());
      parentHierarchyChanges.incrementAndGet();
    });
    child.addHierarchyListener(event -> {
      assertSame(child, event.getTypedSource());
      childHierarchyChanges.incrementAndGet();
    });
    child.addAbsoluteTransformationListener(event -> {
      assertSame(child, event.getTypedSource());
      childAbsoluteChanges.incrementAndGet();
    });

    scene.addComponent(parent);

    assertEquals(1, parentHierarchyChanges.get());
    assertEquals(1, childHierarchyChanges.get());
    assertEquals(1, childAbsoluteChanges.get());
    assertSame(scene, child.getRoot());
  }

  @Test
  public void transformableConvertsPointsBetweenLocalParentAndSceneFrames() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();
    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    scene.addComponent(parent);
    parent.addComponent(child);

    assertPointEquals(new Point3(11, 7, 3), child.transformToAbsolute(new Point3(1, 2, 3)));
    assertPointEquals(new Point3(1, 7, 3), child.transformTo(new Point3(1, 2, 3), parent));
    assertPointEquals(new Point3(1, 2, 3), child.transformFrom(new Point3(1, 7, 3), parent));
  }

  private static Mesh meshWithVertices(double... xyzs) {
    Mesh mesh = new Mesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(xyzs));
    return mesh;
  }

  private static WeightedMesh weightedMeshWithVertices(double... xyzs) {
    WeightedMesh mesh = new WeightedMesh();
    mesh.vertexBuffer.setValue(DoubleBuffer.wrap(xyzs));
    return mesh;
  }

  private static WeightedMesh weightedMeshWithUnnormalizedWeights(String leftId, String rightId) {
    WeightedMesh mesh = weightedMeshWithVertices(0, 0, 0, 1, 0, 0);
    WeightInfo weights = new WeightInfo();
    weights.addReference(leftId, weights(3.0f, 0.0f));
    weights.addReference(rightId, weights(1.0f, 5.0f));
    mesh.weightInfo.setValue(weights);
    return mesh;
  }

  private static void assertNormalizedPair(WeightInfo weights, String leftId, String rightId) {
    assertArrayEquals(new float[] {0.75f, 0.0f}, expandedWeights(weights.getMap().get(leftId), 2), 0.000001f);
    assertArrayEquals(new float[] {0.25f, 1.0f}, expandedWeights(weights.getMap().get(rightId), 2), 0.000001f);
  }

  private static Joint joint(String id, AxisAlignedBox bounds) {
    Joint joint = new Joint();
    joint.jointID.setValue(id);
    joint.boundingBox.setValue(bounds);
    return joint;
  }

  private static InverseAbsoluteTransformationWeightsPair weights(float... values) {
    return weightsWithTransform(values, AffineMatrix4x4.IDENTITY);
  }

  private static InverseAbsoluteTransformationWeightsPair weightsWithTransform(float[] values, AffineMatrix4x4 transform) {
    return InverseAbsoluteTransformationWeightsPair.createInverseAbsoluteTransformationWeightsPair(values, transform);
  }

  private static float[] expandedWeights(InverseAbsoluteTransformationWeightsPair pair, int vertexCount) {
    float[] values = new float[vertexCount];
    InverseAbsoluteTransformationWeightsPair.WeightIterator iterator = pair.getIterator();
    while (iterator.hasNext()) {
      int index = iterator.getIndex();
      values[index] = iterator.next();
    }
    return values;
  }
}
