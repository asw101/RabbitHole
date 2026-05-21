package edu.cmu.cs.dennisc.java.awt.event;

import org.junit.Test;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LenientMouseClickAdapterTest {
  @Test
  public void pressAndReleaseWithinThresholdTriggersSingleClick() {
    RecordingAdapter adapter = new RecordingAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 100, 10, 10));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 150, 12, 11));

    assertEquals(List.of(1), adapter.clickCounts);
  }

  @Test
  public void secondNearbyClickIncrementsClickCount() {
    RecordingAdapter adapter = new RecordingAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 100, 10, 10));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 150, 10, 10));
    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 200, 11, 11));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 240, 12, 12));

    assertEquals(List.of(1, 2), adapter.clickCounts);
  }

  @Test
  public void dragBeyondThresholdCancelsClick() {
    RecordingAdapter adapter = new RecordingAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 100, 10, 10));
    adapter.mouseDragged(event(panel, MouseEvent.MOUSE_DRAGGED, 120, 25, 25));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 140, 25, 25));

    assertTrue(adapter.clickCounts.isEmpty());
  }

  @Test
  public void movedUnclickBeyondThresholdResetsSequence() {
    RecordingAdapter adapter = new RecordingAdapter();
    JPanel panel = new JPanel();

    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 100, 10, 10));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 150, 10, 10));
    adapter.mouseMoved(event(panel, MouseEvent.MOUSE_MOVED, 700, 40, 40));
    adapter.mousePressed(event(panel, MouseEvent.MOUSE_PRESSED, 800, 10, 10));
    adapter.mouseReleased(event(panel, MouseEvent.MOUSE_RELEASED, 820, 10, 10));

    assertEquals(List.of(1, 1), adapter.clickCounts);
  }

  @Test
  public void noOpMouseCallbacksDoNotThrow() {
    RecordingAdapter adapter = new RecordingAdapter();
    JPanel panel = new JPanel();
    MouseEvent event = event(panel, MouseEvent.MOUSE_CLICKED, 100, 0, 0);

    adapter.mouseEntered(event);
    adapter.mouseExited(event);
    adapter.mouseClicked(event);
  }

  private static MouseEvent event(JPanel panel, int id, long when, int x, int y) {
    return new MouseEvent(panel, id, when, 0, x, y, 1, false, MouseEvent.BUTTON1);
  }

  private static final class RecordingAdapter extends LenientMouseClickAdapter {
    private final List<Integer> clickCounts = new ArrayList<>();

    @Override
    protected void mouseQuoteClickedUnquote(MouseEvent e, int quoteClickCountUnquote) {
      clickCounts.add(quoteClickCountUnquote);
    }
  }
}
