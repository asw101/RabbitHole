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
    CheckIcon icon = new CheckIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void instanceOf_abstractIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(new CheckIcon(new Dimension(16, 16)) instanceof javax.swing.Icon);
  }

  @Test
  public void constructor_smallSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    CheckIcon icon = new CheckIcon(new Dimension(8, 8));
    assertEquals(8, icon.getIconWidth());
  }
}
