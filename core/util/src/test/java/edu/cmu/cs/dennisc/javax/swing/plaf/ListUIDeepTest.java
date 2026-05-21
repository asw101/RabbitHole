package edu.cmu.cs.dennisc.javax.swing.plaf;

import org.junit.Test;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import java.awt.Point;
import java.awt.Rectangle;

import static org.junit.Assert.*;

public class ListUIDeepTest {

  @Test
  public void installUIBuildsButtonsForModel() {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("alpha");
    model.addElement("bravo");
    JList<String> list = new JList<>(model);
    TestListUI ui = new TestListUI();

    ui.installUI(list);

    assertEquals(3, list.getComponentCount());
    assertEquals("alpha", ((AbstractButton) list.getComponent(0)).getText());
  }

  @Test
  public void modelChangesRefreshButtons() {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("alpha");
    JList<String> list = new JList<>(model);
    TestListUI ui = new TestListUI();
    ui.installUI(list);

    model.addElement("bravo");
    model.removeElement("alpha");

    assertEquals(2, list.getComponentCount());
    assertEquals("bravo", ((AbstractButton) list.getComponent(0)).getText());
  }

  @Test
  public void selectingButtonUpdatesListSelection() {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("alpha");
    model.addElement("bravo");
    JList<String> list = new JList<>(model);
    TestListUI ui = new TestListUI();
    ui.installUI(list);

    AbstractButton second = (AbstractButton) list.getComponent(1);
    second.setSelected(true);

    assertEquals(1, list.getSelectedIndex());
  }

  @Test
  public void indexLocationAndBoundsReflectLaidOutComponents() {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("alpha");
    model.addElement("bravo");
    JList<String> list = new JList<>(model);
    TestListUI ui = new TestListUI();
    ui.installUI(list);
    list.setSize(120, 60);
    list.doLayout();

    Point firstLocation = ui.indexToLocation(list, 0);
    Rectangle bounds = ui.getCellBounds(list, 0, 1);

    assertNotNull(firstLocation);
    assertNotNull(bounds);
    assertEquals(0, ui.locationToIndex(list, new Point(firstLocation.x + 1, firstLocation.y + 1)));
  }

  @Test
  public void uninstallUIClearsInstalledState() {
    DefaultListModel<String> model = new DefaultListModel<>();
    model.addElement("alpha");
    JList<String> list = new JList<>(model);
    TestListUI ui = new TestListUI();
    ui.installUI(list);

    ui.uninstallUI(list);

    assertEquals(0, list.getComponentCount());
    assertNull(list.getLayout());
  }

  private static final class TestListUI extends ListUI<String> {
    @Override
    protected AbstractButton createComponentFor(int index, String value) {
      return new JToggleButton(value);
    }

    @Override
    protected void updateIndex(AbstractButton button, int index) {
      button.putClientProperty("index", index);
    }
  }
}
