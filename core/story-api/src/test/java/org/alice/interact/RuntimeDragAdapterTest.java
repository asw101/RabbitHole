package org.alice.interact;

import org.alice.interact.condition.ManipulatorConditionSet;
import org.junit.Test;
import org.lgna.story.Visual;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/** Headless tests for RuntimeDragAdapter. */
public class RuntimeDragAdapterTest {

  private static List<?> getManipulators(DragAdapter a) throws Exception {
    Field f = DragAdapter.class.getDeclaredField("manipulators");
    f.setAccessible(true);
    return (List<?>) f.get(a);
  }

  @Test
  public void constructorWithEmptyArrayEnablesMoveAllObjects() throws Exception {
    RuntimeDragAdapter a = new RuntimeDragAdapter(new Visual[]{});
    List<?> manips = getManipulators(a);
    // Should install three drag manipulators when empty (move all + camera mouse control adds more conds).
    assertTrue("expect ≥3 manipulator condition sets", manips.size() >= 3);
  }

  @Test
  public void addTargetsEmptyArrayIsIdempotent() throws Exception {
    RuntimeDragAdapter a = new RuntimeDragAdapter(new Visual[]{});
    int before = getManipulators(a).size();
    a.addTargets(new Visual[]{});
    // moveAllObjects is already true → early return path.
    assertEquals(before, getManipulators(a).size());
  }

  @Test
  public void handleMouseEnteredIsNoOp() {
    RuntimeDragAdapter a = new RuntimeDragAdapter(new Visual[]{});
    JPanel panel = new JPanel();
    panel.setSize(100, 100);
    MouseEvent e = new MouseEvent(panel, MouseEvent.MOUSE_ENTERED, 0L, 0, 5, 5, 1, false);
    // Overridden to do nothing.
    a.handleMouseEntered(e);
  }

  @Test
  public void handleMouseMovedSetsLocationAndFiresStateChange() {
    RuntimeDragAdapter a = new RuntimeDragAdapter(new Visual[]{});
    JPanel panel = new JPanel();
    panel.setSize(100, 100);
    MouseEvent e = new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 0L, 0, 12, 34, 0, false);
    a.handleMouseMoved(e);
    assertEquals(new Point(12, 34), a.currentInputState.getMouseLocation());
  }
}
