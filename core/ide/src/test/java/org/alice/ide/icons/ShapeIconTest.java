package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class ShapeIconTest {
  @Test
  public void padConstant_isTwo() {
    assertEquals(2, ShapeIcon.PAD);
  }

  @Test
  public void fillPaint_isLavender() {
    assertEquals(new Color(191, 191, 255), ShapeIcon.FILL_PAINT);
  }

  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ShapeIcon icon = new BoxIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ShapeIcon icon = new SphereIcon(new Dimension(64, 48));
    assertEquals(64, icon.getIconWidth());
    assertEquals(48, icon.getIconHeight());
  }

  @Test
  public void constructor_minimumSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ShapeIcon icon = new BoxIcon(new Dimension(1, 1));
    assertEquals(1, icon.getIconWidth());
    assertEquals(1, icon.getIconHeight());
  }

  @Test
  public void fillPaint_isNotNull() {
    assertNotNull(ShapeIcon.FILL_PAINT);
  }

  @Test
  public void fillPaint_hasExpectedRGB() {
    Color fill = ShapeIcon.FILL_PAINT;
    assertEquals(191, fill.getRed());
    assertEquals(191, fill.getGreen());
    assertEquals(255, fill.getBlue());
  }
}
