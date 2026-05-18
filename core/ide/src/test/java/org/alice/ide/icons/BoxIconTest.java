package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class BoxIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(48, 32));
    assertEquals(48, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_zeroSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    BoxIcon icon = new BoxIcon(new Dimension(0, 0));
    assertEquals(0, icon.getIconWidth());
    assertEquals(0, icon.getIconHeight());
  }
}
