package org.lgna.croquet.imp.booleanstate;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.TestBooleanState;
import org.lgna.croquet.views.PopupMenu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import java.util.UUID;

import static org.junit.Assert.*;

public class BooleanStateMenuModelTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-7754-ffffffffffff"), "booleanMenu");

  private TestBooleanState state;
  private BooleanStateMenuModel model;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
    state.setTextForTrueAndTextForFalse("Enabled", "Disabled");
    model = new BooleanStateMenuModel(state);
  }

  @Test
  public void getSubKeyForLocalization_returnsMenu() {
    assertEquals("menu", model.getSubKeyForLocalization());
  }

  @Test
  public void isEnabled_reflectsBooleanState() {
    state.setEnabled(false);

    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_updatesBooleanState() {
    model.setEnabled(false);

    assertFalse(state.isEnabled());
  }

  @Test
  public void initContents_createsTwoCheckBoxMenuItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertEquals(2, popup.getAwtComponent().getComponentCount());
  }

  @Test
  public void initContents_falseState_selectsFalseMenuItem() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertFalse(menuItem(popup, 0).isSelected());
    assertTrue(menuItem(popup, 1).isSelected());
  }

  @Test
  public void initContents_trueState_selectsTrueMenuItem() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);
    state.setValueTransactionlessly(true);

    runOnEdt(() -> model.initContents(popup));

    assertTrue(menuItem(popup, 0).isSelected());
    assertFalse(menuItem(popup, 1).isSelected());
  }

  @Test
  public void initContents_usesOperationNamesAsMenuText() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.initContents(popup));

    assertEquals("Enabled", menuItem(popup, 0).getText());
    assertEquals("Disabled", menuItem(popup, 1).getText());
  }

  @Test
  public void initContents_rebuildsMenuInsteadOfDuplicatingItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      model.initContents(popup);
      state.setValueTransactionlessly(true);
      model.initContents(popup);
    });

    assertEquals(2, popup.getAwtComponent().getComponentCount());
    assertTrue(menuItem(popup, 0).isSelected());
  }

  @Test
  public void handleShowing_populatesMenuItems() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> model.handleShowing(popup, new PopupMenuEvent(popup.getAwtComponent())));

    assertEquals(2, popup.getAwtComponent().getComponentCount());
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
}
