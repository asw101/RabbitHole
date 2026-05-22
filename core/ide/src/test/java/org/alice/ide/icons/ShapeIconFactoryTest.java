package org.alice.ide.icons;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;

import static org.junit.Assert.*;

public class ShapeIconFactoryTest {
  @Test
  public void getIconExactSizeCreatesConfiguredShapeIconType() {
    ShapeIconFactory factory = new ShapeIconFactory(BoxIcon::new);

    Icon icon = factory.getIconExactSize(new Dimension(24, 18));

    assertTrue(icon instanceof BoxIcon);
  }

  @Test
  public void getIconExactSizeUsesRequestedDimensions() {
    ShapeIconFactory factory = new ShapeIconFactory(SphereIcon::new);

    Icon icon = factory.getIconExactSize(new Dimension(30, 12));

    assertEquals(30, icon.getIconWidth());
    assertEquals(12, icon.getIconHeight());
  }

  @Test
  public void getIconExactSizeDoesNotCacheInstancesByDefault() {
    ShapeIconFactory factory = new ShapeIconFactory(BoxIcon::new);

    Icon first = factory.getIconExactSize(new Dimension(24, 18));
    Icon second = factory.getIconExactSize(new Dimension(24, 18));

    assertNotSame(first, second);
  }
}
