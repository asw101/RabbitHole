package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import com.jogamp.opengl.util.packrect.Rect;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class TextRendererPropertiesBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

  @Test
  public void setColorWithColorFlushesOnlyWhenColorChanges() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);

    renderer.properties.setColor(Color.RED);
    renderer.properties.setColor(Color.RED);
    renderer.properties.setColor(Color.BLUE);

    assertEquals(2, renderer.flushCount);
    assertTrue(renderer.properties.haveCachedColor);
    assertEquals(Color.BLUE, renderer.properties.cachedColor);
  }

  @Test
  public void setColorWithFloatsFlushesOnlyWhenComponentsChange() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);

    renderer.properties.setColor(0.1f, 0.2f, 0.3f, 0.4f);
    renderer.properties.setColor(0.1f, 0.2f, 0.3f, 0.4f);
    renderer.properties.setColor(0.3f, 0.2f, 0.1f, 0.4f);

    assertEquals(2, renderer.flushCount);
    assertNull(renderer.properties.cachedColor);
    assertEquals(0.3f, renderer.properties.cachedR, 0.0001f);
  }

  @Test
  public void getBoundsUsesCachedRectWhenPresent() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    Rect rect = new Rect(0, 0, 20, 10, new TextData("cached", new Point(3, 4), new Rectangle2D.Double(-3, -4, 15, 8), -1));
    renderer.stringLocations.put("cached", rect);

    Rectangle2D bounds = renderer.properties.getBounds("cached");

    assertEquals(-3.0, bounds.getX(), 0.0);
    assertEquals(-4.0, bounds.getY(), 0.0);
    assertEquals(20.0, bounds.getWidth(), 0.0);
    assertEquals(10.0, bounds.getHeight(), 0.0);
  }

  @Test
  public void getCharWidthUsesRegisteredGlyphAdvance() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    renderer.mGlyphProducer.register(new TextRendererGlyph(65, 4, 6.5f, null, renderer.mGlyphProducer, renderer));

    assertEquals(6.5f, renderer.properties.getCharWidth('A'), 0.0001f);
  }

  @Test
  public void disposeClearsRendererCaches() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    renderer.getGraphics2D();

    renderer.properties.dispose();

    assertNull(renderer.packer);
    assertNull(renderer.cachedBackingStore);
    assertNull(renderer.cachedGraphics);
    assertNull(renderer.cachedFontRenderContext);
  }

  private static class SpyRenderer extends NonCachingTextRenderer {
    private int flushCount;

    private SpyRenderer() {
      super(FONT);
    }

    @Override
    void flushGlyphPipeline() {
      this.flushCount++;
    }
  }
}
