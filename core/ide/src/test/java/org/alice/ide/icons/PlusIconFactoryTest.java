package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class PlusIconFactoryTest {
  @Test
  public void getInstance_returnsSameInstance() {
    PlusIconFactory f1 = PlusIconFactory.getInstance();
    PlusIconFactory f2 = PlusIconFactory.getInstance();
    assertSame(f1, f2);
  }

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(PlusIconFactory.getInstance());
  }

  @Test
  public void getIconExactSize_returnsPlusIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = PlusIconFactory.getInstance().getIconExactSize(new Dimension(16, 16));
    assertTrue(icon instanceof PlusIcon);
  }

  @Test
  public void getIconExactSize_respectsDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = PlusIconFactory.getInstance().getIconExactSize(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void getIconExactSize_varyingDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = PlusIconFactory.getInstance().getIconExactSize(new Dimension(20, 14));
    assertEquals(20, icon.getIconWidth());
    assertEquals(14, icon.getIconHeight());
  }
}
