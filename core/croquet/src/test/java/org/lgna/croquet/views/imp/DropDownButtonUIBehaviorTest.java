package org.lgna.croquet.views.imp;

import org.junit.Test;

import javax.swing.JButton;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DropDownButtonUIBehaviorTest {
  @Test
  public void installAndUninstallListenersRoundTrip() {
    JButton button = new JButton("drop");
    DropDownButtonUI ui = new DropDownButtonUI(button);

    int listenersBefore = button.getMouseListeners().length;
    int motionBefore = button.getMouseMotionListeners().length;

    ui.installUI(button);
    assertTrue(button.getMouseListeners().length > listenersBefore);
    assertTrue(button.getMouseMotionListeners().length > motionBefore);

    ui.uninstallUI(button);
    assertEquals(motionBefore, button.getMouseMotionListeners().length);
  }

  @Test
  public void mousePressDragReleaseUpdatesButtonModelAndDispatchState() throws Exception {
    JButton button = new JButton("drop");
    button.setSize(20, 20);
    DropDownButtonUI ui = new DropDownButtonUI(button);

    invoke(ui, "handleMouseEntered", event(button, MouseEvent.MOUSE_ENTERED, 1, 1));
    assertTrue(button.getModel().isRollover());

    invoke(ui, "handleMousePressed", event(button, MouseEvent.MOUSE_PRESSED, 1, 1));
    assertTrue(button.getModel().isPressed());
    assertNotNull(getField(ui, "dispatchState"));

    invoke(ui, "handleMouseDragged", event(button, MouseEvent.MOUSE_DRAGGED, 50, 50));
    invoke(ui, "handleMouseReleased", event(button, MouseEvent.MOUSE_RELEASED, 50, 50));
    invoke(ui, "handleMouseExited", event(button, MouseEvent.MOUSE_EXITED, 50, 50));

    assertFalse(button.getModel().isPressed());
    assertFalse(button.getModel().isRollover());
    assertNull(getField(ui, "dispatchState"));
    assertNull(getField(ui, "mousePressedEvent"));
  }

  private static MouseEvent event(JButton button, int id, int x, int y) {
    return new MouseEvent(button, id, System.currentTimeMillis(), 0, x, y, 1, false, MouseEvent.BUTTON1);
  }

  private static void invoke(Object target, String name, Object arg) throws Exception {
    Method method = DropDownButtonUI.class.getDeclaredMethod(name, MouseEvent.class);
    method.setAccessible(true);
    method.invoke(target, arg);
  }

  private static Object getField(Object target, String name) throws Exception {
    Field field = DropDownButtonUI.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }
}
