package edu.cmu.cs.dennisc.javax.swing.icons;

import org.junit.Test;

import javax.swing.JToggleButton;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class ArrowIconDeepTest {

  @Test
  public void sizeAccessorsReflectConfiguration() {
    ArrowIcon icon = new ArrowIcon(8, true);
    assertEquals(8, icon.getIconWidth());
    assertEquals(8, icon.getIconHeight());

    icon.setSize(12);
    assertEquals(12, icon.getIconWidth());
    assertEquals(12, icon.getIconHeight());
  }

  @Test
  public void paintIconDrawsArrowForNormalAndSelectedStates() {
    UIManager.put("ComboBox.buttonArrowColor", Color.BLACK);
    UIManager.put("ComboBox.buttonPressedArrowColor", Color.BLUE);
    UIManager.put("ComboBox.buttonHoverArrowColor", Color.GREEN);
    UIManager.put("ComboBox.buttonDisabledArrowColor", Color.GRAY);

    ArrowIcon icon = new ArrowIcon(10, true);
    JToggleButton button = new JToggleButton();
    button.setSize(20, 20);

    BufferedImage normal = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g1 = normal.createGraphics();
    try {
      icon.paintIcon(button, g1, 5, 5);
    } finally {
      g1.dispose();
    }

    button.setSelected(true);
    BufferedImage selected = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = selected.createGraphics();
    try {
      icon.paintIcon(button, g2, 5, 5);
    } finally {
      g2.dispose();
    }

    assertTrue(hasNonTransparentPixel(normal));
    assertTrue(hasNonTransparentPixel(selected));
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
}
