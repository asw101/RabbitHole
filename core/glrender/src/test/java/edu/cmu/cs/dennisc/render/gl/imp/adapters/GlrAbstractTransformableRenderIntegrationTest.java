package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlrAbstractTransformableRenderIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void renderAndPickUseLocalAndAbsoluteTransformBuffers() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));
    visual.isShowing.setValue(true);
    Transformable transformable = AdapterRenderTestSupport.transformableWith(visual, 1.0, 2.0, 3.0);
    GlrTransformable<?> adapter = AdapterFactory.getAdapterFor(transformable);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    PickContext pc = AdapterRenderTestSupport.pickContext(gl);

    adapter.EPIC_HACK_FOR_ICON_CAPTURE_renderOpaque(rc);
    adapter.renderOpaque(rc);
    adapter.renderGhost(rc, null);
    adapter.pick(pc, AdapterRenderTestSupport.requiredPickParameters());

    assertTrue(gl.calls("glPushMatrix").size() >= 4);
    assertTrue(gl.calls("glPopMatrix").size() >= 4);
    assertTrue(gl.calls("glMultMatrixd").size() >= 4);
    assertFalse(rc.isScaled());
    assertFalse(pc.isScaled());
  }
}
