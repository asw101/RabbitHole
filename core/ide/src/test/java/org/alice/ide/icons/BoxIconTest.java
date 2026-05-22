package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class BoxIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_rectangularSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(64, 48));
    assertEquals(64, icon.getIconWidth());
    assertEquals(48, icon.getIconHeight());
  }

  @Test
  public void instanceOf_shapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(16, 16));
    assertTrue(icon instanceof ShapeIcon);
  }

  @Test
  public void constructor_largeSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(256, 256));
    assertEquals(256, icon.getIconWidth());
  }
}
