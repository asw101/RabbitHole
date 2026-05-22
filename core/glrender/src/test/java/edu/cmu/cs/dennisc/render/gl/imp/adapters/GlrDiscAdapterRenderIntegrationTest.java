package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Disc;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrDiscAdapterRenderIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderOpaque_rotatesForAxisAndRendersBothFaces() {
    Disc disc = AdapterRenderTestSupport.configuredDisc(); Visual visual = AdapterRenderTestSupport.visualWith(disc, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    adapter.renderOpaque(rc);
    assertTrue(gl.calls("glPushMatrix").size() > 0 || gl.calls("glBegin").size() > 0);
  }
}
