package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.opengl.GLExtensions;
import com.jogamp.opengl.util.packrect.Rect;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class NonCachingTextRendererBehaviorTest {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 24);

  @Test
  public void normalizeExpandsUsingFontDependentBoundary() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);

    Rectangle2D normalized = renderer.normalize(new Rectangle2D.Double(2.4, 3.6, 4.2, 5.1));

    assertEquals(1.0, normalized.getX(), 0.0);
    assertEquals(2.0, normalized.getY(), 0.0);
    assertEquals(7.0, normalized.getWidth(), 0.0);
    assertEquals(8.0, normalized.getHeight(), 0.0);
  }

  @Test
  public void getGraphics2DCachesInstanceAndConfiguresFontAndHints() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);

    Graphics2D first = renderer.getGraphics2D();
    Graphics2D second = renderer.getGraphics2D();

    assertSame(first, second);
    assertEquals(FONT, first.getFont());
    assertEquals(Color.WHITE, first.getColor());
    assertEquals(AlphaComposite.Src, first.getComposite());
    assertEquals(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, first.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
    assertEquals(RenderingHints.VALUE_FRACTIONALMETRICS_OFF, first.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS));
  }

  @Test
  public void getBoundsUsesDelegateAndNormalizeWhenCacheIsEmpty() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);

    Rectangle2D expected = renderer.normalize(renderer.renderDelegate.getBounds("Hello", renderer.font, renderer.getFontRenderContext()));
    Rectangle2D actual = renderer.getBounds("Hello");

    assertEquals(expected.getX(), actual.getX(), 0.0001);
    assertEquals(expected.getY(), actual.getY(), 0.0001);
    assertEquals(expected.getWidth(), actual.getWidth(), 0.0001);
    assertEquals(expected.getHeight(), actual.getHeight(), 0.0001);
  }

  @Test
  public void clearUnusedEntriesRetainsUsedRectsAndPurgesUnusedStringsAndGlyphs() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    Rect used = new Rect(0, 0, 8, 8, new TextData("used", new Point(1, 1), new Rectangle2D.Double(-1, -1, 6, 6), -1));
    Rect dead = new Rect(10, 0, 8, 8, new TextData("dead", new Point(1, 1), new Rectangle2D.Double(-1, -1, 6, 6), 65));
    ((TextData) used.getUserData()).markUsed();
    renderer.packer.add(used);
    renderer.packer.add(dead);
    renderer.stringLocations.put("used", used);
    renderer.stringLocations.put("dead", dead);
    renderer.mGlyphProducer.register(new TextRendererGlyph(65, 2, 3.0f, null, renderer.mGlyphProducer, renderer));

    renderer.clearUnusedEntries();

    assertTrue(renderer.stringLocations.containsKey("used"));
    assertFalse(((TextData) used.getUserData()).used());
    assertFalse(renderer.stringLocations.containsKey("dead"));
    assertEquals(TextRendererGlyphProducer.undefined, renderer.mGlyphProducer.unicodes2Glyphs[65]);
  }

  @Test
  public void is15AvailableCachesFirstExtensionLookup() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withExtension(GLExtensions.VERSION_1_5, true);

    assertTrue(renderer.is15Available(gl));
    gl.withExtension(GLExtensions.VERSION_1_5, false);
    assertTrue(renderer.is15Available(gl));
  }
}
