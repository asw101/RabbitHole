package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.Collections;

import static org.junit.Assert.*;

public class GroupIconTest {
  @Test
  public void constructor_emptyIconFactories_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIcon icon = new GroupIcon(new Dimension(100, 100), Collections.emptyList());
    assertEquals(100, icon.getIconWidth());
    assertEquals(100, icon.getIconHeight());
  }

  @Test
  public void constructor_smallSize_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIcon icon = new GroupIcon(new Dimension(32, 32), Collections.emptyList());
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void constructor_zeroSize_setsSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIcon icon = new GroupIcon(new Dimension(0, 0), Collections.emptyList());
    assertEquals(0, icon.getIconWidth());
    assertEquals(0, icon.getIconHeight());
  }

  @Test
  public void createBackShape_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIcon icon = new GroupIcon(new Dimension(100, 100), Collections.emptyList());
    assertNotNull(icon.createBackShape(100, 100));
  }

  @Test
  public void createFrontShape_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIcon icon = new GroupIcon(new Dimension(100, 100), Collections.emptyList());
    assertNotNull(icon.createFrontShape(100, 100));
  }
}
