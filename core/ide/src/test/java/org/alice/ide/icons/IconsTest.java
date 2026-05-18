package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

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
  public void emptyHeightIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(Icons.EMPTY_HEIGHT_ICON_SMALL);
  }

  @Test
  public void emptyHeightIcon_hasCorrectHeight() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertEquals(Icons.SMALL_HEIGHT, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconHeight());
  }

  @Test
  public void emptyHeightIcon_hasZeroWidth() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertEquals(0, Icons.EMPTY_HEIGHT_ICON_SMALL.getIconWidth());
  }

  @Test
  public void folderIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(Icons.FOLDER_ICON_SMALL);
  }
}
