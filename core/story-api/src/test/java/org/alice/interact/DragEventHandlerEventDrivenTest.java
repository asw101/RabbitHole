package org.alice.interact;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Drives DragEventHandler's AWT event handlers through registered listeners
 * to exercise event paths (no render target attached so picking branches
 * take the non-AWT-component path).
 */
public class DragEventHandlerEventDrivenTest {

  private static class TestAdapter extends DragAdapter { }

  private static MouseEvent mouseEvent(JPanel p, int id, int x, int y, int button) {
    return new MouseEvent(p, id, System.currentTimeMillis(), 0, x, y, 1, false, button);
  }

  private static MouseWheelEvent wheelEvent(JPanel p, int rotation) {
    return new MouseWheelEvent(p, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
        5, 5, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, rotation);
  }

  private static KeyEvent keyEvent(JPanel p, int id, int code) {
    return new KeyEvent(p, id, System.currentTimeMillis(), 0, code, KeyEvent.CHAR_UNDEFINED);
  }

  private static MouseListener mouseListener(DragEventHandler h, JPanel p) {
    for (MouseListener l : p.getMouseListeners()) {
      if (l == h.getMouseListener()) return l;
    }
    return null;
  }

  private static MouseMotionListener motionListener(DragEventHandler h, JPanel p) {
    for (MouseMotionListener l : p.getMouseMotionListeners()) {
      if (l == h.getMouseMotionListener()) return l;
    }
    return null;
  }

  private static KeyListener keyListener(DragEventHandler h, JPanel p) {
    for (KeyListener l : p.getKeyListeners()) {
      if (l == h.getKeyListener()) return l;
    }
    return null;
  }

  private static MouseWheelListener wheelListener(DragEventHandler h, JPanel p) {
    for (MouseWheelListener l : p.getMouseWheelListeners()) {
      if (l == h.getMouseWheelListener()) return l;
    }
    return null;
  }

