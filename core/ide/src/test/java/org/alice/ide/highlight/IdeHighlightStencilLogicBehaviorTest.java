package org.alice.ide.highlight;

import org.junit.Test;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Area;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class IdeHighlightStencilLogicBehaviorTest {
  @Test
  public void createStencilPaintUsesLineColorAtOriginAndBaseColorAwayFromHighlight() {
    TexturePaint paint = IdeHighlightStencilLogic.createStencilPaint(8, 8, Color.BLACK, Color.WHITE);

    assertEquals(new Rectangle(0, 0, 8, 8), paint.getAnchorRect());
    assertEquals(Color.WHITE.getRGB(), paint.getImage().getRGB(0, 0));
    assertEquals(Color.BLACK.getRGB(), paint.getImage().getRGB(7, 7));
  }

  @Test
  public void subtractFeatureAreaLeavesAreaUntouchedWhenFeatureIsNull() {
    Area area = new Area(new Rectangle(0, 0, 10, 10));

    Area returned = IdeHighlightStencilLogic.subtractFeatureArea(area, null);

    assertSame(area, returned);
    assertTrue(area.contains(2, 2));
  }

  @Test
  public void subtractFeatureAreaRemovesCoveredRegion() {
    Area area = new Area(new Rectangle(0, 0, 10, 10));

    IdeHighlightStencilLogic.subtractFeatureArea(area, new Rectangle(0, 0, 5, 10));

    assertFalse(area.contains(2, 5));
    assertTrue(area.contains(7, 5));
  }
}
