package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class TypeIconTest {

  @Test
  public void constructor_withJavaType_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class));
    assertNotNull(icon);
  }

  @Test
  public void getIconWidth_positive() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class));
    assertTrue(icon.getIconWidth() > 0);
  }

  @Test
  public void getIconHeight_positive() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class));
    assertTrue(icon.getIconHeight() > 0);
  }

  @Test
  public void getInstance_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = TypeIcon.getInstance(JavaType.getInstance(Integer.class));
    assertNotNull(icon);
  }

  @Test
  public void paintIcon_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class));
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    BufferedImage img = new BufferedImage(Math.max(w, 1), Math.max(h, 1), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    try {
      icon.paintIcon(new javax.swing.JLabel(), g2, 0, 0);
    } finally {
      g2.dispose();
    }
  }

  @Test
  public void fourArgConstructor_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Font font = new Font("SansSerif", Font.PLAIN, 12);
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class), false, font, null);
    assertNotNull(icon);
    assertTrue(icon.getIconWidth() > 0);
  }
}
