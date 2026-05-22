package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class CheckIconFactoryTest {
  @Test
  public void getInstance_returnsSameInstance() {
    CheckIconFactory f1 = CheckIconFactory.getInstance();
    CheckIconFactory f2 = CheckIconFactory.getInstance();
    assertSame(f1, f2);
  }

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(CheckIconFactory.getInstance());
  }

  @Test
  public void getIconExactSize_returnsCheckIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = CheckIconFactory.getInstance().getIconExactSize(new Dimension(16, 16));
    assertTrue(icon instanceof CheckIcon);
  }

  @Test
  public void getIconExactSize_respectsDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = CheckIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void getIconExactSize_differentSizes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon small = CheckIconFactory.getInstance().getIconExactSize(new Dimension(8, 8));
    Icon large = CheckIconFactory.getInstance().getIconExactSize(new Dimension(32, 32));
    assertEquals(8, small.getIconWidth());
    assertEquals(32, large.getIconWidth());
  }
}
