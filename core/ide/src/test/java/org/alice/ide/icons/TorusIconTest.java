package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class TorusIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TorusIcon icon = new TorusIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_largeDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TorusIcon icon = new TorusIcon(new Dimension(256, 256));
    assertEquals(256, icon.getIconWidth());
    assertEquals(256, icon.getIconHeight());
  }
}
