package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.awt.Font;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class TextRendererGlyphProducerBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

  @Test
  public void getGlyphsUsesSingleStringFastPathWhenGlyphCacheIsDisabled() throws Exception {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);

    List<TextRendererGlyph> glyphs = renderer.mGlyphProducer.getGlyphs(new StringBuilder("abc"));

    assertEquals(1, glyphs.size());
    Field strField = TextRendererGlyph.class.getDeclaredField("str");
    strField.setAccessible(true);
    assertEquals("abc", strField.get(glyphs.get(0)));
  }

  @Test
  public void registerAndGetGlyphPixelWidthUseCachedGlyphAdvance() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    TextRendererGlyph glyph = new TextRendererGlyph(65, 3, 4.5f, null, renderer.mGlyphProducer, renderer);

    renderer.mGlyphProducer.register(glyph);

    assertEquals(4.5f, renderer.mGlyphProducer.getGlyphPixelWidth('A'), 0.0001f);
    assertSame(glyph, renderer.mGlyphProducer.glyphCache[3]);
    assertEquals(3, renderer.mGlyphProducer.unicodes2Glyphs['A']);
  }

  @Test
  public void clearCacheEntryRemovesRegisteredGlyphAndResetsMapping() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    renderer.mGlyphProducer.register(new TextRendererGlyph(65, 2, 2.0f, null, renderer.mGlyphProducer, renderer));

    renderer.mGlyphProducer.clearCacheEntry('A');

    assertNull(renderer.mGlyphProducer.glyphCache[2]);
    assertEquals(TextRendererGlyphProducer.undefined, renderer.mGlyphProducer.unicodes2Glyphs['A']);
  }

  @Test
  public void clearAllCacheEntriesResetsMultipleRegisteredGlyphs() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    renderer.mGlyphProducer.register(new TextRendererGlyph(65, 1, 1.0f, null, renderer.mGlyphProducer, renderer));
    renderer.mGlyphProducer.register(new TextRendererGlyph(66, 2, 2.0f, null, renderer.mGlyphProducer, renderer));

    renderer.mGlyphProducer.clearAllCacheEntries();

    assertEquals(TextRendererGlyphProducer.undefined, renderer.mGlyphProducer.unicodes2Glyphs['A']);
    assertEquals(TextRendererGlyphProducer.undefined, renderer.mGlyphProducer.unicodes2Glyphs['B']);
    assertNull(renderer.mGlyphProducer.glyphCache[1]);
    assertNull(renderer.mGlyphProducer.glyphCache[2]);
  }
}
