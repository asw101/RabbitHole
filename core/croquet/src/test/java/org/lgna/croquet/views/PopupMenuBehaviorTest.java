package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.PopupPrepModel;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JLabel;
import javax.swing.event.PopupMenuListener;
import java.util.UUID;

import static org.junit.Assert.*;

public class PopupMenuBehaviorTest {
  @Test
  public void addSeparatorAndRemoveAllMenuItems_delegateToSwingPopupMenu() {
    UserActivity activity = CroquetTestUtils.ensureTestApplication().acquireOpenActivity();
    try {
      PopupMenu menu = new PopupMenu(new TestPopupPrepModel(), activity);
      int baseCount = menu.getAwtComponent().getComponentCount();

      menu.addSeparator();
      assertEquals(baseCount + 1, menu.getAwtComponent().getComponentCount());
      menu.removeAllMenuItems();

      assertEquals(baseCount, menu.getAwtComponent().getComponentCount());
    } finally {
      activity.finish();
    }
  }

  @Test
  public void popupMenuListeners_andActivityAreExposed() {
    UserActivity activity = CroquetTestUtils.ensureTestApplication().acquireOpenActivity();
    try {
      PopupMenu menu = new PopupMenu(new TestPopupPrepModel(), activity);
      int baseCount = menu.getAwtComponent().getPopupMenuListeners().length;
      PopupMenuListener listener = new javax.swing.event.PopupMenuListener() {
        @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
        @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
        @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
      };

      menu.addPopupMenuListener(listener);
      assertEquals(baseCount + 1, menu.getAwtComponent().getPopupMenuListeners().length);
      menu.removePopupMenuListener(listener);

      assertEquals(baseCount, menu.getAwtComponent().getPopupMenuListeners().length);
      assertSame(activity, menu.getActivity());
    } finally {
      activity.finish();
    }
  }

  @Test
  public void showBelow_usesInvokerHeightForYCoordinate() {
    UserActivity activity = CroquetTestUtils.ensureTestApplication().acquireOpenActivity();
    try {
      RecordingPopupMenu menu = new RecordingPopupMenu(new TestPopupPrepModel(), activity);
      LabelView invoker = new LabelView();
      invoker.getAwtComponent().setSize(20, 11);

      menu.showBelow(invoker);

      assertSame(invoker, menu.lastInvoker);
      assertEquals(0, menu.lastX);
      assertEquals(11, menu.lastY);
    } finally {
      activity.finish();
    }
  }

  private static class RecordingPopupMenu extends PopupMenu {
    private AwtComponentView<?> lastInvoker;
    private int lastX;
    private int lastY;

    private RecordingPopupMenu(PopupPrepModel model, UserActivity activity) {
      super(model, activity);
    }

    @Override
    public void showAtLocation(AwtComponentView<?> invoker, int x, int y) {
      this.lastInvoker = invoker;
      this.lastX = x;
      this.lastY = y;
    }
  }

  private static final class LabelView extends AwtComponentView<JLabel> {
    @Override
    protected JLabel createAwtComponent() {
      return new JLabel("invoker");
    }
  }

  private static final class TestPopupPrepModel extends PopupPrepModel {
    private TestPopupPrepModel() {
      super(UUID.fromString("00000000-0000-0000-0000-000000000101"));
    }

    @Override
    protected void perform(UserActivity activity) {
    }
  }
}
