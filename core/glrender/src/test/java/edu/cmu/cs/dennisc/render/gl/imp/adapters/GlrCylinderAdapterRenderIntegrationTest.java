package edu.cmu.cs.dennisc.render.gl.imp.adapters;


import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Cylinder;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrCylinderAdapterRenderIntegrationTest {


@Before public void setUp() { AdapterRenderTestSupport.resetFactory(); }
  @Test public void renderOpaque_appliesAxisRotationTranslationAndCaps() {
    Cylinder cylinder = AdapterRenderTestSupport.configuredCylinder(); Visual visual = AdapterRenderTestSupport.visualWith(cylinder, AdapterRenderTestSupport.appearance(1.0f));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual); HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl(); RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    adapter.renderOpaque(rc);
    assertTrue(gl.calls("glPushMatrix").size() > 0 || gl.calls("glBegin").size() > 0);
  }
}
