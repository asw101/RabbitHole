package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SkeletonVisualTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void defaultSkeletonIsNull() {
    SkeletonVisual sv = new SkeletonVisual();
    assertNull(sv.skeleton.getValue());
  }

  @Test
  public void defaultTrackerIsNull() {
    SkeletonVisual sv = new SkeletonVisual();
    assertNull(sv.getTracker());
  }

  @Test
  public void setTrackerRoundTrips() {
    SkeletonVisual sv = new SkeletonVisual();
    // No real tracker to inject; just test null
    sv.setTracker(null);
    assertNull(sv.getTracker());
  }

  @Test
  public void getGeometryCountWithNoWeightedMeshes() {
    SkeletonVisual sv = new SkeletonVisual();
    assertEquals(0, sv.getGeometryCount());
  }

  @Test
  public void getGeometryCountIncludesWeightedMeshes() {
    SkeletonVisual sv = new SkeletonVisual();
    WeightedMesh wm1 = new WeightedMesh();
    WeightedMesh wm2 = new WeightedMesh();
    sv.weightedMeshes.setValue(new WeightedMesh[]{wm1, wm2});
    assertEquals(2, sv.getGeometryCount());
  }

  @Test
  public void getGeometryCountIncludesBothRegularAndWeighted() {
    SkeletonVisual sv = new SkeletonVisual();
    Box box = new Box();
    sv.geometries.setValue(new Geometry[]{box});
    WeightedMesh wm = new WeightedMesh();
    sv.weightedMeshes.setValue(new WeightedMesh[]{wm});
    assertEquals(2, sv.getGeometryCount());
  }

  @Test
  public void getGeometryAtReturnsRegularFirst() {
    SkeletonVisual sv = new SkeletonVisual();
    Box box = new Box();
    sv.geometries.setValue(new Geometry[]{box});
    WeightedMesh wm = new WeightedMesh();
    sv.weightedMeshes.setValue(new WeightedMesh[]{wm});
    assertEquals(box, sv.getGeometryAt(0));
    assertEquals(wm, sv.getGeometryAt(1));
  }

  @Test
  public void hasDefaultPoseWeightedMeshesDefaultsFalse() {
    SkeletonVisual sv = new SkeletonVisual();
    assertEquals(false, sv.hasDefaultPoseWeightedMeshes.getValue());
  }

  @Test
  public void boundingBoxFromWeightedMeshes() {
    SkeletonVisual sv = new SkeletonVisual();
    // WeightedMesh extends Mesh, so use vertexBuffer
    WeightedMesh wm = new WeightedMesh();
    java.nio.DoubleBuffer vb = java.nio.DoubleBuffer.wrap(
        new double[]{-1, -1, -1, 1, 1, 1});
    wm.vertexBuffer.setValue(vb);
    wm.normalBuffer.setValue(java.nio.FloatBuffer.wrap(new float[]{0, 0, 1, 0, 0, 1}));
    wm.indexBuffer.setValue(java.nio.IntBuffer.wrap(new int[]{0, 1, 0}));
    sv.weightedMeshes.setValue(new WeightedMesh[]{wm});

    AxisAlignedBox bbox = sv.getAxisAlignedMinimumBoundingBox(true);
    assertNotNull(bbox);
    assertEquals(-1.0, bbox.minimum().x(), EPSILON);
    assertEquals(1.0, bbox.maximum().x(), EPSILON);
  }

  @Test
  public void boundingBoxFromDefaultPoseWeightedMeshes() {
    SkeletonVisual sv = new SkeletonVisual();
    sv.hasDefaultPoseWeightedMeshes.setValue(true);

    WeightedMesh wm = new WeightedMesh();
    java.nio.DoubleBuffer vb = java.nio.DoubleBuffer.wrap(
        new double[]{-2, -2, -2, 2, 2, 2});
    wm.vertexBuffer.setValue(vb);
    wm.normalBuffer.setValue(java.nio.FloatBuffer.wrap(new float[]{0, 0, 1, 0, 0, 1}));
    wm.indexBuffer.setValue(java.nio.IntBuffer.wrap(new int[]{0, 1, 0}));
    sv.defaultPoseWeightedMeshes.setValue(new WeightedMesh[]{wm});

    AxisAlignedBox bbox = sv.getAxisAlignedMinimumBoundingBox(true);
    assertNotNull(bbox);
    assertEquals(-2.0, bbox.minimum().x(), EPSILON);
    assertEquals(2.0, bbox.maximum().x(), EPSILON);
  }

  @Test
  public void renderBackfacesReturnsFalseByDefault() {
    SkeletonVisual sv = new SkeletonVisual();
    sv.weightedMeshes.setValue(new WeightedMesh[0]);
    assertEquals(false, sv.renderBackfaces());
  }

  @Test
  public void renderBackfacesReturnsTrueWhenMeshDisablesCull() {
    SkeletonVisual sv = new SkeletonVisual();
    WeightedMesh wm = new WeightedMesh();
    wm.cullBackfaces.setValue(false);
    sv.weightedMeshes.setValue(new WeightedMesh[]{wm});
    assertTrue(sv.renderBackfaces());
  }

  @Test
  public void renderBackfacesChecksRegularGeometries() {
    SkeletonVisual sv = new SkeletonVisual();
    sv.weightedMeshes.setValue(new WeightedMesh[0]);
    Mesh mesh = new Mesh();
    mesh.cullBackfaces.setValue(false);
    sv.geometries.setValue(new Geometry[]{mesh});
    assertTrue(sv.renderBackfaces());
  }

  @Test
  public void scaleWithNullSkeleton() {
    SkeletonVisual sv = new SkeletonVisual();
    Mesh mesh = new Mesh();
    java.nio.DoubleBuffer vb = java.nio.DoubleBuffer.wrap(
        new double[]{1, 0, 0, 0, 1, 0, 0, 0, 1});
    mesh.vertexBuffer.setValue(vb);
    mesh.normalBuffer.setValue(java.nio.FloatBuffer.wrap(new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1}));
    mesh.indexBuffer.setValue(java.nio.IntBuffer.wrap(new int[]{0, 1, 2}));
    sv.geometries.setValue(new Geometry[]{mesh});
    sv.weightedMeshes.setValue(new WeightedMesh[0]);

    sv.scale(2.0);
    assertEquals(2.0, mesh.vertexBuffer.getValue().get(0), EPSILON);
  }

  @Test
  public void releaseWithEmptyArraysDoesNotThrow() {
    SkeletonVisual sv = new SkeletonVisual();
    sv.weightedMeshes.setValue(new WeightedMesh[0]);
    sv.defaultPoseWeightedMeshes.setValue(new WeightedMesh[0]);
    sv.textures.setValue(new TexturedAppearance[0]);
    sv.release();
  }

  @Test
  public void setParentWithNullSkeletonDoesNotThrow() {
    SkeletonVisual sv = new SkeletonVisual();
    Transformable parent = new Transformable();
    sv.setParent(parent);
    // Should not throw
    assertEquals(parent, sv.getParent());
  }
}
