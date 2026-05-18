package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class CheckIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    CheckIcon icon = new CheckIcon(new Dimension(16, 16));
    assertEquals(16, icon.getIconWidth());
    assertEquals(16, icon.getIconHeight());
  }

  @Test
  public void constructor_largeSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    CheckIcon icon = new CheckIcon(new Dimension(48, 48));
    assertEquals(48, icon.getIconWidth());
    assertEquals(48, icon.getIconHeight());
  }
}
