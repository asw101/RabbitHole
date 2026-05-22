package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class SceneIconFactoryTest {
  @Test
  public void getInstance_returnsSameInstance() {
    SceneIconFactory f1 = SceneIconFactory.getInstance();
    SceneIconFactory f2 = SceneIconFactory.getInstance();
    assertSame(f1, f2);
  }

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(SceneIconFactory.getInstance());
  }

  @Test
  public void getIconExactSize_returnsSceneIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = SceneIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    assertTrue(icon instanceof SceneIcon);
  }

  @Test
  public void getIconExactSize_respectsDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = SceneIconFactory.getInstance().getIconExactSize(new Dimension(48, 48));
    assertEquals(48, icon.getIconWidth());
    assertEquals(48, icon.getIconHeight());
  }

  @Test
  public void markAllIconsDirty_doesNotThrowWhenEmpty() {
    SceneIconFactory.getInstance().markAllIconsDirty();
  }
}
