package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrVisualRenderStateIntegrationTest {
@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void alphaAppearance_skipsOpaqueButRendersAlphaPass() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(0.5f)); GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 opaqueGl = AdapterRenderTestSupport.gl(); RenderContext opaqueContext = AdapterRenderTestSupport.renderContext(opaqueGl); adapter.renderOpaque(opaqueContext); assertEquals(0, opaqueGl.calls("glBegin").size());
    HeadlessRecordingGL2 alphaGl = AdapterRenderTestSupport.gl(); RenderContext alphaContext = AdapterRenderTestSupport.renderContext(alphaGl); adapter.renderAlphaBlended(alphaContext); assertTrue(alphaGl.calls("glBegin").size() > 0);
  }
}
