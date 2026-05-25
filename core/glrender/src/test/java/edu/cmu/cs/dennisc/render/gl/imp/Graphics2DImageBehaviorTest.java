package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class Graphics2DImageBehaviorTest {
@Test public void drawImage_remembersPaintsAndForgetsTransientImage() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); RenderContext rc = new RenderContext(); rc.setGL(gl); Graphics2D graphics = new Graphics2D(rc); graphics.initialize(new Dimension(16, 16)); BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    assertTrue(graphics.drawImage(image, 3, 4, null));
    assertTrue(graphics.isValid());
  }
}
