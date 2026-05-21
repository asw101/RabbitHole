package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.views.AbstractMenu;
import org.lgna.croquet.views.AwtComponentView;

import javax.swing.JMenu;
import javax.swing.event.PopupMenuListener;
import java.util.UUID;

import static org.junit.Assert.*;

public class AbstractMenuBehaviorTest {
  @Test
  public void addSeparatorAndRemoveAllMenuItems_delegateToSwingMenu() {
    TestMenu menu = new TestMenu(new TestMenuPrepModel(false));

    menu.addSeparator();
    assertEquals(1, menu.getMenuComponentCount());
    menu.removeAllMenuItems();

    assertEquals(0, menu.getMenuComponentCount());
  }

  @Test
  public void popupMenuListenersAndViewController_areExposed() {
    TestMenu menu = new TestMenu(new TestMenuPrepModel(false));
    int baseCount = menu.getAwtComponent().getPopupMenu().getPopupMenuListeners().length;
    PopupMenuListener listener = new javax.swing.event.PopupMenuListener() {
      @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
      @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
      @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
    };

    menu.addPopupMenuListener(listener);
    assertEquals(baseCount + 1, menu.getAwtComponent().getPopupMenu().getPopupMenuListeners().length);
    menu.removePopupMenuListener(listener);

    assertEquals(baseCount, menu.getAwtComponent().getPopupMenu().getPopupMenuListeners().length);
    assertSame(menu, menu.getViewController());
    assertNull(menu.getIcon());
  }

  private static final class TestMenu extends AbstractMenu<TestMenuPrepModel> {
    private TestMenu(TestMenuPrepModel model) {
      super(model);
    }

    @Override
    public org.lgna.croquet.history.UserActivity getActivity() {
      return null;
    }
  }

  private static final class TestMenuPrepModel extends MenuItemPrepModel {
    private final boolean showScrollArrows;

    private TestMenuPrepModel(boolean showScrollArrows) {
      super(UUID.fromString("00000000-0000-0000-0000-000000000102"));
      this.showScrollArrows = showScrollArrows;
    }

    @Override
    protected void localize() {
    }

    @Override
    public boolean showScrollArrows() {
      return this.showScrollArrows;
    }
  }
}
