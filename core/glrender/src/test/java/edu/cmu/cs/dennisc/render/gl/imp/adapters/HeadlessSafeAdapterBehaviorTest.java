package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2GL3;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.RecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.ComponentArray;
import edu.cmu.cs.dennisc.scenegraph.Ghost;
import edu.cmu.cs.dennisc.scenegraph.IndexedQuadrilateralArray;
import edu.cmu.cs.dennisc.scenegraph.LineArray;
import edu.cmu.cs.dennisc.scenegraph.LineLoop;
import edu.cmu.cs.dennisc.scenegraph.LineStrip;
import edu.cmu.cs.dennisc.scenegraph.PointArray;
import edu.cmu.cs.dennisc.scenegraph.QuadArray;
import edu.cmu.cs.dennisc.scenegraph.Sprite;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.TriangleArray;
import edu.cmu.cs.dennisc.scenegraph.TriangleFan;
import edu.cmu.cs.dennisc.scenegraph.TriangleStrip;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.scenegraph.VertexGeometry;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class HeadlessSafeAdapterBehaviorTest {

  @Before
  public void setUp() {
    AdapterFactory.forgetAllElements();
  }

  @Test
  public void vertexGeometryUpdatesAlphaBlendingFromVertices() {
    LineArray geometry = new LineArray();
    GlrLineArray adapter = new GlrLineArray();
    adapter.initialize(geometry);

    Vertex[] vertices = new Vertex[] {
        Vertex.createXYZIJK(0.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZRGBA(1.0, 0.0, 0.0, 0.2f, 0.3f, 0.4f, 0.5f),
        Vertex.createXYZIJK(1.0, 1.0, 0.0, 0.0f, 0.0f, 1.0f)
    };
    geometry.vertices.setValue(vertices);

    adapter.propertyChanged(geometry.vertices);

    assertTrue(adapter.isAlphaBlended());
    assertSame(vertices[1], adapter.accessVertexAt(1));
  }

  @Test
  public void indexedQuadrilateralArrayRendersPicksAndIntersectsPlane() {
    IndexedQuadrilateralArray geometry = new IndexedQuadrilateralArray();
    GlrIndexedQuadrilateralArray adapter = new GlrIndexedQuadrilateralArray();
    adapter.initialize(geometry);
    geometry.vertices.setValue(planarVertices());
    geometry.polygonData.setValue(IntBuffer.wrap(new int[] {0, 1, 2, 3}));

    adapter.propertyChanged(geometry.vertices);
    adapter.propertyChanged(geometry.polygonData);

    RecordingGL2 renderGl = new RecordingGL2();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(renderGl);
    adapter.renderGeometry(renderContext, GlrVisual.RenderType.OPAQUE);

    assertTrue(renderGl.wasCalledWith("glBegin", GL2GL3.GL_QUADS));
    assertEquals(4, renderGl.calls("glVertex3d").size());
    assertTrue(renderGl.wasCalledWith("glEnd"));

    RecordingGL2 pickGl = new RecordingGL2();
    PickContext pickContext = new PickContext(true);
    pickContext.setGL(pickGl);
    adapter.pickGeometry(pickContext, true);

    assertTrue(pickGl.wasCalledWith("glPushName", -1));
    assertTrue(pickGl.wasCalledWith("glLoadName", 0));
    assertTrue(pickGl.wasCalledWith("glBegin", GL2GL3.GL_QUADS));
    assertEquals(4, pickGl.calls("glVertex3d").size());
    assertTrue(pickGl.wasCalledWith("glPopName"));

    Point3 hit = adapter.getIntersectionInSource(
        Ray.fromAtoB(new Point3(0.0, 0.0, 1.0), new Point3(0.0, 0.0, -1.0)),
        Matrix4x4.IDENTITY,
        0);
    assertFalse(hit.isNaN());
    assertEquals(0.0, hit.x(), 0.0001);
    assertEquals(0.0, hit.y(), 0.0001);
    assertEquals(0.0, hit.z(), 0.0001);
  }

  @Test
  public void primitiveArrayAdaptersUseExpectedModesAndNaNIntersections() {
    assertPrimitiveAdapter(new GlrLineArray(), new LineArray(), GL.GL_LINES);
    assertPrimitiveAdapter(new GlrLineLoop(), new LineLoop(), GL.GL_LINE_LOOP);
    assertPrimitiveAdapter(new GlrLineStrip(), new LineStrip(), GL.GL_LINE_STRIP);
    assertPrimitiveAdapter(new GlrPointArray(), new PointArray(), GL.GL_POINTS);
    assertPrimitiveAdapter(new GlrTriangleArray(), new TriangleArray(), GL.GL_TRIANGLES);
    assertPrimitiveAdapter(new GlrTriangleFan(), new TriangleFan(), GL.GL_TRIANGLE_FAN);
    assertPrimitiveAdapter(new GlrTriangleStrip(), new TriangleStrip(), GL.GL_TRIANGLE_STRIP);

    GlrQuadArray quadArray = new GlrQuadArray();
    assertPrimitiveAdapter(quadArray, new QuadArray(), GL2GL3.GL_QUADS);
    assertFalse(quadArray.isDisplayListDesired());
  }

  @Test
  public void ghostOpacitySyncsAndOpaqueRenderIsNoOp() throws Exception {
    Ghost ghost = new Ghost();
    GlrGhost adapter = new GlrGhost();
    adapter.initialize(ghost);

    Field opacityField = GlrGhost.class.getDeclaredField("opacity");
    opacityField.setAccessible(true);
    assertEquals(ghost.opacity.getValue(), opacityField.getFloat(adapter), 0.0001f);

    ghost.opacity.setValue(0.25f);
    adapter.propertyChanged(ghost.opacity);

    assertEquals(0.25f, opacityField.getFloat(adapter), 0.0001f);
    adapter.renderOpaque(null);
  }

  @Test
  public void componentArrayBehaviorDocumentsTodoPaths() throws Exception {
    TestGlrComponentArray adapter = new TestGlrComponentArray();
    ComponentArray componentArray = new ComponentArray();

    assertTodo(() -> adapter.initialize(componentArray));
    assertSame(componentArray, adapter.getOwner());
    assertTrue(isGeometryChanged(adapter));

    assertTodo(() -> adapter.renderGeometry(null, GlrVisual.RenderType.OPAQUE));
    assertTodo(() -> adapter.pickGeometry(null, false));

    componentArray.component.setValue(new Transformable());
    assertTodo(() -> adapter.propertyChanged(componentArray.component));
    assertTrue(isGeometryChanged(adapter));
  }

  @Test
  public void spriteBehaviorDocumentsTodoPathsAndRadiusChangesGeometryState() throws Exception {
    GlrSprite adapter = new GlrSprite();
    Sprite sprite = new Sprite();
    adapter.initialize(sprite);

    assertFalse(adapter.isAlphaBlended());
    assertTrue(adapter.getIntersectionInSource(
        Ray.fromAtoB(new Point3(0.0, 0.0, 1.0), new Point3(0.0, 0.0, -1.0)),
        Matrix4x4.IDENTITY,
        0).isNaN());

    sprite.radius.setValue(2.0);
    adapter.propertyChanged(sprite.radius);
    assertTrue(isGeometryChanged(adapter));

    assertTodo(() -> adapter.renderGeometry(null, GlrVisual.RenderType.OPAQUE));
    assertTodo(() -> adapter.pickGeometry(null, false));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static void assertPrimitiveAdapter(GlrVertexGeometry adapter, VertexGeometry geometry, int expectedMode) {
    adapter.initialize(geometry);
    geometry.vertices.setValue(planarVertices());
    adapter.propertyChanged(geometry.vertices);

    RecordingGL2 renderGl = new RecordingGL2();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(renderGl);
    adapter.renderGeometry(renderContext, GlrVisual.RenderType.OPAQUE);

    assertTrue(renderGl.wasCalledWith("glBegin", expectedMode));
    assertEquals(planarVertices().length, renderGl.calls("glVertex3d").size());
    assertTrue(renderGl.wasCalledWith("glEnd"));

    RecordingGL2 pickGl = new RecordingGL2();
    PickContext pickContext = new PickContext(true);
    pickContext.setGL(pickGl);
    adapter.pickGeometry(pickContext, false);

    assertTrue(pickGl.wasCalledWith("glPushName", -1));
    assertTrue(pickGl.wasCalledWith("glBegin", expectedMode));
    assertEquals(planarVertices().length, pickGl.calls("glVertex3d").size());
    assertTrue(pickGl.wasCalledWith("glPopName"));

    assertTrue(adapter.getIntersectionInSource(
        Ray.fromAtoB(new Point3(0.0, 0.0, 1.0), new Point3(0.0, 0.0, -1.0)),
        Matrix4x4.IDENTITY,
        0).isNaN());
  }

  private static Vertex[] planarVertices() {
    return new Vertex[] {
        Vertex.createXYZIJK(0.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZIJK(1.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZIJK(1.0, 1.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZIJK(0.0, 1.0, 0.0, 0.0f, 0.0f, 1.0f)
    };
  }

  private static boolean isGeometryChanged(Object adapter) throws Exception {
    Field field = GlrGeometry.class.getDeclaredField("isGeometryChanged");
    field.setAccessible(true);
    return field.getBoolean(adapter);
  }

  private static void assertTodo(ThrowingRunnable runnable) {
    try {
      runnable.run();
      fail("Expected RuntimeException(todo)");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private interface ThrowingRunnable {
    void run() throws Exception;
  }

  private static final class TestGlrComponentArray extends GlrComponentArray {
    @Override
    public Point3 getIntersectionInSource(Ray ray, Matrix4x4 m, int subElement) {
      return Point3.NaN;
    }
  }
}
