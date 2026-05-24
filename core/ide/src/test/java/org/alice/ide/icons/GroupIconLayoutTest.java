package org.alice.ide.icons;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupIconLayoutTest {
  @Test
  public void shouldRenderComposite_requiresWidthGreaterThanSixtyFour() {
    assertFalse(GroupIconLayout.shouldRenderComposite(64));
    assertTrue(GroupIconLayout.shouldRenderComposite(65));
  }

  @Test
  public void createLayout_forSingleIcon_centersTheOnlySourceIcon() {
    GroupIconLayout.Layout layout = GroupIconLayout.createLayout(1);

    assertArrayEquals(new int[] {2}, layout.getDrawOrder());
    assertEquals(2, layout.getSlotForSourceIndex(0));
  }

  @Test
  public void createLayout_forThreeIcons_spreadsIconsAcrossBackRowAndCenter() {
    GroupIconLayout.Layout layout = GroupIconLayout.createLayout(3);

    assertArrayEquals(new int[] {0, 4, 2}, layout.getDrawOrder());
    assertEquals(0, layout.getSlotForSourceIndex(0));
    assertEquals(2, layout.getSlotForSourceIndex(1));
    assertEquals(4, layout.getSlotForSourceIndex(2));
  }

  @Test
  public void createLayout_forFourIcons_usesOuterAndInnerSlots() {
    GroupIconLayout.Layout layout = GroupIconLayout.createLayout(4);

    assertArrayEquals(new int[] {0, 4, 1, 3}, layout.getDrawOrder());
    assertEquals(0, layout.getSlotForSourceIndex(0));
    assertEquals(1, layout.getSlotForSourceIndex(1));
    assertEquals(3, layout.getSlotForSourceIndex(2));
    assertEquals(4, layout.getSlotForSourceIndex(3));
  }
}
