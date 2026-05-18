package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DiscIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DiscIcon icon = new DiscIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_rectangularDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DiscIcon icon = new DiscIcon(new Dimension(30, 20));
    assertEquals(30, icon.getIconWidth());
    assertEquals(20, icon.getIconHeight());
  }
}
