package org.alice.ide.croquet.models.history;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.undo.UndoHistory;

import javax.swing.JLabel;
import javax.swing.JList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HistoryModelBehaviorTest {
  @Test
  public void historyStackModelIncludesOpenProjectSentinelBeforeEdits() {
    UndoHistory history = createHistoryWithEdits();
    HistoryStackModel model = new HistoryStackModel(history);

    assertEquals(3, model.getSize());
    assertNull(model.getElementAt(0));
    assertEquals("first edit", ((Edit) model.getElementAt(1)).getTerseDescription());
    assertEquals("second edit", ((Edit) model.getElementAt(2)).getTerseDescription());
  }

  @Test
  public void undoAndRedoOperationsMoveHistoryInsertionIndex() {
    TestEdit first = new TestEdit(Application.PROJECT_GROUP, "first edit");
    TestEdit second = new TestEdit(Application.PROJECT_GROUP, "second edit");
    UndoHistory history = new UndoHistory(Application.PROJECT_GROUP);
    history.push(first);
    history.push(second);

    UndoOperation undo = new UndoOperation(null);
    RedoOperation redo = new RedoOperation(null);

    undo.performInternal(history);
    assertEquals(1, history.getInsertionIndex());
    assertEquals(1, second.undoCount);

    redo.performInternal(history);
    assertEquals(2, history.getInsertionIndex());
    assertEquals(1, second.redoCount);
  }

  @Test
  public void historyCellRendererDisablesEntriesAfterSelectedIndex() {
    UndoHistory history = createHistoryWithEdits();
    HistoryStackModel model = new HistoryStackModel(history);
    JList<Object> list = new JList<>(model);
    list.setSelectedIndex(1);
    HistoryCellRenderer renderer = new HistoryCellRenderer();

    JLabel openProject = renderer.getListCellRendererComponent(new JLabel(), list, null, 0, false, false);
    JLabel current = renderer.getListCellRendererComponent(new JLabel(), list, (Edit) model.getElementAt(1), 1, true, false);
    JLabel future = renderer.getListCellRendererComponent(new JLabel(), list, (Edit) model.getElementAt(2), 2, false, false);

    assertEquals("---open project---", openProject.getText());
    assertTrue(current.isEnabled());
    assertFalse(future.isEnabled());
  }

  private static UndoHistory createHistoryWithEdits() {
    UndoHistory history = new UndoHistory(Application.PROJECT_GROUP);
    history.push(new TestEdit(Application.PROJECT_GROUP, "first edit"));
    history.push(new TestEdit(Application.PROJECT_GROUP, "second edit"));
    return history;
  }

  private static final class TestEdit implements Edit {
    private final Group group;
    private final String description;
    private int undoCount;
    private int redoCount;

    private TestEdit(Group group, String description) {
      this.group = group;
      this.description = description;
    }

    @Override
    public Group getGroup() {
      return this.group;
    }

    @Override
    public boolean canUndo() {
      return true;
    }

    @Override
    public boolean canRedo() {
      return true;
    }

    @Override
    public void doOrRedo(boolean isDo) {
      if (!isDo) {
        this.redoCount++;
      }
    }

    @Override
    public void undo() {
      this.undoCount++;
    }

    @Override
    public String getRedoPresentation() {
      return this.description;
    }

    @Override
    public String getUndoPresentation() {
      return this.description;
    }

    @Override
    public String getTerseDescription() {
      return this.description;
    }

    @Override
    public String getDetailedDescription() {
      return this.description;
    }

    @Override
    public String getLogDescription() {
      return this.description;
    }
  }
}
