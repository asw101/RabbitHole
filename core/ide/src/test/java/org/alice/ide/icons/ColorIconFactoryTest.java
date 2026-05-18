package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class ColorIconFactoryTest {
  @Test
  public void constructor_withRedColor_doesNotThrow() {
    new ColorIconFactory(Color.RED);
  }

  @Test
  public void constructor_withNullColor_doesNotThrow() {
    new ColorIconFactory(null);
  }

  @Test
  public void getDefaultSize_returnsNonNull() {
    ColorIconFactory factory = new ColorIconFactory(Color.BLUE);
    Dimension size = factory.getDefaultSize(new Dimension(100, 100));
    assertNotNull(size);
  }

  @Test
  public void getDefaultSize_ignoresFallback() {
    ColorIconFactory factory = new ColorIconFactory(Color.GREEN);
    Dimension fallback = new Dimension(999, 999);
    Dimension actual = factory.getDefaultSize(fallback);
    assertNotSame(fallback, actual);
  }

  @Test
  public void getIconExactSize_returnsCorrectDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ColorIconFactory factory = new ColorIconFactory(Color.RED);
    Icon icon = factory.getIconExactSize(new Dimension(16, 16));
    assertNotNull(icon);
    assertEquals(16, icon.getIconWidth());
    assertEquals(16, icon.getIconHeight());
  }

  @Test
  public void getIconExactSize_differentColors_returnDistinctIcons() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ColorIconFactory red = new ColorIconFactory(Color.RED);
    ColorIconFactory blue = new ColorIconFactory(Color.BLUE);
    Dimension size = new Dimension(16, 16);
    Icon redIcon = red.getIconExactSize(size);
    Icon blueIcon = blue.getIconExactSize(size);
    assertNotSame(redIcon, blueIcon);
  }
}
