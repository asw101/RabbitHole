package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class CylinderIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    CylinderIcon icon = new CylinderIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    CylinderIcon icon = new CylinderIcon(new Dimension(36, 36));
    assertEquals(36, icon.getIconWidth());
    assertEquals(36, icon.getIconHeight());
  }
}
