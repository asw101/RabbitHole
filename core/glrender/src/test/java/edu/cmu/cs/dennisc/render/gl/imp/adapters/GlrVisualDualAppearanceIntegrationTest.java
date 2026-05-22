package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.PickParameters;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_FRONT;
import static org.junit.Assert.*;

public class GlrVisualDualAppearanceIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void renderPickAndReleaseHandleDifferentFrontAndBackAppearances() {
    Visual visual = new Visual();
    SimpleAppearance front = AdapterRenderTestSupport.appearance(1.0f);
    SimpleAppearance back = AdapterRenderTestSupport.appearance(0.4f);
    visual.frontFacingAppearance.setValue(front);
    visual.backFacingAppearance.setValue(back);
    visual.geometries.setValue(new Geometry[]{new Box()});
    visual.isShowing.setValue(true);
    visual.isPickable.setValue(true);

    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    PickContext pc = AdapterRenderTestSupport.pickContext(gl);

    adapter.renderOpaque(rc);
    adapter.renderAlphaBlended(rc);
    adapter.renderAllAlphaBlended(rc);
    adapter.pick(pc, new PickParameters(null, null, new Point(4, 4), false, null));
    adapter.handleReleased();

    assertTrue(gl.wasCalledWith("glCullFace", GL_BACK));
    assertTrue(gl.wasCalledWith("glCullFace", GL_FRONT));
    assertTrue(gl.calls("glPushName").size() >= 1);
    assertTrue(gl.calls("glBegin").size() > 0);
    assertNull(adapter.getOwner());
  }
}
