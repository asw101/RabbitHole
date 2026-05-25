package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.SimpleAppearance;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.Matrix3x3;
import org.junit.Before;
import org.junit.Test;

import static com.jogamp.opengl.GL.GL_BACK;
import static com.jogamp.opengl.GL.GL_FRONT;
import static org.junit.Assert.*;

public class GlrVisualTwoSidedRenderBehaviorTest {

@Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void differentFrontAndBackAppearances_cullEachFaceSeparatelyAndApplyScale() {
    Visual visual = new Visual();
    visual.geometries.setValue(new edu.cmu.cs.dennisc.scenegraph.Geometry[]{new Box()});
    visual.frontFacingAppearance.setValue(new SimpleAppearance());
    visual.backFacingAppearance.setValue(new SimpleAppearance());
    visual.scale.setValue(Matrix3x3.IDENTITY.scale(2.0));
    GlrVisual<Visual> adapter = AdapterRenderTestSupport.visualAdapter(visual);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);

    adapter.renderAlphaBlended(rc);

    assertTrue(gl.wasCalledWith("glCullFace", GL_BACK));
    assertTrue(gl.wasCalledWith("glCullFace", GL_FRONT));
    assertTrue(gl.calls("glMultMatrixd").size() >= 2);
  }
}
