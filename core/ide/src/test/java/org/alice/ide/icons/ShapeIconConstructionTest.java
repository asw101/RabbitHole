package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Consolidated test for all ShapeIcon subclasses that accept a Dimension constructor.
 * Each icon must store and report the requested dimensions correctly.
 */
@RunWith(Parameterized.class)
public class ShapeIconConstructionTest {

  @FunctionalInterface
  private interface IconFactory {
    Icon create(Dimension size);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> iconFactories() {
    return Arrays.asList(new Object[][] {
        {"AxesIcon",       (IconFactory) AxesIcon::new},
        {"BillboardIcon",  (IconFactory) BillboardIcon::new},
        {"BoxIcon",        (IconFactory) BoxIcon::new},
        {"CheckIcon",      (IconFactory) CheckIcon::new},
        {"ConeIcon",       (IconFactory) ConeIcon::new},
        {"CylinderIcon",   (IconFactory) CylinderIcon::new},
        {"DiscIcon",       (IconFactory) DiscIcon::new},
        {"GroundIcon",     (IconFactory) GroundIcon::new},
        {"JointIcon",      (IconFactory) JointIcon::new},
        {"PlusIcon",       (IconFactory) PlusIcon::new},
        {"SceneIcon",      (IconFactory) SceneIcon::new},
        {"SphereIcon",     (IconFactory) SphereIcon::new},
        {"TextModelIcon",  (IconFactory) TextModelIcon::new},
        {"TorusIcon",      (IconFactory) TorusIcon::new},
        {"TabIcon",        (IconFactory) dim -> new TabIcon(dim, Color.BLUE)},
    });
  }

  private final String name;
  private final IconFactory factory;

  public ShapeIconConstructionTest(String name, IconFactory factory) {
    this.name = name;
    this.factory = factory;
  }

  @Test
  public void squareDimension_reportsCorrectSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = factory.create(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void rectangularDimension_reportsCorrectSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = factory.create(new Dimension(48, 32));
    assertEquals(48, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }
}
