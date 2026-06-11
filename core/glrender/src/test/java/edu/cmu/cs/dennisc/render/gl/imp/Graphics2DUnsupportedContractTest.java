package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

public class Graphics2DUnsupportedContractTest {
  private static final String NOT_IMPLEMENTED = "not implemented";
  private static final String ATTRIBUTED_TEXT_MESSAGE = "todo: use drawString( String, float, float ) for now";

  @Test
  public void intentionallyUnsupportedGraphicsMethodsKeepRuntimeExceptionContract() {
    Graphics2D graphics = createGraphics();

    assertRuntimeExceptionValue(() -> graphics.create(), NOT_IMPLEMENTED);
    assertRuntimeException(graphics::setPaintMode, NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.setXORMode(Color.BLACK), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(graphics::getClipBounds, NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.clipRect(0, 0, 1, 1), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.setClip(0, 0, 1, 1), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(graphics::getClip, NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.setClip((Shape) null), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.copyArea(0, 0, 1, 1, 1, 1), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.drawArc(0, 0, 1, 1, 0, 90), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.fillArc(0, 0, 1, 1, 0, 90), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.draw3DRect(0, 0, 1, 1, true), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.fill3DRect(0, 0, 1, 1, true), NOT_IMPLEMENTED);
  }

  @Test
  public void intentionallyUnsupportedImageMethodsKeepRuntimeExceptionContract() {
    Graphics2D graphics = createGraphics();
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    assertRuntimeExceptionValue(() -> graphics.drawImage(image, 0, 0, 1, 1, null), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(() -> graphics.drawImage(image, 0, 0, Color.BLACK, null), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(() -> graphics.drawImage(image, 0, 0, 1, 1, Color.BLACK, null), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(() -> graphics.drawImage(image, 0, 0, 1, 1, 0, 0, 1, 1, null), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(() -> graphics.drawImage(image, 0, 0, 1, 1, 0, 0, 1, 1, Color.BLACK, null), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(() -> graphics.drawImage(image, new AffineTransform(), null), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.drawImage(image, null, 0, 0), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.drawRenderedImage(image, new AffineTransform()), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.drawRenderableImage(null, new AffineTransform()), NOT_IMPLEMENTED);
  }

  @Test
  public void intentionallyUnsupportedGraphics2DMethodsKeepRuntimeExceptionContract() {
    Graphics2D graphics = createGraphics();

    assertRuntimeException(() -> graphics.drawString((AttributedCharacterIterator) null, 1, 2), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.drawString((AttributedCharacterIterator) null, 1.0f, 2.0f), ATTRIBUTED_TEXT_MESSAGE);
    assertRuntimeExceptionValue(() -> graphics.hit(new Rectangle(), new Rectangle2D.Double(), false), NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(graphics::getDeviceConfiguration, NOT_IMPLEMENTED);
    assertRuntimeExceptionValue(graphics::getComposite, NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.setComposite(AlphaComposite.Src), NOT_IMPLEMENTED);
    assertRuntimeException(() -> graphics.clip(new Rectangle2D.Double()), NOT_IMPLEMENTED);
  }

  @Test
  public void nonColorPaintFailuresKeepCompatibilityMessages() {
    Graphics2D graphics = createGraphics();
    Paint gradient = new GradientPaint(0, 0, Color.RED, 1, 1, Color.BLUE);

    assertRuntimeException(() -> graphics.setPaint(gradient), NOT_IMPLEMENTED);
    setPaintField(graphics, gradient);
    assertRuntimeExceptionValue(graphics::getColor, "use getPaint()");
  }

  private static Graphics2D createGraphics() {
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(new HeadlessRecordingGL2());
    Graphics2D graphics = new Graphics2D(renderContext);
    graphics.initialize(new java.awt.Dimension(8, 8));
    return graphics;
  }

  private static void setPaintField(Graphics2D graphics, Paint paint) {
    try {
      java.lang.reflect.Field field = Graphics2D.class.getDeclaredField("paint");
      field.setAccessible(true);
      field.set(graphics, paint);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static <T> void assertRuntimeExceptionValue(ThrowingSupplier<T> action, String message) {
    RuntimeException thrown = assertThrows(RuntimeException.class, action::get);
    assertSame(RuntimeException.class, thrown.getClass());
    assertEquals(message, thrown.getMessage());
  }

  private static void assertRuntimeException(ThrowingRunnable action, String message) {
    RuntimeException thrown = assertThrows(RuntimeException.class, action::run);
    assertSame(RuntimeException.class, thrown.getClass());
    assertEquals(message, thrown.getMessage());
  }

  private interface ThrowingRunnable {
    void run();
  }

  private interface ThrowingSupplier<T> {
    T get();
  }
}
