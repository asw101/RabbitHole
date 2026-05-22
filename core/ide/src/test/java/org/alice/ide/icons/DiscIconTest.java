package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class DiscIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DiscIcon icon = new DiscIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void instanceOf_shapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(new DiscIcon(new Dimension(16, 16)) instanceof ShapeIcon);
  }

  @Test
  public void constructor_rectangularDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DiscIcon icon = new DiscIcon(new Dimension(80, 40));
    assertEquals(80, icon.getIconWidth());
    assertEquals(40, icon.getIconHeight());
  }
}
