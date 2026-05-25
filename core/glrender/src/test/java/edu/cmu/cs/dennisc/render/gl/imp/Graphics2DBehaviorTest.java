package edu.cmu.cs.dennisc.render.gl.imp;

import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class Graphics2DBehaviorTest {

@Test
  public void initializeAndDisposeManageLifecycleAndGlState() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    Graphics2D graphics = createGraphics(gl);

    graphics.initialize(new Dimension(64, 48));

    assertTrue(graphics.isValid());
    assertEquals(64, graphics.getWidth());
    assertEquals(48, graphics.getHeight());
    assertTrue(gl.wasCalledWith("glMatrixMode", com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION));
    assertTrue(gl.wasCalledWith("glMatrixMode", com.jogamp.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW));
    assertTrue(gl.wasCalledWith("glOrtho", 0.0, 63.0, 47.0, 0.0, -1.0, 1.0));

    graphics.dispose();

    assertFalse(graphics.isValid());
    assertEquals(1, gl.calls("glFlush").size());
    assertEquals(2, gl.calls("glPopMatrix").size());
  }

  @Test
  public void colorStateRenderingHintsAndTransformRoundTrip() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    Graphics2D graphics = createGraphics(gl);
    graphics.initialize(new Dimension(32, 24));

    Font font = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    BasicStroke stroke = new BasicStroke(3.5f);
    graphics.setFont(font);
    graphics.setStroke(stroke);
    graphics.setBackground(Color.YELLOW);
    graphics.setColor(new Color(12, 34, 56, 128));
    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    graphics.translate(5, 7);
    graphics.scale(2.0, 3.0);
    graphics.rotate(Math.PI / 6.0);

    assertEquals(font, graphics.getFont());
    assertEquals(stroke, graphics.getStroke());
    assertEquals(Color.YELLOW, graphics.getBackground());
    assertEquals(new Color(12, 34, 56, 128), graphics.getColor());
    assertEquals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON, graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
    assertEquals(RenderingHints.VALUE_FRACTIONALMETRICS_ON, graphics.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
    assertTrue(graphics.getFontRenderContext().isAntiAliased());
    assertTrue(graphics.getFontRenderContext().usesFractionalMetrics());
    assertNotEquals(new AffineTransform(), graphics.getTransform());
    assertTrue(gl.wasCalledWith("glEnable", GL.GL_BLEND));
    assertTrue(gl.calls("glLoadMatrixd").size() >= 3);
  }

  @Test
  public void nonColorPaintMakesGetColorFailAndColorPaintCanBeRestored() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    Graphics2D graphics = createGraphics(gl);
    graphics.initialize(new Dimension(16, 16));

    setPaintField(graphics, new GradientPaint(0, 0, Color.RED, 1, 1, Color.BLUE));
    RuntimeException thrown = assertThrows(RuntimeException.class, graphics::getColor);
    assertEquals("use getPaint()", thrown.getMessage());

    graphics.setPaint(Color.CYAN);
    assertEquals(Color.CYAN, graphics.getColor());
    assertEquals(Color.CYAN, graphics.getPaint());
  }

  @Test
  public void unsupportedOperationsThrowExpectedMessages() {
    Graphics2D graphics = createGraphics(new HeadlessRecordingGL2());
    graphics.initialize(new Dimension(8, 8));
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    assertNotImplemented(() -> graphics.create(), "not implemented");
    assertNotImplemented(graphics::setPaintMode, "not implemented");
    assertNotImplemented(() -> graphics.setXORMode(Color.BLACK), "not implemented");
    assertNotImplemented(graphics::getClipBounds, "not implemented");
    assertNotImplemented(() -> graphics.clipRect(0, 0, 1, 1), "not implemented");
    assertNotImplemented(() -> graphics.setClip(0, 0, 1, 1), "not implemented");
    assertNotImplemented(graphics::getClip, "not implemented");
    assertNotImplemented(() -> graphics.setClip((java.awt.Shape) null), "not implemented");
    assertNotImplemented(() -> graphics.copyArea(0, 0, 1, 1, 1, 1), "not implemented");
    assertNotImplemented(() -> graphics.drawArc(0, 0, 1, 1, 0, 90), "not implemented");
    assertNotImplemented(() -> graphics.fillArc(0, 0, 1, 1, 0, 90), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, 0, 0, 1, 1, null), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, 0, 0, Color.BLACK, null), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, 0, 0, 1, 1, Color.BLACK, null), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, 0, 0, 1, 1, 0, 0, 1, 1, null), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, 0, 0, 1, 1, 0, 0, 1, 1, Color.BLACK, null), "not implemented");
    assertNotImplemented(() -> graphics.draw3DRect(0, 0, 1, 1, true), "not implemented");
    assertNotImplemented(() -> graphics.fill3DRect(0, 0, 1, 1, true), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, new AffineTransform(), null), "not implemented");
    assertNotImplemented(() -> graphics.drawImage(image, null, 0, 0), "not implemented");
    assertNotImplemented(() -> graphics.drawRenderedImage(image, new AffineTransform()), "not implemented");
    assertNotImplemented(() -> graphics.drawRenderableImage(null, new AffineTransform()), "not implemented");
    assertNotImplemented(() -> graphics.drawString((java.text.AttributedCharacterIterator) null, 1.0f, 2.0f), "todo: use drawString( String, float, float ) for now");
    assertNotImplemented(() -> graphics.hit(new java.awt.Rectangle(), new Rectangle2D.Double(), false), "not implemented");
    assertNotImplemented(graphics::getDeviceConfiguration, "not implemented");
    assertNotImplemented(graphics::getComposite, "not implemented");
    assertNotImplemented(() -> graphics.setComposite(java.awt.AlphaComposite.Src), "not implemented");
    assertNotImplemented(() -> graphics.clip(new Rectangle2D.Double()), "not implemented");
  }

  private static Graphics2D createGraphics(HeadlessRecordingGL2 gl) {
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(gl);
    return new Graphics2D(renderContext);
  }

  private static void setPaintField(Graphics2D graphics, java.awt.Paint paint) {
    try {
      java.lang.reflect.Field field = Graphics2D.class.getDeclaredField("paint");
      field.setAccessible(true);
      field.set(graphics, paint);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static void assertNotImplemented(Runnable action, String message) {
    RuntimeException thrown = assertThrows(RuntimeException.class, action::run);
    assertEquals(message, thrown.getMessage());
  }
}
