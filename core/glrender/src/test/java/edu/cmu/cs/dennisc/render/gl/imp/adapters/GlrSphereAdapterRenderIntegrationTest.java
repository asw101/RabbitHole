package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Sphere;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrSphereAdapterRenderIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderOpaque_emitsSphereVerticesAndNormals() {
    Sphere sphere = new Sphere(); sphere.radius.setValue(2.0); Visual visual = AdapterRenderTestSupport.visualWith(sphere, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    adapter.renderOpaque(rc);
    assertTrue(gl.calls("glNormal3d").size() > 0); assertTrue(gl.calls("glVertex3d").size() > 0); assertTrue(gl.calls("glBegin").size() > 0);
  }
}
