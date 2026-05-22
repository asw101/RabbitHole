package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class RendererUtilityBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void characterCacheFallsBackToBoxingForNonAsciiCharacters() {
    assertSame(CharacterCache.cache['A'], CharacterCache.valueOf('A'));
    assertEquals(Character.valueOf('\u00E9'), CharacterCache.valueOf('\u00E9'));
  }

  @Test
  public void defaultRenderDelegateDrawMethodsMutateTargetImage() {
    DefaultRenderDelegate delegate = new DefaultRenderDelegate();
    Font font = new Font(Font.DIALOG, Font.PLAIN, 18);
    BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.setFont(font);
    try {
      FontRenderContext frc = graphics.getFontRenderContext();
      GlyphVector gv = font.createGlyphVector(frc, "A");

      delegate.draw(graphics, "B", 5, 20);
      delegate.drawGlyphVector(graphics, gv, 5, 30);

      boolean hasInk = false;
      for (int y = 0; y < image.getHeight() && !hasInk; y++) {
        for (int x = 0; x < image.getWidth(); x++) {
          if ((image.getRGB(x, y) >>> 24) != 0) {
            hasInk = true;
            break;
          }
        }
      }
      assertTrue(hasInk);
    } finally {
      graphics.dispose();
    }
  }
}
