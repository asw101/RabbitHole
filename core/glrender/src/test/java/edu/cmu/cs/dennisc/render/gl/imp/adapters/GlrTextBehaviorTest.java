package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Text;
import org.junit.Before;
import org.junit.Test;

import java.awt.Font;

import static org.junit.Assert.*;

public class GlrTextBehaviorTest {
  @Before
  public void setUp() {
    AdapterRenderTestSupport.resetFactory();
  }

  @Test
  public void render_and_pick_emit_tessellatedGeometryAndPickNames() {
    Text text = new Text();
    text.text.setValue("Alice");
    text.font.setValue(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    text.depth.setValue(0.5);
    GlrText adapter = (GlrText) AdapterFactory.getAdapterFor(text);
    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);

    adapter.render(rc, GlrVisual.RenderType.OPAQUE);
    adapter.render(rc, GlrVisual.RenderType.OPAQUE);

    PickContext pickContext = new PickContext(false);
    pickContext.gl = gl;
    adapter.pickGeometry(pickContext, true);
    adapter.pickGeometry(pickContext, false);

    assertTrue(gl.calls("glBegin").size() > 0);
    assertTrue(gl.calls("glVertex3d").size() > 0);
    assertTrue(gl.calls("glPushName").size() >= 2);
    assertTrue(gl.wasCalledWith("glPushName", 0));
    assertTrue(gl.wasCalledWith("glPushName", -1));
    assertTrue(gl.calls("glNewList").size() > 0);
    assertTrue(gl.calls("glCallList").size() > 0 || gl.calls("glNewList").size() > 1);
  }

  @Test
  public void propertyChanges_markGeometryRefreshNeeded() {
    Text text = new Text();
    GlrText adapter = (GlrText) AdapterFactory.getAdapterFor(text);
    text.text.setValue("Hello");
    adapter.propertyChanged(text.text);
    text.font.setValue(new Font(Font.MONOSPACED, Font.PLAIN, 18));
    adapter.propertyChanged(text.font);
    text.depth.setValue(1.0);
    adapter.propertyChanged(text.depth);
    adapter.propertyChanged(text.leftToRightAlignment);
    adapter.propertyChanged(text.topToBottomAlignment);
    adapter.propertyChanged(text.frontToBackAlignment);

    HeadlessRecordingGL2 gl = AdapterRenderTestSupport.gl();
    RenderContext rc = AdapterRenderTestSupport.renderContext(gl);
    adapter.render(rc, GlrVisual.RenderType.OPAQUE);

    assertTrue(gl.calls("glNewList").size() > 0);
    assertTrue(gl.calls("glVertex3d").size() > 0);
  }
}
