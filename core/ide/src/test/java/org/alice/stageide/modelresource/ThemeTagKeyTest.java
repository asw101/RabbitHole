package org.alice.stageide.modelresource;

import org.alice.ide.icons.ThemeIconFactory;
import org.junit.Test;
import org.lgna.croquet.icon.IconFactory;

import static org.junit.Assert.*;

public class ThemeTagKeyTest {
  @Test
  public void constructor_setsTag() {
    ThemeTagKey key = new ThemeTagKey("theme:household");
    assertEquals("theme:household", key.getTag());
  }

  @Test
  public void getInternalName_returnsLastSegment() {
    ThemeTagKey key = new ThemeTagKey("theme:household");
    assertEquals("household", key.getInternalName());
  }

  @Test
  public void getIconFactory_returnsThemeIconFactory() {
    ThemeTagKey key = new ThemeTagKey("theme:household");
    IconFactory iconFactory = key.getIconFactory();
    assertNotNull(iconFactory);
    assertTrue(iconFactory instanceof ThemeIconFactory);
  }

  @Test
  public void getLocalizedCreationText_matchesLocalizedName() {
    ThemeTagKey key = new ThemeTagKey("theme:household");
    assertEquals(key.getLocalizedName(), key.getLocalizedCreationText());
  }

  @Test
  public void isNotLeafOrInstanceCreator() {
    ThemeTagKey key = new ThemeTagKey("theme:household");
    assertFalse(key.isLeaf());
    assertFalse(key.isInstanceCreator());
  }
}
