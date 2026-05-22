package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import org.junit.Test;

import java.awt.Dimension;

import static com.jogamp.opengl.GL.GL_LINE_LOOP;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_LINE_STRIP;
import static com.jogamp.opengl.GL2.GL_POLYGON;
import static org.junit.Assert.*;

public class Graphics2DPrimitiveRenderBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test public void primitiveDrawingMethods_emitExpectedOpenGlModes() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); RenderContext rc = new RenderContext(); rc.setGL(gl); Graphics2D graphics = new Graphics2D(rc); graphics.initialize(new Dimension(32, 24));
    graphics.drawLine(1, 2, 3, 4); graphics.fillRect(0, 0, 5, 6); graphics.drawOval(0, 0, 8, 8); graphics.drawPolyline(new int[]{0, 1, 2}, new int[]{0, 1, 0}, 3); graphics.fillPolygon(new int[]{0, 4, 0}, new int[]{0, 2, 4}, 3);
    assertTrue(gl.wasCalledWith("glBegin", GL_LINES)); assertTrue(gl.wasCalledWith("glBegin", GL_POLYGON)); assertTrue(gl.wasCalledWith("glBegin", GL_LINE_LOOP)); assertTrue(gl.wasCalledWith("glBegin", GL_LINE_STRIP)); assertTrue(gl.calls("glVertex2i").size() > 0 || gl.calls("glVertex2d").size() > 0);
  }
}
