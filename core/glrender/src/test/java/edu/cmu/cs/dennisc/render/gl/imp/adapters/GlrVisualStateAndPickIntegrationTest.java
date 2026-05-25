package edu.cmu.cs.dennisc.render.gl.imp.adapters;


import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrVisualStateAndPickIntegrationTest {


@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void appearanceAndVisibilityDriveOpaqueAndAlphaState() {
    Visual opaqueVisual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));
    opaqueVisual.isShowing.setValue(true);
    GlrVisual<Visual> opaqueAdapter = AdapterRenderTestSupport.visualAdapter(opaqueVisual);

    assertTrue(opaqueAdapter.isActuallyShowing());
    assertTrue(opaqueAdapter.hasOpaque());
    assertFalse(opaqueAdapter.isAlphaBlended());

    Visual translucentVisual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(0.4f));
    translucentVisual.isShowing.setValue(true);
    GlrVisual<Visual> translucentAdapter = AdapterRenderTestSupport.visualAdapter(translucentVisual);

    assertTrue(translucentAdapter.isAlphaBlended());
    assertTrue(translucentAdapter.isAllAlpha());
    assertFalse(translucentAdapter.hasOpaque());

    translucentVisual.isShowing.setValue(false);
    assertFalse(translucentAdapter.isActuallyShowing());
  }

  @Test
  public void pickRegistersVisualNameWhenRenderable() {
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));
    visual.isShowing.setValue(true);
    visual.isPickable.setValue(true);
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    PickContext pc = AdapterRenderTestSupport.pickContext(gl);

    adapter.pick(pc, AdapterRenderTestSupport.requiredPickParameters());

    assertSame(adapter, pc.getPickVisualAdapterForName(0));
    assertTrue(gl.calls("glPushName").size() >= 3);
    assertTrue(gl.calls("glBegin").size() > 0);
  }
}
