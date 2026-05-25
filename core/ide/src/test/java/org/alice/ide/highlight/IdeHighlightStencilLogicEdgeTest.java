package org.alice.ide.highlight;

import org.junit.Test;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Area;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IdeHighlightStencilLogicEdgeTest {
  @Test
  public void createStencilPaintSupportsRectangularTextures() {
    TexturePaint paint = IdeHighlightStencilLogic.createStencilPaint(12, 6, Color.BLUE, Color.YELLOW);

    assertEquals(new Rectangle(0, 0, 12, 6), paint.getAnchorRect());
    assertEquals(Color.YELLOW.getRGB(), paint.getImage().getRGB(0, 0));
    assertEquals(Color.BLUE.getRGB(), paint.getImage().getRGB(11, 5));
  }

  @Test
  public void subtractFeatureAreaLeavesDisjointAreaUntouched() {
    Area area = new Area(new Rectangle(0, 0, 10, 10));

    IdeHighlightStencilLogic.subtractFeatureArea(area, new Rectangle(20, 20, 3, 3));

    assertTrue(area.contains(1, 1));
    assertTrue(area.contains(9, 9));
  }
}