  @Test
  public void mousePressedSetsButtonAndLocation() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 10, 20, MouseEvent.BUTTON1));
    assertTrue(a.currentInputState.isMouseDown(MouseEvent.BUTTON1));
    assertEquals(new Point(10, 20), a.currentInputState.getMouseLocation());
  }

  @Test
  public void mouseReleasedClearsButton() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mouseEntered(mouseEvent(p, MouseEvent.MOUSE_ENTERED, 1, 1, MouseEvent.NOBUTTON));
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 5, 5, MouseEvent.BUTTON1));
    mouseListener(h, p).mouseReleased(mouseEvent(p, MouseEvent.MOUSE_RELEASED, 6, 7, MouseEvent.BUTTON1));
    assertFalse(a.currentInputState.isMouseDown(MouseEvent.BUTTON1));
  }

  @Test
  public void mouseEnteredUpdatesLocationWhenNoButton() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mouseEntered(mouseEvent(p, MouseEvent.MOUSE_ENTERED, 4, 6, MouseEvent.NOBUTTON));
    assertEquals(new Point(4, 6), a.currentInputState.getMouseLocation());
  }

  @Test
  public void mouseEnteredIsNoOpWhenButtonDown() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 1, 1, MouseEvent.BUTTON1));
    Point before = a.currentInputState.getMouseLocation();
    mouseListener(h, p).mouseEntered(mouseEvent(p, MouseEvent.MOUSE_ENTERED, 99, 99, MouseEvent.NOBUTTON));
    assertEquals(before, a.currentInputState.getMouseLocation());
  }

  @Test
  public void mouseExitedClearsRolloverWhenNoButton() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mouseExited(mouseEvent(p, MouseEvent.MOUSE_EXITED, 0, 0, MouseEvent.NOBUTTON));
    assertNull(a.currentInputState.getRolloverHandle());
  }

  @Test
  public void mouseMovedUpdatesLocation() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    motionListener(h, p).mouseMoved(mouseEvent(p, MouseEvent.MOUSE_MOVED, 11, 12, MouseEvent.NOBUTTON));
    assertEquals(new Point(11, 12), a.currentInputState.getMouseLocation());
  }

  @Test
  public void mouseDraggedAfterPressUpdatesLocation() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 1, 1, MouseEvent.BUTTON1));
    motionListener(h, p).mouseDragged(mouseEvent(p, MouseEvent.MOUSE_DRAGGED, 22, 33, MouseEvent.BUTTON1));
    assertEquals(new Point(22, 33), a.currentInputState.getMouseLocation());
  }

  @Test
  public void keyPressedAndReleasedTrackState() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    keyListener(h, p).keyPressed(keyEvent(p, KeyEvent.KEY_PRESSED, KeyEvent.VK_A));
    assertTrue(a.currentInputState.isKeyDown(KeyEvent.VK_A));
    keyListener(h, p).keyReleased(keyEvent(p, KeyEvent.KEY_RELEASED, KeyEvent.VK_A));
    assertFalse(a.currentInputState.isKeyDown(KeyEvent.VK_A));
  }

  @Test
  public void keyTypedListenerIsNoOp() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    keyListener(h, p).keyTyped(new KeyEvent(p, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0,
        KeyEvent.VK_UNDEFINED, 'a'));
  }

  @Test
  public void mouseClickedListenerIsNoOp() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mouseClicked(mouseEvent(p, MouseEvent.MOUSE_CLICKED, 0, 0, MouseEvent.BUTTON1));
  }

  @Test
  public void mouseWheelMovedActivatesWheelAndSetsStartLocation() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    wheelListener(h, p).mouseWheelMoved(wheelEvent(p, 3));
    assertTrue(h.isMouseWheelActive());
    assertNotNull(h.getMouseWheelStartLocationForTest());
  }

  @Test
  public void wheelEventThenLargeMoveStopsWheel() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    wheelListener(h, p).mouseWheelMoved(wheelEvent(p, 1));
    assertTrue(h.isMouseWheelActive());
    motionListener(h, p).mouseMoved(mouseEvent(p, MouseEvent.MOUSE_MOVED, 500, 500, MouseEvent.NOBUTTON));
    assertFalse(h.isMouseWheelActive());
  }

  @Test
  public void mouseWheelStartLocationPersistsAcrossWheelEvents() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    wheelListener(h, p).mouseWheelMoved(wheelEvent(p, 1));
    Point first = h.getMouseWheelStartLocationForTest();
    wheelListener(h, p).mouseWheelMoved(wheelEvent(p, -1));
    assertEquals(first, h.getMouseWheelStartLocationForTest());
  }

  @Test
  public void mouseDraggedWrapsRuntimeException() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    // The try/catch in handleMouseDragged catches RuntimeException via
    // printStackTrace; here just verify normal path doesn't throw.
    motionListener(h, p).mouseDragged(mouseEvent(p, MouseEvent.MOUSE_DRAGGED, 1, 1, MouseEvent.BUTTON1));
  }

  @Test
  public void mousePressThenReleaseFromDifferentComponentsExercisesRollover() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    JPanel p2 = new JPanel();
    h.addListeners(p);
    h.addListeners(p2);
    mouseListener(h, p2).mouseEntered(mouseEvent(p2, MouseEvent.MOUSE_ENTERED, 1, 1, MouseEvent.NOBUTTON));
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 5, 5, MouseEvent.BUTTON1));
    // release from a different component (currentRolloverComponent = p2 != null)
    mouseListener(h, p2).mouseReleased(mouseEvent(p2, MouseEvent.MOUSE_RELEASED, 10, 10, MouseEvent.BUTTON1));
    assertFalse(a.currentInputState.isMouseDown(MouseEvent.BUTTON1));
  }

  @Test
  public void exitDoesNothingWhenButtonHeld() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    mouseListener(h, p).mousePressed(mouseEvent(p, MouseEvent.MOUSE_PRESSED, 1, 1, MouseEvent.BUTTON1));
    mouseListener(h, p).mouseExited(mouseEvent(p, MouseEvent.MOUSE_EXITED, 50, 50, MouseEvent.NOBUTTON));
    // Still held: rollover not necessarily cleared (no assertion needed; exercise branch)
    assertTrue(a.currentInputState.isMouseDown(MouseEvent.BUTTON1));
  }

  @Test
  public void multipleKeysPressedTrackIndependently() {
    TestAdapter a = new TestAdapter();
    DragEventHandler h = a.eventHandler;
    JPanel p = new JPanel();
    h.addListeners(p);
    keyListener(h, p).keyPressed(keyEvent(p, KeyEvent.KEY_PRESSED, KeyEvent.VK_W));
    keyListener(h, p).keyPressed(keyEvent(p, KeyEvent.KEY_PRESSED, KeyEvent.VK_S));
    assertTrue(a.currentInputState.isKeyDown(KeyEvent.VK_W));
    assertTrue(a.currentInputState.isKeyDown(KeyEvent.VK_S));
    keyListener(h, p).keyReleased(keyEvent(p, KeyEvent.KEY_RELEASED, KeyEvent.VK_W));
    assertFalse(a.currentInputState.isKeyDown(KeyEvent.VK_W));
    assertTrue(a.currentInputState.isKeyDown(KeyEvent.VK_S));
  }

  @Test
  public void dragAdapterUpdateDoesNotThrowWithoutManipulators() {
    TestAdapter a = new TestAdapter();
    a.update(0.05);
  }

  @Test
  public void dragAdapterClearMouseAndKeyboardStateZeroes() {
    TestAdapter a = new TestAdapter();
    a.currentInputState.setMouseState(MouseEvent.BUTTON1, true);
    a.currentInputState.setKeyState(KeyEvent.VK_X, true);
    a.currentInputState.setMouseWheelState(3);
    a.clearMouseAndKeyboardState();
    assertFalse(a.currentInputState.isMouseDown(MouseEvent.BUTTON1));
    assertFalse(a.currentInputState.isKeyDown(KeyEvent.VK_X));
    assertEquals(0, a.currentInputState.getMouseWheelState());
  }
}
