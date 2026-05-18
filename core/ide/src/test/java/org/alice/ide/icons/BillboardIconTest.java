package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class BillboardIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BillboardIcon icon = new BillboardIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BillboardIcon icon = new BillboardIcon(new Dimension(40, 30));
    assertEquals(40, icon.getIconWidth());
    assertEquals(30, icon.getIconHeight());
  }
}
