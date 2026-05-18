package org.alice.imageeditor.croquet;

import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class FilenameComboBoxModelTest {
  private static final class RecordingListener implements ListDataListener {
    private final List<ListDataEvent> events = new ArrayList<ListDataEvent>();

    @Override
    public void intervalAdded(ListDataEvent e) {
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
      this.events.add(e);
    }
  }

  @Test
  public void initialStateIsEmpty() {
    FilenameComboBoxModel model = new FilenameComboBoxModel();

    assertEquals(0, model.getSize());
    assertNull(model.getSelectedItem());
  }

  @Test
  public void prologueAddAllDoneAndSelectionChangesFireExpectedEvents() {
    FilenameComboBoxModel model = new FilenameComboBoxModel();
    RecordingListener listener = new RecordingListener();
    model.addListDataListener(listener);

    model.prologue();
    assertEquals(1, model.getSize());
    assertNull(model.getElementAt(0));
    assertEquals(1, listener.events.size());
    assertEvent(listener.events.get(0), 0, 0);

    File first = new File("alpha.png").getAbsoluteFile();
    File second = new File("beta.png").getAbsoluteFile();
    model.addAll(Arrays.asList(first, second));
    assertEquals(3, model.getSize());
    assertEquals(first.getAbsolutePath(), model.getElementAt(0));
    assertEquals(second.getAbsolutePath(), model.getElementAt(1));
    assertNull(model.getElementAt(2));
    assertEquals(2, listener.events.size());
    assertEvent(listener.events.get(1), 0, 1);

    model.done(new File[] {first, second});
    assertEquals(2, model.getSize());

    model.setSelectedItem(first.getAbsolutePath());
    assertEquals(first.getAbsolutePath(), model.getSelectedItem());
    assertEquals(3, listener.events.size());
    assertEvent(listener.events.get(2), -1, -1);

    model.setSelectedItem(new String(first.getAbsolutePath()));
    assertEquals(3, listener.events.size());

    model.setSelectedItem(second.getAbsolutePath());
    assertEquals(second.getAbsolutePath(), model.getSelectedItem());
    assertEquals(4, listener.events.size());
    assertEvent(listener.events.get(3), -1, -1);
  }

  @Test
  public void removingListenerStopsNotifications() {
    FilenameComboBoxModel model = new FilenameComboBoxModel();
    RecordingListener listener = new RecordingListener();
    model.addListDataListener(listener);
    model.prologue();
    assertEquals(1, listener.events.size());

    model.removeListDataListener(listener);
    model.setSelectedItem("gamma.png");

    assertEquals(1, listener.events.size());
    assertSame("gamma.png", model.getSelectedItem());
  }

  private static void assertEvent(ListDataEvent event, int index0, int index1) {
    assertEquals(ListDataEvent.CONTENTS_CHANGED, event.getType());
    assertEquals(index0, event.getIndex0());
    assertEquals(index1, event.getIndex1());
  }
}
