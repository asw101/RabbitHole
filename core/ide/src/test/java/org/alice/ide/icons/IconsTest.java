package org.alice.ide.icons;

import org.junit.Test;

import static org.junit.Assert.*;

public class IconsTest {
  @Test
  public void smallWidth_is24() {
    assertEquals(24, Icons.SMALL_WIDTH);
  }

  @Test
  public void smallHeight_is24() {
    assertEquals(24, Icons.SMALL_HEIGHT);
  }

  @Test
  public void emptyHeightIconSmall_notNull() {
    assertNotNull(Icons.EMPTY_HEIGHT_ICON_SMALL);
  }

  @Test
  public void emptyHeightIconSmall_height() {
    assertEquals(Icons.SMALL_HEIGHT, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconHeight());
  }

  @Test
  public void emptyHeightIconSmall_widthIsZero() {
    assertEquals(0, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconWidth());
  }
}
