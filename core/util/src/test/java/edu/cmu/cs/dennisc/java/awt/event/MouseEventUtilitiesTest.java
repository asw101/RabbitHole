package edu.cmu.cs.dennisc.java.awt.event;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import org.junit.Test;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MenuDragMouseEvent;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static org.junit.Assert.*;

public class MouseEventUtilitiesTest {

  private static class ComponentPair {
    private final JPanel root = new JPanel(null);
    private final JPanel source = new JPanel();
    private final JPanel destination = new JPanel();

    private ComponentPair() {
      root.setBounds(0, 0, 200, 200);
      source.setBounds(10, 20, 30, 30);
      destination.setBounds(40, 70, 30, 30);
      root.add(source);
      root.add(destination);
    }
  }

  private static MouseEvent createMouseEvent(Component component, int id, int modifiers, int button) {
    return new MouseEvent(component, id, 123L, modifiers, 10, 20, 1, false, button);
  }

  @Test
  public void isQuoteLeftUnquoteMouseButton_plainLeftClick_matchesPlatformBehavior() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);

    assertEquals(true, MouseEventUtilities.isQuoteLeftUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteLeftUnquoteMouseButton_controlLeftClick_matchesPlatformBehavior() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, MouseEvent.BUTTON1);

    assertEquals(!SystemUtilities.isMac(), MouseEventUtilities.isQuoteLeftUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteLeftUnquoteMouseButton_rightClick_isFalse() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON3_DOWN_MASK, MouseEvent.BUTTON3);

    assertFalse(MouseEventUtilities.isQuoteLeftUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteLeftUnquoteMouseButton_middleClick_isFalse() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON2_DOWN_MASK, MouseEvent.BUTTON2);

    assertFalse(MouseEventUtilities.isQuoteLeftUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteRightUnquoteMouseButton_rightClick_isTrue() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON3_DOWN_MASK, MouseEvent.BUTTON3);

    assertTrue(MouseEventUtilities.isQuoteRightUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteRightUnquoteMouseButton_plainLeftClick_isFalse() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);

    assertFalse(MouseEventUtilities.isQuoteRightUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteRightUnquoteMouseButton_controlLeftClick_matchesPlatformBehavior() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON1_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, MouseEvent.BUTTON1);

    assertEquals(SystemUtilities.isMac(), MouseEventUtilities.isQuoteRightUnquoteMouseButton(event));
  }

  @Test
  public void isQuoteRightUnquoteMouseButton_middleClick_isFalse() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_PRESSED, InputEvent.BUTTON2_DOWN_MASK, MouseEvent.BUTTON2);

    assertFalse(MouseEventUtilities.isQuoteRightUnquoteMouseButton(event));
  }

  @Test
  public void performPlatformFilter_whenNoFilteringOccurs_returnsSameInstance() {
    MouseEvent event = createMouseEvent(new JPanel(), MouseEvent.MOUSE_MOVED, InputEvent.SHIFT_DOWN_MASK, MouseEvent.NOBUTTON);

    assertSame(event, MouseEventUtilities.performPlatformFilter(event));
  }

  @Test
  public void convertMouseEvent_plainEvent_translatesCoordinates() {
    ComponentPair pair = new ComponentPair();
    MouseEvent sourceEvent = createMouseEvent(pair.source, MouseEvent.MOUSE_MOVED, InputEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);

    MouseEvent converted = MouseEventUtilities.convertMouseEvent(pair.source, sourceEvent, pair.destination);

    assertEquals(-20, converted.getX());
    assertEquals(-30, converted.getY());
  }

  @Test
  public void convertMouseEvent_plainEvent_preservesCompleteModifiers() {
    ComponentPair pair = new ComponentPair();
    int modifiers = InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.BUTTON1_MASK | InputEvent.BUTTON1_DOWN_MASK;
    MouseEvent sourceEvent = createMouseEvent(pair.source, MouseEvent.MOUSE_PRESSED, modifiers, MouseEvent.BUTTON1);

    MouseEvent converted = MouseEventUtilities.convertMouseEvent(pair.source, sourceEvent, pair.destination);

    assertEquals(InputEventUtilities.getCompleteModifiers(sourceEvent), InputEventUtilities.getCompleteModifiers(converted));
  }

  @Test
  public void convertMouseEvent_plainEvent_returnsPlainMouseEvent() {
    ComponentPair pair = new ComponentPair();
    MouseEvent sourceEvent = createMouseEvent(pair.source, MouseEvent.MOUSE_RELEASED, InputEvent.BUTTON1_DOWN_MASK, MouseEvent.BUTTON1);

    MouseEvent converted = MouseEventUtilities.convertMouseEvent(pair.source, sourceEvent, pair.destination);

    assertTrue(converted.getClass().equals(MouseEvent.class));
  }

  @Test
  public void convertMouseEvent_mouseWheelEvent_returnsMouseWheelEvent() {
    JPanel panel = new JPanel();
    MouseWheelEvent sourceEvent = new MouseWheelEvent(panel, MouseEvent.MOUSE_WHEEL, 5L,
            InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK, 3, 4, 1, false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL, 2, -1);

    MouseEvent converted = MouseEventUtilities.convertMouseEvent(panel, sourceEvent, panel);

    assertTrue(converted instanceof MouseWheelEvent);
  }

  @Test
  public void convertMouseEvent_mouseWheelEvent_preservesWheelRotation() {
    JPanel panel = new JPanel();
    MouseWheelEvent sourceEvent = new MouseWheelEvent(panel, MouseEvent.MOUSE_WHEEL, 5L,
            InputEvent.SHIFT_MASK | InputEvent.SHIFT_DOWN_MASK, 3, 4, 1, false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL, 2, -2);

    MouseWheelEvent converted = (MouseWheelEvent) MouseEventUtilities.convertMouseEvent(panel, sourceEvent, panel);

    assertEquals(-2, converted.getWheelRotation());
  }

  @Test
  public void convertMouseEvent_mouseWheelEvent_preservesScrollAmount() {
    JPanel panel = new JPanel();
    MouseWheelEvent sourceEvent = new MouseWheelEvent(panel, MouseEvent.MOUSE_WHEEL, 5L,
            InputEvent.BUTTON2_MASK | InputEvent.BUTTON2_DOWN_MASK, 3, 4, 1, false,
            MouseWheelEvent.WHEEL_UNIT_SCROLL, 7, 1);

    MouseWheelEvent converted = (MouseWheelEvent) MouseEventUtilities.convertMouseEvent(panel, sourceEvent, panel);

    assertEquals(7, converted.getScrollAmount());
  }

  @Test
  public void convertMouseEvent_menuDragEvent_returnsMenuDragMouseEvent() {
    JMenuItem item = new JMenuItem("item");
    MenuElement[] path = new MenuElement[] { item };
    MenuSelectionManager manager = new MenuSelectionManager();
    MenuDragMouseEvent sourceEvent = new MenuDragMouseEvent(item, MouseEvent.MOUSE_DRAGGED, 8L,
            InputEvent.BUTTON1_MASK | InputEvent.BUTTON1_DOWN_MASK, 1, 2, 1, false, path, manager);

    MouseEvent converted = MouseEventUtilities.convertMouseEvent(item, sourceEvent, item);

    assertTrue(converted instanceof MenuDragMouseEvent);
  }

  @Test
  public void convertMouseEvent_menuDragEvent_preservesPathAndSelectionManager() {
    JMenuItem item = new JMenuItem("item");
    MenuElement[] path = new MenuElement[] { item };
    MenuSelectionManager manager = new MenuSelectionManager();
    MenuDragMouseEvent sourceEvent = new MenuDragMouseEvent(item, MouseEvent.MOUSE_DRAGGED, 8L,
            InputEvent.BUTTON1_MASK | InputEvent.BUTTON1_DOWN_MASK, 1, 2, 1, false, path, manager);

    MenuDragMouseEvent converted = (MenuDragMouseEvent) MouseEventUtilities.convertMouseEvent(item, sourceEvent, item);

    assertArrayEquals(path, converted.getPath());
    assertSame(manager, converted.getMenuSelectionManager());
  }
}
