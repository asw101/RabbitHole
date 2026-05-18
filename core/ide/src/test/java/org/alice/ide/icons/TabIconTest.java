package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class TabIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(32, 32), Color.BLUE);
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void instanceOf_shapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(new TabIcon(new Dimension(16, 16), Color.RED) instanceof ShapeIcon);
  }

  @Test
  public void constructor_differentColor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(48, 48), Color.GREEN);
    assertEquals(48, icon.getIconWidth());
  }

  @Test
  public void constructor_nullColor_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(32, 32), null);
    assertNotNull(icon);
  }
}
