package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.IndexedTriangleArray;
import edu.cmu.cs.dennisc.scenegraph.PlanarReflector;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import java.nio.IntBuffer;

import static org.junit.Assert.assertTrue;

public class GlrSceneReflectorRenderIntegrationTest {

@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void renderSceneUsesStencilAndClipPlaneForFacingReflector() {
    PlanarReflector reflector = new PlanarReflector();
    reflector.frontFacingAppearance.setValue(AdapterRenderTestSupport.appearance(1.0f));
    reflector.backFacingAppearance.setValue(AdapterRenderTestSupport.appearance(0.4f));
    reflector.geometries.setValue(new Geometry[]{createTriangle()});
    reflector.isShowing.setValue(true);

    Visual visual = AdapterRenderTestSupport.visualWith(new edu.cmu.cs.dennisc.scenegraph.Box(), AdapterRenderTestSupport.appearance(1.0f));
    Transformable node = AdapterRenderTestSupport.transformableWith(visual, 1.0, 0.0, 0.0);
    SymmetricPerspectiveCamera camera = AdapterRenderTestSupport.perspectiveCamera();
    Scene scene = AdapterRenderTestSupport.sceneWith(camera, reflector, node);
    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);

    sceneAdapter.renderScene(rc, AdapterRenderTestSupport.cameraAdapter(camera), null);

    assertTrue(gl.wasCalledWith("glEnable", GL.GL_STENCIL_TEST));
    assertTrue(gl.wasCalledWith("glEnable", GL2.GL_CLIP_PLANE0));
    assertTrue(gl.wasCalledWith("glFrontFace", GL.GL_CW));
    assertTrue(gl.wasCalledWith("glFrontFace", GL.GL_CCW));
    assertTrue(gl.calls("glStencilFunc").size() >= 2);
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
