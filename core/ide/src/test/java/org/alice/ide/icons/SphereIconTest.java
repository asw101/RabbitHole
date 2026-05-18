package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class SphereIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    SphereIcon icon = new SphereIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    SphereIcon icon = new SphereIcon(new Dimension(100, 100));
    assertEquals(100, icon.getIconWidth());
    assertEquals(100, icon.getIconHeight());
  }
}
