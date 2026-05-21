package org.alice.imageeditor.croquet;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilenameComboBoxModelBehaviorTest {
  @Test
  public void prologueAddAllAndDoneExposeWorkingPlaceholderAndAbsolutePaths() throws Exception {
    FilenameComboBoxModel model = new FilenameComboBoxModel();
    File root = TestSupport.createEmptyDirectory("filename-combo-box-model");
    File first = new File(root, "first.png");
    File second = new File(root, "second.png");

    model.prologue();
    Assert.assertEquals(1, model.getSize());
    Assert.assertNull(model.getElementAt(0));

    model.addAll(Arrays.asList(first, second));
    Assert.assertEquals(3, model.getSize());
    Assert.assertEquals(first.getAbsolutePath(), model.getElementAt(0));
    Assert.assertEquals(second.getAbsolutePath(), model.getElementAt(1));
    Assert.assertNull(model.getElementAt(2));

    model.done(new File[] {first, second});
    Assert.assertEquals(2, model.getSize());
  }

  @Test
  public void selectionChangeCoalescesEquivalentTextAndNotifiesListeners() {
    FilenameComboBoxModel model = new FilenameComboBoxModel();
    RecordingListener listener = new RecordingListener();
    model.addListDataListener(listener);

    model.prologue();
    int afterPrologue = listener.events.size();
    model.setSelectedItem("alpha.png");
    Assert.assertEquals("alpha.png", model.getSelectedItem());
    Assert.assertEquals(afterPrologue + 1, listener.events.size());

    model.setSelectedItem(new StringBuilder("alpha.png"));
    Assert.assertEquals(afterPrologue + 1, listener.events.size());

    model.setSelectedItem("beta.png");
    Assert.assertEquals("beta.png", model.getSelectedItem());
    Assert.assertEquals(afterPrologue + 2, listener.events.size());

    model.removeListDataListener(listener);
    model.setSelectedItem("gamma.png");
    Assert.assertEquals(afterPrologue + 2, listener.events.size());
  }

  private static final class RecordingListener implements ListDataListener {
    private final List<ListDataEvent> events = new ArrayList<>();

    @Override
    public void intervalAdded(ListDataEvent e) {
      this.events.add(e);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
      this.events.add(e);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
      this.events.add(e);
    }
  }
}
