package edu.cmu.cs.dennisc.ui;

import edu.cmu.cs.dennisc.pattern.Action;
import org.junit.Test;

import java.util.EmptyStackException;

import static org.junit.Assert.*;

public class UndoRedoManagerTest {
  private static final class RecordingUndoRedoManager extends UndoRedoManager {
    private int value;
    private int changeCount;

    @Override
    protected void handleChange() {
      this.changeCount++;
    }
  }

  private static final class DeltaAction implements Action {
    private final RecordingUndoRedoManager manager;
    private final int delta;

    private DeltaAction(RecordingUndoRedoManager manager, int delta) {
      this.manager = manager;
      this.delta = delta;
    }

    @Override
    public void run() {
      this.manager.value += this.delta;
    }

    @Override
    public void undo() {
      this.manager.value -= this.delta;
    }

    @Override
    public void redo() {
      this.manager.value += this.delta;
    }
  }

  @Test
  public void stacksStartEmpty() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    assertTrue(manager.isUndoStackEmpty());
    assertTrue(manager.isRedoStackEmpty());
  }

  @Test
  public void runAndPushRunsActionAndPopulatesUndoStack() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    manager.runAndPush(new DeltaAction(manager, 5));

    assertEquals(5, manager.value);
    assertFalse(manager.isUndoStackEmpty());
    assertTrue(manager.isRedoStackEmpty());
    assertEquals(1, manager.changeCount);
  }

  @Test
  public void pushAlreadyRunActionClearsRedoStack() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    manager.runAndPush(new DeltaAction(manager, 3));
    manager.undo();

    DeltaAction action = new DeltaAction(manager, 2);
    action.run();
    manager.pushAlreadyRunActionOntoUndoStack(action);

    assertEquals(2, manager.value);
    assertTrue(manager.isRedoStackEmpty());
  }

  @Test
  public void undoMovesActionToRedoAndRevertsValue() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    manager.runAndPush(new DeltaAction(manager, 4));
    manager.undo();

    assertEquals(0, manager.value);
    assertTrue(manager.isUndoStackEmpty());
    assertFalse(manager.isRedoStackEmpty());
  }

  @Test
  public void redoReappliesActionAndMovesBackToUndo() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    manager.runAndPush(new DeltaAction(manager, 4));
    manager.undo();
    manager.redo();

    assertEquals(4, manager.value);
    assertFalse(manager.isUndoStackEmpty());
    assertTrue(manager.isRedoStackEmpty());
  }

  @Test
  public void multipleUndoRedoUsesLastInFirstOutOrdering() {
    RecordingUndoRedoManager manager = new RecordingUndoRedoManager();
    manager.runAndPush(new DeltaAction(manager, 1));
    manager.runAndPush(new DeltaAction(manager, 2));
    manager.undo();
    assertEquals(1, manager.value);
    manager.undo();
    assertEquals(0, manager.value);
    manager.redo();
    assertEquals(1, manager.value);
  }

  @Test(expected = EmptyStackException.class)
  public void undoOnEmptyStackThrows() {
    new RecordingUndoRedoManager().undo();
  }

  @Test(expected = EmptyStackException.class)
  public void redoOnEmptyStackThrows() {
    new RecordingUndoRedoManager().redo();
  }
}
