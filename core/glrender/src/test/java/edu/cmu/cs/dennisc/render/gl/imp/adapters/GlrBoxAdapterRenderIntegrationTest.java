package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static org.junit.Assert.*;

public class GlrBoxAdapterRenderIntegrationTest {
  @Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderOpaque_drawsAllBoxFacesAndAppearanceState() {
    Box box = new Box();
    box.xMinimum.setValue(-1.0); box.xMaximum.setValue(1.5); box.yMinimum.setValue(-2.0); box.yMaximum.setValue(2.0); box.zMinimum.setValue(-3.0); box.zMaximum.setValue(3.0);
    Visual visual = AdapterRenderTestSupport.visualWith(box, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    adapter.renderOpaque(rc);
    assertTrue(gl.wasCalledWith("glBegin", GL_QUADS));
    assertEquals(24, gl.calls("glVertex3d").size());
    assertTrue(gl.wasCalledWith("glPolygonMode", com.jogamp.opengl.GL.GL_FRONT_AND_BACK, com.jogamp.opengl.GL2GL3.GL_FILL));
    assertTrue(gl.wasCalledWith("glDepthFunc", com.jogamp.opengl.GL.GL_LESS));
  }
}
