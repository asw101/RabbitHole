package org.lgna.croquet.imp.liststate;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.SingleSelectListState;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.views.PopupMenu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import java.util.UUID;

import static org.junit.Assert.*;

public class SingleSelectListStateMenuTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-7755-ffffffffffff"), "listMenu");

  private MutableListData<String> data;
  private TestSingleSelectListState state;
  private SingleSelectListStateMenuModel<String, MutableListData<String>> model;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "bravo", "charlie"});
    state = new TestSingleSelectListState(1, data);
    CroquetTestUtils.removeListSelectionListeners(state);
    model = new SingleSelectListStateMenuModel<>(state);
  }

  @Test
  public void getListSelectionState_returnsWrappedState() {
    assertSame(state, model.getListSelectionState());
  }

  @Test
  public void isEnabled_reflectsUnderlyingState() {
    state.setEnabled(false);

    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_updatesUnderlyingState() {
    model.setEnabled(false);

    assertFalse(state.isEnabled());
  }

  @Test
  public void initContents_createsOneMenuItemPerDataItem() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertEquals(3, popup.getAwtComponent().getComponentCount());
  }

  @Test
  public void initContents_selectsCurrentStateItem() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertFalse(menuItem(popup, 0).isSelected());
    assertTrue(menuItem(popup, 1).isSelected());
    assertFalse(menuItem(popup, 2).isSelected());
  }

  @Test
  public void initContents_usesItemRepresentationAsMenuText() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertEquals("alpha", menuItem(popup, 0).getText());
    assertEquals("bravo", menuItem(popup, 1).getText());
    assertEquals("charlie", menuItem(popup, 2).getText());
  }

  @Test
  public void initContents_afterSelectionChange_updatesSelectedMenuItem() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);
    state.setSelectedIndex(2);

    runOnEdt(() -> model.initContents(popup));

    assertFalse(menuItem(popup, 1).isSelected());
    assertTrue(menuItem(popup, 2).isSelected());
  }

  @Test
  public void initContents_withEmptyList_createsNoMenuItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);
    state.clear();

    runOnEdt(() -> model.initContents(popup));

    assertEquals(0, popup.getAwtComponent().getComponentCount());
  }

  @Test
  public void handleHiding_removesAllMenuItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      model.initContents(popup);
      model.handleHiding(popup, new PopupMenuEvent(popup.getAwtComponent()));
    });

    assertEquals(0, popup.getAwtComponent().getComponentCount());
  }

  @Test
  public void handleShowing_populatesMenuItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.handleShowing(popup, new PopupMenuEvent(popup.getAwtComponent())));

    assertEquals(3, popup.getAwtComponent().getComponentCount());
  }

  private static JCheckBoxMenuItem menuItem(PopupMenu popup, int index) {
    return (JCheckBoxMenuItem) popup.getAwtComponent().getComponent(index);
  }

  private static void runOnEdt(ThrowingRunnable runnable) throws Exception {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      SwingUtilities.invokeAndWait(() -> {
        try {
          runnable.run();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  private interface ThrowingRunnable {
    void run() throws Exception;
  }

  private static final class TestSingleSelectListState extends SingleSelectListState<String, MutableListData<String>> {
    private TestSingleSelectListState(int selectedIndex, MutableListData<String> data) {
      super(TEST_GROUP, CroquetTestUtils.nextTestUUID(), selectedIndex, data);
    }
  }
}
