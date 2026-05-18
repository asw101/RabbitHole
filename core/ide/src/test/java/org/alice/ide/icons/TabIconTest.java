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
    TabIcon icon = new TabIcon(new Dimension(24, 24), Color.BLUE);
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_withRedColor() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(32, 32), Color.RED);
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_differentDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TabIcon icon = new TabIcon(new Dimension(20, 30), Color.GREEN);
    assertEquals(20, icon.getIconWidth());
    assertEquals(30, icon.getIconHeight());
  }
}
