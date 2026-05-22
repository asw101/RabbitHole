package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class IconsTest {
  @Test
  public void allShapeIcons_areSubclassOfShapeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Dimension d = new Dimension(32, 32);
    assertTrue(new BoxIcon(d) instanceof ShapeIcon);
    assertTrue(new ConeIcon(d) instanceof ShapeIcon);
    assertTrue(new CylinderIcon(d) instanceof ShapeIcon);
    assertTrue(new SphereIcon(d) instanceof ShapeIcon);
    assertTrue(new TorusIcon(d) instanceof ShapeIcon);
    assertTrue(new DiscIcon(d) instanceof ShapeIcon);
    assertTrue(new GroundIcon(d) instanceof ShapeIcon);
  }

  @Test
  public void allShapeIcons_respectDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Dimension d = new Dimension(64, 48);
    ShapeIcon[] icons = {
        new BoxIcon(d), new ConeIcon(d), new CylinderIcon(d),
        new SphereIcon(d), new TorusIcon(d), new DiscIcon(d), new GroundIcon(d)
    };
    for (ShapeIcon icon : icons) {
      assertEquals(64, icon.getIconWidth());
      assertEquals(48, icon.getIconHeight());
    }
  }

  @Test
  public void shapeIcon_padConstant() {
    assertEquals(2, ShapeIcon.PAD);
  }

  @Test
  public void shapeIcon_fillPaint_notNull() {
    assertNotNull(ShapeIcon.FILL_PAINT);
  }

  @Test
  public void shapeIcon_fillPaint_isLavender() {
    assertEquals(191, ShapeIcon.FILL_PAINT.getRed());
    assertEquals(191, ShapeIcon.FILL_PAINT.getGreen());
    assertEquals(255, ShapeIcon.FILL_PAINT.getBlue());
  }
}
