package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrVisualPickStateIntegrationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void nonPickableOrEthereal_visualDoesNotEmitPickNames() {
    Visual nonPickable = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f)); nonPickable.isPickable.setValue(false);
    GlrVisual<Visual> nonPickableAdapter = AdapterRenderTestSupport.visualAdapter(nonPickable); HeadlessRecordingGL2 nonPickableGl = AdapterRenderTestSupport.gl(); PickContext nonPickableContext = AdapterRenderTestSupport.pickContext(nonPickableGl); nonPickableAdapter.pick(nonPickableContext, AdapterRenderTestSupport.requiredPickParameters()); assertEquals(0, nonPickableGl.calls("glPushName").size());
    AdapterRenderTestSupport.resetFactory(); Visual ethereal = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.wireframeAppearance(1.0f, true)); GlrVisual<Visual> etherealAdapter = AdapterRenderTestSupport.visualAdapter(ethereal); HeadlessRecordingGL2 etherealGl = AdapterRenderTestSupport.gl(); PickContext etherealContext = AdapterRenderTestSupport.pickContext(etherealGl); etherealAdapter.pick(etherealContext, AdapterRenderTestSupport.requiredPickParameters()); assertEquals(0, etherealGl.calls("glPushName").size());
  }
}
