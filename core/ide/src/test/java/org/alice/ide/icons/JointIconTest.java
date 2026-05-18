package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class JointIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    JointIcon icon = new JointIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    JointIcon icon = new JointIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }
}
