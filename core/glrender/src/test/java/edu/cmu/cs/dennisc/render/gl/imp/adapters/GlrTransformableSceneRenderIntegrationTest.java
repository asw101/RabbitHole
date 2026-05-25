package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static org.junit.Assert.*;

public class GlrTransformableSceneRenderIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderScene_walksCameraTransformableAndVisualHierarchy() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f)); Transformable transformable = AdapterRenderTestSupport.transformableWith(visual, 1.0, 2.0, 3.0); SymmetricPerspectiveCamera camera = AdapterRenderTestSupport.perspectiveCamera(); Scene scene = AdapterRenderTestSupport.sceneWith(camera, transformable);
    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene); GlrSymmetricPerspectiveCamera cameraAdapter = AdapterRenderTestSupport.cameraAdapter(camera); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    sceneAdapter.renderScene(rc, cameraAdapter, null);
    assertTrue(gl.wasCalledWith("glClear", GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)); assertTrue(gl.calls("glLoadMatrixd").size() >= 1); assertTrue(gl.calls("glMultMatrixd").size() >= 1); assertTrue(gl.calls("glBegin").size() > 0);
  }
}
