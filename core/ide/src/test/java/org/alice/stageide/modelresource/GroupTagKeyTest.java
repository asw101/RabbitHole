package org.alice.stageide.modelresource;

import org.alice.ide.icons.GroupIconFactory;
import org.junit.Test;
import org.lgna.croquet.icon.AbstractSingleSourceImageIconFactory;
import org.lgna.croquet.icon.IconFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.junit.Assert.*;

public class GroupTagKeyTest {
  private AbstractSingleSourceImageIconFactory createIconFactory() {
    return new AbstractSingleSourceImageIconFactory(new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))) {
      @Override
      protected Icon createIcon(Dimension size) {
        return getSourceImageIcon();
      }
    };
  }

  @Test
  public void constructor_setsTag() {
    GroupTagKey key = new GroupTagKey("group:household", List.of(createIconFactory()));
    assertEquals("group:household", key.getTag());
  }

  @Test
  public void getInternalName_returnsLastSegment() {
    GroupTagKey key = new GroupTagKey("group:household", List.of(createIconFactory()));
    assertEquals("household", key.getInternalName());
  }

  @Test
  public void getIconFactory_returnsGroupIconFactory() {
    GroupTagKey key = new GroupTagKey("group:household", List.of(createIconFactory()));
    IconFactory iconFactory = key.getIconFactory();
    assertNotNull(iconFactory);
    assertTrue(iconFactory instanceof GroupIconFactory);
  }

  @Test
  public void getSearchText_containsInternalName() {
    GroupTagKey key = new GroupTagKey("group:household", List.of(createIconFactory()));
    assertTrue(key.getSearchText().contains("household"));
  }

  @Test
  public void isNotLeafOrInstanceCreator() {
    GroupTagKey key = new GroupTagKey("group:household", List.of(createIconFactory()));
    assertFalse(key.isLeaf());
    assertFalse(key.isInstanceCreator());
  }
}
