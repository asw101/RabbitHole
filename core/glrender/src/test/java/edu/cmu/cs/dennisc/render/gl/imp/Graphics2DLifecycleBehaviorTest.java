package edu.cmu.cs.dennisc.render.gl.imp;



import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;

import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;
import static org.junit.Assert.*;

public class Graphics2DLifecycleBehaviorTest {



@Test public void initializeAndDispose_manageMatricesAndValidity() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); RenderContext rc = new RenderContext(); rc.setGL(gl); Graphics2D graphics = new Graphics2D(rc);
    graphics.initialize(new Dimension(20, 10)); graphics.setColor(Color.BLUE); graphics.clearRect(1, 1, 4, 3);
    assertTrue(graphics.isValid()); assertTrue(gl.wasCalledWith("glMatrixMode", GL_PROJECTION)); assertTrue(gl.wasCalledWith("glMatrixMode", GL_MODELVIEW));
    graphics.dispose();
    assertFalse(graphics.isValid()); assertTrue(gl.calls("glPopMatrix").size() >= 2); assertTrue(gl.calls("glFlush").size() >= 1);
  }
}
