package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.views.Menu;
import org.lgna.croquet.views.PopupMenu;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Graphics;

import static org.junit.Assert.*;

public class AbstractMenuModelTest {

  private TestMenuModel model;

  @Before
  public void setUp() {
    model = new TestMenuModel(null);
  }

  @Test
  public void getAction_returnsSameInstanceAcrossCalls() {
    assertSame(model.getAction(), model.getAction());
  }

  @Test
  public void setName_updatesUnderlyingActionName() {
    model.setName("File");

    assertEquals("File", model.getAction().getValue(Action.NAME));
  }

  @Test
  public void setSmallIcon_updatesUnderlyingActionIcon() {
    Icon icon = new StubIcon();
    model.setSmallIcon(icon);

    assertSame(icon, model.getAction().getValue(Action.SMALL_ICON));
  }

  @Test
  public void setEnabled_false_disablesUnderlyingAction() {
    model.setEnabled(false);

    assertFalse(model.getAction().isEnabled());
  }

  @Test
  public void setEnabled_true_reEnablesUnderlyingAction() {
    model.setEnabled(false);
    model.setEnabled(true);

    assertTrue(model.getAction().isEnabled());
  }

  @Test
  public void getClassUsedForLocalization_usesOverrideWhenProvided() {
    TestMenuModel overridden = new TestMenuModel(AbstractElement.class);

    assertEquals(AbstractElement.class, overridden.exposedClassUsedForLocalization());
  }

  @Test
  public void getClassUsedForLocalization_defaultsToRuntimeClassWhenOverrideMissing() {
    assertEquals(TestMenuModel.class, model.exposedClassUsedForLocalization());
  }

  @Test
  public void createMenu_returnsMenuBackedByThisModel() {
    Menu menu = model.createMenu();

    assertSame(model, menu.getModel());
  }

  @Test
  public void createMenuItemAndAddTo_addsMenuToPopupContainer() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);
    final Menu[] created = new Menu[1];
    final int[] before = new int[1];

    runOnEdt(() -> {
      before[0] = popup.getMenuComponentCount();
      created[0] = model.createMenuItemAndAddTo(popup);
    });

    assertNotNull(created[0]);
    assertEquals(before[0] + 1, popup.getMenuComponentCount());
    boolean found = false;
    for (org.lgna.croquet.views.AwtComponentView<?> component : popup.getMenuComponents()) {
      if (component == created[0]) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void addAndRemovePopupMenuListener_updatesPopupListenerCount() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      int before = popup.getAwtComponent().getPopupMenuListeners().length;
      model.addPopupMenuListener(popup);
      assertEquals(before + 1, popup.getAwtComponent().getPopupMenuListeners().length);
      model.removePopupMenuListener(popup);
      assertEquals(before, popup.getAwtComponent().getPopupMenuListeners().length);
    });
  }

  @Test
  public void popupMenuVisible_dispatchesToHandleShowing() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      model.addPopupMenuListener(popup);
      PopupMenuListener listener = popup.getAwtComponent().getPopupMenuListeners()[0];
      listener.popupMenuWillBecomeVisible(new PopupMenuEvent(popup.getAwtComponent()));
      model.removePopupMenuListener(popup);
    });

    assertEquals(1, model.showingCount);
    assertSame(popup, model.lastContainer);
  }

  @Test
  public void popupMenuInvisible_dispatchesToHandleHiding() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      model.addPopupMenuListener(popup);
      PopupMenuListener listener = popup.getAwtComponent().getPopupMenuListeners()[0];
      listener.popupMenuWillBecomeInvisible(new PopupMenuEvent(popup.getAwtComponent()));
      model.removePopupMenuListener(popup);
    });

    assertEquals(1, model.hidingCount);
    assertSame(popup, model.lastContainer);
  }

  @Test
  public void popupMenuCanceled_dispatchesToHandleCanceled() throws Exception {
    PopupMenu popup = new PopupMenu(null, null);

    runOnEdt(() -> {
      model.addPopupMenuListener(popup);
      PopupMenuListener listener = popup.getAwtComponent().getPopupMenuListeners()[0];
      listener.popupMenuCanceled(new PopupMenuEvent(popup.getAwtComponent()));
      model.removePopupMenuListener(popup);
    });

    assertEquals(1, model.canceledCount);
    assertSame(popup, model.lastContainer);
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

  private static final class TestMenuModel extends AbstractMenuModel {
    private int showingCount;
    private int hidingCount;
    private int canceledCount;
    private org.lgna.croquet.views.MenuItemContainer lastContainer;

    private TestMenuModel(Class<? extends AbstractElement> clsForI18N) {
      super(CroquetTestUtils.nextTestUUID(), clsForI18N);
    }

    private Class<? extends Element> exposedClassUsedForLocalization() {
      return this.getClassUsedForLocalization();
    }

    @Override
    protected void handleShowing(org.lgna.croquet.views.MenuItemContainer menuItemContainer, PopupMenuEvent e) {
      this.showingCount++;
      this.lastContainer = menuItemContainer;
    }

    @Override
    protected void handleHiding(org.lgna.croquet.views.MenuItemContainer menuItemContainer, PopupMenuEvent e) {
      this.hidingCount++;
      this.lastContainer = menuItemContainer;
    }

    @Override
    protected void handleCanceled(org.lgna.croquet.views.MenuItemContainer menuItemContainer, PopupMenuEvent e) {
      this.canceledCount++;
      this.lastContainer = menuItemContainer;
    }
  }

  private static final class StubIcon implements Icon {
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
    }

    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public int getIconHeight() {
      return 16;
    }
  }
}
