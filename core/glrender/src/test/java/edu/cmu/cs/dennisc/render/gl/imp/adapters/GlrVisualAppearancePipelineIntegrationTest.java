package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL2GL3.GL_LINE;
import static org.junit.Assert.*;

public class GlrVisualAppearancePipelineIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void wireframeEtherealAppearance_updatesPipelineState() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.wireframeAppearance(1.0f, true)); GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl); adapter.renderOpaque(rc);
    assertTrue(gl.wasCalledWith("glPolygonMode", com.jogamp.opengl.GL.GL_FRONT_AND_BACK, GL_LINE)); assertTrue(gl.wasCalledWith("glDepthFunc", GL_LEQUAL));
  }
}
