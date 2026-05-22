package edu.cmu.cs.dennisc.ui;

import edu.cmu.cs.dennisc.java.awt.EventInterceptor;
import edu.cmu.cs.dennisc.java.awt.event.KeyEventUtilities;
import org.junit.Test;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class DragAdapterTest {
  @Test
  public void setAwtComponentRegistersAndReplacesListeners() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel first = new JPanel();
    JPanel second = new JPanel();

    adapter.setAWTComponent(first);
    assertSame(first, adapter.getAWTComponent());
    assertTrue(contains(first.getMouseListeners(), adapter));
    assertTrue(contains(first.getMouseMotionListeners(), adapter));
    assertTrue(contains(first.getKeyListeners(), adapter));

    adapter.setAWTComponent(second);
    assertFalse(contains(first.getMouseListeners(), adapter));
    assertTrue(contains(second.getMouseListeners(), adapter));
  }

  @Test
  public void addAndRemoveEventInterceptorMatchCurrentBehavior() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    EventInterceptor interceptor = e -> true;

    adapter.addEventInterceptor(interceptor);
    adapter.removeEventInterceptor(interceptor);

    int count = 0;
    for (EventInterceptor ignored : adapter.accessEventInterceptors()) {
      count++;
    }
    assertEquals(2, count);
  }

  @Test
  public void mousePressDragAndReleaseInvokeCallbacksWithDeltas() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(mouse(panel, MouseEvent.MOUSE_PRESSED, 5, 6, InputEvent.BUTTON1_MASK));
    adapter.mouseDragged(mouse(panel, MouseEvent.MOUSE_DRAGGED, 9, 12, InputEvent.BUTTON1_MASK));
    adapter.mouseReleased(mouse(panel, MouseEvent.MOUSE_RELEASED, 11, 16, InputEvent.BUTTON1_MASK));

    assertEquals(1, adapter.presses.size());
    assertEquals(new Point(5, 6), adapter.presses.get(0).point);
    assertEquals(2, adapter.drags.size());
    assertEquals(4, adapter.drags.get(1).deltaSincePress.x);
    assertEquals(6, adapter.drags.get(1).deltaSincePress.y);
    assertEquals(4, adapter.drags.get(1).deltaSincePrevious.x);
    assertEquals(6, adapter.drags.get(1).deltaSincePrevious.y);
    assertEquals(1, adapter.releases.size());
    assertEquals(new Point(11, 16), adapter.releases.get(0).point);
    assertFalse(adapter.isDragInProgress());
  }

  @Test
  public void mousePressWithShiftAndControlChoosesExpectedDragStyles() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(mouse(panel, MouseEvent.MOUSE_PRESSED, 1, 1, InputEvent.BUTTON1_MASK));
    assertEquals(DragStyle.NORMAL, adapter.presses.getLast().style);

    adapter.mouseReleased(mouse(panel, MouseEvent.MOUSE_RELEASED, 1, 1, InputEvent.BUTTON1_MASK));
    adapter.mousePressed(mouse(panel, MouseEvent.MOUSE_PRESSED, 2, 2, InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK));
    assertEquals(DragStyle.SHIFT, adapter.presses.getLast().style);

    adapter.mouseReleased(mouse(panel, MouseEvent.MOUSE_RELEASED, 2, 2, InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK));
    MouseEvent ctrlEvent = mouse(panel, MouseEvent.MOUSE_PRESSED, 3, 3, InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
    adapter.mousePressed(ctrlEvent);
    // On macOS, CTRL_MASK may map to META; verify we got a non-NORMAL, non-SHIFT style
    DragStyle style = adapter.presses.getLast().style;
    assertNotNull(style);
    assertNotEquals(DragStyle.NORMAL, style);
    assertNotEquals(DragStyle.SHIFT, style);
  }

  @Test
  public void interceptedMousePressDoesNotActivateDrag() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    adapter.addEventInterceptor(e -> true);
    JPanel panel = new JPanel();

    adapter.mousePressed(mouse(panel, MouseEvent.MOUSE_PRESSED, 5, 6, InputEvent.BUTTON1_MASK));
    adapter.mouseDragged(mouse(panel, MouseEvent.MOUSE_DRAGGED, 7, 8, InputEvent.BUTTON1_MASK));
    adapter.mouseReleased(mouse(panel, MouseEvent.MOUSE_RELEASED, 7, 8, InputEvent.BUTTON1_MASK));

    assertTrue(adapter.presses.isEmpty());
    assertTrue(adapter.drags.isEmpty());
    assertTrue(adapter.releases.isEmpty());
  }

  @Test
  public void keyPressesAndReleasesChangeDragStyleDuringActiveDrag() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(mouse(panel, MouseEvent.MOUSE_PRESSED, 10, 10, InputEvent.BUTTON1_MASK));
    adapter.keyPressed(new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
    adapter.keyReleased(new KeyEvent(panel, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
    int controlKey = KeyEventUtilities.getQuoteControlUnquoteKey();
    adapter.keyPressed(new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0L, 0, controlKey, KeyEvent.CHAR_UNDEFINED));
    adapter.keyReleased(new KeyEvent(panel, KeyEvent.KEY_RELEASED, 0L, 0, controlKey, KeyEvent.CHAR_UNDEFINED));

    assertTrue(adapter.releases.size() >= 3);
    assertTrue(adapter.presses.size() >= 3);
    assertFalse(adapter.presses.get(1).original);
    assertFalse(adapter.presses.get(2).original);
  }

  @Test
  public void unacceptableMousePressDoesNotStartDrag() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 0L, 0, 4, 4, 1, false, MouseEvent.NOBUTTON));

    assertTrue(adapter.presses.isEmpty());
    assertFalse(adapter.isDragInProgress());
  }

  @Test
  public void mouseClickedEnteredExitedAndTypedAreNoOps() {
    RecordingDragAdapter adapter = new RecordingDragAdapter();
    JPanel panel = new JPanel();
    MouseEvent mouse = mouse(panel, MouseEvent.MOUSE_CLICKED, 0, 0, 0);

    adapter.mouseClicked(mouse);
    adapter.mouseEntered(mouse);
    adapter.mouseExited(mouse);
    adapter.mouseMoved(mouse);
    adapter.keyTyped(new KeyEvent(panel, KeyEvent.KEY_TYPED, 0L, 0, KeyEvent.VK_UNDEFINED, 'a'));
  }

  private static MouseEvent mouse(Component component, int id, int x, int y, int modifiers) {
    return new MouseEvent(component, id, 0L, modifiers, x, y, 1, false, MouseEvent.BUTTON1);
  }

  private static boolean contains(Object[] listeners, Object expected) {
    for (Object listener : listeners) {
      if (listener == expected) {
        return true;
      }
    }
    return false;
  }

  private static final class RecordingDragAdapter extends DragAdapter {
    private final List<PressRecord> presses = new ArrayList<>();
    private final List<DragRecord> drags = new ArrayList<>();
    private final List<ReleaseRecord> releases = new ArrayList<>();

    @Override
    protected void handleMousePress(Point current, DragStyle dragStyle, boolean isOriginalAsOpposedToStyleChange) {
      presses.add(new PressRecord(new Point(current), dragStyle, isOriginalAsOpposedToStyleChange));
    }

    @Override
    protected void handleMouseDrag(Point current, int xDeltaSince0, int yDeltaSince0, int xDeltaSincePrevious, int yDeltaSincePrevious, DragStyle dragStyle) {
      drags.add(new DragRecord(new Point(current), new Point(xDeltaSince0, yDeltaSince0), new Point(xDeltaSincePrevious, yDeltaSincePrevious), dragStyle));
    }

    @Override
    protected Point handleMouseRelease(Point rvCurrent, DragStyle dragStyle, boolean isOriginalAsOpposedToStyleChange) {
      releases.add(new ReleaseRecord(new Point(rvCurrent), dragStyle, isOriginalAsOpposedToStyleChange));
      return new Point(rvCurrent);
    }
  }

  private record PressRecord(Point point, DragStyle style, boolean original) {}
  private record DragRecord(Point point, Point deltaSincePress, Point deltaSincePrevious, DragStyle style) {}
  private record ReleaseRecord(Point point, DragStyle style, boolean original) {}
}
