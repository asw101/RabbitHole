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
  public void bufferFields_existAndArePrivate() throws Exception {
    String[] names = {"vertexBuffer", "normalBuffer", "textCoordBuffer", "indexBuffer"};
    Class<?>[] types = {DoubleBuffer.class, FloatBuffer.class, FloatBuffer.class, IntBuffer.class};
    for (int i = 0; i < names.length; i++) {
      Field f = GlrMesh.class.getDeclaredField(names[i]);
      assertTrue(names[i] + " should be private", Modifier.isPrivate(f.getModifiers()));
      assertEquals(names[i] + " type", types[i], f.getType());
    }
  }

  @Test
  public void newMesh_allBuffersAreNull() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    for (String name : new String[]{"vertexBuffer", "normalBuffer", "textCoordBuffer", "indexBuffer"}) {
      assertNull(name + " should be null initially", getBufferField(mesh, name));
    }
  }

  // ── set buffers via reflection ─────────────────────────────────────

  @Test
  public void setBuffers_viaReflection_areStored() throws Exception {
    TestableGlrMesh mesh = new TestableGlrMesh();
    Object[] buffers = {DoubleBuffer.allocate(9), FloatBuffer.allocate(9),
        FloatBuffer.allocate(6), IntBuffer.allocate(3)};
    String[] names = {"vertexBuffer", "normalBuffer", "textCoordBuffer", "indexBuffer"};
    for (int i = 0; i < names.length; i++) {
      setBufferField(mesh, names[i], buffers[i]);
      assertSame(names[i], buffers[i], getBufferField(mesh, names[i]));
    }
  }

  // ── structural checks ─────────────────────────────────────────────

  @Test
  public void glrMesh_isPublic_andExtendsGlrGeometry() {
    assertTrue(Modifier.isPublic(GlrMesh.class.getModifiers()));
    assertEquals(GlrGeometry.class, GlrMesh.class.getSuperclass());
  }

  @Test
  public void publicApiMethods_exist() throws Exception {
    assertNotNull(GlrMesh.class.getMethod("isAlphaBlended"));
    assertNotNull(GlrMesh.class.getMethod("getIntersectionInSource",
        Ray.class, Matrix4x4.class, int.class));
  }

  @Test
  public void internalRenderMethods_arePackagePrivateStatic() throws Exception {
    for (String methodName : new String[]{"renderMesh", "pickMesh"}) {
      Method m;
      if (methodName.equals("renderMesh")) {
        m = GlrMesh.class.getDeclaredMethod(methodName,
            edu.cmu.cs.dennisc.render.gl.imp.RenderContext.class,
            DoubleBuffer.class, FloatBuffer.class, FloatBuffer.class, IntBuffer.class);
      } else {
        m = GlrMesh.class.getDeclaredMethod(methodName,
            edu.cmu.cs.dennisc.render.gl.imp.PickContext.class,
            DoubleBuffer.class, IntBuffer.class);
      }
      assertFalse(methodName + " should not be public", Modifier.isPublic(m.getModifiers()));
      assertTrue(methodName + " should be static", Modifier.isStatic(m.getModifiers()));
    }
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

  // ── NIO buffer indexing conventions (documenting project assumptions) ─

  @Test
  public void textCoord_index2x_and_normal_index3x_conventions() {
    // Validates the indexing convention: texcoords use index*2, normals/vertices use index*3
    FloatBuffer tc = FloatBuffer.allocate(8);
    tc.put(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f});
    assertEquals(0, tc.capacity() % 2);

    FloatBuffer nb = FloatBuffer.allocate(9);
    nb.put(new float[]{0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f});
    assertEquals(0, nb.capacity() % 3);

    DoubleBuffer vb = DoubleBuffer.allocate(9);
    assertEquals(0, vb.capacity() % 3);
  }

  // ── helpers — Field objects cached to avoid repeated lookup ─────────

  private static final java.util.Map<String, Field> BUFFER_FIELDS = new java.util.HashMap<>();
  static {
    for (String name : new String[]{"vertexBuffer", "normalBuffer", "textCoordBuffer", "indexBuffer"}) {
      try {
        Field f = GlrMesh.class.getDeclaredField(name);
        f.setAccessible(true);
        BUFFER_FIELDS.put(name, f);
      } catch (NoSuchFieldException e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  private Object getBufferField(TestableGlrMesh mesh, String fieldName) throws Exception {
    return BUFFER_FIELDS.get(fieldName).get(mesh);
  }

  private void setBufferField(TestableGlrMesh mesh, String fieldName, Object value) throws Exception {
    BUFFER_FIELDS.get(fieldName).set(mesh, value);
  }

  /**
   * Concrete subclass to enable testing non-GL logic in GlrMesh.
   * Uses a raw type to avoid needing an actual Mesh subclass instance.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static class TestableGlrMesh extends GlrMesh {
  }
}
