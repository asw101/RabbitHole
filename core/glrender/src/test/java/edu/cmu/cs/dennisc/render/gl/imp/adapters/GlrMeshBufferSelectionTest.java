package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrMesh} — buffer field management, structural API,
 * getIntersectionInSource returning NaN, and isAlphaBlended returning false.
 * Also documents the NIO buffer indexing conventions (index×2 for texcoords,
 * index×3 for normals/vertices) assumed by {@code renderMeshAsArrays}.
 * Avoids actual GL calls.
 */
public class GlrMeshBufferSelectionTest {

  // ── isAlphaBlended always false ────────────────────────────────────

  @Test
  public void isAlphaBlended_returnsFalse() {
    TestableGlrMesh mesh = new TestableGlrMesh();
    assertFalse(mesh.isAlphaBlended());
  }

  // ── getIntersectionInSource returns NaN point ──────────────────────

  @Test
  public void getIntersectionInSource_returnsNaN() {
    TestableGlrMesh mesh = new TestableGlrMesh();
    Ray ray = new Ray(new Point3(0, 0, 0), new Vector3(1, 0, 0));
    Matrix4x4 m = Matrix4x4.IDENTITY;
    Point3 result = mesh.getIntersectionInSource(ray, m, 0);
    assertNotNull(result);
    assertTrue(Double.isNaN(result.x()));
    assertTrue(Double.isNaN(result.y()));
    assertTrue(Double.isNaN(result.z()));
  }

  @Test
  public void getIntersectionInSource_anySubElement_returnsNaN() {
    TestableGlrMesh mesh = new TestableGlrMesh();
    Ray ray = new Ray(new Point3(1, 2, 3), new Vector3(0, 1, 0));
    Matrix4x4 m = Matrix4x4.IDENTITY;
    for (int sub = -1; sub <= 5; sub++) {
      Point3 result = mesh.getIntersectionInSource(ray, m, sub);
      assertTrue("subElement=" + sub, Double.isNaN(result.x()));
    }
  }

