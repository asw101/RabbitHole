package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Consolidated test for OpenTrashIcon and ClosedTrashIcon —
 * both share constructor and paint behavior.
 */
@RunWith(Parameterized.class)
public class TrashIconTest {

  @FunctionalInterface
  private interface TrashIconFactory {
    Icon create(int w, int h, Color c);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> icons() {
    return Arrays.asList(new Object[][] {
        {"OpenTrashIcon", (TrashIconFactory) OpenTrashIcon::new},
        {"ClosedTrashIcon", (TrashIconFactory) ClosedTrashIcon::new},
    });
  }

  private final String name;
  private final TrashIconFactory factory;

  public TrashIconTest(String name, TrashIconFactory factory) {
    this.name = name;
    this.factory = factory;
  }

  private static void assumeAwtAvailable() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
  }

  @Test
  public void constructorStoresDimensions() {
    assumeAwtAvailable();
    Icon icon = factory.create(20, 24, Color.RED);
    assertEquals(20, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  @Test
  public void paintIconRendersWithoutThrowing() {
    assumeAwtAvailable();
    Icon icon = factory.create(20, 24, Color.RED);
    BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      icon.paintIcon(null, graphics, 0, 0);
    } finally {
      graphics.dispose();
    }
    assertEquals(32, image.getWidth());
  }
}
