package edu.cmu.cs.dennisc.javax.swing;

import org.junit.Test;

import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class PopupMenuUtilitiesBehaviorTest {

  @Test
  public void explicitPointIsPassedThroughToPopupShow() {
    RecordingPopupMenu popup = new RecordingPopupMenu();
    JPanel invoker = new JPanel();

    PopupMenuUtilities.showModal(popup, invoker, new Point(7, 9));

    assertSame(invoker, popup.lastInvoker);
    assertEquals(7, popup.lastX);
    assertEquals(9, popup.lastY);
  }

  @Test
  public void nullPointDefaultsToBottomOfInvoker() {
    RecordingPopupMenu popup = new RecordingPopupMenu();
    JPanel invoker = new JPanel();
    invoker.setSize(120, 34);

    PopupMenuUtilities.showModal(popup, invoker, null);

    assertSame(invoker, popup.lastInvoker);
    assertEquals(0, popup.lastX);
    assertEquals(34, popup.lastY);
  }

  @Test
  public void nullInvokerAndPointFallbackToOrigin() {
    RecordingPopupMenu popup = new RecordingPopupMenu();

    PopupMenuUtilities.showModal(popup, null, null);

    assertNull(popup.lastInvoker);
    assertEquals(0, popup.lastX);
    assertEquals(0, popup.lastY);
  }

  private static final class RecordingPopupMenu extends JPopupMenu {
    private Component lastInvoker;
    private int lastX;
    private int lastY;

    @Override
    public void show(Component invoker, int x, int y) {
      this.lastInvoker = invoker;
      this.lastX = x;
      this.lastY = y;
    }
  }
}
