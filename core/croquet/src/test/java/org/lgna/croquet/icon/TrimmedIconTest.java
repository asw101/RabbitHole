package org.lgna.croquet.icon;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

/**
 * Tests for {@link TrimmedIcon} and its superclass {@link AbstractIcon}.
 * Exercises icon dimensions, painting with scaling, same-size painting,
 * and null-icon handling.
 */
public class TrimmedIconTest {

  // ── AbstractIcon dimensions ───────────────────────────────────────

  @Test
  public void getIconWidth_matchesConstructor() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(32, 32), new Dimension(48, 48));
    assertEquals(48, icon.getIconWidth());
  }

  @Test
  public void getIconHeight_matchesConstructor() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(32, 32), new Dimension(48, 36));
    assertEquals(36, icon.getIconHeight());
  }

  // ── getImageIcon ──────────────────────────────────────────────────

  @Test
  public void getImageIcon_returnsWrappedIcon() {
    Icon inner = stubIcon(16, 16);
    TrimmedIcon icon = new TrimmedIcon(inner, new Dimension(24, 24));
    assertSame(inner, icon.getImageIcon());
  }

  @Test
  public void getImageIcon_null_returnsNull() {
    TrimmedIcon icon = new TrimmedIcon(null, new Dimension(24, 24));
    assertNull(icon.getImageIcon());
  }

  // ── paintIcon with scaling (width-limited) ────────────────────────

  @Test
  public void paintIcon_scaledDown_noException() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(100, 50), new Dimension(50, 50));
    paintOnBufferedImage(icon);
  }

  // ── paintIcon with scaling (height-limited) ───────────────────────

  @Test
  public void paintIcon_heightLimited_noException() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(50, 100), new Dimension(50, 50));
    paintOnBufferedImage(icon);
  }

  // ── paintIcon same size ───────────────────────────────────────────

  @Test
  public void paintIcon_sameSize_noException() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(32, 32), new Dimension(32, 32));
    paintOnBufferedImage(icon);
  }

  // ── paintIcon with null inner icon ────────────────────────────────

  @Test
  public void paintIcon_nullInner_noException() {
    TrimmedIcon icon = new TrimmedIcon(null, new Dimension(32, 32));
    paintOnBufferedImage(icon);
  }

  // ── paintIcon via AbstractIcon.paintIcon (public) ─────────────────

  @Test
  public void paintIcon_publicMethod_restoresState() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(20, 20), new Dimension(40, 40));
    BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    Paint origPaint = g2.getPaint();
    icon.paintIcon(null, g2, 10, 10);
    // Paint should be restored
    assertEquals(origPaint, g2.getPaint());
    g2.dispose();
  }

  @Test
  public void paintIcon_publicMethod_atOrigin() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(16, 16), new Dimension(16, 16));
    BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    icon.paintIcon(null, g2, 0, 0);
    g2.dispose();
  }

  // ── paintIcon with wider aspect ratio ─────────────────────────────

  @Test
  public void paintIcon_widerThanTall_scales() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(200, 100), new Dimension(100, 100));
    paintOnBufferedImage(icon);
  }

  // ── paintIcon with taller aspect ratio ────────────────────────────

  @Test
  public void paintIcon_tallerThanWide_scales() {
    TrimmedIcon icon = new TrimmedIcon(stubIcon(100, 200), new Dimension(100, 100));
    paintOnBufferedImage(icon);
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static void paintOnBufferedImage(TrimmedIcon icon) {
    BufferedImage img = new BufferedImage(
        Math.max(icon.getIconWidth(), 1),
        Math.max(icon.getIconHeight(), 1),
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    icon.paintIcon(null, g2, 0, 0);
    g2.dispose();
  }

  private static Icon stubIcon(int w, int h) {
    return new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        g.fillRect(x, y, w, h);
      }

      @Override
      public int getIconWidth() { return w; }

      @Override
      public int getIconHeight() { return h; }
    };
  }
}
