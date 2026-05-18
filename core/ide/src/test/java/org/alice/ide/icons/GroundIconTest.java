package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class GroundIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroundIcon icon = new GroundIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_wideDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroundIcon icon = new GroundIcon(new Dimension(64, 16));
    assertEquals(64, icon.getIconWidth());
    assertEquals(16, icon.getIconHeight());
  }
}
