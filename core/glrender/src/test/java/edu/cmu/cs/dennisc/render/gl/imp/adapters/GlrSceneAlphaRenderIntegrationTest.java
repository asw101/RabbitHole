package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static com.jogamp.opengl.GL.GL_BLEND;
import static com.jogamp.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static com.jogamp.opengl.GL.GL_SRC_ALPHA;
import static org.junit.Assert.*;

public class GlrSceneAlphaRenderIntegrationTest {
  @Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderScene_enablesBlendForAlphaVisuals() {
    Visual opaque = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f)); Visual alpha = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(0.4f)); Transformable opaqueNode = AdapterRenderTestSupport.transformableWith(opaque, 0.0, 0.0, 0.0); Transformable alphaNode = AdapterRenderTestSupport.transformableWith(alpha, 2.0, 0.0, 0.0); SymmetricPerspectiveCamera camera = AdapterRenderTestSupport.perspectiveCamera(); Scene scene = AdapterRenderTestSupport.sceneWith(camera, opaqueNode, alphaNode);
    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    sceneAdapter.renderScene(rc, AdapterRenderTestSupport.cameraAdapter(camera), null);
    assertTrue(gl.wasCalledWith("glEnable", GL_BLEND)); assertTrue(gl.wasCalledWith("glBlendFunc", GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)); assertTrue(gl.calls("glDisable").size() > 0);
  }
}
