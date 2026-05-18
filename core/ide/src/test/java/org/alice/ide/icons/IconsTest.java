package org.alice.ide.icons;

import org.junit.Test;

import static org.junit.Assert.*;

public class IconsTest {
  @Test
  public void smallDimensions_arePositive() {
    assertTrue("SMALL_WIDTH should be positive", Icons.SMALL_WIDTH > 0);
    assertTrue("SMALL_HEIGHT should be positive", Icons.SMALL_HEIGHT > 0);
  }

  @Test
  public void emptyHeightIconSmall_matchesSmallHeight() {
    assertNotNull(Icons.EMPTY_HEIGHT_ICON_SMALL);
    assertEquals(Icons.SMALL_HEIGHT, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconHeight());
    assertEquals(0, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconWidth());
  }
}
