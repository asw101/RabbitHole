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
    GroundIcon icon = new GroundIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void instanceOf_shapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(new GroundIcon(new Dimension(16, 16)) instanceof ShapeIcon);
  }

  @Test
  public void constructor_largeSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroundIcon icon = new GroundIcon(new Dimension(200, 200));
    assertEquals(200, icon.getIconWidth());
  }
}
