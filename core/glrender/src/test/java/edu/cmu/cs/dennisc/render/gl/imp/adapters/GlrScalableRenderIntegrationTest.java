package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Scalable;
import org.alice.math.immutable.Dimension3;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GlrScalableRenderIntegrationTest {

@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void nonIdentityScaleWrapsRenderGhostAndPickInScalingCalls() {
    Scalable scalable = new Scalable();
    scalable.scale.setValue(new Dimension3(2, 3, 4));
    GlrScalable adapter = (GlrScalable) AdapterFactory.getAdapterFor(scalable);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    PickContext pc = AdapterRenderTestSupport.pickContext(gl);

    adapter.renderOpaque(rc);
    adapter.renderGhost(rc, null);
    adapter.pick(pc, AdapterRenderTestSupport.requiredPickParameters());

    assertTrue(gl.wasCalledWith("glScaled", 2.0, 3.0, 4.0));
    assertTrue(gl.calls("glPushMatrix").size() >= 3);
    assertTrue(gl.calls("glPopMatrix").size() >= 3);
    assertFalse(rc.isScaled());
    assertFalse(pc.isScaled());
  }

  @Test
  public void identityScaleSkipsScalingCalls() {
    GlrScalable adapter = (GlrScalable) AdapterFactory.getAdapterFor(new Scalable());
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();

    adapter.renderOpaque(AdapterRenderTestSupport.renderContext(gl));

    assertTrue(gl.calls("glScaled").isEmpty());
  }
}
