package org.alice.ide.icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

final class SceneIconTestSupport {
  private SceneIconTestSupport() {
  }

  static void seedCachedImage(SceneIcon icon) {
    seedCachedImage(icon, new Color(0x3366cc));
  }

  static void seedCachedImage(SceneIcon icon, Color color) {
    BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      graphics.setColor(color);
      graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    } finally {
      graphics.dispose();
    }
    writeField(icon, "image", image);
    writeField(icon, "isDirty", Boolean.FALSE);
  }

  static Object readField(SceneIcon icon, String fieldName) {
    try {
      Field field = SceneIcon.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(icon);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }

  private static void writeField(SceneIcon icon, String fieldName, Object value) {
    try {
      Field field = SceneIcon.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(icon, value);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError(roe);
    }
  }
}
