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
    SphereIcon icon = new SphereIcon(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void instanceOf_shapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertTrue(new SphereIcon(new Dimension(48, 48)) instanceof ShapeIcon);
  }

  @Test
  public void constructor_rectangularDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    SphereIcon icon = new SphereIcon(new Dimension(100, 50));
    assertEquals(100, icon.getIconWidth());
    assertEquals(50, icon.getIconHeight());
  }
}
