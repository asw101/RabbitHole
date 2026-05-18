package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class AxesIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    AxesIcon icon = new AxesIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_largeDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    AxesIcon icon = new AxesIcon(new Dimension(64, 64));
    assertEquals(64, icon.getIconWidth());
    assertEquals(64, icon.getIconHeight());
  }
}
