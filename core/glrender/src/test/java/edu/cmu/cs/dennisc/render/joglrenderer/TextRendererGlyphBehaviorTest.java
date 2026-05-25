package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.opengl.util.packrect.Rect;
import org.junit.Test;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class TextRendererGlyphBehaviorTest {

  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

  @Test
  public void stringGlyphWithoutAdvanceReturnsZeroAndDelegatesToRenderer() {
    RecordingRenderer renderer = HeadlessTextRendererFactory.createRenderer(RecordingRenderer.class, FONT);
    TextRendererGlyph glyph = new TextRendererGlyph("sample", false, renderer);

    float advance = glyph.draw3D(1.0f, 2.0f, 3.0f, 4.0f);

    assertEquals(0.0f, advance, 0.0001f);
    assertEquals("sample", renderer.lastString);
    assertEquals(1.0f, renderer.lastX, 0.0001f);
    assertEquals(4.0f, renderer.lastScale, 0.0001f);
  }

  @Test
  public void stringGlyphWithAdvanceSumsUnderlyingGlyphAdvances() {
    RecordingRenderer renderer = HeadlessTextRendererFactory.createRenderer(RecordingRenderer.class, FONT);
    TextRendererGlyph glyph = new TextRendererGlyph("wide", true, renderer);
    GlyphVector glyphVector = FONT.createGlyphVector(renderer.getFontRenderContext(), "wide");
    float expected = 0.0f;
    for (int i = 0; i < glyphVector.getNumGlyphs(); i++) {
      expected += glyphVector.getGlyphMetrics(i).getAdvance();
    }

    float actual = glyph.draw3D(0.0f, 0.0f, 0.0f, 1.0f);

    assertEquals(expected, actual, 0.0001f);
  }

  @Test
  public void clearDropsCachedTextureRectReference() throws Exception {
    RecordingRenderer renderer = HeadlessTextRendererFactory.createRenderer(RecordingRenderer.class, FONT);
    TextRendererGlyph glyph = new TextRendererGlyph(65, 1, 2.0f, null, renderer.mGlyphProducer, renderer);
    java.lang.reflect.Field rectField = TextRendererGlyph.class.getDeclaredField("glyphRectForTextureMapping");
    rectField.setAccessible(true);
    rectField.set(glyph, new Rect(0, 0, 4, 4, new TextData(null, new java.awt.Point(), new Rectangle2D.Double(0, 0, 1, 1), 65)));

    glyph.clear();

    assertNull(rectField.get(glyph));
  }

  private static final class RecordingRenderer extends NonCachingTextRenderer {
    private String lastString;
    private float lastX;
    private float lastScale;

    private RecordingRenderer() {
      super(FONT);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
      BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = image.createGraphics();
      try {
        return g.getFontRenderContext();
      } finally {
        g.dispose();
      }
    }

    @Override
    void draw3D_ROBUST(CharSequence str, float x, float y, float z, float scaleFactor) {
      this.lastString = str.toString();
      this.lastX = x;
      this.lastScale = scaleFactor;
    }
  }
}
