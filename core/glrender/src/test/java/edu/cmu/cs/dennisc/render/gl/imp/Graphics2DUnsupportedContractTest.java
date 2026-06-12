package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.image.ImageGenerator;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy;
import org.junit.Test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void unsupportedExceptionsStartAtGraphics2DCallSite() {
    Graphics2D graphics = createGraphics();

    RuntimeException thrown = assertThrows(RuntimeException.class, graphics::setPaintMode);

    assertEquals(Graphics2D.class.getName(), thrown.getStackTrace()[0].getClassName());
    assertEquals("setPaintMode", thrown.getStackTrace()[0].getMethodName());
  }

  @Test
  public void legacyUnsupportedImageInputsKeepDelegateCompatibilityMessages() {
    Graphics2D graphics = createGraphics();

    RuntimeException drawImageThrown = assertThrows(RuntimeException.class,
        () -> graphics.drawImage(new UnsupportedImage(), 0, 0, null));
    assertSame(RuntimeException.class, drawImageThrown.getClass());
    assertEquals("todo", drawImageThrown.getMessage());
    assertEquals(GlImageRenderer.class.getName(),
        drawImageThrown.getStackTrace()[0].getClassName());

    RuntimeException imageThrown = assertThrows(RuntimeException.class,
        () -> graphics.remember(new UnsupportedImage()));
    assertSame(RuntimeException.class, imageThrown.getClass());
    assertEquals("todo", imageThrown.getMessage());
    assertEquals(GlImageRenderer.class.getName(),
        imageThrown.getStackTrace()[0].getClassName());

    RuntimeException generatorThrown = assertThrows(RuntimeException.class,
        () -> graphics.remember(new UnsupportedImageGenerator()));
    assertSame(RuntimeException.class, generatorThrown.getClass());
    assertEquals("TODO", generatorThrown.getMessage());
    assertEquals(GlImageRenderer.class.getName(),
        generatorThrown.getStackTrace()[0].getClassName());
  }

  @Test
  public void disposeForgottenImageGeneratorsPreservesCrossMapCompatibility() throws Exception {
    Graphics2D graphics = createGraphics();
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    texture.setMipMappingDesired(false);

    graphics.remember(texture);
    graphics.forget(texture);

    Object imageRenderer = getField(graphics, "imageRenderer");
    @SuppressWarnings("unchecked")
    Map<ImageGenerator, ReferencedObject<Pixels>> forgottenImages =
        (Map<ImageGenerator, ReferencedObject<Pixels>>) getField(imageRenderer,
            "forgottenImageGeneratorToPixelsMap");
    assertEquals(1, forgottenImages.size());
    ReferencedObject<Pixels> pixelsRef = forgottenImages.get(texture);

    Object textRenderer = getField(graphics, "textRenderer");
    @SuppressWarnings("unchecked")
    Map<Font, ReferencedObject<Object>> forgottenFonts =
        (Map<Font, ReferencedObject<Object>>) getField(textRenderer,
            "forgottenFontToTextRendererMap");
    forgottenFonts.put(new Font(Font.DIALOG, Font.PLAIN, 12),
        new ReferencedObject<Object>(new Object(), 0));

    graphics.disposeForgottenImageGenerators();

    assertEquals(1, forgottenImages.size());
    assertSame(pixelsRef, forgottenImages.get(texture));
    assertTrue(forgottenFonts.isEmpty());
    assertNull(getField(pixelsRef.getObject(), "m_texture"));
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

  private static Object getField(Object target, String name) throws Exception {
    java.lang.reflect.Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
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

  private static final class UnsupportedImage extends Image {
    @Override
    public int getWidth(ImageObserver observer) { return 1; }

    @Override
    public int getHeight(ImageObserver observer) { return 1; }

    @Override
    public ImageProducer getSource() { return null; }

    @Override
    public Graphics getGraphics() { return null; }

    @Override
    public Object getProperty(String name, ImageObserver observer) { return null; }
  }

  private static final class UnsupportedImageGenerator implements ImageGenerator {
    @Override
    public int getWidth() { return 1; }

    @Override
    public int getHeight() { return 1; }

    @Override
    public boolean isPotentiallyAlphaBlended() { return false; }

    @Override
    public boolean isMipMappingDesired() { return false; }

    @Override
    public MipMapGenerationPolicy getMipMapGenerationPolicy() {
      return MipMapGenerationPolicy.PAINT_ONLY_HIGHEST_LEVEL_THEN_SCALE_REMAINING;
    }

    @Override
    public void paint(java.awt.Graphics2D g, int width, int height) {
    }
  }
}
