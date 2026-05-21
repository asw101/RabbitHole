package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.MutableDataSingleSelectListState;
import org.lgna.croquet.data.MutableListData;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class ItemSelectablePanelBehaviorTest {
  @Test
  public void handleDisplayable_buildsButtonsAndCachesItems() {
    TestListState state = new TestListState("alpha", "beta");
    TestItemSelectablePanel panel = new TestItemSelectablePanel(state);

    panel.activateDisplayable();
    panel.activateDisplayable();

    assertEquals(2, panel.itemsAdded.size());
    assertEquals(2, panel.getButtonCount());
    assertEquals(1, panel.removeAllCount);
  }

  @Test
  public void listDataChanges_rebuildVisibleButtons() {
    TestListState state = new TestListState("alpha");
    TestItemSelectablePanel panel = new TestItemSelectablePanel(state);
    panel.activateDisplayable();

    state.getData().internalAddItem(1, "beta");

    assertEquals(Arrays.asList("alpha", "beta"), panel.itemsAdded);
    assertEquals(2, panel.getButtonCount());
  }

  private static final class TestListState extends MutableDataSingleSelectListState<String> {
    private TestListState(String... values) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), 0, CroquetTestUtils.STRING_CODEC, values);
    }
  }

  private static final class TestItemSelectablePanel extends ItemSelectablePanel<String> {
    private final List<String> itemsAdded = new ArrayList<>();
    private int removeAllCount;

    private TestItemSelectablePanel(TestListState model) {
      super(model);
    }

    @Override
    protected LayoutManager createLayoutManager(JPanel jPanel) {
      return new FlowLayout();
    }

    @Override
    protected PushButton createButtonForItemSelectedState(String item, org.lgna.croquet.BooleanState itemSelectedState) {
      return new PushButton(itemSelectedState);
    }

    @Override
    protected void removeAllDetails() {
      this.removeAllCount++;
      this.getAwtComponent().removeAll();
      this.itemsAdded.clear();
    }

    @Override
    protected void addPrologue(int count) {
    }

    @Override
    protected void addItem(String item, BooleanStateButton<?> button) {
      this.itemsAdded.add(item);
      this.getAwtComponent().add(button.getAwtComponent());
    }

    @Override
    protected void addEpilogue() {
    }

    private void activateDisplayable() {
      this.handleDisplayable();
    }

    private int getButtonCount() {
      return this.getAllButtons().size();
    }
  }
}