  @Test
  public void getIntersectionInSource_isSameNaNInstance() {
    TestableGlrMesh mesh = new TestableGlrMesh();
    Ray ray = new Ray(new Point3(0, 0, 0), new Vector3(0, 0, 1));
    Point3 r1 = mesh.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);
    Point3 r2 = mesh.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 1);
    // Both should be Point3.NaN
    assertSame(Point3.NaN, r1);
    assertSame(Point3.NaN, r2);
  }

  // ── buffer fields via reflection ───────────────────────────────────

  @Test
  public void vertexBuffer_fieldExists() throws Exception {
    Field f = GlrMesh.class.getDeclaredField("vertexBuffer");
    assertNotNull(f);
    assertEquals(DoubleBuffer.class, f.getType());
  }

  @Test
  public void normalBuffer_fieldExists() throws Exception {
    Field f = GlrMesh.class.getDeclaredField("normalBuffer");
    assertNotNull(f);
    assertEquals(FloatBuffer.class, f.getType());
  }

  @Test
  public void textCoordBuffer_fieldExists() throws Exception {
    Field f = GlrMesh.class.getDeclaredField("textCoordBuffer");
    assertNotNull(f);
    assertEquals(FloatBuffer.class, f.getType());
  }

  @Test
  public void indexBuffer_fieldExists() throws Exception {
    Field f = GlrMesh.class.getDeclaredField("indexBuffer");
    assertNotNull(f);
    assertEquals(IntBuffer.class, f.getType());
  }

  @Test
  public void allBufferFields_arePrivate() throws Exception {
    String[] names = {"vertexBuffer", "normalBuffer", "textCoordBuffer", "indexBuffer"};
    for (String name : names) {
      Field f = GlrMesh.class.getDeclaredField(name);
      assertTrue(name + " should be private", Modifier.isPrivate(f.getModifiers()));
    }
  }

  // ── new mesh: buffers are initially null ───────────────────────────

  @Test
  public void newMesh_vertexBufferIsNull() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    assertNull(getBufferField(mesh, "vertexBuffer"));
  }

  @Test
  public void newMesh_normalBufferIsNull() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    assertNull(getBufferField(mesh, "normalBuffer"));
  }

  @Test
  public void newMesh_textCoordBufferIsNull() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    assertNull(getBufferField(mesh, "textCoordBuffer"));
  }

  @Test
  public void newMesh_indexBufferIsNull() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    assertNull(getBufferField(mesh, "indexBuffer"));
  }

  // ── set buffers via reflection ─────────────────────────────────────

  @Test
  public void setVertexBuffer_viaReflection_isStored() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    DoubleBuffer buf = DoubleBuffer.allocate(9);
    setBufferField(mesh, "vertexBuffer", buf);
    assertSame(buf, getBufferField(mesh, "vertexBuffer"));
  }

  @Test
  public void setNormalBuffer_viaReflection_isStored() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    FloatBuffer buf = FloatBuffer.allocate(9);
    setBufferField(mesh, "normalBuffer", buf);
    assertSame(buf, getBufferField(mesh, "normalBuffer"));
  }

  @Test
  public void setTextCoordBuffer_viaReflection_isStored() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    FloatBuffer buf = FloatBuffer.allocate(6);
    setBufferField(mesh, "textCoordBuffer", buf);
    assertSame(buf, getBufferField(mesh, "textCoordBuffer"));
  }

  @Test
  public void setIndexBuffer_viaReflection_isStored() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    IntBuffer buf = IntBuffer.allocate(3);
    setBufferField(mesh, "indexBuffer", buf);
    assertSame(buf, getBufferField(mesh, "indexBuffer"));
  }

  // ── structural checks ─────────────────────────────────────────────

  @Test
  public void glrMesh_isPublic() {
    assertTrue(Modifier.isPublic(GlrMesh.class.getModifiers()));
  }

  @Test
  public void glrMesh_extendsGlrGeometry() {
    assertEquals(GlrGeometry.class, GlrMesh.class.getSuperclass());
  }

  @Test
  public void renderMesh_methodExists_packagePrivate() throws Exception {
    Method m = GlrMesh.class.getDeclaredMethod("renderMesh",
        edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class,
        DoubleBuffer.class, FloatBuffer.class, FloatBuffer.class, IntBuffer.class);
    assertNotNull(m);
    assertFalse(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void pickMesh_methodExists_packagePrivate() throws Exception {
    Method m = GlrMesh.class.getDeclaredMethod("pickMesh",
        edu.cmu.cs.dennisc.render.gl.imp.PickContext.class,
        DoubleBuffer.class, IntBuffer.class);
    assertNotNull(m);
    assertFalse(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void isAlphaBlended_isPublic() throws Exception {
    Method m = GlrMesh.class.getMethod("isAlphaBlended");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void getIntersectionInSource_isPublic() throws Exception {
    Method m = GlrMesh.class.getMethod("getIntersectionInSource",
        Ray.class, Matrix4x4.class, int.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── NIO buffer indexing conventions used by renderMeshAsArrays ──────

  @Test
  public void doubleBuffer_vertexCapacity_isMultipleOfThree() {
    DoubleBuffer vb = DoubleBuffer.allocate(9);
    vb.put(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0});
    assertEquals(0, vb.capacity() % 3);
    assertEquals(3, vb.capacity() / 3);
  }

  @Test
  public void floatBuffer_normalCapacity_matchesVertexCount() {
    int vertexCount = 4;
    FloatBuffer nb = FloatBuffer.allocate(vertexCount * 3);
    assertEquals(vertexCount * 3, nb.capacity());
  }

  @Test
  public void floatBuffer_textCoordCapacity_isTwoPerVertex() {
    int vertexCount = 4;
    FloatBuffer tc = FloatBuffer.allocate(vertexCount * 2);
    assertEquals(vertexCount * 2, tc.capacity());
  }

  @Test
  public void intBuffer_indexTriangles_isMultipleOfThree() {
    IntBuffer ib = IntBuffer.allocate(6);
    ib.put(new int[]{0, 1, 2, 2, 3, 0});
    assertEquals(0, ib.capacity() % 3);
  }

  @Test
  public void bufferRewind_resetsPositionToZero() {
    DoubleBuffer vb = DoubleBuffer.allocate(9);
    vb.put(1.0);
    vb.put(2.0);
    assertEquals(2, vb.position());
    vb.rewind();
    assertEquals(0, vb.position());
  }

  @Test
  public void indexBufferPosition_afterPut_advancesCorrectly() {
    IntBuffer ib = IntBuffer.allocate(6);
    ib.put(0);
    ib.put(1);
    ib.put(2);
    assertEquals(3, ib.position());
    ib.rewind();
    assertEquals(0, ib.position());
    assertEquals(6, ib.remaining());
  }

  @Test
  public void textCoordBuffer_indexAccess_2x() {
    FloatBuffer tc = FloatBuffer.allocate(8);
    tc.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f});
    int index = 2;
    int index2x = index * 2;
    tc.position(index2x);
    assertEquals(1.0f, tc.get(), 0.0001f);
    assertEquals(1.0f, tc.get(), 0.0001f);
  }

  @Test
  public void normalBuffer_indexAccess_3x() {
    FloatBuffer nb = FloatBuffer.allocate(9);
    nb.put(new float[]{0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f});
    int index = 1;
    int index3x = index * 3;
    nb.position(index3x);
    assertEquals(0.0f, nb.get(), 0.0001f);
    assertEquals(0.0f, nb.get(), 0.0001f);
    assertEquals(1.0f, nb.get(), 0.0001f);
  }

  @Test
  public void vertexBuffer_indexAccess_3x() {
    DoubleBuffer vb = DoubleBuffer.allocate(9);
    vb.put(new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0});
    int index = 2;
    int index3x = index * 3;
    assertEquals(7.0, vb.get(index3x), 0.0001);
    assertEquals(8.0, vb.get(index3x + 1), 0.0001);
    assertEquals(9.0, vb.get(index3x + 2), 0.0001);
  }

  // ── helpers ────────────────────────────────────────────────────────

  private Object getBufferField(TestableGlrMesh mesh, String fieldName) throws Exception {
    Field f = GlrMesh.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    return f.get(mesh);
  }

  private void setBufferField(TestableGlrMesh mesh, String fieldName, Object value) throws Exception {
    Field f = GlrMesh.class.getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(mesh, value);
  }

  /**
   * Concrete subclass to enable testing non-GL logic in GlrMesh.
   * Uses a raw type to avoid needing an actual Mesh subclass instance.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static class TestableGlrMesh extends GlrMesh {
  }
}
