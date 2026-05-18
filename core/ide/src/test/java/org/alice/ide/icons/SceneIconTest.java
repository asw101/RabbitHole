package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class SceneIconTest {
  @Test
  public void constructor_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    SceneIcon icon = new SceneIcon(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void constructor_largeDimension() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    SceneIcon icon = new SceneIcon(new Dimension(160, 120));
    assertEquals(160, icon.getIconWidth());
    assertEquals(120, icon.getIconHeight());
  }
}
