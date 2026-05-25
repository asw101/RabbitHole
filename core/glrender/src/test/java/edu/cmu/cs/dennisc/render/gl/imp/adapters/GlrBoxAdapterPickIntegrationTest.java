package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrBoxAdapterPickIntegrationTest {
@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void pick_withSubElements_loadsNamesForEveryFace() {
    Box box = new Box(); Visual visual = AdapterRenderTestSupport.visualWith(box, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); PickContext pc = AdapterRenderTestSupport.pickContext(gl);
    adapter.pick(pc, AdapterRenderTestSupport.requiredPickParameters());
    assertTrue(gl.calls("glPushName").size() > 0); assertTrue(gl.calls("glVertex3d").size() > 0); assertTrue(gl.wasCalledWith("glEnable", com.jogamp.opengl.GL.GL_CULL_FACE));
  }
}
