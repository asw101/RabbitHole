package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import com.jogamp.opengl.GL;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;
import org.junit.Before;
import org.junit.Test;

import java.nio.IntBuffer;

import static org.junit.Assert.*;

public class GlrIndexedTriangleArrayBehaviorIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void renderGeometryEmitsTriangleVertices() {
    GlrIndexedTriangleArray adapter = (GlrIndexedTriangleArray) AdapterFactory.getAdapterFor(createTriangle());
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);

    adapter.renderGeometry(rc, GlrVisual.RenderType.OPAQUE);

    assertTrue(gl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    assertEquals(3, gl.calls("glVertex3d").size());
    assertTrue(gl.wasCalledWith("glEnd"));
  }

  @Test
  public void pickGeometryAndIntersectionUsePolygonData() {
    GlrIndexedTriangleArray adapter = (GlrIndexedTriangleArray) AdapterFactory.getAdapterFor(createTriangle());
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    PickContext pc = AdapterRenderTestSupport.pickContext(gl);

    adapter.pickGeometry(pc, true);

    assertTrue(gl.wasCalledWith("glPushName", -1));
    assertTrue(gl.wasCalledWith("glLoadName", 0));
    assertEquals(3, gl.calls("glVertex3d").size());

    Ray ray = new Ray(new Point3(0.2, 0.2, 1.0), Vector3.NEGATIVE_Z_AXIS);
    Point3 hit = adapter.getIntersectionInSource(ray, Matrix4x4.IDENTITY, 0);

    assertFalse(hit.isNaN());
    assertEquals(0.2, hit.x(), 0.0001);
    assertEquals(0.2, hit.y(), 0.0001);
    assertEquals(0.0, hit.z(), 0.0001);
    assertTrue(adapter.getIntersectionInSource(ray, Matrix4x4.IDENTITY, -1).isNaN());
  }

  private static IndexedTriangleArray createTriangle() {
    IndexedTriangleArray triangle = new IndexedTriangleArray();
    triangle.vertices.setValue(new Vertex[]{
        Vertex.createXYZIJK(0.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZIJK(1.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZIJK(0.0, 1.0, 0.0, 0.0f, 0.0f, 1.0f)
    });
    triangle.polygonData.setValue(IntBuffer.wrap(new int[]{0, 1, 2}));
    return triangle;
  }
}
