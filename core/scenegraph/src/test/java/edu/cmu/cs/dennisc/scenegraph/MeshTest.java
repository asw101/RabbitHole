package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MeshTest {
  private static final double EPSILON = 0.000001;

  private Mesh createSimpleMesh() {
    Mesh mesh = new Mesh();
    // A simple triangle: three vertices
    DoubleBuffer vb = DoubleBuffer.wrap(new double[]{0, 0, 0, 1, 0, 0, 0, 1, 0});
    FloatBuffer nb = FloatBuffer.wrap(new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1});
    FloatBuffer tb = FloatBuffer.wrap(new float[]{0, 0, 1, 0, 0, 1});
    IntBuffer ib = IntBuffer.wrap(new int[]{0, 1, 2});
    mesh.vertexBuffer.setValue(vb);
    mesh.normalBuffer.setValue(nb);
    mesh.textCoordBuffer.setValue(tb);
    mesh.indexBuffer.setValue(ib);
    return mesh;
  }

  @Test
  public void defaultConstructorCreatesEmptyMesh() {
    Mesh mesh = new Mesh();
    assertNotNull(mesh);
  }

  @Test
  public void copyConstructorCopiesBuffers() {
    Mesh original = createSimpleMesh();
    original.setName("testMesh");
    original.textureId.setValue(42);
    original.useAlphaTest.setValue(true);
    original.cullBackfaces.setValue(false);

    Mesh copy = new Mesh(original);
    assertNotSame(original, copy);
    assertEquals("testMesh", copy.getName());
    assertEquals(42, copy.textureId.getValue().intValue());
    assertTrue(copy.useAlphaTest.getValue());
    assertFalse(copy.cullBackfaces.getValue());
  }

  @Test
  public void copyConstructorCopiesAllBuffers() {
    Mesh original = createSimpleMesh();
    original.setName("bufferMesh");
    Mesh copy = new Mesh(original);
    // Verify all buffers were copied
    assertNotNull(copy.vertexBuffer.getValue());
    assertNotNull(copy.normalBuffer.getValue());
    assertNotNull(copy.textCoordBuffer.getValue());
    assertNotNull(copy.indexBuffer.getValue());
    assertEquals("bufferMesh", copy.getName());
  }

  @Test
  public void createCopyReturnsDistinctMesh() {
    Mesh original = createSimpleMesh();
    Mesh copy = original.createCopy();
    assertNotNull(copy);
    assertNotSame(original, copy);
  }

  @Test
  public void scaleMultipliesVertexPositions() {
    Mesh mesh = createSimpleMesh();
    mesh.scale(2.0);

    DoubleBuffer vb = mesh.vertexBuffer.getValue();
    // vertex at (1,0,0) should become (2,0,0)
    assertEquals(2.0, vb.get(3), EPSILON);
    // vertex at (0,1,0) should become (0,2,0)
    assertEquals(2.0, vb.get(7), EPSILON);
  }

  @Test
  public void invertNormalsNegatesAllComponents() {
    Mesh mesh = createSimpleMesh();
    mesh.invertNormals();

    FloatBuffer nb = mesh.normalBuffer.getValue();
    // Original normals were (0,0,1), should now be (0,0,-1)
    assertEquals(-1.0f, nb.get(2), EPSILON);
    assertEquals(-1.0f, nb.get(5), EPSILON);
    assertEquals(-1.0f, nb.get(8), EPSILON);
  }

  @Test
  public void invertIndicesSwapsSecondAndThirdPerTriangle() {
    Mesh mesh = createSimpleMesh();
    mesh.invertIndices();

    IntBuffer ib = mesh.indexBuffer.getValue();
    // Original: 0,1,2 -> should become 0,2,1
    assertEquals(0, ib.get(0));
    assertEquals(2, ib.get(1));
    assertEquals(1, ib.get(2));
  }

  @Test
  public void transformMovesVertices() {
    Mesh mesh = createSimpleMesh();
    Matrix4x4 translate = Matrix4x4.fromTranslation(new Point3(10, 20, 30));
    mesh.transform(translate);

    DoubleBuffer vb = mesh.vertexBuffer.getValue();
    // First vertex (0,0,0) should now be (10,20,30)
    assertEquals(10.0, vb.get(0), EPSILON);
    assertEquals(20.0, vb.get(1), EPSILON);
    assertEquals(30.0, vb.get(2), EPSILON);
  }

  @Test
  public void updateBoundingBoxReturnsBounds() {
    Mesh mesh = createSimpleMesh();
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
    Mesh mesh = createSimpleMesh();
    assertNotNull(mesh.getPlane());
  }

  @Test
  public void getReferencedTextureIdsReturnsDefaultWhenNoArray() {
    Mesh mesh = createSimpleMesh();
    mesh.textureId.setValue(7);
    List<Integer> ids = mesh.getReferencedTextureIds();
    assertEquals(1, ids.size());
    assertEquals(7, ids.get(0).intValue());
  }

  @Test
  public void getReferencedTextureIdsUsesArrayWhenPopulated() {
    Mesh mesh = createSimpleMesh();
    mesh.textureIdArray.add(1);
    mesh.textureIdArray.add(2);
    mesh.textureIdArray.add(1);
    List<Integer> ids = mesh.getReferencedTextureIds();
    assertEquals(2, ids.size());
    assertTrue(ids.contains(1));
    assertTrue(ids.contains(2));
  }

  @Test
  public void getTextureIdReturnsDefaultWhenArrayEmpty() {
    Mesh mesh = createSimpleMesh();
    mesh.textureId.setValue(5);
    assertEquals(5, mesh.getTextureId(0).intValue());
  }

  @Test
  public void getTextureIdFromArrayReturnsCorrectValue() {
    Mesh mesh = createSimpleMesh();
    mesh.textureIdArray.add(3);
    mesh.textureIdArray.add(3);
    mesh.textureIdArray.add(3);
    assertEquals(3, mesh.getTextureId(0).intValue());
  }

  @Test
  public void cullBackfacesDefaultsToTrue() {
    Mesh mesh = new Mesh();
    assertTrue(mesh.cullBackfaces.getValue());
  }

  @Test
  public void useAlphaTestDefaultsToFalse() {
    Mesh mesh = new Mesh();
    assertFalse(mesh.useAlphaTest.getValue());
  }
}
