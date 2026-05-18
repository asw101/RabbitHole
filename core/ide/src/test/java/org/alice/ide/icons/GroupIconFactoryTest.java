package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.Collections;

import static org.junit.Assert.*;

public class GroupIconFactoryTest {
  @Test
  public void constructor_emptyList_doesNotThrow() {
    new GroupIconFactory(Collections.emptyList());
  }

  @Test
  public void getDefaultSize_emptyFactories_returnsFallback() {
    GroupIconFactory factory = new GroupIconFactory(Collections.emptyList());
    Dimension fallback = new Dimension(64, 64);
    Dimension actual = factory.getDefaultSize(fallback);
    assertSame(fallback, actual);
  }

  @Test
  public void getIconExactSize_returnsGroupIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIconFactory factory = new GroupIconFactory(Collections.emptyList());
    Icon icon = factory.getIconExactSize(new Dimension(100, 100));
    assertTrue(icon instanceof GroupIcon);
  }

  @Test
  public void getIconExactSize_respectsDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    GroupIconFactory factory = new GroupIconFactory(Collections.emptyList());
    Icon icon = factory.getIconExactSize(new Dimension(80, 60));
    assertEquals(80, icon.getIconWidth());
    assertEquals(60, icon.getIconHeight());
  }
}
