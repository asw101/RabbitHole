package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OldMeshTest {
  private static final double EPSILON = 0.000001;

  private OldMesh createSimpleOldMesh() {
    OldMesh mesh = new OldMesh();
    mesh.xyzs.setValue(new double[]{0, 0, 0, 1, 0, 0, 0, 1, 0});
    mesh.ijks.setValue(new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1});
    mesh.uvs.setValue(new float[]{0, 0, 1, 0, 0, 1});
    return mesh;
  }

  @Test
  public void defaultConstructorCreatesOldMesh() {
    OldMesh mesh = new OldMesh();
    assertNotNull(mesh);
  }

  @Test
  public void updateBoundingBoxReturnsCorrectBounds() {
    OldMesh mesh = createSimpleOldMesh();
    AxisAlignedBox bbox = mesh.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertEquals(0.0, bbox.minimum().x(), EPSILON);
    assertEquals(0.0, bbox.minimum().y(), EPSILON);
    assertEquals(0.0, bbox.minimum().z(), EPSILON);
    assertEquals(1.0, bbox.maximum().x(), EPSILON);
    assertEquals(1.0, bbox.maximum().y(), EPSILON);
    assertEquals(0.0, bbox.maximum().z(), EPSILON);
  }

  @Test
  public void getPlaneReturnsAffineMatrix() {
    OldMesh mesh = createSimpleOldMesh();
    AffineMatrix4x4 plane = mesh.getPlane();
    assertNotNull(plane);
    // Translation should be first vertex position
    assertEquals(0.0, plane.translation().x(), EPSILON);
    assertEquals(0.0, plane.translation().y(), EPSILON);
    assertEquals(0.0, plane.translation().z(), EPSILON);
  }

  @Test
  public void transformIsNoOp() {
    OldMesh mesh = createSimpleOldMesh();
    // transform is a no-op (todo in source), just verify it doesn't throw
    mesh.transform(null);
  }

  @Test
  public void propertiesAreSettable() {
    OldMesh mesh = new OldMesh();
    mesh.xyzs.setValue(new double[]{1, 2, 3, 4, 5, 6});
    mesh.ijks.setValue(new float[]{0, 1, 0, 0, 1, 0});
    mesh.uvs.setValue(new float[]{0, 0, 1, 1});
    mesh.xyzTriangleIndices.setValue(new short[]{0, 1, 0});
    mesh.ijkTriangleIndices.setValue(new short[]{0, 1, 0});
    mesh.uvTriangleIndices.setValue(new short[]{0, 1, 0});
    mesh.xyzQuadrangleIndices.setValue(new short[]{0, 1, 0, 1});
    mesh.ijkQuadrangleIndices.setValue(new short[]{0, 1, 0, 1});
    mesh.uvQuadrangleIndices.setValue(new short[]{0, 1, 0, 1});

    assertNotNull(mesh.xyzs.getValue());
    assertNotNull(mesh.ijks.getValue());
    assertNotNull(mesh.uvs.getValue());
  }
}
