package edu.cmu.cs.dennisc.memory;

import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryWidgetsBehaviorTest {
  @Test
  public void memoryViewRegistersListenerAndRepaintsOnMousePress() throws Exception {
    RecordingMemoryView view = new RecordingMemoryView();
    JPanel parent = new JPanel();

    runOnEdt(() -> {
      assertEquals(0, view.getMouseListeners().length);
      parent.add(view);
      parent.addNotify();
      assertEquals(1, view.getMouseListeners().length);

      view.getMouseListeners()[0].mousePressed(null);
      assertTrue(view.repaintCount > 0);

      parent.remove(view);
      view.removeNotify();
      assertEquals(0, view.getMouseListeners().length);
    });
  }

  @Test
  public void memoryUsagePanelStartsAndStopsSamplingTimerWithDisplayLifecycle() throws Exception {
    ExposedMemoryUsagePanel[] panelRef = new ExposedMemoryUsagePanel[1];
    runOnEdt(() -> panelRef[0] = new ExposedMemoryUsagePanel());
    ExposedMemoryUsagePanel panel = panelRef[0];
    Timer timer = timer(panel);

    runOnEdt(() -> {
      assertFalse(timer.isRunning());
      panel.display();
      assertTrue(timer.isRunning());
      panel.undisplay();
      assertFalse(timer.isRunning());
    });
  }

  private static Timer timer(MemoryUsagePanel panel) throws Exception {
    Field field = MemoryUsagePanel.class.getDeclaredField("timer");
    field.setAccessible(true);
    return (Timer) field.get(panel);
  }

  private static void runOnEdt(Runnable runnable) throws Exception {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      SwingUtilities.invokeAndWait(runnable);
    }
  }

  private static class RecordingMemoryView extends MemoryView {
    private int repaintCount;

    @Override
    public void repaint() {
      repaintCount++;
    }
  }

  private static class ExposedMemoryUsagePanel extends MemoryUsagePanel {
    public void display() {
      super.handleDisplayable();
    }

    public void undisplay() {
      super.handleUndisplayable();
    }
  }
}
