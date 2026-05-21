package edu.cmu.cs.dennisc.javax.swing.plaf;

import org.junit.Test;

import javax.swing.JButton;
import javax.swing.plaf.ComponentUI;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class HyperlinkUIDeepTest {

  @Test
  public void createUIReturnsHyperlinkUI() {
    ComponentUI ui = HyperlinkUI.createUI(new JButton("link"));
    assertTrue(ui instanceof HyperlinkUI);
  }

  @Test
  public void settersAndGettersRoundTrip() {
    HyperlinkUI ui = new HyperlinkUI();
    ui.setDisabledColor(Color.MAGENTA);
    ui.setUnderlinedWhenDisabled(false);
    ui.setUnderlinedOnlyWhenRolledOver(false);

    assertEquals(Color.MAGENTA, ui.getDisabledColor());
    assertFalse(ui.isUnderlinedWhenDisabled());
    assertFalse(ui.isUnderlinedOnlyWhenRolledOver());
  }

  @Test
  public void paintTextUsesDisabledColor() {
    ExposedHyperlinkUI ui = new ExposedHyperlinkUI();
    ui.setDisabledColor(Color.RED);
    ui.setUnderlinedWhenDisabled(true);
    ui.setUnderlinedOnlyWhenRolledOver(false);

    JButton button = new JButton("link");
    button.setEnabled(false);
    button.setBackground(Color.WHITE);
    button.setForeground(Color.BLACK);

    BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g = image.createGraphics();
    try {
      ui.paintTextPublic(g, button, new Rectangle(5, 5, 40, 15), "link");
    } finally {
      g.dispose();
    }

    assertTrue(hasColor(image, Color.RED));
  }

  @Test
  public void paintTextHandlesRolloverAndUnderline() {
    ExposedHyperlinkUI ui = new ExposedHyperlinkUI();
    ui.setUnderlinedOnlyWhenRolledOver(true);

    JButton button = new JButton("link");
    button.getModel().setRollover(true);
    button.setBackground(Color.WHITE);
    button.setForeground(Color.BLACK);

    BufferedImage image = new BufferedImage(80, 30, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g = image.createGraphics();
    try {
      ui.paintTextPublic(g, button, new Rectangle(5, 5, 40, 15), "link");
    } finally {
      g.dispose();
    }

    assertTrue(hasNonTransparentPixel(image));
  }

  private static boolean hasColor(BufferedImage image, Color color) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) == color.getRGB()) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean hasNonTransparentPixel(BufferedImage image) {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if ((image.getRGB(x, y) >>> 24) != 0) {
          return true;
        }
      }
    }
    return false;
  }

  private static final class ExposedHyperlinkUI extends HyperlinkUI {
    private void paintTextPublic(java.awt.Graphics g, javax.swing.AbstractButton b, Rectangle textRect, String text) {
      super.paintText(g, b, textRect, text);
    }
  }
}
