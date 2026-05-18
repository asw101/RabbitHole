package org.lgna.croquet.history;

import org.junit.After;
import org.junit.Test;

import javax.swing.*;
import javax.swing.MenuElement;

import static org.junit.Assert.*;

public class MenuSelectionTest {

  @After
  public void clearSelection() {
    MenuSelectionManager.defaultManager().clearSelectedPath();
  }

  @Test
  public void emptySelectionIsInvalid() {
    MenuSelectionManager.defaultManager().clearSelectedPath();
    MenuSelection selection = new MenuSelection();
    assertFalse(selection.isValid());
  }

  @Test
  public void emptySelectionHasNoLastModel() {
    MenuSelectionManager.defaultManager().clearSelectedPath();
    MenuSelection selection = new MenuSelection();
    assertNull(selection.getLastMenuItemPrepModel());
  }

  @Test
  public void croquetMenuSelectionIsValid() {
    assertTrue(HistoryTestSupport.createSelectionFixture("file", "open").selection.isValid());
  }

  @Test
  public void lastPrepModelMatchesSelectedMenuItem() {
    HistoryTestSupport.SelectionFixture fixture = HistoryTestSupport.createSelectionFixture("file", "open");
    assertSame(fixture.operation.getMenuItemPrepModel(), fixture.selection.getLastMenuItemPrepModel());
  }

  @Test
  public void longerSelectionIsPreviousOfShorterPrefix() {
    HistoryTestSupport.SelectionFixture longer = HistoryTestSupport.createSelectionFixture("file", "open");
    MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{
        longer.menuBar.getAwtComponent(), longer.menu.getAwtComponent()
    });
    MenuSelection shorter = new MenuSelection();

    assertTrue(longer.selection.isPrevious(shorter));
  }

  @Test
  public void differentMenuBarsAreNotPrevious() {
    HistoryTestSupport.SelectionFixture first = HistoryTestSupport.createSelectionFixture("file", "open");
    HistoryTestSupport.SelectionFixture second = HistoryTestSupport.createSelectionFixture("edit", "copy");
    assertFalse(first.selection.isPrevious(second.selection));
  }

  @Test
  public void sameLengthSelectionIsNotPrevious() {
    HistoryTestSupport.SelectionFixture first = HistoryTestSupport.createSelectionFixture("file", "open");
    HistoryTestSupport.SelectionFixture second = HistoryTestSupport.createSelectionFixture("file", "save");
    assertFalse(first.selection.isPrevious(second.selection));
  }

  @Test
  public void plainSwingSelectionIsIgnored() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Plain");
    JMenuItem item = new JMenuItem("Item");
    menu.add(item);
    menuBar.add(menu);
    MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[]{menuBar, menu, item});

    MenuSelection selection = new MenuSelection();

    assertFalse(selection.isValid());
  }
}
