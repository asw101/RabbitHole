package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Sphere;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrSphereAdapterPickIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void pick_emitsVisualNamesAndVertices() {
    Sphere sphere = new Sphere(); Visual visual = AdapterRenderTestSupport.visualWith(sphere, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); PickContext pc = AdapterRenderTestSupport.pickContext(gl);
    adapter.pick(pc, AdapterRenderTestSupport.requiredPickParameters());
    assertTrue(gl.calls("glPushName").size() >= 2); assertTrue(gl.calls("glVertex3d").size() > 0);
  }
}
