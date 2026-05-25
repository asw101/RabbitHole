package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import com.jogamp.opengl.GL2ES1;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.PlanarReflector;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.nio.IntBuffer;

import static org.junit.Assert.assertTrue;

public class GlrPlanarReflectorRenderIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void reflectionAndStencilRenderingIssueExpectedGlCalls() {
    PlanarReflector reflector = new PlanarReflector();
    reflector.frontFacingAppearance.setValue(AdapterRenderTestSupport.appearance(1.0f));
    reflector.backFacingAppearance.setValue(AdapterRenderTestSupport.appearance(0.5f));
    reflector.geometries.setValue(new Geometry[]{createTriangle()});
    reflector.isShowing.setValue(true);

    GlrPlanarReflector adapter = (GlrPlanarReflector) AdapterFactory.getAdapterFor(reflector);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);

    adapter.applyReflection(rc);
    adapter.renderStencil(rc, GlrVisual.RenderType.OPAQUE);
    assertTrue(adapter.isFacing(AdapterRenderTestSupport.cameraAdapter(new SymmetricPerspectiveCamera())));

    assertTrue(gl.calls("glClipPlane").size() >= 1);
    assertTrue(gl.calls("glMultMatrixd").size() >= 2);
    assertTrue(gl.wasCalledWith("glEnable", GL2ES1.GL_CLIP_PLANE0) || gl.calls("glClipPlane").size() >= 1);
    assertTrue(gl.calls("glBegin").size() > 0);
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
