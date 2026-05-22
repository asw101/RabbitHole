package org.lgna.croquet.views.imp;

import org.junit.Test;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.Dimension;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class ScrollingPopupMenuLayoutBehaviorTest {
  @Test
  public void preferredSize_includesSideColumnWidth() {
    JPopupMenu target = new JPopupMenu();
    ScrollingPopupMenuLayout layout = new ScrollingPopupMenuLayout(target);
    target.setLayout(layout);
    target.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 3, 4));

    target.add(createItem("main", 100, 20), ScrollingPopupMenuLayout.ColumnConstraint.MAIN);
    target.add(createItem("side", 10, 20), ScrollingPopupMenuLayout.ColumnConstraint.SIDE);

    Dimension preferred = layout.minimumLayoutSize(target);

    assertTrue(preferred.width >= 100 + 20 + 6);
  }

  @Test
  public void layoutContainer_withOverflow_setsScrollCountsAndHidesOverflowItems() {
    JPopupMenu target = new JPopupMenu();
    ScrollingPopupMenuLayout layout = new ScrollingPopupMenuLayout(target);
    target.setLayout(layout);
    JScrollMenuItem pageStart = new JScrollMenuItem(layout, ScrollDirection.UP);
    JScrollMenuItem pageEnd = new JScrollMenuItem(layout, ScrollDirection.DOWN);
    target.add(pageStart, ScrollingPopupMenuLayout.ScrollConstraint.PAGE_START);
    target.add(pageEnd, ScrollingPopupMenuLayout.ScrollConstraint.PAGE_END);
    for (int i = 0; i < 5; i++) {
      target.add(createItem("item-" + i, 80, 18), ScrollingPopupMenuLayout.ColumnConstraint.MAIN);
    }

    target.setSize(120, 55);
    layout.layoutContainer(target);

    assertTrue(pageEnd.getCount() > 0);
    assertTrue(target.getComponent(2).getBounds().height > 0);
    assertEquals(0, target.getComponent(6).getBounds().height);
  }

  @Test
  public void adjustIndex_skipsSeparators() throws Exception {
    JPopupMenu target = new JPopupMenu();
    ScrollingPopupMenuLayout layout = new ScrollingPopupMenuLayout(target);
    target.setLayout(layout);
    target.add(createItem("item-0", 80, 18), ScrollingPopupMenuLayout.ColumnConstraint.MAIN);
    target.add(new JPopupMenu.Separator(), ScrollingPopupMenuLayout.ColumnConstraint.MAIN);
    target.add(createItem("item-2", 80, 18), ScrollingPopupMenuLayout.ColumnConstraint.MAIN);

    layout.adjustIndex(1);

    Field field = ScrollingPopupMenuLayout.class.getDeclaredField("index0");
    field.setAccessible(true);
    assertEquals(2, field.getInt(layout));
  }

  private static JMenuItem createItem(String text, int width, int height) {
    JMenuItem item = new JMenuItem(text);
    item.setPreferredSize(new Dimension(width, height));
    item.setMinimumSize(new Dimension(width, height));
    item.setMaximumSize(new Dimension(width, height));
    return item;
  }
}
