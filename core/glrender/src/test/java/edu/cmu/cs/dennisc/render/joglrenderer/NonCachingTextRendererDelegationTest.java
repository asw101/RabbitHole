package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class NonCachingTextRendererDelegationTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

  @Test
  public void wrapperMethodsDelegateToPipelineAndProperties() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    SpyPipeline pipeline = new SpyPipeline(renderer);
    SpyProperties properties = new SpyProperties(renderer);
    set(renderer, "pipeline", pipeline);
    set(renderer, "properties", properties);

    renderer.beginRendering(12, 8);
    assertTrue(pipeline.beginCalled);
    assertTrue(pipeline.beginOrtho);
    assertTrue(pipeline.disableDepthTest);
    assertEquals(12, pipeline.width);
    assertEquals(8, pipeline.height);

    renderer.beginRendering(7, 6, false);
    assertFalse(pipeline.disableDepthTest);
    renderer.begin3DRendering();
    assertFalse(pipeline.beginOrtho);

    renderer.draw("alpha", 1, 2);
    renderer.draw((CharSequence) new StringBuilder("beta"), 3, 4);
    renderer.draw3D("gamma", 5.0f, 6.0f, 7.0f, 1.5f);
    renderer.draw3D((CharSequence) new StringBuilder("delta"), 8.0f, 9.0f, 10.0f, 2.0f);
    assertEquals("delta", pipeline.lastInternalText.toString());
    assertEquals(4, pipeline.internalCalls);

    renderer.setColor(Color.RED);
    renderer.setColor(0.1f, 0.2f, 0.3f, 0.4f);
    assertEquals(Color.RED, properties.lastColor);
    assertArrayEquals(new float[]{0.1f, 0.2f, 0.3f, 0.4f}, properties.lastRgba, 0.0001f);

    assertSame(properties.stringBounds, renderer.getBounds("s"));
    assertSame(properties.sequenceBounds, renderer.getBounds((CharSequence) new StringBuilder("t")));
    assertEquals(7.5f, renderer.getCharWidth('x'), 0.0001f);

    renderer.flush();
    renderer.endRendering();
    renderer.end3DRendering();
    renderer.dispose();
    assertEquals(1, pipeline.flushCalls);
    assertTrue(pipeline.endCalled);
    assertFalse(pipeline.endOrtho);
    assertTrue(properties.disposeCalled);
  }

  @Test
  public void fontAndStateWrappersUseUnderlyingProperties() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);

    assertEquals(FONT, renderer.getFont());
    renderer.setUseVertexArrays(false);
    renderer.setSmoothing(false);

    assertFalse(renderer.getMyUseVertexArrays());
    assertFalse(renderer.getSmoothing());
    assertNotNull(renderer.getFontRenderContext());
    assertSame(renderer.getFontRenderContext(), renderer.getFontRenderContext());

    Rectangle2D normalized = NonCachingTextRenderer.preNormalize(new Rectangle2D.Double(2.2, 3.4, 4.1, 5.2));
    assertEquals(1.0, normalized.getX(), 0.0);
    assertEquals(2.0, normalized.getY(), 0.0);
    assertEquals(7.0, normalized.getWidth(), 0.0);
    assertEquals(8.0, normalized.getHeight(), 0.0);
  }

  private static void set(NonCachingTextRenderer renderer, String name, Object value) {
    try {
      Field field = NonCachingTextRenderer.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(renderer, value);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static final class SpyPipeline extends TextRendererPipeline {
    private boolean beginCalled;
    private boolean beginOrtho;
    private int width;
    private int height;
    private boolean disableDepthTest;
    private boolean endCalled;
    private boolean endOrtho;
    private int internalCalls;
    private CharSequence lastInternalText;
    private int flushCalls;

    private SpyPipeline(NonCachingTextRenderer renderer) {
      super(renderer);
    }

    @Override
    void beginRendering(boolean ortho, int width, int height, boolean disableDepthTestForOrtho) {
      this.beginCalled = true;
      this.beginOrtho = ortho;
      this.width = width;
      this.height = height;
      this.disableDepthTest = disableDepthTestForOrtho;
    }

    @Override
    void endRendering(boolean ortho) {
      this.endCalled = true;
      this.endOrtho = ortho;
    }

    @Override
    void internal_draw3D(CharSequence str, float x, float y, float z, float scaleFactor) {
      this.internalCalls++;
      this.lastInternalText = str;
    }

    @Override
    void flushGlyphPipeline() {
      this.flushCalls++;
    }
  }

  private static final class SpyProperties extends TextRendererProperties {
    private final Rectangle2D stringBounds = new Rectangle2D.Double(1, 2, 3, 4);
    private final Rectangle2D sequenceBounds = new Rectangle2D.Double(5, 6, 7, 8);
    private Color lastColor;
    private float[] lastRgba;
    private boolean disposeCalled;

    private SpyProperties(NonCachingTextRenderer renderer) {
      super(renderer);
    }

    @Override
    public Rectangle2D getBounds(String str) {
      return stringBounds;
    }

    @Override
    public Rectangle2D getBounds(CharSequence str) {
      return sequenceBounds;
    }

    @Override
    public float getCharWidth(char inChar) {
      return 7.5f;
    }

    @Override
    public void setColor(Color color) {
      this.lastColor = color;
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
      this.lastRgba = new float[]{r, g, b, a};
    }

    @Override
    public void dispose() {
      this.disposeCalled = true;
    }
  }
}
